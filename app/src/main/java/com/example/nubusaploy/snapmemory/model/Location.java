package com.example.nubusaploy.snapmemory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jutarat on 6/13/2016.
 */
public class Location extends RealmObject {
    @PrimaryKey
    private int location_id;

    private int image_id;
    private int user_id;
    private String location_name;
    private String location_address;
    private float latitude;
    private float longiude;

    public void setLocation_id(int location_id) {this.location_id = location_id;}

    public int getLocation_id() {return location_id;}

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getImage_id() {
        return image_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLocation_address() {
        return location_address;
    }

    public void setLocation_address(String address) {
        this.location_address = address;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongiude() {
        return longiude;
    }

    public void setLongiude(float longiude) {
        this.longiude = longiude;
    }
}