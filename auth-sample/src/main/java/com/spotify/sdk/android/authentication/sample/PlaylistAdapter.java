package com.spotify.sdk.android.authentication.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {
    PlaylistDB playlistDB;
    List playlistNames;
    List playlistCount;
    List playlistIDs;
    List playlistHref;
    String userId;
    String accessToken;
    LayoutInflater mInflater;
    Context ctx;

    RecyclerView playlistRecycler;

    private final View.OnClickListener playlistListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = playlistRecycler.getChildLayoutPosition(v);

            String currentHrefPlaylist = String.valueOf(playlistHref.get(i));
            String currentNamePlaylist = String.valueOf(playlistNames.get(i));
            String currentCountPlaylist = String.valueOf(playlistCount.get(i));

            Log.d("currentNamePlaylist", currentNamePlaylist);
            // Toast.makeText(ctx, String.valueOf(playlistNames.get(index)), Toast.LENGTH_SHORT).show();
            Intent playlistIntent = new Intent(ctx, FilterActivity.class);
            playlistIntent.putExtra("Token", accessToken);
            playlistIntent.putExtra("Href", currentHrefPlaylist);
            playlistIntent.putExtra("Name", currentNamePlaylist);
            playlistIntent.putExtra("Count", currentCountPlaylist);
            playlistIntent.putExtra("userID", userId);
            ctx.startActivity(playlistIntent);
            }
    };

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.playlistRecycler = recyclerView;
    }

    @Override
    public PlaylistHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.itemplaylist, parent, false);
        view.setOnClickListener(playlistListener);
        return new PlaylistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder playlistHolder, int i) {
        String currentNamePlaylist = String.valueOf(playlistNames.get(i));
        String currentCountPlaylist = String.valueOf(playlistCount.get(i));

        playlistHolder.playlistCount.setText(currentCountPlaylist);
        playlistHolder.playlistTitle.setText(currentNamePlaylist);
        byte[] imageByte = playlistDB.getIMGByte((String) playlistIDs.get(i));
        Bitmap image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        playlistHolder.playlistImage.setImageBitmap(image);
    }

    public PlaylistAdapter(Context ctx, List newPlaylistNames, List newPlaylistCount, List newPlaylistIDs, List newPlaylistHref, String newUserID, String newAccessToken){
        Log.d("TRACKADAPTER", "Created Instance");
        mInflater = LayoutInflater.from(ctx);
        playlistDB = new PlaylistDB(ctx, "PL_IMAGE_DATABASE", null, 1);
        this.ctx = ctx;
        this.userId = newUserID;
        this.accessToken = newAccessToken;
        this.playlistHref = newPlaylistHref;
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
