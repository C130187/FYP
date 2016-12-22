package com.example.nubusaploy.snapmemory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jutarat on 6/13/2016.
 */
public class Mood_list extends RealmObject {
    @PrimaryKey
    private int mood_list_id;

    private String  mood_name;

    // Standard getters & setters generated by your IDE…


    public int getMood_list_id() {return mood_list_id;}

    public void setMood_list_id(int mood_list_id) {this.mood_list_id = mood_list_id;}

    public String getMood_name() {
        return mood_name;
    }

    public void   setMood_name(String mood_name) {
        this.mood_name = mood_name;
    }

}
