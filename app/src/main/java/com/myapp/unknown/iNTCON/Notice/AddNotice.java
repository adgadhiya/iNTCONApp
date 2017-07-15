package com.myapp.unknown.iNTCON.Notice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.unknown.iNTCON.InstRequest.FieldDetail;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class AddNotice extends AppCompatActivity implements View.OnClickListener {

    private EditText title,message;
    private Spinner departmentSpinner, yearSpinner;
    private ImageView imageView;
    private TextView textView;

    private CardView attachment_cardView;

    private TextView attach_gallery,attach_camera,attach_doc;

    private ArrayList<CharSequence> departmentArrayList;
    private ArrayList<CharSequence> departmentKeyList;

    private ArrayAdapter<CharSequence> departmentAdapter;

    private DatabaseReference root;

    private boolean isAttachShown = false;

    private String img_path = null;

    private NetworkConnectionCheck checkingConnextion;

    private DialogHandler dialogHandler;

    private Uri uri = null;

    private boolean isFile = false;

    private Bitmap bitmap = null;

    private SharedPreferences sp;

    private int groupType;

    private String selected_year;
    private String selected_department,selected_department_key;

    ///////////////////////////////////////////////////////////////////////////
    ///////////OPTION CREATE//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_add_notice);

        title = (EditText) findViewById(R.id.add_notice_title);
        message = (EditText) findViewById(R.id.add_notice_message);
        departmentSpinner = (Spinner) findViewById(R.id.add_notice_department);
        yearSpinner = (Spinner) findViewById(R.id.add_notice_year);
        RelativeLayout choose = (RelativeLayout) findViewById(R.id.add_notice_choose);
        Button send = (Button) findViewById(R.id.add_notice_send);
        imageView = (ImageView) findViewById(R.id.add_notice_iv);
        Button remove_photo = (Button) findViewById(R.id.add_notice_remove_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addNotice);
        textView = (TextView)findViewById(R.id.attach_tv);

        attachment_cardView = (CardView)findViewById(R.id.card_attachment);

        attach_camera = (TextView) findViewById(R.id.attach_camera);
        attach_gallery = (TextView) findViewById(R.id.attach_gallery);
        attach_doc = (TextView) findViewById(R.id.attach_docs);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        groupType = sp.getInt(AllPreferences.getGroupType(),0);

        root = FirebaseDatabase.getInstance().getReference().getRoot();

        departmentArrayList = new ArrayList<>();
        departmentKeyList = new ArrayList<>();


        if(groupType == GlobalNames.getUniversityOrCollege())
        {
            departmentSpinner.setVisibility(View.VISIBLE);
            getFieldName();

            ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_array_no_none, android.R.layout.simple_spinner_dropdown_item);
            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearSpinner.setAdapter(yearAdapter);

        }
        else if(groupType == GlobalNames.getPrivateInstituteClg())
        {
            departmentSpinner.setVisibility(View.VISIBLE);
            getFieldName();
        }
        else if(groupType == GlobalNames.getPrivateInstituteSchool() || groupType == GlobalNames.getSCHOOL())
        {
            departmentSpinner.setVisibility(View.VISIBLE);

            CharSequence[] departments = getResources().getStringArray(R.array.school_year_array);

            departmentArrayList.add(0,"Institute");

            Collections.addAll(departmentArrayList,departments);
            departmentArrayList.remove(1);

            departmentAdapter =
                    new ArrayAdapter<>(
                            AddNotice.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            departmentArrayList);

            departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            departmentSpinner.setAdapter(departmentAdapter);

        }

        remove_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(getResources().getString(R.string.optional_image));
                imageView.setImageBitmap(null);
                isFile = false;
                bitmap = null;
                uri = null;
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_year = (String) yearSpinner.getAdapter().getItem(position);
                selected_year = selected_year.replace(" ","");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_department = (String) departmentArrayList.get(position);

                if(position == 0)
                {
                    yearSpinner.setVisibility(View.GONE);
                }
                else if(groupType == GlobalNames.getUniversityOrCollege())
                {
                    yearSpinner.setVisibility(View.VISIBLE);
                }

                if(groupType == GlobalNames.getPrivateInstituteSchool() || groupType == GlobalNames.getSCHOOL())
                {
                    selected_department_key = selected_department.replace(" ","");
                }
                else
                {
                    selected_department_key = (String) departmentKeyList.get(position);
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(departmentSpinner.getWindowToken(),0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        choose.setOnClickListener(this);
        send.setOnClickListener(this);
        attach_gallery.setOnClickListener(this);
        attach_doc.setOnClickListener(this);
        attach_camera.setOnClickListener(this);

        checkingConnextion = new NetworkConnectionCheck(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON START////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON STOP////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        MyApplication.activityStopped();
        super.onStop();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////ON OPTION ITEM SELECTING////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
            return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN BACK PRESSED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

        if(isAttachShown){

            exitAnimation();
            return;
        }

        super.onBackPressed();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON ANY BUTTON CLICK////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()){

            case R.id.add_notice_choose :

                if(isAttachShown){
                    return;
                }

                enterAnimation();
                break;

            case R.id.add_notice_send :
                checkingAuth(v);
                break;

            case R.id.attach_gallery :
                exitAnimation();
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GlobalNames.getNoticeSendToGal());
                isAttachShown = false;
                break;

            case R.id.attach_camera:
                exitAnimation();
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(getExternalCacheDir(), String.valueOf(System.currentTimeMillis()) + ".jpg");
                uri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(intent,GlobalNames.getNoticeSendToCamera());
                isAttachShown = false;
                break;

            case R.id.attach_docs:
                exitAnimation();
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/msword,application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,8989);
                isAttachShown = false;
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

        long size;
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream is;

        if(resultCode == RESULT_OK && requestCode == GlobalNames.getNoticeSendToGal())
        {
            if(data == null)
            {
                Toast.makeText(AddNotice.this, "No Any Image Found", Toast.LENGTH_SHORT).show();
                return;
            }

            isFile = false;
            textView.setText("");
            uri = data.getData();
        }
        else if(resultCode == RESULT_OK && requestCode == GlobalNames.getNoticeSendToCamera())
        {
            isFile = false;
            textView.setText("");
        }
        else if(resultCode == RESULT_OK && requestCode == 8989)
        {
            textView.setText(data.getData().getPath());
            uri = data.getData();
            isFile = true;
            return;
        }

        try {
            is = getContentResolver().openInputStream(uri);
           size = is.available()/1024;

        } catch (IOException e) {
            Toast.makeText(AddNotice.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (size > 5120) {
            Toast.makeText(AddNotice.this, "Image Size should be less then 5MB", Toast.LENGTH_SHORT).show();
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
    ////////////////////////////////////////////GET FIELD NAME/////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getFieldName(){

        final DatabaseReference clg_field_name =
                root.child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getCoursesOffered())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        clg_field_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator iterator = dataSnapshot.getChildren().iterator();

                FieldDetail fieldDetail;

                while (iterator.hasNext())
                {
                    fieldDetail = ((DataSnapshot)iterator.next()).getValue(FieldDetail.class);
                    departmentArrayList.add(fieldDetail.getFieldName());
                    departmentKeyList.add(fieldDetail.getKey());
                }

                departmentArrayList.add(0,"Institute");
                departmentKeyList.add(0,"Institute");

                departmentAdapter =
                        new ArrayAdapter<>(
                                AddNotice.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                departmentArrayList);

                departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                departmentSpinner.setAdapter(departmentAdapter);
                clg_field_name.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////ADD NOTICE////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addNotice(Bitmap bitmap){

        if(bitmap != null)
        {
            uploadImage();
        }
        else if(isFile)
        {
            uploadDocument();
        }
        else
        {
            img_path = GlobalNames.getNONE();
            uploadDataToDatabase();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////CHECK AUTHENTICATED USER OR NOT////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkingAuth(final View v){

        if(checkingConnextion.checkingConnextion())
        {
            Toast.makeText(AddNotice.this, "Network Connection Error!", Toast.LENGTH_SHORT).show();
            return;
        }

        dialogHandler = new DialogHandler(this,false);

        final DatabaseReference isAuth = root.child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        isAuth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.getValue(Boolean.class))
                    {
                        addNotice(bitmap);
                    }
                    else
                    {
                        Snackbar.make(v,"User is unauthorized. This action will be recorded.",Snackbar.LENGTH_SHORT).show();
                        root.child(GlobalNames.getUnauthorisedUser()).push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid() + " Notice");
                        dialogHandler.sendEmptyMessage(0);
                    }

                }
                else
                {
                    Snackbar.make(v,"User is unauthorized. This action will be recorded.",Snackbar.LENGTH_SHORT).show();
                    root.child(GlobalNames.getUnauthorisedUser()).push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid() + " Notice");
                    dialogHandler.sendEmptyMessage(0);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////UPLOAD DATA TO DATABASE////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadDataToDatabase(){

        DatabaseReference notice_msg;

        if(groupType == GlobalNames.getPrivateGroup())
        {

            notice_msg = root.child(GlobalNames.getNOTICE())
                    .child(GlobalNames.getNoticeMsg())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .child(GlobalNames.getDummyNode())
                    .child(GlobalNames.getDummyNode());

            selected_department_key = GlobalNames.getDummyNode();
            selected_year = GlobalNames.getDummyNode();

        }
        else
        {
            if(selected_department.contains(GlobalNames.getInstitute()))
            {
                notice_msg = root.child(GlobalNames.getNOTICE())
                        .child(GlobalNames.getNoticeMsg())
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(GlobalNames.getInstitute());

                selected_department_key = GlobalNames.getInstitute();
                selected_year = GlobalNames.getDummyNode();

            }
            else if(groupType == GlobalNames.getUniversityOrCollege())
            {
                notice_msg = root.child(GlobalNames.getNOTICE())
                        .child(GlobalNames.getNoticeMsg())
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(selected_department_key)
                        .child(selected_year);
            }
            else if(groupType == GlobalNames.getPrivateInstituteClg())
            {
                notice_msg = root.child(GlobalNames.getNOTICE())
                        .child(GlobalNames.getNoticeMsg())
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(selected_department_key)
                        .child(GlobalNames.getDummyNode());

                selected_year = GlobalNames.getDummyNode();

            }
            else if(groupType == GlobalNames.getSCHOOL() || groupType == GlobalNames.getPrivateInstituteSchool())
            {
                notice_msg = root.child(GlobalNames.getNOTICE())
                        .child(GlobalNames.getNoticeMsg())
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(GlobalNames.getDummyNode())
                        .child(selected_department_key);

                selected_year = selected_department_key;
                selected_department_key = GlobalNames.getDummyNode();
            }
            else
            {
                notice_msg = root.child(GlobalNames.getNONE());
            }

        }

        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy",Locale.getDefault());

        NoticeListProvider noticeListProvider = new NoticeListProvider();

        noticeListProvider.setImg_path(img_path);
        noticeListProvider.setDepartment(sp.getString(AllPreferences.getFullFieldNamePreference(),"NONE"));
        noticeListProvider.setMessage(message.getText().toString());
        noticeListProvider.setTitle(title.getText().toString());
        noticeListProvider.setDate(format.format(new Date()));
        noticeListProvider.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

        format = new SimpleDateFormat("hh:mm aa",Locale.getDefault());
        noticeListProvider.setTime(format.format(new Date()));

        format = new SimpleDateFormat("d", Locale.getDefault());
        noticeListProvider.setNumber(Integer.parseInt(format.format(new Date())));

        String key = notice_msg.push().getKey();

        noticeListProvider.setKey(key);

        notice_msg.child(key).setValue(noticeListProvider);

        addToNoticeByMe(key);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////ADD TO DATABASE NOTICE BY ME////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addToNoticeByMe(String key){

        DatabaseReference my_notice = root.child(GlobalNames.getNOTICE())
                .child(GlobalNames.getMyNotice())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        My_Notice myNotice = new My_Notice();

        myNotice.setKey(key);
        myNotice.setField_key(selected_department_key);
        myNotice.setYear(selected_year);
        myNotice.setGroup_key(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        my_notice.child(key).setValue(myNotice);

        dialogHandler.sendEmptyMessage(0);
        Toast.makeText(AddNotice.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();

        finish();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ANIMATION ON ENTER////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void enterAnimation(){

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.enter_animation);

        attachment_cardView.startAnimation(animation);

        isAttachShown = true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ANIMATION ON EXIT////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void exitAnimation(){

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.exit_animation);
        attachment_cardView.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                attachment_cardView.clearAnimation();
                attach_camera.clearAnimation();
                attach_doc.clearAnimation();
                attach_gallery.clearAnimation();
                attachment_cardView.setVisibility(View.INVISIBLE);
            }
        },500);

        isAttachShown = false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////UPLOAD IMAGE////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadImage(){
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss",Locale.getDefault());

        Random random = new Random();
        String s = format.format(new Date());
        s = s + String.valueOf(random.nextInt(999));

        byte[] data;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        data = baos.toByteArray();

        final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl(GlobalNames.getStorageRefUrl())
                .child(GlobalNames.getNOTICE())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())).child(s + ".jpg");

        final UploadTask task = firebaseStorage.putBytes(data);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                img_path = firebaseStorage.getPath();
                task.removeOnSuccessListener(this);
                uploadDataToDatabase();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNotice.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dialogHandler.sendEmptyMessage(0);
                task.removeOnFailureListener(this);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////UPLOAD DOCUMENT////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadDocument(){

        File file = new File(uri.getPath());

        if(file.exists() && file.length() > 5 * 1024 * 1024){
            Toast.makeText(AddNotice.this, "File size should be less than 5MB.", Toast.LENGTH_SHORT).show();
            dialogHandler.sendEmptyMessage(0);
            return;
        }

        final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReferenceFromUrl(GlobalNames.getStorageRefUrl())
                .child(GlobalNames.getNOTICE())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .child(System.currentTimeMillis() + uri.getLastPathSegment());

        final UploadTask task = firebaseStorage.putFile(uri);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                img_path = firebaseStorage.getPath();
                uploadDataToDatabase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogHandler.sendEmptyMessage(0);
                Toast.makeText(AddNotice.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                task.removeOnFailureListener(this);
            }
        });

    }
}
