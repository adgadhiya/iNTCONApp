package com.myapp.unknown.iNTCON.NavigationView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.InstRequest.CoursesOffered;
import com.myapp.unknown.iNTCON.InstRequest.FieldDetail;
import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class GroupInfo extends AppCompatActivity implements View.OnClickListener {

    private TextView group_info_group_members_tv,group_info_group_favourites_tv,group_info_group_downloads_tv,group_info_about_group_tv;
    private TextView group_info_group_name_tv,group_info_created_on_tv,group_info_user_name_tv,group_info_group_type_tv;
    private ImageView user_profile,group_profile;
    private Button group_info_send_request_btn,group_info_favourite_btn,group_info_edit;

    private EditText updateAboutGroup;
    private LinearLayout updateAboutGroupLayout;

    private String groupID;
    private boolean isIntconGroup;

    private DatabaseReference root;

    private boolean isFavourite,isMember,isRequested;

    private DecimalFormat decimalFormat;

    private  String uid;

    private long aLong;

    private int groupType;

    private ArrayList<String> fieldNameList, fieldKeyList;
    private ArrayList<FieldDetail> fieldArrayList;

    File file;

    private SharedPreferences sp;

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE ACTIVITY////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_group_info);

        isIntconGroup = getIntent().getBooleanExtra(getString(R.string.is_intcon_group),false);

        group_info_group_members_tv = (TextView) findViewById(R.id.group_info_group_members);
        group_info_group_favourites_tv = (TextView) findViewById(R.id.group_info_group_favourites_tv);
        group_info_favourite_btn = (Button) findViewById(R.id.group_info_group_favourites_btn);
        group_info_group_downloads_tv = (TextView) findViewById(R.id.group_info_group_downloads);
        group_info_send_request_btn = (Button) findViewById(R.id.group_info_send_request);
        group_info_about_group_tv = (TextView) findViewById(R.id.group_info_about_group);
        group_info_group_name_tv = (TextView) findViewById(R.id.group_info_group_name);
        group_info_created_on_tv = (TextView) findViewById(R.id.group_info_created_on);
        group_info_user_name_tv = (TextView) findViewById(R.id.group_info_user_name);
        group_info_group_type_tv = (TextView) findViewById(R.id.group_info_group_type);
        user_profile = (ImageView) findViewById(R.id.group_info_user_profile);
        group_profile = (ImageView) findViewById(R.id.group_info_group_profile);
        group_info_edit = (Button) findViewById(R.id.group_info_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_info_toolbar);

        updateAboutGroup = (EditText) findViewById(R.id.group_info_about_group_edit_et);
        Button update = (Button) findViewById(R.id.group_info_about_group_edit_update);
        Button cancel = (Button) findViewById(R.id.group_info_about_group_edit_cancel);
        updateAboutGroupLayout = (LinearLayout) findViewById(R.id.group_info_about_group_edit_layout);

        group_info_group_favourites_tv.setOnClickListener(this);
        group_info_send_request_btn.setOnClickListener(this);
        update.setOnClickListener(this);
        cancel.setOnClickListener(this);
        group_info_edit.setOnClickListener(this);
        group_info_favourite_btn.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        decimalFormat = new DecimalFormat("#,###,###");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        sp = getSharedPreferences(AllPreferences.getPreferenceName(), MODE_PRIVATE);

        if(getIntent().getStringExtra(getString(R.string.group_info_group_id)) != null)
        {
            groupID = getIntent().getStringExtra(getString(R.string.group_info_group_id));
        }
        else
        {
            groupID = sp.getString(AllPreferences.getGroupKey(), GlobalNames.getNONE());
        }

        root = FirebaseDatabase.getInstance().getReference().getRoot();

        if(ImageSaver.isExternalStorageWritable())
        {
            file = new ImageSaver(this).setExternal(true).setDirectoryName("iNTCON").setFileName(aLong + ".jpg").createFile();
        }
        else
        {
            file = new ImageSaver(this).setExternal(true).setDirectoryName("iNTCON").setFileName(aLong + ".jpg").createFile();
        }

        getGroupInfo();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "AlteHaasGroteskBold.ttf");
        group_info_group_favourites_tv.setTypeface(typeface);
        group_info_group_members_tv.setTypeface(typeface);
        group_info_group_downloads_tv.setTypeface(typeface);
        group_info_user_name_tv.setTypeface(typeface);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                getUserAuthentication();
            }
        },1000);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON OPTION ITEM SELECTED///////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET USER AUTHENTICATION///////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUserAuthentication() {

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication()).child(uid)
                .child(groupID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if(dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class))
                        {
                            group_info_edit.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            group_info_edit.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////CALLED WHEN CHANGE GROUP PROFILE SELECTED FROM OPTION MENU///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void changeGroupProfileSelected() {

        aLong = System.currentTimeMillis();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,GlobalNames.getAchSendToGal());

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON ACTIVITYFORRESULT///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK)
        {
            final DialogHandler dialogHandler = new DialogHandler(GroupInfo.this,false);

            final UploadTask task = FirebaseStorage.getInstance().getReferenceFromUrl(GlobalNames.getStorageRefUrl())
                    .child(GlobalNames.getGroupProfile()).child(groupID).child(aLong + ".jpg").putFile(Uri.fromFile(file));

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {


                    root.child(GlobalNames.getGroupDetail())
                            .child(GlobalNames.getIntconGroupProfile())
                            .child(groupID).child(GlobalNames.getGroupProfilePath())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists())
                                    {
                                        root.child(GlobalNames.getGroupDetail())
                                                .child(GlobalNames.getIntconGroupProfile())
                                                .child(groupID).child(GlobalNames.getGroupProfilePath())
                                                .setValue(taskSnapshot.getDownloadUrl().toString());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    root.child(GlobalNames.getGroupDetail())
                            .child(GlobalNames.getGroupProfile())
                            .child(groupID).child(GlobalNames.getGroupProfilePath())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists())
                                    {
                                        root.child(GlobalNames.getGroupDetail())
                                                .child(GlobalNames.getGroupProfile())
                                                .child(groupID).child(GlobalNames.getGroupProfilePath())
                                                .setValue(taskSnapshot.getDownloadUrl().toString());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    dialogHandler.sendEmptyMessage(0);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfo.this);
                    builder.setTitle("Error Message!");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK",null);

                    dialogHandler.sendEmptyMessage(0);

                }
            });

        }
        else if(resultCode == RESULT_OK && requestCode == GlobalNames.getAchSendToGal() && data.getData() != null)
        {
            Crop.of(data.getData(), Uri.fromFile(file)).withMaxSize(360,360).withAspect(360,360).start(this);
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////CALLED WHEN CHANGE GROUP NAME SELECTED FROM OPTION MENU/////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void changeGroupNameSelected(){

        View view = getLayoutInflater().inflate(R.layout.edit_text_layout,null);
        final EditText editText = (EditText) view.findViewById(R.id.et);

        editText.setText(group_info_group_name_tv.getText().toString());

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view,15,15,15,15);
        builder.setTitle("Update Group Name");

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(editText.getText().toString().isEmpty())
                {
                    Toast.makeText(GroupInfo.this, "Group name can not be empty", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    root.child(GlobalNames.getGroupDetail())
                            .child(GlobalNames.getIntconGroupProfile())
                            .child(groupID)
                            .child(GlobalNames.getGroupName())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists())
                                    {
                                        root.child(GlobalNames.getGroupDetail())
                                                .child(GlobalNames.getIntconGroupProfile())
                                                .child(groupID)
                                                .child(GlobalNames.getGroupName())
                                                .setValue(editText.getText().toString());

                                        root.child(GlobalNames.getGroupDetail())
                                                .child(GlobalNames.getIntconGroupProfile())
                                                .child(groupID)
                                                .child(GlobalNames.getGroupNameLower())
                                                .setValue(editText.getText().toString().toLowerCase().replace(" ",""));
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                    root.child(GlobalNames.getGroupDetail())
                            .child(GlobalNames.getGroupProfile())
                            .child(groupID)
                            .child(GlobalNames.getGroupName())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists())
                                    {
                                        root.child(GlobalNames.getGroupDetail())
                                                .child(GlobalNames.getGroupProfile())
                                                .child(groupID)
                                                .child(GlobalNames.getGroupName()).setValue(editText.getText().toString());

                                        root.child(GlobalNames.getGroupDetail())
                                                .child(GlobalNames.getGroupProfile())
                                                .child(groupID)
                                                .child(GlobalNames.getGroupNameLower()).setValue(editText.getText().toString().toLowerCase().replace(" ",""));
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel",null);

        builder.show();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET GROUP INFO///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getGroupInfo()
   {

       DatabaseReference ref;

       ref = root.child(GlobalNames.getGroupDetail())
               .child(GlobalNames.getIntconGroupDetail()).child(groupID);

       final DatabaseReference finalRef = ref;
       ref.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {

                       if(!dataSnapshot.exists())
                       {
                           finalRef.removeEventListener(this);
                           return;
                       }

                       getProfiles(dataSnapshot.getValue(GroupList.class));
                       getGroupOtherInfo(dataSnapshot.getValue(GroupList.class));

                       group_info_created_on_tv.setText(getString(R.string.created_on,dataSnapshot.getValue(GroupList.class).getCreated_on()));
                       group_info_about_group_tv.setText(dataSnapshot.getValue(GroupList.class).getAbout_group());

                       groupType = dataSnapshot.getValue(GroupList.class).getGroup_type();

                       switch (groupType)
                       {
                           case 2503 :
                               group_info_group_type_tv.setText(getString(R.string.private_group_type));
                               break;

                           case 2504 :
                               group_info_group_type_tv.setText(getString(R.string.institute_clg_type));
                               break;

                           case 2505 :
                               group_info_group_type_tv.setText(getString(R.string.institute_school_type));
                               break;

                           case 2506 :
                               group_info_group_type_tv.setText(getString(R.string.private_institute_clg_type));
                               break;

                           case 2507 :
                               group_info_group_type_tv.setText(getString(R.string.private_institute_school_type));
                               break;

                           default:
                               group_info_group_type_tv.setText(getString(R.string.group_type_not_found));
                               break;
                       }

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });


       /////////////////////////////////////////////////////////////////////////////////////////////////
       /////////////////////////////////////////////////////////////////////////////////////////////////
       /////////////////////////////////////////////////////////////////////////////////////////////////

       ref = root.child(GlobalNames.getGroupDetail())
               .child(GlobalNames.getGroupList()).child(groupID);

       final DatabaseReference finalRef1 = ref;
       ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               if(!dataSnapshot.exists())
               {
                   finalRef1.removeEventListener(this);
                   return;
               }

               getProfiles(dataSnapshot.getValue(GroupList.class));
               getGroupOtherInfo(dataSnapshot.getValue(GroupList.class));

               group_info_created_on_tv.setText(getString(R.string.created_on,dataSnapshot.getValue(GroupList.class).getCreated_on()));
               group_info_about_group_tv.setText(dataSnapshot.getValue(GroupList.class).getAbout_group());

               groupType = dataSnapshot.getValue(GroupList.class).getGroup_type();

               switch (groupType)
               {
                   case 2503 :
                       group_info_group_type_tv.setText(getString(R.string.private_group_type));
                       break;

                   case 2504 :
                       group_info_group_type_tv.setText(getString(R.string.institute_clg_type));
                       break;

                   case 2505 :
                       group_info_group_type_tv.setText(getString(R.string.institute_school_type));
                       break;

                   case 2506 :
                       group_info_group_type_tv.setText(getString(R.string.private_institute_clg_type));
                       break;

                   case 2507 :
                       group_info_group_type_tv.setText(getString(R.string.private_institute_school_type));
                       break;

                   default:
                       group_info_group_type_tv.setText(getString(R.string.group_type_not_found));
                       break;
               }

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
   }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET GROUP PROFILE////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getProfiles(GroupList groupList){

        DatabaseReference refPROFILE;

        refPROFILE = root.child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getIntconGroupProfile())
                .child(groupID);

        ///////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////GET GROUP PROFILE IF INTCON GROUP/////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////
        final DatabaseReference finalRefPROFILE = refPROFILE;
        refPROFILE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    finalRefPROFILE.removeEventListener(this);
                    return;
                }

                group_info_group_name_tv.setText(dataSnapshot.getValue(GroupProfile.class).getGroup_name());
                Glide.with(getApplicationContext()).load(dataSnapshot.getValue(GroupProfile.class).getGroup_profile_path())
                        .into(group_profile);

            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        ///////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////GET GROUP PROFILE IF NORMAL GROUP/////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        refPROFILE = root.child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getGroupProfile())
                .child(groupList.getGroup_id());

        final DatabaseReference finalRefPROFILE1 = refPROFILE;
        refPROFILE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    finalRefPROFILE1.removeEventListener(this);
                    return;
                }

                group_info_group_name_tv.setText(dataSnapshot.getValue(GroupProfile.class).getGroup_name());
                Glide.with(getApplicationContext()).load(dataSnapshot.getValue(GroupProfile.class).getGroup_profile_path())
                        .into(group_profile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        root.child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserProfile())
                .child(groupList.getUser_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                group_info_user_name_tv.setText(dataSnapshot.getValue(UserProfile.class).getUserName());
                Glide.with(getApplicationContext()).load(dataSnapshot.getValue(UserProfile.class).getUserProfile())
                        .into(user_profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON DESTROY////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        Glide.clear(group_profile);
        super.onDestroy();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET GROUP OTHER INFO////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getGroupOtherInfo(GroupList groupList)
    {

        root.child(GlobalNames.getGroupDetail()).child(GlobalNames.getGroupFavourites())
                .child(groupList.getGroup_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<String> favouritesUIDs = new ArrayList<>();

                        if(dataSnapshot.exists())
                        {
                            group_info_group_favourites_tv.setText(String.valueOf(decimalFormat.format(dataSnapshot.getChildrenCount())));

                            Iterator iterator = dataSnapshot.getChildren().iterator();

                            while (iterator.hasNext()){
                                favouritesUIDs.add(((DataSnapshot)iterator.next()).getValue(String.class));
                            }

                            if(favouritesUIDs.toString().matches("\\[.*\\b" + uid + "\\b.*]"))
                            {
                                group_info_favourite_btn.setText(R.string.not_favourite);
                                isFavourite = true;
                            }
                            else
                            {
                                group_info_favourite_btn.setText(R.string.favourites);
                                isFavourite = false;
                            }
                        }
                        else
                        {
                            group_info_group_favourites_tv.setText(getString(R.string.zero_favourites));
                            group_info_favourite_btn.setText(R.string.favourites);
                            isFavourite = false;

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getDataResourceDownloads())
                .child(groupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        group_info_group_downloads_tv.setText(String.valueOf(decimalFormat.format(dataSnapshot.getChildrenCount())));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        root.child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getGroupMemberCount()).child(groupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<String> memberUIDs = new ArrayList<>();

                        if(dataSnapshot.exists())
                        {
                            group_info_group_members_tv.setText(String.valueOf(decimalFormat.format(dataSnapshot.getChildrenCount())));

                            Iterator iterator = dataSnapshot.getChildren().iterator();

                            while (iterator.hasNext())
                            {
                                memberUIDs.add(((DataSnapshot)iterator.next()).getValue(String.class));
                            }

                            if(memberUIDs.toString().matches("\\[.*\\b" + uid + "\\b.*]"))
                            {
                                group_info_send_request_btn.setText(getString(R.string.leave_group));
                                isMember = true;
                            }
                            else
                            {
                                checkRequested();
                                isMember = false;
                            }

                        }
                        else
                        {
                            group_info_group_members_tv.setText(getString(R.string.nil));
                            isMember = false;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////CHECK GROUP MEMBER OR REQUESTED OR NOT///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkRequested() {

        root.child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserRequests())
                .child(groupID)
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            group_info_send_request_btn.setText(getString(R.string.cancel_request));
                            isRequested = true;
                        }
                        else
                        {
                            group_info_send_request_btn.setText(getString(R.string.send_request));
                            isRequested = false;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON BUTTON CLICKED////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {

            ////////////////////////////////////////
            ///////ON EDIT SELECTED/////////////////
            ////////////////////////////////////////
            case R.id.group_info_edit :

                final CharSequence[] chooseFrom;

                if(groupType == GlobalNames.getUniversityOrCollege() || groupType == GlobalNames.getPrivateInstituteClg())
                {
                    chooseFrom = new CharSequence[]{"Group Profile", "Group Name", "About Group","Courses Offered"};
                }
                else
                {
                    chooseFrom = new CharSequence[]{"Group Profile", "Group Name", "About Group"};

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfo.this);

                builder.setTitle("Update Group Information");

                builder.setItems(chooseFrom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (chooseFrom[which].equals("Group Profile"))
                        {
                            changeGroupProfileSelected();
                        }
                        else if(chooseFrom[which].equals("Group Name"))
                        {
                            changeGroupNameSelected();
                        }
                        else if(chooseFrom[which].equals("About Group"))
                        {
                            updateAboutGroupLayout.setVisibility(View.VISIBLE);
                            updateAboutGroup.setVisibility(View.VISIBLE);
                            group_info_about_group_tv.setVisibility(View.GONE);
                            updateAboutGroup.requestFocus();
                            updateAboutGroup.setText(group_info_about_group_tv.getText().toString());
                            updateAboutGroup.setSelection(updateAboutGroup.getText().length());
                        }
                        else if(chooseFrom[which].equals("Courses Offered"))
                        {
                            Intent intent = new Intent(GroupInfo.this, CoursesOffered.class);
                            intent.putExtra(getString(R.string.group_id_intent),groupID);
                            startActivity(intent);
                        }

                        dialog.dismiss();
                    }
                });

                builder.show();

                break;


            ////////////////////////////////////////
            ///////ON FAVOURITE SELECTED////////////
            ////////////////////////////////////////
            case R.id.group_info_group_favourites_btn :

                if(isFavourite)
                {
                    root.child(GlobalNames.getGroupDetail())
                            .child(GlobalNames.getGroupFavourites())
                            .child(groupID).child(uid).removeValue();

                }
                else
                {
                    root.child(GlobalNames.getGroupDetail())
                            .child(GlobalNames.getGroupFavourites())
                            .child(groupID).child(uid).setValue(uid);
                }

                break;


            //////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////WHEN SEND REQUEST BUTTON CALLED//////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////
            case R.id.group_info_send_request :

                if(group_info_send_request_btn.getText().toString().isEmpty())
                {
                    Toast.makeText(GroupInfo.this, "Please wait...", Toast.LENGTH_SHORT).show();
                    return;
                }

                //////////////////////////////////////////////////////////////////////////////////////////////////////
                ///////CALLED OPTION IF ALREADY MEMBER////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////
                if(isMember)
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setTitle("Confirm Exit Group.");
                    builder1.setMessage("Are you sure you want to exit from this group?");
                    builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getGroupDetail())
                                    .child(GlobalNames.getGroupMemberList())
                                    .child(groupID)
                                    .child(uid)
                                    .removeValue();

                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getGroupDetail())
                                    .child(GlobalNames.getGroupMemberCount())
                                    .child(groupID)
                                    .child(uid)
                                    .removeValue();

                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getUserGroupDetail())
                                    .child(GlobalNames.getUserAuthentication())
                                    .child(uid)
                                    .child(groupID)
                                    .removeValue();

                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getUserGroupDetail())
                                    .child(GlobalNames.getUserGroupList())
                                    .child(uid)
                                    .child(groupID)
                                    .removeValue();

                        }
                    });

                    builder1.setNegativeButton("NO", null);
                    builder1.show();
                }
                //////////////////////////////////////////////////////////////////////////////////////////////////////
                ///////CALLED IF REQUESTED////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////
                else if(isRequested)
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setTitle("Confirm Cancel Request.");
                    builder1.setMessage("Are you sure you want to cancel this request?");
                    builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getUserGroupDetail())
                                    .child(GlobalNames.getUserRequests())
                                    .child(groupID)
                                    .child(uid).removeValue();

                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getUserGroupDetail())
                                    .child(GlobalNames.getMyGroupRequests())
                                    .child(uid).child(groupID).removeValue();
                        }
                    });

                    builder1.setNegativeButton("NO", null);
                    builder1.show();
                }
                //////////////////////////////////////////////////////////////////////////////////////////////////////
                ///////IF NEITHER MEMBER NOR REQUESTED//////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////
                else
                {
                    if(isIntconGroup)
                    {
                        handleiNTCONGroupRequest();
                        return;
                    }

                    //////////////////////////////////////////////////////////////////////////////////////////////////////
                    ///////IF UNIVERSITY OR PRIVATE INSTITUTE//////////////////////////////////////////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(groupType == GlobalNames.getPrivateInstituteClg() || groupType == GlobalNames.getUniversityOrCollege())
                    {
                        View v = getLayoutInflater().inflate(R.layout.group_info_spinner,null);
                        final Spinner spinner = (Spinner) v.findViewById(R.id.group_info_spinner);

                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupInfo.this);
                        builder1.setTitle("Choose Field for this Group.");
                        builder1.setView(v,15,15,15,15);

                        builder1.setPositiveButton("send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(spinner.getChildCount() == 0)
                                {
                                    return;
                                }

                                if(spinner.getSelectedItem().equals(GlobalNames.getNONE()))
                                {
                                    Toast.makeText(GroupInfo.this, "You haven't choose any field for this group.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                                    UserRequestDetailClass requestDetailClass = new UserRequestDetailClass();
                                    requestDetailClass.setUser_id(uid);
                                    requestDetailClass.setField(spinner.getSelectedItem().toString());
                                    requestDetailClass.setRequested_on(format.format(new Date()));
                                    requestDetailClass.setUserName(sp.getString(AllPreferences.getUSERNAME(),GlobalNames.getNONE()));
                                    requestDetailClass.setUser_profile(sp.getString(AllPreferences.getUserProfile(),GlobalNames.getNONE()));
                                    requestDetailClass.setField_id(fieldKeyList.get(spinner.getSelectedItemPosition()));

                                    String providers = FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0);

                                    if(providers.equals("password") || providers.equals("google.com"))
                                    {
                                        requestDetailClass.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    }

                                    FirebaseDatabase.getInstance().getReference().getRoot()
                                            .child(GlobalNames.getUserGroupDetail())
                                            .child(GlobalNames.getUserRequests())
                                            .child(groupID)
                                            .child(uid).setValue(requestDetailClass);

                                    FirebaseDatabase.getInstance().getReference().getRoot()
                                            .child(GlobalNames.getUserGroupDetail())
                                            .child(GlobalNames.getMyGroupRequests())
                                            .child(uid).child(groupID).setValue(groupID);
                                }

                            }
                        });

                        builder1.setNegativeButton("cancel",null);

                        builder1.show();

                        FirebaseDatabase.getInstance().getReference()
                                .getRoot().child(GlobalNames.getGroupDetail())
                                .child(GlobalNames.getCoursesOffered())
                                .child(groupID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(!dataSnapshot.exists())
                                {
                                    Toast.makeText(GroupInfo.this, "No any course found for this group. Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                    fieldNameList = new ArrayList<>();
                                    fieldKeyList = new ArrayList<>();
                                    fieldArrayList = new ArrayList<>();
                                    int i = 0;

                                    fieldKeyList.add(GlobalNames.getNONE());
                                    fieldNameList.add(GlobalNames.getNONE());

                                    Iterator iterator = dataSnapshot.getChildren().iterator();

                                    while (iterator.hasNext())
                                    {
                                        fieldArrayList.add(((DataSnapshot)iterator.next()).getValue(FieldDetail.class));
                                        fieldNameList.add(fieldArrayList.get(i).getFieldName());
                                        fieldKeyList.add(fieldArrayList.get(i).getKey());
                                        i++;
                                    }

                                    spinner.setAdapter(new ArrayAdapter<>(GroupInfo.this,android.R.layout.simple_list_item_1, fieldNameList));

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(GroupInfo.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                        UserRequestDetailClass requestDetailClass = new UserRequestDetailClass();
                        requestDetailClass.setUser_id(uid);
                        requestDetailClass.setRequested_on(format.format(new Date()));
                        requestDetailClass.setUserName(sp.getString(AllPreferences.getUSERNAME(),GlobalNames.getNONE()));
                        requestDetailClass.setUser_profile(sp.getString(AllPreferences.getUserProfile(),GlobalNames.getNONE()));
                        requestDetailClass.setField_id(GlobalNames.getDummyNode());

                        String providers = FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0);

                        if(providers.equals("password") || providers.equals("google.com"))
                        {
                            requestDetailClass.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        }

                        FirebaseDatabase.getInstance().getReference().getRoot()
                                .child(GlobalNames.getUserGroupDetail())
                                .child(GlobalNames.getUserRequests())
                                .child(groupID)
                                .child(uid).setValue(requestDetailClass);


                        FirebaseDatabase.getInstance().getReference().getRoot()
                                .child(GlobalNames.getUserGroupDetail())
                                  .child(GlobalNames.getMyGroupRequests())
                                .child(uid).child(groupID).setValue(groupID);
                    }

                }

                break;

            //////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////WHEN ABOUT GROUP EDIT UPDATE CALLED//////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////
            case R.id.group_info_about_group_edit_update :


                root.child(GlobalNames.getGroupDetail())
                        .child(GlobalNames.getIntconGroupDetail())
                        .child(groupID)
                        .child(GlobalNames.getAboutGroup())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists())
                                {
                                    root.child(GlobalNames.getGroupDetail())
                                            .child(GlobalNames.getIntconGroupDetail())
                                            .child(groupID)
                                            .child(GlobalNames.getAboutGroup()).setValue(updateAboutGroup.getText().toString());
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                root.child(GlobalNames.getGroupDetail())
                        .child(GlobalNames.getGroupList())
                        .child(groupID)
                        .child(GlobalNames.getAboutGroup())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists())
                                {
                                    root.child(GlobalNames.getGroupDetail())
                                            .child(GlobalNames.getGroupList())
                                            .child(groupID)
                                            .child(GlobalNames.getAboutGroup()).setValue(updateAboutGroup.getText().toString());
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                group_info_about_group_tv.setText(updateAboutGroup.getText().toString());

                updateAboutGroupLayout.setVisibility(View.GONE);
                updateAboutGroup.setVisibility(View.GONE);
                group_info_about_group_tv.setVisibility(View.VISIBLE);

                break;


            //////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////WHEN ABOUT GROUP EDIT CANCEL CALLED//////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////
            case R.id.group_info_about_group_edit_cancel :
                updateAboutGroupLayout.setVisibility(View.GONE);
                updateAboutGroup.setVisibility(View.GONE);
                group_info_about_group_tv.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////CALLED IF GROUP IS INTCON GROUP///////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleiNTCONGroupRequest() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot();

        Map<String,Object> map = new HashMap<>();

        UserProfile userProfile = new UserProfile();
        userProfile.setUser_id(uid);
        userProfile.setUserProfile(sp.getString(AllPreferences.getUserProfile(),GlobalNames.getNONE()));
        userProfile.setUserName_lower(sp.getString(AllPreferences.getUSERNAME(),GlobalNames.getNONE()).toLowerCase().replace(" ",""));
        userProfile.setUserName(sp.getString(AllPreferences.getUSERNAME(),GlobalNames.getNONE()));

        map.put(GlobalNames.getGroupDetail()
                + "/" + GlobalNames.getGroupMemberList()
                + "/" + groupID
                + "/" + uid,userProfile);

        map.put(GlobalNames.getGroupDetail()
                + "/" + GlobalNames.getGroupMemberCount()
                + "/" + groupID
                + "/" + uid, uid);

        UserGroupListClass userGroupListClass = new UserGroupListClass();
        userGroupListClass.setGroup_id(groupID);
        userGroupListClass.setField_key(GlobalNames.getDummyNode());
        userGroupListClass.setYear(GlobalNames.getDummyNode());

        map.put(GlobalNames.getUserGroupDetail()
                + "/" + GlobalNames.getUserGroupList()
                + "/" + uid
                + "/" + groupID, userGroupListClass);


        map.put(GlobalNames.getUserGroupDetail()
                + "/" + GlobalNames.getUserAuthentication()
                + "/" + uid
                + "/" + groupID, false);

        reference.updateChildren(map);
    }
}
