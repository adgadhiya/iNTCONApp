package com.myapp.unknown.iNTCON.DataResource;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 2/4/2017.
 */
public class DataResourceProvider implements Parcelable {

    String uid,title,created_on,key;
    int item_cnt,comment_cnt;

    public DataResourceProvider(){
        
    }

    public DataResourceProvider(int comment_cnt, String created_on, int item_cnt, String key, String title, String uid) {
        this.comment_cnt = comment_cnt;
        this.created_on = created_on;
        this.item_cnt = item_cnt;
        this.key = key;
        this.title = title;
        this.uid = uid;
    }


    protected DataResourceProvider(Parcel in) {
        uid = in.readString();
        title = in.readString();
        created_on = in.readString();
        key = in.readString();
        item_cnt = in.readInt();
        comment_cnt = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(title);
        dest.writeString(created_on);
        dest.writeString(key);
        dest.writeInt(item_cnt);
        dest.writeInt(comment_cnt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataResourceProvider> CREATOR = new Creator<DataResourceProvider>() {
        @Override
        public DataResourceProvider createFromParcel(Parcel in) {
            return new DataResourceProvider(in);
        }

        @Override
        public DataResourceProvider[] newArray(int size) {
            return new DataResourceProvider[size];
        }
    };

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt() {
        this.comment_cnt = 0;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public int getItem_cnt() {
        return item_cnt;
    }

    public void setItem_cnt() {
        this.item_cnt = 0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
