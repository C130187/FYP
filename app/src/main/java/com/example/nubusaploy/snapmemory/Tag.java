package com.example.nubusaploy.snapmemory;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nubusaploy.snapmemory.model.Activity_list;
import com.example.nubusaploy.snapmemory.model.Image;
import com.example.nubusaploy.snapmemory.model.Location;
import com.example.nubusaploy.snapmemory.model.Mood_list;
import com.example.nubusaploy.snapmemory.model.Other_activity;
import com.example.nubusaploy.snapmemory.model.Other_people;
import com.example.nubusaploy.snapmemory.model.People;
import com.example.nubusaploy.snapmemory.model.People_list;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by saowaga on 6/10/2016.
 */
public class Tag extends AppCompatActivity {


    //Open Image
    ImageView imageView;
    String imagePath;
    Uri imageUri;
    String imgDecodableString;
    String requestCode;
    ExifInterface exif;
    //identity
    int user_id;
    String image_name;

    //image's date and time
    String dateTime;
    String date, time;
    String date_formatted = "";
    ExifInterface intf;
    static final int DIALOG_DATE_ID = 0;
    static final int DIALOG_TIME_ID = 1;

    //image's date
    Button btnChangeDate;
    int year_x, month_x, day_x;
    String month;

    //image's time
    Button btnChangeTime;
    int hour_x, minute_x;
    String stringHour,stringMinute;


    //For location
    Button btnAddLocation;
    TextView t_location;
    private boolean valid = false;
    Float latitude = null, longitude = null;
    String attrlatitude, attrlatitude_ref, attrlongitude, attrlongitude_ref;
    Geocoder geocoder;
    int PLACE_PICKER_REQUEST = 1;
    Boolean firstHaveLatLng = false;

    //Check Google Play Service Version
    int version;

    //For People
    Button btnTagPeople;
    TextView t_people;
    int PEOPLE = 2;
    String people_count = "0";
    String[] people_chosen;
    int int_people_count;
    ArrayList<String> people = new ArrayList<String>();
    ArrayList<String> x;
    ArrayList<String> y;
    String people_name;

    //For Activity
    Button btnAddActivity;
    int ACTIVITY = 3;
    TextView t_activity;

    //For Mood
    Button btnAddMood;
    int MOOD = 4;
    TextView t_mood;

    //For save all user chosen
    String imagePath_chosen, locationName_chosen, locationAddress_chosen,
            latitude_chosen, longitude_chosen, mood_chosen, image_datetime,
            activity_chosen;
    String caption="";
    String horizontal_size_pic = "0";
    String vertical_size_pic = "0";
    int new_other_people_list_id = 0;
    int new_other_activity_list_id = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag);

        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        imageView = (ImageView) findViewById(R.id.shownPhoto);

        //Get data from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imagePath = extras.getString("imagePath"); // getString(valuename) -- valuename same as the one it use for send
            imageUri = Uri.parse(extras.getString("imageUri"));
            requestCode = extras.getString("requestCode");
            imagePath_chosen = imagePath;
            if(extras.getString("imageName")!=null){
                image_name = extras.getString("imageName");
            }
            user_id = 0;
        }

        try {
            version = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //For Image
        openImage();
        getImageWidthLength(imagePath);
        //Click on "image" Then open new page to see large image
        ImageButton preview = (ImageButton) findViewById(R.id.shownPhoto);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Preview.class);
                //Send data to another activity putExtra(newvaluename, value that want to send)
                i.putExtra("imagePath",imagePath);
                i.putExtra("requestCode","CAMERA");
                i.putExtra("imageUri", imageUri.toString());
                startActivity(i);
            }
        });


        //For date and time
        btnChangeDate = (Button) findViewById(R.id.addDate);
        btnChangeTime = (Button) findViewById(R.id.addTime);
        //Get Date and Time From meta-data of image
        getDateTime(imagePath);
        if (dateTime == null) {
            getDateTimeLastModified(imagePath);
        }


        btnChangeDate.setText(date);
        btnChangeTime.setText(time);
        image_datetime = dateTime + time;

        //Show current Day, Month, and Year in Dialog
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        showDatePickerDialog();

        //Show Time in Dialog
        showTimePickerDialog();

        //For location
        btnAddLocation = (Button)findViewById(R.id.addLocation);

        location();


        Realm realmtest = Realm.getInstance(this);
        RealmResults<Image> check1 =  realmtest.where(Image.class)
                .findAll();
        if(check1.size()!=0) {
            Image results2 =
                    realmtest.where(Image.class)
                            .contains("video_path", imagePath)
                            .findFirst();
            if (results2 != null) {
                //String existing_people = results2.getPeople_name();
                String existing_time = results2.getTime();
                String existing_date = results2.getDate();
                String existing_activity = results2.getActivity_name();
                String existing_mood = results2.getMood_name();
                String existing_location = results2.getLocation();
                activity_chosen = existing_activity;
                mood_chosen = existing_mood;
                locationName_chosen = existing_location;
                t_location.setText(existing_location);

                if (!activity_chosen.equals("")) {
                    btnAddActivity.setText("Change Activity");
                    t_activity.setText(activity_chosen);
                } else {
                    btnAddActivity.setText("No Activity Added");
                    t_activity.setText(activity_chosen);

                }
                if (!mood_chosen.equals("")) {
                    btnAddMood.setText("Change Mood");
                    t_mood.setText(mood_chosen);
                } else {
                    btnAddMood.setText("No Mood Added");
                    t_mood.setText(mood_chosen);

                }
                /* come back to this later

                if(!people_chosen.equals("")){
                    btnTagPeople.setText("Change People");
                    //t_people.setText(existing_people);
                    t_people.setText(people_chosen);
                }
                else
                {
                    btnTagPeople.setText("No People Added");
                    //t_people.setText(existing_people);
                    t_people.setText(people_chosen);
                }
                */
                btnChangeDate.setText(existing_date);
                btnChangeTime.setText(existing_time);

            }
            //For Tag People
            people_chosen = new String[int_people_count * 4];
            t_people = (TextView) findViewById(R.id.people);
            btnTagPeople = (Button) findViewById(R.id.tagPeople);
            btnTagPeople.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), TagPeople.class);
                    //For Image
                    i.putExtra("imagePath", imagePath);
                    i.putExtra("requestCode", "CAMERA");
                    i.putExtra("imageUri", imageUri.toString());
                    //For People
                    i.putExtra("people_chosen", people_chosen); //String[]
                    i.putExtra("people_count", people_count);
                    i.putExtra("user_id", user_id);
                    startActivityForResult(i, PEOPLE);
                }
            })


            //For Activity
            t_activity = (TextView) findViewById(R.id.activity);
            btnAddActivity = (Button) findViewById(R.id.addActivity);
            btnAddActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), AddActivity.class);
                    i.putExtra("user_id", user_id);
                    startActivityForResult(i, ACTIVITY);
                }
            });


            //For Mood
            t_mood = (TextView) findViewById(R.id.mood);
            btnAddMood = (Button) findViewById(R.id.addMood);
            btnAddMood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), AddMood.class);
                    startActivityForResult(i, MOOD);
                }
            });

            ImageButton save = (ImageButton) findViewById(R.id.save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText text = (EditText) findViewById(R.id.textCaption);
                    caption = "" + text.getText();
                    checkAllData();
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PLACE_PICKER_REQUEST) {
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                locationName_chosen = ""+place.getName();
                locationAddress_chosen = ""+place.getAddress();
                btnAddLocation.setText("Update Location");

                t_location.setText(locationName_chosen+"\n"+locationAddress_chosen);
                if(!firstHaveLatLng){
                    String latLng = ""+place.getLatLng();
                    latLng = latLng.substring(10,latLng.length()-1);
                    String[] latLngArr = latLng.split(",", 2);
                    latitude_chosen = latLngArr[0];
                    longitude_chosen = latLngArr[1];
                }

            }

        }else if (requestCode == PEOPLE) {
            if(resultCode == RESULT_OK){
                people_chosen = data.getStringArrayExtra("people_chosen");
                people_count = data.getStringExtra("people_count");
                int_people_count = Integer.parseInt(people_count);
                splitPeopleChosenToPeopleArray(people_chosen);
                if(people_chosen != null){
                    btnTagPeople.setText("Add more people");
                }
                int temp = 0;
                for (int i = 0; i < int_people_count; i++){
                    if(people_chosen[(i*4)+3].equals("-1")){
                        temp++;
                    }
                }
                int_people_count = int_people_count - temp;
                if(int_people_count == 0){
                    btnTagPeople.setText("Tag People");
                }else{
                    String[] chosen_people = new String[int_people_count];
                    chosen_people = people.toArray(new String[0]);
                    String[] newpeople = new String[int_people_count];
                    List<String> newpeoplelist = new ArrayList<String>();
                    int count;
                    for (int i=0; i < chosen_people.length; i++){
                        count = 0;
                        for(int j=0; j < chosen_people.length; j++){
                            if(chosen_people[i].equals(newpeople[j])){
                                count++;
                            }
                        }
                        if(count == 0){
                            newpeople[i] = chosen_people[i];
                            newpeoplelist.add(chosen_people[i]);
                        }

                    }
                    t_people.setText(newpeoplelist.toString());
                }


            }
        }else if (requestCode == ACTIVITY) {
            if(resultCode == RESULT_OK){
                activity_chosen=data.getStringExtra("activity_chosen");
                if(activity_chosen != null){
                    btnAddActivity.setText("Change Activity");
                }
                t_activity.setText(activity_chosen);

            }
        }else if (requestCode == MOOD) {
            if(resultCode == RESULT_OK){
                mood_chosen=data.getStringExtra("mood_chosen");
                String x = mood_chosen;
                if(mood_chosen != null){
                    btnAddMood.setText("Change Mood");
                }
                t_mood.setText(mood_chosen);

            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.tag_photo,menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }


    /**
     * Open the chosen image
     */
    public void openImage() {

        try {
            //When image is taken by camera
            if (requestCode.equals("CAMERA")) {
                //imageView.setImageDrawable(Drawable.createFromPath(path));
                //Uri imageUri = data.getData();

                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap imageData = BitmapFactory.decodeFile(imagePath, options);
                Matrix matrix = new Matrix();
                int rotateImage = getCameraPhotoOrientation(this, imageUri, imagePath);
                if (rotateImage != 0) {
                    matrix.postRotate(rotateImage);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), matrix, true);
                    Bitmap resizedBitmap = getResizedBitmap(rotatedBitmap, 4096);
                    imageView.setImageBitmap(resizedBitmap);
                    imageData.recycle();

                } else {
                    matrix.postRotate(0);
                    Bitmap createdBitmap = Bitmap.createBitmap(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), matrix, true);
                    Bitmap resizedBitmap = getResizedBitmap(createdBitmap, 4096);
                    //Set the Image in ImageView after decoding the String
                    //imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                    imageView.setImageBitmap(resizedBitmap);
                    imageData.recycle();

                }


            }
            //When an Image is picked
            else if (requestCode.equals("GALLERY")) {

                //Get the image from data
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                //Get the cursor
                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                //Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap imageData = BitmapFactory.decodeFile(imagePath, options);
                Matrix matrix = new Matrix();
                int rotateImage = getCameraPhotoOrientation(this, imageUri, imgDecodableString);
                if (rotateImage != 0) {

                    matrix.postRotate(rotateImage);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), matrix, true);
                    Bitmap resizedBitmap = getResizedBitmap(rotatedBitmap, 4096);
                    imageView.setImageBitmap(resizedBitmap);
                    imageData.recycle();

                } else {
                    matrix.postRotate(0);
                    Bitmap createdBitmap = Bitmap.createBitmap(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), matrix, true);
                    Bitmap resizedBitmap = getResizedBitmap(createdBitmap, 4096);
                    //Set the Image in ImageView after decoding the String
                    imageView.setImageBitmap(resizedBitmap);

                }


            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Check the orientation of photo
     * Then find the rotate angle
     *
     * @param context
     * @param imageUri
     * @param imagePath
     * @return rotat angle
     */
    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    /**
     *  Resize Bitmap
     * @param image
     * @param maxSize
     * @return resizedBitmap
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * Find the width and length of the image
     * @param imagePath
     */
    public void getImageWidthLength(String imagePath){
        intf = null;
        try {
            intf = new ExifInterface(imagePath);
            horizontal_size_pic = intf.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            vertical_size_pic = intf.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the date and time from meta-data of image
     * @param iamgePath
     */
    public void getDateTime(String iamgePath) {
        intf = null;
        try {
            intf = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (intf != null) {
            //YYYY:MM:DD HH:MM:SS
            dateTime = intf.getAttribute(ExifInterface.TAG_DATETIME);

            if (dateTime != null) {
                String[] stringDateTime = dateTime.split(" ", 2);
                String[] stringDate = stringDateTime[0].split(":", 3);

                String stringYear = stringDate[0];
                String stringMonth = stringDate[1];
                switch (stringMonth) {
                    case "01":
                        stringMonth = "Jan";
                        break;
                    case "02":
                        stringMonth = "Feb";
                        break;
                    case "03":
                        stringMonth = "Mar";
                        break;
                    case "04":
                        stringMonth = "Apr";
                        break;
                    case "05":
                        stringMonth = "May";
                        break;
                    case "06":
                        stringMonth = "Jun";
                        break;
                    case "07":
                        stringMonth = "Jul";
                        break;
                    case "08":
                        stringMonth = "Aug";
                        break;
                    case "09":
                        stringMonth = "Sep";
                        break;
                    case "10":
                        stringMonth = "Oct";
                        break;
                    case "11":
                        stringMonth = "Nov";
                        break;
                    case "12":
                        stringMonth = "Dec";
                        break;
                }

                String stringDay = stringDate[2];
                //DD MMM YYYY
                date = stringDay + " " + stringMonth + " " + stringYear;
                //HH:MM:SS
                //time = stringDateTime[1];
                //HH:MM
                String[] stringTime = stringDateTime[1].split(":", 3);
                time = stringTime[0]+":"+stringTime[1];
            }
        }

    }

    /**
     * Get the last modified date and time of image in case of no meta-data date and time
     * @param imagePath
     */
    public void getDateTimeLastModified(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) //Extra check, Just to validate the given path
        {
            //Mon MMM DD HH:MM:SS GMT+08.00 YYYY
            Date lastModDate = new Date(file.lastModified());
            String stringLastModDate = lastModDate + "";
            String[] modDateTime = stringLastModDate.split(" ", 6);

            //DD MMM YYYY
            date = modDateTime[2] + " " + modDateTime[1] + " " + modDateTime[5];
            switch (modDateTime[1]) {
                case "Jan":
                    modDateTime[1] = "01";
                    break;
                case "Feb":
                    modDateTime[1] = "02";
                    break;
                case "Mar":
                    modDateTime[1] = "03";
                    break;
                case "Apr":
                    modDateTime[1] = "04";
                    break;
                case "May":
                    modDateTime[1] = "05";
                    break;
                case "Jun":
                    modDateTime[1] = "06";
                    break;
                case "Jul":
                    modDateTime[1] = "07";
                    break;
                case "Aug":
                    modDateTime[1] = "08";
                    break;
                case "Sep":
                    modDateTime[1] = "09";
                    break;
                case "Oct":
                    modDateTime[1] = "10";
                    break;
                case "Nov":
                    modDateTime[1] = "11";
                    break;
                case "Dec":
                    modDateTime[1] = "12";
                    break;
            }
            date_formatted = modDateTime[2] + "-" + modDateTime[1] + "-" + modDateTime[5];
            //HH:MM:SS
            //time = modDateTime[3];
            //HH:MM
            String[] stringTime = modDateTime[3].split(":", 3);
            time = stringTime[0]+":"+stringTime[1];


        }
    }

    /**
     * Show the DatePicker for user to choose the new date
     */
    public void showDatePickerDialog(){
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE_ID);
            }
        });
    }

    /**
     * Show the TimePicker for user to choose the new time
     */
    public void showTimePickerDialog(){
        btnChangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_DATE_ID)
            return new DatePickerDialog(this, datePickerListener, year_x, month_x, day_x);
        if (id == DIALOG_TIME_ID)
            return new TimePickerDialog(this, timePickerListener, hour_x, minute_x, true);
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            changeNumMonthToWord(month_x);
            day_x = dayOfMonth;
            //DD MMM YYYY
            date = day_x + " " + month + " " + year_x;
            btnChangeDate.setText(date);

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            stringHour = changeMinuteHour_Digit(hour_x);
            minute_x = minute;
            stringMinute = changeMinuteHour_Digit(minute_x);
            //HH:MM
            time = stringHour + ":" + stringMinute;
            btnChangeTime.setText(time);
        }
    };

    /**
     * Change the digit of minute and hour from 0-9 to 00-09
     * @param timeDigit_x
     * @return timeDigit_x (new format of minute and hour)
     */
    public String changeMinuteHour_Digit(int timeDigit_x){
        if (timeDigit_x < 10){
            String stringTimeDigit = ""+timeDigit_x;
            switch (stringTimeDigit){
                case "0":
                    stringTimeDigit = "00";
                    break;
                case "1":
                    stringTimeDigit = "01";
                    break;
                case "2":
                    stringTimeDigit = "02";
                    break;
                case "3":
                    stringTimeDigit = "03";
                    break;
                case "4":
                    stringTimeDigit = "04";
                    break;
                case "5":
                    stringTimeDigit = "05";
                    break;
                case "6":
                    stringTimeDigit = "06";
                    break;
                case "7":
                    stringTimeDigit = "07";
                    break;
                case "8":
                    stringTimeDigit = "08";
                    break;
                case "9":
                    stringTimeDigit = "09";
                    break;
            }
            return stringTimeDigit;
        }
        return ""+timeDigit_x;
        

    }


    /**
     * Change the format of month from 1-12 to Jan-Dec
     * @param month_x
     */
    public void changeNumMonthToWord(int month_x){
        String stringMonth = ""+month_x;
        switch (stringMonth) {
            case "1":
                stringMonth = "Jan";
                break;
            case "2":
                stringMonth = "Feb";
                break;
            case "3":
                stringMonth = "Mar";
                break;
            case "4":
                stringMonth = "Apr";
                break;
            case "5":
                stringMonth = "May";
                break;
            case "6":
                stringMonth = "Jun";
                break;
            case "7":
                stringMonth = "Jul";
                break;
            case "8":
                stringMonth = "Aug";
                break;
            case "9":
                stringMonth = "Sep";
                break;
            case "10":
                stringMonth = "Oct";
                break;
            case "11":
                stringMonth = "Nov";
                break;
            case "12":
                stringMonth = "Dec";
                break;
        }
        month = stringMonth;

    }


    /**
     * Check Location of the Image using imagePath to find path to that image
     * Get Latitude, Latitude Reference, Longitude, Longitude Reference from the image
     * Compute latitude and longitude value
     *
     * @param imagePath
     */
    public void getLocation(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            exif = new ExifInterface(imageFile.getAbsolutePath());
            attrlatitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            attrlatitude_ref = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            attrlongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            attrlongitude_ref = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if ((attrlatitude != null) && (attrlatitude_ref != null) && (attrlongitude != null) && (attrlongitude_ref != null)) {
                valid = true;

                if (attrlatitude_ref.equals("N")) {
                    latitude = convertToDegree(attrlatitude);
                } else {
                    latitude = 0 - convertToDegree(attrlatitude);
                }

                if (attrlongitude_ref.equals("E")) {
                    longitude = convertToDegree(attrlongitude);
                } else {
                    longitude = 0 - convertToDegree(attrlongitude);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Seperate attribute of latitude and logitude into String[]
     * seperate each value(degree, minute, second)
     * Then find decimal degree of latitude and longitude
     * Formula: dd = d + m/60 + s/3600
     *
     * @param stringDMS
     * @return result [converted latitude || longitude]
     */
    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        //Degree
        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        //Minute
        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        //Second
        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;

    }

    @Override
    public String toString() {
        return (String.valueOf(latitude) + ", " + String.valueOf(longitude));
    }

    /**
     * Find the location of the image from meta-data
     * If the image does not have the location, let user add/update the location from place picker
     */
    public void location(){
        t_location = (TextView) findViewById(R.id.location);
        getLocation(imagePath);
        latitude_chosen = "" + latitude;
        longitude_chosen = "" + longitude;


        if(latitude != null && longitude != null){
            geocoder = new Geocoder(this);
            List<Address> geoResult = null;
            try {
                geoResult = geocoder.getFromLocation(latitude, longitude,1);
                if(geoResult != null){
                    List<String> geoStringResult = new ArrayList<String>();

                    Address thisAddress = geoResult.get(0);
                    String stringThisAddress = "";
                    locationAddress_chosen = "";
                    for(int i=0; i < thisAddress.getMaxAddressLineIndex(); i++) {


                        stringThisAddress += thisAddress.getAddressLine(i) + "\n";
                        locationAddress_chosen += thisAddress.getAddressLine(i) + " ";



                    }
                    locationAddress_chosen += thisAddress.getCountryName();
                    stringThisAddress += "" + thisAddress.getCountryName();
                    locationName_chosen = ""+locationAddress_chosen;
                    geoStringResult.add(stringThisAddress);

                    t_location.setText(stringThisAddress);
                    btnAddLocation.setText("Update Location");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            btnAddLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        if (version <= 9020000) {
                            String x = "" + version;
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage("Please update your google play service")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            pickLocationFromLatLng();
                        }
                    }else{

                        new AlertDialog.Builder(Tag.this).setTitle("Internet Connection")
                                .setMessage("You don't have the Internet connection. " +
                                        "Please connect to the Internet to add/update your location.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which){
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert).show();


                    }
                }
            });


        }else {

            btnAddLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        if (version <= 9020000) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage("Please update your google play service")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            pickLocation();
                        }
                    }else{

                        new AlertDialog.Builder(Tag.this).setTitle("Internet Connection")
                                .setMessage("You don't have the Internet connection. " +
                                        "Please connect to the Internet to add/update your location.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which){
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert).show();
                    }
                }
            });
        }
    }

    /**
     * Launch PlacePicker for user to select location
     * The initial location will be the current location of user
     */
    public void pickLocation(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Context context = this;
        Activity activity = (Activity) context;
        try {
            startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launch PlacePicker for user to select location
     * The initial location will get from latitude and longitude of the meta-data of image
     */
    public void pickLocationFromLatLng(){

        firstHaveLatLng = true;
        LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                new LatLng(latitude, longitude), new LatLng(latitude,longitude));
        Toast.makeText(this,"Entered pick location",Toast.LENGTH_LONG).show();
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
        Context context = this;
        Activity activity = (Activity) context;
        try {
            startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    /**
     * For find id and add a new data if it doesn't exist
     * @param tableName
     * @return
     */
    public int findID(String tableName) {
        int new_id = 1;
        Realm realm = Realm.getInstance(this);
        // find the last id number
        if (tableName.equals("Image")) {
            RealmResults<Image> check1 = realm.where(Image.class).equalTo("user_id",user_id)
                    .findAllSorted("image_id",RealmResults.SORT_ORDER_DESCENDING);
            if (check1.size() != 0) {
                int test = check1.get(0).getImage_id();
                new_id = test + 1;
            }
        }else if(tableName.equals("Activity")){
            RealmResults<Activity_list> check1 = realm.where(Activity_list.class).equalTo("activity_name",activity_chosen).findAll();
            //add new other activity
            if (check1.size()<1) {

                RealmResults<Other_activity> check2 = realm.where(Other_activity.class).equalTo("other_activity_name",activity_chosen).findAll();
                if(check2.size()==0) {
                    RealmResults<Other_activity> check3 = realm.where(Other_activity.class).findAllSorted("other_activity_id",RealmResults.SORT_ORDER_DESCENDING);
                    if( check3.size()>0){
                        int test = check3.get(0).getOther_activity_id();
                        new_other_activity_list_id = test + 1;
                    }else{
                        new_other_activity_list_id = 1;
                    }
                    //save new activity to database
                    Realm realm2 = Realm.getInstance(this);
                    realm2.beginTransaction();
                    Other_activity activity = realm2.createObject(Other_activity.class);
                    activity.setOther_activity_id(new_other_activity_list_id);
                    activity.setOther_activity_name(activity_chosen);
                    activity.setUser_id(user_id);
                    realm2.commitTransaction();
                }else{
                    new_other_activity_list_id = check2.get(0).getOther_activity_id();
                }
                new_id = 9;
            }else{
                //already have this activity
                new_id = check1.get(0).getActivity_list_id();
            }
        }else if(tableName.equals("Mood")){
            RealmResults<Mood_list> check2 = realm.where(Mood_list.class).equalTo("mood_name",mood_chosen).findAll();
            if (check2.size() != 0){
                new_id = check2.get(0).getMood_list_id();
            }
        }else if(tableName.equals("Location")){
            RealmResults<Location> check2 = realm.where(Location.class).findAllSorted("location_id",RealmResults.SORT_ORDER_DESCENDING);
            if (check2.size() != 0) {
                int test = check2.get(0).getLocation_id();
                new_id = test + 1;
            }
        }else if(tableName.equals("People_list")){
            RealmResults<People_list> check1 = realm.where(People_list.class).equalTo("people_name",people_name).findAll();
            //chosen other people
            if (check1.size()<1) {
                RealmResults<Other_people> check2 = realm.where(Other_people.class).equalTo("other_people_name",people_name).findAll();
                if(check2.size() == 0) {
                    RealmResults<Other_people> check3 = realm.where(Other_people.class).findAllSorted("other_people_id",RealmResults.SORT_ORDER_DESCENDING);
                    if( check3.size()>0){
                        int test = check3.get(0).getOther_people_id();
                        new_other_people_list_id = test + 1;
                    }else{
                        new_other_people_list_id = 1;
                    }
                    //save new people to database
                    Realm realm5 = Realm.getInstance(this);
                    realm5.beginTransaction();
                    Other_people otherpeople = realm5.createObject(Other_people.class);
                    otherpeople.setOther_people_id(new_other_people_list_id);
                    otherpeople.setOther_people_name(people_name);
                    otherpeople.setUser_id(user_id);
                    realm5.commitTransaction();
                }else{
                    new_other_people_list_id = check2.get(0).getOther_people_id();
                }
                new_id = 9;

            }else{
                //already have this activity
                new_id = check1.get(0).getPeople_list_id();
            }
        }else if(tableName.equals("People")){
            RealmResults<People> check2 = realm.where(People.class).findAllSorted("people_id",RealmResults.SORT_ORDER_DESCENDING);
            if (check2.size() != 0) {
                int test = check2.get(0).getPeople_id();
                new_id = test + 1;
            }
        }
        return new_id;
    }


    /**
     * Split people_chosen_string into 3 ArrayList(people, x, y)
     * @param people_chosen_
     */
    public void splitPeopleChosenToPeopleArray(String[] people_chosen_){
        people = new ArrayList<String>();
        x = new ArrayList<String>();
        y = new ArrayList<String>();

        if (people_chosen_ != null) {
            for(int i=0;i<people_chosen_.length;i++){
                if(i%4==0&&(!people_chosen_[i].equals("-1"))){
                    people.add(people_chosen_[i]);
                }else if(i%4==1&&(!people_chosen_[i].equals("-1"))){
                    x.add(people_chosen_[i]);
                }else if(i%4==2&&(!people_chosen_[i].equals("-1"))){
                    y.add(people_chosen_[i]);
                }
            }
        }
    }

    /**
     * Check the all data
     * If user didn't provide necessary data, user have to put it
     * @return
     */
    public boolean checkAllData(){
        if(locationName_chosen == null){
            Toast.makeText(this,"Please add a location of your photo.",Toast.LENGTH_LONG).show();
        }
        else if(activity_chosen== null){
            Toast.makeText(this,"Please add your activity.",Toast.LENGTH_LONG).show();
        }else if(mood_chosen== null){
            Toast.makeText(this,"Please add your feeling.",Toast.LENGTH_LONG).show();
        }else if(people.size()==0){
            new AlertDialog.Builder(this).setTitle("Any body here?")
                    .setMessage("You didn't add any people, do you want to save?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            saveImageToAlbum();
                            saveAllDataToLocal();
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){

                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }else{
            saveImageToAlbum();
            saveAllDataToLocal();
        }
        return false;
    }

    /**
     * After click save button, the image will be saved to the application album
     */
    public void saveImageToAlbum(){
        File folder = new File(Environment.getExternalStorageDirectory()+"/DCIM/snapM");

        if(!folder.exists()){
            folder.mkdir();
        }
        File image_file = new File(folder,image_name);
        String newPath = image_file.getAbsolutePath();


        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap imageData = BitmapFactory.decodeFile(imagePath, options);



        try {
            FileOutputStream out = new FileOutputStream(image_file);

            imageData.compress(Bitmap.CompressFormat.JPEG,100, out);

            out.flush();
            out.close();
            try {
                copyExif(imagePath, newPath);
                imagePath = newPath;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Copy all meta-data from the existing image to the new image to save in application album
     * @param oldPath
     * @param newPath
     * @throws IOException
     */
    public static void copyExif(String oldPath, String newPath) throws IOException
    {
        ExifInterface oldExif = new ExifInterface(oldPath);

        String[] attributes = new String[]
                {
                        ExifInterface.TAG_APERTURE,
                        ExifInterface.TAG_DATETIME,
                        ExifInterface.TAG_DATETIME_DIGITIZED,
                        ExifInterface.TAG_EXPOSURE_TIME,
                        ExifInterface.TAG_FLASH,
                        ExifInterface.TAG_FOCAL_LENGTH,
                        ExifInterface.TAG_GPS_ALTITUDE,
                        ExifInterface.TAG_GPS_ALTITUDE_REF,
                        ExifInterface.TAG_GPS_DATESTAMP,
                        ExifInterface.TAG_GPS_LATITUDE,
                        ExifInterface.TAG_GPS_LATITUDE_REF,
                        ExifInterface.TAG_GPS_LONGITUDE,
                        ExifInterface.TAG_GPS_LONGITUDE_REF,
                        ExifInterface.TAG_GPS_PROCESSING_METHOD,
                        ExifInterface.TAG_GPS_TIMESTAMP,
                        ExifInterface.TAG_IMAGE_LENGTH,
                        ExifInterface.TAG_IMAGE_WIDTH,
                        ExifInterface.TAG_ISO,
                        ExifInterface.TAG_MAKE,
                        ExifInterface.TAG_MODEL,
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.TAG_SUBSEC_TIME,
                        ExifInterface.TAG_SUBSEC_TIME_DIG,
                        ExifInterface.TAG_SUBSEC_TIME_ORIG,
                        ExifInterface.TAG_WHITE_BALANCE
                };

        ExifInterface newExif = new ExifInterface(newPath);
        for (int i = 0; i < attributes.length; i++)
        {
            String value = oldExif.getAttribute(attributes[i]);
            if (value != null)
                newExif.setAttribute(attributes[i], value);
        }
        newExif.saveAttributes();
    }

    /**
     * Save all data in to local database
     */
    public void saveAllDataToLocal(){

        //find image id (id+username)
        int new_image_id = findID("Image");

        //save activity to list if user put a new one and find activity id
        int activity_id = findID("Activity");

        //find mood id
        int mood_id = findID("Mood");

        //upload status
        boolean upload_status = false;

        //save all info to image table(image name, path, time, activity id., mood id, description)
        /*
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        Image image = realm.createObject(Image.class);
        image.setImage_id(new_image_id);
        image.setUser_id(user_id);
        image.setImage_name(image_name);
        image.setImage_path(imagePath_chosen);
        image.setTime(date+"|"+time);//format DD MM YYYY|HH:MM
        image.setActivity_list_id(activity_id);
        image.setOther_activity_id(new_other_activity_list_id);
        image.setMood_list_id(mood_id);
        image.setDescription(caption);
        image.setHorizontal_size_pic(Integer.parseInt(horizontal_size_pic));
        image.setVertical_size_pic(Integer.parseInt(vertical_size_pic));
        if(upload_status){
            image.setUpload_status(true);
        }else{
            image.setUpload_status(false);
        }

        //save location to location table by connect w/ image_id
        //find current location table id
        int location_id = findID("Location");
        Location location = realm.createObject(Location.class);
        location.setLocation_id(location_id);
        location.setImage_id(new_image_id);
        location.setUser_id(user_id);
        location.setLocation_name(locationName_chosen);
        location.setLocation_address(locationAddress_chosen);
        location.setLatitude(Float.parseFloat(latitude_chosen));
        location.setLongiude(Float.parseFloat(longitude_chosen));
        realm.commitTransaction();

        //save people to list if user put a new one
        //have to find table current id
        //add each people
        for(int i =0;i<people.size();i++){
            people_name = people.get(i);
            new_other_people_list_id = 0;
            int id_people = findID("People");
            int id_people_list = findID("People_list");
            Realm realm2 = Realm.getInstance(this);
            realm2.beginTransaction();
            People realm_people = realm.createObject(People.class);
            realm_people.setPeople_id(id_people);
            realm_people.setImage_id(new_image_id);
            realm_people.setUser_id(user_id);
            realm_people.setPeople_list_id(id_people_list);
            realm_people.setOther_people_list_id(new_other_people_list_id);
            realm_people.setPeople_x(Float.parseFloat(x.get(i)));
            realm_people.setPeople_y(Float.parseFloat(y.get(i)));
            realm2.commitTransaction();
        }

     */
        Realm realmsave = Realm.getInstance(this);
        RealmResults<Image> check1 =  realmsave.where(Image.class)
                .findAll();

            Image results3 =
                    realmsave.where(Image.class)
                            .contains("image_path", imagePath)
                            .findFirst();
            Realm realm = Realm.getInstance(this);
            if (results3 == null) {
                //save all info to image table(image name, path, time, activity id., mood id, description)
                realm.beginTransaction();
                Image image = realm.createObject(Image.class);
                image.setImage_id(new_image_id);
                image.setUser_id(user_id);
                image.setImage_name(image_name);
                image.setImage_path(imagePath_chosen);
                image.setTime(time);
                image.setDate(date);
                image.setDate_formatted(date_formatted);
                image.setActivity_list_id(activity_id);
                image.setOther_activity_id(new_other_activity_list_id);
                image.setMood_list_id(mood_id);
                image.setDescription(caption);
                image.setHorizontal_size_pic(Integer.parseInt(horizontal_size_pic));
                image.setVertical_size_pic(Integer.parseInt(vertical_size_pic));
                image.setPeople_count(Integer.parseInt(people_count));
                //if(people_chosen!=null) {
                //    image.setPeople_name(people_chosen);
                //}
                image.setLatitude(String.valueOf(latitude));
                image.setLongitude(String.valueOf(longitude));
                if(activity_chosen!=null) {
                    image.setActivity_name(activity_chosen);
                }
                if(mood_chosen!=null){
                    image.setMood_name(mood_chosen);
                }
                image.setLocation(locationAddress_chosen);
                image.setUpload_status(false);
                image.setModified_after_upload(false);
                realm.commitTransaction();
            } else {
                realm.beginTransaction();
                results3.setTime(time);//format DD MM YYYY|HH:MM
                results3.setDate(date);
                results3.setDate_formatted(date_formatted);
                results3.setActivity_list_id(activity_id);
                results3.setOther_activity_id(new_other_activity_list_id);
                results3.setMood_list_id(mood_id);
                results3.setDescription(caption);
                results3.setHorizontal_size_pic(Integer.parseInt(horizontal_size_pic));
                results3.setVertical_size_pic(Integer.parseInt(vertical_size_pic));
                //if(people_chosen!=null){
                //    results3.setPeople_name(people_chosen);
                //}
                results3.setLatitude(String.valueOf(latitude));
                results3.setLatitude(String.valueOf(longitude));
                results3.setPeople_count(Integer.parseInt(people_count));
                if(activity_chosen!=null){
                    results3.setActivity_name(activity_chosen);
                }
                if(mood_chosen!=null){
                    results3.setMood_name(mood_chosen);
                }
                results3.setLocation(locationAddress_chosen);
                //realm.copyToRealmOrUpdate(results3);
                //if(results3.isUpload_status()){
                    results3.setModified_after_upload(true);
                //}
                //else {
                    //results3.setModified_after_upload(false);
                //}
                realm.commitTransaction();
                Toast.makeText(Tag.this,String.valueOf(results3.isUpload_status())+String.valueOf(results3.isModified_after_upload()), Toast.LENGTH_LONG).show();
            }


    finish();


        Toast.makeText(Tag.this, "Save success!!", Toast.LENGTH_LONG).show();

    }



    /**
     * Save all data into server database by send all information to BackgroundTask Page
     * @return

    public boolean saveAllDataToServer(){
        String method = "tag_image";
        String people_chosen_string = "";
        for(int i =0 ; i<people.size();i++){
            if(i<people.size()-1){
                people_chosen_string+=people.get(i)+","+x.get(i)+","+y.get(i)+",";
            }else{
                people_chosen_string+=people.get(i)+","+x.get(i)+","+y.get(i);
            }
        }
        BackgroundTask backgroundTask_image = new BackgroundTask(this);
        backgroundTask_image.execute(method, ""+user_id, image_name, imagePath,date+"|"+time, activity_chosen,
                mood_chosen,caption, horizontal_size_pic, vertical_size_pic,
                locationName_chosen, locationAddress_chosen, ""+latitude_chosen, ""+longitude_chosen,
                people_chosen_string,people_count);

        return true;
    }
     */


}