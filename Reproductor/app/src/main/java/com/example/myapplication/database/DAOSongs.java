package com.example.myapplication.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DAOSongs implements Serializable {

    SQLiteDatabase dbWritable, dbReadable;
    Context context;

    public DAOSongs(Context context){
        this.context = context;
        MusicDBHelper helper = new MusicDBHelper(context);
        dbWritable = helper.getWritableDatabase();
        dbReadable = helper.getReadableDatabase();
    }

    // Leemos el total de canciones del dispositivo y las agregamos a la base de datos
    public void LoadSongs(){

        ContentValues values = new ContentValues();

        ContentResolver resolver = context.getContentResolver();

        Cursor musicCursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA},
                null, null, null);

        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int songUriColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);


        while (musicCursor.moveToNext()) {
            int idSong = musicCursor.getInt(idColumn);
            String title = musicCursor.getString(titleColumn);
            String artist = musicCursor.getString(artistColumn);
            int albumId = musicCursor.getInt(albumIdColumn);
            String album = musicCursor.getString(albumColumn);
            String duration = musicCursor.getString(durationColumn);
            String songUri = musicCursor.getString(songUriColumn);

            Song song = new Song(idSong, title, artist, albumId, album, setCorrectDuration(duration), songUri);

            values.put(SongsDataSource.ID_SONG, song.getSongID());
            values.put(SongsDataSource.TITLE, song.getTitle());
            values.put(SongsDataSource.ARTIST, song.getArtist());
            values.put(SongsDataSource.ALBUM_ID, song.getAlbumId());
            values.put(SongsDataSource.ALBUM, song.getAlbum());
            values.put(SongsDataSource.DURATION, song.getDuration());
            values.put(SongsDataSource.SONG_URI, song.getSongUri());

            dbWritable.insert(SongsDataSource.SONGS_TABLE_NAME, null, values);
        }
    }

    // Consulta para mostrar el total de canciones
    public List<Song> GetSongs(){
        List<Song> songList = new ArrayList<>();
        Song song;

        Cursor cursor = dbReadable.query(SongsDataSource.SONGS_TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()){
            int songId = cursor.getInt(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            int albumId = cursor.getInt(3);
            String album = cursor.getString(4);
            String duration = cursor.getString(5);
            String songUri= cursor.getString(6);

            song = new Song(songId, title, artist, albumId, album, duration, songUri);
            songList.add(song);
        }

        return songList;
    }

    // Consulta a devuelve canción a partir de su ID
    public Song GetSong(int songId){
        String selection = SongsDataSource.ID_SONG + " = ?";
        String[] selectionArgs = {String.valueOf(songId)};

        Cursor cursor = dbReadable.query(SongsDataSource.SONGS_TABLE_NAME, null, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();

        String title = cursor.getString(1);
        String artist = cursor.getString(2);
        int albumId = cursor.getInt(3);
        String album = cursor.getString(4);
        String duration = cursor.getString(5);
        String songUri= cursor.getString(6);

        Song song = new Song(songId, title, artist, albumId, album, duration, songUri);

        return song;
    }

    // Convierte en Bitmap la carátula del álbum recibido a partir su ID
    public Bitmap getAlbumArt(int album_id) {

        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        }
        return bm;
    }

    // Formatea la duración de una canción en minutos y segundos
    public String setCorrectDuration(String songs_duration) {

        if (Integer.valueOf(songs_duration) != null) {
            int time = Integer.valueOf(songs_duration);

            int seconds = time / 1000;
            int minutes = seconds / 60;
            seconds = seconds % 60;

            if (seconds < 10) {
                songs_duration = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
            } else {
                songs_duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
            }
            return songs_duration;
        }
        return null;
    }
}
