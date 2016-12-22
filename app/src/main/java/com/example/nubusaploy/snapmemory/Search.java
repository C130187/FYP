package com.example.nubusaploy.snapmemory;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by saowaga on 7/13/2016.
 */
public class Search extends AppCompatActivity {
    EditText search_editext;
    Button search_button;
    GridView show_image;
    ArrayList<File> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_photo);
        search_editext = (EditText)findViewById(R.id.searchPhoto);
        search_button = (Button)findViewById(R.id.button);
        //list = imageReader(Environment.getExternalStorageDirectory());

        show_image = (GridView)findViewById(R.id.gridView);
        //show_image.setAdapter(new GridAdapter());

    }
    /*class GridAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.)

            return null;
        }
    }
    ArrayList<File> imageReader(File root){
        ArrayList<File> a = new ArrayList<>();
        File[] files = root.listFiles();
        for(int i = 0;i<files.length;i++){
            if(files[i].isDirectory()){
                a.addAll(imageReader(files[i]));
            }else{
                if(files[i].getName().endsWith(".jpg")){
                    a.add(files[i]);
                }
            }
        }
        return a;
    }
    public void checkTag(){

        /*ArrayList<String> child = new ArrayList();
        child.add("+");
        ArrayList<Image> childDatabases = new ArrayList<>();

        Realm realm = Realm.getInstance(this);
        // Transactions give you easy thread-safety
//        realm.beginTransaction();
        int image_id = Integer.parseInt(search_editext.toString());
        RealmResults<Image> query = realm.where(Image.class)
                .equalTo("user_id",image_id).findAll();


        //String result = query.first().getPicPath();
        for(int i = 0;i<query.size();i++){
            childDatabases.add(query.get(i));
        }
        addToList(childDatabases);
    }*/
}
