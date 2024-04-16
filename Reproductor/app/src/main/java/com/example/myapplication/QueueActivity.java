package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.myapplication.models.Song;
import com.example.myapplication.services.MusicService;
import com.example.myapplication.ui.home.LocalMusicAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueueActivity extends AppCompatActivity {

    ImageButton btnFinish;
    ListView lvQueueSongs;

    List<Song> songList = new ArrayList<>();

    MusicService musicService = new MusicService();

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
        setContentView(R.layout.activity_queue);
        getSupportActionBar().hide();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        btnFinish = (ImageButton) findViewById(R.id.btnFinish);
        lvQueueSongs = (ListView) findViewById(R.id.lvQueueSongs);

        songList = (List<Song>) getIntent().getSerializableExtra("Songs");

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LocalMusicAdapter adapter = new LocalMusicAdapter(this, songList);
        lvQueueSongs.setAdapter(adapter);

        lvQueueSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(musicService != null) {
                    musicService.stop();
                }
                try {
                    musicService.playNewSong(songList, position);
                } catch (IOException e) {
                    //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }

                LocalMusicAdapter adapter = new LocalMusicAdapter(getApplicationContext(), musicService.updateSongList);
                lvQueueSongs.setAdapter(adapter);
            }
        });
    }
}