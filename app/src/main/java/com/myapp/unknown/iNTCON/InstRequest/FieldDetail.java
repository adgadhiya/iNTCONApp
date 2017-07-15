package com.myapp.unknown.iNTCON.InstRequest;

/**
 * Created by UNKNOWN on 12/4/2016.
 */
public class FieldDetail {

    public String fieldName,key;

    public FieldDetail(){

    }

    public FieldDetail(String fieldName) {
        this.fieldName = fieldName;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}

