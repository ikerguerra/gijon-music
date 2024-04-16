package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.DAOSongs;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MusicService;

import java.io.Serializable;

public class PlayerActivity extends AppCompatActivity {

    // Elementos View
    ImageView imgReturn, imgAlbum;
    TextView txtTitle, txtArtist, txtDuration, txtViewProgress;
    SeekBar seekBar;
    ImageButton btnPlay, btnPlayLast, btnPlayNext, btnQueueMusic;

    Song song = new Song();

    int count = 0;
    int currentSong = 0;

    DAOSongs daoSongs;

    // Servicios
    MusicService musicService;

    // Establecemos la conexión con el servicio
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().hide();

        daoSongs = new DAOSongs(this);

        // Elementos View
        imgReturn = (ImageView) findViewById(R.id.imgReturn);
        imgAlbum = (ImageView) findViewById(R.id.imgAlbum);
        txtTitle = (TextView) findViewById(R.id.textViewName);
        txtArtist = (TextView) findViewById(R.id.textViewArtist);
        txtDuration = (TextView) findViewById(R.id.textViewDurationSong);
        txtViewProgress = (TextView) findViewById(R.id.textViewProgress);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnPlay = (ImageButton) findViewById(R.id.buttonPlay);
        btnPlayLast = (ImageButton) findViewById(R.id.buttonPlayLast);
        btnPlayNext = (ImageButton) findViewById(R.id.buttonPlayNext);
        btnQueueMusic = (ImageButton) findViewById(R.id.btnQueueMusic);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int dest = seekBar.getProgress();

                int mMax = musicService.mediaPlayer.getDuration();
                int sMax = seekBar.getMax();

                musicService.mediaPlayer.seekTo(mMax*dest/sMax);
            }
        });

        btnPlayLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    count = musicService.position;

                    if (count == 0) {
                        count = musicService.songList.size() - 1;
                    } else {
                        count--;
                    }
                    musicService.after(musicService.songList, count);
                    song = musicService.songList.get(count);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (musicService.mediaPlayer.isPlaying() && musicService.mediaPlayer != null) {
                        btnPlay.setBackgroundResource(R.drawable.ic_play);
                        musicService.pause();
                    } else {
                        btnPlay.setBackgroundResource(R.drawable.ic_pause);
                        musicService.play();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    count = musicService.position;

                    if (count == musicService.songList.size() - 1) {
                        count = 0;
                    } else {
                        count++;
                    }
                    musicService.next(musicService.songList, count);
                    song = musicService.songList.get(count);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnQueueMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, QueueActivity.class);
                intent.putExtra("Songs", (Serializable) musicService.updateSongList);
                startActivity(intent);
            }
        });

        startProgressUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void updateInfoSong(Song song) {
        txtTitle.setText(song.getTitle());
        txtArtist.setText(song.getArtist());
        txtDuration.setText(song.getDuration());

        Bitmap albumArt = daoSongs.getAlbumArt(song.getAlbumId());

        if(albumArt != null) {
            imgAlbum.setImageBitmap(albumArt);
        }else{
            imgAlbum.setImageResource(R.drawable.default_record_album);
        }

        if (musicService.mediaPlayer.isPlaying() && musicService.mediaPlayer != null) {
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
        } else {
            btnPlay.setBackgroundResource(R.drawable.ic_play);
        }
    }

    // Date: 22/10/2021
    public void startProgressUpdate(){
        // Abre Thread para actualizar SeekBar regularmente
        DelayThread dThread = new DelayThread(100);
        dThread.start();
    }

    // Date: 22/10/2021
    public class DelayThread extends Thread {
        int milliseconds;

        public DelayThread(int i){
            milliseconds = i;
        }
        public void run() {
            while(true){
                try {
                    sleep(milliseconds);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mHandle.sendEmptyMessage(0);
            }
        }
    }

    // Date: 22/10/2021
    private Handler mHandle = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int position = musicService.mediaPlayer.getCurrentPosition();

            int mMax = musicService.mediaPlayer.getDuration();
            int sMax = seekBar.getMax();

            seekBar.setProgress(position*sMax/mMax);

            String progress = String.valueOf(position);

            txtViewProgress.setText(daoSongs.setCorrectDuration(progress));

            currentSong = musicService.position;

            String posicion = daoSongs.setCorrectDuration(String.valueOf(position));
            String max = daoSongs.setCorrectDuration(String.valueOf(mMax));

            if(posicion.equals(max)) {
                try {
                    if (currentSong == musicService.songList.size() - 1) {
                        currentSong = 0;
                    } else {
                        currentSong++;
                    }
                    musicService.next(musicService.songList, currentSong);
                } catch (Exception e) {

                }
            }

            updateInfoSong(musicService.song);
        }
    };
}