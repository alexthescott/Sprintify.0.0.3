package com.spotify.sdk.android.authentication.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoggedIn extends AppCompatActivity {
    Context ctx = LoggedIn.this;
    ProgressDialog PlaylistDialog;

    // List playlistSnapshot_ID = new ArrayList();
    List playlistNames = new ArrayList();
    List playlistImageURL = new ArrayList();
    List playlistHref = new ArrayList();
    List playlistTrackTotal = new ArrayList();

    String mAccessToken;

    int numberPlaylists;
    int numberPlaylistsLeft;

    int numberOffset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Intent intent = getIntent();
        mAccessToken = intent.getExtras().getString("Token");

        new GetPlaylistJson().execute(mAccessToken);

        // Potentially will have to find a way to obtain more playlists
        // if the user has more than 50 playlists

    }

    private class GetPlaylistJson extends AsyncTask<String, Void, JSONObject> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PlaylistDialog = new ProgressDialog(ctx);
            PlaylistDialog.setTitle("Retrieving Playlist");
            PlaylistDialog.setMessage("Loading...");
            PlaylistDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
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

                // Extract Name
                Log.d("Name:", playlists.getString("name"));
                playlistNames.add(playlists.getString("name"));

                // Extract href
                Log.d("href", playlists.getString("href"));
                playlistHref.add(playlists.getString("href"));

                // Extract track total
                Log.d("track total", playlists.getJSONObject("tracks").getString("total"));
                playlistTrackTotal.add(playlists.getJSONObject("tracks").getString("total"));

                // Extract Playlist Image URL
                String imageUrl = playlists.getJSONArray("images").getJSONObject(0).getString("url");
                Log.d("Image URL",imageUrl);
                playlistImageURL.add(imageUrl);

                // Extract snapshot_id
                // Log.d("snapshot_id", playlists.getString("snapshot_id"));
            }
            
            if(numberPlaylistsLeft > 50){ // more playlists to find
                Log.d("50+ Playlists", String.valueOf(numberOffset));

                numberPlaylistsLeft = numberPlaylistsLeft - 50;
                numberOffset = numberOffset + 50;
                new GetMorePlaylistJSON().execute(mAccessToken);
            }
            else{ // No more playlists to find
                PlaylistDialog.dismiss();
                // TODO: DISPLAY RESULT
            }
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
}
