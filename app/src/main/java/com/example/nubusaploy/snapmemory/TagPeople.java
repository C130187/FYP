package com.example.nubusaploy.snapmemory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by nubusaploy on 6/28/2016.
 */
public class TagPeople extends Activity {

    //Open Image
    String imagePath,people_x,people_y;
    Uri imageUri;
    ImageView imageView;
    String imgDecodableString;
    String requestCode;
    String imagePath_chosen;
    ExifInterface exif;

    int PEOPLE = 2;

    ImageButton doneButton;
    Button[] btn;
    RelativeLayout layout;
    RelativeLayout.LayoutParams p;

    String people_count = "0";
    int int_people_count;
    String[] people_chosen;
    int user_id;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.people);

        //Hide Keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        layout = (RelativeLayout) findViewById(R.id.show_image_layout);
        p = new RelativeLayout.LayoutParams(
                310, 130
        );


        //Get data from previous activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            //From Tag.java
            //For Image
            imagePath = extras.getString("imagePath");
            imageUri = Uri.parse(extras.getString("imageUri"));
            requestCode = extras.getString("requestCode");
            imagePath_chosen = imagePath;

            //For People
            people_chosen = extras.getStringArray("people_chosen");
            people_count = extras.getString("people_count");
            int_people_count = Integer.parseInt(people_count);
            user_id = extras.getInt("user_id");
        }

        if(int_people_count != 0){
            TextView removeDescription = (TextView) findViewById(R.id.removeDescription);
            removeDescription.setVisibility(View.VISIBLE);
            showPeopleBtn(people_chosen, int_people_count);


        }

        //Open the image
        imageView = (ImageView) findViewById(R.id.photoTagPeople);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    removeButton();
                    //Get x and y when user click the screen
                    people_x = String.valueOf(event.getX());
                    people_y = String.valueOf(event.getY());
                    Intent i = new Intent(getApplicationContext(), TagPeopleByPerson.class);
                    i.putExtra("people_chosen",people_chosen);
                    i.putExtra("people_count",people_count);
                    i.putExtra("people_x",people_x);
                    i.putExtra("people_y",people_y);
                    i.putExtra("user_id",user_id);
                    startActivityForResult(i,PEOPLE );
                }
                return true;
            }
        });
        openImage();


        doneButton = (ImageButton) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //Send data back to Tag.java
                intent.putExtra("people_chosen",people_chosen);
                intent.putExtra("people_count",people_count);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        ImageButton closebtn = (ImageButton) findViewById(R.id.closePeople);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        removeButton();
        showPeopleBtn(people_chosen, int_people_count);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Get the information from TagPeopleByPerson Page
        if (requestCode == PEOPLE) {
            if(resultCode == RESULT_OK){
                people_chosen = data.getStringArrayExtra("people_chosen");
                people_count = data.getStringExtra("people_count");
                int_people_count = Integer.parseInt(people_count);
                showPeopleBtn(people_chosen, int_people_count);
                if(int_people_count != 0){
                    TextView removeDescription = (TextView) findViewById(R.id.removeDescription);
                    removeDescription.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Show all tagged people on button at X,Y coordinate
     * @param people_chosen_
     * @param people_count_
     */
    public void showPeopleBtn(final String[] people_chosen_, final int people_count_){

        btn = new Button[people_count_];

        float peopleX,peopleY;


        for(int i=0; i < people_count_; i++){

            final int Index = i;

            peopleX = Float.parseFloat(people_chosen_[(Index*4)+1]);
            peopleY = Float.parseFloat(people_chosen_[(Index*4)+2]);

            btn[Index] = new Button(this);
            btn[Index].setText(people_chosen_[Index*4]);
            btn[Index].setX(peopleX-155);
            btn[Index].setY(peopleY);
            btn[Index].setBackgroundColor(getResources().getColor(R.color.buttonTrans2));
            btn[Index].setTextColor(getResources().getColor(R.color.heading));
            btn[Index].setId(Index);
            btn[Index].setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            btn[Index].setLayoutParams(p);
            //If user delete tagged person, change all values of the button to "-1"
            btn[Index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn[Index].getId() == ((Button) v).getId()){

                        people_chosen_[Index*4] = "-1";
                        people_chosen_[(Index*4)+1] = "-1";
                        people_chosen_[(Index*4)+2] = "-1";
                        people_chosen_[(Index*4)+3] = "-1";

                        layout.removeView(btn[btn[Index].getId()]);
                    }
                }
            });

            layout.addView(btn[Index]);
            float x = btn[Index].getX();
            String s = ""+x;
            float y = btn[Index].getY();
            //Don't show the delete button
            //x == -156 because -1-155
            if(x == -156 && y == -1){
                layout.removeView(btn[Index]);
            }


        }
    }

    /**
     * Remove all button before show the new one
     */
    public void removeButton() {
        //Remove when open this page again
        for(int i = 0; i < int_people_count; i++){
            final int Index = i;
            layout.removeView(btn[Index]);
        }
    }

    /**
     * Open the chosen image
     */
    public void openImage(){

        try {
            //When image is taken by camera
            if (requestCode.equals("CAMERA")){

                //imageView.setImageDrawable(Drawable.createFromPath(path));
                //Uri imageUri = data.getData();
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap imageData = BitmapFactory.decodeFile(imagePath, options);
                Matrix matrix = new Matrix();
                int rotateImage = getCameraPhotoOrientation(this, imageUri, imagePath);
                if (rotateImage != 0){
                    matrix.postRotate(rotateImage);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), matrix, true);
                    Bitmap resizedBitmap = getResizedBitmap(rotatedBitmap, 4096);
                    imageView.setImageBitmap(resizedBitmap);

                    imageData.recycle();

                }else{
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
                if (rotateImage != 0){

                    matrix.postRotate(rotateImage);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), matrix, true);
                    Bitmap resizedBitmap = getResizedBitmap(rotatedBitmap, 4096);
                    imageView.setImageBitmap(resizedBitmap);
                    imageData.recycle();

                }else{
                    matrix.postRotate(0);
                    Bitmap createdBitmap = Bitmap.createBitmap(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), matrix, true);
                    Bitmap resizedBitmap = getResizedBitmap(createdBitmap, 4096);
                    //Set the Image in ImageView after decoding the String
                    imageView.setImageBitmap(resizedBitmap);

                }


            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Check the orientation of photo
     * Then find the rotate angle
     * @param context
     * @param imageUri
     * @param imagePath
     * @return rotat angle
     */
    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
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


}