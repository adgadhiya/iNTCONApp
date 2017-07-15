package com.myapp.unknown.iNTCON.OtherClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;

import com.myapp.unknown.iNTCON.R;

/**
 * Created by UNKNOWN on 9/14/2016.
 */
public class DialogHandler extends Handler {

    final private ProgressDialog p;

    public DialogHandler(Context context, boolean isCancelable){
        p = new ProgressDialog(context,R.style.MyTheme2);
        p.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        p.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCancelable(isCancelable);
        p.show();

    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if(p.isShowing()) {
            p.dismiss();
        }
    }

}
