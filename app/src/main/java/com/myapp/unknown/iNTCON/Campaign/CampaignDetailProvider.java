package com.myapp.unknown.iNTCON.Campaign;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by UNKNOWN on 9/23/2016.
 */

public class CampaignDetailProvider implements Parcelable {

    public String group_key,title,content,date,fees,prize,img_path,key,uid;
    public String venue,email,phone,site,registration_last_date,eventDate,accommodation,profile_path;
    public int chat_count,group_type;

    public CampaignDetailProvider(){
    }

    protected CampaignDetailProvider(Parcel in) {
        group_key = in.readString();
        title = in.readString();
        content = in.readString();
        date = in.readString();
        fees = in.readString();
        prize = in.readString();
        img_path = in.readString();
        key = in.readString();
        uid = in.readString();
        group_type = in.readInt();
        venue = in.readString();
        email = in.readString();
        phone = in.readString();
        site = in.readString();
        registration_last_date = in.readString();
        eventDate = in.readString();
        accommodation = in.readString();
        profile_path = in.readString();
        chat_count = in.readInt();
    }

    public static final Creator<CampaignDetailProvider> CREATOR = new Creator<CampaignDetailProvider>() {
        @Override
        public CampaignDetailProvider createFromParcel(Parcel in) {
            return new CampaignDetailProvider(in);
        }

        @Override
        public CampaignDetailProvider[] newArray(int size) {
            return new CampaignDetailProvider[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(group_key);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(date);
        parcel.writeString(fees);
        parcel.writeString(prize);
        parcel.writeString(img_path);
        parcel.writeString(key);
        parcel.writeString(uid);
        parcel.writeInt(group_type);
        parcel.writeString(venue);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(site);
        parcel.writeString(registration_last_date);
        parcel.writeString(eventDate);
        parcel.writeString(accommodation);
        parcel.writeString(profile_path);
        parcel.writeInt(chat_count);
    }

    public String getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Creator<CampaignDetailProvider> getCREATOR() {
        return CREATOR;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getRegistration_last_date() {
        return registration_last_date;
    }

    public void setRegistration_last_date(String registration_last_date) {
        this.registration_last_date = registration_last_date;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}

