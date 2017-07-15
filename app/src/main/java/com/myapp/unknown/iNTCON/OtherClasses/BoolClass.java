package com.myapp.unknown.iNTCON.OtherClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 10/22/2016.
 */
public class BoolClass implements Parcelable {

    public boolean like_go_int;

    public BoolClass(){

    }

    protected BoolClass(Parcel in) {
        like_go_int = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (like_go_int ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BoolClass> CREATOR = new Creator<BoolClass>() {
        @Override
        public BoolClass createFromParcel(Parcel in) {
            return new BoolClass(in);
        }

        @Override
        public BoolClass[] newArray(int size) {
            return new BoolClass[size];
        }
    };
}
