package com.myapp.unknown.iNTCON.NavigationView;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class JoinGroup extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView joinGroupiNTCONGroupsRV;
    private RecyclerView joinGroupSearchGroupRV;
    private TextView textView,joinGroupMessageTV;
    private SearchView searchView;
    private ProgressBar searchGroupProgressbar,intconGroupProgressbar;

    private String querySTR;

    private ArrayList<GroupProfile> searchGroupProfileArrayList,intconGroupProfileArrayList;
    private ArrayList<String> createdOnList;

    private SearchGroupAdapter groupSearchGroupAdapter;
    private IntconGroupAdapter intconGroupAdapter;


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

        setContentView(R.layout.activity_join_group);

        joinGroupiNTCONGroupsRV = (RecyclerView) findViewById(R.id.join_group_intcon_special);
        joinGroupSearchGroupRV = (RecyclerView) findViewById(R.id.join_group_search_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.join_group_toolbar);
        textView = (TextView) findViewById(R.id.join_group_tv);
        searchGroupProgressbar = (ProgressBar) findViewById(R.id.join_group_search_progressBar);
        intconGroupProgressbar = (ProgressBar) findViewById(R.id.join_group_intcon_progressBar);
        joinGroupMessageTV = (TextView) findViewById(R.id.join_group_message_tv);

        joinGroupSearchGroupRV.setHasFixedSize(true);
        joinGroupiNTCONGroupsRV.setHasFixedSize(true);

        joinGroupiNTCONGroupsRV.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        joinGroupSearchGroupRV.setLayoutManager(new LinearLayoutManager(this));
        joinGroupiNTCONGroupsRV.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getIntconGroups();

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
    ///////ON OPTION ITEM SELECTED/////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET ALL INTCON CREATED GROUPS//////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getIntconGroups(){

        intconGroupProgressbar.setVisibility(View.VISIBLE);

        intconGroupProfileArrayList = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference().getRoot()
                .child(GlobalNames.getGroupDetail())
                .child(GlobalNames.getIntconGroupProfile())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterator iterator = dataSnapshot.getChildren().iterator();

                        while(iterator.hasNext())
                        {
                            intconGroupProfileArrayList.add(((DataSnapshot)iterator.next()).getValue(GroupProfile.class));
                        }

                        intconGroupAdapter = new IntconGroupAdapter(JoinGroup.this,
                                intconGroupProfileArrayList,
                                new IntconGroupAdapter.IntconGroupInterface() {
                            @Override
                            public void handleItemClicked(int position) {
                                Intent intent = new Intent(JoinGroup.this, GroupInfo.class);
                                intent.putExtra(getString(R.string.group_info_group_id), intconGroupProfileArrayList.get(position).getGroup_id());
                                intent.putExtra(getString(R.string.is_intcon_group), true);
                                startActivity(intent);
                            }
                        });

                        joinGroupiNTCONGroupsRV.setAdapter(intconGroupAdapter);
                        intconGroupProgressbar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON QUERY TEXT SUBMIT///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onQueryTextSubmit(String query) {

        if(searchGroupProgressbar.getVisibility() == View.VISIBLE)
        {
            Toast.makeText(JoinGroup.this, "Please wait for a moment", Toast.LENGTH_SHORT).show();
            return true;
        }

        joinGroupMessageTV.setVisibility(View.GONE);
        searchGroupProgressbar.setVisibility(View.VISIBLE);

        if(groupSearchGroupAdapter != null ||
                searchGroupProfileArrayList != null || createdOnList != null)
        {
            searchGroupProfileArrayList.clear();
            createdOnList.clear();
            groupSearchGroupAdapter.notifyDataSetChanged();
        }

        textView.setText(getString(R.string.search_result,query));

        querySTR = query;
        query = query.toLowerCase().replace(" ","");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(),0);
        findGroup(query);
        return true;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON QUERY TEXT CHANGED////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////WHEN SEARCH CLICKED GET GROUP LIST CONTAINS GIVEN STRING////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void findGroup(final String query) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .getRoot().child(GlobalNames.getGroupDetail()).child(GlobalNames.getGroupProfile());

        ref.orderByChild(GlobalNames.getGroupNameLower()).startAt(query).endAt(query + "\uf8ff").limitToFirst(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.exists() && query != null && query.length() > 0)
                        {
                            if(query.length() > 1)
                            {
                                findGroup(query.substring(0,query.length() - 1));
                            }
                            else
                            {
                                joinGroupMessageTV.setText(getString(R.string.no_result_found,querySTR));
                                joinGroupMessageTV.setVisibility(View.VISIBLE);
                                searchGroupProgressbar.setVisibility(View.GONE);
                            }
                            return;
                        }

                        searchGroupProfileArrayList = new ArrayList<>();
                        Iterator iterator = dataSnapshot.getChildren().iterator();

                        while (iterator.hasNext())
                        {
                            searchGroupProfileArrayList.add(((DataSnapshot)iterator.next()).getValue(GroupProfile.class));
                        }

                        getGroupCreationDate();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET GROUP GROUP CREATION LIST////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getGroupCreationDate(){

        createdOnList = new ArrayList<>();

        for(int i = 0; i< searchGroupProfileArrayList.size() ; i++)
        {

            final int finalI = i;
            FirebaseDatabase.getInstance().getReference()
                    .getRoot().child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getGroupList())
                    .child(searchGroupProfileArrayList.get(i).getGroup_id())
                    .child(GlobalNames.getCreatedOn()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    createdOnList.add(dataSnapshot.getValue(String.class));

                    if(finalI == searchGroupProfileArrayList.size() - 1)
                    {
                        groupSearchGroupAdapter = new SearchGroupAdapter(JoinGroup.this,
                                searchGroupProfileArrayList, createdOnList, true,  new SearchGroupAdapter.JoinGroupInterface() {
                            @Override
                            public void onItemClicked(int position) {
                                Intent intent = new Intent(JoinGroup.this, GroupInfo.class);
                                intent.putExtra(getString(R.string.group_info_group_id), searchGroupProfileArrayList.get(position).getGroup_id());
                                intent.putExtra(getString(R.string.is_intcon_group), false);
                                startActivity(intent);
                                searchView.clearFocus();
                            }
                        });

                        searchGroupProgressbar.setVisibility(View.GONE);
                        joinGroupMessageTV.setVisibility(View.GONE);
                        joinGroupSearchGroupRV.setAdapter(groupSearchGroupAdapter);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
