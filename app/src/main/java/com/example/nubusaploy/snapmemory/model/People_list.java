package com.example.nubusaploy.snapmemory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jutarat on 6/13/2016.
 */
public class People_list extends RealmObject {
    @PrimaryKey
    private int people_list_id;

    private String  people_name;

    public int getPeople_list_id() {return people_list_id;}

    public void setPeople_list_id(int people_list_id) {this.people_list_id = people_list_id;}

    public String getPeople_name() {
        return people_name;
    }

    public void setPeople_name(String people_name) {
        this.people_name = people_name;
    }
}
