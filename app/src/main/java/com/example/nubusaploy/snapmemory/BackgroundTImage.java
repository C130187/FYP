package com.example.nubusaploy.snapmemory;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by HP on 21-12-2016.
 */
public class BackgroundTImage extends AsyncTask<String,Void, String> {


    Context ctx;
    String image_id = "";
    BackgroundTImage(Context ctx){
        this.ctx = ctx;
    }
    ArrayList<String> people = new ArrayList<String>();
    ArrayList<String> x = new ArrayList<String>();
    ArrayList<String> y = new ArrayList<String>();
    String people_chosen_string;
    int people_count;
    String user_id;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... params) {
        //save_url = ip of server/php file
        //String save_url = "http://10.27.117.155/snapMemory/save_image.php";//10.0.2.2 in emulator
        String save_url = "http://192.168.1.5/MyLife/albums_browser/image_metadata.php";//10.0.2.2 in emulator
        String method = params[0];
        if(method.equals("tag_image")){
            //Get value from Tag Page
            String event_date = params[1];
            String event_time = params[2];
            String event_mood = params[3];
            String event_description = params[4];
            String event_location = params[5];
            String latitude = params[6];
            String longitude = params[7];
            String filepath = params[8];
            people_chosen_string = params[9];
            people_count = Integer.parseInt(params[10]);
            String event_update = params[11];
            splitPeopleChosenToPeopleArray(people_chosen_string);

            if(android.os.Debug.isDebuggerConnected())
            {
                android.os.Debug.waitForDebugger();
            }
            //Log.d("entered lah", "very happy lah");


            try {
                //Connect to server and send data to php file
                URL url = new URL(save_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("event_date", "UTF-8") + "="+URLEncoder.encode(event_date,"UTF-8")+"&"+
                        URLEncoder.encode("event_time","UTF-8") + "=" + URLEncoder.encode(event_time, "UTF-8")+"&"+
                        URLEncoder.encode("event_mood","UTF-8") + "=" + URLEncoder.encode(event_mood, "UTF-8")+"&"+
                        URLEncoder.encode("event_description","UTF-8") + "=" + URLEncoder.encode(event_description, "UTF-8")+"&"+
                        URLEncoder.encode("event_location","UTF-8") + "=" + URLEncoder.encode(event_location, "UTF-8")+"&"+
                        URLEncoder.encode("latitude","UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8")+"&"+
                        URLEncoder.encode("longitude","UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8")+"&"+
                        URLEncoder.encode("filepath","UTF-8") + "=" + URLEncoder.encode(filepath, "UTF-8")+"&"+
                        URLEncoder.encode("event_update","UTF-8") + "=" + URLEncoder.encode(event_update, "UTF-8");;

                if(people_count > 0){
                    for(int i = 0;i<people.size();i++){
                        data+= "&"+URLEncoder.encode("people"+i,"UTF-8") + "=" + URLEncoder.encode(people.get(i), "UTF-8")+"&"+
                                URLEncoder.encode("x"+i,"UTF-8") + "=" + URLEncoder.encode(x.get(i), "UTF-8")+"&"+
                                URLEncoder.encode("y"+i,"UTF-8") + "=" + URLEncoder.encode(y.get(i), "UTF-8");
                    }
                }



                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();

                IS.close();
                httpURLConnection.disconnect();


                return  image_id;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Split people_chosen_string into 3 ArrayList(people, x, y)
     * @param people_chosen
     */
    public void splitPeopleChosenToPeopleArray(String people_chosen){
        ArrayList<String> people = new ArrayList<String>();
        ArrayList<String> x = new ArrayList<String>();
        ArrayList<String> y = new ArrayList<String>();
        String people_chosen_[] = people_chosen.split(",",people_count*3);

        if (people_chosen_ != null) {
            for(int i=0;i<people_chosen_.length;i++){
                if(i%3==0&&(!people_chosen_[i].equals("-1"))){
                    people.add(people_chosen_[i]);
                }else if(i%3==1&&(!people_chosen_[i].equals("-1"))){
                    x.add(people_chosen_[i]);
                }else if(i%3==2&&(!people_chosen_[i].equals("-1"))){
                    y.add(people_chosen_[i]);
                }
            }
        }

    }




    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {

    }


}
