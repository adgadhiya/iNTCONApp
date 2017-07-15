package com.myapp.unknown.iNTCON.Acheivement;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.R;
import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.io.File;
import java.util.Random;

public class AchievementDetail extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, AppBarLayout.OnOffsetChangedListener {

    private CheckBox checkBox;
    private LinearLayout toolbar_linear_layout;

    private boolean isShow = false;
    private int scrollRange = -1;

    private boolean isLiked;

    private AchievementListProvider listProvider;

    private ImageSaver imageSaver;

    private ImageView imageView;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_achievement_detail);

        isLiked = getIntent().getBooleanExtra("bool",false);
        listProvider = getIntent().getParcelableExtra(getString(R.string.ach_list_provider_intent));
        GroupProfile groupProfile = getIntent().getParcelableExtra(getString(R.string.ach_group_profile_intent));

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        TextView title = (TextView) findViewById(R.id.achievement_detail_title);
        TextView detail = (TextView) findViewById(R.id.achievement_detail_detail);
        TextView date = (TextView) findViewById(R.id.tv_ach_detail_date);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout_ach_detail);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_ach_detail);

        checkBox = (CheckBox) findViewById(R.id.chk_box_ach_interested);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ach_detail);
        TextView university_toolbar = (TextView) findViewById(R.id.name_toolbar);
        ImageView profile_toolbar = (ImageView) findViewById(R.id.profile_toolbar);
        ImageView home = (ImageView) findViewById(R.id.home);
        toolbar_linear_layout = (LinearLayout) findViewById(R.id.linear_layout_toolbar);
        imageView = (ImageView) findViewById(R.id.achievement_detail_iv);

        detail.setMovementMethod(LinkMovementMethod.getInstance());

        setSupportActionBar(toolbar);

        Glide.with(getApplicationContext())
                .load(groupProfile.getGroup_profile_path())
                .placeholder(R.mipmap.create_group_profile)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.create_group_profile)
                .into(profile_toolbar);


        Glide.with(getApplicationContext())
                .load(listProvider.getImagepath())
                .placeholder(R.mipmap.default_event_achievement_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.default_event_achievement_error)
                .into(imageView);


        university_toolbar.setText(groupProfile.getGroup_name());

        toolbar_linear_layout.setVisibility(View.GONE);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("");
            collapsingToolbarLayout.setTitle("");
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        Typeface typeface = Typeface.createFromAsset(getAssets(),"ml.ttf");
        detail.setTypeface(typeface);

        title.setText(listProvider.getTitle());
        detail.setText(Html.fromHtml(listProvider.getContent()));
        date.setText(listProvider.getDate());

        checkBox.setChecked(isLiked);

        appBarLayout.addOnOffsetChangedListener(this);

        checkBox.setOnCheckedChangeListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////ON START//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////ON STOP/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.activityStopped();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////ON CREATE OPTION ITEM/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.gplus_fb,menu);

        for(int i=0;i<menu.size();i++){
            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////ON OPTION ITEM SELECTED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_share_fb:
                shareWithFB();
                return true;

            case R.id.action_share_gplus:
                signedInWithGPlus();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////ON ACTIVITY FOR RESULT//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GlobalNames.getGooglePlusActivityResult())
        {
            imageSaver.deleteFile();
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////ON CHECK CHANGED STATE/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(buttonView.isPressed()){
            handlelikedOrNot(isChecked);
            isLiked = isChecked;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////UPDATE LIKE CHECKBOX BUTTON/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handlelikedOrNot(final boolean likedOrNot){

        DatabaseReference like = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getACHIEVEMENT())
                .child(GlobalNames.getLIKE())
                .child(listProvider.getKey())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(likedOrNot)
        {
            like.setValue(true);
            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getLikeCnt())
                    .child(listProvider.getKey())
                    .setValue(listProvider.getLikes() + 1);
        }
        else
        {
            like.removeValue();
            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getLikeCnt())
                    .child(listProvider.getKey())
                    .setValue(listProvider.getLikes() - 1);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////IF EXISTS THAN LOAD DATA//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("isChecked",isLiked);
        setResult(RESULT_OK,intent);
        finish();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////ON SCROLLING UP/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        final Animation animation;

        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }

        if (scrollRange + verticalOffset == 0)
        {
            animation = AnimationUtils.loadAnimation(AchievementDetail.this,R.anim.shrink_animation);
            animation.setDuration(100);

            toolbar_linear_layout.setVisibility(View.VISIBLE);
            checkBox.startAnimation(animation);

            if(Build.VERSION.SDK_INT >= 21)
            {
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary,null));
            }
            else
            {
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
            }

            isShow = true;

        } else if (isShow) {

            animation = AnimationUtils.loadAnimation(AchievementDetail.this,R.anim.expand_animation);
            animation.setDuration(100);

            toolbar_linear_layout.setVisibility(View.GONE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkBox.startAnimation(animation);
                }
            },20);


            if(Build.VERSION.SDK_INT >= 21)
            {
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.black_shade,null));
            }
            else
            {
                    getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.black_shade));
             }
            isShow = false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SHARE WITH FACEBOOK///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void shareWithFB()
    {
        imageView.setDrawingCacheEnabled(true);

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentDescription(getString(R.string.download_app))
                .setContentTitle(listProvider.getTitle())
                .setImageUrl(Uri.parse(listProvider.getImagepath()))
                .setContentUrl(Uri.parse(getString(R.string.play_store))).build();

        shareDialog.show(shareLinkContent, ShareDialog.Mode.AUTOMATIC);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SHARE WITH GOOGLE PLUS////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void signedInWithGPlus(){

        Random random = new Random();

        if(ImageSaver.isExternalStorageWritable())
        {
            imageSaver = new ImageSaver(this)
                    .setFileName(random.nextInt() + ".jpg")
                    .setExternal(true)
                    .setDirectoryName("images");
        }
        else
        {
            imageSaver = new ImageSaver(this)
                    .setFileName(random.nextInt() + ".jpg")
                    .setExternal(false)
                    .setDirectoryName("images");
        }

        imageView.setDrawingCacheEnabled(true);
        imageSaver.save(imageView.getDrawingCache());

        File file = imageSaver.createFile();

        try {
            Intent shareIntent = new PlusShare.Builder(this)
                    .setType("text/plain")
                    .setText(listProvider.getTitle() + "\n " + getString(R.string.download_app))
                    .setStream(Uri.fromFile(file))
                    .getIntent();

            startActivityForResult(shareIntent, GlobalNames.getGooglePlusActivityResult());

        } catch (ActivityNotFoundException e)
        {
            Snackbar.make(getWindow().getDecorView().getRootView(),"You Need to Install Google+ in order to Share the Achievements.",Snackbar.LENGTH_SHORT).show();
        }
    }
}