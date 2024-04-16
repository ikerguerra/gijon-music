package com.example.myapplication.database;

public class PlaylistSongsDataSource {
    public static final String PLAYLIST_SONGS_TABLE_NAME = "PlaylistSongs";
    public static final String STRING_TYPE = "text";
    public static final String INT_TYPE = "integer";

    public static final String ID_PLAYLIST_SONGS = "idSongsPlaylist";
    public static final String ID_SONG = "idSong";
    public static final String ID_PLAYLIST = "idPlaylist";

    public static final String CREATE_TABLE_SCRIPT =
            "create table " + PLAYLIST_SONGS_TABLE_NAME + "(" +
                    ID_PLAYLIST_SONGS + " " + INT_TYPE + " primary key autoincrement," +
                    ID_SONG + " " + STRING_TYPE + " not null," +
                    ID_PLAYLIST + " " + STRING_TYPE + " not null," +
                    " FOREIGN KEY (" + ID_SONG + ") REFERENCES " + SongsDataSource.SONGS_TABLE_NAME + " (" + SongsDataSource.ID_SONG + "), " +
                    " FOREIGN KEY (" + ID_PLAYLIST + ") REFERENCES " + PlaylistsDataSource.PLAYLISTS_TABLE_NAME + " (" + PlaylistsDataSource.ID_PLAYLIST + "))";
}
