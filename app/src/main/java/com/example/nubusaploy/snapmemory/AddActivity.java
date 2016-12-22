package com.example.nubusaploy.snapmemory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nubusaploy.snapmemory.model.Activity_list;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by saowaga on 6/29/2016.
 */
public class AddActivity extends AppCompatActivity {


    // Items entered by the user is stored in this ArrayList variable
    ArrayList<String> activity_list = new ArrayList<String>();
    // Declaring an ArrayAdapter to set items to ListView
    ArrayAdapter<String> activity_adapter;

    TextView show_activity_chosen;
    String activity_chosen;
    int user_id;

    final int OTHERS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //Get data from previous page
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            user_id = extras.getInt("user_id");
        }

        ListView activityList = (ListView)findViewById(R.id.activityList);

        // Defining the ArrayAdapter to set items to Spinner Widget
        Realm realmcheck = Realm.getInstance(this);
        //get activity list from database
        show_activity_chosen = (TextView) findViewById(R.id.activity_chosen);
        RealmResults<Activity_list> activity_list_database = realmcheck.where(Activity_list.class).findAll();

        for (int i =0;i<activity_list_database.size();i++){
            activity_list.add(activity_list_database.get(i).getActivity_name());
        }

        activity_adapter =  new ArrayAdapter<String>(AddActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, activity_list);
        activityList.setAdapter(activity_adapter);

        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                activity_chosen = textView.getText().toString();
                //String message = "You clicked # "+position + ", which is string: " + textView.getText().toString();
                // Toast.makeText(AddActivity.this, message, Toast.LENGTH_LONG).show();

                //If user choose Others category
                if(activity_chosen.equals("Others")){
                    Intent i = new Intent(getApplicationContext(), OtherActivity.class);
                    //Send data to OtherActivity Page
                    i.putExtra("user_id",user_id);
                    startActivityForResult(i,OTHERS);
                }else{
                    sendResultBack();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //Get information from OtherActivity Page
            if(requestCode == OTHERS){
                activity_chosen = data.getStringExtra("activity_chosen");
                sendResultBack();
            }
        }
    }

    /**
     * Send the Result Back to Tag Page
     */
    public void sendResultBack(){
        Intent intent = new Intent();
        intent.putExtra("activity_chosen",activity_chosen);
        setResult(RESULT_OK, intent);
        finish();
    }




}
