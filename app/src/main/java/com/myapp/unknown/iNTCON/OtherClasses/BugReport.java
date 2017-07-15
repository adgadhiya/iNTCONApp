package com.myapp.unknown.iNTCON.OtherClasses;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.unknown.iNTCON.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class BugReport extends AppCompatActivity implements View.OnClickListener {

    private File file;
    private Uri uri;
    private ImageView imageView;
    private Bitmap bitmap = null;
    private EditText detail;
    private EditText title;

    private DialogHandler dialogHandler;

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

        setContentView(R.layout.activity_bug_report);

        detail = (EditText) findViewById(R.id.bug_report_et);
        title = (EditText) findViewById(R.id.bug_report_title_et);
        TextView textView = (TextView) findViewById(R.id.bug_report_tv);

        Button attach = (Button) findViewById(R.id.bug_report_attach);
        Button send = (Button) findViewById(R.id.bug_report_btn);

        setSupportActionBar((Toolbar)findViewById(R.id.bug_report_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.bug_report_attach_iv);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"Roboto-Regular.ttf");
        textView.setTypeface(typeface);

        attach.setOnClickListener(this);
        send.setOnClickListener(this);
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////ON OPTIN ITEM SELECTED//////////////////////////////////////////
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
    ///////////ON BUTTON CLICKED//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.bug_report_attach)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(detail.getWindowToken(),0);
            pickOrTakePicture();
        }
        else if(view.getId() == R.id.bug_report_btn)
        {

            if(title.getText().toString().isEmpty() || detail.getText().toString().isEmpty())
            {
                Snackbar.make(view,"Both fields are required",Snackbar.LENGTH_SHORT).show();
                return;
            }

            dialogHandler = new DialogHandler(this,false);

            if(bitmap != null)
            {
                uploadToStorage();
            }
            else
            {
                sendToDatabase("NONE");
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////ON ACTIVITY RESULT//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        long size;
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream is;

        if(resultCode == RESULT_OK && requestCode == GlobalNames.getCampSendToGal()) {

            if(data == null) {
                return;
            }
            uri = data.getData();
        }

        try
        {
            is = getContentResolver().openInputStream(uri);
            size = is.available()/1024;
        } catch (IOException e) {
            Toast.makeText(BugReport.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (size > 8192)
        {
            Toast.makeText(BugReport.this, "Image Size should be less then 8MB", Toast.LENGTH_SHORT).show();
            return;
        } else if (size > 3072)
        {
            options.inSampleSize = 8;
        } else if (size > 1024)
        {
            options.inSampleSize = 4;
        } else
        {
            options.inSampleSize = 2;
        }

        bitmap = BitmapFactory.decodeStream(is,null,options);

        if(bitmap != null)
        {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////GET PICTURE IF ANY//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void pickOrTakePicture(){

        final CharSequence[] chooseFrom = {"Take Photo","Choose From Library","cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(BugReport.this);

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


    ///////////////////////////////////////////////////////////////////////////
    ///////////UPLOAD IMAGE TO STORAGE//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void uploadToStorage(){

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://message-mi.appspot.com/")
                .child("bug").child(System.currentTimeMillis() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] bytes = baos.toByteArray();

        final UploadTask task = storageReference.putBytes(bytes);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                sendToDatabase(taskSnapshot.getDownloadUrl().toString());
                task.removeOnSuccessListener(this);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BugReport.this);
                builder.setTitle("Error!");
                builder.setMessage("Some internal error occurred. Please try again");
                builder.setPositiveButton("OK",null);
                builder.show();
                task.removeOnFailureListener(this);
                dialogHandler.sendEmptyMessage(0);
            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////UPLOAD DATA TO DATABASE//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void sendToDatabase(String path){

        BugReportClass bugReportClass = new BugReportClass();

        bugReportClass.setTitle(title.getText().toString());
        bugReportClass.setDetail(detail.getText().toString());
        bugReportClass.setAttachment(path);
        bugReportClass.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child("bug").push().setValue(bugReportClass);

        AlertDialog.Builder builder = new AlertDialog.Builder(BugReport.this);
        builder.setTitle("Thank You");
        builder.setMessage("Thank you for submitting your feedback to Us. We will try to fix the problem as soon as possible.");
        builder.setPositiveButton("go back",null);
        builder.show();

        dialogHandler.sendEmptyMessage(0);

        detail.setText("");
        title.setText("");
        imageView.setVisibility(View.GONE);
    }
}
