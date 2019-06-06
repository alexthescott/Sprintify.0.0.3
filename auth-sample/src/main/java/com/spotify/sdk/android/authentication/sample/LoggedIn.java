package com.spotify.sdk.android.authentication.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class LoggedIn extends AppCompatActivity {
    final String pref = "SPRINTIFY_PREF";

    PlaylistDB playlistDB;
    PlaylistAdapter playlistAdapter;
    Context ctx = LoggedIn.this;
    ProgressDialog PlaylistDialog;

    // List playlistSnapshot_ID = new ArrayList();
    List playlistNames = new ArrayList();
    List playlistImageURL = new ArrayList();
    List playlistHref = new ArrayList();
    List playlistTrackTotal = new ArrayList();
    List playlistId = new ArrayList();
    List playlistSnap = new ArrayList();

    String mAccessToken;
    String userID;

    int numberPlaylists;
    int numberPlaylistsLeft;
    int numberOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("Sprintify");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        playlistDB = new PlaylistDB(this, "PL_IMAGE_DATABASE", null, 1);
        Intent intent = getIntent();
        mAccessToken = intent.getExtras().getString("Token");

        new GetPlaylistJson().execute(mAccessToken);
        new getUserID().execute(mAccessToken);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.hide();
    }

    public void refreshPlaylists(View view) {
        setContentView(R.layout.activity_logged_in);

        playlistNames.clear();
        playlistImageURL.clear();
        playlistHref.clear();
        playlistTrackTotal.clear();
        playlistId.clear();
        playlistSnap.clear();

        new GetPlaylistJson().execute(mAccessToken);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences(pref, MODE_PRIVATE).edit();
        editor.putInt("loggedIn", 1);
        editor.apply();
    }

    private class GetPlaylistJson extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PlaylistDialog = new ProgressDialog(ctx, R.style.AppCompatAlertDialogStyle);
            PlaylistDialog.setTitle("Retrieving Playlist");
            PlaylistDialog.setMessage("Loading...");
            PlaylistDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            String mAccessToken = strings[0];
            final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/playlists?limit=50")
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
        protected void onPostExecute(JSONObject playlistOBJ) {
            super.onPostExecute(playlistOBJ);
            Log.d("JSON", playlistOBJ.toString());
            getNumberOfPlaylists(playlistOBJ);
            getPlaylistNames(playlistOBJ);
        }
    }

    private class GetMorePlaylistJSON extends AsyncTask<String, Void, JSONObject> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String mAccessToken = strings[0];
            String url = "https://api.spotify.com/v1/me/playlists?limit=50&offset=" + numberOffset;
            final Request request = new Request.Builder()
                    .url(url)
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
        protected void onPostExecute(JSONObject playlistOBJ) {
            super.onPostExecute(playlistOBJ);
            getPlaylistNames(playlistOBJ);
        }
    }

    private class getUserID extends AsyncTask<String, Void, JSONObject> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            mAccessToken = strings[0];
            OkHttpClient client =  new OkHttpClient();
            final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();
            try{
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject userJson = new JSONObject(jsonData);
                return userJson;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject userObj) {
            super.onPostExecute(userObj);
            try {
                userID = userObj.getString("id");
                Log.d("userID", userID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ImageDownloader extends AsyncTask<Integer, Void, ByteArrayOutputStream> {

        @Override
        protected ByteArrayOutputStream doInBackground(Integer... index) {
            try {
                //get URL of images in playlistImageURL
                URL url = new URL((String) playlistImageURL.get(index[0]));

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

    private void showPlaylists() {
        for(int i = 0; i < playlistNames.size(); i++){
            // playlist not in DataBase
            if (!(playlistDB.checkID((String) playlistId.get(i)))) {
                ImageDownloader imageDownloader = new ImageDownloader();
                try {
                    ByteArrayOutputStream outputStream = imageDownloader.execute(i).get();
                    Log.d("PlaylistTitle", String.valueOf(playlistNames.get(i)));
                    if (outputStream.toByteArray() != null) {
                        byte[] imageByte = outputStream.toByteArray();
                        playlistDB.insert((String) playlistId.get(i), (String) playlistSnap.get(i), (String) playlistImageURL.get(i), imageByte);
                    }
                    // scroll.addView(view);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // playlist
            } else if (!(playlistDB.checkSnap((String) playlistId.get(i), (String) playlistSnap.get(i)))) {
                ImageDownloader imageDownloader = new ImageDownloader();
                try {
                    ByteArrayOutputStream outputStream = imageDownloader.execute(i).get();
                    Log.d("PlaylistTitle", String.valueOf(playlistNames.get(i)));
                    if (outputStream.toByteArray() != null) {
                        byte[] imageByte = outputStream.toByteArray();
                        playlistDB.delete((String) playlistId.get(i));
                        playlistDB.insert((String) playlistId.get(i), (String) playlistSnap.get(i), (String) playlistImageURL.get(i), imageByte);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Playlist Found
            }
        }

        for(int i = 0; i < playlistNames.size(); i++){
            // playlist not in DataBase
            if (!(playlistDB.checkID((String) playlistId.get(i)))) {
                ImageDownloader imageDownloader = new ImageDownloader();
                try {
                    ByteArrayOutputStream outputStream = imageDownloader.execute(i).get();
                    Log.d("PlaylistTitle", String.valueOf(playlistNames.get(i)));
                    if (outputStream.toByteArray() != null) {
                        byte[] largeImageByte = outputStream.toByteArray();
                        Bitmap largeImage = BitmapFactory.decodeByteArray(largeImageByte, 0, largeImageByte.length);
                        Bitmap image = Bitmap.createScaledBitmap(largeImage, 100, 100, false);
                        ByteArrayOutputStream compress = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 10, compress);
                        byte[] imageByte = compress.toByteArray();
                        playlistDB.insert((String) playlistId.get(i), (String) playlistSnap.get(i), (String) playlistImageURL.get(i), imageByte);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // playlist
            } else if (!(playlistDB.checkSnap((String) playlistId.get(i), (String) playlistSnap.get(i)))) {
                ImageDownloader imageDownloader = new ImageDownloader();
                try {
                    ByteArrayOutputStream outputStream = imageDownloader.execute(i).get();
                    Log.d("PlaylistTitle", String.valueOf(playlistNames.get(i)));
                    if (outputStream.toByteArray() != null) {
                        byte[] largeImageByte = outputStream.toByteArray();
                        Bitmap largeImage = BitmapFactory.decodeByteArray(largeImageByte, 0, largeImageByte.length);
                        Bitmap image = Bitmap.createScaledBitmap(largeImage, 100, 100, false);
                        ByteArrayOutputStream compress = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 10, compress);
                        byte[] imageByte = compress.toByteArray();
                        playlistDB.delete((String) playlistId.get(i));
                        playlistDB.insert((String) playlistId.get(i), (String) playlistSnap.get(i), (String) playlistImageURL.get(i), imageByte);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        playlistAdapter = new PlaylistAdapter(LoggedIn.this, playlistNames, playlistTrackTotal, playlistId, playlistHref, userID, mAccessToken);
        RecyclerView recyclerView = findViewById(R.id.playlist_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(playlistAdapter);
        PlaylistDialog.dismiss();
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.show();
    }

    private void getNumberOfPlaylists(JSONObject playlistOBJ) {
        try{
            JSONObject reader = new JSONObject(String.valueOf(playlistOBJ));
            numberPlaylists = reader.getInt("total");
            numberPlaylistsLeft = numberPlaylists;
            numberOffset = 0;

            Log.d("numberPlaylists", String.valueOf(numberPlaylists));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String[] getPlaylistNames(JSONObject playlistData){
        try {
            JSONObject reader = new JSONObject(String.valueOf(playlistData));
            JSONArray items = reader.getJSONArray("items");

            for(int i = 0; i < 50 && i < numberPlaylistsLeft; i++){
                JSONObject playlists = items.getJSONObject(i);

                if(Integer.valueOf(playlists.getJSONObject("tracks").getString("total")) != 0){
                    // Extract track total
                    Log.d("track total", playlists.getJSONObject("tracks").getString("total"));
                    playlistTrackTotal.add(playlists.getJSONObject("tracks").getString("total"));

                    // Extract ID
                    Log.d("ID", playlists.getString("id"));
                    playlistId.add(playlists.getString("id"));

                    // Extract snapshot_id
                    Log.d("snapshot_id", playlists.getString("snapshot_id"));
                    playlistSnap.add(playlists.getString("snapshot_id"));

                    // Extract Name
                    Log.d("Name:", playlists.getString("name"));
                    playlistNames.add(playlists.getString("name"));

                    // Extract href
                    Log.d("href", playlists.getString("href"));
                    playlistHref.add(playlists.getString("href"));

                    // Extract Playlist Image URL
                    String imageUrl = playlists.getJSONArray("images").getJSONObject(0).getString("url");
                    Log.d("Image URL",imageUrl);
                    playlistImageURL.add(imageUrl);

                }
            }

            if(numberPlaylistsLeft > 50){ // more playlists to find
                Log.d("50+ Playlists", String.valueOf(numberOffset));

                numberPlaylistsLeft = numberPlaylistsLeft - 50;
                numberOffset = numberOffset + 50;
                new GetMorePlaylistJSON().execute(mAccessToken);
            }
            else{ // No more playlists to find
                showPlaylists();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
