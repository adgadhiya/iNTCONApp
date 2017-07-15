package com.myapp.unknown.iNTCON.Uploads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.DataResource.DataResourceItemProvider;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseUploadedOrError;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseUploading;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UploadResourcesListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUploading;
    private RecyclerView recyclerViewUploaded;
    TextView uploadedTV,uploadingTV;
    Toolbar toolbar;

    private LocalBroadcastManager bManager;

    private SqliteDataBaseUploadedOrError sqliteDataBaseUploadedOrError;
    private SqliteDataBaseUploading sqliteDataBaseUploading;

    private UploadedResourceListAdapter uploadedResourceListAdapter;
    private UploadingResourcesAdapter uploadingResourcesAdapter;

    private ArrayList<DataResourceItemProvider> uploadedDataResourceItemList, uploadingDataResourcesItemList;
    private ArrayList<Integer> uploadingProgressList, uploadedErrorList, uploadedCompleteList;
    private ArrayList<String> keys,refPath;

    public static final String RECEIVE_UPLOAD_PROGRESS = "com.myapp.unknown.iNTCON.UPLOAD_PROGRESS";
    public static final String RECEIVE_UPLOAD_COMPLETE = "com.myapp.unknown.iNTCON.UPLOAD_COMPLETE";


    ///////////////////////////////////////////////////////////////////////////
    ///////////ON CREATE ACTIVITY//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_upload_resources);

        toolbar = (Toolbar) findViewById(R.id.upload_resources_toolbar);
        recyclerViewUploading = (RecyclerView) findViewById(R.id.upload_resources_uploading_rv);
        recyclerViewUploaded = (RecyclerView) findViewById(R.id.upload_resources_uploaded_rv);
        uploadedTV = (TextView) findViewById(R.id.upload_resources_uploaded_tv);
        uploadingTV = (TextView) findViewById(R.id.upload_resources_uploading_tv);

        recyclerViewUploaded.setHasFixedSize(true);
        recyclerViewUploading.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerViewUploaded.setLayoutManager(linearLayoutManager);
        recyclerViewUploading.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sqliteDataBaseUploadedOrError = new SqliteDataBaseUploadedOrError(this);
        sqliteDataBaseUploading = new SqliteDataBaseUploading(this);

        ((DefaultItemAnimator) recyclerViewUploaded.getItemAnimator()).setSupportsChangeAnimations(false);
        ((DefaultItemAnimator) recyclerViewUploading.getItemAnimator()).setSupportsChangeAnimations(false);

        uploadingDataResourcesItemList = new ArrayList<>();
        uploadingProgressList = new ArrayList<>();

        uploadedDataResourceItemList = new ArrayList<>();
        uploadedCompleteList = new ArrayList<>();
        uploadedErrorList = new ArrayList<>();

        keys = new ArrayList<>();
        refPath = new ArrayList<>();

        getDataFromSqliteUploading();
        getDataFromSqLiteUploaded();

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_UPLOAD_COMPLETE);
        intentFilter.addAction(RECEIVE_UPLOAD_PROGRESS);
        bManager.registerReceiver(bReceiver, intentFilter);

        findViewById(R.id.upload_resources_clear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SqliteDataBaseUploadedOrError(UploadResourcesListActivity.this).deleteAll();
                getDataFromSqLiteUploaded();
            }
        });


        ///////////////////////////////////////////////////////////////////////////
        ///////////SHOW OR HIDE UPLOADED RESOURCES LIST/////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        uploadedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerViewUploading.setVisibility(View.GONE);

                if(recyclerViewUploaded.getVisibility() == View.VISIBLE)
                {
                    recyclerViewUploaded.setVisibility(View.GONE);
                }
                else
                {
                    recyclerViewUploaded.setVisibility(View.VISIBLE);
                }
            }
        });


        ///////////////////////////////////////////////////////////////////////////
        ///////////SHOW OR HIDE UPLOADING RESOURCES LIST//////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        uploadingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerViewUploaded.setVisibility(View.GONE);

                if(recyclerViewUploading.getVisibility() == View.VISIBLE)
                {
                    recyclerViewUploading.setVisibility(View.GONE);
                }
                else
                {
                    recyclerViewUploading.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////ON OPTION ITEM SELECTED//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////GET UPLOADED DATA FROM SQLITE DATABASE//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void getDataFromSqLiteUploaded() {

        Cursor cursor = sqliteDataBaseUploadedOrError.getAllData();

        uploadedDataResourceItemList.clear();
        uploadedCompleteList.clear();
        uploadedErrorList.clear();
        keys.clear();

        if (cursor.moveToFirst()) {
            uploadedTV.setText(getString(R.string.uploaded, cursor.getCount()));

            do {

                DataResourceItemProvider resourceItemProvider = new DataResourceItemProvider();

                uploadedErrorList.add(cursor.getInt(7));
                uploadedCompleteList.add(cursor.getInt(8));
                keys.add(cursor.getString(9));
                refPath.add(cursor.getString(11));

                resourceItemProvider.setDownload_path(cursor.getString(2));
                resourceItemProvider.setSize(cursor.getLong(4));
                resourceItemProvider.setTitle(cursor.getString(1));
                resourceItemProvider.setDate(cursor.getString(5));
                resourceItemProvider.setTime(cursor.getString(6));
                resourceItemProvider.setUid(GlobalNames.getNONE());
                resourceItemProvider.setKey(GlobalNames.getNONE());

                uploadedDataResourceItemList.add(resourceItemProvider);

            } while (cursor.moveToNext());

            setUploadedRecyclerViw();

        }
        else
        {
            uploadedTV.setText(getString(R.string.uploaded,0));

            if(uploadedResourceListAdapter != null)
            {
                uploadedResourceListAdapter.notifyDataSetChanged();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////SET UPLOADED RECYCLER VIEW//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void setUploadedRecyclerViw()
    {
        uploadedResourceListAdapter =
                new UploadedResourceListAdapter(this, uploadedDataResourceItemList,
                        uploadedErrorList, new UploadedResourceListAdapter.UploadedResourceListInterface() {
                    @Override
                    public void btn_clicked(int position) {

                        if(uploadedErrorList.get(position) == 1)
                        {

                            SimpleDateFormat dateF = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                            SimpleDateFormat timeF = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

                            sqliteDataBaseUploading.insertData(uploadedDataResourceItemList.get(position).getTitle(),
                                    uploadedDataResourceItemList.get(position).getDownload_path(),
                                    uploadedDataResourceItemList.get(position).getSize(),
                                    keys.get(position),
                                    refPath.get(position), dateF.format(new Date()), timeF.format(new Date()));

                            sqliteDataBaseUploadedOrError.deleteData(uploadedDataResourceItemList.get(position).getDownload_path());

                            getDataFromSqLiteUploaded();
                            getDataFromSqliteUploading();

                            if(!MyApplication.isUploading())
                            {
                                startService(new Intent(UploadResourcesListActivity.this,UploadService.class));
                            }

                        }
                        else if(uploadedCompleteList.get(position) == 1)
                        {

                            new SqliteDataBaseUploadedOrError(UploadResourcesListActivity.this)
                                    .deleteData(uploadedDataResourceItemList.get(position).getDownload_path());

                            getDataFromSqLiteUploaded();

                        }

                    }
                });

        recyclerViewUploaded.setAdapter(uploadedResourceListAdapter);
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////GET UPLOADING RESOURCE LIST FROM SQLITE DATABASE//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void getDataFromSqliteUploading() {

        Cursor cursor = sqliteDataBaseUploading.getAllData();

        uploadingDataResourcesItemList.clear();
        uploadingProgressList.clear();

        if(cursor.moveToFirst())
        {
            do
            {
                DataResourceItemProvider resourceItemProvider = new DataResourceItemProvider();

                uploadingProgressList.add(cursor.getInt(3));

                resourceItemProvider.setDownload_path(cursor.getString(2));
                resourceItemProvider.setSize(cursor.getLong(4));
                resourceItemProvider.setTitle(cursor.getString(1));
                resourceItemProvider.setDate(cursor.getString(9));
                resourceItemProvider.setTime(cursor.getString(10));
                resourceItemProvider.setUid(GlobalNames.getNONE());
                resourceItemProvider.setKey(GlobalNames.getNONE());

                uploadingDataResourcesItemList.add(resourceItemProvider);

            }while (cursor.moveToNext());

            uploadingTV.setText(getString(R.string.uploading,cursor.getCount()));

            uploadingResourcesAdapter = new UploadingResourcesAdapter
                    (
                            this,
                            uploadingDataResourcesItemList,
                            uploadingProgressList,
                            new UploadingResourcesAdapter.UploadingResourcesInterface() {
                                @Override
                                public void btn_clicked(int position) {

                                    sqliteDataBaseUploading.deleteData(uploadingDataResourcesItemList.get(position).getDownload_path());
                                    getDataFromSqliteUploading();

                                }
                            });

            recyclerViewUploading.setAdapter(uploadingResourcesAdapter);

        }
        else
        {
            uploadingTV.setText(getString(R.string.uploading,0));

            if(uploadingResourcesAdapter != null)
            {
                uploadingResourcesAdapter.notifyDataSetChanged();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////ON DESTROY//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        bManager.unregisterReceiver(bReceiver);
        sqliteDataBaseUploadedOrError.close();
        sqliteDataBaseUploading.close();
        super.onDestroy();
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////BROAD CAT RECEIVER//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if(intent.getAction().equals(RECEIVE_UPLOAD_PROGRESS))
            {

                String download_path = intent.getStringExtra("file_path");
                long progressLong = intent.getIntExtra("progress",0);

                for (int i=0 ; i<uploadingDataResourcesItemList.size();i++)
                {
                    if(uploadingProgressList.size() != 0 && download_path.equals(uploadingDataResourcesItemList.get(i).getDownload_path()))
                    {
                        uploadingProgressList.set(i, (int) progressLong);
                        uploadingResourcesAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            else if(intent.getAction().equals(RECEIVE_UPLOAD_COMPLETE))
            {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        String download_path = intent.getStringExtra("file_path");
                        sqliteDataBaseUploading.deleteData(download_path);
                        getDataFromSqliteUploading();
                        getDataFromSqLiteUploaded();
                    }
                });
            }
        }
    };
}