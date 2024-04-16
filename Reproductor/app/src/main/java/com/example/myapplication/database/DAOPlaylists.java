package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.Playlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DAOPlaylists implements Serializable {

    SQLiteDatabase dbWritable, dbReadable;

    List<Playlist> listPlaylist;
    Playlist playlist;

    public DAOPlaylists(Context context){
        MusicDBHelper helper = new MusicDBHelper(context);
        dbWritable = helper.getWritableDatabase();
        dbReadable = helper.getReadableDatabase();
    }

    public void InsertPlaylist(Playlist playlist){
        ContentValues values = new ContentValues();

        values.put(PlaylistsDataSource.ID_PLAYLIST, playlist.getPlaylistID());
        values.put(PlaylistsDataSource.NAME_PLAYLIST, playlist.getNamePlaylist());
        values.put(PlaylistsDataSource.IMAGE_URI, playlist.getUriImagePlaylist());

        dbWritable.insert(PlaylistsDataSource.PLAYLISTS_TABLE_NAME, null, values);
    }

    // Consulta para recuperar todas las playlist existentes
    public List<Playlist> GetPlaylists(){
        List<Playlist> playlistList = new ArrayList<>();

        Cursor cursor = dbReadable.query(PlaylistsDataSource.PLAYLISTS_TABLE_NAME, null, null, null, null, null, null);

        while(cursor.moveToNext()){
            String playlistID = cursor.getString(0);
            String playlistName = cursor.getString(1);
            String imageUriPlaylist = cursor.getString(2);

            playlist = new Playlist(playlistID, playlistName, imageUriPlaylist);
            playlistList.add(playlist);
        }

        return playlistList;
    }

    public Playlist GetPlaylist(String id){

        String whereClause = PlaylistsDataSource.ID_PLAYLIST + " = ?";

        String[] whereArgs = { id };

        Cursor cursor = dbReadable.query(PlaylistsDataSource.PLAYLISTS_TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        cursor.moveToFirst();

        String playlistID = cursor.getString(0);
        String playlistName = cursor.getString(1);

        playlist = new Playlist(playlistID, playlistName);

        return playlist;
    }

    public void DeletePlaylist(Playlist playlist){

        String whereClause = PlaylistsDataSource.ID_PLAYLIST + " = ?";

        String[] whereArgs = { playlist.getPlaylistID() };

        dbWritable.delete(PlaylistsDataSource.PLAYLISTS_TABLE_NAME, whereClause, whereArgs);
    }

    public List<Playlist> LoadPlaylists(){
        listPlaylist = new ArrayList();

        listPlaylist = GetPlaylists();

        return listPlaylist;
    }

    public Playlist GetPlaylistByPosition(int position){
        listPlaylist = GetPlaylists();

        playlist = listPlaylist.get(position);

        return playlist;
    }

    public void UpdateInfoPlaylist(Playlist playlist){
        ContentValues values = new ContentValues();
        values.put(PlaylistsDataSource.NAME_PLAYLIST, playlist.getNamePlaylist());
        values.put(PlaylistsDataSource.IMAGE_URI, playlist.getUriImagePlaylist());

        String whereClause = PlaylistsDataSource.ID_PLAYLIST + " = ?";

        String[] whereArgs = { playlist.getPlaylistID() };

        dbWritable.update(PlaylistsDataSource.PLAYLISTS_TABLE_NAME, values, whereClause, whereArgs);
    }
}
