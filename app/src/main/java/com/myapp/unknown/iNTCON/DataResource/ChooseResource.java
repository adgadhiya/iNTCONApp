package com.myapp.unknown.iNTCON.DataResource;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.OtherClasses.FileSelectionActivity;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseUploading;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;
import com.myapp.unknown.iNTCON.Uploads.UploadService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChooseResource extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    Button button;
    private static final String UPLOAD_FILE = "upload" ;
    private String refPath;
    private SqliteDataBaseUploading sqliteDataBaseUploading;

    ArrayList<File> Files;

    private UploadResourceAdapter adapter;

    DataResourceProvider dataResourceProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_choose_resource);


        dataResourceProvider = getIntent().getParcelableExtra(getString(R.string.data_resource_item_key_intent));
        refPath = getIntent().getStringExtra(getString(R.string.data_resource_path_intent));

        sqliteDataBaseUploading = new SqliteDataBaseUploading(this);

        recyclerView = (RecyclerView) findViewById(R.id.choose_resource_rv);
        toolbar = (Toolbar) findViewById(R.id.choose_resource_toolbar);
        button = (Button) findViewById(R.id.choose_resource_choose_btn);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startActivityForResult(new Intent(ChooseResource.this,FileSelectionActivity.class),6326);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ChooseResource.this,FileSelectionActivity.class),6326);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 6326 && resultCode == RESULT_OK) {

            button.setVisibility(View.GONE);
            Files = (ArrayList<File>) data.getSerializableExtra(UPLOAD_FILE);

            if (Files != null && Files.size() > 0) {

                adapter = new UploadResourceAdapter(ChooseResource.this, Files, new UploadResourceAdapter.UploadResourceInterface() {
                    @Override
                    public void uploadAllSelected() {
                        uploadResourcesToDataBase();
                    }

                    @Override
                    public void chooseOtherSelected() {
                        Files.clear();
                        adapter.notifyDataSetChanged();
                        startActivityForResult(new Intent(ChooseResource.this,FileSelectionActivity.class),6326);
                    }

                    @Override
                    public void removeSelected(int position) {
                        Files.remove(position);
                        adapter.notifyItemRemoved(position + 1);
                    }
                });

                recyclerView.setAdapter(adapter);

            }
        }
    }

    private void uploadResourcesToDataBase(){

        SimpleDateFormat dateF = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeF = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

        for(int i = 0; i<Files.size() ; i++) {

            sqliteDataBaseUploading
                    .insertData(Files.get(i).getName(),
                            Files.get(i).getAbsolutePath(),
                            Files.get(i).length(),
                            dataResourceProvider.getKey(),refPath, dateF.format(new Date()), timeF.format(new Date()));

            if (Files.size() - 1  == i)
            {
                if (!MyApplication.isUploading())
                {
                    startService(new Intent(ChooseResource.this, UploadService.class));
                }
                Files.clear();
                adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onDestroy() {
        sqliteDataBaseUploading.close();
        super.onDestroy();
    }
}
