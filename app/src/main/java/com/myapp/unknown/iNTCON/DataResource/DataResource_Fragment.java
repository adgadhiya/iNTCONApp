package com.myapp.unknown.iNTCON.DataResource;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.DashBoard;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.NetworkConnectionCheck;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseShare;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataResource_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private DataResourceAdapter adapter;
    private ArrayList<DataResourceProvider> dataResourceProviders;
    private ArrayList<UserProfile> userProfileArrayList;
    private ProgressBar progressBar;

    private ImageView dataResourceMessageIV;
    private TextView dataResourceMessageTV;
    private LinearLayout dataResourceMessageLayout;

    private ChildEventListener childEventListener;

    private DatabaseReference root,reference;
    private SharedPreferences sp;

    private boolean isDataLoading;
    private boolean isClearedList;

    public DataResource_Fragment() {
        // Required empty public constructor
    }

    ///////////////////////////////////////////////////
    ////ON CREATE VIEW///////////
    ///////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_data_center, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.data_center_rv);
        progressBar = (ProgressBar) view.findViewById(R.id.data_center_fragment_progressBar);
        recyclerView.setHasFixedSize(true);

        dataResourceMessageIV = (ImageView)view.findViewById(R.id.data_resource_message_iv);
        dataResourceMessageTV = (TextView)view.findViewById(R.id.data_resource_message_tv);
        dataResourceMessageLayout = (LinearLayout) view.findViewById(R.id.data_resource_message_layout);
        Button dataResourceMessageRefreshBTN = (Button) view.findViewById(R.id.data_resource_message_refresh);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sp = getActivity().getSharedPreferences(AllPreferences.getPreferenceName(), Context.MODE_PRIVATE);

        isDataLoading = false;
        isClearedList = true;

        root = FirebaseDatabase.getInstance().getReference().getRoot();

        userProfileArrayList = new ArrayList<>();
        dataResourceProviders = new ArrayList<>();

            dataResourceMessageRefreshBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataResourceSelected();

            }
        });

        return view;
    }


    ///////////////////////////////////////////////////
    //GET RESOURCE LIST FROM DATABASE////////////
    ///////////////////////////////////////////////////
    private void getDataResourceList() {

        isDataLoading = true;

        final String refString = reference.toString();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                isDataLoading = false;

                if (!dataSnapshot.exists())
                {
                    dataResourceMessageLayout.setVisibility(View.VISIBLE);
                    dataResourceMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                    dataResourceMessageTV.setText(getString(R.string.no_data));
                    progressBar.setVisibility(View.GONE);
                    reference.removeEventListener(childEventListener);
                }
                else
                {
                    dataResourceMessageLayout.setVisibility(View.GONE);
                    dataResourceMessageLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                if(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()).equals(GlobalNames.getNONE()))
                {
                    dataResourceMessageTV.setText(getString(R.string.no_group_selected));
                }
                else
                {
                    dataResourceMessageTV.setText(databaseError.getMessage());
                }

                dataResourceMessageLayout.setVisibility(View.VISIBLE);
                dataResourceMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                progressBar.setVisibility(View.GONE);            }
        });


        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataResourceMessageLayout.getVisibility() == View.VISIBLE) {
                    dataResourceMessageLayout.setVisibility(View.GONE);
                    dataResourceMessageLayout.setVisibility(View.GONE);
                }

                if (refString.equals(dataSnapshot.getRef().getParent().toString())) {
                    dataResourceProviders.add(dataSnapshot.getValue(DataResourceProvider.class));
                    userProfileArrayList.add(new UserProfile());
                    getUserProfile(dataResourceProviders.size() - 1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (refString.equals(dataSnapshot.getRef().getParent().toString())) {
                    for (int i = 0; i < dataResourceProviders.size(); i++) {
                        if (dataResourceProviders.get(i).getKey().equals(dataSnapshot.getKey())) {
                            dataResourceProviders.set(i, dataSnapshot.getValue(DataResourceProvider.class));
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                if (refString.equals(dataSnapshot.getRef().getParent().toString())) {
                    for (int i = 0; i < dataResourceProviders.size(); i++) {
                        if (dataResourceProviders.get(i).getKey().equals(dataSnapshot.getKey())) {
                            dataResourceProviders.remove(i);
                            userProfileArrayList.remove(i);
                            adapter.notifyItemRemoved(i);
                            break;
                        }
                    }

                    if(dataResourceProviders.isEmpty())
                    {
                        reference.removeEventListener(childEventListener);
                        dataResourceMessageLayout.setVisibility(View.VISIBLE);
                        dataResourceMessageIV.setImageResource(R.drawable.ic_cloud_off_black_48dp);
                        dataResourceMessageTV.setText(getString(R.string.no_data));
                        progressBar.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        reference.addChildEventListener(childEventListener);

    }


    ///////////////////////////////////////////////////
    ////GET USERS PROFILE////////////
    ///////////////////////////////////////////////////
    private void getUserProfile( final int position) {

        root.child(GlobalNames.getUserGroupDetail())
                .child(GlobalNames.getUserProfile())
                .child(dataResourceProviders.get(position).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if( dataSnapshot.exists() &&
                                isClearedList &&
                                userProfileArrayList.size() - 1 >= position)
                        {

                            userProfileArrayList.set(position,dataSnapshot.getValue(UserProfile.class));
                            adapter = new DataResourceAdapter(getActivity(),
                                    dataResourceProviders,
                                    userProfileArrayList, new DataResourceAdapter.DataCenterInterface() {
                                @Override
                                public void onItemClickListener(int position) {

                                    if(sp.getBoolean(AllPreferences.getIsAdmin(),false) && MyApplication.isSharingOn())
                                    {
                                        showDialog(position);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(getActivity(),DataResourceItems.class);
                                        intent.putExtra(getString(R.string.data_resource_item_key_intent),dataResourceProviders.get(position));
                                        intent.putExtra(getString(R.string.user_profile_intent),userProfileArrayList.get(position));
                                        intent.putExtra(getString(R.string.data_resource_path_intent),reference.toString());
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onItemLongClickListener(final int position) {

                                    if(sp.getBoolean(AllPreferences.getIsAdmin(),false))
                                    {

                                        final CharSequence[] chooseFrom = {"Rename","Delete"};

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                        builder.setItems(chooseFrom, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (chooseFrom[which].equals("Rename"))
                                                {
                                                    showRenameDialog(position);
                                                }
                                                else
                                                {
                                                    showDeleteDialog(position);
                                                }
                                            }
                                        });
                                        builder.show();
                                    }

                                }
                            });

                            recyclerView.setAdapter(adapter);
                            isDataLoading = false;
                            isClearedList = false;
                        }
                        else
                        {
                            if(dataSnapshot.exists())
                            {
                                if(userProfileArrayList.size() - 1 >= position &&
                                        dataResourceProviders.get(position).getUid()
                                        .equals(dataSnapshot.getValue(UserProfile.class).getUser_id()))
                                {
                                    userProfileArrayList.set(position,dataSnapshot.getValue(UserProfile.class));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    ///////////////////////////////////////////////////
    //WHEN DELETE ITEM SELECTED SHOW DIALOG////////////
    ///////////////////////////////////////////////////
    private void showDeleteDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Delete?");
        builder.setMessage("Are you sure you want to remove this item from the list?");

        builder.setPositiveButton("remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                root.child(GlobalNames.getDataResource())
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()))
                        .child(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ",""))
                        .child(dataResourceProviders.get(position).getKey())
                        .removeValue();

            }
        }).setNegativeButton("NO",null);

        builder.show();
    }


    ///////////////////////////////////////////////////
    //WHEN RENAME SELECTED////////////
    ///////////////////////////////////////////////////
    private void showRenameDialog(final int position) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.edit_text_layout,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Resource Title");
        builder.setView(view,25,25,25,25);

        final EditText editText = (EditText) view.findViewById(R.id.et);
        editText.setText(dataResourceProviders.get(position).getTitle());
        editText.setSelection(editText.getText().length());

        builder.setPositiveButton("RENAME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(editText.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity(), "Empty name!", Toast.LENGTH_SHORT).show();
                }

                FirebaseDatabase.getInstance().getReference().getRoot()
                        .child(GlobalNames.getDataResource())
                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                        .child(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()))
                        .child(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ",""))
                        .child(dataResourceProviders.get(position).getKey())
                        .child(GlobalNames.getTITLE()).setValue(editText.getText().toString());

            }
        }).setNegativeButton("cancel",null);

        builder.show();
    }


    ///////////////////////////////////////////////////
    //SHOW DIALOG FOR COFIRM SHARING////////////
    ///////////////////////////////////////////////////
    private void showDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Sharing");
        builder.setMessage("Share Resources with " + dataResourceProviders.get(position).getTitle() + "?");

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        Cursor cursor = new SqliteDataBaseShare(getActivity()).getAllData();

                        if(cursor.moveToFirst())
                        {

                            SimpleDateFormat dateF = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                            SimpleDateFormat timeF = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getDataResourceItems())
                                    .child(dataResourceProviders.get(position).getKey());

                            do
                            {
                                String push_key = reference.push().getKey();

                                DataResourceItemProvider itemProvider = new DataResourceItemProvider();

                                itemProvider.setTitle(cursor.getString(1));
                                itemProvider.setDownload_path(cursor.getString(2));
                                itemProvider.setSize(cursor.getLong(3));
                                itemProvider.setTime(timeF.format(new Date()));
                                itemProvider.setDate(dateF.format(new Date()));
                                itemProvider.setUid(uid);
                                itemProvider.setKey(push_key);

                                reference.child(push_key).setValue(itemProvider);

                                FirebaseDatabase.getInstance().getReference().getRoot()
                                        .child(GlobalNames.getDataResource())
                                        .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                                        .child(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()))
                                        .child(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ",""))
                                        .child(dataResourceProviders.get(position).getKey())
                                        .child("item_cnt").setValue(dataResourceProviders.get(position).getItem_cnt() + 1);

                            }while(cursor.moveToNext());

                            MyApplication.setIsSharingOn(false);
                            ((DashBoard)getActivity()).hideCancelShareBTN();
                            new SqliteDataBaseShare(getActivity()).deleteAll();

                        }
                        else
                        {
                            MyApplication.setIsSharingOn(false);
                            ((DashBoard)getActivity()).hideCancelShareBTN();
                        }
                    }
                });
            }
        }).setNegativeButton("CANCEL",null);

        builder.show();
    }


    ///////////////////////////////////////////////////
    //METHOD CALLED WHEN ANY ITEM SELECTED////////////
    ///////////////////////////////////////////////////
    public void dataResourceSelected()
    {

        NetworkConnectionCheck connectionCheck = new NetworkConnectionCheck(getActivity());

        if(connectionCheck.checkingConnextion())
        {
            dataResourceMessageLayout.setVisibility(View.VISIBLE);
            dataResourceMessageIV.setImageResource(R.mipmap.no_connection);
            dataResourceMessageTV.setText(getString(R.string.no_network));
            progressBar.setVisibility(View.GONE);
            return;
        }

        if( (dataResourceProviders == null || dataResourceProviders.size() == 0 ||
                userProfileArrayList == null || userProfileArrayList.size() == 0) && !isDataLoading )
        {
            reference = root.child(GlobalNames.getDataResource())
                    .child(sp.getString(AllPreferences.getGroupKey(), GlobalNames.getNONE()))
                    .child(sp.getString(AllPreferences.getFieldKey(), GlobalNames.getDummyNode()))
                    .child(sp.getString(AllPreferences.getYEAR(), GlobalNames.getDummyNode()).replace(" ", ""));

            getDataResourceList();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    ///////////////////////////////////////////////////
    ////CLEAR LIST ON PARAMETER CHANGED(GROUP,FIELD,YEAR ETC.)////////////
    ///////////////////////////////////////////////////
    public void clearList()
    {
        dataResourceProviders.clear();
        userProfileArrayList.clear();
        isDataLoading= false;
        isClearedList = true;

        if(adapter != null)
        {
            reference.removeEventListener(childEventListener);
            adapter.notifyDataSetChanged();
        }

    }
}
