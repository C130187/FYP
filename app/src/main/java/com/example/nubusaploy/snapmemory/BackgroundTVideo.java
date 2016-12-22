package com.example.nubusaploy.snapmemory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by saowaga on 7/14/2016.
 *
 *
 */
public class BackgroundTVideo extends AsyncTask<String,Void, String> {

    Context ctx;
    String video_id = "";
    BackgroundTVideo(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... params) {
        //save_url = ip of server/php file
        String save_url = "http:// 192.168.1.3/MyLife/albums_browser/video_metadata.php";
        //String save_url = "http://10.27.87.200/MyLife/albums_browser/video_metadata.php";//10.0.2.2 in emulator
        //String save_url = "http://192.168.0.102/MyLife/albums_browser/video_metadata.php";
       // String save_url = "http://172.22.186.22/MyLife/albums_browser/video_metadata.php";//10.0.2.2 in emulator
        String method = params[0];
        if(method.equals("tag_video")){
            //Get value from Tag Page


            if(android.os.Debug.isDebuggerConnected())
            {
                android.os.Debug.waitForDebugger();
            }
            //Log.d("entered lah", "very happy lah");

            String event_date = params[1];
            String event_time = params[2];
            String event_mood = params[3];
            String event_description = params[4];
            String event_location = params[5];
            String latitude = params[6];
            String longitude = params[7];
            String filepath = params[8];
            String event_people = params[9];
            String event_update = params[10];

            try {
                //Connect to server and send data to php file
                //Log.d("sending leh","sending");
                while(!seeIfConnected()){
                    Log.d("not connected","not connected");
                }
                //Log.d("connected","connected");
                URL url = new URL(save_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("event_date", "UTF-8") + "="+URLEncoder.encode(event_date,"UTF-8")+"&"+
                        URLEncoder.encode("event_time","UTF-8") + "=" + URLEncoder.encode(event_time, "UTF-8")+"&"+
                        URLEncoder.encode("event_people","UTF-8") + "=" + URLEncoder.encode(event_people, "UTF-8")+"&"+
                        URLEncoder.encode("event_mood","UTF-8") + "=" + URLEncoder.encode(event_mood, "UTF-8")+"&"+
                        URLEncoder.encode("event_description","UTF-8") + "=" + URLEncoder.encode(event_description, "UTF-8")+"&"+
                        URLEncoder.encode("event_location","UTF-8") + "=" + URLEncoder.encode(event_location, "UTF-8")+"&"+
                        URLEncoder.encode("latitude","UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8")+"&"+
                        URLEncoder.encode("longitude","UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8")+"&"+
                        URLEncoder.encode("filepath","UTF-8") + "=" + URLEncoder.encode(filepath, "UTF-8")+"&"+
                        URLEncoder.encode("event_update","UTF-8") + "=" + URLEncoder.encode(event_update, "UTF-8");;


                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                //Log.d("Sent leh","sent leh");
                InputStream IS = httpURLConnection.getInputStream();

                IS.close();

                httpURLConnection.disconnect();


                return  video_id;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {

    }
    public boolean seeIfConnected() {
        ConnectivityManager connMngr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return connMngr.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (NullPointerException npe) {
            return false;

        }
    }

}