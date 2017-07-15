package com.myapp.unknown.iNTCON.OtherClasses;

import android.os.Build;

/**
 * Created by UNKNOWN on 12/17/2016.
 */
public class BugReportClass {

    public String title,detail,attachment,uid;
    public final String fingerPrint = Build.FINGERPRINT;

    public BugReportClass(){

    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
