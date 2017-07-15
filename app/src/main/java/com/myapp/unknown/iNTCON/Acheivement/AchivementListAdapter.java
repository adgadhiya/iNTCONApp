package com.myapp.unknown.iNTCON.Acheivement;

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
import com.myapp.unknown.iNTCON.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by UNKNOWN on 9/8/2016.
 */

public class AchivementListAdapter extends RecyclerView.Adapter<AchivementListAdapter.AchievementListHolder> {

    private final ArrayList<AchievementListProvider> listProvider;
    private final List<Boolean> booleanArrayList;
    private final ArrayList<GroupProfile> groupProfileArrayList;

    private final Context context;

    private final RequestManager requestManager;

    private final AchievementInterface achievementInterface;

    public AchivementListAdapter(Context context,
                                 ArrayList<AchievementListProvider> listProvider,
                                 List<Boolean> booleanArrayList,
                                 ArrayList<GroupProfile> groupProfileArrayList,
                                 RequestManager requestManager,
                                 AchievementInterface achievementInterface) {
        this.listProvider = listProvider;
        this.context =context;
        this.achievementInterface = achievementInterface;
        this.booleanArrayList = booleanArrayList;
        this.requestManager = requestManager;
        this.groupProfileArrayList = groupProfileArrayList;
    }

    @Override
    public AchivementListAdapter.AchievementListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acheivement_list,parent,false);

        return new AchievementListHolder(view);

    }

    @Override
    public void onBindViewHolder(final AchivementListAdapter.AchievementListHolder holder, int position) {

        holder.title.setText(listProvider.get(position).getTitle());
        holder.university.setText(groupProfileArrayList.get(position).getGroup_name());
        holder.likes.setText(context.getString(R.string.likes,listProvider.get(position).getLikes()));
        holder.date.setText(listProvider.get(position).getDate());

        holder.achievement_chk_box.setChecked(booleanArrayList.get(position));

        if(Achievement_Fragment.isLongPressed)
        {
            holder.delete_checkBox.setVisibility(View.VISIBLE);

            if(Achievement_Fragment.wishedToDelete.get(position))
            {
                holder.delete_checkBox.setChecked(true);
            }
            else
            {
                holder.delete_checkBox.setChecked(false);
            }
        }
        else
        {
            holder.delete_checkBox.setVisibility(View.GONE);
            holder.delete_checkBox.setChecked(false);
        }

        requestManager
                .load(listProvider.get(position).getImagepath())
                .centerCrop()
                .placeholder(R.mipmap.default_event_achievement_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.default_event_achievement_error)
                .into(holder.achivement_img);

        requestManager
                .load(groupProfileArrayList.get(position).getGroup_profile_path())
                .dontAnimate()
                .placeholder(R.mipmap.create_group_profile)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.mipmap.create_group_profile)
                .into(holder.profile);

    }

    @Override
    public int getItemCount() {
        return listProvider.size();
    }


    public class AchievementListHolder extends RecyclerView.ViewHolder{

        final TextView title,university,likes,date;
        final ImageView achivement_img,profile;
        final CheckBox achievement_chk_box,delete_checkBox;
        final ImageButton facebook,gplus;

        public AchievementListHolder(final View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.tv_acheivement_list_title);
            university = (TextView) itemView.findViewById(R.id.tv_acheivement_list_uni_name);
            likes = (TextView) itemView.findViewById(R.id.tv_acheivement_list_likes);
            date = (TextView) itemView.findViewById(R.id.acheivement_list_date);
            achivement_img = (ImageView) itemView.findViewById(R.id.acheivement_list_img);
            profile = (ImageView) itemView.findViewById(R.id.achievement_profile);

            delete_checkBox = (CheckBox) itemView.findViewById(R.id.delete_ach);
            achievement_chk_box = (CheckBox) itemView.findViewById(R.id.acheivement_list_chk_box);

            delete_checkBox.setVisibility(View.GONE);

            facebook = (ImageButton)itemView.findViewById(R.id.share_facebook_ach);
            gplus = (ImageButton)itemView.findViewById(R.id.share_gplus_ach);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(),"AlteHaasGroteskRegular.ttf");
            university.setTypeface(typeface);

            typeface = Typeface.createFromAsset(context.getAssets(), "AlteHaasGroteskBold.ttf");
            title.setTypeface(typeface);

            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    achivement_img.buildDrawingCache();
                    achievementInterface.fbClicked(getAdapterPosition());
                }
            });

            gplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    achivement_img.buildDrawingCache();
                    achievementInterface.gplusClicked(achivement_img.getDrawingCache(),getAdapterPosition());
                }
            });

            achivement_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Achievement_Fragment.isLongPressed)
                    {
                        achievementInterface.deleteItem(getAdapterPosition(),!delete_checkBox.isChecked());
                    }
                    else
                    {
                        achivement_img.buildDrawingCache();
                        profile.buildDrawingCache();
                        achievementInterface.achievementBTN(getAdapterPosition());
                        achivement_img.destroyDrawingCache();
                        profile.destroyDrawingCache();
                    }
                }
            });

            delete_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(buttonView.isPressed()){
                        achievementInterface.deleteItem(getAdapterPosition(),isChecked);
                    }

                }
            });

            achivement_img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    achivement_img.buildDrawingCache();
                    profile.buildDrawingCache();
                    achievementInterface.onLongClicked
                            (getAdapterPosition(),!delete_checkBox.isChecked());
                    return true;
                }
            });

            achievement_chk_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                   if(buttonView.isPressed())
                   {
                       achievementInterface.achievementLike(isChecked,getAdapterPosition());
                   }
               }
           });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface AchievementInterface {
        void achievementLike(boolean isChecked,int position);
        void achievementBTN(int position);
        void gplusClicked(Bitmap bitmap,int position);
        void fbClicked(int position);
        void onLongClicked(int position, boolean isChecked);
        void deleteItem(int position,boolean isChecked);
    }
}
