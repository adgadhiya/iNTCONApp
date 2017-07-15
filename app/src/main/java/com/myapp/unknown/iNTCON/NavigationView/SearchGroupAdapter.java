package com.myapp.unknown.iNTCON.NavigationView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.R;

import java.util.ArrayList;

/**
 * Created by UNKNOWN on 1/31/2017.
 */
public class SearchGroupAdapter extends RecyclerView.Adapter<SearchGroupAdapter.JoinGroupHolder> {

    private final Context context;
    private final ArrayList<GroupProfile> groupProfileArrayList;
    private final ArrayList<String> groupCreatedOn;
    private final JoinGroupInterface joinGroupInterface;
    private final boolean isJoinGroup;

    public SearchGroupAdapter(Context context,
                              ArrayList<GroupProfile> groupProfileArrayList,
                              ArrayList<String> groupCreatedOn,
                              boolean isJoinGroup,
                              JoinGroupInterface joinGroupInterface) {
        this.context = context;
        this.groupCreatedOn = groupCreatedOn;
        this.groupProfileArrayList = groupProfileArrayList;
        this.joinGroupInterface = joinGroupInterface;
        this.isJoinGroup = isJoinGroup;
    }

    @Override
    public JoinGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.join_group_search_groups_layout,parent,false);
        return new JoinGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(JoinGroupHolder holder, int position) {

        holder.groupName.setText(groupProfileArrayList.get(position).getGroup_name());
        Glide.with(context.getApplicationContext())
                .load(groupProfileArrayList.get(position).getGroup_profile_path())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.groupProfile);

        if(isJoinGroup)
        {
            holder.createdOn.setText(groupCreatedOn.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return groupProfileArrayList.size();
    }


    public class JoinGroupHolder extends RecyclerView.ViewHolder {

        final TextView groupName;
        final TextView createdOn;
        final LinearLayout linearLayout;
        final ImageView groupProfile;

        public JoinGroupHolder(View itemView) {
            super(itemView);

            groupName = (TextView) itemView.findViewById(R.id.search_group_group_name);
            createdOn = (TextView) itemView.findViewById(R.id.search_date_created_on);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.search_group_layout);
            groupProfile = (ImageView) itemView.findViewById(R.id.search_group_iv);

            if(isJoinGroup)
            {
                createdOn.setVisibility(View.VISIBLE);
            }

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinGroupInterface.onItemClicked(getAdapterPosition());
                }
            });

        }
    }

    public interface JoinGroupInterface{
        void onItemClicked(int position);
    }

}
