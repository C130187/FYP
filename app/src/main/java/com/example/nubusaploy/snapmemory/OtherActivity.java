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

import com.example.nubusaploy.snapmemory.model.Other_activity;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by MMink on 7/12/2016.
 */
public class OtherActivity extends AppCompatActivity {

    //For Activity
    // Items entered by the user is stored in this ArrayList variable
    ArrayList<String> activity_list = new ArrayList<String>();
    // Declaring an ArrayAdapter to set items to ListView
    ArrayAdapter<String> activity_adapter;

    TextView show_activity_chosen;
    String activity_chosen;


    TextView textView;
    String newactivity;
    ListView activityList;
    int user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity_other);

        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //Get data from previous page (AddActivity Page)
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            user_id = extras.getInt("user_id");
        }


        activityList = (ListView)findViewById(R.id.activityList);
        final EditText inputSearch = (EditText) findViewById(R.id.searchActivity);

        // Defining the ArrayAdapter to set items to Spinner Widget
        Realm realmcheck = Realm.getInstance(this);
        //get activity list from database
        show_activity_chosen = (TextView) findViewById(R.id.activity_chosen);
        RealmResults<Other_activity> activity_list_database = realmcheck.where(Other_activity.class).findAll();

        for (int i =0;i<activity_list_database.size();i++){
            activity_list.add(activity_list_database.get(i).getOther_activity_name());
        }

        activity_adapter =  new ArrayAdapter<String>(OtherActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, activity_list);
        activityList.setAdapter(activity_adapter);

        //Searching Function
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user changed text
                OtherActivity.this.activity_adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        Button add = (Button) findViewById(R.id.addOtherActivityButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newactivity = inputSearch.getText().toString();
                int new_other_activity_list_id;
                Realm realm = Realm.getInstance(OtherActivity.this);
                RealmResults<Other_activity> check2 = realm.where(Other_activity.class).equalTo("other_activity_name",newactivity).findAll();
                if(check2.size() == 0) {
                    RealmResults<Other_activity> check3 = realm.where(Other_activity.class).findAllSorted("other_activity_id",RealmResults.SORT_ORDER_DESCENDING);
                    if( check3.size()>0){
                        int test = check3.get(0).getOther_activity_id();
                        new_other_activity_list_id = test + 1;
                    }else{
                        new_other_activity_list_id = 1;
                    }
                    //save new activity to database
                    Realm realm5 = Realm.getInstance(OtherActivity.this);
                    realm5.beginTransaction();
                    Other_activity otherpeople = realm5.createObject(Other_activity.class);
                    otherpeople.setOther_activity_id(new_other_activity_list_id);
                    otherpeople.setOther_activity_name(newactivity);
                    otherpeople.setUser_id(user_id);
                    realm5.commitTransaction();
                }else{
                    new_other_activity_list_id = check2.get(0).getOther_activity_id();
                }
                inputSearch.setText("");
                // Defining the ArrayAdapter to set items to Spinner Widget
                Realm realmcheck = Realm.getInstance(OtherActivity.this);
                //get people list from database
                show_activity_chosen = (TextView) findViewById(R.id.people_chosen);
                RealmResults<Other_activity> people_list_database = realmcheck.where(Other_activity.class).findAll();
                activity_list.clear();
                for (int i =0;i<people_list_database.size();i++){
                    activity_list.add(people_list_database.get(i).getOther_activity_name());
                }

                activity_adapter =  new ArrayAdapter<String>(OtherActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, activity_list);
                activityList.setAdapter(activity_adapter);
            }
        });


        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView = (TextView) view;
                activity_chosen = textView.getText().toString();
                sendResultBack();
            }
        });

        ImageButton closebtn = (ImageButton) findViewById(R.id.closeActivity);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Send result back to Addactivity Page
     */
    public void sendResultBack(){
        Intent intent = new Intent();
        intent.putExtra("activity_chosen",activity_chosen);
        setResult(RESULT_OK, intent);
        finish();
    }
}
