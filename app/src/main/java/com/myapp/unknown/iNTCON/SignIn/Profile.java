package com.myapp.unknown.iNTCON.SignIn;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapp.unknown.iNTCON.OtherClasses.DashBoard;
import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private ImageView userProfile;
    private TextView user_name_tv;

    private String userName;

    private String uid;

    private Bitmap bitmap;
    private Uri uri;
    private File file;
    private File profilePath;

    private DialogHandler dialogHandler;

    private ArrayList<String> user_key_list;
    private ArrayList<String> user_requests_key_list;

    private SharedPreferences sp;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON CREATE/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        userProfile = (ImageView) findViewById(R.id.user_profile_iv);
        user_name_tv = (TextView) findViewById(R.id.user_name_tv);
        ImageButton edit_user_name = (ImageButton) findViewById(R.id.user_name_edit_btn);
        TextView profile_tv = (TextView) findViewById(R.id.profile_tv);

        userProfile.setImageResource(R.mipmap.profile_default);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"Roboto-Regular.ttf");
        profile_tv.setTypeface(typeface);

        setSupportActionBar(toolbar);

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userProfile.setOnClickListener(this);
        edit_user_name.setOnClickListener(this);

        if(sp.getString(AllPreferences.getUserProfile(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
        {
            getUserProfile();
        }
        else
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            userName = sp.getString(AllPreferences.getUSERNAME(),GlobalNames.getNONE());
            user_name_tv.setText(sp.getString(AllPreferences.getUSERNAME(),GlobalNames.getNONE()));
            Glide.with(this)
                    .load(sp.getString(AllPreferences.getUserProfile(),GlobalNames.getNONE()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(userProfile);
        }

        user_key_list = new ArrayList<>();
        user_requests_key_list = new ArrayList<>();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON CREATE OPTION MENU/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done,menu);
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON OPTION ITEM SELECTED/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_done )
        {
            getUserGroupList();

            return true;
        }
        else if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else
        {
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON BACK PRESSED/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

        if(!sp.getString(AllPreferences.getUserProfile(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
        {
            super.onBackPressed();
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON ACTIVITY FOR RESULT////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED)
        {
            return;
        }

        if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK)
        {
            if(profilePath != null)
            {
                Glide.with(this).load(profilePath.getAbsolutePath()).into(userProfile);
            }
            else
            {
                Toast.makeText(Profile.this, "No image Found.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(resultCode == RESULT_OK && requestCode == GlobalNames.getNoticeSendToGal())
        {
            if(data == null)
            {
                Toast.makeText(Profile.this, "No Any Image Found", Toast.LENGTH_SHORT).show();
                return;
            }

            uri = data.getData();
            Crop.of(data.getData(), Uri.fromFile(profilePath)).withMaxSize(360,360).asSquare().start(this);
        }
        else if(resultCode == RESULT_OK && requestCode == GlobalNames.getNoticeSendToCamera())
        {
            Crop.of(uri, Uri.fromFile(profilePath)).withMaxSize(360,360).asSquare().start(this);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON CLICK/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.user_profile_iv)
        {

            if(!ImageSaver.isExternalStorageWritable() || !ImageSaver.isExternalStorageReadable())
            {
                Snackbar.make(view,"Storage read and/or write is disabled.",Snackbar.LENGTH_SHORT).show();
                return;
            }

            final CharSequence[] chooseFrom = {"Take Photo","Choose From Library","cancel"};

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

            builder.setTitle("Choose An Image!");

            if(ImageSaver.isExternalStorageWritable())
            {
                profilePath = new ImageSaver(Profile.this)
                        .setExternal(true)
                        .setDirectoryName("iNTCON")
                        .setFileName(System.currentTimeMillis() + ".jpg").createFile();
            }
            else
            {
                profilePath = new ImageSaver(Profile.this)
                        .setExternal(true)
                        .setDirectoryName("iNTCON")
                        .setFileName(System.currentTimeMillis() + ".jpg").createFile();
            }

            builder.setItems(chooseFrom, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (chooseFrom[which].equals("Take Photo"))
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file = new File(getExternalCacheDir(),String.valueOf(System.currentTimeMillis()) + ".jpg");
                        uri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                        startActivityForResult(intent, GlobalNames.getNoticeSendToCamera());

                    }
                    else if(chooseFrom[which].equals("Choose From Library"))
                    {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,GlobalNames.getNoticeSendToGal());

                    } else
                    {
                        dialog.dismiss();
                    }
                }
            });

            builder.show();

        }
        else if(view.getId() == R.id.user_name_edit_btn)
        {

            View v = getLayoutInflater().inflate(R.layout.edit_text_layout,null);
            final EditText input = (EditText)v.findViewById(R.id.et);

            input.setText(user_name_tv.getText().toString());
            input.setSelection(input.getText().length());

            final AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
            builder.setView(v);
            builder.setTitle("Choose Username");
            builder.setMessage("Set Username for your Account");
            builder.setCancelable(false);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if(input.getText().toString().isEmpty())
                    {
                        Toast.makeText(Profile.this, "Username cannot be Empty", Toast.LENGTH_SHORT).show();
                        builder.show();
                        return;
                    }

                    user_name_tv.setText(input.getText().toString());
                    userName = input.getText().toString();
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    userName = input.getText().toString();
                }
            });

            builder.show();
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////GET USER GROUP LIST/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUserGroupList(){

        dialogHandler = new DialogHandler(Profile.this,false);

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserGroupList())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            Iterator iterator = dataSnapshot.getChildren().iterator();

                            while (iterator.hasNext())
                            {
                                user_key_list.add(((DataSnapshot)iterator.next()).getKey());
                            }
                        }

                        getRequestedGroupList();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Profile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////GET REQUESTED GROUP LIST/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getRequestedGroupList(){

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getMyGroupRequests())
                .child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Iterator iterator = dataSnapshot.getChildren().iterator();

                    while (iterator.hasNext())
                    {
                        user_requests_key_list.add(((DataSnapshot)iterator.next()).getValue(String.class));
                    }
                }

                setUserProfile();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////GET USER PROFILE/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUserProfile(){

        dialogHandler = new DialogHandler(this,false);

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserProfile())
                .child(uid).child(GlobalNames.getUserName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    user_name_tv.setText(dataSnapshot.getValue(String.class));
                    userName = dataSnapshot.getValue(String.class);
                }
                else
                {
                    userName = user_name_tv.getText().toString();
                }

                dialogHandler.sendEmptyMessage(0);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                dialogHandler.sendEmptyMessage(0);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////SET USER PROFILE/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setUserProfile(){

        byte[] data;

        userProfile.buildDrawingCache();
        bitmap = userProfile.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50 , baos);
        data = baos.toByteArray();

        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://message-mi.appspot.com/")
                .child(GlobalNames.getUserProfile())
                .child(uid)
                .child(System.currentTimeMillis() + ".jpg");

        final UploadTask task = firebaseStorage.putBytes(data);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadToDatabase(taskSnapshot.getDownloadUrl().toString());
                task.removeOnSuccessListener(this);

                new ImageSaver(Profile.this)
                        .setExternal(false)
                        .setDirectoryName("iNTCON")
                        .setFileName(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .save(bitmap);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(AllPreferences.getUserProfile(),taskSnapshot.getDownloadUrl().toString());
                editor.putString(AllPreferences.getUSERNAME(),user_name_tv.getText().toString());
                editor.apply();
                editor.clear();

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                task.removeOnFailureListener(this);
                Snackbar.make(getWindow().getDecorView().getRootView(),e.getMessage(),Snackbar.LENGTH_SHORT).show();
                dialogHandler.sendEmptyMessage(0);

            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////UPLOAD DATA TO DATABASE/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadToDatabase(final String download_path)
    {

        UserProfile userProfile = new UserProfile();
        userProfile.setUserName(userName);
        userProfile.setUserProfile(download_path);
        userProfile.setUserName_lower(userName.replace(" ","").toLowerCase());
        userProfile.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(user_key_list != null && user_key_list.size() != 0)
        {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .getRoot().child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getGroupMemberList());

            Map<String, Object> map = new HashMap<>();

            for(String key : user_key_list)
            {
                map.put(key + "/" + uid ,userProfile);
            }

            reference.updateChildren(map);
        }

        if(user_requests_key_list != null && user_requests_key_list.size() != 0)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .getRoot().child(GlobalNames.getUserGroupDetail())
                    .child(GlobalNames.getUserRequests());

            Map<String, Object> map = new HashMap<>();

            for(String key : user_requests_key_list)
            {
                map.put(key + "/" + uid + "/" + GlobalNames.getUserProfile(),userProfile.getUserProfile());
                map.put(key + "/" + uid + "/" + GlobalNames.getUserName(),userProfile.getUserName());
            }

            reference.updateChildren(map);

        }

        FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserProfile())
                .child(uid).setValue(userProfile);

        if(sp.getString(AllPreferences.getUserProfile(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
        {
            dialogHandler.sendEmptyMessage(0);
            startActivity(new Intent(Profile.this, DashBoard.class));
            finish();
        }
        else
        {
            dialogHandler.sendEmptyMessage(0);
            finish();
        }
    }
}
