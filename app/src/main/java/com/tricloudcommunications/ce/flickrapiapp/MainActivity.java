package com.tricloudcommunications.ce.flickrapiapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    String flickrPhotoID = "";
    String flickrPhotoOwnerID = "";
    String flickrPhotoFarmID = "";
    String flickrPhotoServerID = "";
    String flickrPhotoSecret = "";




    public void setGlobalVariables(
            String flickrPhotoID,
            String flickrPhotoOwnerID,
            String flickrPhotoFarmID,
            String flickrPhotoServerID,
            String flickrPhotoSecret
            ){

        //Log.i("Photo ID", photoID);

        //String values of all the data we want from the JSON response
       //flickrPhotoID = photoID;
       //flickrPhotoOwnerID = photOwnerID;

        //FlickrImageDownloader flickrImageDownloader = new FlickrImageDownloader();
        //flickrImageDownloader.execute("http://www.flickr.com/photos/"+photoOwnerID+"/"+photoID);

        //start and execute the ImageDownloader() class to download the weather icon image
        FlickrImageDownloader downloadImageTask = new FlickrImageDownloader();
        Bitmap myImage;

        try {

            //myImage = downloadImageTask.execute("http://openweathermap.org/img/w/"+iconImageName+".png").get();
            //myImage = downloadImageTask.execute("http://openweathermap.org/img/w/10d.png").get();
            //myImage = downloadImageTask.execute("https://www.flickr.com/photos/"+photoOwnerID+"/"+photoID).get();
            myImage = downloadImageTask.execute("https://farm"+flickrPhotoFarmID+".staticflickr.com/"+flickrPhotoServerID+"/"+flickrPhotoID+"_"+flickrPhotoSecret+".jpg").get();

            Drawable drawable = new BitmapDrawable(getResources(), myImage);
            relativeLayout.setBackground(drawable);



        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        relativeLayout = (RelativeLayout) findViewById(R.id.content_main);

        /*
         Source1: https://www.flickr.com/services/api/explore/flickr.photos.search
         Source2: https://www.flickr.com/services/api/misc.urls.html
         Note: To get back a clean JSON response (no extra text in the front or back)-
         Make sure that you 'Do not sign call' by adding this to the end of the url string '&format=json&nojsoncallback=1'
        */
        FlickrAPIDataDownload getFlickrDataTask = new FlickrAPIDataDownload();
        //getFlickrDataTask.execute("http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=65dd98b8db61ede0ad592cb221e31201&tags=sun&media=photos&per_page=1&page=1&format=json");
        getFlickrDataTask.execute("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=65dd98b8db61ede0ad592cb221e31201&tags=sun&media=photos&per_page=1&page=1&format=json&nojsoncallback=1");
    }

    public class FlickrAPIDataDownload extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            relativeLayout.setBackgroundColor(Color.DKGRAY);

        }

        @Override
        protected String doInBackground(String... params) {

            /*
               Note: the code below from the source below is for making a HTTPS connection and then reading the response
               from that secure connection. In this case it's for Flickr but can be used in any other case where a secure
               Https connection needs to be establish before communicating with a host.
               Source1: https://www.codota.com/android/scenarios/52fcbd97da0ab8e225ec74c6/javax.net.ssl.HttpsURLConnection?tag=dragonfly
               Source2: http://stackoverflow.com/questions/16504527/how-to-do-an-https-post-from-android
            */

            String result = "";

            try{
                //Set up Https request
                URL url = new URL(params[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String input = params[0];
                Log.i("Flickr API Content","Sending: "+input);
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
                os.close();

                //Read the data from the response
                InputStream is = conn.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data !=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                //Log.i("Flickr API Content","API call sent with result "+result);
                return result;

            } catch (IOException e) {
                //Log.e("Flickr API Content","Error sending API call", e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);



            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONObject photoObject = jsonObject.getJSONObject("photos");
                JSONArray photoArray = photoObject.getJSONArray("photo");

                /*
                NOTE:
                  We have only one photo in this exercise, if we wanted or had more photos
                  the we would use the array key number to get the photo with that key

                  Take a look at the chosen answer from this source:
                  http://stackoverflow.com/questions/19205527/json-parsing-in-android-flickr-api
                  Example of JSON for more than one Image in the photo array
                    JSONObject json = new JSONObject(jsonString);
                    JSONObject photos = json.getJSONObject("photos");
                    JSONArray photo = photos.getJSONArray("photo");
                    if (photo.length() > 0) {
                        JSONObject first = photo.getJSONObject(0);
                        String picOwner = first.getString("owner");
                        String picID = first.getString("id");
                    }
                */

                JSONObject flickerPhotos = photoArray.getJSONObject(0);

                flickrPhotoID = flickerPhotos.getString("id");
                flickrPhotoOwnerID = flickerPhotos.getString("owner");
                flickrPhotoFarmID = flickerPhotos.getString("farm");
                flickrPhotoServerID = flickerPhotos.getString("server");
                flickrPhotoSecret = flickerPhotos.getString("secret");


                setGlobalVariables(flickrPhotoID,flickrPhotoOwnerID,flickrPhotoFarmID,flickrPhotoServerID,flickrPhotoSecret);

                Log.i("Photo Object ID", flickrPhotoID);
                Log.i("Photo Object Owner", flickrPhotoOwnerID);
                Log.i("Photo Object farm", flickrPhotoFarmID);
                Log.i("Photo Object server", flickrPhotoServerID);
                Log.i("Photo Object secret", flickrPhotoSecret);


            } catch (JSONException e) {

                e.printStackTrace();

            }

            //Log.i("On Post Execute", result);
        }
    }

    public class FlickrImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            String result;
            URL url;
            HttpURLConnection urlConnection;

            try {

                //Set up Https request
                url = new URL(urls[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String input = urls[0];
                Log.i("Flickr API Content","Sending: "+input);
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
                os.close();

                InputStream inputStream = conn.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
