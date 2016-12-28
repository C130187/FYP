package com.example.nubusaploy.snapmemory;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nubusaploy.snapmemory.model.Activity_list;
import com.example.nubusaploy.snapmemory.model.Image;
import com.example.nubusaploy.snapmemory.model.Mood_list;
import com.example.nubusaploy.snapmemory.model.People_list;
import com.example.nubusaploy.snapmemory.model.Video;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends Activity {

    ImageView imageView;

    private TextView textViewResponse;
    ImageButton imagebutton,searchbutton, uploadbutton;
    private static final int PICK_IMAGE = 2;
    private static final int PICK_VIDEO =4 ;
    private static final int CAM_REQUEST =1;
    private static final int REQUEST_VIDEO_CAPTURE =3;
    private String selectedPath;
    String imagePath;
    String videoPath;

    //Time take photo
    String image_name;
    String video_name;
    ArrayList<String> mood_list = new ArrayList<String>();
    ArrayList<String> activity_list = new ArrayList<String>();
    ArrayList<String> people_list = new ArrayList<String>();

    List<Video> uploadedVideoList = new ArrayList<Video>();
    List<Image> uploadedImageList = new ArrayList<Image>();

    //Storage Permissions variales -- Android 6 - Need to ask permission
    private static final int REQUEST_PERMISSION = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyPermissions(this);

        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //Screen Rotation
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 2) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.homepage_land);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.homepage);
        }

        imageView = (ImageView)findViewById(R.id.shownPhoto);
        imagebutton = (ImageButton) findViewById(R.id.selectPhoto);
        searchbutton = (ImageButton) findViewById(R.id.searchPhoto);
        uploadbutton = (ImageButton) findViewById(R.id.uploadPhoto);


        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    int writePermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int readPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    //Check if we have camera permission
                    int cameraPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                    int locationPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    if(writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED && cameraPermission == PackageManager.PERMISSION_GRANTED && locationPermission == PackageManager.PERMISSION_GRANTED){
                        selectMedia();
                    }else{
                        verifyPermissions(MainActivity.this);
                    }
                }else{
                    selectMedia();
                }


            }
        });
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int writePermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int readPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                int locationPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                if(writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED || locationPermission != PackageManager.PERMISSION_GRANTED){
                    verifyPermissions(MainActivity.this);
                }


            }
        });

        setDatabase();

        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check wifi connection and battery level
                boolean wifiConnection = false;
                boolean batteryEnough = false;
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWifi.isConnected()) {
                    wifiConnection =true;
                }
                Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryLevel;

                // Error checking that probably isn't needed but I added just in case.
                if(level == -1 || scale == -1) {
                   batteryLevel= 50.0f;
                }

                batteryLevel= ((float)level / (float)scale) * 100.0f;
               if(batteryLevel>=40){
                  batteryEnough = true;
                }
                if(wifiConnection&&batteryEnough){
                    //uploading images
                    updateServerforUploadedImages();
                    doImageUpload();

                    //uploading videos
                    updateServerforUploadedVideos();
                    doVideoUpload();
                    Log.d("conditions satisfied","enters uploading");

                }
                Log.d("not satisfied","no");
                Log.d("wifi value",String.valueOf(wifiConnection));
                Log.d("battery value",String.valueOf(batteryLevel));

             }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds options to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    private void updateServerforUploadedImages(){

        String temppath = "", method = "tag_image", event_date = "",event_time= "", event_mood ="",event_description = "", event_location="",
                filepath = "",latitude = "", longitude = "", event_people="";
        int people_count = 0;

        Realm realm = Realm.getInstance(this);

        String localpath = "MyLife/ImageUpload/uploads/";

        RealmResults<Image> result_uploaded = realm.where(Image.class)
                .equalTo("modified_after_upload", true)
                .equalTo("upload_status", true)
                .findAll();

        List<Image> modifyList = new ArrayList<Image>();
        if(result_uploaded.size()>0){
            Log.d("result_uploaded size",String.valueOf(result_uploaded.size()));

            for(int i =0; i<result_uploaded.size();i++){
                Image tempimage = result_uploaded.get(i);
                Log.d("imagemetadata updated", tempimage.getImage_path());
                modifyList.add(tempimage);
                event_date = tempimage.getDate_formatted();
                event_time = tempimage.getTime();
                event_mood = tempimage.getMood_name();
                event_description = tempimage.getDescription();
                event_location =tempimage.getLocation();
                filepath = localpath+tempimage.getImage_name();
                latitude = tempimage.getLatitude();
                longitude = tempimage.getLongitude();
                event_people = tempimage.getPeople_name();
                people_count = tempimage.getPeople_count();

                //tempvideo.setModified_after_upload(false);

                Toast.makeText(this,"second for loop:"+temppath + tempimage.isUpload_status()+tempimage.isModified_after_upload(),Toast.LENGTH_LONG).show();
                BackgroundTImage backgroundT_image = new BackgroundTImage(this);
                backgroundT_image.execute(method, ""+event_date, event_time, event_mood,event_description,event_location,
                        String.valueOf(latitude),String.valueOf(longitude), filepath, event_people, String.valueOf(people_count),"true");

            }
            if(modifyList.size()>0) {
                for (int i = 0; i < modifyList.size(); i++) {
                    realm.beginTransaction();
                    modifyList.get(i).setModified_after_upload(false);
                    realm.commitTransaction();
                }
            }
        }
    }



    private void doImageUpload(){


        String temppath = "", method = "tag_image", event_date = "",event_time= "", event_mood ="",event_description = "", event_location="",
                filepath = "",latitude = "", longitude = "", event_people="";
        int people_count =0 ;


        Realm realm = Realm.getInstance(this);

        RealmResults<Image> result = realm.where(Image.class)
                .equalTo("upload_status", false)
                .findAll();

        //Toast.makeText(this,"enteredupload",Toast.LENGTH_LONG).show();
        String localpath = "MyLife/ImageUpload/uploads/";

        for(int i =0; i<result.size(); i++)
        {
            Log.d("i"+i,result.get(i).getImage_path()+result.get(i).isUpload_status());
            Image tempimage = result.get(i);
            temppath = tempimage.getImage_path();
            event_date = tempimage.getDate_formatted();
            event_time = tempimage.getTime();
            event_mood = tempimage.getMood_name();
            event_description = tempimage.getDescription();
            event_location =tempimage.getLocation();
            filepath = localpath+tempimage.getImage_name();
            latitude = tempimage.getLatitude();
            longitude = tempimage.getLongitude();
            event_people = tempimage.getPeople_name();
            people_count = tempimage.getPeople_count();
            Toast.makeText(this,"first for loop:"+temppath + tempimage.isUpload_status(),Toast.LENGTH_LONG).show();
            BackgroundTImage backgroundT_image = new BackgroundTImage(this);
            backgroundT_image.execute(method, ""+event_date, event_time, event_mood,event_description,event_location,
                    String.valueOf(latitude),String.valueOf(longitude), filepath, event_people,String.valueOf(people_count), "false");
            Log.d(temppath,"pathh");
            Log.d(event_date,"date");
            Log.d(event_time,"time");
            Log.d(event_mood,"mood");
            Log.d(event_description,"description");
            Log.d(event_location,"location");
            Log.d(latitude,"latitude");
            Log.d(longitude,"longitude");
            Log.d(event_people,"event_people");
            Log.d(String.valueOf(people_count),"people_count");
            Toast.makeText(this,event_date+event_mood+event_people,Toast.LENGTH_LONG).show();
            selectedPath = temppath;
            uploadImage(realm, tempimage);

        }

    }

    private void updateServerforUploadedVideos(){

        String temppath = "", method = "tag_video", event_date = "",event_time= "", event_mood ="",event_description = "", event_location="",
                filepath = "",latitude = "", longitude = "", event_people="";

        Realm realm = Realm.getInstance(this);

        String localpath = "VideoUpload/uploads/";

        RealmResults<Video> result_uploaded = realm.where(Video.class)
                .equalTo("modified_after_upload", true)
                .equalTo("upload_status", true)
                .findAll();

        List<Video> modifyList = new ArrayList<Video>();
        if(result_uploaded.size()>0){
            Log.d("result_uploaded size",String.valueOf(result_uploaded.size()));

            for(int i =0; i<result_uploaded.size();i++){
                Video tempvideo = result_uploaded.get(i);
                Log.d("videometadata updated", tempvideo.getVideo_path());
                modifyList.add(tempvideo);
                event_date = tempvideo.getDate_formatted();
                event_time = tempvideo.getTime();
                event_mood = tempvideo.getMood_name();
                event_description = tempvideo.getDescription();
                event_location =tempvideo.getLocation();
                filepath = localpath+tempvideo.getVideo_name();
                latitude = tempvideo.getLatitude();
                longitude = tempvideo.getLongitude();
                event_people = tempvideo.getPeople_name();

                //tempvideo.setModified_after_upload(false);

                Toast.makeText(this,"second for loop:"+temppath + tempvideo.isUpload_status()+tempvideo.isModified_after_upload(),Toast.LENGTH_LONG).show();
                BackgroundTVideo backgroundT_video = new BackgroundTVideo(this);
                backgroundT_video.execute(method, ""+event_date, event_time, event_mood,event_description,event_location,
                        String.valueOf(latitude),String.valueOf(longitude), filepath, event_people, "true");

            }
            if(modifyList.size()>0) {
                for (int i = 0; i < modifyList.size(); i++) {
                    realm.beginTransaction();
                    modifyList.get(i).setModified_after_upload(false);
                    realm.commitTransaction();
                }
            }
        }
    }



    private void doVideoUpload(){


        String temppath = "", method = "tag_video", event_date = "",event_time= "", event_mood ="",event_description = "", event_location="",
                filepath = "",latitude = "", longitude = "", event_people="";

        Realm realm = Realm.getInstance(this);

        RealmResults<Video> result = realm.where(Video.class)
                .equalTo("upload_status", false)
                .findAll();

        //Toast.makeText(this,"enteredupload",Toast.LENGTH_LONG).show();
        String localpath = "VideoUpload/uploads/";

        for(int i =0; i<result.size(); i++)
        {
            Log.d("i"+i,result.get(i).getVideo_path()+result.get(i).isUpload_status());
            Video tempvideo = result.get(i);
            temppath = tempvideo.getVideo_path();
            event_date = tempvideo.getDate_formatted();
            event_time = tempvideo.getTime();
            event_mood = tempvideo.getMood_name();
            event_description = tempvideo.getDescription();
            event_location =tempvideo.getLocation();
            filepath = localpath+tempvideo.getVideo_name();
            latitude = tempvideo.getLatitude();
            longitude = tempvideo.getLongitude();
            event_people = tempvideo.getPeople_name();
            Toast.makeText(this,"first for loop:"+temppath + tempvideo.isUpload_status(),Toast.LENGTH_LONG).show();
            BackgroundTVideo backgroundT_video = new BackgroundTVideo(this);
            backgroundT_video.execute(method, ""+event_date, event_time, event_mood,event_description,event_location,
                    String.valueOf(latitude),String.valueOf(longitude), filepath, event_people, "false");
            //Log.d(temppath,"pathh");
            selectedPath = temppath;
            uploadVideo(realm, tempvideo);

        }

    }

    private void updateVideoUploadStatus(Realm realm){
        Log.d("uploadedVideoList size",String.valueOf(uploadedVideoList.size()));
        for(int i=0; i<uploadedVideoList.size();i++){
            Toast.makeText(this,"setting the upload value for i"+i+uploadedVideoList.get(i).getVideo_path(),Toast.LENGTH_LONG).show();
            realm.beginTransaction();
            uploadedVideoList.get(i).setUpload_status(true);
            Log.d("for i"+i, String.valueOf(uploadedVideoList.get(i).isUpload_status()));
            realm.commitTransaction();
        }
        List<Video> confirmUploaded = new ArrayList<Video>();
        for(int i=0; i<uploadedVideoList.size();i++){
            if(uploadedVideoList.get(i).isUpload_status())
            {
                Log.d("removing this element :",uploadedVideoList.get(i).getVideo_path());
                confirmUploaded.add(uploadedVideoList.get(i));

            }
        }
        uploadedVideoList.remove(confirmUploaded);
        if(uploadedVideoList.size()>0){
            for(int i=0; i<uploadedVideoList.size();i++){
                Log.d("uploadedVideoList now:",uploadedVideoList.get(i).getVideo_path());
            }

        }
    }

    private void updateImageUploadStatus(Realm realm){
        Log.d("uploadedImageList size",String.valueOf(uploadedImageList.size()));
        for(int i=0; i<uploadedImageList.size();i++){
            Toast.makeText(this,"setting the upload value for i"+i+uploadedImageList.get(i).getImage_path(),Toast.LENGTH_LONG).show();
            realm.beginTransaction();
            uploadedImageList.get(i).setUpload_status(true);
            Log.d("for i"+i, String.valueOf(uploadedImageList.get(i).isUpload_status()));
            realm.commitTransaction();
        }
        List<Image> confirmUploaded = new ArrayList<Image>();
        for(int i=0; i<uploadedImageList.size();i++){
            if(uploadedImageList.get(i).isUpload_status())
            {
                Log.d("removing this element :",uploadedImageList.get(i).getImage_path());
                confirmUploaded.add(uploadedImageList.get(i));

            }
        }
        uploadedImageList.remove(confirmUploaded);
        if(uploadedImageList.size()>0){
            for(int i=0; i<uploadedImageList.size();i++){
                Log.d("uploadedImageList now:",uploadedImageList.get(i).getImage_path());
            }

        }
    }




    /**
     * Select method to choose image: Take from camera or Choose from gallery
     */
    private void selectMedia(){
        final CharSequence[] options = { "Take a Photo","Take a Video","Choose Photo from Gallery", "Choose Video from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add a Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take a Photo"))
                {
                    openCamera();

                }
                else if (options[item].equals("Take a Video"))
                {
                    openVideo();

                }
                else if (options[item].equals("Choose Photo from Gallery"))
                {
                    openGallery();

                }
                else if (options[item].equals("Choose Video from Gallery"))
                {
                    openVideoGallery();

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Take Photo
     */
    private void openCamera(){
        Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, CAM_REQUEST);

    }

    /**
     * Take Video
     */
    private void openVideo(){
        Intent takeVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }

    }


    /**
     * Open Gallery to select photos
     */
    private void openGallery(){
        //Create intent to open image application like Gallery
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Start the intent
        startActivityForResult(Intent.createChooser(gallery, "Select Application"), PICK_IMAGE);
    }

    /**
     * Open Gallery to select videos
     */
    private void openVideoGallery(){
        //Create intent to open image application like Gallery
        Intent gallery_video = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //Start the intent
        startActivityForResult(Intent.createChooser(gallery_video, "Select Application"), PICK_VIDEO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        try {
            if(resultCode == RESULT_OK){
                //When image is taken by camera
                if (requestCode == CAM_REQUEST){
                    Uri imageUri = data.getData();
                    imagePath = findPath(imageUri);
                    File f = new File(imagePath);
                    image_name = f.getName();

                    //Change activity
                    Intent i = new Intent(getApplicationContext(), Tag.class);
                    i.putExtra("imagePath",imagePath);
                    i.putExtra("requestCode","CAMERA");
                    i.putExtra("imageUri", imageUri.toString());
                    i.putExtra("imageName",image_name);
                    startActivity(i);
                }
                else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                    Uri videoUri = data.getData();
                    videoPath = findVideoPath(videoUri);
                    File f = new File(videoPath);
                    video_name = f.getName();

                    //Toast.makeText(this, "entered video capture", Toast.LENGTH_SHORT).show();
                    // mVideoView.setVideoURIvideoUri);
                    //Change activity
                    Intent i = new Intent(getApplicationContext(), TagVideo.class);
                    //Toast.makeText(this, "entered video capture TagVideo", Toast.LENGTH_SHORT).show();
                    i.putExtra("videoPath",videoPath);
                    i.putExtra("requestCode","CAMERA");
                    i.putExtra("videoUri", videoUri.toString());
                    i.putExtra("videoName",video_name);
                    startActivity(i);

                }
                //When an Image is picked
                else if (requestCode == PICK_IMAGE && null != data) {
                    image_name="";
                    Uri imageUri_2 = data.getData();
                    imagePath = findPath(imageUri_2);
                    File f = new File(imagePath);
                    image_name = f.getName();
                    //Change activity
                    Intent i = new Intent(getApplicationContext(), Tag.class);
                    i.putExtra("imagePath",imagePath);
                    i.putExtra("requestCode","GALLERY");
                    i.putExtra("imageUri", imageUri_2.toString());
                    i.putExtra("imageName",image_name);
                    startActivity(i);
                }
                //When a Video is picked
                else if (requestCode == PICK_VIDEO && null != data) {
                    video_name="";
                    Uri videoUri_2 = data.getData();
                    videoPath = findVideoPath(videoUri_2);
                    File f = new File(videoPath);
                    video_name = f.getName();
                    //Change activity
                    Intent i = new Intent(getApplicationContext(), TagVideo.class);
                    //Toast.makeText(this, "entered pick video Tag", Toast.LENGTH_SHORT).show();
                    i.putExtra("videoPath",videoPath);
                    //Log.d("videoPath",videoPath);
                    i.putExtra("requestCode","GALLERY");
                    //Log.d("requestCode", String.valueOf(requestCode));
                    i.putExtra("videoUri", videoUri_2.toString());
                    //Log.d("videoUri",videoUri_2.toString());
                    i.putExtra("videoName",video_name);
                    //Log.d("videoName",video_name);
                    //Toast.makeText(this, "about to start", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    //Toast.makeText(this, "finished start", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            Log.e("MYAPP", "exception", e);
        }



    }

    /**
     * Find the Path of image
     * @param uri
     * @return image path
     */
    private String findPath(Uri uri){
        String imagePath;

        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            imagePath = cursor.getString(columnIndex);
        }else{
            imagePath = uri.getPath();
        }
        return imagePath;
    }
    /**
     * Find the Path of video
     * @param uri
     * @return image path
     */
    private String findVideoPath(Uri uri){
        String videoPath;

        String[] columns = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoPath = cursor.getString(columnIndex);
        }else{
            videoPath = uri.getPath();
        }
        return videoPath;
    }

    /**
     * Verify write,read,camera,and location permission (Needed in Android Version 6 or later)
     * @param activity
     */
    public static void verifyPermissions(Activity activity){
        //Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        //Check if we have camera permission
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        //Check if we have camera permission
        int locationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);


        if(writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED || locationPermission != PackageManager.PERMISSION_GRANTED){
            //We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
        }



    }

    /**
     * Set the pre data into local database
     */
    public void setDatabase(){
        Realm realmtest = Realm.getInstance(this);
        // find the last id number
        RealmResults<Mood_list> check1 =  realmtest.where(Mood_list.class)
                .findAll();
        if(check1.size()==0){
            //set mood_list
            mood_list.add("Astonished");
            mood_list.add("Excited");
            mood_list.add("Happy");
            mood_list.add("Satisfied");
            mood_list.add("Tried");
            mood_list.add("Sad");
            mood_list.add("Miserable");
            mood_list.add("Annoyed");
            mood_list.add("Neutral");
            //set mood database
            Realm realm = Realm.getInstance(this);
            realm.beginTransaction();
            for(int i =1;i<10;i++) {
                Mood_list mood = realm.createObject(Mood_list.class);
                mood.setMood_list_id(i);
                mood.setMood_name(mood_list.get(i-1));
            }

            //set activity
            activity_list.add("Work");
            activity_list.add("Meal");
            activity_list.add("Sports");
            activity_list.add("Travel");
            activity_list.add("School");
            activity_list.add("Shopping");
            activity_list.add("Religious");
            activity_list.add("Leisure");
            activity_list.add("Others");
            //set activity  database
            for(int i =1;i<10;i++) {
                Activity_list activity = realm.createObject(Activity_list.class);
                activity.setActivity_list_id(i);
                activity.setActivity_name(activity_list.get(i-1));
            }

            //set people
            people_list.add("Family");
            people_list.add("Neighbours");
            people_list.add("Spouse");
            people_list.add("Friends");
            people_list.add("Classmates");
            people_list.add("Colleagues");
            people_list.add("Acquaintances");
            people_list.add("Strangers");
            people_list.add("Others");
            //set prople database
            for(int i =1;i<10;i++) {
                People_list people = realm.createObject(People_list.class);
                people.setPeople_list_id(i);
                people.setPeople_name(people_list.get(i-1));
            }

            realm.commitTransaction();
        }
    }

    /*
    * This is the method responsible for image upload
    * We need the full image path and the name for the image in this method
    * */
    public void uploadImage(final Realm realm, final Image tempimage) {
        //getting name for the image
        String name = "";

        //getting the actual path of the image
        String path = tempimage.getImage_path();

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request

            new MultipartUploadRequest(this, uploadId, Constants.UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", name) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            uploadedImageList.add(tempimage);
            updateImageUploadStatus(realm);

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }





    private void uploadVideo(final Realm realm, final Video tempvideo) {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(MainActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                if(s.equals("Could not upload")){
                    //realm.beginTransaction();
                    //tempvideo.setUpload_status(false);
                    //realm.commitTransaction();
                }
                else
                {
                    //realm.beginTransaction();
                    //tempvideo.setUpload_status(true);
                    //realm.commitTransaction();
                    Log.d("enteredpostexecute",tempvideo.getVideo_path());
                    uploadedVideoList.add(tempvideo);
                    updateVideoUploadStatus(realm);
                }
                super.onPostExecute(s);
                uploading.dismiss();



                //textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                //textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.upLoad2Server(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }
}
