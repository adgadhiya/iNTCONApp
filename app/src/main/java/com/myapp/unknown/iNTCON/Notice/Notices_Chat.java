package com.myapp.unknown.iNTCON.Notice;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class Notices_Chat extends AppCompatActivity {

    private RecyclerView rv_notice_chat;
    private RecyclerView.Adapter adapter;
    private ArrayList<NoticeChatListProvider> listProvider;
    private ArrayList<UserProfile> userProfiles;
    private EditText editText;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private TextView toolbar_delete_title_tv;

    static boolean isLongPressed;

    private DatabaseReference chat,chat_cnt;

    private NoticeListProvider provider;

    private ArrayList<Boolean> wishedToDelete;

    private ArrayList<NoticeChatListProvider> dataToBeDeleted;

    private Menu menu;

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE ACTIVITY//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.notice_chat_activity);

        provider = getIntent().getParcelableExtra(GlobalNames.getNoticeToDetail());
        UserProfile userProfile = getIntent().getParcelableExtra(getResources().getString(R.string.user_profile_intent));

        rv_notice_chat = (RecyclerView) findViewById(R.id.rv_notice_chat);
        fab = (FloatingActionButton) findViewById(R.id.notice_chat_fab);
        toolbar = (Toolbar) findViewById(R.id.notice_chat_toolbar);
        editText = (EditText) findViewById(R.id.notice_chat_et);
        TextView notice_Chat_notice_date = (TextView) findViewById(R.id.notice_chat_notice_date);
        TextView notice_chat_notice_message = (TextView) findViewById(R.id.notice_chat_notice_message);
        TextView notice_chat_notice_title = (TextView) findViewById(R.id.notice_chat_notice_title);
        TextView textView = (TextView) findViewById(R.id.name_toolbar);
        TextView time = (TextView) findViewById(R.id.time_toolbar);
        ImageView imageView = (ImageView) findViewById(R.id.profile_toolbar);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout_toolbar);
        toolbar_delete_title_tv = (TextView) findViewById(R.id.tv_delete_toolbar);

        ImageView home = (ImageView) findViewById(R.id.home);

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            textView.setText(userProfile.getUserName());
            time.setText(provider.getTime().toUpperCase());
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(25,118,210)));
        }


        time.setVisibility(View.VISIBLE);
        time.setText(provider.getTime());

        Glide.with(this)
                .load(userProfile.getUserProfile())
                .placeholder(R.mipmap.profile_default)
                .dontAnimate()
                .into(imageView);

        notice_Chat_notice_date.setText(provider.getDate());
        notice_chat_notice_title.setText(provider.getTitle());
        notice_chat_notice_message.setText(provider.getMessage());

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

        chat = root.child(GlobalNames.getNOTICE())
                .child(GlobalNames.getNoticeChat())
                .child(GlobalNames.getChatAndCount())
                .child(GlobalNames.getCHAT())
                .child(provider.getKey());

        chat_cnt = root.child(GlobalNames.getNOTICE())
                .child(GlobalNames.getNoticeChat())
                .child(GlobalNames.getChatAndCount())
                .child(GlobalNames.getChatCount())
                .child(provider.getKey());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChatMessage(editText.getText().toString());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rv_notice_chat.setLayoutManager(layoutManager);
        rv_notice_chat.setNestedScrollingEnabled(false);
        rv_notice_chat.setItemAnimator(null);

        wishedToDelete = new ArrayList<>();

        isLongPressed = false;

        listProvider = new ArrayList<>();
        userProfiles = new ArrayList<>();

        getdata();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON START////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.activityStarted();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON DESTROY//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        isLongPressed = false;
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON CREATE OPTION MENU//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.refresh_data,menu);

        this.menu = menu;

        menu.findItem(R.id.download_action).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.findItem(R.id.top_action).setVisible(false);

        if(!provider.getImg_path().equals("NONE"))
        {
            menu.findItem(R.id.download_action).setVisible(true);
        }

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON OPTION ITEM SELECTED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.download_action:
                getMimeFromDatabase();
                return true;

            case R.id.delete_items:
                deleteItems();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////GET DATA FROM DATABASE//////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getdata(){

        chat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator iterator = dataSnapshot.getChildren().iterator();

                ArrayList<NoticeChatListProvider> chatListProvider = new ArrayList<>();
                ArrayList<UserProfile> userProfileArrayList = new ArrayList<>();

                if(!iterator.hasNext())
                {
                    listProvider = new ArrayList<>();

                    adapter = new NoticeChatListAdapter(Notices_Chat.this,
                            listProvider,
                            userProfiles,
                            false,
                            wishedToDelete,
                            new NoticeChatListAdapter.NoticeChatInterface() {
                        @Override
                        public void onLongClicked(int position, boolean isChecked) {
                            handleLongClicked(position,isChecked);
                        }

                        @Override
                        public void onMessageClicked(int position, boolean isChecked) {
                            handleClicked(position, isChecked);
                        }
                    });

                    rv_notice_chat.setAdapter(adapter);
                }
                else if(dataSnapshot.getChildrenCount() < listProvider.size())
                {
                    wishedToDelete.clear();

                    while(iterator.hasNext())
                    {
                        chatListProvider.add(((DataSnapshot)iterator.next()).getValue(NoticeChatListProvider.class));
                        userProfileArrayList.add(new UserProfile());
                    }

                    listProvider.clear();
                    userProfiles.clear();

                    listProvider.addAll(chatListProvider);
                    userProfiles.addAll(userProfileArrayList);
                    getUserProfileList();
                    adapter.notifyDataSetChanged();

                    for (int i=0;i<chatListProvider.size();i++)
                    {
                        wishedToDelete.add(false);
                    }
                    return;
                }

                while(iterator.hasNext())
                {
                    chatListProvider.add(((DataSnapshot)iterator.next()).getValue(NoticeChatListProvider.class));
                    userProfileArrayList.add(new UserProfile());
                }

                if(listProvider == null || listProvider.size() == 0)
                {

                    for (int i=0;i<chatListProvider.size();i++)
                    {
                        wishedToDelete.add(false);
                    }

                    listProvider.addAll(chatListProvider);
                    userProfiles.addAll(userProfileArrayList);
                    getUserProfileList();

                    adapter = new NoticeChatListAdapter(Notices_Chat.this,
                            listProvider,
                            userProfiles,
                            false,
                            wishedToDelete,
                            new NoticeChatListAdapter.NoticeChatInterface() {
                        @Override
                        public void onLongClicked(int position, boolean isChecked) {
                            handleLongClicked(position,isChecked);
                        }

                        @Override
                        public void onMessageClicked(int position, boolean isChecked) {
                            handleClicked(position,isChecked);
                        }
                    });

                    rv_notice_chat.setAdapter(adapter);
                }
                else
                {
                    listProvider.add(chatListProvider.get(chatListProvider.size() - 1 ));
                    userProfiles.add(new UserProfile());
                    getUserProfile();
                    wishedToDelete.add(false);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                                userProfiles.set(finalI,dataSnapshot.getValue(UserProfile.class));
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
                                userProfiles.set(i,dataSnapshot.getValue(UserProfile.class));
                                adapter.notifyItemChanged(i);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON STOP////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        MyApplication.activityStopped();
        super.onStop();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ADD MESSAGE TO CHAT////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addChatMessage(String message){

        if(message.trim().length() == 0) {
            Toast.makeText(Notices_Chat.this, "Empty Message!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());

        String key = chat.push().getKey();

        NoticeChatListProvider noticeChatListProvider =
                new NoticeChatListProvider(dateFormat.format(new Date()),
                        message,
                        sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()),
                        key,
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        timeFormat.format(new Date()));

        chat.child(key).setValue(noticeChatListProvider);

        chat_cnt.child(key).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fab.getWindowToken(),0);

        editText.setText("");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////ON BACK PRESSED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

        if(isLongPressed)
        {
            toolbar.getMenu().clear();
            onCreateOptionsMenu(menu);
            isLongPressed = false;
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(25,118,210)));
            setTitle();

            for(int i=0;i<listProvider.size();i++)
            {
                wishedToDelete.set(i,false);
            }
            adapter.notifyDataSetChanged();

            toolbar_delete_title_tv.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);

            return;
        }


        Intent intent = new Intent();

        if(listProvider.size() > 0)
        {
            intent.putExtra(GlobalNames.getIsDataAddedIntent(),true);
        }
        else
        {
            intent.putExtra(GlobalNames.getIsDataAddedIntent(),false);
        }

        setResult(RESULT_OK,intent);

        super.onBackPressed();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////WHEN LONG CLICKED////////////////////////////////////////////////////////////
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

            handleClicked(position,isChecked);
        }
        else
        {
            handleClicked(position,isChecked);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////HANDLE WHEN CLICKED////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleClicked(int position, boolean isChecked){

        if(!isLongPressed){
            return;
        }

        if(isChecked)
        {
            dataToBeDeleted.add(listProvider.get(position));
            wishedToDelete.set(position,true);
        }
        else
        {
            dataToBeDeleted.remove(listProvider.get(position));
            wishedToDelete.set(position,false);
        }

        adapter.notifyDataSetChanged();
        setTitle();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////SET TITLE////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setTitle(){

        if(isLongPressed)
        {
            toolbar_delete_title_tv.setText(getString(R.string.item_selected,dataToBeDeleted.size()));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////DELETE CHAT MESSAGE////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void deleteItems(){

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(25,118,210)));

        if(dataToBeDeleted.size() == 0)
        {
            Toast.makeText(Notices_Chat.this, "You haven't choose any message yet!", Toast.LENGTH_SHORT).show();
        }

        for(int i=0;i<dataToBeDeleted.size();i++)
        {
            if(FirebaseAuth.getInstance().getCurrentUser().getUid()
                    .equals(dataToBeDeleted.get(i).getUid()))
            {
                chat.child(dataToBeDeleted.get(i).getKey()).removeValue();
                chat_cnt.child(dataToBeDeleted.get(i).getKey()).removeValue();
            }
        }

        wishedToDelete.clear();

        for(int j=0;j<listProvider.size();j++)
        {
            wishedToDelete.add(j,false);
        }

        adapter.notifyDataSetChanged();

        isLongPressed = false;
        setTitle();

        toolbar_delete_title_tv.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);

        toolbar.getMenu().clear();
        onCreateOptionsMenu(menu);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////GET MIME FROM DATABASE/////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getMimeFromDatabase(){

        if(!ImageSaver.isExternalStorageWritable()){
            Toast.makeText(Notices_Chat.this, "No Storage found.", Toast.LENGTH_SHORT).show();
            return;
        }

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(this);
        if(connectionCheck.checkingConnextion()){
            Toast.makeText(Notices_Chat.this, "Network Connection Error!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(Notices_Chat.this, "Downloading...", Toast.LENGTH_SHORT).show();

        FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://message-mi.appspot.com/")
                .child(provider.getImg_path()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                saveImageFromDatabase(storageMetadata.getName(),storageMetadata.getContentType());

            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Notices_Chat.this,e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////SAVE IMAGE FROM DATABASE///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void saveImageFromDatabase(final String fileName, final String mimeType){

        FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://message-mi.appspot.com/")
                .child(provider.getImg_path()).getBytes(5 * 1024 * 1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        File dataFile = null;

                        try {
                            File file = new File(Environment.getExternalStorageDirectory(),"iNTCON");
                            file.mkdirs();
                            dataFile = new File(file,fileName);

                            FileOutputStream fos;
                            fos = new FileOutputStream(dataFile);
                            fos.write(bytes);
                            fos.flush();
                            fos.close();

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(dataFile),mimeType);
                            startActivity(intent);

                        } catch (IOException ex) {
                            Toast.makeText(Notices_Chat.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(dataFile),"application/*");
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Notices_Chat.this, "File May be Corrupted", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
