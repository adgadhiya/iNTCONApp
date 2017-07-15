package com.myapp.unknown.iNTCON.NavigationView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class SentRequest extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchGroupAdapter searchGroupAdapter;
    private TextView textView;

    private ArrayList<String> requestsKeyList;
    private ArrayList<GroupProfile> groupProfileArrayList;
    private ArrayList<String> createdOnList;


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

        setContentView(R.layout.activity_sent_request);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sent_request_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.sent_request_rv);
        textView = (TextView) findViewById(R.id.sent_request_tv);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getgroupRequestKeys();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON OPTION ITEM SELECTED//////////////////////////////////////////////////////
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
    ///////GET SENT REQUEST KEYS//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getgroupRequestKeys(){

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getMyGroupRequests())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        requestsKeyList = new ArrayList<>();

                        if(dataSnapshot.exists())
                        {

                            Iterator iterator = dataSnapshot.getChildren().iterator();

                            while(iterator.hasNext())
                            {
                                requestsKeyList.add(((DataSnapshot)iterator.next()).getValue(String.class));
                            }

                            setRecyclerView();
                            textView.setVisibility(View.GONE);

                        }
                        else
                        {
                           textView.setVisibility(View.VISIBLE);

                            if(recyclerView.getAdapter() != null)
                            {
                                createdOnList.clear();
                                groupProfileArrayList.clear();
                                searchGroupAdapter.notifyDataSetChanged();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////SET RECYCLER VIEW//////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setRecyclerView(){

        groupProfileArrayList = new ArrayList<>();
        createdOnList = new ArrayList<>();

        for(int i=0 ; i<requestsKeyList.size() ; i++)
        {
            final int finalI = i;
            FirebaseDatabase.getInstance().getReference().getRoot()
                    .child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getGroupProfile())
                    .child(requestsKeyList.get(i))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {
                                groupProfileArrayList.add(dataSnapshot.getValue(GroupProfile.class));

                                if(finalI == requestsKeyList.size() - 1)
                                {
                                    getGroupCreationDate();
                                }

                            }
                            else
                            {
                                groupProfileArrayList.add(new GroupProfile());
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET GROUP CREATION DATE//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getGroupCreationDate(){

        for(int i=0 ; i< groupProfileArrayList.size() ; i++)
        {

            final int finalI = i;
            FirebaseDatabase.getInstance().getReference()
                    .getRoot().child(GlobalNames.getGroupDetail())
                    .child(GlobalNames.getGroupList())
                    .child(groupProfileArrayList.get(i).getGroup_id())
                    .child(GlobalNames.getCreatedOn()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    createdOnList.add(dataSnapshot.getValue(String.class));

                    if(finalI == groupProfileArrayList.size() - 1)
                    {
                        searchGroupAdapter = new SearchGroupAdapter(SentRequest.this,
                                groupProfileArrayList, createdOnList, true, new SearchGroupAdapter.JoinGroupInterface() {
                            @Override
                            public void onItemClicked(int position) {
                                Intent intent = new Intent(SentRequest.this, GroupInfo.class);
                                intent.putExtra(getString(R.string.group_info_group_id),groupProfileArrayList.get(position).getGroup_id());
                                startActivity(intent);
                            }
                        });

                        recyclerView.setAdapter(searchGroupAdapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
