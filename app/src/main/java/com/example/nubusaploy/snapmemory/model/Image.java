package com.example.nubusaploy.snapmemory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jutarat on 6/13/2016.
 */
public class Image extends RealmObject {
    @PrimaryKey
    private int image_id;

    private int user_id;
    private String  image_name;
    private String image_path;
    private String time;
    private String date;

    private String date_formatted;
    private int activity_list_id;
    private int other_activity_id;
    private int mood_list_id;
    private String mood_name;
    private String people_name;
    private String activity_name;
    private int people_count;
    private String description;
    private int horizontal_size_pic;
    private int vertical_size_pic;
    private String latitude;
    private String longitude;
    private String location;
    private boolean upload_status;
    private boolean modified_after_upload;


    public int getImage_id() {return image_id;}

    public void setImage_id(int image_id) {this.image_id = image_id;}

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {this.image_name = image_name;}

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getActivity_list_id() {
        return activity_list_id;
    }

    public void setActivity_list_id(int activity_list_id) {
        this.activity_list_id = activity_list_id;
    }

    public int getOther_activity_id() {
        return other_activity_id;
    }

    public void setOther_activity_id(int other_activity_id) {
        this.other_activity_id = other_activity_id;
    }

    public int getMood_list_id() {
        return mood_list_id;
    }

    public void setMood_list_id(int mood_list_id) {
        this.mood_list_id = mood_list_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setHorizontal_size_pic(int horizontal_size_pic) {this.horizontal_size_pic = horizontal_size_pic;}

    public int getHorizontal_size_pic() {
        return horizontal_size_pic;
    }

    public void setVertical_size_pic(int vertical_size_pic) {this.vertical_size_pic = vertical_size_pic;}

    public int getVertical_size_pic() {
        return vertical_size_pic;
    }

    public boolean isUpload_status() {
        return upload_status;
    }

    public void setUpload_status(boolean upload_status) {
        this.upload_status = upload_status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_formatted() {
        return date_formatted;
    }

    public void setDate_formatted(String date_formatted) {
        this.date_formatted = date_formatted;
    }

    public String getMood_name() {
        return mood_name;
    }

    public void setMood_name(String mood_name) {
        this.mood_name = mood_name;
    }

    public String getPeople_name() {
        return people_name;
    }

    public void setPeople_name(String people_name) {
        this.people_name = people_name;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public int getPeople_count() {
        return people_count;
    }

    public void setPeople_count(int people_count) {
        this.people_count = people_count;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isModified_after_upload() {
        return modified_after_upload;
    }

    public void setModified_after_upload(boolean modified_after_upload) {
        this.modified_after_upload = modified_after_upload;
    }

}
