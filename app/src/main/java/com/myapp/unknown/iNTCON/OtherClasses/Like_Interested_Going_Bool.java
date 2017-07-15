package com.myapp.unknown.iNTCON.OtherClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 2/17/2017.
 */
public class Like_Interested_Going_Bool implements Parcelable{

    boolean likes = false;
    boolean interested = false;
    boolean going = false;

    public Like_Interested_Going_Bool(){

    }

    public Like_Interested_Going_Bool(boolean going, boolean interested, boolean likes) {
        this.going = going;
        this.interested = interested;
        this.likes = likes;
    }

    protected Like_Interested_Going_Bool(Parcel in) {
        likes = in.readByte() != 0;
        interested = in.readByte() != 0;
        going = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (likes ? 1 : 0));
        dest.writeByte((byte) (interested ? 1 : 0));
        dest.writeByte((byte) (going ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Like_Interested_Going_Bool> CREATOR = new Creator<Like_Interested_Going_Bool>() {
        @Override
        public Like_Interested_Going_Bool createFromParcel(Parcel in) {
            return new Like_Interested_Going_Bool(in);
        }

        @Override
        public Like_Interested_Going_Bool[] newArray(int size) {
            return new Like_Interested_Going_Bool[size];
        }
    };

    public boolean isGoing() {
        return going;
    }

    public void setGoing(boolean going) {
        this.going = going;
    }

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public boolean isLikes() {
        return likes;
    }

    public void setLikes(boolean likes) {
        this.likes = likes;
    }
}
