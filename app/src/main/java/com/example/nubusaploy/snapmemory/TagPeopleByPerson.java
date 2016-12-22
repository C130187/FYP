package com.example.nubusaploy.snapmemory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nubusaploy.snapmemory.model.People_list;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by saowaga on 6/29/2016.
 */
public class TagPeopleByPerson extends AppCompatActivity {
    String people_x,people_y;
    ArrayList<String> people_list = new ArrayList<String>();
    ArrayAdapter<String> people_adapter;

    TextView show_people_chosen;

    String[] people_chosen;
    int int_people_count;
    String people_count;
    String people_name_chosen;
    int user_id;

    final int OTHERS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_people);

        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //Get data from the previous page - TagPeople Page
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            people_chosen = extras.getStringArray("people_chosen");
            people_count = extras.getString("people_count");
            int_people_count = Integer.parseInt(people_count);
            people_x = extras.getString("people_x");
            people_y = extras.getString("people_y");
            user_id = extras.getInt("user_id");
        }

        ListView peopleList = (ListView)findViewById(R.id.peopleList);
        Realm realmcheck = Realm.getInstance(this);
        show_people_chosen = (TextView) findViewById(R.id.people_chosen);
        RealmResults<People_list> people_list_database = realmcheck.where(People_list.class).findAll();

        for (int i =0;i<people_list_database.size();i++){
            people_list.add(people_list_database.get(i).getPeople_name());
        }

        people_adapter =  new ArrayAdapter<String>(TagPeopleByPerson.this, android.R.layout.simple_list_item_1, android.R.id.text1, people_list);
        peopleList.setAdapter(people_adapter);

        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView) view;
                people_name_chosen = textView.getText().toString();
                if(people_name_chosen.equals("Others")){
                    Intent i = new Intent(getApplicationContext(), OtherPeople.class);
                    i.putExtra("user_id",user_id);
                    startActivityForResult(i,OTHERS);
                }else{
                    people_chosen = addToPeople_Chosen(people_name_chosen, people_chosen, people_x, people_y, int_people_count);
                    int_people_count++;
                    people_count = ""+int_people_count;
                    Intent intent = new Intent();
                    intent.putExtra("people_chosen",people_chosen);
                    intent.putExtra("people_count",people_count);
                    setResult(RESULT_OK, intent);
                    finish();
                }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //Get data from OtherPeople page
            if(requestCode == OTHERS){
                people_name_chosen = data.getStringExtra("people_chosen");
                people_chosen = addToPeople_Chosen(people_name_chosen, people_chosen, people_x, people_y, int_people_count);
                int_people_count++;
                people_count = ""+int_people_count;
                Intent intent = new Intent();
                intent.putExtra("people_chosen",people_chosen);
                intent.putExtra("people_count",people_count);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    /**
     * Put all data into String[]
     * @param people_name_chosen_
     * @param people_chosen_
     * @param people_x_
     * @param people_y_
     * @param people_count_
     * @return people_chosen_ string[]
     */
    public String[] addToPeople_Chosen(String people_name_chosen_, String[] people_chosen_, String people_x_, String people_y_, int people_count_){
        if(people_chosen_.length == 0){
            people_chosen_ = new String[4];
            people_chosen_[0] = people_name_chosen_;
            people_chosen_[1] = people_x_;
            people_chosen_[2] = people_y_;
            people_chosen_[3] = "0";
        }else{
            String[] temp_people_chosen = people_chosen_;
            people_chosen_ = new String[(people_count_*4)+4];
            for (int i = 0; i < temp_people_chosen.length+1; i++){
                if(i>temp_people_chosen.length-1){

                    people_chosen_[i] = people_name_chosen_;
                    people_chosen_[i+1] = people_x_;
                    people_chosen_[i+2] = people_y_;
                    people_chosen_[i+3] = ""+(people_chosen_.length/4-1);

                }else{
                    people_chosen_[i] = temp_people_chosen[i];
                }

            }
        }

        return people_chosen_;
    }


}
