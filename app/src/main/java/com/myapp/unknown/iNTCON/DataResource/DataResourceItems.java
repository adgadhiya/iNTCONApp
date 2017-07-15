package com.myapp.unknown.iNTCON.DataResource;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.unknown.iNTCON.Campaign.Campaign_Chat;
import com.myapp.unknown.iNTCON.Downloads.DownloadService;
import com.myapp.unknown.iNTCON.OtherClasses.ImageSaver;
import com.myapp.unknown.iNTCON.OtherClasses.MyApplication;
import com.myapp.unknown.iNTCON.OtherClasses.DashBoard;
import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseDownloading;
import com.myapp.unknown.iNTCON.SQLiteDataBase.SqliteDataBaseShare;
import com.myapp.unknown.iNTCON.SignIn.MainActivity;
import com.myapp.unknown.iNTCON.ViewHolders.DataResourceItemHolder;

import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

public class DataResourceItems extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private FirebaseRecyclerAdapter<DataResourceItemProvider,DataResourceItemHolder> adapter;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private FloatingActionButton fab_done,fab_cancel;
    private FloatingActionMenu fab_main;
    private com.github.clans.fab.FloatingActionButton fab_add,fab_delete,fab_share,fab_edit;

    private LinearLayout mTitleContainer,linearLayout_toolbar;
    private TextView mTitle,created_on, messages_count, resource_count,titleContainer,createdOnContainer;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private ImageView user_profile_placeholder,user_profile;

    private RecyclerView recyclerView;

    private SqliteDataBaseDownloading sqliteDataBaseDownloading;

    private DataResourceProvider dataResourceProvider;

    private boolean isShareSelected;
    private boolean isDeleteSelected;
    private boolean isEditSelected;

    private String refPath;

    private SqliteDataBaseShare sqliteDataBaseShare;

    ArrayList<DataResourceItemProvider> selectedResourceItemList;
    ArrayList<Boolean> isSelected;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////ON CREATE///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.activity_data_resource_items);

        final UserProfile  userProfile = getIntent().getParcelableExtra(getString(R.string.user_profile_intent));
        dataResourceProvider = getIntent().getParcelableExtra(getString(R.string.data_resource_item_key_intent));
        refPath = getIntent().getStringExtra(getString(R.string.data_resource_path_intent));

        bindActivity();

        Glide.with(this).load(userProfile.getUserProfile()).into(user_profile);
        mTitle.setText(dataResourceProvider.getTitle());
        created_on.setText(dataResourceProvider.getCreated_on());
        titleContainer.setText(dataResourceProvider.getTitle());
        createdOnContainer.setText(dataResourceProvider.getCreated_on());

        fab_share.setOnClickListener(this);
        fab_delete.setOnClickListener(this);
        fab_edit.setOnClickListener(this);
        fab_add.setOnClickListener(this);
        fab_main.setOnClickListener(this);
        fab_done.setOnClickListener(this);
        fab_cancel.setOnClickListener(this);

        mAppBarLayout.addOnOffsetChangedListener(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        Glide.with(this).load(userProfile.getUserProfile()).asBitmap().into(new BitmapImageViewTarget(user_profile_placeholder) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);
                Blurry.with(DataResourceItems.this).from(bitmap).into(user_profile_placeholder);
            }
        });


        messages_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DataResourceItems.this, Campaign_Chat.class);
                intent.putExtra("KEY",dataResourceProvider.getKey());
                intent.putExtra("DATE",dataResourceProvider.getCreated_on());
                intent.putExtra("USER_PROFILE",userProfile);
                intent.putExtra("REF_PATH",refPath);
                intent.putExtra(getString(R.string.is_data_resource_chat_intent),true);
                startActivity(intent);

            }
        });

        selectedResourceItemList = new ArrayList<>();
        isSelected = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DataResourceItems.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sqliteDataBaseDownloading = new SqliteDataBaseDownloading(this);
        sqliteDataBaseShare = new SqliteDataBaseShare(this);

        SharedPreferences sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        if(userProfile.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                sp.getBoolean(AllPreferences.getIsAdmin(),false))
        {
            fab_main.setVisibility(View.VISIBLE);
        }

        setRecyclerView();
        getCommentCount();

    }


    ////////////////////////////////////////
    //////ON DESTROY////////////////////////
    ////////////////////////////////////////


    @Override
    protected void onDestroy() {
        sqliteDataBaseDownloading.close();
        sqliteDataBaseShare.close();
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////GET COMMENTS COUNT FOR THE RESOURCES////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getCommentCount() {

        FirebaseDatabase.getInstance().getReferenceFromUrl(refPath)
                .child(dataResourceProvider.getKey())
                .child("comment_cnt")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messages_count.setText(getString(R.string.comments, dataSnapshot.getValue(Integer.class)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////INITIALIZE ALL VIEW COMPONENTS///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void bindActivity() {
        mToolbar                     = (Toolbar) findViewById(R.id.data_center_item_toolbar);
        mTitle                       = (TextView) findViewById(R.id.data_center_item_title_toolbar);
        created_on                   = (TextView) findViewById(R.id.data_center_item_created_on_toolbar);
        mTitleContainer              = (LinearLayout) findViewById(R.id.data_center_container_layout);
        mAppBarLayout                = (AppBarLayout) findViewById(R.id.data_center_item_appbar);
        linearLayout_toolbar         = (LinearLayout) findViewById(R.id.data_center_item_layout_toolbar);
        user_profile_placeholder     = (ImageView) findViewById(R.id.data_center_item_profile_placeholder);
        recyclerView                 = (RecyclerView) findViewById(R.id.data_center_items_rv);
        messages_count               = (TextView) findViewById(R.id.data_center_item_message_cnt);
        resource_count               = (TextView) findViewById(R.id.data_center_item_resource_cnt);
        createdOnContainer           = (TextView) findViewById(R.id.data_center_item_created_on);
        titleContainer               = (TextView) findViewById(R.id.data_center_item_title);
        user_profile                 = (ImageView) findViewById(R.id.data_center_item_user_profile);
        fab_main                     = (FloatingActionMenu) findViewById(R.id.fab_main);
        fab_add                      = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_add_resource);
        fab_edit                     = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_edit_resource);
        fab_delete                   = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_delete_resource);
        fab_share                    = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_share_resource);
        fab_done                     = (FloatingActionButton) findViewById(R.id.share_resource_done_fab);
        fab_cancel                   = (FloatingActionButton) findViewById(R.id.share_resource_cancel_fab);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////SET RECYCLER VIEW///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setRecyclerView(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(GlobalNames.getDataResourceItems())
                .child(dataResourceProvider.getKey());

        adapter = new FirebaseRecyclerAdapter<DataResourceItemProvider, DataResourceItemHolder>
                (
                        DataResourceItemProvider.class,
                        R.layout.data_resource_item_layout,
                        DataResourceItemHolder.class,
                        reference
                )
        {
            @Override
            protected void populateViewHolder(final DataResourceItemHolder viewHolder,
                                              final DataResourceItemProvider model,
                                              final int position) {

                resource_count.setText(getString(R.string.resources,adapter.getItemCount()));

                viewHolder.title_tv.setText(model.getTitle());
                viewHolder.date.setText(model.getDate());
                viewHolder.time.setText(model.getTime());

                Float fileSize = (float) (model.getSize() / 1024);

                if(fileSize > 1024)
                {
                    fileSize = fileSize / 1024;
                    viewHolder.size.setText(getString(R.string.file_size_MB,fileSize));
                }
                else
                {
                    viewHolder.size.setText(getString(R.string.file_size_KB,fileSize));
                }


                ///////////////////////////////////////
                //IF SHARE BUTTON  SELECTED////////////
                ///////////////////////////////////////
                if(isShareSelected)
                {

                    int[] attrs = new int[]{R.drawable.share_resource_click};
                    TypedArray typedArray = obtainStyledAttributes(attrs);
                    int backgroundResource = typedArray.getResourceId(0, 0);
                    viewHolder.linearLayout.setBackgroundResource(backgroundResource);
                    typedArray.recycle();

                    if(isSelected.get(position))
                    {
                        viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.item_selected));
                    }
                    else
                    {
                        viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.item_not_selected));
                    }
                }
                ///////////////////////////////////////
                //EDIT BUTTON SELECTED////////////
                ///////////////////////////////////////
                else if(isEditSelected)
                {
                    viewHolder.title_et.setText(model.getTitle());
                    viewHolder.title_et.setVisibility(View.VISIBLE);
                    viewHolder.title_tv.setVisibility(View.GONE);
                }
                ///////////////////////////////////////
                //DELETE BUTTON  SELECTED////////////
                ///////////////////////////////////////
                else if(isDeleteSelected)
                {
                    int[] attrs = new int[]{R.drawable.share_resource_click};
                    TypedArray typedArray = obtainStyledAttributes(attrs);
                    int backgroundResource = typedArray.getResourceId(0, 0);
                    viewHolder.linearLayout.setBackgroundResource(backgroundResource);
                    typedArray.recycle();

                    if(isSelected.get(position))
                    {
                        viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.item_selected));
                    }
                    else
                    {
                        viewHolder.linearLayout.setBackgroundColor(getResources().getColor(R.color.item_not_selected));
                    }

                }
                ///////////////////////////////////////
                //SHOW LIST NORMALLY////////////
                ///////////////////////////////////////
                else
                {
                    int[] attrs = new int[]{R.attr.selectableItemBackground};
                    TypedArray typedArray = obtainStyledAttributes(attrs);
                    int backgroundResource = typedArray.getResourceId(0, 0);
                    viewHolder.linearLayout.setBackgroundResource(backgroundResource);
                    typedArray.recycle();

                    viewHolder.title_et.setVisibility(View.GONE);
                    viewHolder.title_tv.setVisibility(View.VISIBLE);
                }

                final int lastIndex = model.getTitle().lastIndexOf(".");

                setImage(model.getTitle().substring(lastIndex + 1),viewHolder);


                ///////////////////////////////////////////////////
                //HANDLE TITLE RENAME FOCUS CHANGE LISTENER////////////
                ///////////////////////////////////////////////////
                viewHolder.bindToUIFocusChanged(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {

                        if(!b && !viewHolder.title_et.getText().toString().isEmpty() &&
                                !viewHolder.title_et.getText().toString().equals(model.getTitle()))
                        {
                            FirebaseDatabase.getInstance().getReference().getRoot()
                                    .child(GlobalNames.getDataResourceItems())
                                    .child(dataResourceProvider.getKey())
                                    .child(model.getKey())
                                    .child(GlobalNames.getTITLE())
                                    .setValue(viewHolder.title_et.getText().toString() + model.title.substring(lastIndex));
                        }

                    }
                });

                ///////////////////////////////////////////////////
                //HANDLE WHEN ITEM CLICKED////////////
                ///////////////////////////////////////////////////
                viewHolder.bindToUIlayout(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ///////////////////////////////////////////////////////////////////
                        //IF SHARE SELECTED  ADD OR REMOVE RESOURCE TO BE SHARED////////////
                        ////////////////////////////////////////////////////////////////////
                        if(isShareSelected)
                        {
                            if(!isSelected.get(position))
                            {
                                sqliteDataBaseShare.insertData(model.getTitle(),model.getDownload_path(),model.getSize());
                                isSelected.set(position,true);
                            }
                            else
                            {
                                sqliteDataBaseShare.deleteData(model.getDownload_path());
                                isSelected.set(position,false);
                            }

                            adapter.notifyDataSetChanged();

                        }
                        ///////////////////////////////////////////////////
                        //IF EDIT IS SELECTED HANDLE IT////////
                        ///////////////////////////////////////////////////
                        else if(isEditSelected)
                        {
                            viewHolder.title_et.requestFocus();
                        }
                        ///////////////////////////////////////////////////////
                        //IF DELETE IS SELECTED HANDLE IT////////////
                        ///////////////////////////////////////////////////////
                        else if(isDeleteSelected)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DataResourceItems.this);
                            builder.setTitle("Confirm Delete?");
                            builder.setMessage("Are you sure you want to remove selected Resource?");
                            builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    SharedPreferences sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

                                    FirebaseDatabase.getInstance().getReference().getRoot()
                                            .child(GlobalNames.getDataResource())
                                            .child(sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()))
                                            .child(sp.getString(AllPreferences.getFieldKey(),GlobalNames.getDummyNode()))
                                            .child(sp.getString(AllPreferences.getYEAR(),GlobalNames.getDummyNode()).replace(" ",""))
                                            .child(dataResourceProvider.getKey())
                                            .child("item_cnt").setValue(adapter.getItemCount() - 1);

                                    FirebaseDatabase.getInstance().getReference().getRoot()
                                            .child(GlobalNames.getDataResourceItems())
                                            .child(dataResourceProvider.getKey())
                                            .child(model.getKey())
                                            .removeValue();

                                    adapter.notifyDataSetChanged();

                                }
                            }).setNegativeButton("NO",null);

                            builder.show();

                        }
                        ///////////////////////////////////////////////////
                        //SHOW DIALOG TO CONFIRM DOWNLOAD////////////
                        ///////////////////////////////////////////////////
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DataResourceItems.this);
                            builder.setMessage("Download Resource?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    downloadResource(model);

                                }
                            }).setNegativeButton("NO",null);

                            builder.show();
                        }
                    }

                });
            }
        };

        recyclerView.setAdapter(adapter);
    }


    ///////////////////////////////////////////////////
    ////WHEN DOWNLOAD CLICKED////////////
    ///////////////////////////////////////////////////
    private void downloadResource(final DataResourceItemProvider model) {

        if(!ImageSaver.isExternalStorageWritable())
        {
            Toast.makeText(DataResourceItems.this, "No Any Storage Found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(DataResourceItems.this, "Added to Download List.", Toast.LENGTH_SHORT).show();

        SharedPreferences sp = getSharedPreferences(AllPreferences.getPreferenceName(),MODE_PRIVATE);

        sqliteDataBaseDownloading.insertData
                      (model.getKey(),model.getTime(),
                      model.getDate(),model.getSize(),
                      model.getDownload_path(),
                      model.getTitle(),
                              sp.getString(AllPreferences.getGroupKey(),GlobalNames.getNONE()));

        if(!MyApplication.isDownloading())
      {
          startService(new Intent(DataResourceItems.this,DownloadService.class));
      }
    }

    ///////////////////////////////////////////////////
    //HANDLE OFFSET CHANGE LISTENER////////////
    ///////////////////////////////////////////////////
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }


    ///////////////////////////////////////////////////
    //SHOW OR HIDE TOOLBAR TITLE ON SCROLL////////////
    ///////////////////////////////////////////////////
    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                startAlphaAnimation(linearLayout_toolbar, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                startAlphaAnimation(linearLayout_toolbar, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    ///////////////////////////////////////////////////
    //CHANGE ALPHA VALUES FOR TITLE ON SCROLL////////////
    ///////////////////////////////////////////////////
    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    ///////////////////////////////////////////////////
    //START ALPHA ANIMATION ON SCROLL////////////
    ///////////////////////////////////////////////////
    public static void startAlphaAnimation(View v, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration((long) DataResourceItems.ALPHA_ANIMATIONS_DURATION);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    ///////////////////////////////////////////////////
    //HANDLE WHEN ANY BUTTON CLICKED////////////
    ///////////////////////////////////////////////////
    @Override
    public void onClick(View view) {

        fab_main.close(true);

        switch (view.getId())
        {

            case R.id.fab_share_resource :

                isShareSelected = true;
                selectedResourceItemList.clear();
                adapter.notifyDataSetChanged();
                fab_main.setVisibility(View.GONE);
                fab_done.setVisibility(View.VISIBLE);
                fab_cancel.setVisibility(View.VISIBLE);

                isSelected.clear();
                for(int i=0 ; i<recyclerView.getAdapter().getItemCount() ; i++)
                {
                    isSelected.add(false);
                }

                break;

            case R.id.fab_delete_resource :
                isDeleteSelected = true;
                selectedResourceItemList.clear();
                adapter.notifyDataSetChanged();
                fab_main.setVisibility(View.GONE);
                fab_done.setVisibility(View.VISIBLE);
                fab_cancel.setVisibility(View.VISIBLE);

                isSelected.clear();
                for(int i=0 ; i<recyclerView.getAdapter().getItemCount() ; i++)
                {
                    isSelected.add(false);
                }

                break;

            case R.id.fab_edit_resource :
                isEditSelected = true;
                selectedResourceItemList.clear();
                adapter.notifyDataSetChanged();
                fab_main.setVisibility(View.GONE);
                fab_done.setVisibility(View.VISIBLE);
                fab_cancel.setVisibility(View.VISIBLE);

                isSelected.clear();
                for(int i=0 ; i<recyclerView.getAdapter().getItemCount() ; i++)
                {
                    isSelected.add(false);
                }

                break;

            case R.id.fab_add_resource :
                Intent intent = new Intent(DataResourceItems.this,ChooseResource.class);
                intent.putExtra(getString(R.string.data_resource_item_key_intent),dataResourceProvider);
                intent.putExtra(getString(R.string.data_resource_path_intent),refPath);
                startActivity(intent);
                break;

            case R.id.share_resource_done_fab :

                if(isShareSelected)
                {
                    shareSelected();
                    isShareSelected = false;
                }
                else if(isEditSelected)
                {
                    isEditSelected = false;
                }
                else if(isDeleteSelected)
                {
                    isDeleteSelected = false;
                }

                fab_main.setVisibility(View.VISIBLE);
                fab_done.setVisibility(View.GONE);
                fab_cancel.setVisibility(View.GONE);

                isSelected.clear();
                adapter.notifyDataSetChanged();

                break;

            case R.id.share_resource_cancel_fab :

                isShareSelected = false;
                isEditSelected = false;
                isDeleteSelected = false;
                fab_main.setVisibility(View.VISIBLE);
                fab_done.setVisibility(View.GONE);
                fab_cancel.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

                break;

        }
    }


    ///////////////////////////////////////////////////
    ////HANDLE WHEN SHARE SELECTED///////////
    ///////////////////////////////////////////////////
    private void shareSelected() {
        MyApplication.setIsSharingOn(true);
        Intent RTReturn = new Intent(DashBoard.SHARE_ITEMS);
        LocalBroadcastManager.getInstance(DataResourceItems.this).sendBroadcast(RTReturn);
    }


    ///////////////////////////////////////////////////
    ///SET APPROPRIATE IMAGE FOR IMAGE VIEW ////////////
    ///////////////////////////////////////////////////
    private void setImage(String fileExtension,DataResourceItemHolder holder) {

        switch (fileExtension)
        {

            case "pdf" :
                holder.resource_type.setImageResource(R.mipmap.pdf_one);
                break;

            case "ppt" :
                holder.resource_type.setImageResource(R.mipmap.ppt);
                break;

            case "doc" :
                holder.resource_type.setImageResource(R.mipmap.doc);
                break;

            case "xls" :
                holder.resource_type.setImageResource(R.mipmap.xls);
                break;

            case "jpg" :
                holder.resource_type.setImageResource(R.mipmap.jpg);
                break;

            case "jpeg" :
                holder.resource_type.setImageResource(R.mipmap.jpg);
                break;

            case "png" :
                holder.resource_type.setImageResource(R.mipmap.jpg);
                break;

            case "rar" :
                holder.resource_type.setImageResource(R.mipmap.rar);
                break;

            case "zip" :
                holder.resource_type.setImageResource(R.mipmap.zip);
                break;

            case "txt" :
                holder.resource_type.setImageResource(R.mipmap.txt);
                break;

            default:
                holder.resource_type.setImageResource(R.mipmap.docs);
                break;
        }
    }
}
