package com.myapp.unknown.iNTCON.Downloads;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
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
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.DataResource.DataResourceItemProvider;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseDownloadedOrError;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseDownloading;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.io.File;
import java.util.ArrayList;

public class DownloadResourcesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDownloading;
    private RecyclerView recyclerViewDownloaded;
    TextView downloadedTV,downloadingTV;
    Toolbar toolbar;

    private SqliteDataBaseDownloadedOrError sqliteDataBaseDownloadedOrError;
    private SqliteDataBaseDownloading sqliteDataBaseDownloading;

    private DownloadedResourcesAdapter downloadedResourcesAdapter;
    private DownloadingResourcesAdapter downloadingResourcesAdapter;

    ArrayList<DataResourceItemProvider> downloadedDataResourceItemList,downloadingDataResourcesItemList;
    ArrayList<Integer> downloadingProgressList,downloadedErrorList,downloadedCompleteList;
    ArrayList<String> groupKeys;

    private LocalBroadcastManager bManager;

    public static final String RECEIVE_DOWNLOAD_PROGRESS = "com.myapp.unknown.iNTCON.DOWNLOAD_PROGRESS";
    public static final String RECEIVE_DOWNLOAD_COMPLETE = "com.myapp.unknown.iNTCON.DOWNLOAD_COMPLETE";


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE ACTIVITY////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(DownloadResourcesActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_download_resources);

        toolbar = (Toolbar) findViewById(R.id.download_resource_toolbar);
        recyclerViewDownloading = (RecyclerView) findViewById(R.id.download_resources_downloading_rv);
        recyclerViewDownloaded = (RecyclerView) findViewById(R.id.download_resource_downloaded_rv);
        downloadedTV = (TextView) findViewById(R.id.download_resources_downloaded_tv);
        downloadingTV = (TextView) findViewById(R.id.download_resources_downloading_tv);

        recyclerViewDownloaded.setHasFixedSize(true);
        recyclerViewDownloading.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerViewDownloaded.setLayoutManager(linearLayoutManager);
        recyclerViewDownloading.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sqliteDataBaseDownloadedOrError = new SqliteDataBaseDownloadedOrError(this);
        sqliteDataBaseDownloading = new SqliteDataBaseDownloading(this);

        ((DefaultItemAnimator) recyclerViewDownloaded.getItemAnimator()).setSupportsChangeAnimations(false);
        ((DefaultItemAnimator) recyclerViewDownloading.getItemAnimator()).setSupportsChangeAnimations(false);

        downloadingDataResourcesItemList = new ArrayList<>();
        downloadingProgressList = new ArrayList<>();
        groupKeys = new ArrayList<>();

        downloadedDataResourceItemList = new ArrayList<>();
        downloadedCompleteList = new ArrayList<>();
        downloadedErrorList = new ArrayList<>();

        getDataFromSqliteDownloading();
        getDataFromSqLiteDownloaded();

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_DOWNLOAD_COMPLETE);
        intentFilter.addAction(RECEIVE_DOWNLOAD_PROGRESS);
        bManager.registerReceiver(bReceiver, intentFilter);


        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////ON CLEAR ALL CLICKED////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        findViewById(R.id.download_resources_clear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SqliteDataBaseDownloadedOrError(DownloadResourcesActivity.this).deleteAll();
                getDataFromSqLiteDownloaded();
            }
        });



        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////HIDE OR SHOW CURRENTLY DOWNLOADING ITEMS//////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        downloadingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerViewDownloaded.setVisibility(View.GONE);

                if(recyclerViewDownloading.getVisibility() == View.VISIBLE)
                {
                    recyclerViewDownloading.setVisibility(View.GONE);
                }
                else
                {
                    recyclerViewDownloading.setVisibility(View.VISIBLE);
                }
            }
        });


        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////HIDE OR SHOW DOWNLOADED ITEMS/////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        downloadedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerViewDownloading.setVisibility(View.GONE);

                if(recyclerViewDownloaded.getVisibility() == View.VISIBLE)
                {
                    recyclerViewDownloaded.setVisibility(View.GONE);
                }
                else
                {
                    recyclerViewDownloaded.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON OPTION ITEM SELECTED///////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON DESTROY ACTIVITY////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        bManager.unregisterReceiver(bReceiver);
        sqliteDataBaseDownloadedOrError.close();
        sqliteDataBaseDownloading.close();
        super.onDestroy();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET DATA FROM SQLITE DATABASE(IF ANY)///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getDataFromSqLiteDownloaded() {

        Cursor cursor = sqliteDataBaseDownloadedOrError.getAllData();

            downloadedDataResourceItemList.clear();
            downloadedCompleteList.clear();
            downloadedErrorList.clear();

        if(cursor.moveToFirst())
        {
            downloadedTV.setText(getString(R.string.downloaded,cursor.getCount()));

            do
            {

                DataResourceItemProvider resourceItemProvider = new DataResourceItemProvider();

                downloadedErrorList.add(cursor.getInt(7));
                downloadedCompleteList.add(cursor.getInt(8));

                resourceItemProvider.setDownload_path(cursor.getString(2));
                resourceItemProvider.setSize(cursor.getLong(4));
                resourceItemProvider.setTitle(cursor.getString(1));
                resourceItemProvider.setDate(cursor.getString(5));
                resourceItemProvider.setTime(cursor.getString(6));
                resourceItemProvider.setUid(GlobalNames.getNONE());
                resourceItemProvider.setKey(cursor.getString(9));

                groupKeys.add(cursor.getString(10));
                downloadedDataResourceItemList.add(resourceItemProvider);

            }while (cursor.moveToNext());

            setDownloadedRecyclerView();

        }
        else
        {
            downloadedTV.setText(getString(R.string.downloaded,0));

            if(downloadedResourcesAdapter != null)
            {
                downloadedResourcesAdapter.notifyDataSetChanged();
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////SHOW LIST OF DOWNLOADED RESOURCES////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setDownloadedRecyclerView()
    {
        downloadedResourcesAdapter =
                new DownloadedResourcesAdapter(this,
                        downloadedDataResourceItemList,
                        downloadedErrorList,
                        new DownloadedResourcesAdapter.DownloadResourceInterface() {
                            @Override
                            public void btn_clicked(int position) {

                                if(downloadedErrorList.get(position) == 1)
                                {
                                    sqliteDataBaseDownloading.insertData
                                            (
                                                    downloadedDataResourceItemList.get(position).getKey(),
                                                    downloadedDataResourceItemList.get(position).getTime(),
                                                    downloadedDataResourceItemList.get(position).getTime(),
                                                    downloadedDataResourceItemList.get(position).getSize(),
                                                    downloadedDataResourceItemList.get(position).getDownload_path(),
                                                    downloadedDataResourceItemList.get(position).getTitle(),
                                                    groupKeys.get(position)
                                            );

                                    if(!MyApplication.isDownloading())
                                    {
                                        startService(new Intent(DownloadResourcesActivity.this,DownloadService.class));
                                    }

                                    new SqliteDataBaseDownloadedOrError(DownloadResourcesActivity.this)
                                            .deleteData(downloadedDataResourceItemList.get(position).getDownload_path());

                                    getDataFromSqLiteDownloaded();
                                    getDataFromSqliteDownloading();

                                }
                                else if(downloadedCompleteList.get(position) == 1)
                                {
                                    new SqliteDataBaseDownloadedOrError(DownloadResourcesActivity.this)
                                            .deleteData(downloadedDataResourceItemList.get(position).getDownload_path());

                                    getDataFromSqLiteDownloaded();

                                }

                            }

                            @Override
                            public void handleClicked(int position) {
                                handleItemClicked(position);
                            }
                        });

        recyclerViewDownloaded.setAdapter(downloadedResourcesAdapter);
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET CURRENTY DOWNLOADING DATA FROM SQLITE DATABSE////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getDataFromSqliteDownloading(){

        Cursor cursor = sqliteDataBaseDownloading.getAllData();

        downloadingDataResourcesItemList.clear();
        downloadingProgressList.clear();

        if(cursor.moveToFirst())
        {
            do
            {

                DataResourceItemProvider resourceItemProvider = new DataResourceItemProvider();

                downloadingProgressList.add(cursor.getInt(3));

                resourceItemProvider.setDownload_path(cursor.getString(2));
                resourceItemProvider.setSize(cursor.getLong(4));
                resourceItemProvider.setTitle(cursor.getString(1));
                resourceItemProvider.setDate(cursor.getString(5));
                resourceItemProvider.setTime(cursor.getString(6));
                resourceItemProvider.setUid(GlobalNames.getNONE());
                resourceItemProvider.setKey(GlobalNames.getNONE());

                downloadingDataResourcesItemList.add(resourceItemProvider);

            }while (cursor.moveToNext());

            downloadingTV.setText(getString(R.string.downloading,cursor.getCount()));

            downloadingResourcesAdapter = new DownloadingResourcesAdapter
                    (
                            this,
                            downloadingDataResourcesItemList,
                            downloadingProgressList,
                            new DownloadingResourcesAdapter.DownloadingResourcesInterface() {
                @Override
                public void handleClicked(int position) {

                    sqliteDataBaseDownloading.deleteData(downloadingDataResourcesItemList.get(position).getDownload_path());
                    getDataFromSqliteDownloading();
                }
            });

            recyclerViewDownloading.setAdapter(downloadingResourcesAdapter);

        }
        else
        {
            downloadingTV.setText(getString(R.string.downloading,0));

            if(downloadingResourcesAdapter != null)
            {
                downloadingResourcesAdapter.notifyDataSetChanged();
            }
        }
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////HANDLE WHEN ANY DOWNLOADED ITEM CLICKED/////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleItemClicked(int position){

        if(ImageSaver.isExternalStorageWritable())
        {

            File defaultFile = new File(Environment.getExternalStorageDirectory(),"iNTCON");
            defaultFile.mkdirs();
            File saveFile = new File(defaultFile,downloadedDataResourceItemList.get(position).getTitle());

            if(saveFile.exists())
            {

                String mimeType =  getMimeType(Uri.fromFile(saveFile));

                try
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(saveFile),mimeType);
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(saveFile),"application/*");
                    startActivity(intent);
                }

            }
            else
            {
                Toast.makeText(DownloadResourcesActivity.this, "File not found", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(DownloadResourcesActivity.this, "No any storage found", Toast.LENGTH_SHORT).show();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET MIME TYPE OF DOWNLOADED FILE///////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if(intent.getAction().equals(RECEIVE_DOWNLOAD_PROGRESS))
            {
                String download_path = intent.getStringExtra("download_path");
                long progressLong = intent.getIntExtra("progress",0);

                for (int i=0 ; i<downloadingDataResourcesItemList.size();i++)
                {
                    if(downloadingProgressList.size() != 0 &&
                            download_path.equals(downloadingDataResourcesItemList.get(i).getDownload_path()) )
                    {
                        downloadingProgressList.set(i, (int) progressLong);
                        downloadingResourcesAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
            else if(intent.getAction().equals(RECEIVE_DOWNLOAD_COMPLETE))
            {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        String download_path = intent.getStringExtra("download_path");
                        sqliteDataBaseDownloading.deleteData(download_path);
                        getDataFromSqliteDownloading();
                        getDataFromSqLiteDownloaded();
                    }
                });
            }
        }
    };

}
