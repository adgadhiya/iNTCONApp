package com.myapp.unknown.iNTCON.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.unknown.iNTCON.R;

/**
 * Created by UNKNOWN on 3/3/2017.
 */

public class FAQViewHolder extends RecyclerView.ViewHolder {

    public final TextView title,detail;
    public final LinearLayout linearLayout;
    public final ImageView imageView;


    public FAQViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.faq_title);
        detail = (TextView) itemView.findViewById(R.id.faq_detail);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.faq_layout);
        imageView = (ImageView) itemView.findViewById(R.id.faq_iv);
    }

    public void bindToUIlayout(View.OnClickListener onClickListener)
    {
        linearLayout.setOnClickListener(onClickListener);
    }
}
