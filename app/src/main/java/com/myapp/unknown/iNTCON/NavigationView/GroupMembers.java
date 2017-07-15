package com.myapp.unknown.iNTCON.NavigationView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class GroupMembers extends AppCompatActivity implements SearchView.OnQueryTextListener {


    private RecyclerView recyclerView;
    private GroupMemberViewAdapter groupMemberViewAdapter;
    private ProgressBar progressBar;

    private ArrayList<UserProfile> userProfileArrayList;
    private ArrayList<Boolean> isAdmin;

    private SharedPreferences sp;

    private DatabaseReference refToMemberList;

    SearchView searchView;

    private LocalBroadcastManager bManager;
    public static final String AUTH_CHANGED = "com.myapp.unknown.iNTCON.NavigationView.AUTHENTICATION_CHANGED";


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE ACTIVITY////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_group_members);

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_members_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.group_members_rv);
        progressBar = (ProgressBar) findViewById(R.id.group_members_search_progressBar);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userProfileArrayList = new ArrayList<>();
        isAdmin = new ArrayList<>();

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        refToMemberList = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getGroupMemberList())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AUTH_CHANGED);
        bManager.registerReceiver(bReceiver, intentFilter);

        setRecyclerView();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////BROAD CAST RECEIVER///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(AUTH_CHANGED))
            {
                groupMemberViewAdapter.notifyDataSetChanged();
            }

        }
    };


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON DESTROY ACTIVITY////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        bManager.unregisterReceiver(bReceiver);
        super.onDestroy();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON CREATE OPTION MENU////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_group_action);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Search Group...");
        searchView.setOnQueryTextListener(this);
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON OPTION ITEM SELECTED////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////SET RECYCLER VIEW////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setRecyclerView(){

        refToMemberList.limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Iterator iterator = dataSnapshot.getChildren().iterator();

                    while(iterator.hasNext())
                    {
                        userProfileArrayList.add(((DataSnapshot)iterator.next()).getValue(UserProfile.class));
                    }

                    getUserAuthentication();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET USER OR ADMIN///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUserAuthentication()
    {
        final DatabaseReference refToAdmin = FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication());

        for(int i=0 ; i<userProfileArrayList.size() ; i++)
        {
            final int finalI = i;

            isAdmin.add(finalI,false);

            refToAdmin.child(userProfileArrayList.get(i).getUser_id())
                    .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(groupMemberViewAdapter == null)
                            {
                                if(dataSnapshot.exists())
                                {
                                    isAdmin.set(finalI,dataSnapshot.getValue(Boolean.class));
                                }
                                else
                                {
                                    isAdmin.set(finalI,false);
                                }

                                groupMemberViewAdapter =
                                        new GroupMemberViewAdapter(GroupMembers.this,
                                                userProfileArrayList,
                                                isAdmin,
                                                new GroupMemberViewAdapter.GroupMemberInterface() {
                                                    @Override
                                                    public void overflowBTNClicked(int position, View view) {
                                                        checkAuthentication(position,view);
                                                    }
                                                });

                                if(progressBar.isShown())
                                {
                                    progressBar.setVisibility(View.GONE);
                                }

                                recyclerView.setAdapter(groupMemberViewAdapter);
                            }
                            else
                            {
                                if(dataSnapshot.exists())
                                {
                                    isAdmin.set(finalI,dataSnapshot.getValue(Boolean.class));
                                    groupMemberViewAdapter.notifyItemChanged(finalI);
                                }
                                else
                                {
                                    isAdmin.remove(finalI);
                                    userProfileArrayList.remove(finalI);
                                    groupMemberViewAdapter.notifyItemRemoved(finalI );
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////CHECK AUTHENTICATION :: WHEN CHANGE AUTHENTICATION CLICKED////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkAuthentication(final int position, final View view){

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class))
                        {
                            handleOverflowClicked(position,view);
                        }
                        else
                        {
                            Toast.makeText(GroupMembers.this, "You don't have permission to remove user or change authentication.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////WHEN OVERFLOW BUTTON CLICKED///////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleOverflowClicked(final int position, View view)
    {
        PopupMenu popupMenu = new PopupMenu(GroupMembers.this,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.group_members_popup_menu,popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.group_members_popup_chnge_auth :

                        FirebaseDatabase.getInstance().getReference().getRoot()
                                .child(GlobalNames.getUserGroupDetail())
                                .child(GlobalNames.getUserAuthentication())
                                .child(userProfileArrayList.get(position).getUser_id())
                                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                                .setValue(!isAdmin.get(position));

                        return true;

                    case R.id.group_members_popup_remove_user :

                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupMembers.this);
                        builder.setTitle("Remove User!");
                        builder.setMessage("Are you sure you want to remove the User from this group?");
                        builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getGroupDetail())
                                        .child(GlobalNames.getGroupMemberList())
                                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                                        .child(userProfileArrayList.get(position).getUser_id())
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getGroupDetail())
                                        .child(GlobalNames.getGroupMemberCount())
                                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                                        .child(userProfileArrayList.get(position).getUser_id())
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getUserGroupDetail())
                                        .child(GlobalNames.getUserAuthentication())
                                        .child(userProfileArrayList.get(position).getUser_id())
                                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getUserGroupDetail())
                                        .child(GlobalNames.getUserGroupList())
                                        .child(userProfileArrayList.get(position).getUser_id())
                                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                                        .removeValue();
                            }
                        });

                        builder.setNegativeButton("CANCEL",null);
                        builder.show();

                        return true;

                    default:
                        return false;
                }
            }
        });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON QUERY SUBMIT///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onQueryTextSubmit(String query) {

        if(progressBar.getVisibility() == View.VISIBLE)
        {
            Toast.makeText(GroupMembers.this, "Please wait...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(userProfileArrayList != null && userProfileArrayList.size() > 0)
        {
            userProfileArrayList.clear();
            isAdmin.clear();
            groupMemberViewAdapter.notifyDataSetChanged();
        }

        progressBar.setVisibility(View.VISIBLE);

        query = query.toLowerCase().replace(" ","");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(),0);
        findGroupMembers(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET MEMBER LIST FOR GIVEN QUERY////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void findGroupMembers(final String query) {

        refToMemberList = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getGroupMemberList())
                .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        refToMemberList.orderByChild(GlobalNames.getUserName_lower())
                .startAt(query).endAt(query + "\uf8ff")
                .limitToFirst(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        userProfileArrayList.clear();
                        isAdmin.clear();

                        if(groupMemberViewAdapter != null)
                        {
                            groupMemberViewAdapter.notifyDataSetChanged();
                            groupMemberViewAdapter = null;
                        }

                        if(!dataSnapshot.exists() && query != null && query.length() > 0)
                        {
                            if(query.length() > 1)
                            {
                                findGroupMembers(query.substring(0,query.length() - 1));
                            }
                            else
                            {
                                Toast.makeText(GroupMembers.this, "No Any Data found for selected Search...", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            return;
                        }

                        Iterator iterator = dataSnapshot.getChildren().iterator();

                        while (iterator.hasNext())
                        {
                            userProfileArrayList.add(((DataSnapshot)iterator.next()).getValue(UserProfile.class));
                        }

                        getUserAuthentication();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
