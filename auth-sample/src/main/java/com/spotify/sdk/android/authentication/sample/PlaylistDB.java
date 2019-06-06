package com.spotify.sdk.android.authentication.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaylistDB extends SQLiteOpenHelper {
    SQLiteDatabase db;
    Context ctx;
    static String DB_NAME = "DATABASE";
    static String TABLE_NAME = "PLAYLIST_TABLE";
    static int VERSION = 1;

    public PlaylistDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx = context;
        VERSION = version;
        DB_NAME = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY, PL_ID TEXT, PL_SNAP TEXT, PL_IMG_URL TEXT, PL_IMG_DATA BYTE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(VERSION == oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            VERSION = newVersion;
            onCreate(db);
        }
    }

    public void insert(String pl_id, String snap_id, String img_url, byte[] img) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PL_ID", pl_id);
        cv.put("PL_SNAP", snap_id);
        cv.put("PL_IMG_URL", img_url);
        cv.put("PL_IMG_DATA", img);
        db.insert(TABLE_NAME, null, cv);
    }

    public void delete(String pl_id) {
        db = getReadableDatabase();
        Cursor playlistIDs = db.rawQuery("SELECT PL_ID FROM " + TABLE_NAME + ";", null);

        while (playlistIDs.moveToNext()) {
            if (pl_id.equals(playlistIDs.getString(0))) {
                db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _id = \"" + pl_id + "\";");
                // playlistIDs.close();
            }
        }
        playlistIDs.close();
    }

    public boolean checkID(String pl_id) {
        db = getReadableDatabase();
        Cursor playlistIDs = db.rawQuery("SELECT PL_ID FROM " + TABLE_NAME + ";", null);

        while (playlistIDs.moveToNext()){
            if (pl_id.equals(playlistIDs.getString(0))) {
                playlistIDs.close();
                return true;
            }
        }

        playlistIDs.close();
        return false;
    }

    public boolean checkSnap(String pl_id, String pl_snap) {
        db = getReadableDatabase();
        Cursor playlistData = db.rawQuery("SELECT PL_ID, PL_SNAP FROM " + TABLE_NAME + ";", null);

        while (playlistData.moveToNext()) {
            if (pl_id.equals(playlistData.getString(0))) {
                if (pl_snap.equals(playlistData.getString(1))) {
                    playlistData.close();
                    return true;
                }
            }
        }

        playlistData.close();
        return false;
    }

    public byte[] getIMGByte(String pl_id){
        db = getReadableDatabase();
        Cursor playlistData = db.rawQuery("SELECT PL_ID, PL_IMG_DATA FROM " + TABLE_NAME + ";", null);

        while (playlistData.moveToNext()) {
            if (pl_id.equals(playlistData.getString(0))) {
                byte[] imgByte = playlistData.getBlob(1);

                playlistData.close();
                return imgByte;
            }
        }

        playlistData.close();
        return null;
    }
}
