package com.myapp.unknown.iNTCON.OtherClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.Acheivement.Achievement_Fragment;
import com.myapp.unknown.iNTCON.Acheivement.AddAchievement;
import com.myapp.unknown.iNTCON.Campaign.AddCampaign;
import com.myapp.unknown.iNTCON.Campaign.Campaign_Fragment;
import com.myapp.unknown.iNTCON.NavigationView.CreateGroup;
import com.myapp.unknown.iNTCON.DataResource.DataResource_Fragment;
import com.myapp.unknown.iNTCON.DataResource.DataResourceProvider;
import com.myapp.unknown.iNTCON.Downloads.DownloadResourcesActivity;
import com.myapp.unknown.iNTCON.NavigationView.FAQ;
import com.myapp.unknown.iNTCON.NavigationView.GroupInfo;
import com.myapp.unknown.iNTCON.NavigationView.GroupMembers;
import com.myapp.unknown.iNTCON.InstRequest.FieldDetail;
import com.myapp.unknown.iNTCON.NavigationView.JoinGroup;
import com.myapp.unknown.iNTCON.NavigationView.PrivacyPolicy;
import com.myapp.unknown.iNTCON.Notice.AddNotice;
import com.myapp.unknown.iNTCON.Notice.NoticeListProvider;
import com.myapp.unknown.iNTCON.Notice.Notice_Fragment;
import com.myapp.unknown.iNTCON.NavigationView.SentRequest;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.SignIn.Profile;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseDownloadedOrError;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseDownloading;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseShare;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseUploadedOrError;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseUploading;
import com.myapp.unknown.iNTCON.Uploads.UploadResourcesListActivity;
import com.myapp.unknown.iNTCON.NavigationView.UserGroupListAdapter;
import com.myapp.unknown.iNTCON.NavigationView.UserGroupListClass;
import com.myapp.unknown.iNTCON.NavigationView.UserRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class DashBoard extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        View.OnClickListener,
        RewardedVideoAdListener,
        AdapterView.OnItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private boolean isLongPressed;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView navBarUserGroupList;
    private ImageView navHeaderUserProfile, navHeaderGroupProfile;
    private TextView navHeaderUserName, navHeaderGroupName;
    private ImageView navHeaderArrowIV;
    private TextView navBarMemberCount_tv;
    private TextView navBarRequestCount_tv;
    private TextView navBarRequestSentCount_tv;
    private ProgressBar navBarProgressbar;
    private Spinner navBarYearSpinner;
    private Button cancel_share;
    private TextView navBarMessageTV;
    private ImageView navBarMessageIV;
    private Button navBarMessageRefreshOrCreateGroupBTN,navBarMessageJoinGroupBTN;
    private LinearLayout navBarMessageLayout,navBarMessageSeparatorLayout;
    private RelativeLayout navigationHeaderParentLayout;

    private boolean isSecondTime;

    private Menu menu;

    private Notice_Fragment notice_fragment;
    private Achievement_Fragment achievement_fragment;
    private Campaign_Fragment campaign_fragment;
    private DataResource_Fragment dataResourceFragment;

    private AppBarLayout.LayoutParams params_toolber;

    private SharedPreferences sp;

    private RewardedVideoAd mAd;

    private String[] navBarSpinnerYear;

    private boolean isGroupList = false;
    private int group_type;

    private String selectedField;
    private String selectedYear;

    private ArrayList<GroupProfile> userGroupList;
    private ArrayList<UserGroupListClass> userGroupDetail;
    private ArrayList<String> userRequestKeyList;
    private ArrayList<String> yearStringArrayList;
    private ArrayList<FieldDetail> fieldKeyArrayList;
    private ArrayList<Boolean> isNewGroupMSGReceived;

    NetworkConnectionCheck connectionCheck;

    UserGroupListAdapter userGroupListAdapter;

    private String userProfilePath;
    private int selectedYearPosition;

    private GoogleApiClient googleApiClient;

    private LocalBroadcastManager bManager;
    public static final String SHARE_ITEMS = "com.myapp.unknown.iNTCON.OtherClasses.SHARE_ITEMS";

    private Ringtone playSound;

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

        setContentView(R.layout.activity_dash_board);

        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////

        viewPager = (ViewPager) findViewById(R.id.view_pager_board);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_dashboard);
        fab = (FloatingActionButton) findViewById(R.id.add_notice_event_achievement_resources);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar_dashboard);
        navBarUserGroupList = (RecyclerView) findViewById(R.id.navbar_group_list_rv);
        navBarProgressbar = (ProgressBar) findViewById(R.id.navbar_progressBar);
        cancel_share = (Button) findViewById(R.id.cancel_share_btn);

        View headerView = navigationView.getHeaderView(0);

        LinearLayout navHeaderSwitchGroupLayout = (LinearLayout) headerView.findViewById(R.id.navbar_header_switch_group);
        navHeaderUserProfile = (ImageView)headerView. findViewById(R.id.navbar_header_user_profile);
        navHeaderGroupProfile = (ImageView)headerView.findViewById(R.id.navbar_header_group_profile);
        navHeaderUserName = (TextView)headerView. findViewById(R.id.navbar_header_user_name);
        navHeaderGroupName = (TextView)headerView. findViewById(R.id.navbar_header_group_name);
        navHeaderArrowIV = (ImageView) headerView.findViewById(R.id.navbar_header_arrow_iv);
        navigationHeaderParentLayout = (RelativeLayout) headerView.findViewById(R.id.navigation_header_layout);

        navBarMemberCount_tv = (TextView) navigationView.getMenu().findItem(R.id.navbar_menu_group_members).getActionView();
        navBarRequestCount_tv = (TextView) navigationView.getMenu().findItem(R.id.navbar_menu_requests).getActionView();
        navBarRequestSentCount_tv = (TextView) navigationView.getMenu().findItem(R.id.navbar_menu_sent_requests).getActionView();

        navBarMessageIV = (ImageView) findViewById(R.id.navbar_message_iv);
        navBarMessageTV = (TextView) findViewById(R.id.navbar_message_tv);
        navBarMessageJoinGroupBTN = (Button) findViewById(R.id.navbar_message_join_btn);
        navBarMessageRefreshOrCreateGroupBTN = (Button) findViewById(R.id.navbar_message_refresh_create_btn);
        navBarMessageLayout = (LinearLayout) findViewById(R.id.navbar_message_layout);
        navBarMessageSeparatorLayout = (LinearLayout) findViewById(R.id.navbar_message_separator_layout);

        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////

        notice_fragment = new Notice_Fragment();
        campaign_fragment = new Campaign_Fragment();
        achievement_fragment = new Achievement_Fragment();
        dataResourceFragment = new DataResource_Fragment();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_closed);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        connectionCheck = new NetworkConnectionCheck(this);
        userGroupList = new ArrayList<>();
        userGroupDetail = new ArrayList<>();
        isNewGroupMSGReceived = new ArrayList<>();

        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////

        fragmentAdapter.addFragment(achievement_fragment,"Activities");
        fragmentAdapter.addFragment(notice_fragment,"AdminNotices");
        fragmentAdapter.addFragment(dataResourceFragment,"Resources");
        fragmentAdapter.addFragment(campaign_fragment,"Events");

        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.achievement_tab_selected);
        tabLayout.getTabAt(1).setIcon(R.drawable.notice_tab_selected);
        tabLayout.getTabAt(2).setIcon(R.drawable.materialstorage_tab_selected);
        tabLayout.getTabAt(3).setIcon(R.drawable.campaign_tab_selected);


        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////

        setSupportActionBar(toolbar);
        navBarUserGroupList.setHasFixedSize(true);
        navBarUserGroupList.setLayoutManager(new LinearLayoutManager(this));
        navBarUserGroupList.setItemAnimator(null);

        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////

        navHeaderSwitchGroupLayout.setOnClickListener(this);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        fab.setOnClickListener(this);
        tabLayout.addOnTabSelectedListener(this);
        navHeaderUserProfile.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        navBarMessageJoinGroupBTN.setOnClickListener(this);
        navBarMessageRefreshOrCreateGroupBTN.setOnClickListener(this);


        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        params_toolber = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();


        ////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .enableAutoManage(DashBoard.this,null)
                .build();

        ////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////

        selectedYear(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()));
        checkSharedPreferenceOrNot();
        getGroupList();
        getUserProfile();
        getSentRequests();
        setNavigationHeaderImage();

        removePendingDownloads();
        removePendingUploads();

        if(!sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
        {
            group_type = sp.getInt(AllPreferences.getGroupType(),0);
            getMemberCNT();
            getUserRequests();
            getAuthentication();
        }

        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SHARE_ITEMS);
        bManager.registerReceiver(bReceiver, intentFilter);


        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        playSound = RingtoneManager.getRingtone(this, ringtone);

        cancel_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyApplication.setIsSharingOn(false);
                cancel_share.setVisibility(View.GONE);
                new SqliteDataBaseShare(DashBoard.this).deleteAll();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////BROADCAST RECEVER/////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(SHARE_ITEMS))
            {
                cancel_share.setVisibility(View.VISIBLE);
            }

        }
    };

    public void hideCancelShareBTN()
    {
        cancel_share.setVisibility(View.GONE);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////HANDLE ORIENTATION CHANGE/////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////REMOVE PENDING UPLOADS/////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void removePendingUploads() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                SqliteDataBaseUploadedOrError sqliteDataBaseUploadedOrError = new SqliteDataBaseUploadedOrError(DashBoard.this);
                SqliteDataBaseUploading sqliteDataBaseUploading = new SqliteDataBaseUploading(DashBoard.this);

                Cursor cursor = sqliteDataBaseUploading.getAllData();

                String push_key;

                SimpleDateFormat dateF = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                SimpleDateFormat timeF = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

                if(cursor.moveToFirst())
                {
                    do {

                        push_key = FirebaseDatabase.getInstance().getReference().getRoot().push().toString();

                        sqliteDataBaseUploadedOrError.insertData
                                (
                                        cursor.getString(1),
                                        cursor.getString(2),
                                        0,
                                        cursor.getLong(4),
                                        dateF.format(new Date()),
                                        timeF.format(new Date()),
                                        1,
                                        0,
                                        cursor.getString(7),
                                        push_key,
                                        cursor.getString(8)
                                );

                    } while(cursor.moveToNext());

                    sqliteDataBaseUploading.deleteAll();
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////REMOVE PENDING DOWNLOADS/////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void removePendingDownloads() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                SqliteDataBaseDownloadedOrError sqliteDataBaseDownloadedOrError = new SqliteDataBaseDownloadedOrError(DashBoard.this);
                SqliteDataBaseDownloading sqliteDataBaseDownloading = new SqliteDataBaseDownloading(DashBoard.this);

                Cursor cursor = sqliteDataBaseDownloading.getAllData();

                if(cursor.moveToFirst())
                {
                    do
                    {
                        sqliteDataBaseDownloadedOrError.insertData
                                (
                                        cursor.getString(1),
                                        cursor.getString(2),
                                        0,
                                        cursor.getLong(4),
                                        cursor.getString(5),
                                        cursor.getString(6),
                                        1,
                                        0,
                                        cursor.getString(9),
                                        cursor.getString(10)
                                );

                    }while(cursor.moveToNext());

                    sqliteDataBaseDownloading.deleteAll();

                }

                sqliteDataBaseDownloadedOrError.close();
                sqliteDataBaseDownloading.close();

            }
        });

    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////ON DESTROY/////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bManager.unregisterReceiver(bReceiver);
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////GET SENT REQUESTS//////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void getSentRequests(){

        FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getMyGroupRequests())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            Iterator iterator = dataSnapshot.getChildren().iterator();

                            userRequestKeyList = new ArrayList<>();

                            while (iterator.hasNext())
                            {
                                userRequestKeyList.add(((DataSnapshot)iterator.next()).getValue(String.class));
                            }

                            navBarRequestSentCount_tv.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }
                        else
                        {
                            navBarRequestSentCount_tv.setText(String.valueOf(0));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //////////////////////////////////////////////////////////////////////
    //////////////////GET USER REQUEST/////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void getUserRequests(){

        FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class))
                        {
                            getRequestedUserCNT();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////GET REQUEST COUNT//////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void getAuthentication()
    {
        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getKey().equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                                dataSnapshot.exists())
                        {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean(AllPreferences.getIsAdmin(),dataSnapshot.getValue(Boolean.class));
                            editor.apply();
                            editor.clear();

                            Intent RTReturn = new Intent(GroupMembers.AUTH_CHANGED);
                            LocalBroadcastManager.getInstance(DashBoard.this).sendBroadcast(RTReturn);

                            if(dataSnapshot.getValue(Boolean.class) && (group_type != GlobalNames.getPrivateGroup() ||
                                    (group_type == GlobalNames.getPrivateGroup() &&
                                            (tabLayout.getSelectedTabPosition() == 1 || tabLayout.getSelectedTabPosition() == 2))))
                            {
                                fab.show();
                            }
                            else
                            {
                                fab.hide();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////GET REQUEST COUNT//////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void getRequestedUserCNT(){

        FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserRequests())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getKey().equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                        {
                            navBarRequestCount_tv.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////GET MEMBER COUNT///////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void getMemberCNT() {

        FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getGroupMemberCount())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getKey().equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                        {
                            navBarMemberCount_tv.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////ON START/////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();
        MyApplication.activityStarted();
        googleApiClient.connect();
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////ON STOP/////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    @Override
    public void onStop() {
        super.onStop();
        MyApplication.activityStopped();
        googleApiClient.disconnect();
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////ON POST CREATE/////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON-CREATE-OPTION-MENU////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.upload_download, this.menu);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON OPTION ITEM SELECTED////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if(isLongPressed)
                {
                    onBackPressed();
                }
                else
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;

            case R.id.delete_items:
                deleteItems();
                return true;

            case R.id.uploads_menu:
                startActivity(new Intent(DashBoard.this, UploadResourcesListActivity.class));
                return true;

            case R.id.downloads_menu:
                startActivity(new Intent(DashBoard.this, DownloadResourcesActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////SET TITLE//////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN TAB CHANGED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        if(mAd.isLoaded())
        {
            showAdDialog();
            return;
        }
        else
        {
            loadRewardedVideoAd();
        }

        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
        toolbar.getMenu().clear();

        switch (tabLayout.getSelectedTabPosition()){

            case 0 :

                if(sp.getBoolean(AllPreferences.getIsAdmin(),false) &&
                        sp.getInt(AllPreferences.getGroupType(),0) != GlobalNames.getPrivateGroup() &&
                        sp.getInt(AllPreferences.getGroupType(),0) != 0)
                {
                    fab.show();
                }
                else
                {
                    fab.hide();
                }

                toolbar.inflateMenu(R.menu.upload_download);
                achievement_fragment.achievement_selected();
                achievement_fragment.backPressed(true);
                break;

            case 1 :

                toolbar.inflateMenu(R.menu.upload_download);

                if(sp.getBoolean(AllPreferences.getIsAdmin(),false))
                {
                    fab.show();
                }
                else
                {
                    fab.hide();
                }

                notice_fragment.notice_selected();
                notice_fragment.backPressed(true);
                break;

            case 2 :

                toolbar.inflateMenu(R.menu.upload_download);

                if(sp.getBoolean(AllPreferences.getIsAdmin(),false))
                {
                    fab.show();
                }
                else
                {
                    fab.hide();
                }

                dataResourceFragment.dataResourceSelected();
                setActionBarTitle("Resources");

                break;

            case 3 :

                toolbar.inflateMenu(R.menu.camp_achi_menu);

                if(sp.getBoolean(AllPreferences.getIsAdmin(),false) &&
                        sp.getInt(AllPreferences.getGroupType(),0) != GlobalNames.getPrivateGroup() &&
                        sp.getInt(AllPreferences.getGroupType(),0) != 0)
                {
                    fab.show();
                }
                else
                {
                    fab.hide();
                }

                campaign_fragment.campaign_selected();
                campaign_fragment.backPressed(true);

                break;
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(25,118,210)));
        tabLayout.setBackgroundColor(Color.rgb(25,118,210));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle.syncState();

        params_toolber.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

        if(tab.getPosition() == 1)
        {
            notice_fragment.backPressed(true);
        }

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN TAB DIRECTLY CLICKED//////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {


        ///////////////////////////////////////////////////////////////////////////
        ///////////FAB CLICKED//////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        if(v.getId() == R.id.add_notice_event_achievement_resources)
        {
            Intent intent;

            switch (tabLayout.getSelectedTabPosition())
            {
                case 0:
                    intent = new Intent(DashBoard.this, AddAchievement.class);
                    startActivity(intent);
                    break;

                case 1:
                    intent = new Intent(DashBoard.this, AddNotice.class);
                    startActivity(intent);
                    break;

                case 2:
                    createNewResourceItemName();
                    break;


                case 3:
                    intent = new Intent(DashBoard.this, AddCampaign.class);
                    startActivity(intent);
                    break;
            }
        }
        ///////////////////////////////////////////////////////////////////////////
        ///////NAV BAR REFRESH NOW SELECTED//////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        else if(v.getId() == R.id.navbar_message_refresh_create_btn)
        {
            if(navBarMessageRefreshOrCreateGroupBTN.getText().toString().equals(getString(R.string.refresh_now)))
            {
                navBarProgressbar.setVisibility(View.VISIBLE);
                navBarMessageIV.setVisibility(View.GONE);
                navBarMessageRefreshOrCreateGroupBTN.setVisibility(View.GONE);
                navBarMessageJoinGroupBTN.setVisibility(View.GONE);
                navBarMessageLayout.setVisibility(View.GONE);
                navBarMessageSeparatorLayout.setVisibility(View.GONE);
                getGroupList();
            }
            else
            {
                startActivity(new Intent(DashBoard.this,CreateGroup.class));
            }
        }
        ///////////////////////////////////////////////////////////////////////////
        ///////////NAV BAR JOIN BTN CLICKED//////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        else if(v.getId() == R.id.navbar_message_join_btn)
        {
            startActivity(new Intent(DashBoard.this,JoinGroup.class));
        }
        ///////////////////////////////////////////////////////////////////////////
        ///////////NAV BAR SWITCH GROUP SELECTED//////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        else if(v.getId() == R.id.navbar_header_switch_group)
        {
            isGroupList = !isGroupList;

            if(connectionCheck.checkingConnextion() && userGroupList.size() == 0 && isGroupList)
            {

                navBarUserGroupList.setVisibility(View.VISIBLE);
                navigationView.getMenu().clear();

                navBarMessageIV.setImageResource(R.mipmap.no_connection);
                navBarMessageTV.setText(getString(R.string.no_network));
                navBarMessageRefreshOrCreateGroupBTN.setText(R.string.refresh_now);
                navBarMessageLayout.setVisibility(View.VISIBLE);
                navBarMessageIV.setVisibility(View.VISIBLE);
                navBarMessageRefreshOrCreateGroupBTN.setVisibility(View.VISIBLE);
                navBarProgressbar.setVisibility(View.GONE);

            }
            else if (isGroupList)
            {
                navBarUserGroupList.setVisibility(View.VISIBLE);
                navigationView.getMenu().clear();
                navHeaderArrowIV.setImageResource(android.R.drawable.arrow_up_float);

                if(userGroupList.size() == 0)
                {
                    getGroupList();
                }

            }
            else
            {
                navBarProgressbar.setVisibility(View.GONE);

                navBarMessageIV.setVisibility(View.GONE);
                navBarMessageRefreshOrCreateGroupBTN.setVisibility(View.GONE);
                navBarMessageJoinGroupBTN.setVisibility(View.GONE);
                navBarMessageLayout.setVisibility(View.GONE);
                navBarMessageSeparatorLayout.setVisibility(View.GONE);

                navBarUserGroupList.setVisibility(View.GONE);
                navigationView.inflateMenu(R.menu.navbar_menu);
                navHeaderArrowIV.setImageResource(android.R.drawable.arrow_down_float);

                navBarMemberCount_tv = (TextView) navigationView.getMenu().findItem(R.id.navbar_menu_group_members).getActionView();
                navBarRequestCount_tv = (TextView) navigationView.getMenu().findItem(R.id.navbar_menu_requests).getActionView();
                navBarRequestSentCount_tv = (TextView) navigationView.getMenu().findItem(R.id.navbar_menu_sent_requests).getActionView();

                getSentRequests();
                getMemberCNT();
                getUserRequests();

                if(!sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
                {
                    if(sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getPrivateInstituteSchool() ||
                            sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getSCHOOL())
                    {
                        navBarSpinnerYear = getResources().getStringArray(R.array.school_year_navbar_array_no_none);
                        navBarYearSpinner = (Spinner) navigationView.getMenu().findItem(R.id.navbar_menu_change_year).getActionView();
                        navBarYearSpinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,navBarSpinnerYear));
                        navBarYearSpinner.setSelection(selectedYearPosition,false);
                        navBarYearSpinner.setOnItemSelectedListener(this);
                    }
                    else if(sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getUniversityOrCollege())
                    {
                        navBarSpinnerYear = getResources().getStringArray(R.array.year_navbar_array);
                        navBarYearSpinner = (Spinner) navigationView.getMenu().findItem(R.id.navbar_menu_change_year).getActionView();
                        navBarYearSpinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,navBarSpinnerYear));
                        navBarYearSpinner.setSelection(selectedYearPosition,false);
                        navBarYearSpinner.setOnItemSelectedListener(this);
                    }
                    else
                    {
                        navigationView.getMenu().findItem(R.id.navbar_menu_change_year).setVisible(false);
                    }
                }
                else
                {
                    navigationView.getMenu().findItem(R.id.navbar_menu_change_year).setVisible(false);
                }
            }
        }
        ///////////////////////////////////////////////////////////////////////////
        ///////////NAV BAR USER PROFILE CLICKED//////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        else if(v.getId() == R.id.navbar_header_user_profile)
        {
            if(navHeaderUserName.getText().toString().isEmpty() || navHeaderUserProfile == null)
            {
                startActivity(new Intent(DashBoard.this, Profile.class));
            }
            else
            {
                Intent intent = new Intent(DashBoard.this,Profile.class);
                intent.putExtra(getString(R.string.user_name_intent),navHeaderUserName.getText().toString());
                intent.putExtra(getString(R.string.user_profile_intent),userProfilePath);
                startActivity(intent);
            }

        }
    }

    //////////////////////////////////////////////////////////////////////
    /////////////////////CREATE NEW RESOURCE ITEM NAME////////////////////
    //////////////////////////////////////////////////////////////////////
    private void createNewResourceItemName() {

        View view = getLayoutInflater().inflate(R.layout.data_resource_custom_layout,null);
        final EditText editText = (EditText) view.findViewById(R.id.custom_layout_et);
        final Spinner fieldSpinner = (Spinner) view.findViewById(R.id.custom_layout_spinner_field);
        final Spinner yearSpinner = (Spinner) view.findViewById(R.id.custom_layout_spinner_year);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Resource Title");
        builder.setView(view,25,25,25,25);

        yearStringArrayList = new ArrayList<>();
        fieldKeyArrayList = new ArrayList<>();

        selectedField = GlobalNames.getDummyNode();
        selectedYear = GlobalNames.getDummyNode();

        if(group_type == GlobalNames.getUniversityOrCollege())
        {
            String[] yearList = getResources().getStringArray(R.array.year_array_no_none);
            Collections.addAll(yearStringArrayList, yearList);
            yearSpinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,yearStringArrayList));

            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getCoursesOffered())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {

                                Iterator iterator = dataSnapshot.getChildren().iterator();

                                while(iterator.hasNext())
                                {
                                    fieldKeyArrayList.add(((DataSnapshot)iterator.next()).getValue(FieldDetail.class));
                                }

                                ArrayList<String> stringArrayList = new ArrayList<>();

                                for(int i=0 ; i<fieldKeyArrayList.size() ; i++)
                                {
                                    stringArrayList.add(fieldKeyArrayList.get(i).getFieldName());
                                }

                                fieldSpinner.setAdapter(new ArrayAdapter<>(DashBoard.this,android.R.layout.simple_list_item_1,stringArrayList));

                            }
                            else
                            {
                                Toast.makeText(DashBoard.this, "No any course found for selected Group.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            if(FirebaseAuth.getInstance().getCurrentUser() != null)
                            {
                                Toast.makeText(DashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
        else if(group_type == GlobalNames.getSCHOOL() || group_type == GlobalNames.getPrivateInstituteSchool())
        {
            String[] yearList = getResources().getStringArray(R.array.school_year_navbar_array_no_none);
            Collections.addAll(yearStringArrayList,yearList);
            yearSpinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,yearStringArrayList));
            fieldSpinner.setVisibility(View.GONE);

        }
        else if(group_type == GlobalNames.getPrivateInstituteClg())
        {
            yearSpinner.setVisibility(View.GONE);

            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getCoursesOffered())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {

                                Iterator iterator = dataSnapshot.getChildren().iterator();

                                while(iterator.hasNext())
                                {
                                    fieldKeyArrayList.add(((DataSnapshot)iterator.next()).getValue(FieldDetail.class));
                                }

                                ArrayList<String> stringArrayList = new ArrayList<>();

                                for(int i=0 ; i<fieldKeyArrayList.size() ; i++)
                                {
                                    stringArrayList.add(fieldKeyArrayList.get(i).getFieldName());
                                }

                                fieldSpinner.setAdapter(new ArrayAdapter<>(DashBoard.this,android.R.layout.simple_list_item_1,stringArrayList));

                            }
                            else
                            {
                                Toast.makeText(DashBoard.this, "No any course found for selected Group.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            if(FirebaseAuth.getInstance().getCurrentUser() != null)
                            {
                                Toast.makeText(DashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            fieldSpinner.setVisibility(View.GONE);
            yearSpinner.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(editText.getText().toString().isEmpty())
                {
                    Toast.makeText(DashBoard.this, "Item name can not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if((yearSpinner.isShown() && yearSpinner.getAdapter() == null) ||
                        (fieldSpinner.isShown() && fieldSpinner.getAdapter() == null))
                {
                    Toast.makeText(DashBoard.this, "Please wait while data is Loading...", Toast.LENGTH_SHORT).show();
                    builder.show();
                    return;
                }

                if(yearSpinner.getVisibility() == View.VISIBLE)
                {
                    selectedYear = yearStringArrayList.get(yearSpinner.getSelectedItemPosition());
                }

                if(fieldSpinner.getVisibility() == View.VISIBLE)
                {
                    selectedField = fieldKeyArrayList.get(fieldSpinner.getSelectedItemPosition()).getKey();
                }


                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot()
                        .child(GlobalNames.getDataResource())
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(selectedField)
                        .child(selectedYear.replace(" ",""));

                String key = reference.push().getKey();

                DataResourceProvider dataResourceProvider = new DataResourceProvider();
                dataResourceProvider.setComment_cnt();
                dataResourceProvider.setItem_cnt();
                dataResourceProvider.setCreated_on(format.format(new Date()));
                dataResourceProvider.setTitle(editText.getText().toString());
                dataResourceProvider.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                dataResourceProvider.setKey(key);

                reference.child(key).setValue(dataResourceProvider);
            }
        });

        builder.setNegativeButton("cancel",null);

        builder.show();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN LONG PRESSED/..///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void onLongPress(boolean isInLongPressedMode){

        toolbar.getMenu().clear();
        isLongPressed = isInLongPressedMode;

        if(isInLongPressedMode)
        {
            params_toolber.setScrollFlags(0);
            toolbar.inflateMenu(R.menu.delete);
            menu.findItem(R.id.delete_items).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(30,136,229)));
            tabLayout.setBackgroundColor(Color.rgb(30,136,229));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            params_toolber.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.upload_download);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(25,118,210)));
            tabLayout.setBackgroundColor(Color.rgb(25,118,210));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            actionBarDrawerToggle.syncState();

            switch (tabLayout.getSelectedTabPosition()){

                case 0:
                    achievement_fragment.backPressed(false);
                    break;

                case 1:
                    notice_fragment.backPressed(false);
                    break;

                case 3:
                    campaign_fragment.backPressed(false);
                    break;
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN DELETE SELECTED///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void deleteItems(){

        switch (tabLayout.getSelectedTabPosition()){

            case 0:
                achievement_fragment.deleteItems();
                break;

            case 1:
                notice_fragment.deleteItems();
                break;

            case 3:
                campaign_fragment.deleteItems();
                break;

            default:
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN BACK PRESSED//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

        if(!isLongPressed)
        {
            if(MyApplication.isUploading() || MyApplication.isDownloading())
            {
                confirmExit();
            }
            else if(isSecondTime)
            {
                super.onBackPressed();
            }
            else
            {
                Toast.makeText(DashBoard.this, "Press again to exit iNTCON", Toast.LENGTH_SHORT).show();
                isSecondTime = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSecondTime = false;
                    }
                },2000);
            }

        }
        else
        {
            params_toolber.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

            isLongPressed = !isLongPressed;
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.upload_download);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(25,118,210)));
            tabLayout.setBackgroundColor(Color.rgb(25,118,210));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            actionBarDrawerToggle.syncState();

          switch (tabLayout.getSelectedTabPosition()){

              case 0:
                  achievement_fragment.backPressed(false);
                  break;

              case 1:
                  notice_fragment.backPressed(false);
                  break;

              case 3:
                  campaign_fragment.backPressed(false);
                  break;

          }
        }
    }

    private void confirmExit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Downloads and/or Uploads are in progress. Confirm Exit?");
        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyApplication.setIsDownloading(false);
                MyApplication.setIsUploading(false);
                onBackPressed();
            }
        }).setNegativeButton("cancel",null);

        builder.show();

    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////LoaD REWARDED VIDEO AD/////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void loadRewardedVideoAd() {

        long previouslySavedTime = sp.getLong(AllPreferences.getPreviouslySavedTime(),0);

        if(System.currentTimeMillis() - previouslySavedTime > 14000000)
        {
            mAd.loadAd(getResources().getString(R.string.AD_UNIT_ID), new AdRequest.Builder().build());
        }

    }



    private void showAdDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
        builder.setTitle("Contribute Us");
        builder.setMessage("Watch video in order to contribute Us. Thank you");
        builder.setCancelable(false);
        builder.setPositiveButton("Watch Video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAd.show();
            }
        });

        builder.show();
    }


    //////////////////////////////////////////////////////////////////////
    ///////////////////////VIDEO AD METHODS///////////////////////////////
    //////////////////////////////////////////////////////////////////////
    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(AllPreferences.getPreviouslySavedTime(),System.currentTimeMillis());
        editor.apply();
        editor.clear();
        onTabSelected(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////ON YEAR ITEM SELECTED//////////////////////////
    //////////////////////////////////////////////////////////////////////
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        if(position == 0)
        {
            return;
        }

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserGroupList())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .child(GlobalNames.getYEAR()).setValue(navBarYearSpinner.getSelectedItem().toString().replace(" ",""));

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AllPreferences.getYEAR(),navBarYearSpinner.getSelectedItem().toString());

        editor.apply();
        editor.clear();

        if(tabLayout.getSelectedTabPosition() == 1)
        {
            notice_fragment.yearChanged(true);
        }
        else
        {
            notice_fragment.yearChanged(false);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //////////////////////////////////////////////////////////////////////
    ///////////////////////SHOW SELECTED YEAR/////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void selectedYear(String year)
    {

        switch (year)
        {
            case "1ststd" :
                selectedYearPosition = 1;
                break;

            case "2ndstd" :
                selectedYearPosition = 2;
                break;

            case "3rdstd" :
                selectedYearPosition = 3;
                break;

            case "4thstd" :
                selectedYearPosition = 4;
                break;

            case "5thstd" :
                selectedYearPosition = 5;
                break;

            case "6thstd" :
                selectedYearPosition = 6;
                break;

            case "7thstd" :
                selectedYearPosition = 7;
                break;

            case "8thstd" :
                selectedYearPosition = 8;
                break;

            case "9thstd" :
                selectedYearPosition = 9;
                break;

            case "10thstd" :
                selectedYearPosition = 10;
                break;

            case "11thstd-Science" :
                selectedYearPosition = 11;
                break;

            case "11thstd-Commerce" :
                selectedYearPosition = 12;
                break;

            case "11thstd-Arts" :
                selectedYearPosition = 13;
                break;

            case "12thstd-Science" :
                selectedYearPosition = 14;
                break;

            case "12thstd-Commerce" :
                selectedYearPosition = 15;
                break;

            case "12thstd-Arts" :
                selectedYearPosition = 16;
                break;

            case "FirstYear" :
                selectedYearPosition = 1;
                break;

            case "SecondYear" :
                selectedYearPosition = 2;
                break;

            case "ThirdYear" :
                selectedYearPosition = 3;
                break;

            case "FourthYear" :
                selectedYearPosition = 4;
                break;

            case "FifthYear" :
                selectedYearPosition = 5;
                break;

            default:
                selectedYearPosition = 0;
                break;
        }
    }


    //////////////////////////////////////////////////////////////////////
    ///////////////////////GROUP LIST (MEMBER OF GROUPS)//////////////////
    //////////////////////////////////////////////////////////////////////
    private void getGroupList(){

        if(isGroupList && connectionCheck.checkingConnextion())
        {
            navBarMessageIV.setImageResource(R.mipmap.no_connection);
            navBarMessageTV.setText(getString(R.string.no_network));
            navBarMessageRefreshOrCreateGroupBTN.setText(R.string.refresh_now);
            navBarMessageLayout.setVisibility(View.VISIBLE);
            navBarMessageIV.setVisibility(View.VISIBLE);
            navBarMessageRefreshOrCreateGroupBTN.setVisibility(View.VISIBLE);
            navBarProgressbar.setVisibility(View.GONE);
            return;
        }


        FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserGroupList())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.exists() && isGroupList)
                        {
                            navBarMessageIV.setImageResource(R.drawable.ic_group_add_black_48dp);
                            navBarMessageTV.setText(getString(R.string.not_part_of_group));
                            navBarMessageRefreshOrCreateGroupBTN.setText(R.string.create_group);
                            navBarMessageJoinGroupBTN.setText(getString(R.string.join_group));
                            navBarMessageLayout.setVisibility(View.VISIBLE);
                            navBarMessageSeparatorLayout.setVisibility(View.VISIBLE);
                            navBarMessageJoinGroupBTN.setVisibility(View.VISIBLE);
                            navBarMessageIV.setVisibility(View.VISIBLE);
                            navBarMessageRefreshOrCreateGroupBTN.setVisibility(View.VISIBLE);
                            navBarMessageTV.setVisibility(View.VISIBLE);
                            navBarProgressbar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserGroupList())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if(dataSnapshot.exists())
                        {
                            getGroupProfile(dataSnapshot.getValue(UserGroupListClass.class));
                            userGroupDetail.add(dataSnapshot.getValue(UserGroupListClass.class));
                            isNewGroupMSGReceived.add(false);
                            navBarMessageLayout.setVisibility(View.GONE);
                            listenToPorts(dataSnapshot.getValue(UserGroupListClass.class));
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        for(int i = 0 ; i<userGroupList.size() ; i++)
                        {
                            if(dataSnapshot.getValue(UserGroupListClass.class).getGroup_id()
                                    .equals(userGroupList.get(i).getGroup_id()))
                            {
                                userGroupDetail.set(i,dataSnapshot.getValue(UserGroupListClass.class));
                                isNewGroupMSGReceived.set(i,false);

                                if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())
                                        .equals(dataSnapshot.getValue(UserGroupListClass.class).getGroup_id()))
                                {
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(AllPreferences.getYEAR(),userGroupDetail.get(i).getYear());
                                    editor.putString(AllPreferences.getFieldKey(),userGroupDetail.get(i).getField_key());
                                    selectedYear(userGroupDetail.get(i).getYear());
                                    editor.apply();
                                    editor.clear();

                                    listenToPorts(dataSnapshot.getValue(UserGroupListClass.class));

                                    notice_fragment.clearList();
                                    dataResourceFragment.clearList();

                                    drawerLayout.closeDrawers();

                                    if(tabLayout.getSelectedTabPosition() == 1)
                                    {
                                        notice_fragment.notice_selected();
                                    }
                                    else if(tabLayout.getSelectedTabPosition() == 2)
                                    {
                                        dataResourceFragment.dataResourceSelected();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        if(userGroupList.size() == 1)
                        {
                            userGroupList.clear();
                            userGroupDetail.clear();
                            userGroupListAdapter.notifyDataSetChanged();

                            if(isGroupList)
                            {
                                navBarMessageIV.setImageResource(R.mipmap.no_data);
                                navBarMessageTV.setText(getString(R.string.not_part_of_group));
                                navBarMessageRefreshOrCreateGroupBTN.setText(R.string.create_group);
                                navBarMessageJoinGroupBTN.setText(getString(R.string.join_group));
                                navBarMessageLayout.setVisibility(View.VISIBLE);
                                navBarMessageSeparatorLayout.setVisibility(View.VISIBLE);
                                navBarMessageJoinGroupBTN.setVisibility(View.VISIBLE);
                                navBarMessageIV.setVisibility(View.VISIBLE);
                                navBarMessageRefreshOrCreateGroupBTN.setVisibility(View.VISIBLE);
                                navBarProgressbar.setVisibility(View.GONE);
                            }

                            if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())
                                    .equals(dataSnapshot.getValue(UserGroupListClass.class).getGroup_id()))
                            {

                                navHeaderGroupName.setText("");
                                navHeaderGroupProfile.setImageResource(R.mipmap.create_group_profile);

                                SharedPreferences.Editor editor1 = sp.edit();
                                editor1.putString(AllPreferences.getGroupProfile(),GlobalNames.getNONE());
                                editor1.putString(AllPreferences.getGroupName(),GlobalNames.getNONE());
                                editor1.putString(AllPreferences.getGroupKey(),GlobalNames.getNONE());
                                editor1.putInt(AllPreferences.getGroupType(),0);
                                editor1.apply();
                                editor1.clear();

                                notice_fragment.clearList();
                                dataResourceFragment.clearList();

                                drawerLayout.closeDrawers();

                                if(tabLayout.getSelectedTabPosition() == 1)
                                {
                                    notice_fragment.notice_selected();
                                }
                                else if(tabLayout.getSelectedTabPosition() == 2)
                                {
                                    dataResourceFragment.dataResourceSelected();
                                }

                            }
                            return;
                        }

                      for(int i=0 ; i<userGroupList.size() ; i++)
                      {
                            if(userGroupList.get(i).getGroup_id().equals(dataSnapshot.getValue(UserGroupListClass.class).getGroup_id()))
                            {
                                userGroupList.remove(i);
                                userGroupDetail.remove(i);
                                isNewGroupMSGReceived.remove(i);
                                userGroupListAdapter.notifyDataSetChanged();

                                if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())
                                        .equals(dataSnapshot.getValue(UserGroupListClass.class).getGroup_id()))
                                {

                                    navHeaderGroupName.setText("");
                                    navHeaderGroupProfile.setImageResource(R.mipmap.create_group_profile);

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(AllPreferences.getGroupProfile(),GlobalNames.getNONE());
                                    editor.putString(AllPreferences.getGroupName(),GlobalNames.getNONE());
                                    editor.putString(AllPreferences.getGroupKey(),GlobalNames.getNONE());
                                    editor.putInt(AllPreferences.getGroupType(),0);
                                    editor.apply();
                                    editor.clear();

                                    notice_fragment.clearList();
                                    dataResourceFragment.clearList();

                                    drawerLayout.closeDrawers();

                                    if(tabLayout.getSelectedTabPosition() == 1)
                                    {
                                        notice_fragment.notice_selected();
                                    }
                                    else if(tabLayout.getSelectedTabPosition() == 2)
                                    {
                                        dataResourceFragment.dataResourceSelected();
                                    }

                                }
                                break;
                            }
                      }

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if(FirebaseAuth.getInstance().getCurrentUser() != null)
                        {
                            Toast.makeText(DashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            navBarProgressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    //////////////////////////////////////////////////////////////////////
    ///////////////////////GET GROUP PROFILES/////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void getGroupProfile(UserGroupListClass groupListClass){

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getGroupProfile()).child(groupListClass.getGroup_id());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.exists())
                    {
                        reference.removeEventListener(this);
                        return;
                    }

                    if(navBarUserGroupList.getAdapter() == null)
                    {
                        userGroupList.add(dataSnapshot.getValue(GroupProfile.class));

                        userGroupListAdapter = new UserGroupListAdapter(DashBoard.this,
                                userGroupList,
                                isNewGroupMSGReceived,
                                new UserGroupListAdapter.UserGroupListInterface() {

                            @Override
                            public void onGroupItemClicked(int position) {
                                onGroupListItemClicked(position);
                            }

                            @Override
                            public void overflowBTNClicked(int position,View view) {
                                onOverFlowBTNClicked(position,view);
                            }
                        });
                        navBarUserGroupList.setAdapter(userGroupListAdapter);
                    }
                    else
                    {
                        for(int i = 0 ; i < userGroupList.size() ; i++)
                        {

                            if(userGroupList.get(i).getGroup_id().equals(dataSnapshot.getValue(GroupProfile.class).getGroup_id()))
                            {
                                userGroupList.set(i,dataSnapshot.getValue(GroupProfile.class));
                                userGroupListAdapter.notifyItemChanged(i);

                                if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(userGroupList.get(i).getGroup_id()))
                                {
                                    navHeaderGroupName.setText(userGroupList.get(i).getGroup_name());
                                    Glide.with(DashBoard.this)
                                            .load(userGroupList.get(i).getGroup_profile_path())
                                            .into(navHeaderGroupProfile);
                                }

                                break;
                            }

                            if(i == userGroupList.size() - 1)
                            {
                                userGroupList.add(dataSnapshot.getValue(GroupProfile.class));
                                userGroupListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    navBarProgressbar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if(FirebaseAuth.getInstance().getCurrentUser() != null)
                    {
                        Toast.makeText(DashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////GET GROUP INFO IF INTCON GROUP/////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        final DatabaseReference reference_intcon = FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getIntconGroupProfile()).child(groupListClass.getGroup_id());

        reference_intcon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    reference.removeEventListener(this);
                    return;
                }

                if(navBarUserGroupList.getAdapter() == null)
                {
                    userGroupList.add(dataSnapshot.getValue(GroupProfile.class));

                    userGroupListAdapter = new UserGroupListAdapter(DashBoard.this,
                            userGroupList,
                            isNewGroupMSGReceived,
                            new UserGroupListAdapter.UserGroupListInterface() {

                        @Override
                        public void onGroupItemClicked(int position) {
                            onGroupListItemClicked(position);
                        }

                        @Override
                        public void overflowBTNClicked(int position,View view) {
                            onOverFlowBTNClicked(position,view);
                        }
                    });
                    navBarUserGroupList.setAdapter(userGroupListAdapter);
                }
                else
                {
                    for(int i = 0 ; i < userGroupList.size() ; i++)
                    {

                        if(userGroupList.get(i).getGroup_id().equals(dataSnapshot.getValue(GroupProfile.class).getGroup_id()))
                        {
                            userGroupList.set(i,dataSnapshot.getValue(GroupProfile.class));
                            userGroupListAdapter.notifyItemChanged(i);

                            if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(userGroupList.get(i).getGroup_id()))
                            {
                                navHeaderGroupName.setText(userGroupList.get(i).getGroup_name());
                                Glide.with(DashBoard.this).load(userGroupList.get(i).getGroup_profile_path())
                                        .into(navHeaderGroupProfile);
                            }

                            break;
                        }

                        if(i == userGroupList.size() - 1)
                        {
                            userGroupList.add(dataSnapshot.getValue(GroupProfile.class));
                            userGroupListAdapter.notifyDataSetChanged();
                        }
                    }
                }

                navBarProgressbar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null)
                {
                    Toast.makeText(DashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //////////////////////////////////////////////////////////////////////
    ////CHECK GROUP ID AVAILABLE OR NOT (FALSE WHEN USER LOGIN)///////////
    //////////////////////////////////////////////////////////////////////
    private void checkSharedPreferenceOrNot(){

        if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
        {
            drawerLayout.openDrawer(GravityCompat.START);
            isGroupList = true;
            navigationView.getMenu().clear();
            navBarProgressbar.setVisibility(View.VISIBLE);
            navBarUserGroupList.setVisibility(View.VISIBLE);
        }
        else
        {

            drawerLayout.closeDrawers();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notice_fragment.notice_selected();
                }
            },1000);

            /////////////////////////////////////////////////////////////////////////////////
            ///////IF GROUP TYPE IS NOT INTCON GROUP SET HEADER GROUP PROFILE AND NAME///////
            ////////////////////////////////////////////////////////////////////////////////
            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getGroupProfile())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {
                                navHeaderGroupName.setText(dataSnapshot.getValue(GroupProfile.class).getGroup_name());
                                Glide.with(DashBoard.this)
                                        .load(dataSnapshot.getValue(GroupProfile.class).getGroup_profile_path())
                                        .into(navHeaderGroupProfile);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            /////////////////////////////////////////////////////////////////////////////////
            ///////IF GROUP TYPE IS INTCON GROUP SET HEADER GROUP PROFILE AND NAME///////
            ////////////////////////////////////////////////////////////////////////////////
            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getIntconGroupProfile())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {
                                navHeaderGroupName.setText(dataSnapshot.getValue(GroupProfile.class).getGroup_name());
                                Glide.with(DashBoard.this)
                                        .load(dataSnapshot.getValue(GroupProfile.class).getGroup_profile_path())
                                        .into(navHeaderGroupProfile);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            if(sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getPrivateGroup())
            {
                navigationView.getMenu().findItem(R.id.navbar_menu_change_year).setVisible(false);
            }
            else if(sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getPrivateInstituteSchool() ||
                    sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getSCHOOL())
            {
                navBarSpinnerYear = getResources().getStringArray(R.array.school_year_navbar_array_no_none);
                navBarYearSpinner = (Spinner) navigationView.getMenu().findItem(R.id.navbar_menu_change_year).getActionView();
                navBarYearSpinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,navBarSpinnerYear));
                navBarYearSpinner.setSelection(selectedYearPosition,false);
                navBarYearSpinner.setOnItemSelectedListener(this);
            }
            else if(sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getUniversityOrCollege())
            {
                navBarSpinnerYear = getResources().getStringArray(R.array.year_navbar_array);
                navBarYearSpinner = (Spinner) navigationView.getMenu().findItem(R.id.navbar_menu_change_year).getActionView();
                navBarYearSpinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,navBarSpinnerYear));
                navBarYearSpinner.setSelection(selectedYearPosition,false);
                navBarYearSpinner.setOnItemSelectedListener(this);
            }
            else
            {
                navigationView.getMenu().findItem(R.id.navbar_menu_change_year).setVisible(false);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////////GET USER PROFILE///////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void setNavigationHeaderImage() {

        Random random = new Random();
        int randInt = random.nextInt(3);

        switch (randInt)
        {
            case 0:
                navigationHeaderParentLayout.setBackgroundResource(R.mipmap.back_ground_navigation_header);
                break;

            case 1:
                navigationHeaderParentLayout.setBackgroundResource(R.mipmap.back_ground_navigation_header_two);
                break;

            default:
                navigationHeaderParentLayout.setBackgroundResource(R.mipmap.back_ground_navigation_header_three);
                break;
        }

    }


    //////////////////////////////////////////////////////////////////////
    ///////////////////////GET USER PROFILE///////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void getUserProfile(){

            FirebaseDatabase.getInstance().getReference()
                    .getRoot().child(GlobalNames.getUserGroupDetail())
                    .child(GlobalNames.getUserProfile())
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {

                                navHeaderUserName.setText(dataSnapshot.getValue(UserProfile.class).getUserName());
                                Glide.with(getApplicationContext())
                                        .load(dataSnapshot.getValue(UserProfile.class).getUserProfile())
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(navHeaderUserProfile);

                                userProfilePath = dataSnapshot.getValue(UserProfile.class).getUserProfile();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    //////////////////////////////////////////////////////////////////////
    /////////////////WHEN USER CLICKS ON ANY GROUP ITEM///////////////////
    //////////////////////////////////////////////////////////////////////
    private void onGroupListItemClicked(final int position)
    {
        if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(userGroupList.get(position).getGroup_id()))
        {
            Toast.makeText(DashBoard.this, "Already Selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if(connectionCheck.checkingConnextion())
        {
            Toast.makeText(DashBoard.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        fab.hide();
        notice_fragment.hideCardView();

        if(isNewGroupMSGReceived.get(position))
        {
            isNewGroupMSGReceived.set(position,false);
            userGroupListAdapter.notifyItemChanged(position);
        }

        Glide.with(this).load(userGroupList.get(position).getGroup_profile_path()).into(navHeaderGroupProfile);
        navHeaderGroupName.setText(userGroupList.get(position).getGroup_name());

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AllPreferences.getGroupKey(),userGroupList.get(position).getGroup_id());
        editor.apply();
        editor.clear();

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getGroupList())
                .child(userGroupList.get(position).getGroup_id())
                .child(GlobalNames.getGroupType())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        SharedPreferences.Editor editor = sp.edit();

                        if(dataSnapshot.exists())
                        {
                            group_type = dataSnapshot.getValue(Integer.class);
                        }
                        else
                        {
                            group_type = GlobalNames.getPrivateGroup();
                        }

                        if(group_type == GlobalNames.getPrivateGroup())
                        {
                            editor.putInt(AllPreferences.getGroupType(),group_type);
                            editor.putString(AllPreferences.getYEAR(),GlobalNames.getDummyNode());
                            editor.putString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode());
                            editor.apply();
                            editor.clear();

                            notice_fragment.clearList();
                            dataResourceFragment.clearList();
                            drawerLayout.closeDrawers();

                            if(!sp.getBoolean(AllPreferences.getIsAdmin(),false) ||
                                    tabLayout.getSelectedTabPosition() == 0 ||
                                    tabLayout.getSelectedTabPosition() == 3)
                            {
                                fab.hide();
                            }

                            if(tabLayout.getSelectedTabPosition() == 1)
                            {
                                toolbar.getMenu().clear();
                                toolbar.inflateMenu(R.menu.upload_download);
                                notice_fragment.setOptionMenu();
                                notice_fragment.notice_selected();
                            }
                            else if(tabLayout.getSelectedTabPosition() == 2)
                            {
                                toolbar.getMenu().clear();
                                toolbar.inflateMenu(R.menu.upload_download);
                                dataResourceFragment.dataResourceSelected();
                            }
                        }
                        else
                        {
                            editor.putString(AllPreferences.getYEAR(),userGroupDetail.get(position).getYear());
                            editor.putString(AllPreferences.getFieldKey(),userGroupDetail.get(position).getField_key());
                            editor.putInt(AllPreferences.getGroupType(),group_type);
                            editor.apply();
                            editor.clear();
                            selectedYear(userGroupDetail.get(position).getYear());

                            notice_fragment.clearList();
                            dataResourceFragment.clearList();

                            drawerLayout.closeDrawers();

                            if(tabLayout.getSelectedTabPosition() == 1)
                            {
                                toolbar.getMenu().clear();
                                toolbar.inflateMenu(R.menu.upload_download);
                                notice_fragment.setOptionMenu();
                                notice_fragment.notice_selected();
                            }
                            else if(tabLayout.getSelectedTabPosition() == 2)
                            {
                                toolbar.getMenu().clear();
                                toolbar.inflateMenu(R.menu.upload_download);
                                dataResourceFragment.dataResourceSelected();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userGroupList.get(position).getGroup_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.getKey().equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                        {
                            return;
                        }

                        if(dataSnapshot.exists())
                        {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean(AllPreferences.getIsAdmin(),dataSnapshot.getValue(Boolean.class));
                            editor.apply();
                            editor.clear();

                            if(dataSnapshot.getValue(Boolean.class) && (group_type != GlobalNames.getPrivateGroup() ||
                                    (group_type == GlobalNames.getPrivateGroup() &&
                                            (tabLayout.getSelectedTabPosition() == 1 || tabLayout.getSelectedTabPosition() == 2))) )
                            {
                                fab.show();
                            }
                            else
                            {
                                fab.hide();
                            }
                        }
                        else
                        {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean(AllPreferences.getIsAdmin(),false);
                            editor.apply();
                            editor.clear();
                            fab.hide();
                        }

                        Intent RTReturn = new Intent(GroupMembers.AUTH_CHANGED);
                        LocalBroadcastManager.getInstance(DashBoard.this).sendBroadcast(RTReturn);

                        if(tabLayout.getSelectedTabPosition() == 1)
                        {
                            notice_fragment.setOptionMenu();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //////////////////////////////////////////////////////////////////////
    /////////////USER GROUP LIST OVERFLOW MENU CLICKED/////////////////////
    //////////////////////////////////////////////////////////////////////
    private void onOverFlowBTNClicked(final int position, final View view)
    {

        PopupMenu popupMenu = new PopupMenu(this,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.user_group_detail_popup_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.popmenu_remove_group :

                        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to remove this group?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getGroupDetail())
                                        .child(GlobalNames.getGroupMemberList())
                                        .child(userGroupDetail.get(position).getGroup_id())
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getGroupDetail())
                                        .child(GlobalNames.getGroupMemberCount())
                                        .child(userGroupDetail.get(position).getGroup_id())
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getUserGroupDetail())
                                        .child(GlobalNames.getUserAuthentication())
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(userGroupDetail.get(position).getGroup_id())
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getUserGroupDetail())
                                        .child(GlobalNames.getUserGroupList())
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(userGroupDetail.get(position).getGroup_id())
                                        .removeValue();

                                if(userGroupDetail.get(position).getGroup_id()
                                        .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                                {
                                    navHeaderGroupName.setText("");
                                    navHeaderGroupProfile.setImageResource(R.mipmap.create_group_profile);

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(AllPreferences.getGroupProfile(),GlobalNames.getNONE());
                                    editor.putString(AllPreferences.getGroupName(),GlobalNames.getNONE());
                                    editor.putString(AllPreferences.getGroupKey(),GlobalNames.getNONE());
                                    editor.putInt(AllPreferences.getGroupType(),0);
                                    editor.apply();
                                    editor.clear();

                                    notice_fragment.clearList();
                                    dataResourceFragment.clearList();

                                    drawerLayout.closeDrawers();

                                    if(tabLayout.getSelectedTabPosition() == 1)
                                    {
                                        notice_fragment.notice_selected();
                                    }
                                    else if(tabLayout.getSelectedTabPosition() == 2)
                                    {
                                        dataResourceFragment.dataResourceSelected();
                                    }
                                }
                            }
                        });

                        builder.setNegativeButton("NO",null);

                        builder.show();

                        return  true;

                    case R.id.popmenu_group_detail :

                        drawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(DashBoard.this, GroupInfo.class);
                                intent.putExtra(getString(R.string.group_info_group_id),userGroupList.get(position).getGroup_id());
                                startActivity(intent);
                            }
                        },175);
                        return true;

                    default:
                        return false;
                }

            }
        });

    }

    //////////////////////////////////////////////////////////////////////
    /////////////////////ON NAVIGATION ITEM SELECTED//////////////////////
    //////////////////////////////////////////////////////////////////////
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.navbar_menu_requests :

                if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
                {
                    Snackbar.make(getWindow().getDecorView().getRootView(),"You haven't choose any group yet.",Snackbar.LENGTH_SHORT).show();
                    return true;
                }

                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DashBoard.this, UserRequest.class));
                    }
                },175);

                return true;

            case R.id.navbar_menu_group_members :

                if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
                {
                    Snackbar.make(getWindow().getDecorView().getRootView(),"You haven't choose any group yet.",Snackbar.LENGTH_SHORT).show();
                    return true;
                }

                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DashBoard.this, GroupMembers.class));
                    }
                },175);
                return true;

            case R.id.navbar_menu_about_group :

                if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
                {
                    Snackbar.make(getWindow().getDecorView().getRootView(),"You haven't choose any group yet.",Snackbar.LENGTH_SHORT).show();
                    return true;
                }


                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DashBoard.this, GroupInfo.class));
                    }
                },175);
                return true;

            case R.id.navbar_menu_create_group :
                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DashBoard.this, CreateGroup.class));
                    }
                },175);
                return true;

            case R.id.navbar_menu_join_group :
                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DashBoard.this, JoinGroup.class));
                    }
                },175);
                return true;

            case R.id.navbar_menu_sent_requests :

                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(DashBoard.this, SentRequest.class);
                        intent.putExtra(getString(R.string.sent_request_intent),userRequestKeyList);
                        startActivity(intent);
                    }
                },175);

            return true;

            case R.id.navbar_menu_help_and_feedback :
                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DashBoard.this, BugReport.class));
                    }
                },175);
                return true;

            case R.id.navbar_menu_privacy_policy :
                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DashBoard.this, PrivacyPolicy.class));
                    }
                },175);
                return true;

            case R.id.navbar_menu_faq :
                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DashBoard.this, FAQ.class));
                    }
                },175);
                return true;

            case R.id.navbar_menu_logout :

                SharedPreferences.Editor editor = sp.edit();

                editor.putString(AllPreferences.getGroupKey(),GlobalNames.getNONE());
                editor.putString(AllPreferences.getYEAR(),GlobalNames.getDummyNode());
                editor.putString(AllPreferences.getGroupProfile(),GlobalNames.getNONE());
                editor.putString(AllPreferences.getUserProfile(),GlobalNames.getNONE());
                editor.putString(AllPreferences.getUSERNAME(),GlobalNames.getUserName());
                editor.putString(AllPreferences.getGroupName(),GlobalNames.getNONE());
                editor.putString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode());
                editor.putString(AllPreferences.getEMAIL(),GlobalNames.getNONE());
                editor.putString(AllPreferences.getUID(),GlobalNames.getNONE());

                editor.putBoolean(AllPreferences.getIsAdmin(),false);
                editor.putInt(AllPreferences.getGroupType(),0);


                editor.apply();
                editor.clear();

                if (AccessToken.getCurrentAccessToken() != null)
                {
                    LoginManager.getInstance().logOut();
                }

                if(googleApiClient != null && googleApiClient.isConnected())
                {
                    Auth.GoogleSignInApi.signOut(googleApiClient);
                }

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DashBoard.this,MainActivity.class));
                DatabaseReference.goOffline();
                finish();

                return true;

            default:
                return false;
        }
    }

    //////////////////////////////////////////////////////////////////////
    /////////////////////Listen For New Notice////////////////////////////
    //////////////////////////////////////////////////////////////////////
    private void listenToPorts(final UserGroupListClass userGroupListClass)
    {
        final DatabaseReference noticeForGroupFieldYear =
                FirebaseDatabase.getInstance().getReference().getRoot()
                        .child(GlobalNames.getNOTICE())
                        .child(GlobalNames.getNoticeMsg())
                        .child(userGroupListClass.getGroup_id())
                        .child(userGroupListClass.getField_key())
                        .child(userGroupListClass.getYear());

        noticeForGroupFieldYear.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.exists())
                {

                    for(int i=0 ; i<userGroupDetail.size() ; i++)
                    {

                        NoticeListProvider noticeListProvider;

                        try
                        {
                            noticeListProvider = dataSnapshot.getValue(NoticeListProvider.class);
                        }
                        catch (Exception e)
                        {
                            return;
                        }

                        if(
                                (dataSnapshot.getKey()
                                        .compareTo(sp.getString(userGroupDetail.get(i).getGroup_id(),GlobalNames.getNONE())) > 0 ||

                                sp.getString(userGroupDetail.get(i).getGroup_id(),GlobalNames.getNONE())
                                        .equals(GlobalNames.getNONE())) &&

                                userGroupDetail.get(i).getGroup_id()
                                        .equals(dataSnapshot.getRef().getParent().getParent().getParent().getKey()) &&

                                userGroupDetail.get(i).getField_key()
                                        .equals(dataSnapshot.getRef().getParent().getParent().getKey()) &&

                                userGroupDetail.get(i).getYear()
                                        .equals(dataSnapshot.getRef().getParent().getKey()) &&

                                (!sp.getString(userGroupDetail.get(i).getGroup_id(),GlobalNames.getNONE())
                                        .equals(noticeListProvider.getKey()))

                                )
                        {

                            if(dataSnapshot.getRef().getParent().getParent().getParent().getKey()
                                    .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                            {
                                notice_fragment.newNoticeAvailable("New Notice");
                            }
                            else
                            {
                                playSound.play();
                                isNewGroupMSGReceived.set(i,true);
                                userGroupListAdapter.notifyItemChanged(i);
                            }

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(userGroupDetail.get(i).getGroup_id(),noticeListProvider.getKey());
                            editor.apply();
                            editor.clear();

                            break;

                        }
                    }
                }
                else
                {
                    noticeForGroupFieldYear.removeEventListener(this);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                noticeForGroupFieldYear.removeEventListener(this);
            }
        });


        final DatabaseReference noticeForGroupInstitute =
                FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getNOTICE()).child(GlobalNames.getNoticeMsg())
                .child(userGroupListClass.getGroup_id())
                .child(GlobalNames.getInstitute());

        noticeForGroupInstitute.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if(dataSnapshot.exists())
                {

                    NoticeListProvider noticeListProvider;

                    try
                    {
                        noticeListProvider = dataSnapshot.getValue(NoticeListProvider.class);
                    }
                    catch (ClassCastException e)
                    {
                        return;
                    }

                    for(int i=0 ; i<userGroupDetail.size() ; i++)
                    {
                        if(
                            (       dataSnapshot.getKey()
                                        .compareTo(sp.getString(userGroupDetail.get(i).getGroup_id() + GlobalNames.getInstitute() ,GlobalNames.getNONE())) > 0 ||

                                    sp.getString(userGroupDetail.get(i).getGroup_id() + GlobalNames.getInstitute() ,GlobalNames.getNONE())
                                        .equals(GlobalNames.getNONE())
                            ) &&

                            userGroupDetail.get(i).getGroup_id()
                                .equals(dataSnapshot.getRef().getParent().getParent().getKey()) &&

                            (!sp.getString(userGroupDetail.get(i).getGroup_id() + GlobalNames.getInstitute() ,GlobalNames.getNONE())
                                    .equals(noticeListProvider.getKey()))
                           )
                        {

                            if(dataSnapshot.getRef().getParent().getParent().getKey()
                                    .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                            {
                                notice_fragment.newNoticeAvailable("New Institute Notice");
                            }
                            else
                            {
                                playSound.play();
                                isNewGroupMSGReceived.set(i,true);
                                userGroupListAdapter.notifyItemChanged(i);
                            }

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(userGroupDetail.get(i).getGroup_id() + GlobalNames.getInstitute(),noticeListProvider.getKey());
                            editor.apply();
                            editor.clear();
                            break;
                        }
                    }
                }
                else
                {
                    noticeForGroupInstitute.removeEventListener(this);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                noticeForGroupInstitute.removeEventListener(this);
            }
        });
    }
}
