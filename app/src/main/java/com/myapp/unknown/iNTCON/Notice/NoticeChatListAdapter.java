package com.myapp.unknown.iNTCON.Notice;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.unknown.iNTCON.Campaign.Campaign_Chat;
import com.myapp.unknown.iNTCON.Campaign.URLSpanNoUnderline;
import com.myapp.unknown.iNTCON.R;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.unknown.iNTCON.OtherClasses.UserProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by UNKNOWN on 9/8/2016.
 */
public class NoticeChatListAdapter extends RecyclerView.Adapter<NoticeChatListAdapter.NoticeChatListHolder> {

    private static final int TYPE_SELF = 0;
    private static final int TYPE_SELF_SECOND = 1;
    private static final int TYPE_OTHER = 2;
    private static final int TYPE_OTHER_SECOND = 3;

    private long timeMillis;

    private final Context context;

    private final ArrayList<NoticeChatListProvider> listProvider;
    private final ArrayList<UserProfile> userProfiles;
    private final ArrayList<Boolean> wishedToDelete;

    private final NoticeChatInterface noticeChatInterface;

    private final boolean isCAMPAIGN;

    public NoticeChatListAdapter(Context context,ArrayList<NoticeChatListProvider> listProvider,
                                 ArrayList<UserProfile> userProfiles,
                                 boolean isCAMPAIGN,
                                 ArrayList<Boolean> wishedToDelete,
                                 NoticeChatInterface noticeChatInterface) {
        this.context = context;
        this.listProvider = listProvider;
        this.noticeChatInterface = noticeChatInterface;
        this.wishedToDelete = wishedToDelete;
        this.userProfiles = userProfiles;
        this.isCAMPAIGN = isCAMPAIGN;
    }

    @Override
    public NoticeChatListAdapter.NoticeChatListHolder onCreateViewHolder(ViewGroup parent, int viewType) {


          if(viewType == TYPE_SELF) {

              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.self_sent_notice,parent,false);
              return new NoticeChatListHolder(view,viewType);

          } else if (viewType == TYPE_SELF_SECOND) {

              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.self_sent_second_notice,parent,false);
              return new NoticeChatListHolder(view,viewType);


          } else if (viewType == TYPE_OTHER) {

              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_sent_notice,parent,false);
              return new NoticeChatListHolder(view,viewType);


          } else if(viewType == TYPE_OTHER_SECOND) {

              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_sent_second_notice,parent,false);
              return new NoticeChatListHolder(view,viewType);
          }

        return null;
    }

    @Override
    public void onBindViewHolder(NoticeChatListAdapter.NoticeChatListHolder holder, int position) {

            if(holder.checkBox != null)
            {
                if(Campaign_Chat.isLongPressed || Notices_Chat.isLongPressed)
                {
                    if(wishedToDelete.get(position))
                    {
                        holder.linearLayout.setSelected(true);
                    }
                    else
                    {
                        holder.linearLayout.setSelected(false);
                    }
                }
                else
                {
                    holder.linearLayout.setSelected(false);
                }
            }

            holder.name.setText(userProfiles.get(position).getUserName());
            holder.message.setText(listProvider.get(position).getMessage());

            Linkify.addLinks(holder.message,Linkify.ALL);
            stripUnderlines(holder.message);

            holder.time.setText(listProvider.get(position).getTime().toUpperCase());

        if(holder.imageView != null)
        {
            Glide.with(context)
                    .load(userProfiles.get(position).getUserProfile())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.imageView);
        }

        if(isCAMPAIGN)
        {

            if(position == 0 ||
                    !listProvider.get(position).getDate().equals(listProvider.get(position - 1).getDate()) )
            {
                holder.cardView.setVisibility(View.VISIBLE);
                setDate(holder);

            }
            else
            {
                holder.cardView.setVisibility(View.GONE);
            }
        }
        else
        {
            if((position == listProvider.size() - 1) ||
                    !listProvider.get(position).getDate().equals(listProvider.get(position + 1).getDate()))
            {

                holder.cardView.setVisibility(View.VISIBLE);
                setDate(holder);
            }
            else
            {
                holder.cardView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {

        return listProvider.size();

    }

    public class NoticeChatListHolder extends RecyclerView.ViewHolder{

        int viewType;

        //CHAT MESSAGE
        TextView name,date,message,time;
        CardView cardView;
        LinearLayout linearLayout;
        CheckBox checkBox;
        ImageView imageView;

        public NoticeChatListHolder(View itemView, final int viewType) {
            super(itemView);

            if(viewType == TYPE_SELF){

                name = (TextView) itemView.findViewById(R.id.self_notice_name);
                date = (TextView) itemView.findViewById(R.id.self_notice_date);
                message = (TextView) itemView.findViewById(R.id.self_notice_message);
                time = (TextView) itemView.findViewById(R.id.self_notice_time);
                cardView = (CardView)itemView.findViewById(R.id.self_notice_card);
                linearLayout = (LinearLayout)itemView.findViewById(R.id.self_send_linear);
                checkBox = (CheckBox) itemView.findViewById(R.id.self_send_chk_box);
                message.setMovementMethod(LinkMovementMethod.getInstance());
                this.viewType = 0 ;

            } else if (viewType == TYPE_SELF_SECOND) {

                name = (TextView) itemView.findViewById(R.id.self_second_notice_name);
                date = (TextView) itemView.findViewById(R.id.self_notice_second_date);
                message = (TextView) itemView.findViewById(R.id.self_notice_second_message);
                time = (TextView) itemView.findViewById(R.id.self_notice_second_time);
                cardView = (CardView)itemView.findViewById(R.id.self_notice_second_card);
                linearLayout = (LinearLayout)itemView.findViewById(R.id.self_send_second_linear);
                checkBox = (CheckBox) itemView.findViewById(R.id.self_send_second_chk_box);
                message.setMovementMethod(LinkMovementMethod.getInstance());
                this.viewType = 1 ;

            } else if (viewType == TYPE_OTHER) {

                name = (TextView) itemView.findViewById(R.id.other_notice_name);
                date = (TextView) itemView.findViewById(R.id.other_notice_date);
                message = (TextView) itemView.findViewById(R.id.other_notice_message);
                time = (TextView) itemView.findViewById(R.id.other_notice_time);
                cardView = (CardView)itemView.findViewById(R.id.other_notice_card);
                linearLayout = (LinearLayout)itemView.findViewById(R.id.other_send__linear);
                imageView = (ImageView) itemView.findViewById(R.id.other_profile);
                message.setMovementMethod(LinkMovementMethod.getInstance());
                this.viewType = 2 ;

            } else if (viewType == TYPE_OTHER_SECOND) {
                name = (TextView) itemView.findViewById(R.id.other_notice_second_name);
                date = (TextView) itemView.findViewById(R.id.other_notice_second_date);
                message = (TextView) itemView.findViewById(R.id.other_notice_second_message);
                time = (TextView) itemView.findViewById(R.id.other_notice_second_time);
                cardView = (CardView)itemView.findViewById(R.id.other_notice_second_card);
                linearLayout = (LinearLayout)itemView.findViewById(R.id.other_send_second_linear);
                message.setMovementMethod(LinkMovementMethod.getInstance());
                this.viewType = 3 ;
            }
            
            if(linearLayout == null){
                return;
            }

            message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(checkBox != null)
                    {
                        noticeChatInterface.onLongClicked(getAdapterPosition(),!checkBox.isChecked());
                    }

                    return true;
                }
            });

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(viewType == TYPE_SELF || viewType == TYPE_SELF_SECOND)
                    {
                        noticeChatInterface.onMessageClicked(getAdapterPosition(),!linearLayout.isSelected());
                    }
                }
            });

            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(viewType == TYPE_SELF || viewType == TYPE_SELF_SECOND)
                    {
                        noticeChatInterface.onLongClicked(getAdapterPosition(),!linearLayout.isSelected());
                    }

                    return true;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int  position) {

        if(isCAMPAIGN)
        {

            if(position == 0)
            {
                return compare(position);
            }
            else
            {
                if(listProvider.get(position - 1).getUid().equals(listProvider.get(position).getUid()))
                {
                    if(listProvider.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))   ///CHECK PERSON IS SELF OR NOT
                    {
                        return TYPE_SELF_SECOND;
                    } else
                    {
                        return TYPE_OTHER_SECOND;
                    }
                }
                else
                {
                    return compare(position);
                }
            }
        }
        else
        {
            if(position == listProvider.size() - 1)
            {
                return  compare(position);
            }
            else
            {

                if(listProvider.get(position + 1).getUid().equals(listProvider.get(position).getUid())) ///CHECK SECOND MESSAGE OR NOT
                {
                    if(listProvider.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))   ///CHECK PERSON IS SELF OR NOT
                    {
                        return TYPE_SELF_SECOND;
                    } else
                    {
                        return TYPE_OTHER_SECOND;
                    }
                } else {
                    return compare(position);
                }
            }
        }
    }

    public interface NoticeChatInterface{
        void onLongClicked(int position,boolean isChecked);
        void onMessageClicked(int position, boolean isChecked);
    }

    private int compare(int position){

        if(listProvider.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            return TYPE_SELF;
        } else
        {
            return TYPE_OTHER;
        }
    }

    private void setDate(NoticeChatListHolder holder)
    {
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

        try {
            Date todayDate = format.parse(listProvider.get(holder.getAdapterPosition()).getDate());
            timeMillis = todayDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeMillisYest = timeMillis + 86400000;

        if(DateUtils.isToday(timeMillis))
        {
            holder.date.setText(context.getString(R.string.today));
        }
        else if(DateUtils.isToday(timeMillisYest))
        {
            holder.date.setText(context.getString(R.string.yesterday));
        }
        else
        {
            holder.date.setText(listProvider.get(holder.getAdapterPosition()).getDate().toUpperCase());
        }
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }
}
