package com.myapp.unknown.iNTCON.Acheivement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.facebook.share.model.ShareLinkContent;
import com.myapp.unknown.iNTCON.Campaign.KeysAndGroupKeys;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.DashBoard;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.NavigationView.SearchGroupAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Achievement_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView achievement_list_rv;
    private AchivementListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar,load_data_progressBar;

    private DatabaseReference root;

    private DatabaseReference achievement_msg_ref;

    private SharedPreferences sp;

    private boolean isEarlierDataLoading = false;
    private boolean isAnyDataLoading = false;

    static boolean isLongPressed;

    private static int olderSize;

    private boolean isMyAchievement,isGroupAchievement;
    private int selectedGroupType;
    private String selectedGroupKey;

    private static String newest_key,oldest_key;

    private static int btn_clicked_position;
    private int countOptionMenu = 0 ;
    private int countForGroupAndMyACH = 0;

    private ImageView achievementMessageIV;
    private TextView achievementMessageTV;
    private LinearLayout achievementMessageLayout;

    private String achievement;

    private RequestManager requestManager;

    private static String selectedGroup = "All";

    static ArrayList<Boolean> wishedToDelete;

    private AlertDialog alertDialog;

    private ArrayList<AchievementListProvider> dataToBeDeleted;
    private ArrayList<GroupProfile> groupProfileArrayList;
    private ArrayList<AchievementListProvider> list ;
    private ArrayList<Boolean> likeArrayList;
    private ArrayList<String> myACHKeys;
    private ArrayList<String> keysPosition;
    private ArrayList<String> groupKeyList;
    private ArrayList<GroupProfile> dialogGroupProfile;

    private Menu menu_items;

    private ImageSaver imageSaver;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private DialogHandler dialogHandler;

    public Achievement_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_achievement_, container, false);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(getActivity());

        achievement_list_rv = (RecyclerView) view.findViewById(R.id.rv_acheivement);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.acheivement_refreshlayout);
        progressBar = (ProgressBar) view.findViewById(R.id.ach_progressBar);
        load_data_progressBar = (ProgressBar)view.findViewById(R.id.ach_fragment_progressBar);

        achievementMessageIV = (ImageView)view.findViewById(R.id.achievement_message_iv);
        achievementMessageTV = (TextView)view.findViewById(R.id.achievement_message_tv);
        achievementMessageLayout = (LinearLayout) view.findViewById(R.id.achievement_message_layout);
        Button achievementMessageRefreshBTN = (Button) view.findViewById(R.id.achievement_message_refresh);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setDistanceToTriggerSync(50);
        refreshLayout.setColorSchemeColors(Color.rgb(25,118,210));
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        requestManager = Glide.with(getActivity());

        olderSize = 0;

        getActivity();
        sp = getActivity().getSharedPreferences(AllPreferences.getPreferenceName(), Context.MODE_PRIVATE);

        achievement = GlobalNames.getACHIEVEMENT();

        achievement_list_rv.setHasFixedSize(true);
        achievement_list_rv.setLayoutManager(linearLayoutManager);


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////ON REFRESH BUTTON CLICKED/////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        achievementMessageRefreshBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isGroupAchievement || isMyAchievement)
                {
                    getMyACHInitialKeys();
                }
                else
                {
                    loadInitialData();
                }

            }
        });

        achievement_list_rv.setItemAnimator(null);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////HANDLE SCROLL LISTNER :: AUTOMATIC LOAD ON REACHING LAST ITEM///////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        achievement_list_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!recyclerView.canScrollVertically(1) &&
                        !list.isEmpty() &&
                        !isLongPressed &&
                        !isEarlierDataLoading)
                {
                    isEarlierDataLoading = true;

                    if(isMyAchievement || isGroupAchievement)
                    {
                        myACHEarlierKeys();
                    }
                    else
                    {
                        loadEarlierData();
                    }
                }
            }
        });

        if(savedInstanceState != null  && likeArrayList != null && list.size() == likeArrayList.size()){

            list = new ArrayList<>();
            groupProfileArrayList = new ArrayList<>();
            likeArrayList = new ArrayList<>();

            list = savedInstanceState.getParcelableArrayList("PARCEL");
            adapter = new AchivementListAdapter(getActivity(),
                    list,
                    likeArrayList,
                    groupProfileArrayList,
                    requestManager,
                    new AchivementListAdapter.AchievementInterface() {
                        @Override
                        public void achievementLike(boolean isChecked, int position) {
                            handlelikedOrNot(isChecked,position);
                        }

                        @Override
                        public void achievementBTN(int position) {
                            handleBtnClicked(position);
                        }

                        @Override
                        public void gplusClicked(Bitmap bitmap, int position) {
                            signedInWithGPlus(bitmap,position);
                        }

                        @Override
                        public void fbClicked(int position) {
                            shareWithFB(position);
                        }

                        @Override
                        public void onLongClicked(int position,boolean isChecked) {
                            handleOnLongClicked(position,isChecked);
                        }

                        @Override
                        public void deleteItem(int position, boolean isChecked) {
                            handleDeleteItem(position,isChecked);
                        }
                    });


            achievement_list_rv.setAdapter(adapter);
        }
        else
        {
            root = FirebaseDatabase.getInstance().getReference().getRoot();
        }
        setHasOptionsMenu(true);

        return view;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON SAVED INSTSNACE STATE//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(list != null){
            outState.putParcelableArrayList("PARCEL",list);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////PREPARE OPTION MENU :: DIFFERENT ACTION OPTION MENU FOR DIFFERENT FRAGMENT///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.uploads_menu).setVisible(false);
        menu.findItem(R.id.downloads_menu).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////ON CREATE OPTION MENU///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu_items = menu;
        getActivity().getMenuInflater().inflate(R.menu.camp_achi_menu,menu_items);

        menu_items.findItem(R.id.sort_by_group).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if(!isGroupAchievement && !isMyAchievement)
        {
            menu_items.findItem(R.id.sort_by_group).setVisible(false);
        }
        else
        {
            if(selectedGroupKey.equals(GlobalNames.getNONE()))
            {
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
            }
            else
            {
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////OPTION ITEM SELECTED////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(isAnyDataLoading)
        {
            Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
            return true;
        }

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());

        if(connectionCheck.checkingConnextion() && (list == null || list.size() == 0))
        {
            achievementMessageLayout.setVisibility(View.VISIBLE);
            achievementMessageIV.setImageResource(R.mipmap.no_connection);
            achievementMessageTV.setText(getString(R.string.no_network));
            return true;
        }

        switch (item.getItemId())
        {
            case R.id.sort_by_group :
                getGroupKeysFromDataBase();
                return true;

            case R.id.camp_ach_all :

                if(!isMyAchievement &&
                        !isGroupAchievement &&
                        list != null &&
                        list.size() != 0)
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                //////////////////////////////////////
                //CLEAR LIST ON OPTION ITEM SELECTED//
                //////////////////////////////////////

                list.clear();
                likeArrayList.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyAchievement = false;
                isGroupAchievement = false;
                selectedGroup = "All";
                menu_items.findItem(R.id.sort_by_group).setVisible(false);
                achievement_selected();
                return true;

            case R.id.camp_ach_academy :

                if(!isMyAchievement &&
                        isGroupAchievement &&
                        selectedGroupType == GlobalNames.getUniversityOrCollege() &&
                        list != null &&
                        list.size() !=0 &&
                        selectedGroupKey.equals(GlobalNames.getNONE()))
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                list.clear();
                likeArrayList.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyAchievement = false;
                isGroupAchievement = true;
                selectedGroupType = GlobalNames.getUniversityOrCollege();
                selectedGroupKey = GlobalNames.getNONE();
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
                selectedGroup = "All";
                achievement_selected();
                return true;

            case R.id.camp_ach_private_academy :

                if(!isMyAchievement &&
                        isGroupAchievement &&
                        selectedGroupType == GlobalNames.getPrivateInstituteClg() &&
                        list != null &&
                        list.size() !=0 &&
                        selectedGroupKey.equals(GlobalNames.getNONE()))
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                list.clear();
                likeArrayList.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyAchievement = false;
                isGroupAchievement = true;
                selectedGroupType = GlobalNames.getPrivateInstituteClg();
                selectedGroupKey = GlobalNames.getNONE();
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
                selectedGroup = "All";
                achievement_selected();
                return true;

            case R.id.camp_ach_school :

                if(!isMyAchievement &&
                        isGroupAchievement &&
                        selectedGroupType == GlobalNames.getSCHOOL() &&
                        list != null &&
                        list.size() !=0 &&
                        selectedGroupKey.equals(GlobalNames.getNONE()))
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                list.clear();
                likeArrayList.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyAchievement = false;
                isGroupAchievement = true;
                selectedGroupType = GlobalNames.getSCHOOL();
                selectedGroupKey = GlobalNames.getNONE();
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
                selectedGroup = "All";
                achievement_selected();
                return true;

            case R.id.camp_ach_private_school :

                if(!isMyAchievement &&
                        isGroupAchievement &&
                        selectedGroupType == GlobalNames.getPrivateInstituteSchool() &&
                        list != null &&
                        list.size() !=0 &&
                        selectedGroupKey.equals(GlobalNames.getNONE()))
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                list.clear();
                likeArrayList.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyAchievement = false;
                isGroupAchievement = true;
                selectedGroupType = GlobalNames.getPrivateInstituteSchool();
                selectedGroupKey = GlobalNames.getNONE();
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
                selectedGroup = "All";
                achievement_selected();
                return true;

            default : return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    //////ACTIVITY FOR RESULT//////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GlobalNames.getGooglePlusActivityResult())
        {
            imageSaver.deleteFile();
        }

        getActivity();
        if(resultCode == Activity.RESULT_OK && requestCode == GlobalNames.getAchDetailToAchResultCode())
        {
            boolean returnedLiked = data.getBooleanExtra("isChecked",false);
            likeArrayList.set(btn_clicked_position,returnedLiked);
            adapter.notifyDataSetChanged();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////IF EXISTS THAN LOAD DATA//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadInitialData(){

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());

        if(connectionCheck.checkingConnextion())
        {
            achievementMessageLayout.setVisibility(View.VISIBLE);
            achievementMessageIV.setImageResource(R.mipmap.no_connection);
            achievementMessageTV.setText(getString(R.string.no_network));
        return;
        }

        load_data_progressBar.setVisibility(View.VISIBLE);
        setTitle();
        isAnyDataLoading = true;

        list = new ArrayList<>();
        likeArrayList = new ArrayList<>();
        groupProfileArrayList = new ArrayList<>();

        achievement_msg_ref = root.child(GlobalNames.getACHIEVEMENT())
                .child(GlobalNames.getAchievementAll());

        achievement_msg_ref.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if(!iterator.hasNext()){
                    load_data_progressBar.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                    achievementMessageLayout.setVisibility(View.VISIBLE);
                    achievementMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    achievementMessageTV.setText(getString(R.string.no_data));
                    isAnyDataLoading = false;
                    return;
                }

                achievementMessageLayout.setVisibility(View.GONE);

                while (iterator.hasNext()){
                    list.add((iterator.next()).getValue(AchievementListProvider.class));
                    groupProfileArrayList.add(new GroupProfile());
                    likeArrayList.add(false);
                }

                adapter = new AchivementListAdapter(getActivity(),
                        list,
                        likeArrayList,
                        groupProfileArrayList,
                        requestManager,
                        new AchivementListAdapter.AchievementInterface() {
                                @Override
                                public void achievementLike(boolean isChecked, int position) {
                                    handlelikedOrNot(isChecked,position);
                                }

                                @Override
                                public void achievementBTN(int position) {
                                    handleBtnClicked(position);
                                }

                                @Override
                                public void gplusClicked(Bitmap bitmap, int position) {
                                    signedInWithGPlus(bitmap,position);
                                }

                                @Override
                                public void fbClicked(int position) {
                                   shareWithFB(position);
                                }

                                @Override
                                public void onLongClicked(int position, boolean isChecked) {
                                    handleOnLongClicked(position,isChecked);
                                }

                                @Override
                                public void deleteItem(int position, boolean isChecked) {
                                    handleDeleteItem(position,isChecked);
                                }
                            });

                achievement_list_rv.setAdapter(adapter);

                getLikedOrNot(0,0,false);
                getGroupProfile(0,0,false);
                getLikeCount(0,0,false);

                load_data_progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                isAnyDataLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////LOAD EARLIER DATA///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadEarlierData(){

        if(list.size() == 0){
            loadInitialData();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        refreshLayout.setPadding(0,0,0,35);

        String key = list.get(0).getKey();

        isEarlierDataLoading = true;
        isAnyDataLoading = true;

        achievement_msg_ref
                .orderByChild("key")
                .limitToLast(11)
                .endAt(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    load_data_progressBar.setVisibility(View.GONE);
                    isEarlierDataLoading = false;
                    isAnyDataLoading = false;
                    refreshLayout.setPadding(0,0,0,0);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if(dataSnapshot.getChildrenCount() == 1)
                {
                    load_data_progressBar.setVisibility(View.GONE);
                    isEarlierDataLoading = false;
                    isAnyDataLoading = false;
                    refreshLayout.setPadding(0,0,0,0);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                olderSize = list.size();

                ArrayList<AchievementListProvider> listProviders = new ArrayList<>();
                ArrayList<GroupProfile> profiles = new ArrayList<>();
                ArrayList<Boolean> booleen = new ArrayList<>();

                while (iterator.hasNext()){
                    listProviders.add((iterator.next()).getValue(AchievementListProvider.class));
                    profiles.add(new GroupProfile());
                    booleen.add(false);
                }

                if(list.get(0).getKey().equals(listProviders.get(listProviders.size() - 1).getKey()))
                {
                    if(dataSnapshot.getChildrenCount() == 1)
                    {
                        load_data_progressBar.setVisibility(View.GONE);
                        isEarlierDataLoading = false;
                        isAnyDataLoading = false;
                        refreshLayout.setPadding(0,0,0,0);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    else if(dataSnapshot.getChildrenCount() > 1)
                    {
                        listProviders.remove(listProviders.size() - 1);
                        profiles.remove(profiles.size() - 1);
                        booleen.remove(booleen.size() - 1);
                    }
                }

                list.addAll(0,listProviders);
                groupProfileArrayList.addAll(0,profiles);
                likeArrayList.addAll(0,booleen);

                load_data_progressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                adapter.notifyItemRangeInserted(0,list.size() - olderSize);
                getLikedOrNot(olderSize,0,false);
                getGroupProfile(olderSize,0,false);
                getLikeCount(olderSize,0,false);
                isEarlierDataLoading = false;
                isAnyDataLoading = false;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////GET MY ACH INITIAL KEY//////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getMyACHInitialKeys(){

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());

        if(connectionCheck.checkingConnextion())
        {
            achievementMessageLayout.setVisibility(View.VISIBLE);
            achievementMessageIV.setImageResource(R.mipmap.no_connection);
            achievementMessageTV.setText(getString(R.string.no_network));
            return;
        }

        setTitle();
        isAnyDataLoading = true;

        load_data_progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference my_ach_keys;

        if(isMyAchievement)
        {
            my_ach_keys = root.child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getMyAchievement())
                    .child(selectedGroupKey);
        }
        else
        {
            my_ach_keys = root.child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getGroupAchievement())
                    .child(String.valueOf(selectedGroupType));

        }

        myACHKeys = new ArrayList<>();

        my_ach_keys.limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    my_ach_keys.removeEventListener(this);
                    load_data_progressBar.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                    achievementMessageLayout.setVisibility(View.VISIBLE);
                    achievementMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    achievementMessageTV.setText(getString(R.string.no_data));
                    isAnyDataLoading = false;
                    return;
                }

                achievementMessageLayout.setVisibility(View.GONE);

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()) {
                    myACHKeys.add(iterator.next().getValue(KeysAndGroupKeys.class).getKey());
                }

                newest_key = myACHKeys.get(myACHKeys.size() - 1);
                oldest_key = myACHKeys.get(0);

                loadInitialMyACH();
                my_ach_keys.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////LOAD INITIAL MY ACH//////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadInitialMyACH(){

        list = new ArrayList<>();
        likeArrayList = new ArrayList<>();
        groupProfileArrayList = new ArrayList<>();
        olderSize = 0;
        countForGroupAndMyACH = 0;

        for (int i=0;i<myACHKeys.size();i++)
        {

            achievement_msg_ref.child(myACHKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    list.add(dataSnapshot.getValue(AchievementListProvider.class));
                    groupProfileArrayList.add(new GroupProfile());
                    likeArrayList.add(false);
                    countForGroupAndMyACH++;

                    if(countForGroupAndMyACH == myACHKeys.size())
                    {

                        adapter = new AchivementListAdapter(getActivity(),
                                list,
                                likeArrayList,
                                groupProfileArrayList,
                                requestManager,
                                new AchivementListAdapter.AchievementInterface() {
                                    @Override
                                    public void achievementLike(boolean isChecked, int position) {
                                        handlelikedOrNot(isChecked,position);
                                    }

                                    @Override
                                    public void achievementBTN(int position) {
                                        handleBtnClicked(position);
                                    }

                                    @Override
                                    public void gplusClicked(Bitmap bitmap, int position) {
                                        signedInWithGPlus(bitmap,position);
                                    }

                                    @Override
                                    public void fbClicked(int position) {
                                        shareWithFB(position);
                                    }

                                    @Override
                                    public void onLongClicked(int position, boolean isChecked) {
                                        handleOnLongClicked(position, isChecked);
                                    }

                                    @Override
                                    public void deleteItem(int position, boolean isChecked) {
                                        handleDeleteItem(position,isChecked);
                                    }
                                });

                        getLikedOrNot(0,0,false);
                        getGroupProfile(0,0,false);
                        getLikeCount(0,0,false);
                        progressBar.setVisibility(View.GONE);
                        load_data_progressBar.setVisibility(View.GONE);
                        isAnyDataLoading = false;

                        achievement_list_rv.setAdapter(adapter);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////MY ACH EARLIER KEY//////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void myACHEarlierKeys(){

        if(myACHKeys.size() == 0){
            getMyACHInitialKeys();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        refreshLayout.setPadding(0,0,0,35);

        final int oldMyKeySize = myACHKeys.size();

        final DatabaseReference my_ach_keys;

        isEarlierDataLoading = true;
        isAnyDataLoading = true;

        if(isMyAchievement)
        {
            my_ach_keys = root.child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getMyAchievement())
                    .child(selectedGroupKey);
        }
        else
        {
            my_ach_keys = root.child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getGroupAchievement())
                    .child(String.valueOf(selectedGroupType));
        }

        my_ach_keys.orderByKey().limitToLast(11).endAt(oldest_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if(!dataSnapshot.exists()){
                    load_data_progressBar.setVisibility(View.GONE);
                    my_ach_keys.removeEventListener(this);
                    isEarlierDataLoading = false;
                    refreshLayout.setPadding(0,0,0,0);
                    progressBar.setVisibility(View.GONE);
                    isAnyDataLoading = false;
                    return;
                }

                ArrayList<String> myLocalKeys = new ArrayList<>();

                while (iterator.hasNext())
                {
                    myLocalKeys.add(iterator.next().getValue(KeysAndGroupKeys.class).getKey());
                }

                if(myLocalKeys.get(myLocalKeys.size() - 1).equals(myACHKeys.get(0)))
                {
                    if(dataSnapshot.getChildrenCount() == 1)
                    {
                        load_data_progressBar.setVisibility(View.GONE);
                        my_ach_keys.removeEventListener(this);
                        isEarlierDataLoading = false;
                        refreshLayout.setPadding(0,0,0,0);
                        progressBar.setVisibility(View.GONE);
                        isAnyDataLoading = false;
                        return;
                    }
                    else if(dataSnapshot.getChildrenCount() > 1)
                    {
                        myLocalKeys.remove(myLocalKeys.size() - 1);
                    }
                }

                myACHKeys.addAll(0,myLocalKeys);
                oldest_key = myACHKeys.get(0);
                loadMyACHEarlier(myACHKeys.size() - oldMyKeySize);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// LOAD MY ACH EARLIER ////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadMyACHEarlier(int newSize){

        olderSize = list.size();

        for (int i=newSize - 1;i>=0;i--){

            achievement_msg_ref.child(myACHKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    list.add(0,dataSnapshot.getValue(AchievementListProvider.class));
                    groupProfileArrayList.add(0,new GroupProfile());
                    likeArrayList.add(0,false);
                    countForGroupAndMyACH++;

                    if(countForGroupAndMyACH == myACHKeys.size()){
                        adapter.notifyItemRangeInserted(0,list.size() - olderSize);
                        getLikedOrNot(olderSize,0,false);
                        getGroupProfile(olderSize,0,false);
                        getLikeCount(olderSize,0,false);
                        load_data_progressBar.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        isEarlierDataLoading = false;
                        isAnyDataLoading = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////ON REFRESH///////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRefresh() {

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshData();
            }
        });
    }

    private void refreshData(){

        if(isGroupAchievement || isMyAchievement)
        {
            if(list.size() == 0)
            {
                refreshLayout.setRefreshing(false);
                getMyACHInitialKeys();
            }
            else
            {
                newlyAddedMyAchKeys();
            }
        }
        else
        {
            if(list.size() == 0)
            {
                refreshLayout.setRefreshing(false);
                loadInitialData();
            }
            else
            {
                loadNewlyAddedData();
            }
        }


    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////IF NEW DATA IS AVAILABLE//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadNewlyAddedData(){

        String key = list.get(list.size() - 1).getKey();
        olderSize = list.size();
        isAnyDataLoading = true;

        achievement_msg_ref
                .orderByChild("key")
                .startAt(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    refreshLayout.setRefreshing(false);
                    isAnyDataLoading = false;
                    return;
                }

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if((iterator.next()).getValue(AchievementListProvider.class).getKey()
                        .equals(list.get(list.size() - 1).getKey()))
                {

                    if(!iterator.hasNext())
                    {
                        refreshLayout.setRefreshing(false);
                        isAnyDataLoading = false;
                        return;
                    }

                }
                else
                {
                    iterator = dataSnapshot.getChildren().iterator();
                }

                while (iterator.hasNext()){
                    list.add((iterator.next()).getValue(AchievementListProvider.class));
                    groupProfileArrayList.add(new GroupProfile());
                    likeArrayList.add(false);
                }

                getLikedOrNot(olderSize,olderSize,true);
                getGroupProfile(olderSize,olderSize,true);
                getLikeCount(olderSize,olderSize,true);

                adapter.notifyItemRangeInserted(0,list.size() - olderSize);
                setRetainInstance(false);
                refreshLayout.setRefreshing(false);
                isAnyDataLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////NEWLY ADDED MY ACHIEVEMENT KEYS///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void newlyAddedMyAchKeys(){

        final DatabaseReference my_ach_keys;
        isAnyDataLoading = true;

        if(isMyAchievement)
        {
            my_ach_keys = root.child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getMyAchievement())
                    .child(selectedGroupKey);
        }
        else
        {
            my_ach_keys = root.child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getGroupAchievement())
                    .child(String.valueOf(selectedGroupType));
        }

        my_ach_keys.orderByKey().startAt(newest_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    refreshLayout.setRefreshing(false);
                    isAnyDataLoading = false;
                    return;
                }

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if(iterator.next().getValue(KeysAndGroupKeys.class).getKey()
                        .equals(myACHKeys.get(myACHKeys.size() - 1)))
                {
                    if(dataSnapshot.getChildrenCount() == 1)
                    {
                        refreshLayout.setRefreshing(false);
                        isAnyDataLoading = false;
                        return;
                    }
                }
                else
                {
                    iterator = dataSnapshot.getChildren().iterator();
                }


                while(iterator.hasNext()){
                    myACHKeys.add(iterator.next().getValue(KeysAndGroupKeys.class).getKey());
                }

                newest_key = myACHKeys.get(myACHKeys.size() - 1);

                newelyAddedMyACH();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////NEWLY ADDED MY ACHIEVEMENTT///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void newelyAddedMyACH(){

        final int oldSize = list.size();

        for (int i=oldSize; i<myACHKeys.size(); i++)
        {
            achievement_msg_ref.child(myACHKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    list.add(dataSnapshot.getValue(AchievementListProvider.class));
                    groupProfileArrayList.add(new GroupProfile());
                    likeArrayList.add(false);
                    countForGroupAndMyACH++;

                    if(countForGroupAndMyACH == myACHKeys.size()){
                        getLikedOrNot(oldSize,oldSize,true);
                        getGroupProfile(oldSize,oldSize,true);
                        getLikeCount(oldSize,oldSize,true);
                        adapter.notifyItemRangeInserted(0,list.size() - olderSize);
                        isAnyDataLoading = false;
                        refreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////GET LIKED OR NOT/////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getLikedOrNot(final int olderSize, final int initialCNT, final boolean isNEW){

        DatabaseReference like;

        final int newlyAddeddata;

        if(isNEW)
        {
            newlyAddeddata = list.size();
        }
        else
        {
            newlyAddeddata = list.size() - olderSize;
        }

        for (int i=initialCNT ; i<newlyAddeddata ; i++)
        {
            final int temp = i;

            if(list.size() > temp && list.get(temp).getKey() != null)
            {
                like = root.child(achievement).child(GlobalNames.getLIKE())
                        .child(list.get(temp).getKey())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
            else
            {
                return;
            }

            like.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(list.size() > temp && dataSnapshot.getRef().getParent().getKey()
                            .equals(list.get(temp).getKey()))
                    {
                        if(dataSnapshot.exists())
                        {
                            likeArrayList.set(temp,Boolean.TRUE);
                            adapter.notifyItemChanged(temp);
                        }
                        else
                        {
                            likeArrayList.set(temp,Boolean.FALSE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////GET GROUP PROFILE/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getGroupProfile(final int olderSize, final int initialCNT, final boolean isNEW)
    {
        DatabaseReference groupProfileRef;

        final int newlyAddeddata;

        if(isNEW)
        {
            newlyAddeddata = list.size();
        }
        else
        {
            newlyAddeddata = list.size() - olderSize;
        }

        for (int i=initialCNT ; i<newlyAddeddata ; i++)
        {

            final int temp = i;

            if(list.size() > temp && list.get(temp).getKey() != null)
            {
                groupProfileRef = root.
                        child(GlobalNames.getGroupDetail())
                        .child(GlobalNames.getGroupProfile())
                        .child(list.get(temp).getGroup_key());
            }
            else
            {
                return;
            }

            groupProfileRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(list.size() > temp && dataSnapshot.exists() && list.get(temp).getGroup_key().equals(dataSnapshot.getKey()))
                    {
                        groupProfileArrayList.set(temp,dataSnapshot.getValue(GroupProfile.class));
                        adapter.notifyItemChanged(temp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////GET LIKES COUNT/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getLikeCount(final int olderSize, final int initialCNT,final boolean isNEW)
    {
        DatabaseReference groupProfileRef;

        final int newlyAddeddata;

        if(isNEW)
        {
            newlyAddeddata = list.size();
        }
        else
        {
            newlyAddeddata = list.size() - olderSize;
        }

        for (int i=initialCNT ; i<newlyAddeddata ; i++)
        {

            final int temp = i;

            if(list.size() > temp && list.get(temp).getKey() != null)
            {
                groupProfileRef = root
                        .child(GlobalNames.getACHIEVEMENT())
                        .child(GlobalNames.getLikeCnt())
                        .child(list.get(temp).getKey());
            }
            else
            {
                return;
            }

            final DatabaseReference finalGroupProfileRef = groupProfileRef;
            groupProfileRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

                    if(list.size() > temp && list.get(temp).getKey().equals(finalGroupProfileRef.getKey()))
                    {
                        if(dataSnapshot.exists())
                        {
                            list.get(temp).setLikes(dataSnapshot.getValue(Integer.class));
                        }
                        else
                        {
                            list.get(temp).setLikes(0);
                        }

                        adapter.notifyItemChanged(temp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////WHEN READ MORE CLICKED/////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private void handleBtnClicked(int position){

        btn_clicked_position = position;

       Intent intent = new Intent(getActivity(),AchievementDetail.class);
       intent.putExtra(getString(R.string.ach_list_provider_intent),list.get(position));
       intent.putExtra(getString(R.string.ach_group_profile_intent),groupProfileArrayList.get(position));
       intent.putExtra("bool",likeArrayList.get(position));
       progressBar.setVisibility(View.GONE);
       startActivityForResult(intent,GlobalNames.getAchDetailToAchResultCode());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////WHEN READ MORE CLICKED/////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleDeleteItem(int clickedPosition,boolean isChecked){

        if(isChecked)
        {
            dataToBeDeleted.add(list.get(clickedPosition));
            keysPosition.add(myACHKeys.get(clickedPosition));
            wishedToDelete.set(clickedPosition,true);
        }
        else
        {
            dataToBeDeleted.remove(list.get(clickedPosition));
            keysPosition.remove(myACHKeys.get(clickedPosition));
            wishedToDelete.set(clickedPosition,false);
        }

        adapter.notifyItemChanged(clickedPosition,null);

        setTitle();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////WHEN LONG CLICKED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleOnLongClicked(int position, boolean isChecked){

        if(list.get(position).getGroup_key().equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                sp.getBoolean(AllPreferences.getIsAdmin(),false) && !isLongPressed && isMyAchievement)
        {
            isLongPressed = true;
            adapter.notifyDataSetChanged();
            ((DashBoard)getActivity()).onLongPress(isLongPressed);
            dataToBeDeleted = new ArrayList<>();
            keysPosition = new ArrayList<>();
            setTitle();

            wishedToDelete = new ArrayList<>();

            for(int i=0;i<list.size();i++){
                wishedToDelete.add(false);
            }

            handleDeleteItem(position,isChecked);
        }
        else if(list.get(position).getGroup_key().equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                sp.getBoolean(AllPreferences.getIsAdmin(),false) && isLongPressed && isMyAchievement)
        {
            handleDeleteItem(position,isChecked);
        }
        else
        {
            handleBtnClicked(position);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////DELETE SELECTED ITEMS//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void deleteItems(){

        if(dataToBeDeleted == null || dataToBeDeleted.size() == 0){
            Toast.makeText(getActivity(), "You haven't choose any Achievement yet", Toast.LENGTH_SHORT).show();
        }

        for(int i=0;i<dataToBeDeleted.size();i++)
        {
            root.child(achievement)
                    .child(GlobalNames.getAchievementAll())
                    .child(dataToBeDeleted.get(i).getKey()).removeValue();

            root.child(achievement)
                    .child(GlobalNames.getGroupAchievement())
                    .child(String.valueOf(sp.getInt(AllPreferences.getGroupType(),0)))
                    .child(dataToBeDeleted.get(i).getKey()).removeValue();

            root.child(achievement)
                    .child(GlobalNames.getMyAchievement())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .child(dataToBeDeleted.get(i).getKey()).removeValue();

            root.child(achievement)
                    .child(GlobalNames.getLIKE())
                    .child(dataToBeDeleted.get(i).getKey()).removeValue();

            int indexOfItem = list.indexOf(dataToBeDeleted.get(i));
            myACHKeys.remove(keysPosition.get(i));
            list.remove(dataToBeDeleted.get(i));
            likeArrayList.remove(indexOfItem);
            groupProfileArrayList.remove(indexOfItem);
        }

        myACHKeys = new ArrayList<>();

        if(list != null &&list.size() > 0){

            for(int i=0;i<list.size();i++)
            {
                myACHKeys.add(list.get(i).getKey());
            }

            oldest_key = myACHKeys.get(0);
            newest_key = myACHKeys.get(myACHKeys.size() - 1);
        }

        isLongPressed = false;
        adapter.notifyDataSetChanged();
        ((DashBoard)getActivity()).onLongPress(isLongPressed);
        setTitle();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////WHEN BACK PRESSED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void backPressed(boolean isTabChanged)
    {

        if(menu_items != null)
        {
            onCreateOptionsMenu(menu_items,getActivity().getMenuInflater());
        }

        isLongPressed = false;
        setTitle();

        if(list != null && list.size() > 0 && !isTabChanged )
        {
            adapter.notifyDataSetChanged();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////WHEN LIKED BUTTON CLICKED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handlelikedOrNot(final boolean isChecked, final int position){

        final DatabaseReference like = root.child(achievement)
                .child(GlobalNames.getLIKE())
                .child(list.get(position).getKey())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(isChecked)
        {
            like.setValue(true);
            likeArrayList.set(position,Boolean.TRUE);

            root.child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getLikeCnt())
                    .child(list.get(position).getKey())
                    .setValue(list.get(position).getLikes() + 1);

            list.get(position).setLikes(list.get(position).getLikes() + 1);
        }
        else
        {
            like.removeValue();
            likeArrayList.set(position,Boolean.FALSE);

            root.child(GlobalNames.getACHIEVEMENT())
                    .child(GlobalNames.getLikeCnt())
                    .child(list.get(position).getKey())
                    .setValue(list.get(position).getLikes() - 1);

            list.get(position).setLikes(list.get(position).getLikes() - 1);
        }
        adapter.notifyDataSetChanged();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////GET UNIVERSITY LIST///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getGroupKeysFromDataBase() {

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());

        if(connectionCheck.checkingConnextion()){
            Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),"No Internet Connection",Snackbar.LENGTH_SHORT).show();
            return;
        }

        dialogHandler = new DialogHandler(getActivity(),false);

         DatabaseReference groupList =  root.child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getGroupTypeDetail())
                .child(String.valueOf(selectedGroupType));

        groupList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    Toast.makeText(getActivity(), "No Any group registered for selected option.", Toast.LENGTH_SHORT).show();
                    dialogHandler.sendEmptyMessage(0);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                groupKeyList = new ArrayList<>();
                dialogGroupProfile = new ArrayList<>();

                Iterator iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext())
                {
                    groupKeyList.add(((DataSnapshot)iterator.next()).getValue(String.class));
                }

                getGroupList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SHOW UNIVERSITY LIST//////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getGroupList(){

        countOptionMenu = 0;

        for(int i=0 ; i<groupKeyList.size() ; i++)
        {

            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getGroupProfile())
                    .child(groupKeyList.get(i))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            dialogGroupProfile.add(dataSnapshot.getValue(GroupProfile.class));
                            countOptionMenu++;

                            if(countOptionMenu == groupKeyList.size())
                            {
                                setGroupTypeGroupList();
                                dialogHandler.sendEmptyMessage(0);
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SET GROUP TYPE GROUP LIST///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setGroupTypeGroupList(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose Institute");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.universities_list, null);
        builder.setView(dialoglayout);

        RecyclerView recyclerView = (RecyclerView) dialoglayout.findViewById(R.id.ach_camp_group_list_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final SearchGroupAdapter  searchGroupAdapter = new SearchGroupAdapter(getActivity(), dialogGroupProfile, null, false, new SearchGroupAdapter.JoinGroupInterface() {
            @Override
            public void onItemClicked(int position) {

                if(dialogGroupProfile.get(position).getGroup_id().equals(selectedGroupKey))
                {
                    Toast.makeText(getActivity(), "Already Selected this group", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    selectedGroupKey = dialogGroupProfile.get(position).getGroup_id();
                    isMyAchievement = true;
                    isGroupAchievement = false;
                    likeArrayList.clear();
                    groupProfileArrayList.clear();
                    list.clear();
                    selectedGroup = dialogGroupProfile.get(position).getGroup_name();
                    adapter.notifyDataSetChanged();
                    achievement_selected();

                    menu_items.findItem(R.id.sort_by_group).setIcon(R.drawable.filter);
                }

                alertDialog.dismiss();

            }
        });

        recyclerView.setAdapter(searchGroupAdapter);

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        progressBar.setVisibility(View.GONE);

        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();


    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SET ACTIONBAR TITLE///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private  void setTitle(){

        if(isLongPressed)
        {
            ((DashBoard)getActivity()).setActionBarTitle(dataToBeDeleted.size() + " items selected");
        }
        else if(selectedGroup.equals("All"))
        {
            ((DashBoard)getActivity()).setActionBarTitle("Achievements");
        }
        else
        {
            ((DashBoard)getActivity()).setActionBarTitle(selectedGroup);
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////ACHIEVEMENT TAB SELECTED//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void achievement_selected(){

        if(list == null || list.size() == 0)
        {
            if(!isMyAchievement && !isGroupAchievement)
            {
                loadInitialData();
            }
            else
            {
                getMyACHInitialKeys();
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SHARE WITH FACEBOOK///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void shareWithFB(int position)
    {

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentDescription(getString(R.string.download_app))
                .setContentTitle(list.get(position).getTitle())
                .setImageUrl(Uri.parse(list.get(position).getImagepath()))
                .setContentUrl(Uri.parse(getString(R.string.play_store))).build();

        shareDialog.show(shareLinkContent);

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SHARE WITH GOOGLE PLUS////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void signedInWithGPlus(Bitmap bitmap,int position){

        Random random = new Random();

        if(ImageSaver.isExternalStorageWritable())
        {
            imageSaver = new ImageSaver(getActivity())
                    .setFileName(random.nextInt() + ".jpg")
                    .setExternal(true)
                    .setDirectoryName("images");
        }
        else
        {
            imageSaver = new ImageSaver(getActivity())
                    .setFileName(random.nextInt() + ".jpg")
                    .setExternal(false)
                    .setDirectoryName("images");
        }

        imageSaver.save(bitmap);

        File file = imageSaver.createFile();

        try{

            Intent shareIntent = new PlusShare.Builder(getActivity())
                    .setType("text/plain")
                    .setText(list.get(position).getTitle() + "\n " + getString(R.string.download_app))
                    .setStream(Uri.fromFile(file))
                    .getIntent();

            startActivityForResult(shareIntent, GlobalNames.getGooglePlusActivityResult());

        }catch (ActivityNotFoundException e){

            Snackbar.make(getView(),"You Need to Install Google+ in order to Share Achievement",Snackbar.LENGTH_SHORT).show();

        }
    }
}

