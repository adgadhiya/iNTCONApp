package com.myapp.unknown.iNTCON.Downloads;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseDownloadedOrError;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseDownloading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by UNKNOWN on 2/9/2017.
 */
public class DownloadService extends Service {

    private SqliteDataBaseDownloading sqliteDataBaseDownloads;
    private SqliteDataBaseDownloadedOrError sqliteDataBaseDownloadedOrError;
    private String download_path,date,time,key,title,group_key;
    private Long size;
    private File file;


    ///////////////////////////////////////////////////
    //ON CREATE////////////
    ///////////////////////////////////////////////////
    @Override
    public void onCreate() {
        super.onCreate();
        sqliteDataBaseDownloads = new SqliteDataBaseDownloading(this);
        sqliteDataBaseDownloadedOrError = new SqliteDataBaseDownloadedOrError(this);
    }


    ///////////////////////////////////////////////////
    ////BIND SERVICE////////////
    ///////////////////////////////////////////////////
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    ///////////////////////////////////////////////////
    //ON SERVICE START////////////
    ///////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       Cursor cursor =  sqliteDataBaseDownloads.getData();

        if(cursor.moveToFirst())
        {
            MyApplication.setIsDownloading(true);
            title           = cursor.getString(1);
            download_path   = cursor.getString(2);
            size            = cursor.getLong(4);
            date            = cursor.getString(5);
            time            = cursor.getString(6);
            key             = cursor.getString(9);
            group_key       = cursor.getString(10);


            downloadResourceFromDataBase();
        }
        else
        {
            MyApplication.setIsDownloading(false);
            sqliteDataBaseDownloadedOrError.close();
            sqliteDataBaseDownloads.close();
            stopSelf();
        }

        return START_NOT_STICKY;
    }


    ///////////////////////////////////////////////////
    //ON DESTROY////////////
    ///////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    ///////////////////////////////////////////////////
    //DOWNLOAD RESOURCES FROM DATABASE////////////
    ///////////////////////////////////////////////////
    private void downloadResourceFromDataBase()
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(download_path);

        file = new File(getBaseContext().getCacheDir(),title);

            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    title = System.currentTimeMillis() + title;

                    Intent RTReturn = new Intent(DownloadResourcesActivity.RECEIVE_DOWNLOAD_COMPLETE);
                    RTReturn.putExtra("download_path", download_path);
                    LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(RTReturn);

                    File defaultFile = new File(Environment.getExternalStorageDirectory(),"iNTCON");
                    defaultFile.mkdirs();
                    File saveFile = new File(defaultFile,title);

                    try
                    {
                        copyFile(file,saveFile);
                        sqliteDataBaseDownloads.deleteData(download_path);
                        sqliteDataBaseDownloadedOrError.insertData(title,download_path,100,size,date,time,0,1,key,group_key);

                        downloadComplete(group_key);
                    }
                    catch (IOException e)
                    {
                        sqliteDataBaseDownloads.deleteData(download_path);
                        sqliteDataBaseDownloadedOrError.insertData(title,download_path,0,size,date,time,1,0,key,group_key);
                    }
                    finally
                    {

                        Cursor cursor =  sqliteDataBaseDownloads.getData();

                        if(cursor.moveToFirst())
                        {
                            MyApplication.setIsDownloading(true);
                            title           = cursor.getString(1);
                            download_path   = cursor.getString(2);
                            size            = cursor.getLong(4);
                            date            = cursor.getString(5);
                            time            = cursor.getString(6);
                            key             = cursor.getString(9);
                            group_key       = cursor.getString(10);

                            downloadResourceFromDataBase();
                        }
                        else
                        {
                            Toast.makeText(DownloadService.this, "All download completed", Toast.LENGTH_SHORT).show();
                            MyApplication.setIsDownloading(false);
                            sqliteDataBaseDownloadedOrError.close();
                            sqliteDataBaseDownloads.close();
                            stopSelf();

                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    sqliteDataBaseDownloads.deleteData(download_path);
                    sqliteDataBaseDownloadedOrError.insertData(title,download_path,0,size,date,time,1,0,key,group_key);

                    Cursor cursor =  sqliteDataBaseDownloads.getData();

                    if(cursor.moveToFirst())
                    {
                        MyApplication.setIsDownloading(true);
                        title           = cursor.getString(1);
                        download_path   = cursor.getString(2);
                        size            = cursor.getLong(4);
                        date            = cursor.getString(5);
                        time            = cursor.getString(6);
                        key             = cursor.getString(9);
                        group_key       = cursor.getString(10);

                        downloadResourceFromDataBase();
                    }
                    else
                    {
                        Toast.makeText(DownloadService.this, "All download completed", Toast.LENGTH_SHORT).show();
                        MyApplication.setIsDownloading(false);
                        sqliteDataBaseDownloadedOrError.close();
                        sqliteDataBaseDownloads.close();
                        stopSelf();

                    }
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    int progress = (int) ((taskSnapshot.getBytesTransferred() * 100 / taskSnapshot.getTotalByteCount()));

                    if(progress != 100)
                    {
                        Intent RTReturn = new Intent(DownloadResourcesActivity.RECEIVE_DOWNLOAD_PROGRESS);
                        RTReturn.putExtra("progress", progress);
                        RTReturn.putExtra("download_path", download_path);
                        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(RTReturn);
                    }
                }
            });
        }


    ///////////////////////////////////////////////////
    //UPDATE DOWNLOAD VALUES WHEN ITEM DOWNLOAD COMPLETES////////////
    ///////////////////////////////////////////////////
    private void downloadComplete(final String key){

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child("data_resource_downloads")
                .child(key).push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    ///////////////////////////////////////////////////
    //COPY FILE FROM CACHE TO LOCAL DISK////////////
    ///////////////////////////////////////////////////
    public static void copyFile(File src, File dst) throws IOException
    {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
}
