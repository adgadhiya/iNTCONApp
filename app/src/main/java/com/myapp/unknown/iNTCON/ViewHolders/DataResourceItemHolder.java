package com.myapp.unknown.iNTCON.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.unknown.iNTCON.R;

/**
 * Created by UNKNOWN on 2/6/2017.
 */
public class DataResourceItemHolder extends RecyclerView.ViewHolder {

    public final TextView title_tv,date,time,size;
    public final ImageView resource_type;
    public final LinearLayout linearLayout;
    public final EditText title_et;


    public DataResourceItemHolder(View itemView) {
        super(itemView);

        title_et = (EditText) itemView.findViewById(R.id.data_resource_resource_title_et);
        date = (TextView) itemView.findViewById(R.id.data_resource_resource_date);
        time = (TextView) itemView.findViewById(R.id.data_resource_resource_time);
        size = (TextView) itemView.findViewById(R.id.data_resource_resource_size);
        resource_type = (ImageView) itemView.findViewById(R.id.data_resource_resource_type_iv);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.upload_resource__layout);
        title_tv = (TextView) itemView.findViewById(R.id.data_resource_resource_title_tv);

    }

    public void bindToUIlayout(View.OnClickListener onClickListener)
    {
        linearLayout.setOnClickListener(onClickListener);
    }

    public void bindToUIFocusChanged(View.OnFocusChangeListener onFocusChangeListener)
    {
        title_et.setOnFocusChangeListener(onFocusChangeListener);
    }

}
