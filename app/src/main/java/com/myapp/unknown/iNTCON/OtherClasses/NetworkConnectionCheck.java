package com.myapp.unknown.iNTCON.OtherClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by UNKNOWN on 9/13/2016.
 */
public class NetworkConnectionCheck {

    private final Context context;

    public NetworkConnectionCheck(Context context){

        this.context = context;
    }


    public boolean checkingConnextion(){

        boolean flag = false,mobile,wifi;

        ConnectivityManager manager = (ConnectivityManager)context. getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activenetwork=manager.getActiveNetworkInfo();

        if(activenetwork!=null)
        {
            mobile=activenetwork.getType()==ConnectivityManager.TYPE_MOBILE;
            wifi=activenetwork.getType()==ConnectivityManager.TYPE_WIFI;
            if(activenetwork.isConnected()||activenetwork.isConnectedOrConnecting()){
                if(wifi || mobile){
                    flag=false;
                }
                else if(!wifi || !mobile){
                    flag=true;
                }
            }
        }
        else
        {
            flag=true;
        }
        return flag;

    }

}
