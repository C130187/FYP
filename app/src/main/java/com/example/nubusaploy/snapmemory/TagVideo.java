package com.example.nubusaploy.snapmemory;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.example.nubusaploy.snapmemory.model.Location;
import com.example.nubusaploy.snapmemory.model.Mood_list;
import com.example.nubusaploy.snapmemory.model.Other_activity;
import com.example.nubusaploy.snapmemory.model.Other_people;
import com.example.nubusaploy.snapmemory.model.People;
import com.example.nubusaploy.snapmemory.model.People_list;
import com.example.nubusaploy.snapmemory.model.Video;
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
public class TagVideo extends AppCompatActivity {

    //NFC
    //NfcAdapter mNfcAdapter;
    //boolean mAndroidBeamAvailable  = false;



    //Open Image
    //VideoView videoView;
    String videoPath;
    Uri videoUri;
    String videoDecodableString;
    String requestCode;
    MediaMetadataRetriever mmr;
    //ExifInterface exif;
    //identity
    int user_id;
    String video_name;
    TextView description;

    //video's date and time
    String dateTime;
    String date = "", time = "";
    String date_formatted = "";
    MediaMetadataRetriever mmrf;
    //ExifInterface intf;
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
    GPSTracker gps;

    //Check Google Play Service Version
    int version;

    //For People
    Button btnTagPeople;
    TextView t_people;
    int PEOPLE = 2;
    String people_count = "0";
    //String[] people_chosen;
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
    String videoPath_chosen, locationName_chosen, locationAddress_chosen="",
            latitude_chosen, longitude_chosen, mood_chosen="", video_datetime,
            people_chosen="", activity_chosen="";
    String caption="";
    String horizontal_size_vid = "0";
    String vertical_size_vid = "0";
    int new_other_people_list_id = 0;
    int new_other_activity_list_id = 0;

    ExifInterface exif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tagvideo);
        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //videoView = (VideoView) findViewById(R.id.shownVideo);

        //Get data from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoPath = extras.getString("videoPath"); // getString(valuename) -- valuename same as the one it use for send
            videoUri = Uri.parse(extras.getString("videoUri"));
            requestCode = extras.getString("requestCode");
            videoPath_chosen = videoPath;
            if(extras.getString("videoName")!=null){
                video_name = extras.getString("videoName");
            }
            user_id = 0;
        }


        ImageView imageview_micro = (ImageView)findViewById(R.id.shownVideo);

        File videoFile = new File(videoPath);

        Bitmap bmThumbnail;

//MICRO_KIND, size: 96 x 96 thumbnail
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoFile.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
        imageview_micro.setImageBitmap(bmThumbnail);



        try {
            version = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

           getImageWidthLength(videoPath);
        //For date and time
        btnChangeDate = (Button) findViewById(R.id.addDate);
        btnChangeTime = (Button) findViewById(R.id.addTime);

        if (dateTime == null) {
        getDateTimeLastModified(videoPath);
       }

        btnChangeTime.setText(time);
        mmrf = null;
        mmrf = new MediaMetadataRetriever();
        mmrf.setDataSource(videoPath);

        try {
            exif = new ExifInterface(videoFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        description = (TextView) findViewById(R.id.textCaption);

        btnChangeDate.setText(date);
        description.setText(mmrf.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION));
        video_datetime = date + time;

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
        t_people = (TextView) findViewById(R.id.people);
        btnTagPeople = (Button)findViewById(R.id.tagPeople);
        t_activity = (TextView) findViewById(R.id.activity);
        btnAddActivity = (Button)findViewById(R.id.addActivity);
        t_mood = (TextView) findViewById(R.id.mood);
        btnAddMood = (Button)findViewById(R.id.addMood);



        Realm realmtest = Realm.getInstance(this);
        RealmResults<Video> check1 =  realmtest.where(Video.class)
                .findAll();
        if(check1.size()!=0){
            Video results2 =
                    realmtest.where(Video.class)
                            .contains("video_path",videoPath)
                            .findFirst();
            if(results2!=null){
                String existing_people = results2.getPeople_name();
                String existing_time = results2.getTime();
                String existing_date = results2.getDate();
                String existing_activity = results2.getActivity_name();
                String existing_mood = results2.getMood_name();
                String existing_location = results2.getLocation();

                people_chosen = existing_people;
                activity_chosen = existing_activity;
                mood_chosen = existing_mood;
                locationName_chosen = existing_location;
                t_location.setText(existing_location);

                if(!activity_chosen.equals("")){
                    btnAddActivity.setText("Change Activity");
                    //t_activity.setText(existing_activity);
                    t_activity.setText(activity_chosen);
                }
                else
                {
                    btnAddActivity.setText("No Activity Added");
                    //t_activity.setText(existing_activity);
                    t_activity.setText(activity_chosen);

                }
                if(!mood_chosen.equals("")){
                    btnAddMood.setText("Change Mood");
                    //t_mood.setText(existing_mood);
                    t_mood.setText(mood_chosen);
                }
                else
                {
                    btnAddMood.setText("No Mood Added");
                    //t_activity.setText(existing_activity);
                    t_mood.setText(mood_chosen);

                }
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

                btnChangeDate.setText(existing_date);
                btnChangeTime.setText(existing_time);
                Toast.makeText(this,results2.getVideo_name()+results2.getVideo_id(),Toast.LENGTH_LONG).show();
            }
        }


        //For Tag People
        //people_chosen = new String[int_people_count*4];

        btnTagPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddPeople.class);
                i.putExtra("user_id",user_id);
                startActivityForResult(i,PEOPLE);
            }
        });


        //For Activity

        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddActivity.class);
                i.putExtra("user_id",user_id);
                startActivityForResult(i,ACTIVITY );
            }
        });



        //For Mood

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
                caption = ""+text.getText();
                checkAllData();
            }
        });


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
                    people_chosen=data.getStringExtra("people_chosen");
                    if(people_chosen != null){
                        btnTagPeople.setText("Change People");
                    }

                    t_people.setText(people_chosen.toString());

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
     * Find the width and length of the video
     * @param videoPath
     */
    public void getImageWidthLength(String videoPath){
        mmrf = null;

            mmrf = new MediaMetadataRetriever();
            mmrf.setDataSource(videoPath);
            horizontal_size_vid = mmrf.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            vertical_size_vid = mmrf.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);

    }

    /**
     * Get the date and time from meta-data of video
     * @param videoPath
     */
    public void getDateTime(String videoPath) {
        mmrf = null;
        mmrf = new MediaMetadataRetriever();
        mmrf.setDataSource(videoPath);

        if (mmrf != null) {
            //YYYY:MM:DD HH:MM:SS
            //dateTime = intf.getAttribute(ExifInterface.TAG_DATETIME);
            date = mmrf.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);

            if (date != null) {
                //String[] stringDateTime = dateTime.split(" ", 2);
                //String[] stringDate = date.split(":", 3);
                String tempdate= new String(mmrf.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));

                String stringYear = tempdate.substring(0,4);
                String stringMonth = tempdate.substring(4,6);
                String stringDay = tempdate.substring(6,8);
                //String stringYear = stringDate[0];
                //String stringMonth = stringDate[1];
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

                //String stringDay = stringDate[2];
                //DD MMM YYYY
                date = stringDay + " " + stringMonth + " " + stringYear;
                //HH:MM:SS
                //time = stringDateTime[1];
                //HH:MM

            }
            //String timestr = mmrf.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            //String[] stringTime =timestr.split(":", 3);
           // time = stringTime[0]+":"+stringTime[1];
            //time = "00:00";
        }

    }

    /**
     * Get the last modified date and time of image in case of no meta-data date and time
     * @param imagePath

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
            //HH:MM:SS
            //time = modDateTime[3];
            //HH:MM
            String[] stringTime = modDateTime[3].split(":", 3);
            time = stringTime[0]+":"+stringTime[1];


        }
    }
     */


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
     * Check Current Location of the phone and use it as default location the Video
     *
     *
     */
    public void getLocation() {

        gps = new GPSTracker(TagVideo.this);

        if(gps.canGetLocation()) {
            latitude = Float.valueOf(String.valueOf(gps.getLatitude()));
            longitude = Float.valueOf(String.valueOf(gps.getLongitude()));
        }

        latitude_chosen = "" + latitude;
        longitude_chosen = "" + longitude;

    }

    /**
     * Seperate attribute of latitude and logitude into String[]
     * seperate each value(degree, minute, second)
     * Then find decimal degree of latitude and longitude
     * Formula: dd = d + m/60 + s/3600
     *
     * //@param stringDMS
     * @return result [converted latitude || longitude]

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
     */
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
        getLocation();



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

                        new AlertDialog.Builder(TagVideo.this).setTitle("Internet Connection")
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

                        new AlertDialog.Builder(TagVideo.this).setTitle("Internet Connection")
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
        /*
        gps = new GPSTracker(TagVideo.this);
        double latitude = 0;
        double longitude = 0;
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        */
        firstHaveLatLng = true;
        LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                new LatLng(latitude, longitude), new LatLng(latitude,longitude));
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
        if (tableName.equals("Video")) {
            RealmResults<Video> check1 = realm.where(Video.class).equalTo("user_id",user_id)
                    .findAllSorted("video_id",RealmResults.SORT_ORDER_DESCENDING);
            if (check1.size() != 0) {
                int test = check1.get(0).getVideo_id();
                new_id = test + 1;
            }
        }else if(tableName.equals("Activity") && activity_chosen!=null){
            RealmResults<Activity_list> check1 = realm.where(Activity_list.class).equalTo("activity_name",activity_chosen).findAll();
            //add new other activity

            if (check1.size()<1 ) {

                RealmResults<Other_activity> check2 = realm.where(Other_activity.class).equalTo("other_activity_name",activity_chosen).findAll();
                if(check2.size()==0) {
                    RealmResults<Other_activity> check3 = realm.where(Other_activity.class).findAllSorted("other_activity_id", RealmResults.SORT_ORDER_DESCENDING);
                    if (check3.size() > 0 ) {
                        int test = check3.get(0).getOther_activity_id();
                        new_other_activity_list_id = test + 1;
                    } else {
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
        }else if(tableName.equals("Mood") && mood_chosen!=null){
            RealmResults<Mood_list> check2 = realm.where(Mood_list.class).equalTo("mood_name",mood_chosen).findAll();
            if (check2.size() != 0 ){
                new_id = check2.get(0).getMood_list_id();
            }
        }else if(tableName.equals("Location")){
            RealmResults<Location> check2 = realm.where(Location.class).findAllSorted("location_id",RealmResults.SORT_ORDER_DESCENDING);
            if (check2.size() != 0 ) {
                int test = check2.get(0).getLocation_id();
                new_id = test + 1;
            }
        }else if(tableName.equals("People_list")){
            RealmResults<People_list> check1 = realm.where(People_list.class).equalTo("people_name",people_name).findAll();
            //chosen other people
            if (check1.size()<1 ) {
                RealmResults<Other_people> check2 = realm.where(Other_people.class).equalTo("other_people_name",people_name).findAll();
                if(check2.size() == 0) {
                    RealmResults<Other_people> check3 = realm.where(Other_people.class).findAllSorted("other_people_id",RealmResults.SORT_ORDER_DESCENDING);
                    if( check3.size()>0 ){
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
            if (check2.size() != 0 && check2!=null) {
                int test = check2.get(0).getPeople_id();
                new_id = test + 1;
            }
        }
        return new_id;
    }


    /**
     * Check the all data
     * If user didn't provide necessary data, user have to put it
     * @return
     */
    public boolean checkAllData(){
        /*
        if(locationName_chosen == null){
            Toast.makeText(this,"Please add a location of your photo.",Toast.LENGTH_LONG).show();
        }
        else if(activity_chosen== null){
            Toast.makeText(this,"Please add your activity.",Toast.LENGTH_LONG).show();
        }else if(mood_chosen== null){
            Toast.makeText(this,"Please add your feeling.",Toast.LENGTH_LONG).show();
        }else if(people_chosen == null){

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

        }else{*/
            saveImageToAlbum();
            saveAllDataToLocal();
        //}

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
        File video_file = new File(folder,video_name);
        String newPath = video_file.getAbsolutePath();


       // BitmapFactory.Options options = new BitmapFactory.Options();
      //  Bitmap imageData = BitmapFactory.decodeFile(imagePath, options);



        try {
            FileOutputStream out = new FileOutputStream(video_file);

           // imageData.compress(Bitmap.CompressFormat.JPEG,100, out);

            out.flush();
            out.close();
            /*
            try {
                copyExif(imagePath, newPath);
                imagePath = newPath;
            } catch (IOException e) {
                e.printStackTrace();
            }*/

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Save all data in to local database
     */
    public void saveAllDataToLocal(){

        //find image id (id+username)
        int new_video_id = findID("Video");

        //save activity to list if user put a new one and find activity id
        int activity_id = findID("Activity");

        //find mood id
        int mood_id = findID("Mood");

        Realm realmsave = Realm.getInstance(this);
        RealmResults<Video> check1 =  realmsave.where(Video.class)
                .findAll();

            Video results3 =
                    realmsave.where(Video.class)
                            .contains("video_path", videoPath)
                            .findFirst();
            Realm realm = Realm.getInstance(this);
            if (results3 == null) {
                //save all info to image table(image name, path, time, activity id., mood id, description)
                realm.beginTransaction();
                Video video = realm.createObject(Video.class);
                video.setVideo_id(new_video_id);
                video.setUser_id(user_id);
                video.setVideo_name(video_name);
                video.setVideo_path(videoPath_chosen);
                video.setTime(time);
                video.setDate(date);
                video.setDate_formatted(date_formatted);
                video.setActivity_list_id(activity_id);
                video.setOther_activity_id(new_other_activity_list_id);
                video.setMood_list_id(mood_id);
                video.setDescription(caption);
                video.setHorizontal_size_vid(Integer.parseInt(horizontal_size_vid));
                video.setVertical_size_vid(Integer.parseInt(vertical_size_vid));
                if(people_chosen!=null) {
                    video.setPeople_name(people_chosen);
                }
                video.setLatitude(String.valueOf(latitude));
                video.setLongitude(String.valueOf(longitude));
                if(activity_chosen!=null) {
                    video.setActivity_name(activity_chosen);
                }
                if(mood_chosen!=null){
                    video.setMood_name(mood_chosen);
                }
                video.setLocation(locationAddress_chosen);
                video.setUpload_status(false);
                video.setModified_after_upload(false);
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
                results3.setHorizontal_size_vid(Integer.parseInt(horizontal_size_vid));
                results3.setVertical_size_vid(Integer.parseInt(vertical_size_vid));
                if(people_chosen!=null){
                    results3.setPeople_name(people_chosen);
                }
                results3.setLatitude(String.valueOf(latitude));
                results3.setLatitude(String.valueOf(longitude));
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
                Toast.makeText(TagVideo.this,String.valueOf(results3.isUpload_status())+String.valueOf(results3.isModified_after_upload()), Toast.LENGTH_LONG).show();
            }


    finish();


        Toast.makeText(TagVideo.this, "Save success!!", Toast.LENGTH_LONG).show();

    }


}