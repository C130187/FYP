package com.example.nubusaploy.snapmemory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jutarat on 6/13/2016.
 */
public class Activity_list extends RealmObject {
    @PrimaryKey
    private int activity_list_id;

    private String  activity_name;

    public int getActivity_list_id() {
        return activity_list_id;
    }

    public void setActivity_list_id(int activity_list_id) {
        this.activity_list_id = activity_list_id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }
}
