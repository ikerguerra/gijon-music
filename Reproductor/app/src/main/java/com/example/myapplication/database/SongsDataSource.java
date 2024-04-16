package com.example.myapplication.database;

public class SongsDataSource {
    public static final String SONGS_TABLE_NAME = "Songs";
    public static final String STRING_TYPE = "text";
    public static final String INT_TYPE = "integer";

    public static final String ID_SONG = "idSong";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String ALBUM_ID = "albumId";
    public static final String ALBUM = "album";
    public static final String DURATION = "duration";
    public static final String SONG_URI = "songUri";

    public static final String CREATE_TABLE_SCRIPT =
            "create table " + SONGS_TABLE_NAME + "(" +
                    ID_SONG + " " + INT_TYPE + " primary key," +
                    TITLE + " " + STRING_TYPE + " not null," +
                    ARTIST + " " + STRING_TYPE + " not null," +
                    ALBUM_ID + " " + INT_TYPE + " not null," +
                    ALBUM + " " + STRING_TYPE + " not null," +
                    DURATION + " " + STRING_TYPE + " not null," +
                    SONG_URI + " " + STRING_TYPE + " not null)";
}
