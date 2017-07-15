package com.myapp.unknown.iNTCON.Uploads;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapp.unknown.iNTCON.DataResource.DataResourceItemProvider;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseUploadedOrError;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseUploading;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by UNKNOWN on 2/8/2017.
 */
public class UploadService extends Service {

    private SqliteDataBaseUploading sqliteDataBaseUploading;
    private SqliteDataBaseUploadedOrError sqliteDataBaseUploadedOrError;
    private String file_path,title,key,refPath;
    private Long size;
    private UploadTask task;

    @Override
    public void onCreate() {
        super.onCreate();
        sqliteDataBaseUploading = new SqliteDataBaseUploading(this);
        sqliteDataBaseUploadedOrError = new SqliteDataBaseUploadedOrError(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Cursor cursor = sqliteDataBaseUploading.getData();

        if(cursor.moveToFirst())
        {
            MyApplication.setIsUploading(true);
            title           = cursor.getString(1);
            file_path       = cursor.getString(2);
            size            = cursor.getLong(4);
            key             = cursor.getString(7);
            refPath         = cursor.getString(8);

            if(cursor.getLong(4) > 100 * 1024 * 1024)
            {
                addToSQLiteERROR();
            }
            else
            {
                uploadResourceToDataBase();
            }
        }
        else
        {
            MyApplication.setIsUploading(false);
            sqliteDataBaseUploadedOrError.close();
            sqliteDataBaseUploading.close();
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void uploadResourceToDataBase(){

        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://message-mi.appspot.com/")
                .child(GlobalNames.getDataResourceItems())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(System.currentTimeMillis() + title);

        task = firebaseStorage.putFile(Uri.fromFile(new File(file_path)));

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Intent RTReturn = new Intent(UploadResourcesListActivity.RECEIVE_UPLOAD_COMPLETE);
                RTReturn.putExtra("file_path",file_path );
                LocalBroadcastManager.getInstance(UploadService.this).sendBroadcast(RTReturn);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot()
                        .child(GlobalNames.getDataResourceItems())
                        .child(key);

                String push_key = reference.push().getKey();

                SimpleDateFormat dateF = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                SimpleDateFormat timeF = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

                String date = dateF.format(new Date());
                String time = timeF.format(new Date());

                DataResourceItemProvider resourceItemProvider = new DataResourceItemProvider();
                resourceItemProvider.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                resourceItemProvider.setDate(date);
                resourceItemProvider.setTime(time);
                resourceItemProvider.setTitle(title);
                resourceItemProvider.setKey(push_key);
                resourceItemProvider.setSize(size);
                resourceItemProvider.setDownload_path(taskSnapshot.getDownloadUrl().toString());
                reference.child(push_key).setValue(resourceItemProvider);

                updateResourceCNTs(refPath,key);

                sqliteDataBaseUploading.deleteData(file_path);
                sqliteDataBaseUploadedOrError.insertData(title,file_path,100,size,date,time,0,1,key,push_key,refPath);

                Cursor cursor = sqliteDataBaseUploading.getData();

                if(cursor.moveToFirst())
                {

                    MyApplication.setIsUploading(true);
                    title           = cursor.getString(1);
                    file_path       = cursor.getString(2);
                    size            = cursor.getLong(4);
                    key             = cursor.getString(7);
                    refPath         = cursor.getString(8);

                    if(cursor.getLong(4) > 100 * 1024 * 1024)
                    {
                        addToSQLiteERROR();
                    }
                    else
                    {
                        uploadResourceToDataBase();
                    }

                }
                else
                {
                    MyApplication.setIsUploading(false);
                    sqliteDataBaseUploadedOrError.close();
                    sqliteDataBaseUploading.close();
                    stopSelf();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                addToSQLiteERROR();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                int progress;

                try
                {
                    progress = (int) ((taskSnapshot.getBytesTransferred() * 100 / taskSnapshot.getTotalByteCount()));
                }catch (ArithmeticException e)
                {
                    progress = 0;
                }


                Intent RTReturn = new Intent(UploadResourcesListActivity.RECEIVE_UPLOAD_PROGRESS);
                RTReturn.putExtra("progress", progress);
                RTReturn.putExtra("file_path", file_path);
                LocalBroadcastManager.getInstance(UploadService.this).sendBroadcast(RTReturn);

                if(progress == 100)
                {
                    task.removeOnProgressListener(this);
                }
            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////UPDATE UPLOAD COUNT////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void updateResourceCNTs(final String refPath,final String key) {

        FirebaseDatabase.getInstance().getReferenceFromUrl(refPath)
                .child(key).child("item_cnt")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        FirebaseDatabase.getInstance().getReferenceFromUrl(refPath)
                                .child(key).child("item_cnt")
                                .setValue(dataSnapshot.getValue(Integer.class) + 1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void addToSQLiteERROR(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getDataResourceItems())
                .child(key);

        String push_key = reference.push().getKey();

        SimpleDateFormat dateF = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeF = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

        String date = dateF.format(new Date());
        String time = timeF.format(new Date());

        sqliteDataBaseUploading.deleteData(file_path);
        sqliteDataBaseUploadedOrError.insertData(title,file_path,100,size,date,time,1,0,key,push_key,refPath);

        getNextDataFromSQLiteDatabase();

    }


    private void getNextDataFromSQLiteDatabase()
    {
        Cursor cursor = sqliteDataBaseUploading.getData();

        if(cursor.moveToFirst())
        {

            MyApplication.setIsUploading(true);
            title           = cursor.getString(1);
            file_path       = cursor.getString(2);
            size            = cursor.getLong(4);
            key             = cursor.getString(7);
            refPath         = cursor.getString(8);

            if(cursor.getLong(4) > 100 * 1024 * 1024)
            {
                addToSQLiteERROR();
            }
            else
            {
                uploadResourceToDataBase();
            }
        }
        else
        {
            MyApplication.setIsUploading(false);
            sqliteDataBaseUploadedOrError.close();
            sqliteDataBaseUploading.close();
            stopSelf();
        }
    }

}
