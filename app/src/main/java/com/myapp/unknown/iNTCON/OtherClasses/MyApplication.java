package com.myapp.unknown.iNTCON.OtherClasses;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by UNKNOWN on 9/17/2016.
 */
public class MyApplication extends Application {

    private static int appIsInBackGround;
    private static boolean isUploading;
    private static boolean isDownloading;
    private static boolean isSharingOn;

    @Override
    public void onCreate() {
        super.onCreate();
        appIsInBackGround = 0;
        isUploading = false;
        isDownloading = false;
        isSharingOn = false;

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static boolean isAppOnBackGround(){
        return appIsInBackGround == 0 ;
    }

    public static void activityStarted(){
        appIsInBackGround++;
    }

    public static void activityStopped(){
        appIsInBackGround--;
    }


    public static boolean isDownloading() {
        return isDownloading;
    }

    public static void setIsDownloading(boolean isDownloading) {
        MyApplication.isDownloading = isDownloading;
    }

    public static boolean isUploading() {
        return isUploading;
    }

    public static void setIsUploading(boolean isUploading) {
        MyApplication.isUploading = isUploading;
    }

    public static boolean isSharingOn() {
        return isSharingOn;
    }

    public static void setIsSharingOn(boolean isSharingOn) {
        MyApplication.isSharingOn = isSharingOn;
    }
}
