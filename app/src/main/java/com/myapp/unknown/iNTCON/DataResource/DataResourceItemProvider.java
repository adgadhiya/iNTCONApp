package com.myapp.unknown.iNTCON.DataResource;

/**
 * Created by UNKNOWN on 2/6/2017.
 */
public class DataResourceItemProvider {

    String title,date,time,uid,key,download_path;
    long size;

    public DataResourceItemProvider(){

    }

    public DataResourceItemProvider(String date, String download_path, String key, long size, String time, String title, String uid) {
        this.date = date;
        this.download_path = download_path;
        this.key = key;
        this.size = size;
        this.time = time;
        this.title = title;
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public String getDownload_path() {
        return download_path;
    }

    public void setDownload_path(String download_path) {
        this.download_path = download_path;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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
}
