package com.myapp.unknown.iNTCON.Campaign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.OtherClasses.Like_Interested_Going_Bool;
import com.myapp.unknown.iNTCON.OtherClasses.Like_Interested_Going_Cnt;
import com.myapp.unknown.iNTCON.R;

import java.util.ArrayList;

/**
 * Created by UNKNOWN on 9/23/2016.
 */
public class CampaignListAdapter extends RecyclerView.Adapter<CampaignListAdapter.CampaignHolder> {

    private final Context context;
    private final ArrayList<CampaignDetailProvider> list;
    private final ArrayList<GroupProfile> groupProfileArrayList;
    private final ArrayList<Like_Interested_Going_Cnt> like_interested_going_cnts;
    private final ArrayList<Like_Interested_Going_Bool> like_interested_going_bools;

    private final CampaignInterface campaignInterface;

    private final RequestManager requestManager;


    public CampaignListAdapter(Context context,
                               ArrayList<CampaignDetailProvider> list,
                               ArrayList<Like_Interested_Going_Bool> like_interested_going_bools,
                               ArrayList<Like_Interested_Going_Cnt> like_interested_going_cnts,
                               ArrayList<GroupProfile> groupProfileArrayList,
                               RequestManager requestManager,
                               CampaignInterface campaignInterface) {

        this.campaignInterface = campaignInterface;
        this.context = context;
        this.list = list;
        this.like_interested_going_bools = like_interested_going_bools;
        this.like_interested_going_cnts = like_interested_going_cnts;
        this.groupProfileArrayList = groupProfileArrayList;
        this.requestManager = requestManager;
    }

    @Override
    public CampaignListAdapter.CampaignHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.campaign_list,parent,false);
        return new CampaignHolder(view);
    }

    @Override
    public void onBindViewHolder(final CampaignListAdapter.CampaignHolder holder, int position) {

        holder.title.setText(list.get(position).getTitle());
        holder.interested_tv.setText(context.getString(R.string.interested,(float) like_interested_going_cnts.get(position).getInterested()));
        holder.likes_tv.setText(context.getString(R.string.likes,(float) like_interested_going_cnts.get(position).getLikes()));
        holder.going_tv.setText(context.getString(R.string.going, (float) like_interested_going_cnts.get(position).getGoing()));
        holder.date.setText(list.get(position).getDate());
        holder.likes_chk_box.setChecked(like_interested_going_bools.get(position).isLikes());
        holder.interested_chk_box.setChecked(like_interested_going_bools.get(position).isInterested());
        holder.going_chk_box.setChecked(like_interested_going_bools.get(position).isGoing());
        holder.group_name.setText(groupProfileArrayList.get(position).getGroup_name());

        requestManager
                .load(list.get(position).getImg_path())
                .centerCrop()
                .placeholder(R.mipmap.default_event_achievement_placeholder)
                .crossFade()
                .error(R.mipmap.default_event_achievement_error)
                .into(holder.imageView);

        requestManager
                .load(groupProfileArrayList.get(position).getGroup_profile_path())
                .placeholder(R.mipmap.create_group_profile)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.create_group_profile)
                .into(holder.profile);

        if(Campaign_Fragment.isLongPressed)
        {
            holder.delete_check_box.setVisibility(View.VISIBLE);

            if(Campaign_Fragment.isWishedList.get(position))
            {
                holder.delete_check_box.setChecked(true);
            }
            else
            {
                holder.delete_check_box.setChecked(false);
            }
        }
        else
        {
            holder.delete_check_box.setVisibility(View.GONE);
            holder.delete_check_box.setChecked(false);
        }

     }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CampaignHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        final TextView group_name,title,likes_tv,interested_tv,going_tv,date;
        final CheckBox likes_chk_box,interested_chk_box,going_chk_box,delete_check_box;
        final ImageView imageView,profile;
        final ImageButton facebook,gplus;

        public CampaignHolder(final View itemView) {
            super(itemView);

            group_name = (TextView) itemView.findViewById(R.id.tv_campaign_list_uni_name);
            title = (TextView) itemView.findViewById(R.id.tv_campaign_list_title);
            likes_tv = (TextView) itemView.findViewById(R.id.tv_campaign_list_likes);
            interested_tv = (TextView) itemView.findViewById(R.id.tv_campaign_list_interested);
            going_tv = (TextView) itemView.findViewById(R.id.tv_campaign_list_going);
            date = (TextView) itemView.findViewById(R.id.tv_campaign_list_date);

            delete_check_box = (CheckBox) itemView.findViewById(R.id.delete_camp);
            likes_chk_box = (CheckBox) itemView.findViewById(R.id.campaign_list_chk_box_like);
            interested_chk_box = (CheckBox) itemView.findViewById(R.id.campaign_list_chk_box_interested);
            going_chk_box = (CheckBox) itemView.findViewById(R.id.campaign_list_going_chk_box);

            imageView = (ImageView) itemView.findViewById(R.id.campaign_list_img);
            profile = (ImageView) itemView.findViewById(R.id.campaign_profile);

            gplus = (ImageButton) itemView.findViewById(R.id.share_gplus_campaign);
            facebook = (ImageButton)itemView.findViewById(R.id.share_facebook_campaign);

            going_chk_box.setOnCheckedChangeListener(this);
            interested_chk_box.setOnCheckedChangeListener(this);
            likes_chk_box.setOnCheckedChangeListener(this);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(),"AlteHaasGroteskBold.ttf");
            title.setTypeface(typeface);

            typeface = Typeface.createFromAsset(context.getAssets(), "AlteHaasGroteskRegular.ttf");

            group_name.setTypeface(typeface);
                       imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Campaign_Fragment.isLongPressed)
                    {
                        campaignInterface.deleteItem(getAdapterPosition(),!delete_check_box.isChecked());
                    }
                    else {
                        imageView.buildDrawingCache();
                        profile.buildDrawingCache();
                        campaignInterface.imgClicked(getAdapterPosition());
                        imageView.destroyDrawingCache();
                        profile.destroyDrawingCache();
                    }

                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    imageView.buildDrawingCache();
                    profile.buildDrawingCache();
                    campaignInterface.onLongClicked
                            (getAdapterPosition(),!delete_check_box.isChecked());
                    return true;
                }
            });

            delete_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(buttonView.isPressed()) {
                        campaignInterface.deleteItem(getAdapterPosition(),isChecked);
                    }

                }
            });

            gplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imageView.setDrawingCacheEnabled(true);
                    campaignInterface.gplusClicked(imageView.getDrawingCache(),getAdapterPosition());

                }
            });

            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    campaignInterface.fbClicked(getAdapterPosition());
                }
            });
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if(buttonView.isPressed())
            {
                campaignInterface.chkBoxClicked(buttonView,getAdapterPosition(),isChecked);
            }
        }
    }

    public interface CampaignInterface{
        void chkBoxClicked(View v,int position,boolean isChecked);
        void imgClicked(int position);
        void gplusClicked(Bitmap bitmap, int position);
        void fbClicked(int position);
        void onLongClicked(int position, boolean isChecked);
        void deleteItem(int position,boolean isChecked);
    }
}
