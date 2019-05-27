package com.spotify.sdk.android.authentication.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FilterActivity extends AppCompatActivity {
    ProgressDialog PlaylistDialog;
    FloatingActionButton fab;
    Context ctx = FilterActivity.this;

    String mAccessToken;
    String Href;
    String Name;
    String userId;

    int min;
    int max;

    int seen = 0;

    int Count;
    int CountLeft;
    int CountOffset = 0;

    List trackID = new ArrayList();
    List trackName = new ArrayList();
    List trackImageURL = new ArrayList();

    List filteredTrackIds = new ArrayList();
    List filteredBPMs = new ArrayList();
    List filteredTrackName = new ArrayList();
    List filterTrackImageURL = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Intent intent = getIntent();
        mAccessToken = intent.getExtras().getString("Token");
        Href = intent.getExtras().getString("Href");
        Name = intent.getExtras().getString("Name");
        Count = Integer.valueOf(intent.getExtras().getString("Count"));
        userId = intent.getExtras().getString("userId");
        CountLeft = Count;
        this.setTitle(Name);

        fab = findViewById(R.id.floatingActionButton);
        fab.hide();
    }

    public void filterPlaylist(View view) {
        Button filterButton = findViewById(R.id.filterButton);
        EditText minEdit = findViewById(R.id.minEdit);
        EditText maxEdit = findViewById(R.id.maxEdit);

        min = Integer.valueOf(String.valueOf(minEdit.getText()));
        max = Integer.valueOf(String.valueOf(maxEdit.getText()));

        if(min >= max){
            Toast.makeText(this, "Please ensure min is less than max", Toast.LENGTH_SHORT).show();
        }
        else{
            new getTracks().execute(mAccessToken);
            minEdit.setVisibility(View.GONE);
            maxEdit.setVisibility(View.GONE);
            filterButton.setVisibility(View.GONE);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void createPlaylist(View view) {

    }

    // Get List of Tracks & insert into trackID
    private class getTracks extends AsyncTask<String, Void, JSONObject> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PlaylistDialog = new ProgressDialog(ctx, R.style.AppCompatAlertDialogStyle);
            PlaylistDialog.setTitle("Filtering Playlist");
            PlaylistDialog.setMessage("Loading...");
            PlaylistDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String mAccessToken = strings[0];
            String urlRequest = Href + "/tracks";
            final Request request = new Request.Builder()
                    .url(urlRequest)
                    .addHeader("Authorization","Bearer " + mAccessToken)
                    .build();
            try{
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject playlistJson = new JSONObject(jsonData);
                return playlistJson;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject tracksOBJ) {
            super.onPostExecute(tracksOBJ);
            // Log.d("Tracks JSON", tracksOBJ.toString());
            storeTrackInfo(tracksOBJ);
            if(CountLeft > 0){ // more playlists to find
                CountLeft = CountLeft - 100;
                CountOffset = CountOffset + 100;
                // Log.d("100+ tracks", String.valueOf(CountOffset));
                new FilterActivity.getMoreTracks().execute(mAccessToken);
            }
            else{ // No more playlists to find
                new requestBPM().execute(trackID);
            }
        }
    }
    private class getMoreTracks extends AsyncTask<String, Void, JSONObject> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String mAccessToken = strings[0];
            String urlRequest = Href + "/tracks?offset=" + CountOffset;
            final Request request = new Request.Builder()
                    .url(urlRequest)
                    .addHeader("Authorization","Bearer " + mAccessToken)
                    .build();
            try{
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject playlistJson = new JSONObject(jsonData);
                return playlistJson;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject tracksOBJ) {
            super.onPostExecute(tracksOBJ);
            //Log.d("Tracks JSON", tracksOBJ.toString());
            storeTrackInfo(tracksOBJ);
            if(CountLeft > 0){ // more playlists to find
                CountLeft = CountLeft - 100;
                CountOffset = CountOffset + 100;
                // Log.d("100+ tracks", String.valueOf(CountOffset));
                new FilterActivity.getMoreTracks().execute(mAccessToken);
            }
            else{ // No more playlists to find
                new requestBPM().execute(trackID);
            }
        }
    }

    private void analyzeBPM(JSONObject tracksOBJ) {
        // Log.d("Currently", "in analyze BPM");

    }

    private void storeTrackInfo(JSONObject tracksOBJ) {
        try {
            JSONObject reader = new JSONObject(String.valueOf(tracksOBJ));
            JSONArray items = reader.getJSONArray("items");

            for(int i = 0; i < 100 && i < CountLeft; i++){
                JSONObject track = items.getJSONObject(i);
                JSONObject trackInfo = track.getJSONObject("track");
                trackID.add(trackInfo.getString("id"));
                trackName.add(trackInfo.getString("name"));
                trackImageURL.add(trackInfo.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"));
                // Log.d("trackID " + (i + CountOffset), trackInfo.getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class requestBPM extends AsyncTask<List, Void, List>{
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(List... lists) {
            List playlistSongID = lists[0];
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < Count; i++){
                sb.append(playlistSongID.get(i));
                sb.append(",");
                if(i != 0 && i % 99 == 0 || i == Count - 1){ // every 100th value or end of the playlist
                    String urlRequest = "https://api.spotify.com/v1/audio-features?ids=" + sb;
                    final Request request = new Request.Builder()
                            .url(urlRequest)
                            .addHeader("Authorization","Bearer " + mAccessToken)
                            .build();
                    try { // try to build audio features from id list
                        Response response = client.newCall(request).execute();
                        String jsonData = response.body().string();
                        JSONObject playlistJson = new JSONObject(jsonData);
                        if(playlistJson.has("audio_features")){
                            try{
                                JSONArray audioFeatures = playlistJson.getJSONArray("audio_features");
                                int currentCount = audioFeatures.length();
                                for(int j = 0; j < currentCount; j++){
                                    seen++;

                                    JSONObject features = audioFeatures.getJSONObject(j);
                                    double trackBPM = features.getDouble("tempo");
                                    if(min < trackBPM && trackBPM < max){
                                        filteredBPMs.add(trackBPM);
                                        filteredTrackIds.add(trackID.get(j));
                                        filteredTrackName.add(trackName.get(j));
                                        filterTrackImageURL.add(trackImageURL.get(j));

                                        Log.d(trackName.get(j) + " is " + j , String.valueOf(trackBPM));
                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        /*

                        // Lol let's comment this out
                        // and see if the app crashes
                        // if not get rid of else bit
                        // because i'm a meme 5/15/19
                        //         - Alex


                        else{ // In Case no Features found
                            Log.d("FAILED", "No Audio Features");
                            // SINGLE REQUEST
                            for(int z = i - 100; z < i; z++){ // Loop tracks individually
                                String singleUrl = "https://api.spotify.com/v1/audio-features/" + trackID.get(z);
                                final Request singleRequest = new Request.Builder()
                                        .url(singleUrl)
                                        .addHeader("Authorization","Bearer " + mAccessToken)
                                        .build();
                                try{
                                    Response singleResponse = client.newCall(singleRequest).execute();
                                    String stringSingleJSON = singleResponse.body().string();
                                    JSONObject singleJSON = new JSONObject(stringSingleJSON);
                                    Log.d("SINGLEREQ" + z, String.valueOf(singleJSON.getString("tempo")));
                                }catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        */
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    sb = new StringBuilder();
                }

            }
            return null;
        }

        // dismiss ProgressDialog, display tracks, show FAB
        @Override
        protected void onPostExecute(List tracksOBJ) {
            super.onPostExecute(tracksOBJ);
            PlaylistDialog.dismiss();
            displayTracks();
            fab.show();
        }
    }

    // Called after async Filter function
    private void displayTracks() {
        ScrollView filterScroll = findViewById(R.id.trackScroll);
        filterScroll.setVisibility(View.VISIBLE);
        LinearLayout scroll = findViewById(R.id.trackGallery);

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        int size = filteredTrackIds.size();

        Log.d("SIZE", String.valueOf(size));

        for(int i = 0; i < size; i++){
            // Log.d("display object" + i, String.valueOf(filteredBPMs.get(i)) + " "+ String.valueOf(filteredTrackIds.get(i)));
            View view = layoutInflater.inflate(R.layout.itemtrack, scroll, false);

            TextView playlistTitle = view.findViewById(R.id.tracktitle);
            playlistTitle.setText((CharSequence) filteredTrackName.get(i));
            Log.d("track " + i, String.valueOf(filteredTrackName.get(i)));

            TextView playlistCount = view.findViewById(R.id.trackBPM);
            playlistCount.setText(String.valueOf(filteredBPMs.get(i)));

            ImageDownloader imageDownloader = new ImageDownloader();
            try {
                ByteArrayOutputStream outputStream = imageDownloader.execute(i).get();
                Log.d("TrackTitle", String.valueOf(filteredTrackName.get(i)));
                if (outputStream.toByteArray() != null) {
                    byte[] imageByte = outputStream.toByteArray();
                    Bitmap image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

                    ImageView playlistImage = view.findViewById(R.id.trackItemImage);
                    playlistImage.setImageBitmap(image);
                }

                scroll.addView(view);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        double percentfound =  100 * ((double) size / (double) Count);
        Log.d("Percent Found", String.valueOf(percentfound ));
        Toast.makeText(ctx, "Found " + size + " songs, " + percentfound + "% are in range", Toast.LENGTH_LONG).show();

        double percentSeen = 100 * ((double) seen / (double) Count);
        Log.d("Percent Looked At", String.valueOf(percentSeen));
    }

    private class ImageDownloader extends AsyncTask<Integer, Void, ByteArrayOutputStream> {

        @Override
        protected ByteArrayOutputStream doInBackground(Integer... index) {
            try {
                //get URL of images in filterTrackImageURL
                URL url = new URL((String) filterTrackImageURL.get(index[0]));

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(1000);
                con.setReadTimeout(1000);
                con.setRequestMethod("GET");
                con.connect();
                InputStream is = con.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);

                return outputStream;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
