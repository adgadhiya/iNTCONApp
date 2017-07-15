package com.myapp.unknown.iNTCON.OtherClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 1/27/2017.
 */
public class GroupProfile implements Parcelable {

    String group_name,group_profile_path,group_id,group_name_lower;

    public GroupProfile(){

    }

    public GroupProfile(String group_id, String group_name, String group_name_lower, String group_profile_path) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_name_lower = group_name_lower;
        this.group_profile_path = group_profile_path;
    }

    protected GroupProfile(Parcel in) {
        group_name = in.readString();
        group_profile_path = in.readString();
        group_id = in.readString();
        group_name_lower = in.readString();
    }

    public static final Creator<GroupProfile> CREATOR = new Creator<GroupProfile>() {
        @Override
        public GroupProfile createFromParcel(Parcel in) {
            return new GroupProfile(in);
        }

        @Override
        public GroupProfile[] newArray(int size) {
            return new GroupProfile[size];
        }
    };

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_name_lower() {
        return group_name_lower;
    }

    public void setGroup_name_lower(String group_name_lower) {
        this.group_name_lower = group_name_lower;
    }

    public String getGroup_profile_path() {
        return group_profile_path;
    }

    public void setGroup_profile_path(String group_profile_path) {
        this.group_profile_path = group_profile_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(group_name);
        parcel.writeString(group_profile_path);
        parcel.writeString(group_id);
        parcel.writeString(group_name_lower);
    }
}

