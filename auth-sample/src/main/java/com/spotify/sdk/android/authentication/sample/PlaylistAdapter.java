package com.spotify.sdk.android.authentication.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {
    PlaylistDB playlistDB;
    List playlistNames;
    List playlistCount;
    List playlistIDs;
    LayoutInflater mInflater;

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.itemplaylist, parent, false);
        return new PlaylistHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.PlaylistHolder playlistHolder, int i) {
        playlistHolder.playlistCount.setText(playlistCount.get(i).toString());
        playlistHolder.playlistTitle.setText(playlistNames.get(i).toString());
        byte[] imageByte = playlistDB.getIMGByte((String) playlistIDs.get(i));
        Bitmap image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        playlistHolder.playlistImage.setImageBitmap(image);
    }

    public PlaylistAdapter(Context ctx, List newPlaylistNames, List newPlaylistCount, List newPlaylistIDs){
        Log.d("TRACKADAPTER", "Created Instance");
        mInflater = LayoutInflater.from(ctx);
        playlistDB = new PlaylistDB(ctx, "PL_IMAGE_DATABASE", null, 1);
        this.playlistNames = newPlaylistNames;
        this.playlistCount = newPlaylistCount;
        this.playlistIDs = newPlaylistIDs;
    }

    @Override
    public int getItemCount() {
        return playlistNames.size();
    }

    public class PlaylistHolder extends RecyclerView.ViewHolder{
        TextView playlistTitle;
        TextView playlistCount;
        ImageView playlistImage;

        public PlaylistHolder(View itemView) {
            super(itemView);
            playlistTitle = itemView.findViewById(R.id.playlistTitle);
            playlistCount = itemView.findViewById(R.id.playlistCount);
            playlistImage = itemView.findViewById(R.id.playlistImage);
        }
    }
}
