package com.myapp.unknown.iNTCON.NavigationView;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.R;

import java.util.ArrayList;

/**
 * Created by UNKNOWN on 1/27/2017.
 */
public class UserGroupListAdapter extends RecyclerView.Adapter<UserGroupListAdapter.UserGroupListHolder> {

    private final Context context;
    private final ArrayList<GroupProfile> userGroupList;
    private final UserGroupListInterface groupListInterface;
    private final ArrayList<Boolean> isNewNoticeMSG;

    public UserGroupListAdapter(Context context,
                                ArrayList<GroupProfile> userGroupList,
                                ArrayList<Boolean> isNewNoticeMSG,
                                UserGroupListInterface groupListInterface) {

        this.context = context;
        this.userGroupList = userGroupList;
        this.groupListInterface = groupListInterface;
        this.isNewNoticeMSG = isNewNoticeMSG;

    }

    @Override
    public UserGroupListAdapter.UserGroupListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_group_list, parent, false);
        return new UserGroupListHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserGroupListAdapter.UserGroupListHolder holder, int position) {

        holder.group_name.setText(userGroupList.get(position).getGroup_name());
        Glide.with(context).load(userGroupList.get(position).getGroup_profile_path()).into(holder.group_profile);

        if(isNewNoticeMSG.get(position) && holder.newMessage.getVisibility() == View.INVISIBLE)
        {
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.expand_animation);
            holder.newMessage.setAnimation(animation);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.newMessage.setVisibility(View.VISIBLE);
                }
            },300);

        }
        else if(!isNewNoticeMSG.get(position) && holder.newMessage.getVisibility() == View.VISIBLE)
        {
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.shrink_animation);
            holder.newMessage.setAnimation(animation);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.newMessage.setVisibility(View.INVISIBLE);
                }
            },300);
        }

    }

    @Override
    public int getItemCount() {
        return userGroupList.size();
    }

    public class UserGroupListHolder extends RecyclerView.ViewHolder {

        final ImageView group_profile;
        final TextView group_name,newMessage;
        final ImageButton overflow_btn;
        final LinearLayout user_group_list_layout;

        public UserGroupListHolder(View itemView) {
            super(itemView);

            group_name = (TextView) itemView.findViewById(R.id.user_group_list_group_name);
            group_profile = (ImageView) itemView.findViewById(R.id.user_group_list_group_profile);
            overflow_btn = (ImageButton) itemView.findViewById(R.id.user_group_list_overflow_btn);
            user_group_list_layout = (LinearLayout) itemView.findViewById(R.id.user_group_list_layout);
            newMessage = (TextView) itemView.findViewById(R.id.user_group_list_new_notice);

            if(overflow_btn != null)
            {
                overflow_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        groupListInterface.overflowBTNClicked(getAdapterPosition(),view);
                    }
                });
            }

            if(user_group_list_layout != null)
            {
                user_group_list_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        groupListInterface.onGroupItemClicked(getAdapterPosition());
                    }
                });
            }
        }
    }


    public interface UserGroupListInterface{
        void onGroupItemClicked(int position);
        void overflowBTNClicked(int position, View view);
    }


}