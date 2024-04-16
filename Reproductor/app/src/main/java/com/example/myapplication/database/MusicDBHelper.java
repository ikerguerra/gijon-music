package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MusicDBHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "MusicDB.db";
    public static int DATABASE_VERSION = 1;

    public MusicDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MusicDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlaylistsDataSource.CREATE_TABLE_SCRIPT);
        db.execSQL(SongsDataSource.CREATE_TABLE_SCRIPT);
        db.execSQL(PlaylistSongsDataSource.CREATE_TABLE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlaylistsDataSource.PLAYLISTS_TABLE_NAME);
        db.execSQL(PlaylistsDataSource.CREATE_TABLE_SCRIPT);

        db.execSQL("DROP TABLE IF EXISTS " + SongsDataSource.SONGS_TABLE_NAME);
        db.execSQL(SongsDataSource.CREATE_TABLE_SCRIPT);

        db.execSQL("DROP TABLE IF EXISTS " + PlaylistSongsDataSource.PLAYLIST_SONGS_TABLE_NAME);
        db.execSQL(PlaylistSongsDataSource.CREATE_TABLE_SCRIPT);

        DATABASE_VERSION = newVersion;
    }
}
