package com.tricloudcommunications.ce.flickrapiapp;

import android.graphics.Bitmap;
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

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

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


        FlickrAPIDataDownload getFlickrDataTask = new FlickrAPIDataDownload();
        //getFlickrDataTask.execute("http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=65dd98b8db61ede0ad592cb221e31201&tags=sun&media=photos&per_page=1&page=1&format=json");
        getFlickrDataTask.execute("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=65dd98b8db61ede0ad592cb221e31201&tags=sun&media=photos&per_page=1&page=1&format=json");
    }

    public class FlickrAPIDataDownload extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            //Source1: https://www.codota.com/android/scenarios/52fcbd97da0ab8e225ec74c6/javax.net.ssl.HttpsURLConnection?tag=dragonfly
            //Source2: http://stackoverflow.com/questions/16504527/how-to-do-an-https-post-from-android
            String result = "";

            try{
                URL url = new URL(params[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String input = params[0];
                Log.i("Appygram Example","Sending: "+input);
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();
                os.close();

                InputStream is = conn.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data !=-1){

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }

                Log.i("Appygram Example","Appygram sent with result "+result);

                return result;

            } catch (IOException x) {
                Log.e("Appygram Example","Error sending appygram", x);
            }

            return null;


        }
    }

    public class FlickrImageDownloader extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... params) {
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
