package com.myapp.unknown.iNTCON.DataResource;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;

import java.util.ArrayList;

/**
 * Created by UNKNOWN on 2/4/2017.
 */
public class DataResourceAdapter extends RecyclerView.Adapter<DataResourceAdapter.DataCenterViewHolder> {

    final Context context;
    final ArrayList<DataResourceProvider> dataResourceProviders;
    final ArrayList<UserProfile> userProfileArrayList;
    final DataCenterInterface dataCenterInterface;

    public DataResourceAdapter(Context context,
                               ArrayList<DataResourceProvider> dataResourceProviders,
                               ArrayList<UserProfile> userProfileArrayList,
                               DataCenterInterface dataCenterInterface) {

        this.dataCenterInterface = dataCenterInterface;
        this.context = context;
        this.dataResourceProviders = dataResourceProviders;
        this.userProfileArrayList = userProfileArrayList;
    }

    @Override
    public DataResourceAdapter.DataCenterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_resource_layout,parent,false);
        return new DataCenterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataResourceAdapter.DataCenterViewHolder holder, int position) {

        holder.user_name.setText(userProfileArrayList.get(position).getUserName());
        holder.title.setText(dataResourceProviders.get(position).getTitle());
        holder.created_on.setText(dataResourceProviders.get(position).getCreated_on());
        holder.comment_cnt.setText(context.getString(R.string.comments, dataResourceProviders.get(position).getComment_cnt()));
        holder.item_cnt.setText(String.valueOf(dataResourceProviders.get(position).getItem_cnt()));
        holder.item_cnt.setBackgroundResource(R.drawable.round);

        Glide.with(context.getApplicationContext())
                .load(userProfileArrayList.get(position).getUserProfile())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()
                .into(holder.user_profile);
    }

    @Override
    public int getItemCount() {
        return dataResourceProviders.size();
    }


    public class DataCenterViewHolder extends RecyclerView.ViewHolder {

        final TextView user_name;
        final TextView created_on;
        final TextView comment_cnt;
        final TextView item_cnt;
        final TextView title;
        final RelativeLayout relativeLayout;
        final ImageView user_profile;

        public DataCenterViewHolder(View itemView) {
            super(itemView);

            user_name = (TextView) itemView.findViewById(R.id.data_center_user_name);
            created_on = (TextView) itemView.findViewById(R.id.data_center_created_on);
            comment_cnt = (TextView) itemView.findViewById(R.id.data_center_comments);
            item_cnt = (TextView) itemView.findViewById(R.id.data_center_item_cnt);
            title = (TextView) itemView.findViewById(R.id.data_center_title);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.data_center_relative_layout);
            user_profile = (ImageView) itemView.findViewById(R.id.data_center_user_profile);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(),"Roboto-Regular.ttf");
            title.setTypeface(typeface);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataCenterInterface.onItemClickListener(getAdapterPosition());
                }
            });

            relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(userProfileArrayList.get(getAdapterPosition()).getUser_id()
                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                       dataCenterInterface.onItemLongClickListener(getAdapterPosition());
                        return true;
                    }

                    return false;
                }
            });

        }
    }


    public interface DataCenterInterface{

        void onItemClickListener(int position);
        void onItemLongClickListener(int position);
    }
}
