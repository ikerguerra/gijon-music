package com.example.myapplication.database;

import com.example.myapplication.models.Song;

import java.util.List;

public class PlaylistsDataSource {
    public static final String PLAYLISTS_TABLE_NAME = "Playlists";
    public static final String STRING_TYPE = "text";

    public static final String ID_PLAYLIST = "idPlaylist";
    public static final String NAME_PLAYLIST = "namePlaylist";
    public static final String IMAGE_URI = "uriImagePlaylist";

    public static final String CREATE_TABLE_SCRIPT =
            "create table " + PLAYLISTS_TABLE_NAME + "(" +
                    ID_PLAYLIST + " " + STRING_TYPE + " primary key," +
                    NAME_PLAYLIST + " " + STRING_TYPE + " not null," +
                    IMAGE_URI + " " + STRING_TYPE + " null)";
}
