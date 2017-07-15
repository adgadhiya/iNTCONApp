package com.myapp.unknown.iNTCON.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.unknown.iNTCON.R;

/**
 * Created by UNKNOWN on 2/1/2017.
 */
public class UserRequestViewHolder extends RecyclerView.ViewHolder {

    final public TextView user_name,user_field,user_email,requested_on;
    final public ImageView user_profile;
    final public Button admin_btn,user_btn,cancel_btn;

    public UserRequestViewHolder(View itemView) {
        super(itemView);

        user_name = (TextView) itemView.findViewById(R.id.user_request_user_name);
        user_field = (TextView) itemView.findViewById(R.id.user_request_user_field);
        user_email = (TextView) itemView.findViewById(R.id.user_request_user_email);
        requested_on = (TextView) itemView.findViewById(R.id.user_request_requested_on);
        user_profile = (ImageView) itemView.findViewById(R.id.user_request_user_profile);
        admin_btn = (Button) itemView.findViewById(R.id.user_request_confirm_admin);
        user_btn = (Button) itemView.findViewById(R.id.user_request_confirm_user);
        cancel_btn = (Button) itemView.findViewById(R.id.user_request_cancel_request);

    }

    public void bindToUI(View.OnClickListener onClickListener)
    {
        admin_btn.setOnClickListener(onClickListener);
        user_btn.setOnClickListener(onClickListener);
        cancel_btn.setOnClickListener(onClickListener);
    }
}

