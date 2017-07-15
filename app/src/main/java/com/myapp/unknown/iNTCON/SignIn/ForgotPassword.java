package com.myapp.unknown.iNTCON.SignIn;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText email_et;
    private Button reset;

    private DialogHandler dialogHandler;

    ///////////////////////////////////////////////////////////////////////////
    ///////////ON CREATE ACTIVITY//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        reset = (Button) findViewById(R.id.reset_btn);
        email_et = (EditText) findViewById(R.id.reset_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_forgot_pass);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                FirebaseAuth auth = FirebaseAuth.getInstance();

                email_et.clearFocus();
                reset.requestFocus();


                if(email_et.getText().toString().trim().length() == 0) {
                    Snackbar.make(v,"Empty Email Address",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                dialogHandler = new DialogHandler(ForgotPassword.this,true);

                auth.sendPasswordResetEmail(email_et.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialogHandler.sendEmptyMessage(0);
                                Snackbar.make(v,"Check Your Email. We Sent You A link to reset your password.",Snackbar.LENGTH_SHORT).show();
                                email_et.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogHandler.sendEmptyMessage(0);
                                Snackbar.make(v,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////ON OPTION ITEM SELECTED//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
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

}
