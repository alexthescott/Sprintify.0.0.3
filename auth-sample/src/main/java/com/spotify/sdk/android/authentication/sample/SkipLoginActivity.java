package com.spotify.sdk.android.authentication.sample;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.net.URI;

public class SkipLoginActivity extends AppCompatActivity {
    static String bestOfURL = "https://open.spotify.com/user/bassguitar1234/playlist/42f8Se0ZEFHo6DOaOJYh7t?si=34EGSvvNQfWqQ_5ZWcJtxg";
    static String bedroomURL = "https://open.spotify.com/user/bassguitar1234/playlist/5uhMFGRs0ANuftx8NzoXMA?si=KGKJNAijSumrbdzQFMFzyQ";
    static String psychURL = "https://open.spotify.com/user/bassguitar1234/playlist/5hDgT5o3dNHIDtZ5D7B2yG?si=4trug1NISZyRojc8qWDvIA";
    static String brockURL = "https://open.spotify.com/user/bassguitar1234/playlist/4AaIikKY1k6pgnLbvf2gNj?si=qqixgNNRRF2YwfD_L36ajg";
    static String indieURL = "https://open.spotify.com/user/bassguitar1234/playlist/43KbtU1muoR8ufGrvPqm3d?si=X--wB10cRNWQxfrE-wyltA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip_login);
        getSupportActionBar().setTitle("Pregenerated Playlists");
    }

    public void onBestOfClicked(View view){
        Uri bestURI = Uri.parse(bestOfURL);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, bestURI);
        startActivity(launchBrowser);
    }

    public void onBedroomClicked(View view){
        Uri bedroomURI = Uri.parse(bedroomURL);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, bedroomURI);
        startActivity(launchBrowser);
    }

    public void onPsychClicked(View view){
        Uri psychURI = Uri.parse(psychURL);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, psychURI);
        startActivity(launchBrowser);
    }

    public void onBrockClicked(View view){
        Uri brockURI = Uri.parse(brockURL);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, brockURI);
        startActivity(launchBrowser);
    }

    public void onIndieClicked(View view){
        Uri indieURI = Uri.parse(indieURL);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, indieURI);
        startActivity(launchBrowser);
    }
}
