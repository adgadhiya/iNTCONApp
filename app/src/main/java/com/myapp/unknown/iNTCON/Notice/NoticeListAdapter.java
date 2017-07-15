package com.myapp.unknown.iNTCON.Notice;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.unknown.iNTCON.R;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by UNKNOWN on 9/12/2016.
 */
public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.MyHolder>
        implements StickyHeaderAdapter<NoticeListAdapter.HeaderHolder> {

    private final ArrayList<NoticeListProvider> noticeListProviders;
    private final ArrayList<UserProfile> userProfiles;
    private final ArrayList<Integer> msg_num;
    private Date date;
    private final Context context;
    private final ArrayList<Boolean> isLoadingOrUpdating;
    private final ArrayList<Boolean> isImportant;
    private final SimpleDateFormat format;
    private long timeMillis;
    private final NoticeInterface noticeInterface;


    public NoticeListAdapter(ArrayList<NoticeListProvider> noticeListProviders,
                             ArrayList<UserProfile> userProfiles,
                             Context context,
                             ArrayList<Integer> msg_num,
                             ArrayList<Boolean> isLoadingOrUpdating,
                             ArrayList<Boolean> isImportant,
                             NoticeInterface noticeInterface) {
        this.noticeListProviders = noticeListProviders;
        this.noticeInterface = noticeInterface;
        this.context = context ;
        this.userProfiles = userProfiles;
        this.msg_num = msg_num;
        this.isLoadingOrUpdating = isLoadingOrUpdating;
        this.isImportant = isImportant;

        format = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    }


    @Override
    public NoticeListAdapter.MyHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_item, viewGroup, false);
        return new MyHolder(view);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////NOTICE BIND VIEW HOLDER//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        holder.name2.setText(userProfiles.get(position).getUserName());
        holder.date.setText(noticeListProviders.get(position).getTime().toUpperCase());
        holder.title.setText(noticeListProviders.get(position).getTitle());
        holder.detail.setText(noticeListProviders.get(position).getMessage());
        holder.imp_or_not.setChecked(isImportant.get(position));


        Glide.with(context)
                .load(userProfiles.get(position).getUserProfile())
                .placeholder(R.mipmap.profile_default)
                .dontAnimate()
                .bitmapTransform(new RoundedCornersTransformation(context,5,0))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imageView);

        if(Notice_Fragment.isLongPressed)
        {
            holder.delete_notice.setVisibility(View.VISIBLE);

            if(Notice_Fragment.wishedTodelete.get(position))
            {
                holder.delete_notice.setChecked(true);
                holder.linearLayout.setBackgroundResource(R.color.item_not_selected);
            }
            else
            {
                holder.linearLayout.setBackgroundResource(R.color.item_selected);
                holder.delete_notice.setChecked(false);
            }
        }
        else
        {
            holder.delete_notice.setChecked(false);
            holder.delete_notice.setVisibility(View.GONE);

            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            holder.linearLayout.setBackgroundResource(backgroundResource);
            typedArray.recycle();

        }

        try {
            date = format.parse(noticeListProviders.get(position).getTime());
            timeMillis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

       if(!isLoadingOrUpdating.get(position) && isImportant.get(position))
       {
           holder.chat_cnt.setTextColor(Color.WHITE);
           holder.chat_cnt.setBackgroundResource(R.drawable.round_new_count);
       }
        else
       {
           holder.chat_cnt.setTextColor(Color.rgb(170,170,170));
           holder.chat_cnt.setBackgroundResource(R.drawable.round);
       }

        if(msg_num.get(position) == 0)
        {
            holder.chat_cnt.setVisibility(View.GONE);
        }
        else
        {
            holder.chat_cnt.setVisibility(View.VISIBLE);

            if(msg_num.get(position) > 99)
            {
                holder.chat_cnt.setText(context.getString(R.string.chat_cnt));
            }
            else
            {
                holder.chat_cnt.setText(String.valueOf(msg_num.get(position)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return noticeListProviders.size();
    }

    @Override
    public long getHeaderId(int position) {
        return noticeListProviders.get(position).getNumber();
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////CREATE HEADER VIEW HOLDER//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_header, parent, false);
        return new HeaderHolder(view);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////HEADER BIND VIEW HOLDER////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {

        try {
            date = format.parse(noticeListProviders.get(position).getDate());
            timeMillis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeMillisYest = timeMillis + 86400000;

        if(DateUtils.isToday(timeMillis))
        {
            viewholder.header.setText(context.getString(R.string.today));
        }
        else if(DateUtils.isToday(timeMillisYest))
        {
            viewholder.header.setText(context.getString(R.string.yesterday));
        }
        else
        {
            viewholder.header.setText(noticeListProviders.get(position).getDate().toUpperCase());
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////LIST HOLDER/////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView item;

        final TextView name2,title,date,detail,chat_cnt;
        final CheckBox imp_or_not,delete_notice;
        final ImageView imageView;
        final LinearLayout linearLayout;

        public MyHolder(final View itemView) {
            super(itemView);

            name2 = (TextView)itemView.findViewById(R.id.name_tv_notice);
            title = (TextView)itemView.findViewById(R.id.title_tv_notice);
            date = (TextView)itemView.findViewById(R.id.date_tv_notice);
            detail = (TextView) itemView.findViewById(R.id.detail_tv_notice);
            chat_cnt = (TextView)itemView.findViewById(R.id.chat_cnt);
            imageView = (ImageView) itemView.findViewById(R.id.faculty_profile_notice);
            imp_or_not = (CheckBox)itemView.findViewById(R.id.imp_or_not_chk_box_notice);
            delete_notice = (CheckBox) itemView.findViewById(R.id.delete_notice);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.notice_list_linear_layout);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(),"Roboto-Regular.ttf");
            title.setTypeface(typeface);
            detail.setTypeface(typeface);
            name2.setTypeface(typeface);

            imp_or_not.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(buttonView.isPressed()){
                        noticeInterface.onChkBoxChecked(isChecked,getAdapterPosition());
                    }
                }
            });

            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    noticeInterface.onLongClicked(getAdapterPosition(),!delete_notice.isChecked());
                    return true;
                }
            });

            delete_notice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.isPressed()){
                        noticeInterface.deleteItem(getAdapterPosition(),isChecked);
                    }
                }
            });

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Notice_Fragment.isLongPressed)
                    {
                        noticeInterface.deleteItem(getAdapterPosition(),!delete_notice.isChecked());
                    }
                    else
                    {
                        noticeInterface.onNoticeClick(getAdapterPosition());
                        chat_cnt.setTextColor(Color.rgb(170,170,170));
                        chat_cnt.setBackgroundResource(R.drawable.round);
                    }
                }
            });
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////HEADER HOLDER/////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public final TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView.findViewById(R.id.header);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////NOTICE INTERFACE//////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public interface NoticeInterface{
        void onNoticeClick(int position);
        void deleteItem(int position,boolean isChecked);
        void onChkBoxChecked(boolean isChecked,int position);
        void onLongClicked(int position,boolean isChecked);
    }
}