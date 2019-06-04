package com.spotify.sdk.android.authentication.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackHolder> {
    TrackDB trackDB;
    List trackNames;
    List trackBPM;
    List trackIds;
    LayoutInflater mInflater;

    @NonNull
    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.itemtrack, parent, false);
        return new TrackHolder(view);
    }

    public TrackAdapter(Context ctx, List newTrackNames, List newTrackBPM, List newTrackIds){
        Log.d("TRACKADAPTER", "Created Instance");
        mInflater = LayoutInflater.from(ctx);
        trackIds = newTrackIds;
        trackDB = new TrackDB(ctx, "TRACK_IMAGE_DATABASE", null, 1);
        trackNames = newTrackNames;
        trackBPM = newTrackBPM;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackHolder trackHolder, int i) {
        trackHolder.trackTitle.setText((CharSequence) trackNames.get(i));
        trackHolder.trackBPM.setText(String.valueOf(trackBPM.get(i)));
        byte[] imageByte = trackDB.getIMGByte((String) trackIds.get(i));
        Bitmap image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        trackHolder.trackImage.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return trackNames.size();
    }

    public static class TrackHolder extends RecyclerView.ViewHolder{
        TextView trackTitle;
        TextView trackBPM;
        ImageView trackImage;

        public TrackHolder(View itemView) {
            super(itemView);
            trackTitle = itemView.findViewById(R.id.tracktitle);
            trackBPM = itemView.findViewById(R.id.trackBPM);
            trackImage = itemView.findViewById(R.id.trackItemImage);
        }
    }

    public List returnIds(){
        return trackIds;
    }
}
