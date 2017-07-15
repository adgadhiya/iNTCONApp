package com.myapp.unknown.iNTCON.NavigationView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener {

    private EditText group_name_et,group_email_et,group_designation_et,group_contact_et,group_website_et,group_about_et;
    private ImageView group_profile;
    private LinearLayout linearLayout;
    private int group_type = 0;
    private Spinner spinner;

    private Bitmap bitmap;

    private DialogHandler dialogHandler;

    private File profilePath;

    private long aLong;

    private SharedPreferences sp;

    private  String group_id ;

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE ACTIVITY//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_create_group);

        FloatingActionButton create_group_btn = (FloatingActionButton) findViewById(R.id.create_group_btn);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.create_group_toolbar);
        group_name_et = (EditText) findViewById(R.id.create_group_group_name);
        group_profile = (ImageView) findViewById(R.id.create_group_group_profile);
        group_email_et = (EditText) findViewById(R.id.create_group_email);
        group_designation_et = (EditText) findViewById(R.id.create_group_designation);
        group_contact_et = (EditText) findViewById(R.id.create_group_contact);
        group_website_et = (EditText) findViewById(R.id.create_group_website);
        group_about_et = (EditText) findViewById(R.id.create_group_about);
        linearLayout = (LinearLayout) findViewById(R.id.create_group_detail_layout);
        spinner = (Spinner) findViewById(R.id.create_group_spinner);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        create_group_btn.setOnClickListener(this);

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        setSpinner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i)
                {
                    case 0:
                        linearLayout.setVisibility(View.GONE);
                        group_type = GlobalNames.getPrivateGroup();
                        break;

                    case 1:
                        linearLayout.setVisibility(View.VISIBLE);
                        group_type = GlobalNames.getUniversityOrCollege();
                        break;

                    case 2:
                        linearLayout.setVisibility(View.VISIBLE);
                        group_type = GlobalNames.getPrivateInstituteClg();
                        break;

                    case 3:
                        linearLayout.setVisibility(View.VISIBLE);
                        group_type = GlobalNames.getSCHOOL();
                        break;

                    case 4:
                        linearLayout.setVisibility(View.VISIBLE);
                        group_type = GlobalNames.getPrivateInstituteSchool();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if(ImageSaver.isExternalStorageWritable())
        {
            profilePath = new ImageSaver(this).setExternal(true).setDirectoryName("iNTCON").setFileName(aLong + ".jpg").createFile();
        }
        else
        {
            profilePath = new ImageSaver(this).setExternal(true).setDirectoryName("iNTCON").setFileName(aLong + ".jpg").createFile();
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(),"Roboto-Regular.ttf");
        group_name_et.setTypeface(typeface);
        group_email_et.setTypeface(typeface);
        group_designation_et.setTypeface(typeface);
        group_contact_et.setTypeface(typeface);
        group_website_et.setTypeface(typeface);
        group_about_et.setTypeface(typeface);


    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE OPTION MENU//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit,menu);
        menu.findItem(R.id.group_profile_edit_menu).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////ON ACTIVITY FOR RESULT////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED)
        {
            return;
        }

        InputStream is = null;

        if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK)
        {
            try {
                is = getContentResolver().openInputStream(Uri.fromFile(profilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            bitmap = BitmapFactory.decodeStream(is);

            if(bitmap != null )
            {
                group_profile.setImageBitmap(bitmap);
            }
        }
        else if(resultCode == RESULT_OK && requestCode == GlobalNames.getNoticeSendToGal())
        {
            if(data == null)
            {
                Toast.makeText(CreateGroup.this, "No Any Image Found", Toast.LENGTH_SHORT).show();
                return;
            }

            Crop.of(data.getData(), Uri.fromFile(profilePath)).withMaxSize(360,360).asSquare().start(this);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////SET SPINNER VIEW///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setSpinner(){

        List<Map<String, String>> items = new ArrayList<>();

        Map<String, String> item0 = new HashMap<>(2);
        item0.put("text", "Private Group");
        item0.put("subText", "Private Sharing.");
        items.add(item0);

        Map<String, String> item1 = new HashMap<>(2);
        item1.put("text", "Institute");
        item1.put("subText", "University/College");
        items.add(item1);

        Map<String, String> item2 = new HashMap<>(2);
        item2.put("text", "Private Institute");
        item2.put("subText", "For University/College Students");
        items.add(item2);

        Map<String, String> item3 = new HashMap<>(2);
        item3.put("text", "Institute");
        item3.put("subText", "School");
        items.add(item3);

        Map<String, String> item4 = new HashMap<>(2);
        item4.put("text", "Private Institute");
        item4.put("subText", "For School Students");
        items.add(item4);


        SimpleAdapter adapter = new SimpleAdapter(this, items,
                android.R.layout.two_line_list_item,
                new String[] {"text", "subText"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);

        spinner.setAdapter(adapter);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////ON CREATE GROUP CLICKED//////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view) {

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(this);

        if(connectionCheck.checkingConnextion()) {
            Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setActionTextColor(Color.YELLOW)
                    .show();
            return;
        }

        if((group_name_et.getText().toString().isEmpty() || group_about_et.getText().toString().isEmpty())
                && group_type == GlobalNames.getPrivateGroup())
        {
            Snackbar.make(view,"Group Name and/or Group Description are Empty",Snackbar.LENGTH_SHORT).show();
            return;
        }

        if((group_type != GlobalNames.getPrivateGroup()) &&
                (group_contact_et.getText().toString().isEmpty() ||
                        group_designation_et.getText().toString().isEmpty() ||
                        group_email_et.getText().toString().isEmpty()))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
            builder.setTitle("Error!");
            builder.setMessage("Fields containing dark images are mandatory.");
            builder.setPositiveButton("OK",null);
            builder.show();
            return;
        }

        if(bitmap == null){
            Snackbar.make(view,"You haven't choose any profile picture yet.",Snackbar.LENGTH_SHORT).show();
            return;
        }

        group_id = FirebaseDatabase.getInstance().getReference().getRoot().push().getKey();
        uploadProfile();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON OPTION ITEM ITEM SELECTED//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.group_profile_edit_menu)
        {
            aLong = System.currentTimeMillis();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, GlobalNames.getNoticeSendToGal());
            return true;
        }
        else if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////UPOAD PROFILE TO STORAGE/////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadProfile(){

        dialogHandler = new DialogHandler(this,false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] bytes = baos.toByteArray();

        final UploadTask task = FirebaseStorage.getInstance().getReferenceFromUrl(GlobalNames.getStorageRefUrl())
                .child(GlobalNames.getGroupProfile()).child(group_id).child(System.currentTimeMillis() + ".jpg").putBytes(bytes);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadToDataBase(taskSnapshot.getDownloadUrl().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogHandler.sendEmptyMessage(0);
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
                builder.setTitle("Error Message!");
                builder.setMessage("Some Error Occurred while uploading your profile picture. Please try again.");
                builder.setPositiveButton("OK",null);
            }
        });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////UPLOAD DATA TO DATABASE/////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadToDataBase(String profile_path){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        Map<String, Object> updateMap = new HashMap<>();

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////IF SELECTED GROUP TYPE IS PRIVATE GROUP////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        if(group_type == GlobalNames.getPrivateGroup())
        {
            GroupList groupList = new GroupList();
            groupList.setGroup_id(group_id);
            groupList.setAbout_group(group_about_et.getText().toString());
            groupList.setGroup_type(group_type);
            groupList.setUser_id(user_id);
            groupList.setCreated_on(format.format(new Date()));

            GroupProfile groupProfile = new GroupProfile();
            groupProfile.setGroup_name(group_name_et.getText().toString());
            groupProfile.setGroup_profile_path(profile_path);
            groupProfile.setGroup_id(group_id);
            groupProfile.setGroup_name_lower(group_name_et.getText().toString().replace(" ","").toLowerCase());

            UserGroupListClass userGroupListClass = new UserGroupListClass();
            userGroupListClass.setGroup_id(group_id);

            UserProfile userProfile = new UserProfile();
            userProfile.setUser_id(user_id);
            userProfile.setUserName_lower(sp.getString(AllPreferences.getUSERNAME(),GlobalNames.getNONE()).toLowerCase().replace(" ",""));
            userProfile.setUserName(sp.getString(AllPreferences.getUSERNAME(),GlobalNames.getNONE()));
            userProfile.setUserProfile(sp.getString(AllPreferences.getUserProfile(),GlobalNames.getNONE()));

            databaseReference.child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getGroupList())
                    .child(group_id).setValue(groupList);

            updateMap.put(GlobalNames.getUserGroupDetail()
                    + "/" + GlobalNames.getUserGroupList()
                    + "/" + user_id
                    + "/" + group_id,userGroupListClass);

            updateMap.put(GlobalNames.getGroupDetail()
                    + "/" + GlobalNames.getGroupProfile()
                    + "/" + group_id, groupProfile);

            updateMap.put(GlobalNames.getGroupDetail()
                    + "/" + GlobalNames.getGroupMemberList()
                    + "/" + group_id
                    + "/" + user_id, userProfile);

            updateMap.put(GlobalNames.getUserGroupDetail()
                    + "/" + GlobalNames.getUserAuthentication()
                    + "/" + user_id
                    + "/" + group_id, true);

            updateMap.put(GlobalNames.getGroupDetail()
                    + "/" + GlobalNames.getGroupMemberCount()
                    + "/" + group_id
                    + "/" + user_id, user_id);

            databaseReference.updateChildren(updateMap);

            AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
            builder.setTitle("Group Created");
            builder.setMessage("Your group " + group_name_et.getText().toString() +
                    " has been created successfully.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            builder.show();

        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////IF GROUP TYPE IS OTHER THAN PRIVATE GROUP//////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        else
        {
            GroupRequest groupRequest = new GroupRequest();
            groupRequest.setCreated_on(format.format(new Date()));
            groupRequest.setUser_id(user_id);
            groupRequest.setGroup_type(group_type);
            groupRequest.setContact(group_contact_et.getText().toString());
            groupRequest.setAbout_group(group_about_et.getText().toString());
            groupRequest.setDesignation(group_designation_et.getText().toString());
            groupRequest.setEmail(group_email_et.getText().toString());
            groupRequest.setGroup_id(group_id);
            groupRequest.setWebsite(group_website_et.getText().toString());

            GroupProfile groupProfile = new GroupProfile();
            groupProfile.setGroup_name(group_name_et.getText().toString());
            groupProfile.setGroup_profile_path(profile_path);
            groupProfile.setGroup_id(group_id);
            groupProfile.setGroup_name_lower(group_name_et.getText().toString().replace(" ","").toLowerCase());

            updateMap.put(GlobalNames.getGroupDetail()
                    + "/" + GlobalNames.getGroupRequests()
                    + "/" + group_id , groupRequest);

            updateMap.put(GlobalNames.getGroupDetail()
                    + "/" + GlobalNames.getGroupProfile()
                    + "/" + group_id, groupProfile);

            databaseReference.updateChildren(updateMap);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Groups other than Private Groups can directly upload Achievements and Events." +
                    " So We need to verify your group(Institute). We will inform you once we " +
                    "verify your institute via Email or Phone." +
                    " For Any query you can reach to Us Via Email intcon.help@gmail.com. Thank You.");
            builder.setTitle("Message");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    group_name_et.setText("");
                    group_email_et.setText("");
                    group_designation_et.setText("");
                    group_contact_et.setText("");
                    group_website_et.setText("");
                    group_about_et.setText("");
                    group_profile.setImageResource(R.mipmap.create_group_profile);
                    spinner.setSelection(0,true);

                }
            });
            builder.show();
        }

        dialogHandler.sendEmptyMessage(0);
    }
}
