package com.myapp.unknown.iNTCON.Notice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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

import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.OtherClasses.DashBoard;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SQLiteDataBaseCheckChange;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SQLiteDataBaseHelperClass;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;

import java.util.ArrayList;
import java.util.Iterator;

import ca.barrenechea.widget.recyclerview.decoration.DividerDecoration;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class Notice_Fragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout noticeSwipeRefresh;
    private ProgressBar progressBar,load_data_progressBar;

    private ImageView noticeMessageIV;
    private TextView noticeMessageTV;
    private LinearLayout noticeMessageLayout;

    private TextView new_notice_tv;
    private CardView notice_message_new_notice_card_view;

    private boolean isLoading = false;
    private boolean isInitialLoading = false;
    private boolean isNewNoticeInitializing = false;

    private Menu menu_items;

    static boolean isLongPressed;

    private StickyHeaderDecoration headerDecoration;

    private ArrayList<NoticeListProvider> noticeListProvider;
    private ArrayList<UserProfile> userProfiles;
    private ArrayList<String> userIDKeyList;
    private ArrayList<My_Notice> my_notices;
    private ArrayList<String> loadedKeyList;
    private ArrayList<Integer> msg_number;
    private ArrayList<Cursor> cursorArrayList;
    private ArrayList<Integer> numOfMessages;
    private ArrayList<Boolean> isLoadingOrUpdating;
    private ArrayList<Boolean> isImportant;
    private static ArrayList<Boolean> isOpenOrNot;
    private ArrayList<NoticeListProvider> dataToBeDeleted;
    private ArrayList<UserProfile> userProfileToBeDeleted;
    private ArrayList<Integer> msgNumberToBeDeleted;
    private ArrayList<Boolean> isImportantToBeDeleted;
    private ArrayList<My_Notice> myNoticeToBeDeleted;
    static  ArrayList<Boolean> wishedTodelete;

    private SQLiteDataBaseHelperClass sqLiteDataBaseHelperClass;
    private SQLiteDataBaseCheckChange sqLiteDataBaseCheckChange;

    private boolean initiallyLoadingDataChanged = false;
    private boolean earlierLoadingDataChanged = false;
    private boolean newlyLoadingDataChanged = false;

    private NoticeListAdapter noticeListAdapter;

    private SharedPreferences sp;

    private String noticeRefStr, noticeMsg;

    private DatabaseReference notice, chat_cnt, root;

    private static String new_first_key,oldest_last_key;

    private boolean isMyNotice = false;

    private boolean isFaculty = true;

    private static String actionBarTitle;

    private static int clickedPosition;

    private int olderSize;

    private int counter;


    private Ringtone playSound;

    private String openedNotice;

    public Notice_Fragment() {
        // Required empty public constructor
    }


    ///////////////////////////////////////////////////////////////////////////
    /////////ON CREATE OPTION MENU/////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notice_, container, false);

        recyclerView = (RecyclerView)view. findViewById(R.id.notice_list_rv);
        noticeSwipeRefresh = (SwipeRefreshLayout)view. findViewById(R.id.notice_swipe_to_refresh);

        progressBar = (ProgressBar)view.findViewById(R.id.notice_progressBar);
        load_data_progressBar = (ProgressBar)view.findViewById(R.id.notice_fragment_progressBar);
        new_notice_tv = (TextView) view.findViewById(R.id.notice_message_new_notice_tv);
        notice_message_new_notice_card_view = (CardView) view.findViewById(R.id.notice_message_new_notice_card_view);

        noticeMessageIV = (ImageView)view.findViewById(R.id.notice_message_iv);
        noticeMessageTV = (TextView)view.findViewById(R.id.notice_message_tv);
        noticeMessageLayout = (LinearLayout) view.findViewById(R.id.notice_message_layout);
        Button noticeMessageRefreshBTN = (Button) view.findViewById(R.id.notice_message_refresh);

        noticeSwipeRefresh.setOnRefreshListener(this);
        noticeSwipeRefresh.setDistanceToTriggerSync(80);
        noticeSwipeRefresh.setColorSchemeColors(Color.rgb(25,134,210));
        noticeSwipeRefresh.setSize(SwipeRefreshLayout.DEFAULT);

        openedNotice = GlobalNames.getNONE();

        root = FirebaseDatabase.getInstance().getReference().getRoot();

        DividerDecoration dividerDecoration = new DividerDecoration.Builder(getActivity())
                .setHeight(R.dimen.divider)
                .setColor(Color.argb(50, 100, 100, 100))
                .build();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int positionView = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                if(positionView == noticeListProvider.size() - 1 && !noticeListProvider.isEmpty() && !isLongPressed  && !isLoading)
                {
                    isLoading = true;

                    if(isMyNotice)
                    {
                        myNoticeEarlierKeys();
                    }
                    else
                    {
                        loadEarlier();
                    }
                }
            }

        });

        sp = getActivity().getSharedPreferences(AllPreferences.getPreferenceName(), Context.MODE_PRIVATE);

        recyclerView.addItemDecoration(dividerDecoration);

        noticeRefStr = GlobalNames.getNOTICE();
        noticeMsg = GlobalNames.getNoticeMsg();

        noticeListProvider = new ArrayList<>();
        msg_number = new ArrayList<>();
        isLoadingOrUpdating = new ArrayList<>();
        cursorArrayList = new ArrayList<>();
        numOfMessages = new ArrayList<>();
        isOpenOrNot = new ArrayList<>();
        isImportant = new ArrayList<>();
        userProfiles = new ArrayList<>();
        userIDKeyList = new ArrayList<>();

        sqLiteDataBaseHelperClass = new SQLiteDataBaseHelperClass(getActivity());
        sqLiteDataBaseCheckChange = new SQLiteDataBaseCheckChange(getActivity());

        noticeMessageRefreshBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notice_message_new_notice_card_view.setVisibility(View.GONE);

                if(isMyNotice)
                {
                    getMyNoticeInitialKeys();
                }
                else
                {
                    initiateData();
                }

            }
        });

        setHasOptionsMenu(true);

        ((DashBoard) getActivity()).setActionBarTitle(GlobalNames.getAdminNotice());
        actionBarTitle = GlobalNames.getAdminNotice();

        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        playSound = RingtoneManager.getRingtone(getContext(), ringtone);

        return view;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////ON CREATE OPTION MENU/////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onDestroy() {
        sqLiteDataBaseCheckChange.close();
        sqLiteDataBaseHelperClass.close();
        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////ON CREATE OPTION MENU/////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuInflater menuInflater = new MenuInflater(getActivity());
        menuInflater.inflate(R.menu.notice_frag_menu,menu);

        menu.findItem(R.id.uni_or_fac_action).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.my_notice_action).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if(sp.getBoolean(AllPreferences.getIsAdmin(),false))
        {
            menu.findItem(R.id.my_notice_action).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.my_notice_action).setVisible(false);
        }

        if(sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.PRIVATE_GROUP)
        {
            menu.findItem(R.id.uni_or_fac_action).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.uni_or_fac_action).setVisible(true);
        }

        if(isFaculty)
        {
            menu.findItem(R.id.uni_or_fac_action).setIcon(R.drawable.ic_account_balance_black_24dp);
        }
        else
        {
            menu.findItem(R.id.uni_or_fac_action).setIcon(R.drawable.ic_group_white_24dp);
        }

        if(!isMyNotice)
        {
            menu.findItem(R.id.my_notice_action).setIcon(R.drawable.ic_person_white_18dp_not_selected);
        }
        else
        {
            menu.findItem(R.id.my_notice_action).setIcon(R.drawable.ic_person_white_18dp);
        }

        menu_items = menu;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////ON OPTION ITEM SELECTED/////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(notice_message_new_notice_card_view.getVisibility() == View.VISIBLE)
        {
            notice_message_new_notice_card_view.setVisibility(View.GONE);
        }

        if((initiallyLoadingDataChanged ||
                earlierLoadingDataChanged ||
                newlyLoadingDataChanged) ){

            Toast.makeText(getActivity(), "Please Wait...", Toast.LENGTH_SHORT).show();
            return true;
        }

        switch (item.getItemId()) {

            case R.id.uni_or_fac_action :

                isMyNotice = false;
                menu_items.findItem(R.id.my_notice_action).setIcon(R.drawable.ic_person_white_18dp_not_selected);

                isFaculty = !isFaculty;

                if(isFaculty)
                {
                    ((DashBoard) getActivity()).setActionBarTitle(GlobalNames.getAdminNotice());
                    actionBarTitle = GlobalNames.getAdminNotice();

                    menu_items.findItem(R.id.uni_or_fac_action).setIcon(R.drawable.ic_account_balance_black_24dp);

                    notice = root.child(noticeRefStr)
                            .child(noticeMsg)
                            .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                            .child(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()))
                            .child(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ",""));
                }
                else
                {
                    ((DashBoard) getActivity()).setActionBarTitle(GlobalNames.getInstituteNotice());
                    actionBarTitle = GlobalNames.getInstituteNotice();
                    menu_items.findItem(R.id.uni_or_fac_action).setIcon(R.drawable.ic_group_white_24dp);

                    notice = root.child(noticeRefStr)
                            .child(noticeMsg)
                            .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                            .child(GlobalNames.getInstitute());
                }

                initiateData();

                return true;

            case R.id.my_notice_action :

                isMyNotice = !isMyNotice;

                isFaculty = true;
                menu_items.findItem(R.id.uni_or_fac_action).setIcon(R.drawable.ic_account_balance_black_24dp);

                notice = root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()))
                        .child(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ",""));

                if(isMyNotice)
                {
                    getMyNoticeInitialKeys();
                    ((DashBoard) getActivity()).setActionBarTitle("MyNotice");
                    actionBarTitle = "MyNotice";
                    menu_items.findItem(R.id.my_notice_action).setIcon(R.drawable.ic_person_white_18dp);
                }
                else
                {
                    ((DashBoard) getActivity()).setActionBarTitle(GlobalNames.getAdminNotice());
                    actionBarTitle = GlobalNames.getAdminNotice();
                    initiateData();
                    menu_items.findItem(R.id.my_notice_action).setIcon(R.drawable.ic_person_white_18dp_not_selected);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////ON ACTIVITY FOR RESULT/////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GlobalNames.getNchatToNotices()){


            if(data.getBooleanExtra(GlobalNames.getIsDataAddedIntent(),true))
            {
                Cursor cursor = sqLiteDataBaseHelperClass.getData(noticeListProvider.get(clickedPosition).getKey());

                if(cursor == null || cursor.getCount() == 0)
                {
                    sqLiteDataBaseHelperClass
                            .insertData(noticeListProvider.get(clickedPosition).getKey(),msg_number.get(clickedPosition));
                }
                else
                {
                    sqLiteDataBaseHelperClass.updateData(noticeListProvider.get(clickedPosition).getKey(),msg_number.get(clickedPosition));
                }
            }

            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(clickedPosition);

            if(holder == null)
            {
                return;
            }

            TextView textView = (TextView) holder.itemView.findViewById(R.id.chat_cnt);
            textView.setBackgroundResource(R.drawable.round);
            textView.setTextColor(Color.rgb(170,170,170));
            isLoadingOrUpdating.set(clickedPosition,true);
            noticeListAdapter.notifyItemChanged(clickedPosition);

            isOpenOrNot.set(clickedPosition,false);

            openedNotice = GlobalNames.getNONE();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////INITIATE DATA/////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initiateData(){

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());

        if(connectionCheck.checkingConnextion())
        {
            noticeMessageLayout.setVisibility(View.VISIBLE);
            noticeMessageIV.setImageResource(R.mipmap.no_connection);
            noticeMessageTV.setText(getString(R.string.no_network));
            return;
        }

        if((sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getUniversityOrCollege() ||
                sp.getInt(AllPreferences.getGroupType(),0) == GlobalNames.getSCHOOL())   &&
        (sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ","")).equals(GlobalNames.getDummyNode()))
        {
            Snackbar.make(getView(),"Please select year first.",Snackbar.LENGTH_LONG).show();
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

        initiallyLoadingDataChanged = true;

        if(noticeListProvider.size() != 0)
        {
            noticeListProvider.clear();
            userProfiles.clear();
            userIDKeyList.clear();
            noticeListAdapter.notifyDataSetChanged();
        }

        load_data_progressBar.setVisibility(View.VISIBLE);

        noticeListProvider = new ArrayList<>();
        loadedKeyList = new ArrayList<>();
        msg_number = new ArrayList<>();
        cursorArrayList = new ArrayList<>();
        numOfMessages = new ArrayList<>();
        isLoadingOrUpdating = new ArrayList<>();
        isOpenOrNot = new ArrayList<>();
        isImportant = new ArrayList<>();
        userProfiles = new ArrayList<>();
        userIDKeyList = new ArrayList<>();

        notice.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!notice.getParent().getParent().getKey()
                        .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&

                        !notice.getParent().getKey()
                                .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                {
                    return;
                }

                Iterator iterator = dataSnapshot.getChildren().iterator();

                if(!iterator.hasNext()) {

                    isInitialLoading = false;
                    load_data_progressBar.setVisibility(View.GONE);
                    initiallyLoadingDataChanged = false;
                    noticeSwipeRefresh.setRefreshing(false);
                    isLoading = false;
                    noticeMessageLayout.setVisibility(View.VISIBLE);
                    noticeMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);

                    if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
                    {
                        noticeMessageTV.setText(getString(R.string.no_group_selected));
                    }
                    else
                    {
                        noticeMessageTV.setText(getString(R.string.no_data));
                    }

                    return;
                }

                noticeMessageLayout.setVisibility(View.GONE);


                while(iterator.hasNext()){
                    noticeListProvider.add(0,((DataSnapshot)iterator.next()).getValue(NoticeListProvider.class));
                    userProfiles.add(0,new UserProfile());
                    userIDKeyList.add(0,noticeListProvider.get(0).getUid());
                }

                numOfMessage(true,0,0, false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
                {
                    noticeMessageTV.setText(getString(R.string.no_group_selected));
                }
                else
                {
                    noticeMessageTV.setText(databaseError.getMessage());
                }

                isInitialLoading = false;
                load_data_progressBar.setVisibility(View.GONE);
                initiallyLoadingDataChanged = false;
                noticeSwipeRefresh.setRefreshing(false);
                isLoading = false;
                noticeMessageLayout.setVisibility(View.VISIBLE);
                noticeMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                noticeMessageTV.setText(getString(R.string.no_data));
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////LOAD EARLIER///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadEarlier() {

        if(noticeListProvider.size() == 0) {
            initiateData();
            return;
        }

        earlierLoadingDataChanged = true;

        progressBar.setVisibility(View.VISIBLE);
        noticeSwipeRefresh.setPadding(0,0,0,60);

        String key = noticeListProvider.get(noticeListProvider.size() - 1).getKey();

        final int oldSize = noticeListProvider.size();

        notice.orderByChild("key").limitToLast(11).endAt(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    noticeListProvider.remove(noticeListProvider.size() - 1);
                    userProfiles.remove(userProfiles.size() - 1);
                    msg_number.remove(msg_number.size() - 1);
                    isImportant.remove(isImportant.size() - 1);
                    noticeListAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    noticeSwipeRefresh.setPadding(0,0,0,0);
                    isLoading = false;
                    earlierLoadingDataChanged = false;
                    return;
                }

                if(!notice.getParent().getParent().getKey()
                        .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&

                        !notice.getParent().getKey()
                                .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                {

                    return;
                }

                Iterator iterator = dataSnapshot.getChildren().iterator();
                ArrayList<NoticeListProvider> list = new ArrayList<>();
                ArrayList<String> stringArrayList = new ArrayList<>();
                ArrayList<UserProfile> userProfileArrayList = new ArrayList<>();

                while(iterator.hasNext()){
                    list.add(0,((DataSnapshot)iterator.next()).getValue(NoticeListProvider.class));
                    stringArrayList.add(0,list.get(0).getUid());
                    userProfileArrayList.add(0,new UserProfile());
                }

                if((list.get(0).getKey()
                        .equals(noticeListProvider.get(noticeListProvider.size() - 1).getKey())))
                {
                    if(dataSnapshot.getChildrenCount() > 1)
                    {
                        list.remove(0);
                        userProfileArrayList.remove(0);
                        stringArrayList.remove(0);
                    }
                    else if(dataSnapshot.getChildrenCount() == 1)
                    {
                        progressBar.setVisibility(View.GONE);
                        noticeSwipeRefresh.setPadding(0,0,0,0);
                        isLoading = false;
                        earlierLoadingDataChanged = false;
                        return;
                    }
                }

                noticeListProvider.addAll(list);
                userProfiles.addAll(userProfileArrayList);
                userIDKeyList.addAll(stringArrayList);
                numOfMessage(false,oldSize,oldSize, false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////GET MY ACH INITIAL KEY//////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getMyNoticeInitialKeys(){

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());

        if(connectionCheck.checkingConnextion())
        {
            noticeMessageLayout.setVisibility(View.VISIBLE);
            noticeMessageIV.setImageResource(R.mipmap.no_connection);
            noticeMessageTV.setText(getString(R.string.no_network));
            return;
        }

        load_data_progressBar.setVisibility(View.VISIBLE);

        if(noticeListProvider.size() != 0)
        {
            noticeListProvider.clear();
            userProfiles.clear();
            userIDKeyList.clear();
            noticeListAdapter.notifyDataSetChanged();
        }

        my_notices = new ArrayList<>();

        root.child(noticeRefStr)
                .child(GlobalNames.getMyNotice())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.getRef().getKey()
                        .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                {
                    return;
                }

                Iterator iterator = dataSnapshot.getChildren().iterator();

                if(!iterator.hasNext())
                {
                    initiallyLoadingDataChanged = false;
                    load_data_progressBar.setVisibility(View.GONE);
                    noticeSwipeRefresh.setRefreshing(false);
                    noticeMessageLayout.setVisibility(View.VISIBLE);
                    noticeMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    noticeMessageTV.setText(getString(R.string.no_data));
                    return;
                }

                noticeMessageLayout.setVisibility(View.GONE);

                while (iterator.hasNext()) {
                    my_notices.add(0,((DataSnapshot)iterator.next()).getValue(My_Notice.class));
                }

                new_first_key = my_notices.get(0).getKey();
                oldest_last_key = my_notices.get(my_notices.size() - 1).getKey();

                loadInitialMyNotice();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                load_data_progressBar.setVisibility(View.GONE);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////LOAD INITIAL MY ACH//////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadInitialMyNotice(){

        noticeListProvider = new ArrayList<>();
        loadedKeyList = new ArrayList<>();
        msg_number = new ArrayList<>();
        cursorArrayList = new ArrayList<>();
        numOfMessages = new ArrayList<>();
        isLoadingOrUpdating = new ArrayList<>();
        isOpenOrNot = new ArrayList<>();
        isImportant = new ArrayList<>();
        userProfiles = new ArrayList<>();
        userIDKeyList = new ArrayList<>();

        olderSize = 0;
        counter = 0;

        DatabaseReference refToNotice;

        initiallyLoadingDataChanged = true;

        for (int i= 0; i < my_notices.size() ; i++)
        {
            final int finalI = i;

            if(my_notices.get(finalI).getField_key().equals(GlobalNames.getInstitute()))
            {
                refToNotice = root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(my_notices.get(finalI).getGroup_key())
                        .child(GlobalNames.getInstitute());
            }
            else
            {
                refToNotice = root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(my_notices.get(finalI).getGroup_key())
                        .child(my_notices.get(finalI).getField_key())
                        .child(my_notices.get(finalI).getYear());
            }

            noticeListProvider.add(new NoticeListProvider());
            userIDKeyList.add(GlobalNames.getNONE());
            userProfiles.add(new UserProfile());

            refToNotice.child(my_notices.get(i).getKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            counter++;

                            if(!dataSnapshot.getRef().getParent().getParent().getKey()
                                    .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                                    !dataSnapshot.getRef().getParent().getParent().getParent().getKey()
                                            .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                            {
                                return;
                            }

                            noticeListProvider.set(finalI,dataSnapshot.getValue(NoticeListProvider.class));
                            userIDKeyList.set(finalI,dataSnapshot.getValue(NoticeListProvider.class).getUid());

                            if(counter == my_notices.size())
                            {
                                numOfMessage(true,0,0, false);
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
    private void myNoticeEarlierKeys(){

        if(my_notices.size() == 0){
            getMyNoticeInitialKeys();
            return;
        }
        final int myOlderSize = my_notices.size();

        progressBar.setVisibility(View.VISIBLE);
        noticeSwipeRefresh.setPadding(0,0,0,60);

        root.child(noticeRefStr)
                .child(GlobalNames.getMyNotice())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .orderByKey().limitToLast(11).endAt(oldest_last_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.getRef().getKey()
                        .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                {
                    return;
                }

                Iterator iterator = dataSnapshot.getChildren().iterator();

                if(dataSnapshot.getChildrenCount() == 1){
                    load_data_progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    earlierLoadingDataChanged = false;
                    noticeSwipeRefresh.setPadding(0,0,0,0);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                ArrayList<My_Notice> myTempNotice = new ArrayList<>();

                while (iterator.hasNext())
                {
                    myTempNotice.add(0,((DataSnapshot)iterator.next()).getValue(My_Notice.class));
                }

                myTempNotice.remove(0);

                my_notices.addAll(myTempNotice);

                oldest_last_key = my_notices.get(my_notices.size() - 1).getKey();

                loadMyNoticeEarlier(myOlderSize,my_notices.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// LOAD MY ACH EARLIER ////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadMyNoticeEarlier(final int previousSize, final int newSize){

        olderSize = noticeListProvider.size();

        earlierLoadingDataChanged = true;

        DatabaseReference referenceToNotice;

        for (int i= previousSize ; i< newSize ; i++){

            final int finalI = i;

            if(my_notices.get(finalI).getField_key().equals(GlobalNames.getInstitute()))
            {
                referenceToNotice = root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(GlobalNames.getInstitute());
            }
            else
            {

                referenceToNotice = root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(my_notices.get(finalI).getGroup_key())
                        .child(my_notices.get(finalI).getField_key())
                        .child(my_notices.get(finalI).getYear());
            }

            noticeListProvider.add(new NoticeListProvider());
            userIDKeyList.add(GlobalNames.getNONE());
            userProfiles.add(new UserProfile());

            referenceToNotice.child(my_notices.get(i).getKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            counter++;

                            if(!dataSnapshot.getRef().getParent().getParent().getKey()
                                    .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                                    !dataSnapshot.getRef().getParent().getParent().getParent().getKey()
                                            .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                            {
                                return;
                            }

                            noticeListProvider.set(finalI,dataSnapshot.getValue(NoticeListProvider.class));
                            userIDKeyList.set(finalI,dataSnapshot.getValue(NoticeListProvider.class).getUid());

                            if(my_notices.size() == counter) {
                                numOfMessage(false,olderSize,olderSize, false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON REFRESH////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRefresh() {

        notice_message_new_notice_card_view.setVisibility(View.GONE);

        if(isLongPressed){
            noticeSwipeRefresh.setRefreshing(false);
            return;
        }

        noticeSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {

                if(isMyNotice)
                {
                    if(noticeListProvider.size() == 0)
                    {
                        noticeSwipeRefresh.setRefreshing(false);
                        getMyNoticeInitialKeys();
                    }
                    else
                    {
                        newlyAddedMyNoticeKeys();
                    }
                }
                else
                {
                    if (noticeListProvider.size() == 0)
                    {
                        noticeSwipeRefresh.setRefreshing(false);
                        initiateData();
                    }
                    else
                    {
                        newlyAddedData();
                    }
                }
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////NEWLY ADDED DATA//////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void newlyAddedData(){

        String key = noticeListProvider.get(0).getKey();

        final int oldListSize = noticeListProvider.size();

        newlyLoadingDataChanged = true;

        notice.orderByChild("key").limitToFirst(11).startAt(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    noticeListProvider.remove(0);
                    userProfiles.remove(0);
                    msg_number.remove(0);
                    isImportant.remove(0);
                    noticeListAdapter.notifyDataSetChanged();
                    noticeSwipeRefresh.setRefreshing(false);
                    newlyLoadingDataChanged = false;

                    if(noticeListProvider.size() == 0)
                    {
                        noticeMessageLayout.setVisibility(View.VISIBLE);
                        noticeMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                        noticeMessageTV.setText(getString(R.string.no_data));
                    }

                    return;
                }

                if(!notice.getParent().getParent().getKey()
                        .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&

                        !notice.getParent().getKey()
                                .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                {

                    return;
                }

                Iterator iterator = dataSnapshot.getChildren().iterator();

                if(((DataSnapshot)iterator.next()).getKey().equals(noticeListProvider.get(0).getKey()))
                {
                    if(dataSnapshot.getChildrenCount() == 1)
                    {
                        noticeSwipeRefresh.setRefreshing(false);
                        newlyLoadingDataChanged = false;
                        return;
                    }
                }
                else
                {
                    iterator = dataSnapshot.getChildren().iterator();
                }

                while(iterator.hasNext()){
                    noticeListProvider.add(0,((DataSnapshot)iterator.next()).getValue(NoticeListProvider.class));
                    userIDKeyList.add(0,noticeListProvider.get(0).getUid());
                    userProfiles.add(0,new UserProfile());
                }

                SharedPreferences.Editor editor  = sp.edit();
                editor.putString(AllPreferences.getPreviousFacultyKey(),noticeListProvider.get(0).getKey());
                editor.apply();
                editor.clear();

                numOfMessage(false,oldListSize,0, true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////NEWLY ADDED MY_NOTICE_KEY//////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void newlyAddedMyNoticeKeys(){

        final int olderMyNoticeSize = my_notices.size();

        root.child(noticeRefStr)
                .child(GlobalNames.getMyNotice())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .orderByKey().startAt(new_first_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists())
                {
                    newlyLoadingDataChanged = false;
                    noticeSwipeRefresh.setRefreshing(false);
                    return;
                }

                if(!dataSnapshot.getRef().getKey()
                        .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                {
                    return;
                }

                Iterator iterator = dataSnapshot.getChildren().iterator();

                if(((DataSnapshot)iterator.next()).getValue(My_Notice.class).getKey()
                        .equals(noticeListProvider.get(0).getKey()))
                {
                    if(dataSnapshot.getChildrenCount() == 1)
                    {
                        newlyLoadingDataChanged = false;
                        noticeSwipeRefresh.setRefreshing(false);
                        return;
                    }
                }
                else
                {
                    iterator = dataSnapshot.getChildren().iterator();
                }

                while (iterator.hasNext()){
                    my_notices.add(0,((DataSnapshot)iterator.next()).getValue(My_Notice.class));
                }

                new_first_key = my_notices.get(0).getKey();

                newlyAddedMyNotice(olderMyNoticeSize,my_notices.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////NEWLY ADDED MY NOTICE////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void newlyAddedMyNotice(int oldeMyNoticeSize,int newMyNoticeSize) {

        DatabaseReference referenceToNotice;

        final int olderSize = noticeListProvider.size();
        newlyLoadingDataChanged = true;

        for (int i = newMyNoticeSize - oldeMyNoticeSize - 1  ; i >=0  ; i--) {

            final int finalI = i;

            if(my_notices.get(finalI).getField_key().equals(GlobalNames.getInstitute()))
            {
                referenceToNotice = root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(GlobalNames.getInstitute());
            }
            else
            {
                referenceToNotice = root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(my_notices.get(finalI).getGroup_key())
                        .child(my_notices.get(finalI).getField_key())
                        .child(my_notices.get(finalI).getYear());
            }

            noticeListProvider.add(0,new NoticeListProvider());
            userIDKeyList.add(0,GlobalNames.getNONE());
            userProfiles.add(0,new UserProfile());

            referenceToNotice.child(my_notices.get(i).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.getRef().getParent().getParent().getKey()
                            .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                            !dataSnapshot.getRef().getParent().getParent().getParent().getKey()
                                    .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())))
                    {
                        return;
                    }

                    counter++;

                    noticeListProvider.set(finalI,dataSnapshot.getValue(NoticeListProvider.class));
                    userIDKeyList.set(finalI,noticeListProvider.get(finalI).getUid());

                    if(counter == my_notices.size())
                    {

                        numOfMessage(false,olderSize,0, true);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////CHAT COUNT/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void numOfMessage(final boolean isInitial, final int olderSize, final int initialCNT, final boolean isNew){

        final int newlyAddeddata;

        if(isInitial || isNew)
        {
            newlyAddeddata = noticeListProvider.size() - olderSize;
        }
        else
        {
            newlyAddeddata = noticeListProvider.size();
        }

        for(int i=initialCNT;i<newlyAddeddata;i++)
        {
            cursorArrayList.add(i, sqLiteDataBaseHelperClass.getData(noticeListProvider.get(i).getKey()));

            if(cursorArrayList.get(i) != null && cursorArrayList.get(i).getCount() > 0)
            {
                cursorArrayList.get(i).moveToFirst();
                numOfMessages.add(i,cursorArrayList.get(i).getInt(2));
            }
            else
            {
                numOfMessages.add(i,0);
            }

            if(sqLiteDataBaseCheckChange.getData(noticeListProvider.get(i).getKey()).getCount() != 0)
            {
                isImportant.add(i,false);
            }
            else
            {
                isImportant.add(i,true);
            }

            final int finalI = i;


            loadedKeyList.add(finalI,noticeListProvider.get(i).getKey());
            isLoadingOrUpdating.add(finalI,true);
            msg_number.add(finalI,0);
            isOpenOrNot.add(finalI,false);

            chat_cnt.child(noticeListProvider.get(i).getKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        if((initiallyLoadingDataChanged ||
                                earlierLoadingDataChanged ||
                                newlyLoadingDataChanged)   &&
                                !loadedKeyList.toString().matches("\\[.*\\b" + dataSnapshot.getKey() + "\\b.*]"))
                        {
                            if(finalI + 1 >  msg_number.size() ||
                                    ((!notice.getParent().getParent().getKey()
                                            .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                                            (!notice.getParent().getKey()
                                                    .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))))))
                            {
                                return;
                            }

                            msg_number.set(finalI, (int) dataSnapshot.getChildrenCount());
                            loadedKeyList.set(finalI,dataSnapshot.getKey());

                            if(cursorArrayList.get(finalI).getCount() > 0)
                            {
                                if(numOfMessages.get(finalI) < (int) dataSnapshot.getChildrenCount() && isImportant.get(finalI))
                                {
                                    playSound.play();
                                    isLoadingOrUpdating.set(finalI,false);
                                }
                                else
                                {
                                    isLoadingOrUpdating.set(finalI,true);
                                }
                            }
                            else if(isImportant.get(finalI) && !newlyLoadingDataChanged)
                            {
                                playSound.play();
                                isLoadingOrUpdating.set(finalI,false);
                            }
                        }
                        else
                        {
                            for(int k=0;k<noticeListProvider.size();k++)
                            {
                                if(noticeListProvider.get(k).getKey().equals(dataSnapshot.getKey()))
                                {
                                    if(!openedNotice.equals(dataSnapshot.getKey())
                                            && !isOpenOrNot.get(finalI)
                                            && isImportant.get(finalI)
                                            && dataSnapshot.getChildrenCount() > msg_number.get(finalI) )
                                    {
                                        playSound.play();
                                        isLoadingOrUpdating.set(k,false);
                                    }

                                    msg_number.set(k, (int) dataSnapshot.getChildrenCount());
                                    noticeListAdapter.notifyItemChanged(k,null);
                                    break;
                                }
                            }
                            return;
                        }
                    }
                    else
                    {
                        if(finalI + 1 >  msg_number.size() ||
                                ((!notice.getParent().getParent().getKey()
                                        .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE())) &&
                                        (!notice.getParent().getKey()
                                                .equals(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))))) )
                        {
                            return;
                        }
                        loadedKeyList.set(finalI,dataSnapshot.getKey());
                        isLoadingOrUpdating.set(finalI,true);
                        msg_number.set(finalI,0);
                    }

                    if(finalI == newlyAddeddata - 1 )
                    {

                        if(isInitial)
                        {
                            noticeListAdapter = new NoticeListAdapter(noticeListProvider,userProfiles,
                                    getActivity(),
                                    msg_number,
                                    isLoadingOrUpdating,
                                    isImportant,
                                    new NoticeListAdapter.NoticeInterface() {
                                        @Override
                                        public void onNoticeClick(int position) {
                                            handleNoticeClickEvent(position);
                                        }

                                        @Override
                                        public void deleteItem(int position, boolean isChecked) {
                                            handleDeleteItemChecked(position,isChecked);
                                        }

                                        @Override
                                        public void onChkBoxChecked(boolean isChecked,int position) {
                                            handleCheckBoxChanged(isChecked,position);
                                        }

                                        @Override
                                        public void onLongClicked(int position,boolean isChecked) {
                                            handleLongPressed(position,isChecked);
                                        }
                                    });

                            recyclerView.setAdapter(noticeListAdapter);

                            recyclerView.removeItemDecoration(headerDecoration);
                            headerDecoration = new StickyHeaderDecoration(noticeListAdapter);
                            recyclerView.addItemDecoration(headerDecoration,1);

                            load_data_progressBar.setVisibility(View.GONE);

                        }
                        else if(isNew)
                        {
                            noticeListAdapter.notifyItemRangeInserted(0,noticeListProvider.size() - olderSize);
                            recyclerView.scrollToPosition(0);
                        }
                        else
                        {
                            noticeListAdapter.notifyItemRangeInserted(olderSize,noticeListProvider.size());
                        }

                        isLoading = false;

                        getUserProfiles(initialCNT,newlyAddeddata);

                        if(progressBar.isShown()){
                            progressBar.setVisibility(View.GONE);
                            noticeSwipeRefresh.setPadding(0,0,0,0);
                        }

                        initiallyLoadingDataChanged = false;
                        earlierLoadingDataChanged = false ;
                        newlyLoadingDataChanged = false;

                        isInitialLoading = false;
                    }

                    if(noticeSwipeRefresh.isRefreshing())
                    {
                        noticeSwipeRefresh.setRefreshing(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN ITEM CLICKED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUserProfiles(int initial_cnt, int new_data){

        for(int i = initial_cnt ; i<new_data ; i++)
        {
            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getUserGroupDetail())
                    .child(GlobalNames.getUserProfile())
                    .child(noticeListProvider.get(i).getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(userIDKeyList.toString().matches("\\[.*\\b" + dataSnapshot.getKey() + "\\b.*]"))
                            {
                                for(int k=0 ; k< noticeListProvider.size() ; k++)
                                {
                                    if(userIDKeyList.get(k).equals(dataSnapshot.getKey()))
                                    {
                                        userProfiles.set(k,dataSnapshot.getValue(UserProfile.class));
                                    }
                                }
                                noticeListAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN ITEM CLICKED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleNoticeClickEvent(int position){

        if(sqLiteDataBaseHelperClass.getData(noticeListProvider.get(position).getKey()).getCount() > 0)
        {
            sqLiteDataBaseHelperClass.updateData(noticeListProvider.get(position).getKey(),msg_number.get(position));
        }
        else
        {
            sqLiteDataBaseHelperClass.insertData(noticeListProvider.get(position).getKey(),msg_number.get(position));
        }

        clickedPosition = position;
        isOpenOrNot.set(position,true);
        progressBar.setVisibility(View.GONE);
        openedNotice = noticeListProvider.get(position).getKey();

        Intent intent = new Intent(getActivity(),Notices_Chat.class);
        intent.putExtra(GlobalNames.getNoticeToDetail(),noticeListProvider.get(position));
        intent.putExtra(getResources().getString(R.string.user_profile_intent),userProfiles.get(position));
        startActivityForResult(intent,GlobalNames.getNchatToNotices());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN CHECK BOX CHANGE//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleCheckBoxChanged(boolean isChecked,int position){

        if(!isChecked)
        {
            Toast.makeText(getActivity(), "Marked as not important", Toast.LENGTH_SHORT).show();
            sqLiteDataBaseCheckChange.insertData(noticeListProvider.get(position).getKey());
            isLoadingOrUpdating.set(position,true);
        }
        else
        {
            Toast.makeText(getActivity(), "Marked as important", Toast.LENGTH_SHORT).show();

            sqLiteDataBaseCheckChange.deleteData(noticeListProvider.get(position).getKey());

            if(sqLiteDataBaseHelperClass.getData(noticeListProvider.get(position).getKey()).getCount() > 0)
            {
                sqLiteDataBaseHelperClass.updateData(noticeListProvider.get(position).getKey(),msg_number.get(position));
            }
            else
            {
                sqLiteDataBaseHelperClass.insertData(noticeListProvider.get(position).getKey(),msg_number.get(position));
            }
        }

        isImportant.set(position,isChecked);
        noticeListAdapter.notifyItemChanged(position,null);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////HANDLE LONG PRESSED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleLongPressed(int position,boolean isChecked){

        if(!isFaculty)
        {
            handleNoticeClickEvent(position);
        }
        else if(!isLongPressed && isMyNotice)
        {
            isLongPressed = true;
            dataToBeDeleted = new ArrayList<>();
            userProfileToBeDeleted = new ArrayList<>();
            msgNumberToBeDeleted = new ArrayList<>();
            isImportantToBeDeleted = new ArrayList<>();
            myNoticeToBeDeleted = new ArrayList<>();
            noticeListAdapter.notifyDataSetChanged();
            ((DashBoard)getActivity()).onLongPress(isLongPressed);
            setTitle();

            wishedTodelete = new ArrayList<>();

            for(int i=0;i<noticeListProvider.size();i++){
                wishedTodelete.add(false);
            }

            handleDeleteItemChecked(position,isChecked);
        }
        else if(isLongPressed && isMyNotice){
            handleDeleteItemChecked(position,isChecked);
        }
        else
        {
            handleNoticeClickEvent(position);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////SET TITLE////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setTitle(){

        if(isLongPressed)
        {
            ((DashBoard)getActivity()).setActionBarTitle(dataToBeDeleted.size() + " items selected");
        }
        else
        {
            ((DashBoard)getActivity()).setActionBarTitle(actionBarTitle);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN BACK PRESSED//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void backPressed(boolean isTabChanged)
    {
        onCreateOptionsMenu(menu_items,getActivity().getMenuInflater());
        isLongPressed = false;

        if(noticeListProvider != null && noticeListProvider.size() > 0 &&
                (!isTabChanged ||  dataToBeDeleted != null))
        {
            noticeListAdapter.notifyDataSetChanged();
            dataToBeDeleted = null;
        }
        setTitle();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////NOTICE IS SELECTED FROM TAB/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void notice_selected(){

        while(true)
        {
            if(sp != null)
            {
                break;
            }
        }

        if(noticeListProvider == null || noticeListProvider.size() == 0){

            isFaculty = true;
            isMyNotice = false;

            ((DashBoard)getActivity()).setActionBarTitle("AdminNotice");
            actionBarTitle = "AdminNotice";

            notice = root.child(noticeRefStr)
                    .child(noticeMsg)
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .child(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()))
                    .child(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ",""));

            chat_cnt = root.child(noticeRefStr)
                    .child(GlobalNames.getNoticeChat())
                    .child(GlobalNames.getChatAndCount())
                    .child(GlobalNames.getChatCount());

            if(isMyNotice)
            {
                getMyNoticeInitialKeys();
            }
            else
            {
                initiateData();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////SET OPTION MENU///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setOptionMenu()
    {
        onCreateOptionsMenu(menu_items,getActivity().getMenuInflater());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////YEAR CHANGED///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void yearChanged(boolean isSelected)
    {
        isFaculty = true;
        isMyNotice = false;

        if(isSelected)
        {
            onCreateOptionsMenu(menu_items,getActivity().getMenuInflater());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////HANDLE DATA TO BE DELETED IS CHECKED OR NOT///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void clearList()
    {
        noticeListProvider.clear();
        msg_number.clear();
        isLoadingOrUpdating.clear();
        isImportant.clear();
        userProfiles.clear();
        userIDKeyList.clear();
        isLoading = false;
        isInitialLoading = false;
        earlierLoadingDataChanged = false;
        initiallyLoadingDataChanged = false;
        newlyLoadingDataChanged = false;
        isFaculty = true;
        isMyNotice = false;
        isNewNoticeInitializing = true;

        if(noticeListAdapter != null)
        {
            noticeListAdapter.notifyDataSetChanged();
        }
    }
    /////////////////////////////////////
    /////NEW NOTICE AVAILABLE////////////
    /////////////////////////////////////
    public void newNoticeAvailable(String str)
    {

        if(isNewNoticeInitializing)
        {
            isNewNoticeInitializing = false;
        }
        else
        {
            new_notice_tv.setText(str);
            notice_message_new_notice_card_view.setVisibility(View.VISIBLE);
        }

    }

    /////////////////////////////////////
    /////NEW NOTICE AVAILABLE////////////
    /////////////////////////////////////
    public void hideCardView(){
        notice_message_new_notice_card_view.setVisibility(View.GONE);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////HANDLE DATA TO BE DELETED IS CHECKED OR NOT///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleDeleteItemChecked(int clickedPosition,boolean isChecked){

        if(isChecked)
        {
            dataToBeDeleted.add(noticeListProvider.get(clickedPosition));
            userProfileToBeDeleted.add(userProfiles.get(clickedPosition));
            msgNumberToBeDeleted.add(msg_number.get(clickedPosition));
            isImportantToBeDeleted.add(isImportant.get(clickedPosition));
            myNoticeToBeDeleted.add(my_notices.get(clickedPosition));
            wishedTodelete.set(clickedPosition,true);
        }
        else
        {
            dataToBeDeleted.remove(noticeListProvider.get(clickedPosition));
            userProfileToBeDeleted.remove(userProfiles.get(clickedPosition));
            msgNumberToBeDeleted.remove(msg_number.get(clickedPosition));
            isImportantToBeDeleted.remove(isImportant.get(clickedPosition));
            myNoticeToBeDeleted.remove(my_notices.get(clickedPosition));
            wishedTodelete.set(clickedPosition,false);
        }
        noticeListAdapter.notifyItemChanged(clickedPosition,null);
        setTitle();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////DELETE SELECTED ITEMS////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void deleteItems(){

        if(dataToBeDeleted == null || dataToBeDeleted.size() == 0){
            Toast.makeText(getActivity(), "You haven't choose any Notice yet", Toast.LENGTH_SHORT).show();
        }

        for(int i=0;i<dataToBeDeleted.size();i++)
        {
            root.child(noticeRefStr)
                    .child(GlobalNames.getMyNotice())
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .child(dataToBeDeleted.get(i).getKey()).removeValue();

            root.child(noticeRefStr)
                    .child(GlobalNames.getNoticeChat())
                    .child(GlobalNames.getChatAndCount())
                    .child(GlobalNames.getChatCount()).child(dataToBeDeleted.get(i).getKey()).removeValue();

            root.child(noticeRefStr)
                    .child(GlobalNames.getNoticeChat())
                    .child(GlobalNames.getChatAndCount())
                    .child(GlobalNames.getCHAT()).child(dataToBeDeleted.get(i).getKey()).removeValue();

            if(myNoticeToBeDeleted.get(i).getField_key().equals(GlobalNames.getInstitute()))
            {
                root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(GlobalNames.getInstitute())
                        .child(dataToBeDeleted.get(i).getKey()).removeValue();
            }
            else
            {
                root.child(noticeRefStr)
                        .child(noticeMsg)
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()))
                        .child(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ",""))
                        .child(myNoticeToBeDeleted.get(i).getKey()).removeValue();

            }

            sqLiteDataBaseCheckChange.deleteData(dataToBeDeleted.get(i).getKey());
            sqLiteDataBaseHelperClass.deleteData(dataToBeDeleted.get(i).getKey());

            noticeListProvider.remove(dataToBeDeleted.get(i));
            my_notices.remove(myNoticeToBeDeleted.get(i));
            userProfiles.remove(userProfileToBeDeleted.get(i));
            msg_number.remove(msgNumberToBeDeleted.get(i));
            isImportant.remove(isImportantToBeDeleted.get(i));
            counter--;
        }

        isLongPressed = false;
        noticeListAdapter.notifyDataSetChanged();
        ((DashBoard)getActivity()).onLongPress(isLongPressed);

        if(noticeListProvider != null && noticeListProvider.size() >  0)
        {
            new_first_key = noticeListProvider.get(0).getKey();
            oldest_last_key = noticeListProvider.get(noticeListProvider.size() - 1).getKey();
        }

        setTitle();
    }
}