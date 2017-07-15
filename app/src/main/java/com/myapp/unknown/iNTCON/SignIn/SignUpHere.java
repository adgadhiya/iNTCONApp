package com.myapp.unknown.iNTCON.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.NavigationView.PrivacyPolicy;
import com.myapp.unknown.iNTCON.R;

/**
 * Created by UNKNOWN on 2/2/2017.
 */
public class SignUpHere extends AppCompatActivity implements View.OnClickListener {

    private EditText register_email_et,register_pass_et;

    private DialogHandler dialogHandler;

    ///////////////////////////////////////////////////////////////////////////
    ///////////ON CREATE ACTIVITY////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                ,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up_here);

        register_email_et = (EditText) findViewById(R.id.register_email_et);
        register_pass_et = (EditText) findViewById(R.id.register_pass_et);

        Button register = (Button) findViewById(R.id.register_btn);
        Button alreadySignedIn = (Button) findViewById(R.id.already_signed_in);

        TextView privacyPolicy = (TextView) findViewById(R.id.privacy_policy_tv);

        CheckBox checkBox = (CheckBox) findViewById(R.id.show_password);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    register_pass_et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
                else{
                    register_pass_et.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        String already_account = getColoredSpanned("Already have an account?","#aaaaaa");
        String loginHere = getColoredSpanned("Signin Here","#415dae");

        alreadySignedIn.setText(Html.fromHtml(already_account + " " + loginHere));

        alreadySignedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register.setOnClickListener(this);

        String byClicking = getColoredSpanned("By Clicking signup you agree to our ","#aaaaaa");
        String terms = getColoredSpanned("Terms of Use ","#000000");
        String and = getColoredSpanned("and ","#aaaaaa");
        String privacy = getColoredSpanned("Privacy Policy.","#000000");

        privacyPolicy.setText(Html.fromHtml(byClicking + terms + and + privacy));

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpHere.this, PrivacyPolicy.class));
            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////ON START////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////ON STOP/////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.activityStopped();
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////DIFFERENT TEXT COLOR FOR SAME TEXT VIEW///////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private String getColoredSpanned(String text, String color)
    {
        return  "<font color=" + color + ">" + text + "</font>";
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////ON SIGN UP BUTTON CLICK//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(final View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);

        if(register_email_et.getText().toString().trim().length() == 0 |
                register_pass_et.getText().toString().trim().length() == 0 )
        {

            Snackbar.make(v, "Empty Email or Password!", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        dialogHandler = new DialogHandler(this,false);

        completeCreateUser(v);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////COMPLETE CREATE USER//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void completeCreateUser(final View v){

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        final String email = register_email_et.getText().toString();
        String pass = register_pass_et.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            verifyEmailAddress(task.getResult());
                            register_email_et.setText("");
                            register_pass_et.setText("");

                        } else {

                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG)
                                            .show(); }
                            });

                            dialogHandler.sendEmptyMessage(0);
                        }

                    }
                });
    }


    private void verifyEmailAddress(final AuthResult authResult)
    {

        authResult.getUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpHere.this);
                builder.setTitle("Verify Your Account.");
                builder.setMessage("Verification link has been sent on " + authResult.getUser().getEmail());
                builder.setCancelable(false);
                builder.setPositiveButton("OK",null);

                dialogHandler.sendEmptyMessage(0);
                builder.show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(getWindow().getDecorView().getRootView(),
                        "Some error occurred. Please try again.",Snackbar.LENGTH_LONG).show();

                dialogHandler.sendEmptyMessage(0);
            }
        });



    }


}
