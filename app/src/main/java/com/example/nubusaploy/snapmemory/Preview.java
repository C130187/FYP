package com.example.nubusaploy.snapmemory;

import android.app.Activity;
import android.content.Context;
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
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by nubusaploy on 6/27/2016.
 */
public class Preview extends Activity {

    String imagePath;
    Uri imageUri;
    ImageView imageView;
    String imgDecodableString;
    String requestCode;
    String imagePath_chosen;
    ExifInterface exif;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.photo_enlarge);

            //Get data from previous activity
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                imagePath = extras.getString("imagePath");
            imageUri = Uri.parse(extras.getString("imageUri"));
            requestCode = extras.getString("requestCode");
            imagePath_chosen = imagePath;
        }


        imageView = (ImageView) findViewById(R.id.previewImage);

        openImage();

        ImageButton closebtn = (ImageButton) findViewById(R.id.closePreview);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void openImage(){

        try {
            //When image is taken by camera
            if (requestCode.equals("CAMERA")){


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
                    imageView.setImageBitmap(resizedBitmap);
                    imageData.recycle();

                }
            }
            //When an Image is picked
            else if (requestCode.equals("GALLERY")) {

                //Get the image from data
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
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
                    imageView.setImageBitmap(resizedBitmap);

                }


            } else {
                Toast.makeText(this, "You haven't picked the image", Toast.LENGTH_LONG).show();
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
     * @return rotate angle
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
