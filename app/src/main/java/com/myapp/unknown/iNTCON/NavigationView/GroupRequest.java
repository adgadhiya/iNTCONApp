package com.myapp.unknown.iNTCON.NavigationView;

/**
 * Created by UNKNOWN on 1/29/2017.
 */
public class GroupRequest {
    String group_id,user_id,email,contact,designation,website,about_group,created_on;
    int group_type;

    public GroupRequest(){

    }

    public GroupRequest(String about_group, String contact, String created_on, String designation, String email, String group_id, int group_type, String user_id, String website) {
        this.about_group = about_group;
        this.contact = contact;
        this.created_on = created_on;
        this.designation = designation;
        this.email = email;
        this.group_id = group_id;
        this.group_type = group_type;
        this.user_id = user_id;
        this.website = website;
    }


    public String getAbout_group() {
        return about_group;
    }

    public void setAbout_group(String about_group) {
        this.about_group = about_group;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
