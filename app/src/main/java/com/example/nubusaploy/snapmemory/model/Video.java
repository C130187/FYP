package com.example.nubusaploy.snapmemory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HP on 12-09-2016.
 */
public class Video extends RealmObject {
    @PrimaryKey
    private int video_id;

    private int user_id;
    private String  video_name;
    private String video_path;
    private String time;
    private String date;
    private String date_formatted;
    private String activity_name;
    private int activity_list_id;
    private int other_activity_id;
    private int mood_list_id;
    private String mood_name;
    private String people_name;
    private String description;
    private int horizontal_size_vid;
    private int vertical_size_vid;
    private String latitude;
    private String longitude;
    private String location;
    private boolean upload_status;
    private boolean modified_after_upload;


    public int getVideo_id() {return video_id;}

    public void setVideo_id(int video_id) {this.video_id = video_id;}

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {this.video_name = video_name;}

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
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

    public String getPeople_name() {
        return people_name;
    }

    public void setPeople_name(String people_name) {
        this.people_name = people_name;
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

    public void setHorizontal_size_vid(int horizontal_size_vid) {this.horizontal_size_vid = horizontal_size_vid;}

    public int getHorizontal_size_vid() {
        return horizontal_size_vid;
    }

    public void setVertical_size_vid(int vertical_size_vid) {this.vertical_size_vid = vertical_size_vid;}

    public int getVertical_size_vid() {
        return vertical_size_vid;
    }

    public boolean isUpload_status() {
        return upload_status;
    }

    public void setUpload_status(boolean upload_status) {
        this.upload_status = upload_status;
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

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }
    public String getMood_name() {
        return mood_name;
    }

    public void setMood_name(String mood_name) {
        this.mood_name = mood_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getLocation() {
        return location;
    }

    public String getDate_formatted() {
        return date_formatted;
    }

    public void setDate_formatted(String date_formatted) {
        this.date_formatted = date_formatted;
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



