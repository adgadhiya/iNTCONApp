package com.myapp.unknown.iNTCON.NavigationView;

import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;

/**
 * Created by UNKNOWN on 2/1/2017.
 */
public class UserRequestDetailClass {

    String user_id, requested_on,userName,user_profile,field_id;
    String email = GlobalNames.getNONE();
    String field = GlobalNames.getNONE();

    public UserRequestDetailClass(){

    }

    public UserRequestDetailClass(String email, String field, String field_id, String requested_on, String user_id, String user_profile, String userName) {
        this.email = email;
        this.field = field;
        this.field_id = field_id;
        this.requested_on = requested_on;
        this.user_id = user_id;
        this.user_profile = user_profile;
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField_id() {
        return field_id;
    }

    public void setField_id(String field_id) {
        this.field_id = field_id;
    }

    public String getRequested_on() {
        return requested_on;
    }

    public void setRequested_on(String requested_on) {
        this.requested_on = requested_on;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
