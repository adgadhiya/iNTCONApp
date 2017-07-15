package com.myapp.unknown.iNTCON.Campaign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.Notice.NoticeChatListAdapter;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.Notice.NoticeChatListProvider;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class Campaign_Chat extends AppCompatActivity {

    private RecyclerView rv_notice_chat;
    private NoticeChatListAdapter adapter;
    private ArrayList<NoticeChatListProvider> listProvider;
    private EditText editText;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView toolbar_delete_title_tv;
    private LinearLayout linearLayout;

    private DatabaseReference chat;

    ValueEventListener valueEventListener;

    private boolean isDataResourceChat;

    GroupProfile groupProfile;
    UserProfile userProfile;

    String dateSTR;
    private String key,ref_path;

    private ArrayList<Boolean> wishedToDelete;
    private ArrayList<NoticeChatListProvider> dataToBeDeleted;
    private ArrayList<UserProfile> userProfileArrayList;

    public static boolean isLongPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_campaign__chat);

        rv_notice_chat = (RecyclerView) findViewById(R.id.rv_campaign_chat);
        fab = (FloatingActionButton) findViewById(R.id.campaign_send_fab);
        toolbar = (Toolbar) findViewById(R.id.campaign_chat_toolbar);
        editText = (EditText) findViewById(R.id.campaign_chat_et);
        ImageView group_profile_iv = (ImageView) findViewById(R.id.profile_toolbar);
        TextView groupName = (TextView) findViewById(R.id.name_toolbar);
        ImageView home = (ImageView) findViewById(R.id.home);
        TextView dateTV = (TextView) findViewById(R.id.time_toolbar);
        toolbar_delete_title_tv = (TextView) findViewById(R.id.tv_delete_toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout_toolbar);

        setSupportActionBar(toolbar);

        isLongPressed = false;


        isDataResourceChat = getIntent().getBooleanExtra(getString(R.string.is_data_resource_chat_intent),false);

        if(isDataResourceChat)
        {
            key = getIntent().getStringExtra("KEY");
            userProfile = getIntent().getParcelableExtra("USER_PROFILE");
            dateSTR = getIntent().getStringExtra("DATE");
            ref_path = getIntent().getStringExtra("REF_PATH");

            groupName.setText(userProfile.getUserName());
            dateTV.setText(dateSTR);
            dateTV.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(userProfile.getUserProfile())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(group_profile_iv);

            chat = FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getDataResource())
                    .child(GlobalNames.getCHAT())
                    .child(key);
        }
        else
        {
            key = getIntent().getStringExtra("KEY");
            groupProfile = getIntent().getParcelableExtra("GROUP_PROFILE");
            dateSTR = getIntent().getStringExtra("DATE");

            groupName.setText(groupProfile.getGroup_name());
            dateTV.setText(dateSTR);
            dateTV.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(groupProfile.getGroup_profile_path())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(group_profile_iv);

            chat = FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getCAMPAIGN())
                    .child(GlobalNames.getCHAT())
                    .child(key);

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChatMessage(editText.getText().toString());
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        rv_notice_chat.setLayoutManager(layoutManager);
        rv_notice_chat.setHasFixedSize(true);
        rv_notice_chat.setItemAnimator(null);

        wishedToDelete = new ArrayList<>();

        listProvider = new ArrayList<>();
        userProfileArrayList = new ArrayList<>();

        getdata();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON START//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////ON DESTROY///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        isLongPressed = false;
        chat.removeEventListener(valueEventListener);
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE OPTION MENU///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.refresh_data,menu);

        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////OPTION ITEM SELECTED///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.top_action:
                if(listProvider != null && listProvider.size() > 0)
                {
                    rv_notice_chat.smoothScrollToPosition(listProvider.size() - 1);
                }
                return true;

            case R.id.delete_items:
                deleteItems();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////GET CHAT MESSAGES FROM DATABASE//////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getdata(){

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                addChatCount(dataSnapshot);

                Iterator iterator = dataSnapshot.getChildren().iterator();

                ArrayList<NoticeChatListProvider> chatListProvider = new ArrayList<>();
                ArrayList<UserProfile> userProfiles = new ArrayList<>();

                if(dataSnapshot.getChildrenCount() < listProvider.size()){
                    wishedToDelete.clear();

                    while(iterator.hasNext())
                    {
                        chatListProvider.add(((DataSnapshot)iterator.next()).getValue(NoticeChatListProvider.class));
                        userProfiles.add(new UserProfile());
                    }

                    listProvider.clear();
                    userProfileArrayList.clear();

                    listProvider.addAll(chatListProvider);
                    userProfileArrayList.addAll(userProfiles);
                    getUserProfileList();
                    adapter.notifyDataSetChanged();

                    for (int i=0;i<chatListProvider.size();i++)
                    {
                        wishedToDelete.add(false);
                    }
                    return;
                }

                while(iterator.hasNext()){
                    chatListProvider.add(((DataSnapshot)iterator.next()).getValue(NoticeChatListProvider.class));
                    userProfiles.add(new UserProfile());
                }

                if(listProvider == null || listProvider.size() == 0)
                {
                    for (int i=0;i<chatListProvider.size();i++){
                        wishedToDelete.add(false);
                    }

                    listProvider.addAll(chatListProvider);
                    userProfileArrayList.addAll(userProfiles);
                    getUserProfileList();

                    adapter = new NoticeChatListAdapter(Campaign_Chat.this,
                            listProvider,
                            userProfileArrayList,
                            true,
                            wishedToDelete,new NoticeChatListAdapter.NoticeChatInterface() {
                        @Override
                        public void onLongClicked(int position, boolean isChecked) {
                            handleLongClicked(position, isChecked);
                        }

                        @Override
                        public void onMessageClicked(int position, boolean isChecked) {
                            handleClicked(position, isChecked);
                        }
                    });

                    rv_notice_chat.setAdapter(adapter);
                }
                else
                {
                    listProvider.add(chatListProvider.get(chatListProvider.size() - 1));
                    userProfileArrayList.add(new UserProfile());
                    wishedToDelete.add(false);
                    getUserProfile();
                    adapter.notifyDataSetChanged();
                    rv_notice_chat.scrollToPosition(listProvider.size() - 1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        chat.addValueEventListener(valueEventListener);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ADD CHAT COUNT//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addChatCount(DataSnapshot dataSnapshot) {

        if(dataSnapshot.exists())
        {
            if(isDataResourceChat)
            {
                FirebaseDatabase.getInstance().getReferenceFromUrl(ref_path)
                        .child(key)
                        .child("comment_cnt").setValue(dataSnapshot.getChildrenCount());
            }
            else
            {
                FirebaseDatabase.getInstance().getReference().getRoot()
                        .child(GlobalNames.getCAMPAIGN())
                        .child(GlobalNames.getCampaignAll())
                        .child(key)
                        .child(GlobalNames.getChatCount())
                        .setValue(dataSnapshot.getChildrenCount());
            }
        }
        else
        {

            if(!isDataResourceChat)
            {
                FirebaseDatabase.getInstance().getReference().getRoot()
                        .child(GlobalNames.getCAMPAIGN())
                        .child(GlobalNames.getCampaignAll())
                        .child(GlobalNames.getChatCount())
                        .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getCAMPAIGN())
                                    .child(GlobalNames.getCampaignAll())
                                    .child(key)
                                    .child(GlobalNames.getChatCount())
                                    .setValue(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else
            {
                FirebaseDatabase.getInstance().getReferenceFromUrl(ref_path)
                        .child(key)
                        .child("comment_cnt").setValue(0);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON STOP///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        MyApplication.activityStopped();
        super.onStop();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////GET USER PROFILE LIST////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUserProfileList(){

        for(int i=0 ; i<listProvider.size() ; i++)
        {
            final int finalI = i;
            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getUserGroupDetail())
                    .child(GlobalNames.getUserProfile())
                    .child(listProvider.get(i).getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists() &&
                                    (listProvider.size() >= finalI + 1)  &&
                                    listProvider.get(finalI).getUid().equals(dataSnapshot.getKey()))
                            {
                                userProfileArrayList.set(finalI,dataSnapshot.getValue(UserProfile.class));
                                adapter.notifyItemChanged(finalI);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////GET ADDED MESSAGE USER PROFILE////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUserProfile(){

        final int i = listProvider.size() - 1;

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserProfile())
                .child(listProvider.get(i).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists() &&
                                listProvider.size() - 1 >= i &&
                                listProvider.get(i).getUid().equals(dataSnapshot.getKey()))
                        {
                            userProfileArrayList.set(i,dataSnapshot.getValue(UserProfile.class));
                            adapter.notifyItemChanged(i);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////ADD CHAT TO DATA BASE//////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addChatMessage(String message){

            if(message.trim().length() == 0) {
                Toast.makeText(Campaign_Chat.this, "Empty Message!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

            String key = chat.push().getKey();

            NoticeChatListProvider noticeChatListProvider =
                    new NoticeChatListProvider(dateFormat.format(new Date()),
                            message,
                            sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()),
                            key,
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            timeFormat.format(new Date()));

            chat.child(key).setValue(noticeChatListProvider);

            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(fab.getWindowToken(),0);

            editText.setText("");
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON BACK PRESSED CLICKED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

        if(isLongPressed)
        {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.refresh_data);
            isLongPressed = false;
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(25,118,210)));
            setTitle();
            linearLayout.setVisibility(View.VISIBLE);
            toolbar_delete_title_tv.setVisibility(View.GONE);

            for(int i=0;i<listProvider.size();i++)
            {
                wishedToDelete.set(i,false);
            }
            adapter.notifyDataSetChanged();
            return;
        }

        super.onBackPressed();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////HANDLE ON LONG CLICKED/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleLongClicked(int position, boolean isChecked){

        toolbar_delete_title_tv.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        if(!isLongPressed)
        {
            isLongPressed = true;
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.delete);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(30,136,229)));
            dataToBeDeleted = new ArrayList<>();
            handleClicked(position, isChecked);
        }
        else
        {
            handleClicked(position,isChecked);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////HANDLE CLICK EVENT/////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleClicked(int position, boolean isChecked){

        if(!isLongPressed){
            return;
        }

        if(isChecked)
        {
            dataToBeDeleted.add(listProvider.get(position));
            wishedToDelete.set(position,isChecked);
        }
        else
        {
            dataToBeDeleted.remove(listProvider.get(position));
            wishedToDelete.set(position,isChecked);
        }

        adapter.notifyDataSetChanged();
        setTitle();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////SET ACTION BAR TITLE///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setTitle(){

        if(isLongPressed)
        {
            toolbar_delete_title_tv.setText(getString(R.string.item_selected,dataToBeDeleted.size()));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////DELETE CHAT MESSAGES FROM DATABSAE/////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void deleteItems(){

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(25,118,210)));
        linearLayout.setVisibility(View.VISIBLE);
        toolbar_delete_title_tv.setVisibility(View.GONE);

        if(dataToBeDeleted.size() == 0){
            Toast.makeText(Campaign_Chat.this, "You haven't choose any message yet!", Toast.LENGTH_SHORT).show();
        }

        for(int i=0;i<dataToBeDeleted.size();i++){

            if(FirebaseAuth.getInstance().getCurrentUser().getUid()
                    .equals(dataToBeDeleted.get(i).getUid()))
            {

                if(isDataResourceChat)
                {
                    FirebaseDatabase.getInstance().getReference()
                            .getRoot()
                            .child(GlobalNames.getDataResource())
                            .child(GlobalNames.getCHAT())
                            .child(key)
                            .child(dataToBeDeleted.get(i).getKey())
                            .removeValue();
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference()
                            .getRoot().child(GlobalNames.getCAMPAIGN())
                            .child(GlobalNames.getCHAT())
                            .child(key)
                            .child(dataToBeDeleted.get(i).getKey())
                            .removeValue();
                }
            }
        }

        isLongPressed = false;
        setTitle();

        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.refresh_data);
    }

}
