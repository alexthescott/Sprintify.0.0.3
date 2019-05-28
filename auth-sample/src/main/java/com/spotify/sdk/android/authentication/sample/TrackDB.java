package com.spotify.sdk.android.authentication.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrackDB extends SQLiteOpenHelper {
    SQLiteDatabase db;
    Context ctx;
    static String DB_NAME = "DATABASE";
    static String TABLE_NAME = "TRACK_TABLE";
    static int VERSION = 1;

    public TrackDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx = context;
        VERSION = version;
        DB_NAME = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY, TRACK_ID TEXT, TRACK_IMG_URL TEXT, TRACK_IMG_DATA BYTE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(VERSION == oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            VERSION = newVersion;
            onCreate(db);
        }
    }

    public void insert(String track_id, String img_url, byte[] img) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("TRACK_ID", track_id);
        cv.put("TRACK_IMG_URL", img_url);
        cv.put("TRACK_IMG_DATA", img);
        db.insert(TABLE_NAME, null, cv);
    }

    public boolean checkID(String track_id) {
        db = getReadableDatabase();
        Cursor trackIDs = db.rawQuery("SELECT TRACK_ID FROM " + TABLE_NAME + ";", null);

        while (trackIDs.moveToNext()){
            if (track_id.equals(trackIDs.getString(0))) {
                trackIDs.close();
                return true;
            }
        }

        trackIDs.close();
        return false;
    }

    public byte[] getIMGByte(String track_id){
        db = getReadableDatabase();
        Cursor trackData = db.rawQuery("SELECT TRACK_ID, TRACK_IMG_DATA FROM " + TABLE_NAME + ";", null);

        while (trackData.moveToNext()) {
            if (track_id.equals(trackData.getString(0))) {
                byte[] imgByte = trackData.getBlob(1);

                trackData.close();
                return imgByte;
            }
        }

        trackData.close();
        return null;
    }
}
