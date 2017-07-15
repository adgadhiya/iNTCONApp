package com.myapp.unknown.iNTCON.OtherClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 1/27/2017.
 */
public class UserProfile implements Parcelable {

    String userName,userProfile,user_id,userName_lower;

    public UserProfile(){

    }

    public UserProfile(String user_id, String userName, String userName_lower, String userProfile) {
        this.user_id = user_id;
        this.userName = userName;
        this.userName_lower = userName_lower;
        this.userProfile = userProfile;
    }

    protected UserProfile(Parcel in) {
        userName = in.readString();
        userProfile = in.readString();
        user_id = in.readString();
        userName_lower = in.readString();
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName_lower() {
        return userName_lower;
    }

    public void setUserName_lower(String userName_lower) {
        this.userName_lower = userName_lower;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(userProfile);
        parcel.writeString(user_id);
        parcel.writeString(userName_lower);
    }
}

