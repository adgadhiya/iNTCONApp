package com.myapp.unknown.iNTCON.Campaign;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
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

public class AddCampaign extends AppCompatActivity implements View.OnClickListener {

    private EditText accommodation,content,email,fees,phone,prize,lastDate,site,title,venue,eventDate;
    private ImageView imageView;
    private Button remove;
    private RelativeLayout choose;

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

        setContentView(R.layout.activity_add_campaign);

        accommodation = (EditText) findViewById(R.id.add_campaign_accommodation);
        content = (EditText) findViewById(R.id.add_campaign_message);
        email = (EditText) findViewById(R.id.add_campaign_email);
        fees = (EditText) findViewById(R.id.add_campaign_fee);
        phone = (EditText) findViewById(R.id.add_campaign_mobile);
        prize = (EditText) findViewById(R.id.add_campaign_prize);
        lastDate = (EditText) findViewById(R.id.add_campaign_last_date);
        site = (EditText) findViewById(R.id.add_campaign_site);
        title  = (EditText) findViewById(R.id.add_campaign_title);
        venue = (EditText) findViewById(R.id.add_campaign_venue);
        eventDate = (EditText) findViewById(R.id.add__campaign_date_time_message);

        remove = (Button) findViewById(R.id.add_camp_remove_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addCampaign);

        imageView = (ImageView)findViewById(R.id.add_campaign_iv);
        Button send = (Button) findViewById(R.id.add_campaign_send);
        choose = (RelativeLayout) findViewById(R.id.add_campaign_choose);

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        choose.setOnClickListener(this);
        send.setOnClickListener(this);
        remove.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkingConnextion = new NetworkConnectionCheck(this);

        remove.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus)
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(remove.getWindowToken(),0);
                }

            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON START//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////ON STOP/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        MyApplication.activityStopped();
        super.onStop();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////ON OPTION ITEM SELECTED/////////////////////////////////////////////////////////
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
    //////ON BACK PRESSED///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////HANDLE BUTTON CLICK EVENTS//////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.add_campaign_choose :
                pickOrTakePicture();
                site.clearFocus();
                choose.requestFocus();
                break;

            case R.id.add_campaign_send :
                checkCAMPAuth(v);
                break;

            case R.id.add_camp_remove_photo:
                bitmap = null;
                imageView.setImageBitmap(null);
                break;
        }
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

        long size = 0;
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream is = null;

        if(resultCode == RESULT_OK && requestCode == GlobalNames.getCampSendToGal())
        {
            if(data == null)
            {
                Toast.makeText(AddCampaign.this, "No Any Image Found", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AddCampaign.this, "Image Size should be less then 5MB", Toast.LENGTH_SHORT).show();
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
    /////////METHOD CALLED WHEN CHOOSE RESOURCE CALLED///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void pickOrTakePicture(){

        final CharSequence[] chooseFrom = {"Take Photo","Choose From Library","cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddCampaign.this);

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
                    startActivityForResult(intent, GlobalNames.getCampSendToCamera());

                } else if(chooseFrom[which].equals("Choose From Library"))
                {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,GlobalNames.getCampSendToGal());

                } else
                {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////CHECK USER IS ADMIN OR NOT//////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkCAMPAuth(final View v)
    {
        if(checkingConnextion.checkingConnextion())
        {
            Toast.makeText(AddCampaign.this, "Network Connection Error!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(title.getText().toString().isEmpty() ||
                content.getText().toString().isEmpty() ||
                eventDate.getText().toString().isEmpty() ||
                fees.getText().toString().isEmpty() ||
                prize.getText().toString().isEmpty() ||
                accommodation.getText().toString().isEmpty() ||
                lastDate.getText().toString().isEmpty() ||
                venue.getText().toString().isEmpty() ||
                phone.getText().toString().isEmpty() ||
                email.getText().toString().isEmpty() ||
                site.getText().toString().isEmpty())
        {
            Snackbar.make(v,"All fields are required",Snackbar.LENGTH_LONG).show();
            return;
        }


        if(bitmap == null)
        {
            Snackbar.make(v,"You haven't choose any image for Event.",Snackbar.LENGTH_LONG).show();
            return;
        }

        dialogHandler = new DialogHandler(this,false);

        DatabaseReference campAuth = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        campAuth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    uploadImageToStorage(bitmap);
                }
                else
                {
                    dialogHandler.sendEmptyMessage(0);
                    Snackbar.make(v,"User is unauthorized. This action will be recorded.",Snackbar.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().getRoot()
                            .child(GlobalNames.getUnauthorisedUser())
                            .push()
                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()
                            + sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()) + " EVENTS" );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////UPLOAD IMAGE TO STORAGE/////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadImageToStorage(Bitmap bitmap){

        if(bitmap != null)
        {
            final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault());

            Random random = new Random();

            String s = format.format(new Date());
            s = s + String.valueOf(random.nextInt(999)) + uri.getLastPathSegment();

            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://message-mi.appspot.com/")
                    .child(GlobalNames.getCAMPAIGN())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())).child(s);

            byte[] data;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            data = baos.toByteArray();

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
                    Toast.makeText(AddCampaign.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialogHandler.sendEmptyMessage(0);
                    task.removeOnFailureListener(this);
                }
            });
        } else {
            img_path = GlobalNames.getNONE();
            uploadDataToDatabase();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////UPLOAD DATA TO DATABASE//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadDataToDatabase(){

        Map<String,Object> map = new HashMap<>();

        DatabaseReference campaign_msg =
                FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getCAMPAIGN())
                .child(GlobalNames.getCampaignAll());

        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy",Locale.getDefault());

        CampaignDetailProvider listProvider = new CampaignDetailProvider();
        KeysAndGroupKeys keysAndGroupKeys = new KeysAndGroupKeys();

        listProvider.setImg_path(img_path);
        listProvider.setTitle(title.getText().toString());
        listProvider.setGroup_key(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));
        listProvider.setContent(content.getText().toString());
        listProvider.setDate(format.format(new Date()));
        listProvider.setFees(fees.getText().toString());
        listProvider.setPrize(prize.getText().toString());
        listProvider.setAccommodation(accommodation.getText().toString());
        listProvider.setRegistration_last_date(lastDate.getText().toString());
        listProvider.setVenue(venue.getText().toString());
        listProvider.setPhone(phone.getText().toString());
        listProvider.setEmail(email.getText().toString());
        listProvider.setSite(site.getText().toString());
        listProvider.setEventDate(eventDate.getText().toString());
        listProvider.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listProvider.setGroup_type(sp.getInt(AllPreferences.getGroupType(),0));

        String key = campaign_msg.push().getKey();

        listProvider.setKey(key);

        keysAndGroupKeys.setKey(key);
        keysAndGroupKeys.setGroup_key(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        map.put(GlobalNames.getCAMPAIGN()
                + "/" + GlobalNames.getCampaignAll()
                + "/" + key, listProvider);

        map.put(GlobalNames.getCAMPAIGN()
                + "/" + GlobalNames.getMyCampaign()
                + "/" + sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())
                + "/" + key, keysAndGroupKeys);

        map.put(GlobalNames.getCAMPAIGN()
                + "/" + GlobalNames.getGroupCampaign()
                + "/" + String.valueOf(sp.getInt(AllPreferences.getGroupType(),0))
                + "/" + key, keysAndGroupKeys);

        FirebaseDatabase.getInstance().getReference().getRoot().updateChildren(map);

        dialogHandler.sendEmptyMessage(0);
        Toast.makeText(AddCampaign.this, "Event Uploaded", Toast.LENGTH_SHORT).show();
        finish();

    }

}