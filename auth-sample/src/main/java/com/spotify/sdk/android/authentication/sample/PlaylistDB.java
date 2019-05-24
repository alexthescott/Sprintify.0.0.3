package com.spotify.sdk.android.authentication.sample;

import android.content.ContentValues;
import android.content.Context;
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
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(PL_ID INTEGER PRIMARY KEY, PL_SNAP INTEGER, PL_IMG_URL TEXT, PL_IMG_DATA BYTE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(VERSION == oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            VERSION = newVersion;
            onCreate(db);
        }
    }

    public void insert(int snap_id, byte[] img, String title, int track_count){
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PL_SNAP", snap_id);
        cv.put("PL_IMG_DATA", img);
        cv.put("PL_TITLE", title);
        cv.put("PL_TRACK_COUNT", track_count);
        db.insert(TABLE_NAME, null, cv);
    }
}
