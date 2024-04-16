package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DAOPlaylistsSongs implements Serializable {

    SQLiteDatabase dbWritable, dbReadable;
    Context context;

    List<Song> songs = new ArrayList<>();

    public DAOPlaylistsSongs(Context context){
        this.context = context;
        MusicDBHelper helper = new MusicDBHelper(context);
        dbWritable = helper.getWritableDatabase();
        dbReadable = helper.getReadableDatabase();
    }

    /*
    Date: 04/11/2021
    */
    public void AddSongToPlaylist(Playlist playlist, Song song){
        ContentValues values = new ContentValues();

        values.put(PlaylistSongsDataSource.ID_PLAYLIST, playlist.getPlaylistID());
        values.put(PlaylistSongsDataSource.ID_SONG, song.getSongID());

        dbWritable.insert(PlaylistSongsDataSource.PLAYLIST_SONGS_TABLE_NAME, null, values);
    }

    /*
    Date: 09/11/2021
     */
    public void RemoveSongFromPlaylist(Playlist playlist, Song song){
        String whereClause = PlaylistSongsDataSource.ID_PLAYLIST + " = ? AND " + PlaylistSongsDataSource.ID_SONG + " = ?";

        String[] whereArgs = { playlist.getPlaylistID(), String.valueOf(song.getSongID())};

        dbWritable.delete(PlaylistSongsDataSource.PLAYLIST_SONGS_TABLE_NAME, whereClause, whereArgs);
    }

    /*
    Date: 04/11/2021
    */
    public List<Song> GetPlaylistSongs(Playlist playlist){
        DAOSongs daoSongs = new DAOSongs(context);
        songs.clear();

        String[] columns = {
                PlaylistSongsDataSource.ID_SONG
        };

        String selection = PlaylistSongsDataSource.ID_PLAYLIST + " = ?";

        String[] selectionArgs = { playlist.getPlaylistID() };

        Cursor cursor = dbReadable.query(PlaylistSongsDataSource.PLAYLIST_SONGS_TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            int songID = cursor.getInt(0);

            Song song = daoSongs.GetSong(songID);

            songs.add(song);
        }

        return songs;
    }

    /*
    08/11/2021
     */
    public int GetNumSongsPlaylist(Playlist playlist){
        String selection = PlaylistSongsDataSource.ID_PLAYLIST + " = ?";

        String[] selectionArgs = { playlist.getPlaylistID() };

        Cursor cursor = dbReadable.query(PlaylistSongsDataSource.PLAYLIST_SONGS_TABLE_NAME, null, selection, selectionArgs, null, null, null);

        return cursor.getCount();
    }
}
