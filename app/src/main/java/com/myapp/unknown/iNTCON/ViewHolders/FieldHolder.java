package com.myapp.unknown.iNTCON.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.unknown.iNTCON.R;

/**
 * Created by UNKNOWN on 12/30/2016.
 */
public class FieldHolder extends RecyclerView.ViewHolder {

    final public TextView uni_field;
    final public ImageButton delete_btn;
    final public LinearLayout linearLayout;

    public FieldHolder(View itemView) {
        super(itemView);
        uni_field = (TextView) itemView.findViewById(R.id.uni_field);
        delete_btn = (ImageButton) itemView.findViewById(R.id.delete_field);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.default_field);
    }

    public void deleteField(View.OnClickListener onClickListener){
        delete_btn.setOnClickListener(onClickListener);
        linearLayout.setOnClickListener(onClickListener);
    }
}
