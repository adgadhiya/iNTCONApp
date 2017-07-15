package com.myapp.unknown.iNTCON.NavigationView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;
import com.myapp.unknown.iNTCON.ViewHolders.UserRequestViewHolder;

import java.util.HashMap;
import java.util.Map;

public class UserRequest extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textView;
    private ProgressBar progressBar;

    SharedPreferences sp;
    String groupId;

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

        setContentView(R.layout.activity_user_request);

        recyclerView = (RecyclerView) findViewById(R.id.user_request_rv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_request_toolbar);
        textView = (TextView) findViewById(R.id.user_request_message_tv);
        progressBar = (ProgressBar) findViewById(R.id.user_request_progressBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);
        groupId = sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE());

        checkAuthentication();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////ON OPTION ITEM SELECTED//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return  true;
        }

        return false;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////CHECK USER TYPE IS ADMIN OR USER////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkAuthentication(){

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserAuthentication())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            if(dataSnapshot.getValue(Boolean.class))
                            {
                                setRecyclerView();
                            }
                            else
                            {
                                textView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            textView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////GET ALL THE REQUESTED USER PROFILE////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setRecyclerView(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserRequests())
                .child(groupId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() == 0)
                {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(getString(R.string.no_any_request));
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    textView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FirebaseRecyclerAdapter<UserRequestDetailClass,UserRequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserRequestDetailClass, UserRequestViewHolder>
                        (
                            UserRequestDetailClass.class,
                            R.layout.user_request_layout,
                            UserRequestViewHolder.class,
                            reference
                        )
                {
                    @Override
                    protected void populateViewHolder(UserRequestViewHolder viewHolder, final UserRequestDetailClass model, int position) {

                        viewHolder.user_name.setText(model.getUserName());
                        viewHolder.requested_on.setText(model.getRequested_on());

                        Glide.with(getApplicationContext())
                                .load(model.getUser_profile())
                                .into(viewHolder.user_profile);

                        if(model.getEmail().equals(GlobalNames.getNONE()))
                        {
                            viewHolder.user_email.setVisibility(View.GONE);
                        }
                        else
                        {
                            viewHolder.user_email.setText(model.getEmail());
                        }

                        if(model.getField().equals(GlobalNames.getNONE()))
                        {
                            viewHolder.user_field.setVisibility(View.GONE);
                        }
                        else
                        {
                            viewHolder.user_field.setText(model.getField());
                        }

                        viewHolder.bindToUI(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                switch (view.getId())
                                {
                                    case R.id.user_request_confirm_admin :
                                        handleAdminSelected(model);
                                        break;

                                    case R.id.user_request_confirm_user :
                                        handleUserSelected(model);
                                        break;

                                    case  R.id.user_request_cancel_request :
                                        handleCancelSelected(model);
                                        break;
                                }
                            }
                        });

                    }
                };
        recyclerView.setAdapter(adapter);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////HANDLE WHEN ADMIN SELECTED//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleAdminSelected(UserRequestDetailClass modelAdmin){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot();

        Map<String,Object> map = new HashMap<>();

        UserProfile userProfile = new UserProfile();
        userProfile.setUser_id(modelAdmin.getUser_id());
        userProfile.setUserProfile(modelAdmin.getUser_profile());
        userProfile.setUserName_lower(modelAdmin.getUserName().toLowerCase().replace(" ",""));
        userProfile.setUserName(modelAdmin.getUserName());

        UserGroupListClass userGroupListClass = new UserGroupListClass();
        userGroupListClass.setGroup_id(groupId);
        userGroupListClass.setField_key(modelAdmin.getField_id());
        userGroupListClass.setYear(GlobalNames.getDummyNode());

        map.put(GlobalNames.getGroupDetail()
                + "/" + GlobalNames.getGroupMemberList()
                + "/" + groupId
                + "/" + modelAdmin.getUser_id(),userProfile);

        map.put(GlobalNames.getGroupDetail()
                + "/" + GlobalNames.getGroupMemberCount()
                + "/" + groupId
                + "/" + modelAdmin.getUser_id(), modelAdmin.getUser_id());

        map.put(GlobalNames.getUserGroupDetail()
                + "/" + GlobalNames.getUserGroupList()
                + "/" + modelAdmin.getUser_id()
                + "/" + groupId, userGroupListClass);


        map.put(GlobalNames.getUserGroupDetail()
                + "/" + GlobalNames.getUserAuthentication()
                + "/" + modelAdmin.getUser_id()
                + "/" + groupId, true);

        reference.updateChildren(map);

        reference.child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserRequests())
                .child(groupId).child(modelAdmin.getUser_id()).removeValue();

        reference.child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getMyGroupRequests())
                .child(modelAdmin.getUser_id()).child(groupId).removeValue();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////CALLED WHEN USER BUTTON SELECTED/////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleUserSelected(UserRequestDetailClass modelUser){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot();

        Map<String,Object> map = new HashMap<>();

        UserProfile userProfile = new UserProfile();
        userProfile.setUser_id(modelUser.getUser_id());
        userProfile.setUserProfile(modelUser.getUser_profile());
        userProfile.setUserName_lower(modelUser.getUserName().toLowerCase().replace(" ",""));
        userProfile.setUserName(modelUser.getUserName());

        map.put(GlobalNames.getGroupDetail()
                + "/" + GlobalNames.getGroupMemberList()
                + "/" + groupId
                + "/" + modelUser.getUser_id(),userProfile);

        map.put(GlobalNames.getGroupDetail()
                + "/" + GlobalNames.getGroupMemberCount()
                + "/" + groupId
                + "/" + modelUser.getUser_id(), modelUser.getUser_id());


        UserGroupListClass userGroupListClass = new UserGroupListClass();
        userGroupListClass.setGroup_id(groupId);
        userGroupListClass.setField_key(modelUser.getField_id());
        userGroupListClass.setYear(GlobalNames.getDummyNode());

        map.put(GlobalNames.getUserGroupDetail()
                + "/" + GlobalNames.getUserGroupList()
                + "/" + modelUser.getUser_id()
                + "/" + groupId, userGroupListClass);


        map.put(GlobalNames.getUserGroupDetail()
                + "/" + GlobalNames.getUserAuthentication()
                + "/" + modelUser.getUser_id()
                + "/" + groupId, false);


        reference.updateChildren(map);

        reference.child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserRequests())
                .child(groupId).child(modelUser.getUser_id()).removeValue();


        reference.child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getMyGroupRequests())
                .child(modelUser.getUser_id()).child(groupId).removeValue();

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////CALLED WHEN CANCEL BUTTON CLICKED//////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleCancelSelected(UserRequestDetailClass modelCancel){

        FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserRequests())
                .child(groupId).child(modelCancel.getUser_id()).removeValue();
    }
}

