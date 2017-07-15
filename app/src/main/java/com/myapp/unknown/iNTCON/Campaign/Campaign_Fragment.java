package com.myapp.unknown.iNTCON.Campaign;


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
import android.support.v4.content.LocalBroadcastManager;
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
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.OtherClasses.Like_Interested_Going_Bool;
import com.myapp.unknown.iNTCON.OtherClasses.Like_Interested_Going_Cnt;
import com.myapp.unknown.iNTCON.OtherClasses.DialogHandler;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.OtherClasses.DashBoard;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Campaign_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar,load_data_progressBar;

    private ImageView campaignMessageIV;
    private TextView campaignMessageTV;
    private LinearLayout campaignMessageLayout;

    private SwipeRefreshLayout refreshLayout;

    private ArrayList<CampaignDetailProvider> list;
    private ArrayList<Like_Interested_Going_Bool> like_interested_going_bools;
    private ArrayList<Like_Interested_Going_Cnt> like_interested_going_cnts;
    private ArrayList<GroupProfile> groupProfileArrayList;
    private ArrayList<String> campKeys;
    private ArrayList<String> groupKeyList;
    private ArrayList<GroupProfile> dialogGroupProfile;

    private ArrayList<String> myCampaignKeys;
    private ArrayList<CampaignDetailProvider> dataToBeDeleted;
    private ArrayList<String> keysPosition;

    private CampaignListAdapter adapter;

    private static String newest_key,oldest_key;

    private SharedPreferences sp;

    private DatabaseReference root;
    private DatabaseReference campaign_msg;

    private String campaign;

    String selectedGroup = "All";

    private boolean isMyCampaign, isGroupCampaign;
    private int selectedGroupType;
    private String selectedGroupKey;

    private boolean isEarlierDataLoading = false;
    private boolean isAnyDataLoading = false;

    private static boolean isInitialLoading = false;

    static boolean isLongPressed;

    static ArrayList<Boolean> isWishedList;

    private static int img_clicked_position;
    private int countForOptionMenu = 0;
    private int countForGroupAndMyCamp = 0;

    private RequestManager requestManager;

    private AlertDialog alertDialog;

    private Menu menu_items;

    private ImageSaver imageSaver;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private DialogHandler dialogHandler;

    public Campaign_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(getActivity());

        View view = inflater.inflate(R.layout.fragment_campaign_,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_campaign);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.campaign_swipe_to_refresh);
        progressBar = (ProgressBar) view.findViewById(R.id.camp_load_earlier_progressBar);

        campaignMessageIV = (ImageView)view.findViewById(R.id.campaign_message_iv);
        campaignMessageTV = (TextView)view.findViewById(R.id.campaign_message_tv);
        campaignMessageLayout = (LinearLayout) view.findViewById(R.id.campaign_message_layout);
        Button campaignMessageRefreshBTN = (Button) view.findViewById(R.id.campaign_message_refresh);

        load_data_progressBar = (ProgressBar)view.findViewById(R.id.campaign_fragment_progressBar);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.rgb(25,118,210));
        refreshLayout.setDistanceToTriggerSync(80);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

        ///////////////////////////////////////////////////////////////////////////////
        //////ON REFRESH BUTTON CLICKED////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        campaignMessageRefreshBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isGroupCampaign || isMyCampaign)
                {
                    getMyCampaignInitialKeys();
                }
                else
                {
                    loadInitialData();
                }

            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        requestManager = Glide.with(getActivity());

        getActivity();
        sp = getActivity().getSharedPreferences(AllPreferences.getPreferenceName(), Context.MODE_PRIVATE);

        campaign = GlobalNames.getCAMPAIGN();

        root = FirebaseDatabase.getInstance().getReference().getRoot();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        campaign_msg = root.child(campaign).child(GlobalNames.getCampaignAll());

        recyclerView.setItemAnimator(null);


        ///////////////////////////////////////////////////////////////////////////////
        //////SCROLL LISTENER :: LOAD_EARLIER METHOD CALLED////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!recyclerView.canScrollVertically(1)
                        && !list.isEmpty()
                        && !isEarlierDataLoading
                        && !isLongPressed)
                {
                    isEarlierDataLoading = true;

                    if(isMyCampaign || isGroupCampaign)
                    {
                        myCampaignEarlierKeys();
                    }
                    else
                    {
                        loadEarlierData();
                    }
                }
            }
        });

        if(savedInstanceState!= null ) {

            list = new ArrayList<>();
            like_interested_going_bools = new ArrayList<>();
            like_interested_going_cnts = new ArrayList<>();
            groupProfileArrayList = new ArrayList<>();
            campKeys = new ArrayList<>();

            list = savedInstanceState.getParcelableArrayList(GlobalNames.getParcelCampaign());

            adapter = new CampaignListAdapter(getActivity(),
                    list,
                    like_interested_going_bools,
                    like_interested_going_cnts,
                    groupProfileArrayList,
                    requestManager,
                    new CampaignListAdapter.CampaignInterface() {
                        @Override
                        public void chkBoxClicked(View v, int position, boolean isChecked) {
                            handleChkBoxClicked(v,position,isChecked);
                        }

                        @Override
                        public void imgClicked(int position) {
                            handleImgClicked(position);
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
                            handleOnLongClicked(position ,isChecked);
                        }

                        @Override
                        public void deleteItem(int position, boolean isChecked) {
                            handleDeleteItem(position,isChecked);
                        }
                    });

            recyclerView.setAdapter(adapter);

        }
        else
        {
            root = FirebaseDatabase.getInstance().getReference().getRoot();
        }

        setHasOptionsMenu(true);

        return view;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SAVED INSTANCE STATE//////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(list!= null){
            outState.putParcelableArrayList(GlobalNames.getParcelCampaign(),list);
      }
    }
    ///////////////////////////////////////////////////////////////////////////////
    //////ON PREPARE OPTION MENU//////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.uploads_menu).setVisible(false);
        menu.findItem(R.id.downloads_menu).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////ON CREATE OPTION MENU/////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu_items = menu;
        getActivity().getMenuInflater().inflate(R.menu.camp_achi_menu,menu_items);

        menu_items.findItem(R.id.sort_by_group).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if(!isGroupCampaign && !isMyCampaign)
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
    /////////////////////////////////////////ON OPTIONS ITEM SELECTED//////////////////////////////////////////////////////////////
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
            campaignMessageLayout.setVisibility(View.VISIBLE);
            campaignMessageIV.setImageResource(R.mipmap.no_connection);
            campaignMessageTV.setText(getString(R.string.no_network));
            return true;
        }

        switch (item.getItemId())
        {
            case R.id.sort_by_group :
                getGroupKeysFromDataBase();
                return true;

            case R.id.camp_ach_all :

                if(!isMyCampaign &&
                        !isGroupCampaign &&
                        list != null &&
                        list.size() != 0)
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                /////////////////////////////////////////
                ///CLEAR LIST ON OPTION ITEM CLICKED/////
                /////////////////////////////////////////

                list.clear();
                like_interested_going_cnts.clear();
                like_interested_going_bools.clear();
                campKeys.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyCampaign = false;
                isGroupCampaign = false;
                menu_items.findItem(R.id.sort_by_group).setVisible(false);
                selectedGroup = "All";
                campaign_selected();
                return true;

            case R.id.camp_ach_academy :

                if(!isMyCampaign &&
                        isGroupCampaign &&
                        selectedGroupType == GlobalNames.getUniversityOrCollege() &&
                        list != null &&
                        list.size() !=0 &&
                        selectedGroupKey.equals(GlobalNames.getNONE()))
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }


                list.clear();
                like_interested_going_cnts.clear();
                like_interested_going_bools.clear();
                campKeys.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyCampaign = false;
                isGroupCampaign = true;
                selectedGroupType = GlobalNames.getUniversityOrCollege();
                selectedGroupKey = GlobalNames.getNONE();
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
                selectedGroup = "All";
                campaign_selected();
                return true;

            case R.id.camp_ach_private_academy :

                if(!isMyCampaign &&
                        isGroupCampaign &&
                        selectedGroupType == GlobalNames.getPrivateInstituteClg() &&
                        list != null &&
                        list.size() !=0 &&
                        selectedGroupKey.equals(GlobalNames.getNONE()))
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                list.clear();
                like_interested_going_cnts.clear();
                like_interested_going_bools.clear();
                campKeys.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyCampaign = false;
                isGroupCampaign = true;
                selectedGroupType = GlobalNames.getPrivateInstituteClg();
                selectedGroupKey = GlobalNames.getNONE();
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
                selectedGroup = "All";
                campaign_selected();
                return true;

            case R.id.camp_ach_school :

                if(!isMyCampaign &&
                        isGroupCampaign &&
                        selectedGroupType == GlobalNames.getSCHOOL() &&
                        list != null &&
                        list.size() !=0 &&
                        selectedGroupKey.equals(GlobalNames.getNONE()))
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                list.clear();
                like_interested_going_cnts.clear();
                like_interested_going_bools.clear();
                campKeys.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }

                isMyCampaign = false;
                isGroupCampaign = true;
                selectedGroupType = GlobalNames.getSCHOOL();
                selectedGroupKey = GlobalNames.getNONE();
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
                selectedGroup = "All";
                campaign_selected();
                return true;

            case R.id.camp_ach_private_school :

                if(!isMyCampaign &&
                        isGroupCampaign &&
                        selectedGroupType == GlobalNames.getPrivateInstituteSchool() &&
                        list != null &&
                        list.size() !=0 &&
                        selectedGroupKey.equals(GlobalNames.getNONE()))
                {
                    Toast.makeText(getActivity(), "Already Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                list.clear();
                like_interested_going_cnts.clear();
                like_interested_going_bools.clear();
                campKeys.clear();
                groupProfileArrayList.clear();

                if(adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }


                isMyCampaign = false;
                isGroupCampaign = true;
                selectedGroupType = GlobalNames.getPrivateInstituteSchool();
                selectedGroupKey = GlobalNames.getNONE();
                menu_items.findItem(R.id.sort_by_group).setVisible(true).setIcon(R.drawable.filter_no_select);
                selectedGroup = "All";
                campaign_selected();
                return true;

            default : return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////ON ACTIVITY FOR RESULT////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GlobalNames.getGooglePlusActivityResult())
        {
            imageSaver.deleteFile();
        }

        getActivity();
        if(resultCode == Activity.RESULT_OK && requestCode == GlobalNames.getCampDetailToCamp()){

            boolean like = data.getBooleanExtra("likeBool",false);
            boolean interest = data.getBooleanExtra("interestBool",false);
            boolean going = data.getBooleanExtra("goingBool",false);

            like_interested_going_bools.get(img_clicked_position).setLikes(like);
            like_interested_going_bools.get(img_clicked_position).setInterested(interest);
            like_interested_going_bools.get(img_clicked_position).setGoing(going);

            adapter.notifyItemChanged(img_clicked_position);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////LOAD INITIAL DATA//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadInitialData(){

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());
        isAnyDataLoading = true;

        if(connectionCheck.checkingConnextion())
        {
            campaignMessageLayout.setVisibility(View.VISIBLE);
            campaignMessageIV.setImageResource(R.mipmap.no_connection);
            campaignMessageTV.setText(getString(R.string.no_network));
            return;
        }

        if(isInitialLoading)
        {
            return;
        }
        else
        {
            isInitialLoading = true;
        }

        setTitle();

        list = new ArrayList<>();
        groupProfileArrayList = new ArrayList<>();
        like_interested_going_bools = new ArrayList<>();
        like_interested_going_cnts = new ArrayList<>();
        campKeys = new ArrayList<>();

        load_data_progressBar.setVisibility(View.VISIBLE);

        campaign_msg.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if(!iterator.hasNext()) {

                    load_data_progressBar.setVisibility(View.GONE);
                    isInitialLoading = false;
                    isAnyDataLoading = false;
                    refreshLayout.setRefreshing(false);
                    campaignMessageLayout.setVisibility(View.VISIBLE);
                    campaignMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    campaignMessageTV.setText(getString(R.string.no_data));
                    return;
                }

                campaignMessageLayout.setVisibility(View.GONE);

                while (iterator.hasNext()){
                    list.add((iterator.next()).getValue(CampaignDetailProvider.class));
                    groupProfileArrayList.add(new GroupProfile());
                    like_interested_going_cnts.add(new Like_Interested_Going_Cnt());
                    like_interested_going_bools.add(new Like_Interested_Going_Bool());
                    campKeys.add(list.get(list.size() - 1).getKey());
                }

                adapter = new CampaignListAdapter(getActivity(),
                        list,
                        like_interested_going_bools,
                        like_interested_going_cnts,
                        groupProfileArrayList,
                        requestManager,
                        new CampaignListAdapter.CampaignInterface() {
                            @Override
                            public void chkBoxClicked(View v, int position, boolean isChecked) {
                                handleChkBoxClicked(v,position,isChecked);
                            }

                            @Override
                            public void imgClicked(int position) {
                                handleImgClicked(position);
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
                                handleOnLongClicked(position ,isChecked);
                            }

                            @Override
                            public void deleteItem(int position, boolean isChecked) {
                                handleDeleteItem(position,isChecked);
                            }
                        });

                recyclerView.setAdapter(adapter);

                load_data_progressBar.setVisibility(View.GONE);
                getLikeInterestedGoingBool(0,0,false);
                getGroupProfile(0,0,false);
                getLikeInterestedGoingCNT(0,0,false);
                isInitialLoading = false;
                isAnyDataLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////LOAD EARLIER DATA//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadEarlierData() {

        if(list.size() == 0)
        {
            loadInitialData();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        refreshLayout.setPadding(0,0,0,35);
        isAnyDataLoading = true;

        final String key = list.get(0).getKey();

        campaign_msg.orderByChild("key").limitToLast(11).endAt(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if(!dataSnapshot.exists()){
                    load_data_progressBar.setVisibility(View.GONE);
                    isEarlierDataLoading = false;
                    isAnyDataLoading = false;
                    refreshLayout.setPadding(0,0,0,0);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                int olderSize = list.size();

                ArrayList<CampaignDetailProvider> listProviders = new ArrayList<>();
                ArrayList<Like_Interested_Going_Bool> likeInterestedGoingBools = new ArrayList<>();
                ArrayList<Like_Interested_Going_Cnt> likeInterestedGoingCnts = new ArrayList<>();
                ArrayList<GroupProfile> groupProfiles = new ArrayList<>();
                ArrayList<String> keys = new ArrayList<>();

                while (iterator.hasNext()){
                    listProviders.add((iterator.next()).getValue(CampaignDetailProvider.class));
                    likeInterestedGoingBools.add(new Like_Interested_Going_Bool());
                    likeInterestedGoingCnts.add(new Like_Interested_Going_Cnt());
                    groupProfiles.add(new GroupProfile());
                    keys.add(listProviders.get(listProviders.size() - 1).getKey());
                }

                if(list.get(0).getKey().equals(listProviders.get(listProviders.size() - 1).getKey()))
                {
                    if(dataSnapshot.getChildrenCount() == 1)
                    {
                        load_data_progressBar.setVisibility(View.GONE);
                        isEarlierDataLoading = false;
                        refreshLayout.setPadding(0,0,0,0);
                        progressBar.setVisibility(View.GONE);
                        isAnyDataLoading = false;
                        return;
                    }
                    else if(dataSnapshot.getChildrenCount() > 1)
                    {
                        listProviders.remove(listProviders.size() - 1);
                        likeInterestedGoingBools.remove(likeInterestedGoingBools.size() - 1);
                        likeInterestedGoingCnts.remove(likeInterestedGoingCnts.size() - 1);
                        groupProfiles.remove(groupProfiles.size() - 1);
                        keys.remove(keys.size() - 1);
                    }
                }

                list.addAll(0,listProviders);
                groupProfileArrayList.addAll(0,groupProfiles);
                like_interested_going_bools.addAll(0,likeInterestedGoingBools);
                like_interested_going_cnts.addAll(0,likeInterestedGoingCnts);
                campKeys.addAll(0,keys);

                adapter.notifyItemRangeInserted(0,list.size() - olderSize);
                load_data_progressBar.setVisibility(View.GONE);
                isEarlierDataLoading = false;
                isAnyDataLoading = false;
                refreshLayout.setPadding(0,0,0,0);
                progressBar.setVisibility(View.GONE);

                getLikeInterestedGoingBool(olderSize,0,false);
                getGroupProfile(olderSize,0,false);
                getLikeInterestedGoingCNT(olderSize,0,false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////GET MY CAMPAIGN INITIAL KEY//////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getMyCampaignInitialKeys(){

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());

        if(connectionCheck.checkingConnextion())
        {
            campaignMessageLayout.setVisibility(View.VISIBLE);
            campaignMessageIV.setImageResource(R.mipmap.no_connection);
            campaignMessageTV.setText(getString(R.string.no_network));
            return;
        }

        load_data_progressBar.setVisibility(View.VISIBLE);

        DatabaseReference my_campaign_keys;
        isAnyDataLoading = true;

        setTitle();

        if(isMyCampaign)
        {
            my_campaign_keys = root.child(GlobalNames.getCAMPAIGN())
                    .child(GlobalNames.getMyCamp())
                    .child(selectedGroupKey);

        }
        else
        {
            my_campaign_keys = root.child(GlobalNames.getCAMPAIGN())
                    .child(GlobalNames.getGroupCampaign())
                    .child(String.valueOf(selectedGroupType));
        }

        myCampaignKeys = new ArrayList<>();

        my_campaign_keys.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if(!iterator.hasNext())
                {
                    refreshLayout.setRefreshing(false);
                    isAnyDataLoading = false;
                    load_data_progressBar.setVisibility(View.GONE);
                    campaignMessageLayout.setVisibility(View.VISIBLE);
                    campaignMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    campaignMessageTV.setText(getString(R.string.no_data));
                    return;
                }

                campaignMessageLayout.setVisibility(View.GONE);

                while (iterator.hasNext()) {
                    myCampaignKeys.add(iterator.next().getValue(KeysAndGroupKeys.class).getKey());
                }

                newest_key = myCampaignKeys.get(myCampaignKeys.size() - 1);
                oldest_key = myCampaignKeys.get(0);

                loadInitialMyCampaign();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////LOAD INITIAL MY CAMPAIGN//////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadInitialMyCampaign(){

        list = new ArrayList<>();
        like_interested_going_cnts = new ArrayList<>();
        like_interested_going_bools = new ArrayList<>();
        groupProfileArrayList = new ArrayList<>();
        campKeys = new ArrayList<>();
        countForGroupAndMyCamp = 0;

        for (int i=0;i<myCampaignKeys.size();i++)
        {
            campaign_msg.child(myCampaignKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    list.add(dataSnapshot.getValue(CampaignDetailProvider.class));
                    groupProfileArrayList.add(new GroupProfile());
                    like_interested_going_bools.add(new Like_Interested_Going_Bool());
                    like_interested_going_cnts.add(new Like_Interested_Going_Cnt());
                    campKeys.add(dataSnapshot.getValue(CampaignDetailProvider.class).getKey());
                    countForGroupAndMyCamp++;

                    if(countForGroupAndMyCamp == myCampaignKeys.size())
                    {

                        adapter = new CampaignListAdapter(getActivity(),
                                list,
                                like_interested_going_bools,
                                like_interested_going_cnts,
                                groupProfileArrayList,
                                requestManager,
                                new CampaignListAdapter.CampaignInterface() {
                                    @Override
                                    public void chkBoxClicked(View v, int position, boolean isChecked) {
                                        handleChkBoxClicked(v,position,isChecked);
                                    }

                                    @Override
                                    public void imgClicked(int position) {
                                        handleImgClicked(position);
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
                                        handleOnLongClicked(position, isChecked);
                                    }

                                    @Override
                                    public void deleteItem(int position, boolean isChecked) {
                                        handleDeleteItem(position,isChecked);
                                    }
                                });

                        recyclerView.setAdapter(adapter);

                        isAnyDataLoading = false;
                        getLikeInterestedGoingBool(0,0,false);
                        getGroupProfile(0,0,false);
                        getLikeInterestedGoingCNT(0,0,false);
                        refreshLayout.setRefreshing(false);
                        load_data_progressBar.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////MY CAMPAIGN EARLIER KEY//////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void myCampaignEarlierKeys(){

        if(myCampaignKeys.size() == 0){
            getMyCampaignInitialKeys();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        refreshLayout.setPadding(0,0,0,35);

        final int oldMyKeySize = myCampaignKeys.size();

        isAnyDataLoading = true;

        final DatabaseReference my_campaign_keys;

        if(isMyCampaign)
        {
            my_campaign_keys = root.child(GlobalNames.getCAMPAIGN())
                    .child(GlobalNames.getMyCamp())
                    .child(selectedGroupKey);
        }
        else
        {
            my_campaign_keys = root.child(GlobalNames.getCAMPAIGN())
                    .child(GlobalNames.getGroupCampaign())
                    .child(String.valueOf(selectedGroupType));
        }

        my_campaign_keys.orderByKey().limitToLast(11).endAt(oldest_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if(!dataSnapshot.exists()){
                    load_data_progressBar.setVisibility(View.GONE);
                    isEarlierDataLoading = false;
                    isAnyDataLoading = false;
                    refreshLayout.setPadding(0,0,0,0);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                ArrayList<String> myLocalKeys = new ArrayList<>();

                while (iterator.hasNext()){
                    myLocalKeys.add(iterator.next().getValue(KeysAndGroupKeys.class).getKey());
                }

                if(myLocalKeys.get(myLocalKeys.size() - 1).equals(myCampaignKeys.get(0)))
                {
                    if(dataSnapshot.getChildrenCount() == 1)
                    {
                        load_data_progressBar.setVisibility(View.GONE);
                        my_campaign_keys.removeEventListener(this);
                        isEarlierDataLoading = false;
                        isAnyDataLoading = false;
                        refreshLayout.setPadding(0,0,0,0);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    else if(dataSnapshot.getChildrenCount() > 1)
                    {
                        myLocalKeys.remove(myLocalKeys.size() - 1);
                    }
                }

                myCampaignKeys.addAll(0,myLocalKeys);
                oldest_key = myCampaignKeys.get(0);
                loadMyCampaignEarlier(myCampaignKeys.size() - oldMyKeySize);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isAnyDataLoading = false;
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// LOAD MY CAMPAIGN EARLIER ////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadMyCampaignEarlier(final int newSize){

        final int olderSize = list.size();

        for (int i=newSize - 1;i>=0;i--){

            campaign_msg.child(myCampaignKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    list.add(0,dataSnapshot.getValue(CampaignDetailProvider.class));
                    like_interested_going_bools.add(0,new Like_Interested_Going_Bool());
                    like_interested_going_cnts.add(0, new Like_Interested_Going_Cnt());
                    groupProfileArrayList.add(0, new GroupProfile());
                    campKeys.add(0,dataSnapshot.getValue(CampaignDetailProvider.class).getKey());
                    countForGroupAndMyCamp++;

                    if(countForGroupAndMyCamp == myCampaignKeys.size()){
                        getLikeInterestedGoingBool(olderSize,0,false);
                        getGroupProfile(olderSize,0,false);
                        getLikeInterestedGoingCNT(olderSize,0,false);
                        load_data_progressBar.setVisibility(View.GONE);

                        adapter.notifyItemRangeInserted(0,list.size() - olderSize);

                        load_data_progressBar.setVisibility(View.GONE);
                        isEarlierDataLoading = false;
                        isAnyDataLoading = false;
                        refreshLayout.setPadding(0,0,0,0);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////LOAD REFRESHED DATA///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

        if(isGroupCampaign || isMyCampaign)
        {
            if(list.size() == 0)
            {
                refreshLayout.setRefreshing(false);
                getMyCampaignInitialKeys();
            }
            else
            {
                newlyAddedMyCampKeys();
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
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////LOAD REFRESHED DATA///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadNewlyAddedData(){

        String key = list.get(list.size() - 1).getKey();
        final int olderSize = list.size();
        isAnyDataLoading = true;

        campaign_msg.orderByChild("key").startAt(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    refreshLayout.setRefreshing(false);
                    return;
                }

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();


                if((iterator.next()).getValue(CampaignDetailProvider.class).getKey()
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
                    list.add((iterator.next()).getValue(CampaignDetailProvider.class));
                    like_interested_going_bools.add(new Like_Interested_Going_Bool());
                    like_interested_going_cnts.add(new Like_Interested_Going_Cnt());
                    groupProfileArrayList.add(new GroupProfile());
                    campKeys.add(list.get(list.size() - 1).getKey());
                }

                isAnyDataLoading = false;
                refreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size() - 1);
                setRetainInstance(false);

                getLikeInterestedGoingBool(olderSize,olderSize,true);
                getGroupProfile(olderSize,olderSize,true);
                getLikeInterestedGoingCNT(olderSize,olderSize,true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////NEWLY ADDED MY CAMPAIGN KEYS///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void newlyAddedMyCampKeys(){

        DatabaseReference my_campaign_keys;
        isAnyDataLoading = true;

        if(isMyCampaign)
        {
            my_campaign_keys = root.child(GlobalNames.getCAMPAIGN())
                    .child(GlobalNames.getMyCamp())
                    .child(selectedGroupKey);
        }
        else
        {
            my_campaign_keys = root.child(GlobalNames.getCAMPAIGN())
                    .child(GlobalNames.getGroupCampaign())
                    .child(String.valueOf(selectedGroupType));
        }

        my_campaign_keys.orderByKey().startAt(newest_key).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        .equals(myCampaignKeys.get(myCampaignKeys.size() - 1)))
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
                    myCampaignKeys.add(iterator.next().getValue(KeysAndGroupKeys.class).getKey());
                }

                newest_key = myCampaignKeys.get(myCampaignKeys.size() - 1);

                newelyAddedMyCAMP();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////NEWLY ADDED MY CAMPAIGN///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void newelyAddedMyCAMP(){

        final int oldSize = list.size();

        for (int i=oldSize;i<myCampaignKeys.size();i++)
        {
            campaign_msg.child(myCampaignKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    list.add(dataSnapshot.getValue(CampaignDetailProvider.class));
                    groupProfileArrayList.add(new GroupProfile());
                    like_interested_going_bools.add(new Like_Interested_Going_Bool());
                    like_interested_going_cnts.add(new Like_Interested_Going_Cnt());
                    campKeys.add(dataSnapshot.getValue(CampaignDetailProvider.class).getKey());
                    countForGroupAndMyCamp++;

                    if(countForGroupAndMyCamp == myCampaignKeys.size())
                    {
                        refreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(list.size() - 1);
                        setRetainInstance(false);
                        isAnyDataLoading = false;

                        getLikeInterestedGoingBool(oldSize,oldSize,true);
                        getGroupProfile(oldSize,oldSize,true);
                        getLikeInterestedGoingCNT(oldSize,oldSize,true);
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
    private void getLikeInterestedGoingBool(final int olderSize, final int initialCNT, final boolean isNEW){

        DatabaseReference like_interest_going;

        final int newlyAddeddata;

        if(isNEW)
        {
            newlyAddeddata = list.size();
        }
        else
        {
            newlyAddeddata = list.size() - olderSize;
        }

            for (int i=initialCNT;i<newlyAddeddata;i++)
            {
                final int temp = i;

                if(list.size() > temp && list.get(temp).getKey() != null)
                {
                    like_interest_going = root.child(campaign)
                            .child(GlobalNames.getLike_interested_going_bool())
                            .child(list.get(temp).getKey())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
                else
                {
                    return;
                }

                final DatabaseReference finalLike_interest_going = like_interest_going;
                like_interest_going.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Like_Interested_Going_Bool like_interested_going_bool;

                        int indexOflist = campKeys.indexOf(finalLike_interest_going.getParent().getKey());

                        if(list.size()  > temp && indexOflist != -1
                                && finalLike_interest_going.getParent().getKey().equals(list.get(indexOflist).getKey()))
                        {
                            if(dataSnapshot.exists())
                            {
                                like_interested_going_bool = dataSnapshot.getValue(Like_Interested_Going_Bool.class);
                                like_interested_going_bools.set(indexOflist,like_interested_going_bool);
                                adapter.notifyItemChanged(indexOflist);
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
    /////////////////////////////////////////GET NUMBER OF LIKES/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getLikeInterestedGoingCNT(final int olderSize, final int initialCNT, final boolean isNEW)
    {
        DatabaseReference like_interest_going_cnt_ref;

        final int newlyAddeddata;

        if(isNEW)
        {
            newlyAddeddata = list.size();
        }
        else
        {
            newlyAddeddata = list.size() - olderSize;
        }

        for (int i=initialCNT;i<newlyAddeddata;i++)
        {
            final int temp = i;

            if(list.size() > temp && list.get(temp).getKey() != null)
            {
                like_interest_going_cnt_ref = root.child(campaign)
                        .child(GlobalNames.getLike_interested_going_cnt())
                        .child(list.get(temp).getKey());
            }
            else
            {
                return;
            }

            final DatabaseReference finalLike_interest_going_cnt = like_interest_going_cnt_ref;
            like_interest_going_cnt_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int indexOflist = campKeys.indexOf(finalLike_interest_going_cnt.getKey());

                    if(list.size() > temp && indexOflist != -1
                            && finalLike_interest_going_cnt.getKey().equals(list.get(indexOflist).getKey()))
                    {
                        if(dataSnapshot.exists())
                        {
                            Intent RTReturn = new Intent(Campaign_Detail.RECEIVE_CHANGE);
                            RTReturn.putExtra("like_interest_going", dataSnapshot.getValue(Like_Interested_Going_Cnt.class));
                            RTReturn.putExtra("item_key", finalLike_interest_going_cnt.getKey());
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(RTReturn);

                            like_interested_going_cnts.set(indexOflist,dataSnapshot.getValue(Like_Interested_Going_Cnt.class));
                            adapter.notifyItemChanged(indexOflist);
                        }
                        else
                        {
                            like_interested_going_cnts.set(indexOflist,new Like_Interested_Going_Cnt());
                            adapter.notifyItemChanged(indexOflist);
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

                    if(list.size()  > temp && dataSnapshot.exists() && list.get(temp).getGroup_key().equals(dataSnapshot.getKey()))
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

        countForOptionMenu = 0;

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
                            countForOptionMenu++;

                            if(dialogGroupProfile.size() == groupKeyList.size())
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

        final SearchGroupAdapter searchGroupAdapter = new SearchGroupAdapter(getActivity(), dialogGroupProfile, null, false, new SearchGroupAdapter.JoinGroupInterface() {
            @Override
            public void onItemClicked(int position) {

                if(dialogGroupProfile.get(position).getGroup_id().equals(selectedGroupKey))
                {
                    Toast.makeText(getActivity(), "Already Selected this group", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    selectedGroupKey = dialogGroupProfile.get(position).getGroup_id();
                    isMyCampaign = true;
                    isGroupCampaign = false;
                    groupProfileArrayList.clear();
                    list.clear();
                    selectedGroup = dialogGroupProfile.get(position).getGroup_name();
                    adapter.notifyDataSetChanged();
                    campaign_selected();

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
    //////////////////////////////HANDLE LIKED,INTERESTED,GOING CLIKED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleChkBoxClicked(View v, int position, boolean isChecked){

            switch (v.getId()){

                case R.id.campaign_list_chk_box_like:

                    if(isChecked)
                    {
                        like_interested_going_bools.get(position).setLikes(true);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_bool())
                                .child(list.get(position).getKey())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(GlobalNames.getLIKE())
                                .setValue(true);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_cnt())
                                .child(list.get(position).getKey())
                                .child(GlobalNames.getLIKE())
                                .setValue(like_interested_going_cnts.get(position).getLikes() + 1);

                    }
                    else
                    {
                        like_interested_going_bools.get(position).setLikes(false);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_bool())
                                .child(list.get(position).getKey())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(GlobalNames.getLIKE())
                                .removeValue();

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_cnt())
                                .child(list.get(position).getKey())
                                .child(GlobalNames.getLIKE())
                                .setValue(like_interested_going_cnts.get(position).getLikes() - 1);
                    }
                    break;

                case R.id.campaign_list_chk_box_interested:

                    if(isChecked)
                    {

                        like_interested_going_bools.get(position).setInterested(true);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_bool())
                                .child(list.get(position).getKey())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(GlobalNames.getINTERESTED())
                                .setValue(true);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_cnt())
                                .child(list.get(position).getKey())
                                .child(GlobalNames.getINTERESTED())
                                .setValue(like_interested_going_cnts.get(position).getInterested() + 1);
                    }
                    else
                    {
                        like_interested_going_bools.get(position).setInterested(false);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_bool())
                                .child(list.get(position).getKey())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(GlobalNames.getINTERESTED())
                                .removeValue();

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_cnt())
                                .child(list.get(position).getKey())
                                .child(GlobalNames.getINTERESTED())
                                .setValue(like_interested_going_cnts.get(position).getInterested() - 1);
                    }
                    break;

                case R.id.campaign_list_going_chk_box:

                    if(isChecked)
                    {
                        like_interested_going_bools.get(position).setGoing(true);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_bool())
                                .child(list.get(position).getKey())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(GlobalNames.getGOING())
                                .setValue(true);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_cnt())
                                .child(list.get(position).getKey())
                                .child(GlobalNames.getGOING())
                                .setValue(like_interested_going_cnts.get(position).getGoing() + 1);
                    }
                    else
                    {

                        like_interested_going_bools.get(position).setGoing(false);

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_bool())
                                .child(list.get(position).getKey())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(GlobalNames.getGOING())
                                .removeValue();

                        root.child(GlobalNames.getCAMPAIGN())
                                .child(GlobalNames.getLike_interested_going_cnt())
                                .child(list.get(position).getKey())
                                .child(GlobalNames.getGOING())
                                .setValue(like_interested_going_cnts.get(position).getGoing() - 1);
                    }
                    break;
            }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////HANDLE IMAGE CLICKED///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleImgClicked(int position){

        img_clicked_position = position;

        Intent intent = new Intent(getActivity(),Campaign_Detail.class);
        intent.putExtra(getString(R.string.camp_group_profile_intent),groupProfileArrayList.get(position));
        intent.putExtra(getString(R.string.camp_list_provider_intent),list.get(position));
        intent.putExtra(getString(R.string.camp_like_interest_going_bool_intent),like_interested_going_bools.get(position));
        intent.putExtra(getString(R.string.camp_like_interest_going_cnt_intent),like_interested_going_cnts.get(position));
        progressBar.setVisibility(View.GONE);
        startActivityForResult(intent,GlobalNames.getCampDetailToCamp());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SET ACTIONBAR TITLE///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setTitle(){

        if(getActivity() == null){
            return;
        }

        if(isLongPressed)
        {
            ((DashBoard)getActivity()).setActionBarTitle(dataToBeDeleted.size() + " items selected");
        }
        else if(selectedGroup.equals("All"))
        {
            ((DashBoard)getActivity()).setActionBarTitle("Events");
        }
        else
        {
            ((DashBoard)getActivity()).setActionBarTitle(selectedGroup);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////WHEN TAB SELECTED/////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void campaign_selected(){

        if(getActivity() == null)
        {
            return;
        }

        if(list == null || list.size() == 0)
        {
            if(!isMyCampaign && !isGroupCampaign)
            {
                loadInitialData();
            }
            else
            {
                getMyCampaignInitialKeys();
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////SHARE WITH FACEBOOK///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void shareWithFB(int position)
    {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
        ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.campaign_list_img);
        imageView.setDrawingCacheEnabled(true);

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentDescription(getString(R.string.download_app))
                .setContentTitle(list.get(position).getTitle())
                .setImageUrl(Uri.parse(list.get(position).getImg_path()))
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

        try {
            Intent shareIntent = new PlusShare.Builder(getActivity())
                    .setType("text/plain")
                    .setText(list.get(position).getTitle() + "\n " + getString(R.string.download_app))
                    .setStream(Uri.fromFile(file))
                    .getIntent();

            startActivityForResult(shareIntent, GlobalNames.getGooglePlusActivityResult());

        } catch (ActivityNotFoundException e){

            Snackbar.make(getView(),"You Need to Install Google+ in order to Share the Event",Snackbar.LENGTH_SHORT).show();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////WHEN LONG CLICKED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleOnLongClicked(int position, boolean isChecked){


        if(list.get(position).getGroup_key().equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                sp.getBoolean(AllPreferences.getIsAdmin(),false) && !isLongPressed && isMyCampaign)
        {
            isLongPressed = true;
            adapter.notifyDataSetChanged();
            ((DashBoard)getActivity()).onLongPress(isLongPressed);
            dataToBeDeleted = new ArrayList<>();
            keysPosition = new ArrayList<>();
            setTitle();

            isWishedList = new ArrayList<>();

            for(int i=0;i<list.size();i++){
                isWishedList.add(false);
            }

            handleDeleteItem(position,isChecked);

        }
        else if(list.get(position).getGroup_key().equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                sp.getBoolean(AllPreferences.getIsAdmin(),false) && isLongPressed && isMyCampaign)
        {
           handleDeleteItem(position,isChecked);
        }
        else
        {
            handleImgClicked(position);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////WHEN READ MORE CLICKED/////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleDeleteItem(int clickedPosition,boolean isChecked){

        if(isChecked)
        {
            dataToBeDeleted.add(list.get(clickedPosition));
            keysPosition.add(myCampaignKeys.get(clickedPosition));
            isWishedList.set(clickedPosition,isChecked);
        }
        else
        {
            dataToBeDeleted.remove(list.get(clickedPosition));
            keysPosition.remove(myCampaignKeys.get(clickedPosition));
            isWishedList.set(clickedPosition,isChecked);
        }

        adapter.notifyItemChanged(clickedPosition,null);
        setTitle();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////WHILE DELETING :: ON BACK PRESSED//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void backPressed(boolean isTabChanged) {

       if(menu_items != null)
       {
           onCreateOptionsMenu(menu_items,getActivity().getMenuInflater());
       }

       isLongPressed = false;
       setTitle();

       if(list != null && list.size() > 0 && (!isTabChanged))
       {
           adapter.notifyDataSetChanged();
       }
   }

    ///////////////////////////////////////////////////
    ///////DELETE ITEMS////////////////////////////////
    ///////////////////////////////////////////////////
    public void deleteItems() {

       if(dataToBeDeleted == null || dataToBeDeleted.size() == 0){
           Toast.makeText(getActivity(), "You haven't choose any Message yet", Toast.LENGTH_SHORT).show();
       }

       for(int i=0;i<dataToBeDeleted.size();i++)
       {

           root.child(GlobalNames.getCAMPAIGN())
                   .child(GlobalNames.getMyCampaign())
                   .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                   .child(dataToBeDeleted.get(i).getKey()).removeValue();

           root.child(GlobalNames.getCAMPAIGN())
                   .child(GlobalNames.getCHAT())
                   .child(dataToBeDeleted.get(i).getKey()).removeValue();

           root.child(GlobalNames.getCAMPAIGN())
                   .child(GlobalNames.getGroupCampaign())
                   .child(String.valueOf(sp.getInt(AllPreferences.getGroupType(),0)))
                   .child(dataToBeDeleted.get(i).getKey()).removeValue();

           root.child(GlobalNames.getCAMPAIGN())
                   .child(GlobalNames.getLike_interested_going_bool())
                   .child(dataToBeDeleted.get(i).getKey()).removeValue();

           root.child(GlobalNames.getCAMPAIGN())
                   .child(GlobalNames.getLike_interested_going_cnt())
                   .child(dataToBeDeleted.get(i).getKey()).removeValue();

           root.child(GlobalNames.getCAMPAIGN())
                   .child(GlobalNames.getCampaignAll())
                   .child(dataToBeDeleted.get(i).getKey()).removeValue();

           int indexOfItem = list.indexOf(dataToBeDeleted.get(i));
           myCampaignKeys.remove(keysPosition.get(i));
           list.remove(dataToBeDeleted.get(i));
           like_interested_going_bools.remove(indexOfItem);
           like_interested_going_cnts.remove(indexOfItem);
           groupProfileArrayList.remove(indexOfItem);
           campKeys.remove(indexOfItem);
       }

       if(myCampaignKeys != null && myCampaignKeys.size() > 0)
       {
           oldest_key = myCampaignKeys.get(0);
           newest_key = myCampaignKeys.get(myCampaignKeys.size() - 1);
       }

       isLongPressed = false;
       adapter.notifyDataSetChanged();
       ((DashBoard)getActivity()).onLongPress(isLongPressed);
       setTitle();
   }
}