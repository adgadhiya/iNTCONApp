package com.myapp.unknown.iNTCON.Campaign;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.share.model.ShareLinkContent;
import com.google.firebase.database.DatabaseReference;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.OtherClasses.Like_Interested_Going_Bool;
import com.myapp.unknown.iNTCON.OtherClasses.Like_Interested_Going_Cnt;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;

import com.google.android.gms.plus.PlusShare;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.io.File;
import java.util.Random;


public class Campaign_Detail extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, AppBarLayout.OnOffsetChangedListener {

    private ImageButton button;
    private ImageView imageView;
    private CheckBox like,interest,going;

    private LinearLayout toolbar_linear_layout;

    private CampaignDetailProvider detailProvider;

    private DatabaseReference root;

    private ImageSaver imageSaver;

    private boolean isShow = false;
    private int scrollRange = -1;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private LocalBroadcastManager bManager;

    private boolean isAuthorized;

    private long likesF,interestedF,goingF;

    public static final String RECEIVE_CHANGE = "com.myapp.unknown.iNTCON.Campaign.RECEIVE_CHANGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_campaign__detail);

        detailProvider = getIntent().getParcelableExtra(getString(R.string.camp_list_provider_intent));
        final GroupProfile groupProfile = getIntent().getParcelableExtra(getString(R.string.camp_group_profile_intent));
        Like_Interested_Going_Bool like_interested_going_bool = getIntent().getParcelableExtra(getString(R.string.camp_like_interest_going_bool_intent));
        Like_Interested_Going_Cnt like_interested_going_cnt = getIntent().getParcelableExtra(getString(R.string.camp_like_interest_going_cnt_intent));

        likesF = like_interested_going_cnt.getLikes();
        interestedF = like_interested_going_cnt.getInterested();
        goingF = like_interested_going_cnt.getGoing();

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        imageView = (ImageView) findViewById(R.id.campaign_detail_img);

        like = (CheckBox) findViewById(R.id.campaign_detail_like_chk_box);
        interest = (CheckBox)findViewById(R.id.campaign_detail_intetested_chk_box);
        going = (CheckBox)findViewById(R.id.campaign_detail_going_chk_box);

        TextView title = (TextView) findViewById(R.id.tv_campaign_detail_title);
        TextView content = (TextView) findViewById(R.id.tv_campaign_detail_content);
        TextView eventDate = (TextView) findViewById(R.id.campaign_detail_date);
        TextView fees = (TextView) findViewById(R.id.campaign_detail_fees);
        TextView price = (TextView) findViewById(R.id.campaign_detail_winner_price);
        TextView accommodation = (TextView) findViewById(R.id.campaign_detail_acomodation);
        TextView regiLastDate = (TextView) findViewById(R.id.campaign_detail_reg_last_date);
        TextView venue = (TextView) findViewById(R.id.campaign_detail_venue);
        TextView date = (TextView) findViewById(R.id.tv_campaign_detail_date);
        TextView email = (TextView) findViewById(R.id.campaign_detail_email);
        TextView phone = (TextView) findViewById(R.id.campaign_detail_phone);
        TextView site = (TextView) findViewById(R.id.campaign_detail_site);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout_campaign_detail);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_campaigm_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_camp_detail);
        TextView groupName_toolbar = (TextView) findViewById(R.id.name_toolbar);
        ImageView profile_toolbar = (ImageView) findViewById(R.id.profile_toolbar);
        ImageView home = (ImageView) findViewById(R.id.home);
        toolbar_linear_layout = (LinearLayout) findViewById(R.id.linear_layout_toolbar);

        toolbar_linear_layout.setVisibility(View.GONE);

        appBarLayout.addOnOffsetChangedListener(this);

        button = (ImageButton) findViewById(R.id.campaign_chat_btn);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageSaver = new ImageSaver(this);

        content.setMovementMethod(LinkMovementMethod.getInstance());
        email.setMovementMethod(LinkMovementMethod.getInstance());
        phone.setMovementMethod(LinkMovementMethod.getInstance());
        site.setMovementMethod(LinkMovementMethod.getInstance());

        Glide.with(this)
                .load(detailProvider.getImg_path())
                .placeholder(R.mipmap.default_event_achievement_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.default_event_achievement_error)
                .into(imageView);

        Glide.with(this)
                .load(groupProfile.getGroup_profile_path())
                .placeholder(R.mipmap.create_group_profile)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.create_group_profile)
                .into(profile_toolbar);

        like.setChecked(like_interested_going_bool.isLikes());
        interest.setChecked(like_interested_going_bool.isInterested());
        going.setChecked(like_interested_going_bool.isGoing());

        like.setOnCheckedChangeListener(this);
        interest.setOnCheckedChangeListener(this);
        going.setOnCheckedChangeListener(this);

        root = FirebaseDatabase.getInstance().getReference().getRoot();

        SharedPreferences sp = getSharedPreferences(AllPreferences.getPreferenceName(), MODE_PRIVATE);

        isAuthorized = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(sp.getString(AllPreferences.getUID(),"NONE"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Campaign_Detail.this, Campaign_Chat.class);
                intent.putExtra("KEY",detailProvider.getKey());
                intent.putExtra("GROUP_PROFILE",groupProfile);
                intent.putExtra("DATE",detailProvider.getDate());
                intent.putExtra(getString(R.string.is_data_resource_chat_intent),false);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("");
            collapsingToolbarLayout.setTitle(" ");
        }


        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_CHANGE);
        bManager.registerReceiver(bReceiver, intentFilter);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"ml.ttf");
        content.setTypeface(typeface);

        title.setText(detailProvider.getTitle());
        content.setText(detailProvider.getContent());
        eventDate.setText(detailProvider.getEventDate());
        fees.setText(detailProvider.getFees());
        price.setText(detailProvider.getPrize());
        regiLastDate.setText(detailProvider.getRegistration_last_date());
        venue.setText(detailProvider.getVenue());
        accommodation.setText(detailProvider.getAccommodation());
        date.setText(detailProvider.getDate());

        groupName_toolbar.setText(groupProfile.getGroup_name());

        email.setText(detailProvider.getEmail());
        phone.setText(detailProvider.getPhone());
        site.setText(detailProvider.getSite());

        Linkify.addLinks(email,Linkify.ALL);
        Linkify.addLinks(phone,Linkify.ALL);
        Linkify.addLinks(site,Linkify.ALL);

        stripUnderlines(email);
        stripUnderlines(phone);
        stripUnderlines(site);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
    }

    @Override
    protected void onDestroy() {
        bManager.unregisterReceiver(bReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.activityStopped();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////ON CREATE OPTION MENU/////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.gplus_fb,menu);

        for(int i=0;i<menu.size();i++){
            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////ON OPTION ITEM SELECTED//////////////////////////////////////////////////////////////
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
    //////////ON ACTIVITY FOR RESULT///////////////////////////////////////////////////////////
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
    ////////ON OFFSET CHANGED//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        Animation animation;

        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0)
        {
            animation = AnimationUtils.loadAnimation(Campaign_Detail.this,R.anim.shrink_animation);
            animation.setDuration(100);

            toolbar_linear_layout.setVisibility(View.VISIBLE);

            button.startAnimation(animation);

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

            animation = AnimationUtils.loadAnimation(Campaign_Detail.this,R.anim.expand_animation);
            animation.setDuration(100);

            toolbar_linear_layout.setVisibility(View.GONE);

            button.setVisibility(View.VISIBLE);
            button.startAnimation(animation);

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
    ////HANDLE CHECKED CHANGED LISTNER//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        switch (buttonView.getId()){

            case R.id.campaign_detail_like_chk_box:

                if(isChecked)
                {

                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_bool())
                            .child(detailProvider.getKey())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(GlobalNames.getLIKE())
                            .setValue(true);

                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_cnt())
                            .child(detailProvider.getKey())
                            .child(GlobalNames.getLIKE())
                            .setValue(likesF + 1);

                }
                else
                {
                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_bool())
                            .child(detailProvider.getKey())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(GlobalNames.getLIKE())
                            .removeValue();

                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_cnt())
                            .child(detailProvider.getKey())
                            .child(GlobalNames.getLIKE())
                            .setValue(likesF - 1);
                }
                break;

            case R.id.campaign_detail_intetested_chk_box:

                if(isChecked)
                {
                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_bool())
                            .child(detailProvider.getKey())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(GlobalNames.getINTERESTED())
                            .setValue(true);

                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_cnt())
                            .child(detailProvider.getKey())
                            .child(GlobalNames.getINTERESTED())
                            .setValue(interestedF + 1);
                }
                else
                {
                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_bool())
                            .child(detailProvider.getKey())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(GlobalNames.getINTERESTED())
                            .removeValue();

                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_cnt())
                            .child(detailProvider.getKey())
                            .child(GlobalNames.getINTERESTED())
                            .setValue(interestedF - 1);
                }
                break;

            case R.id.campaign_detail_going_chk_box:

                if(isChecked)
                {
                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_bool())
                            .child(detailProvider.getKey())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(GlobalNames.getGOING())
                            .setValue(true);

                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_cnt())
                            .child(detailProvider.getKey())
                            .child(GlobalNames.getGOING())
                            .setValue(goingF + 1);
                }
                else
                {
                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_bool())
                            .child(detailProvider.getKey())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(GlobalNames.getGOING())
                            .removeValue();

                    root.child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getLike_interested_going_cnt())
                            .child(detailProvider.getKey())
                            .child(GlobalNames.getGOING())
                            .setValue(goingF - 1);
                }
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON BACK PRESSED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("likeBool", like.isChecked());
        intent.putExtra("interestBool", interest.isChecked());
        intent.putExtra("goingBool", going.isChecked());
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SHARE WITH FACEBOOK///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void shareWithFB()
    {
        imageView.setDrawingCacheEnabled(true);

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentDescription(getString(R.string.download_app))
                .setContentTitle(detailProvider.getTitle())
                .setImageUrl(Uri.parse(detailProvider.getImg_path()))
                .setContentUrl(Uri.parse(getString(R.string.play_store))).build();

        shareDialog.show(shareLinkContent);

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SHARE WITH GOOGLE PLUS////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void signedInWithGPlus(){

        if(!isAuthorized)
        {
            Snackbar.make(getWindow().getDecorView().getRootView(),"Your request has not been accepted yet",Snackbar.LENGTH_SHORT).show();
            return;
        }

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
                    .setText(detailProvider.getTitle() + "\n " + getString(R.string.download_app))
                    .setStream(Uri.fromFile(file))
                    .getIntent();

            startActivityForResult(shareIntent, GlobalNames.getGooglePlusActivityResult());

        } catch (ActivityNotFoundException e)
        {
            Snackbar.make(getWindow().getDecorView().getRootView(),"You Need to Install Google+ in order to Share the Event",Snackbar.LENGTH_SHORT).show();
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////BROAD CAST RECEIVER/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if(intent.getAction().equals(RECEIVE_CHANGE) &&
                    intent.getStringExtra("item_key").equals(detailProvider.getKey()))
            {
                Like_Interested_Going_Cnt likeInterestedGoingCnt = intent.getParcelableExtra("like_interest_going");
                likesF = likeInterestedGoingCnt.getLikes();
                interestedF = likeInterestedGoingCnt.getInterested();
                goingF = likeInterestedGoingCnt.getGoing();
            }
        }
    };

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////NO UNDER LINES ON HYPER LINKS//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }
}