package com.myapp.unknown.iNTCON.OtherClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 2/17/2017.
 */
public class Like_Interested_Going_Cnt implements Parcelable {

    long likes = 0,going = 0,interested = 0 ;

    public Like_Interested_Going_Cnt(){

    }

    public Like_Interested_Going_Cnt(long going, long interested, long likes) {
        this.going = going;
        this.interested = interested;
        this.likes = likes;
    }


    protected Like_Interested_Going_Cnt(Parcel in) {
        likes = in.readLong();
        going = in.readLong();
        interested = in.readLong();
    }

    public static final Creator<Like_Interested_Going_Cnt> CREATOR = new Creator<Like_Interested_Going_Cnt>() {
        @Override
        public Like_Interested_Going_Cnt createFromParcel(Parcel in) {
            return new Like_Interested_Going_Cnt(in);
        }

        @Override
        public Like_Interested_Going_Cnt[] newArray(int size) {
            return new Like_Interested_Going_Cnt[size];
        }
    };

    public long getGoing() {
        return going;
    }

    public void setGoing(long going) {
        this.going = going;
    }

    public long getInterested() {
        return interested;
    }

    public void setInterested(long interested) {
        this.interested = interested;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(likes);
        parcel.writeLong(going);
        parcel.writeLong(interested);
    }
}
