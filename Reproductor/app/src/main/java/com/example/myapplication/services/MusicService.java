package com.example.myapplication.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.myapplication.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    public MediaPlayer mediaPlayer;
    public List<Song> songList = new ArrayList<>();
    public List<Song> updateSongList = new ArrayList<>();
    public Song song;
    public int position;
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    // Reproduce la canción actual en memoria
    public void play(){
        mediaPlayer.start();
    }

    // Reproducir nueva canción desde la lista recibida
    public void playNewSong(List<Song> list, int position) throws IOException {
        this.songList = list;
        this.position = position;

        // Actualizamos la cola de reproducción
        this.updateSongList = new ArrayList<>(list.subList(position, list.size()));

        if(list != null){
            song = list.get(position);

            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(String.valueOf(song.getSongUri()));
                mediaPlayer.prepare();
            }
            mediaPlayer.start();
        }
    }

    // Pausar canción
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();// pausa la música
        }
    }

    // Detener canción
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();// Pausa la música
            mediaPlayer.release();// Liberar recursos
            mediaPlayer = null;
        }
    }

    // Reproduce la canción anterior
    public void after(List<Song> list, int position) throws IOException {
        // Actualizamos la cola de reproducción
        updateSongList = new ArrayList<>(list.subList(position, list.size()));

        // Detiene la canción actual
        stop();
        // Nueva canción
        playNewSong(list, position);
    }

    // Reproduce la siguiente canción
    public void next(List<Song> list, int position) throws IOException {
        // Actualizamos la cola de reproducción
        updateSongList = new ArrayList<>(list.subList(position, list.size()));

        // Detiene la canción actual
        stop();
        // Nueva canción
        playNewSong(list, position);
    }
}

