package com.myapp.unknown.iNTCON.NavigationView;

/**
 * Created by UNKNOWN on 1/29/2017.
 */
public class GroupList {

    String group_id,user_id,about_group,created_on;
    int group_type;

    public GroupList(){

    }

    public GroupList(String about_group, String created_on, String group_id, int group_type, String user_id) {
        this.about_group = about_group;
        this.created_on = created_on;
        this.group_id = group_id;
        this.group_type = group_type;
        this.user_id = user_id;
    }

    public String getAbout_group() {
        return about_group;
    }

    public void setAbout_group(String about_group) {
        this.about_group = about_group;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public int getGroup_type() {
        return group_type;
    }

    public void setGroup_type(int group_type) {
        this.group_type = group_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
