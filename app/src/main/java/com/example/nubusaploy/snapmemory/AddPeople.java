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
import android.widget.Toast;

import com.example.nubusaploy.snapmemory.model.People_list;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class AddPeople extends AppCompatActivity {

    ArrayList<String> people_list = new ArrayList<String>();
    ArrayList<String> selectedItems = new ArrayList<String>();
    String people_chosen;
    EditText otherPeople;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ArrayAdapter<String> people_adapter;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);


        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        final ListView peopleList = (ListView) findViewById(R.id.peopleList);
        otherPeople = (EditText) findViewById(R.id.OtherPeople);

        // Defining the ArrayAdapter to set items to Spinner Widget
        Realm realmcheck = Realm.getInstance(this);
        //get people list from database

        RealmResults<People_list> people_list_database = realmcheck.where(People_list.class).findAll();

        for (int i =0;i<people_list_database.size();i++){
            people_list.add(people_list_database.get(i).getPeople_name());
        }

        people_adapter =  new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.txt_lan,people_list);
        peopleList.setAdapter(people_adapter);

        peopleList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();

                if (selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem);
                } else
                    selectedItems.add(selectedItem);
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


    public void showSelectedItems(View view) {
        String items = "";
        for (String item : selectedItems) {
        items+=item+',';
            if(item.equals("Others") && otherPeople.getText()!=null){
                items = items.replace("Others", "");
                items += otherPeople.getText().toString() +',';
            }
        }
        items=items.substring(0,items.length()-1);
        //if(otherPeople.getText()!= null && items.contains("Others")) {
        //    items += ':' + otherPeople.getText().toString();
        //}
        people_chosen = items;
        Toast.makeText(this,"You have selected \n"+items, Toast.LENGTH_LONG).show();
        sendResultBack();


    }


    /**
     * Send the Result Back to Tag Page
     */
    public void sendResultBack(){
        Intent intent = new Intent();
        intent.putExtra("people_chosen",people_chosen);
        setResult(RESULT_OK, intent);
        finish();
    }

}