package com.myapp.unknown.iNTCON.Acheivement;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 9/8/2016.
 */
public class AchievementListProvider implements Parcelable {

    public String group_key, title, date, imagepath, key,content,uid;
    public float likes;
    public int group_type;

    public AchievementListProvider(){

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Creator<AchievementListProvider> getCREATOR() {
        return CREATOR;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGroup_key() {
        return group_key;
    }

    public void setGroup_key(String group_key) {
        this.group_key = group_key;
    }

    public int getGroup_type() {
        return group_type;
    }

    public void setGroup_type(int group_type) {
        this.group_type = group_type;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public float getLikes() {
        return likes;
    }

    public void setLikes(float likes) {
        this.likes = likes;
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

    protected AchievementListProvider(Parcel in) {
        group_key = in.readString();
        title = in.readString();
        date = in.readString();
        imagepath = in.readString();
        key = in.readString();
        content = in.readString();
        uid = in.readString();
        likes = in.readFloat();
        group_type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(group_key);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(imagepath);
        dest.writeString(key);
        dest.writeString(content);
        dest.writeString(uid);
        dest.writeFloat(likes);
        dest.writeInt(group_type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AchievementListProvider> CREATOR = new Creator<AchievementListProvider>() {
        @Override
        public AchievementListProvider createFromParcel(Parcel in) {
            return new AchievementListProvider(in);
        }

        @Override
        public AchievementListProvider[] newArray(int size) {
            return new AchievementListProvider[size];
        }
    };
}