package com.myapp.unknown.iNTCON.NavigationView;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.Preferences.AllPreferences;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;

import java.util.ArrayList;

/**
 * Created by UNKNOWN on 1/28/2017.
 */
public class GroupMemberViewAdapter extends RecyclerView.Adapter<GroupMemberViewAdapter.GroupMemberViewHolder>{


    private final ArrayList<UserProfile> userProfileArrayList;
    private final ArrayList<Boolean> isAdmin;
    private final Context context;
    private final GroupMemberInterface groupMemberInterface;
    private final SharedPreferences sp;

    public GroupMemberViewAdapter(Context context,
                                  ArrayList<UserProfile> userProfileArrayList,
                                  ArrayList<Boolean> isAdmin,
                                  GroupMemberInterface groupMemberInterface){

        this.userProfileArrayList = userProfileArrayList;
        this.context = context;
        this.isAdmin = isAdmin;
        this.groupMemberInterface = groupMemberInterface;
        sp = context.getSharedPreferences(AllPreferences.getPreferenceName(),Context.MODE_PRIVATE);
    }

    @Override
    public GroupMemberViewAdapter.GroupMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.group_member_layout, parent, false);
        return new GroupMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupMemberViewAdapter.GroupMemberViewHolder holder, int position) {

        holder.user_name.setText(userProfileArrayList.get(position).getUserName());

        if(isAdmin.get(position))
        {
            holder.user_authentication.setText(context.getString(R.string.admin));
        }
        else
        {
            holder.user_authentication.setText(context.getString(R.string.user));
        }

        if(userProfileArrayList.get(position).getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ||
                !sp.getBoolean(AllPreferences.getIsAdmin(),false))
        {
            holder.overflow_menu.setVisibility(View.GONE);
        }
        else
        {
            holder.overflow_menu.setVisibility(View.VISIBLE);
        }

        Glide.with(context).load(userProfileArrayList.get(position).getUserProfile())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.user_profile);

    }

    @Override
    public int getItemCount() {
        return userProfileArrayList.size();
    }



    public class GroupMemberViewHolder extends RecyclerView.ViewHolder{

        final ImageView user_profile;
        final TextView user_name;
        final TextView user_authentication;
        final ImageButton overflow_menu;

        public GroupMemberViewHolder(View itemView) {
            super(itemView);

            user_profile = (ImageView) itemView.findViewById(R.id.group_member_user_profile);
            user_name = (TextView) itemView.findViewById(R.id.group_member_user_name);
            user_authentication = (TextView) itemView.findViewById(R.id.group_member_user_authentication);
            overflow_menu = (ImageButton) itemView.findViewById(R.id.group_members_popup_menu);

            overflow_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupMemberInterface.overflowBTNClicked(getAdapterPosition(), view);
                }
            });

        }
    }


    public interface GroupMemberInterface{

        void overflowBTNClicked(int position, View view);

    }
}
