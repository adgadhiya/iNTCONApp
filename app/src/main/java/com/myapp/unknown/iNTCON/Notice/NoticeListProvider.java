package com.myapp.unknown.iNTCON.Notice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 9/12/2016.
 */
public class NoticeListProvider implements Parcelable {

    public String department,title,date,time,message,key,img_path,uid;
    public int number;

    public NoticeListProvider(){

    }


    public static Creator<NoticeListProvider> getCREATOR() {
        return CREATOR;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    protected NoticeListProvider(Parcel in) {
        department = in.readString();
        title = in.readString();
        date = in.readString();
        time = in.readString();
        message = in.readString();
        key = in.readString();
        img_path = in.readString();
        uid = in.readString();
        number = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(department);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(message);
        dest.writeString(key);
        dest.writeString(img_path);
        dest.writeString(uid);
        dest.writeInt(number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NoticeListProvider> CREATOR = new Creator<NoticeListProvider>() {
        @Override
        public NoticeListProvider createFromParcel(Parcel in) {
            return new NoticeListProvider(in);
        }

        @Override
        public NoticeListProvider[] newArray(int size) {
            return new NoticeListProvider[size];
        }
    };
}
