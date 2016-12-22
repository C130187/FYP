package com.example.nubusaploy.snapmemory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jutarat on 6/13/2016.
 */
public class People extends RealmObject {
    @PrimaryKey
    private int people_id;

    private int image_id;
    private int user_id;
    private int people_list_id;
    private int other_people_list_id;
    private float people_x;
    private float people_y;

    public int getPeople_id() {return people_id;}

    public void setPeople_id(int people_id) {this.people_id = people_id;}

    public int getUser_id() {return user_id;}

    public void setUser_id(int user_id) {this.user_id = user_id;}

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public int getPeople_list_id() {
        return people_list_id;
    }

    public void setPeople_list_id(int people_list_id) {
        this.people_list_id = people_list_id;
    }

    public float getPeople_x() {
        return people_x;
    }

    public void setPeople_x(float people_x) {
        this.people_x = people_x;
    }

    public float getPeople_y() {
        return people_y;
    }

    public void setPeople_y(float people_y) {
        this.people_y = people_y;
    }

    public int getOther_people_list_id() {return other_people_list_id;}

    public void setOther_people_list_id(int other_people_list_id) {this.other_people_list_id = other_people_list_id;}
}