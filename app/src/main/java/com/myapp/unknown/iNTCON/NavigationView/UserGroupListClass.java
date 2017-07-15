package com.myapp.unknown.iNTCON.NavigationView;

import com.myapp.unknown.iNTCON.OtherClasses.GlobalNames;

/**
 * Created by UNKNOWN on 2/2/2017.
 */
public class UserGroupListClass {

    String group_id;
    String field_key = GlobalNames.getDummyNode();
    String year = GlobalNames.getDummyNode();

    public UserGroupListClass(){

    }

    public UserGroupListClass(String field_key, String group_id, String year) {
        this.field_key = field_key;
        this.group_id = group_id;
        this.year = year;
    }


    public String getField_key() {
        return field_key;
    }

    public void setField_key(String field_key) {
        this.field_key = field_key;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
