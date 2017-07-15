package com.myapp.unknown.iNTCON.SignIn;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.DashBoard;
import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_signIn;

    private LoginButton fb_sign_in;
    private  CallbackManager callbackManager;

    private EditText et_signin_email,et_signin_pass;

    private TextView tv_email_empty,tv_pass_empty;

    private FirebaseAuth auth;

    private DialogHandler dialogHandler;

    private GoogleApiClient googleApiClient ;


    ///////////////////////////////////////////////////////////////////////////
    ///////////ON CRETE ACTIVITY//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                ,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        callbackManager = CallbackManager.Factory.create();
        DatabaseReference.goOnline();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {

            String providers = FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0);

            if((providers.equals(GlobalNames.getPASSWORD()) && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) ||
                    providers.equals(GlobalNames.getFacebookDotCom()) ||
                    providers.equals(GlobalNames.getGoogleDotCom()))
            {
                Intent intent = new Intent(MainActivity.this,DashBoard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .enableAutoManage(MainActivity.this,null)
                .build();

        Button btn_signup = (Button) findViewById(R.id.btn_signup);
        Button btn_forgot_pass = (Button) findViewById(R.id.btn_forgot_pass);
        btn_signIn = (Button) findViewById(R.id.btn_signin);

        ImageButton google_sign_in = (ImageButton) findViewById(R.id.sign_in_google);
        fb_sign_in = (LoginButton) findViewById(R.id.sign_in_facebook);

        et_signin_email = (EditText) findViewById(R.id.signin_email_et);
        et_signin_pass = (EditText) findViewById(R.id.signin_pass_et);

        tv_email_empty = (TextView) findViewById(R.id.tv_email_empty);
        tv_pass_empty = (TextView) findViewById(R.id.tv_pass_empty);

        String notMem = getColoredSpanned("Don't have an account?","#aaaaaa");
        String regiHere = getColoredSpanned("Signup Here","#415dae");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            btn_signup.setText(Html.fromHtml(notMem + " " + regiHere,Html.FROM_HTML_MODE_COMPACT));
        }
        else
        {
            btn_signup.setText(Html.fromHtml(notMem + " " + regiHere));
        }

        btn_signup.setOnClickListener(this);
        btn_signIn.setOnClickListener(this);
        btn_forgot_pass.setOnClickListener(this);

        google_sign_in.setOnClickListener(this);
        fb_sign_in.setOnClickListener(this);

        if(ImageSaver.isExternalStorageWritable())
        {
            File file = new File(Environment.getExternalStorageDirectory(),"iNTCON");
            file.mkdirs();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////ON START//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
        googleApiClient.connect();
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////NON STOP//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.activityStopped();
   }


    ///////////////////////////////////////////////////////////////////////////
    ///////////DIFFERENT TEXT COLOR FOR SAME TEXT VIEW//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private String getColoredSpanned(String text,String color)
    {
        return  "<font color=" + color + ">" + text + "</font>";
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////ON ACTIVITY FOR RESULT//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GlobalNames.getGoogleSigninActivityForResult())
        {
            if(resultCode == RESULT_OK)
            {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if(result.isSuccess())
                {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Not Successful" ,Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                dialogHandler.sendEmptyMessage(0);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////ON BUTTON CLICKED//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(final View v) {

        Intent intent;

        auth = FirebaseAuth.getInstance();

        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btn_signIn.getWindowToken(),0);

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(this);

        if(connectionCheck.checkingConnextion()) {
            Snackbar.make(v, "No Internet Connection", Snackbar.LENGTH_LONG)
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

        switch (v.getId()) {

            ///////////////////////////////////////////////////////////////////////////
            ///////////SIGN IN BUTTON CLICKED//////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////
            case R.id.btn_signin :

                String email = et_signin_email.getText().toString();
                String pass = et_signin_pass.getText().toString();

                if(et_signin_email.getText().toString().trim().length() == 0)
                {
                    tv_email_empty.setVisibility(View.VISIBLE);

                    if(et_signin_pass.getText().toString().trim().length() == 0)
                    {
                        tv_pass_empty.setVisibility(View.VISIBLE);
                        return;
                    }
                    else
                    {
                        tv_pass_empty.setVisibility(View.GONE);
                    }

                    return;

                }
                else
                {
                    tv_email_empty.setVisibility(View.GONE);

                    if(et_signin_pass.getText().toString().trim().length() == 0)
                    {
                        tv_pass_empty.setVisibility(View.VISIBLE);
                        return;
                    }
                    else
                    {
                        tv_pass_empty.setVisibility(View.GONE);
                    }
                }

                dialogHandler = new DialogHandler(MainActivity.this,false);

                auth.signInWithEmailAndPassword(email,pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                checkVerification(authResult);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                dialogHandler.sendEmptyMessage(0);

                                Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.YELLOW)
                                        .show();
                            }
                        });


                break;

            ///////////////////////////////////////////////////////////////////////////
            ///////////SIGN UP BUTTON BTN CLICKED//////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////
            case R.id.btn_signup :

                intent = new Intent(MainActivity.this,SignUpHere.class);
                startActivity(intent);
                break;

            ///////////////////////////////////////////////////////////////////////////
            ///////////FORGOT PASSWORD CLICKED CLICKED//////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////
            case R.id.btn_forgot_pass :

                intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
                break;

            ///////////////////////////////////////////////////////////////////////////
            ///////////SIGN IN WITH GOOGLE CLICKED//////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////
            case R.id.sign_in_google :
                dialogHandler = new DialogHandler(MainActivity.this,false);
                signInWithGoogle();
                break;


            ///////////////////////////////////////////////////////////////////////////
            ///////////SIGN IN WITH FACEBOOK CLICKED//////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////
            case R.id.sign_in_facebook :
                dialogHandler = new DialogHandler(MainActivity.this,false);
                signInWithFaceBook();
               break;
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////SIGN WITH FACEBOOK//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void signInWithFaceBook(){

        fb_sign_in.setReadPermissions("email","public_profile","user_friends");

        fb_sign_in.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                dialogHandler.sendEmptyMessage(0);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                dialogHandler.sendEmptyMessage(0);
            }
        });
    }



    private void handleFacebookAccessToken(AccessToken accessToken) {

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            checkUserProfileAvailability(task.getResult());
                        }
                        else
                        {
                            dialogHandler.sendEmptyMessage(0);
                            Toast.makeText(MainActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////SIGNIN WITH GOOGLE//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void signInWithGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GlobalNames.getGoogleSigninActivityForResult());
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful())
                        {
                            dialogHandler.sendEmptyMessage(0);
                            Toast.makeText(MainActivity.this,"Some Error Occurred. Please try Again",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            checkUserProfileAvailability(task.getResult());
                        }

                    }
                });
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////CHECK VERIFIED EMAIL OR NOT//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    private void checkVerification(final AuthResult authResult){

        if(authResult.getUser().isEmailVerified())
        {
            checkUserProfileAvailability(authResult);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Verification!");
            builder.setMessage("Your Email Address is not verified.");
            builder.setCancelable(true);

            builder.setPositiveButton("Verify Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialogHandler = new DialogHandler(MainActivity.this,false);

                    et_signin_email.setText("");
                    et_signin_pass.setText("");

                    authResult.getUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(getWindow().getDecorView().getRootView(),
                                    "Verification link has been sent on " + authResult.getUser().getEmail(),Snackbar.LENGTH_LONG).show();

                            dialogHandler.sendEmptyMessage(0);

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
            });

            builder.show();
            dialogHandler.sendEmptyMessage(0);
        }
    }

    private void checkUserProfileAvailability(final AuthResult authResult)
    {
        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserProfile())
                .child(authResult.getUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            SharedPreferences sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(AllPreferences.getUserProfile(),dataSnapshot.getValue(UserProfile.class).getUserProfile());
                            editor.putString(AllPreferences.getUSERNAME(),dataSnapshot.getValue(UserProfile.class).getUserName());
                            editor.apply();
                            editor.clear();

                            dialogHandler.sendEmptyMessage(0);
                            startActivity(new Intent(MainActivity.this,DashBoard.class));
                            finish();
                        }
                        else
                        {
                            setUserProfile(authResult);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void setUserProfile(AuthResult authResult)
    {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserProfile("NONE");
        userProfile.setUser_id(authResult.getUser().getUid());
        userProfile.setUserName("iNTCON User");
        userProfile.setUserName_lower("intconuser");

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserProfile())
                .child(authResult.getUser().getUid())
                .setValue(userProfile);

        SharedPreferences sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AllPreferences.getUserProfile(),"NONE");
        editor.putString(AllPreferences.getUSERNAME(),"iNTCON User");
        editor.apply();
        editor.clear();

        dialogHandler.sendEmptyMessage(0);
        startActivity(new Intent(MainActivity.this,Profile.class));
        finish();

    }

}

