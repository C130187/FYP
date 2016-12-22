package com.example.nubusaploy.snapmemory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nubusaploy.snapmemory.model.Other_people;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by MMink on 7/12/2016.
 */
public class OtherPeople extends AppCompatActivity{

    ArrayList<String> people_list = new ArrayList<String>();
    ArrayAdapter<String> people_adapter;

    TextView show_people_chosen;
    String people_chosen;
    int user_id;
    TextView textView;
    String newpeople;
    ListView peopleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_people_other);

        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //Get data from the previous page
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            user_id = extras.getInt("user_id");
        }

        peopleList = (ListView)findViewById(R.id.peopleList);
        final EditText inputSearch = (EditText) findViewById(R.id.searchPeople);

        // Defining the ArrayAdapter to set items to Spinner Widget
        Realm realmcheck = Realm.getInstance(this);
        //get people list from database
        show_people_chosen = (TextView) findViewById(R.id.people_chosen);
        RealmResults<Other_people> people_list_database = realmcheck.where(Other_people.class).findAll();

        for (int i =0;i<people_list_database.size();i++){
            people_list.add(people_list_database.get(i).getOther_people_name());
        }

        people_adapter =  new ArrayAdapter<String>(OtherPeople.this, android.R.layout.simple_list_item_1, android.R.id.text1, people_list);
        peopleList.setAdapter(people_adapter);

        //When user search
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user changed text
                OtherPeople.this.people_adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        Button add = (Button) findViewById(R.id.addOtherPeopleButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newpeople = inputSearch.getText().toString();
                int new_other_people_list_id;
                Realm realm = Realm.getInstance(OtherPeople.this);
                RealmResults<Other_people> check2 = realm.where(Other_people.class).equalTo("other_people_name",newpeople).findAll();
                if(check2.size() == 0) {
                    RealmResults<Other_people> check3 = realm.where(Other_people.class).findAllSorted("other_people_id",RealmResults.SORT_ORDER_DESCENDING);
                    if( check3.size()>0){
                        int test = check3.get(0).getOther_people_id();
                        new_other_people_list_id = test + 1;
                    }else{
                        new_other_people_list_id = 1;
                    }
                    //save new people to database
                    Realm realm5 = Realm.getInstance(OtherPeople.this);
                    realm5.beginTransaction();
                    Other_people otherpeople = realm5.createObject(Other_people.class);
                    otherpeople.setOther_people_id(new_other_people_list_id);
                    otherpeople.setOther_people_name(newpeople);
                    otherpeople.setUser_id(user_id);
                    realm5.commitTransaction();
                }else{
                    new_other_people_list_id = check2.get(0).getOther_people_id();
                }
                inputSearch.setText("");
                // Defining the ArrayAdapter to set items to Spinner Widget
                Realm realmcheck = Realm.getInstance(OtherPeople.this);
                //get people list from database
                show_people_chosen = (TextView) findViewById(R.id.people_chosen);
                RealmResults<Other_people> people_list_database = realmcheck.where(Other_people.class).findAll();
                people_list.clear();
                for (int i =0;i<people_list_database.size();i++){
                    people_list.add(people_list_database.get(i).getOther_people_name());
                }

                people_adapter =  new ArrayAdapter<String>(OtherPeople.this, android.R.layout.simple_list_item_1, android.R.id.text1, people_list);
                peopleList.setAdapter(people_adapter);
            }
        });

        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView = (TextView) view;
                people_chosen = textView.getText().toString();
                sendResultBack();
            }
        });

        ImageButton closebtn = (ImageButton) findViewById(R.id.closePeople2);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Send result back to the previous page - TagPeopleByPerson
     */
    public void sendResultBack(){
        Intent intent = new Intent();
        intent.putExtra("people_chosen",people_chosen);
        setResult(RESULT_OK, intent);
        finish();
    }
}
