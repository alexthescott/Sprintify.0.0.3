package com.spotify.sdk.android.authentication.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoggedIn extends AppCompatActivity {

    private Call mCall;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Intent intent = getIntent();
        String mAccessToken = intent.getExtras().getString("Token");
        // Log.d("mAccessToken = ", mAccessToken);

        // Potentially will have to find a way to obtain more playlists
        // if the user has more than 50 playlists
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/playlists?limit=50")
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTPClient", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject playlistJson = new JSONObject(response.body().string());
                    Log.d("JsonObject found", playlistJson.toString(3));

                } catch (JSONException e) {
                    Log.d("JsonObject","Failed to parse data: " + e);
                }
            }




        });


    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
