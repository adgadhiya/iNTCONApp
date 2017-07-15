package com.myapp.unknown.iNTCON.Notice;

/**
 * Created by UNKNOWN on 9/8/2016.
 */
public class NoticeChatListProvider {

    public String group_key,message,date,key,uid,time;

    public NoticeChatListProvider(){

    }

    public NoticeChatListProvider(String date, String message, String group_key,String key,String uid,String time) {
        this.date = date;
        this.message = message;
        this.group_key = group_key;
        this.key = key;
        this.uid = uid;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroupKey() {
        return group_key;
    }

    public void setGroupKey(String group_key) {
        this.group_key = group_key;
    }
}
