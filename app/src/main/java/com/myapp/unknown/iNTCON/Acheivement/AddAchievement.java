package com.myapp.unknown.iNTCON.Acheivement;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.myapp.unknown.iNTCON.Campaign.KeysAndGroupKeys;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
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
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AddAchievement extends AppCompatActivity implements View.OnClickListener {

    private EditText title,message;
    private ImageView imageView;

    private String img_path = null;

    private NetworkConnectionCheck checkingConnextion;

    private DialogHandler dialogHandler;

    private Uri uri = null;

    private File file;

    private Bitmap bitmap = null;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_add_achievement);

        title = (EditText) findViewById(R.id.add_achievement_title);
        message = (EditText) findViewById(R.id.add_achievement_message);
        RelativeLayout choose = (RelativeLayout) findViewById(R.id.add_achievement_choose);
        Button send = (Button) findViewById(R.id.add_achievement_send);
        imageView = (ImageView) findViewById(R.id.add_achievement_iv);
        Button remove_photo_btn = (Button) findViewById(R.id.add_ach_remove_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addActivity);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        choose.setOnClickListener(this);
        send.setOnClickListener(this);

        checkingConnextion = new NetworkConnectionCheck(this);

        remove_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = null;
                imageView.setImageBitmap(null);

            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////ON START CALLED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////ON STOP CALLED////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        MyApplication.activityStopped();
        super.onStop();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////ON OPTION ITEM SELECTED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////ON BACKPRESSED////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        finish();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////HANDLE BUTTON CLICKED EVENT///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.add_achievement_choose :
                pickOrTakePicture();
                break;

            case R.id.add_achievement_send :
                checkACHAuth(v);
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON ACTIVITY FOR RESULT////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED)
        {
            return;
        }

        long size = 0;
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream is = null;

        if(resultCode == RESULT_OK && requestCode == GlobalNames.getAchSendToGal())
        {
            if(data == null)
            {
                Toast.makeText(AddAchievement.this, "No Any Image Found", Toast.LENGTH_SHORT).show();
                return;
            }
            uri = data.getData();

        }

        try {
            is = getContentResolver().openInputStream(uri);
            size = is.available()/1024;

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (size > 5120) {
            Toast.makeText(AddAchievement.this, "Image Size should be less then 5MB", Toast.LENGTH_SHORT).show();
            return;
        } else if (size > 3072) {
            options.inSampleSize = 8;
        } else if (size > 1024) {
            options.inSampleSize = 4;
        } else {
            options.inSampleSize = 2;
        }

        bitmap = BitmapFactory.decodeStream(is,null,options);

        if(bitmap != null )
        {
            imageView.setImageBitmap(bitmap);
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////OPTION ON CHOOSE IMAGE CLICKED//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void pickOrTakePicture(){

        final CharSequence[] chooseFrom = {"Take Photo","Choose From Library","cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddAchievement.this);

        builder.setTitle("Choose An Image!");

        builder.setItems(chooseFrom, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (chooseFrom[which].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = new File(getExternalCacheDir(),String.valueOf(System.currentTimeMillis()) + ".jpg");
                    uri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                    startActivityForResult(intent, GlobalNames.getAchSendToCamera());

                } else if(chooseFrom[which].equals("Choose From Library"))
                {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,GlobalNames.getAchSendToGal());

                } else
                {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////CHECK USER IS ADMIN OR NOT//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkACHAuth(final View v)
    {
        if(checkingConnextion.checkingConnextion())
        {
            Snackbar.make(v,"Network Connection Error!",Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(title.getText().toString().isEmpty())
        {
            Snackbar.make(v,"Title is Empty",Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(message.getText().toString().isEmpty())
        {
            Snackbar.make(v,"Description is Empty",Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(bitmap == null)
        {
            Snackbar.make(v,"Please add Image with your Achievement.",Snackbar.LENGTH_SHORT).show();
            return;
        }

        dialogHandler = new DialogHandler(this,false);

        DatabaseReference achAuth = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        achAuth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class))
                {
                    uploadImageToStorage(bitmap);
                }
                else
                {
                    Snackbar.make(v,"User is unauthorized. This action will be recorded.",Snackbar.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().getRoot()
                            .child(GlobalNames.getUnauthorisedUser())
                            .push()
                            .setValue(sp.getString(AllPreferences.getUID(),GlobalNames.getNONE())
                                    +  sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())
                                    + " Achievement");

                    dialogHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialogHandler.sendEmptyMessage(0);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////UPLOAD IMAGE TO STORAGE//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadImageToStorage(Bitmap bitmap){

        if(bitmap != null)
        {

            final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss",Locale.getDefault());

            Random random = new Random();
            String s = format.format(new Date());
            s = s + String.valueOf(random.nextInt(999)) + uri.getLastPathSegment();

            byte[] data;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100 , baos);
            data = baos.toByteArray();

            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://message-mi.appspot.com/")
                    .child(GlobalNames.getACHIEVEMENT())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())).child(s);

            final UploadTask task = firebaseStorage.putBytes(data);

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    img_path = taskSnapshot.getDownloadUrl().toString();
                    task.removeOnSuccessListener(this);
                    uploadDataToDatabase();
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddAchievement.this,e.getMessage() , Toast.LENGTH_SHORT).show();
                    dialogHandler.sendEmptyMessage(0);
                    task.removeOnFailureListener(this);
                }
            });
        } else {
            img_path = "NONE";
            uploadDataToDatabase();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////UPLOAD DATA TO DATABASE////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadDataToDatabase(){

        Map<String,Object> map = new HashMap<>();

        DatabaseReference ach_msg = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getACHIEVEMENT())
                .child(GlobalNames.getAchievementAll());

        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

        AchievementListProvider achievementListProvider = new AchievementListProvider();
        KeysAndGroupKeys keysAndGroupKeys = new KeysAndGroupKeys();

        achievementListProvider.setImagepath(img_path);
        achievementListProvider.setContent(message.getText().toString());
        achievementListProvider.setTitle(title.getText().toString());
        achievementListProvider.setDate(format.format(new Date()));
        achievementListProvider.setLikes(0);
        achievementListProvider.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        achievementListProvider.setGroup_type(sp.getInt(AllPreferences.getGroupType(),0));

        achievementListProvider.setGroup_key(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        String key = ach_msg.push().getKey();

        achievementListProvider.setKey(key);

        keysAndGroupKeys.setGroup_key(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));
        keysAndGroupKeys.setKey(key);

        map.put(GlobalNames.getACHIEVEMENT()
                + "/" + GlobalNames.getAchievementAll()
                + "/" + key,achievementListProvider);


        map.put(GlobalNames.getACHIEVEMENT()
                + "/" + GlobalNames.getMyAchievement()
                + "/" + sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())
                + "/" + key, keysAndGroupKeys);

        map.put(GlobalNames.getACHIEVEMENT()
                + "/" + GlobalNames.getGroupAchievement()
                + "/" + String.valueOf(sp.getInt(AllPreferences.getGroupType(),0))
                + "/" + key, keysAndGroupKeys);

        FirebaseDatabase.getInstance().getReference().getRoot().updateChildren(map);

        dialogHandler.sendEmptyMessage(0);
        Toast.makeText(AddAchievement.this, "Achievement Shared", Toast.LENGTH_SHORT).show();
        finish();
    }

}

