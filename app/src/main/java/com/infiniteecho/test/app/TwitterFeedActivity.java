package com.infiniteecho.test.app;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by umesh on 4/30/14.
 */
public class TwitterFeedActivity extends ListActivity  {


    private Context mContext;
    private List<String> fileList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        CreateFileHashAsync createFileHashAsync = new CreateFileHashAsync();
        createFileHashAsync.execute();





    }


    private class LoadTweetsAsync extends AsyncTask<Void, Void, Void> {
       // private Context mContext;
        private List<String> tweetList = new ArrayList<String>();

//        public LoadTweetsAsync(Context context) {
//            mContext = context;
//        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Void doInBackground(Void... params) {
            //do your work here


            File sdcard = Environment.getExternalStorageDirectory();

            //Get the text file
            File file = new File(sdcard,"5kb.csv");

            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {

                    tweetList.add(line);
                }
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
               e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            setListAdapter(new TwitterFeedListAdapter(mContext, tweetList, fileList));

            // do something with data here-display it or send to mainactivity

        }
    }


    private class CreateFileHashAsync extends AsyncTask<Void, Void, Void> {



        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Void doInBackground(Void... params) {
            //do your work here


            File sdcard = Environment.getExternalStorageDirectory();

            //Get the text file
            File file = new File(sdcard,"a");

            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {

                    fileList.add(line);
                }
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
     // do something with data here-display it or send to mainactivity
            LoadTweetsAsync loadTweetsAsync = new LoadTweetsAsync();
            loadTweetsAsync.execute();
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

    }
}
