package com.example.myapplication;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.DAOSongs;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MusicService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public List<Song> listaSong = new ArrayList<>();
    private ActivityMainBinding binding;

    TextView txtTitle, txtArtist;
    ImageView img;
    ImageButton btnPlayPause;

    LinearLayout layoutPlayer;

    Song song;

    int count = 0;

    DAOSongs daoSongs;

    // Services
    MusicService musicService;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    int REQUEST_CODE = 200;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        checkPermissions();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        daoSongs = new DAOSongs(this);

        txtTitle = (TextView)findViewById(R.id.titleMin);
        txtArtist = (TextView)findViewById(R.id.singer);
        img = (ImageView)findViewById(R.id.img);
        btnPlayPause = (ImageButton)findViewById(R.id.btnPlayPause);
        layoutPlayer = (LinearLayout)findViewById(R.id.layoutInfo);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Actualizamos la información del mini reproductor al volver de la pantalla de reproducción
        try{
            if (musicService.song != null) {
                song = musicService.song;

                txtTitle.setText(song.getTitle());
                txtArtist.setText(song.getArtist());

                Bitmap bmAlbum = daoSongs.getAlbumArt(song.getAlbumId());
                if (bmAlbum != null) img.setImageBitmap(bmAlbum);
                else img.setImageResource(R.drawable.default_record_album);

                btnPlayPause.setBackgroundResource(R.drawable.ic_pause);

                if (musicService.mediaPlayer.isPlaying() && musicService.mediaPlayer != null) {
                    btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
                } else {
                    btnPlayPause.setBackgroundResource(R.drawable.ic_play);
                }
            }
        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Verificamos que los permisos de lectura han sido aceptados
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        int permisoLectura = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permisoLectura == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    public void startMusic(View view) {
        btnPlayPause.setBackgroundResource(R.drawable.ic_play);

        try{
            if (musicService.mediaPlayer.isPlaying() && musicService.mediaPlayer != null) {
                btnPlayPause.setBackgroundResource(R.drawable.ic_play);
                musicService.pause();
            } else {
                btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
                musicService.play();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), (CharSequence) e, Toast.LENGTH_SHORT).show();
        }
    }

    public void after(View view) {

        count = musicService.position;

        try{
            if (count == 0) {
            count = musicService.songList.size() - 1;
            } else {
                count--;
            }
            musicService.after(musicService.songList, count);
            song = musicService.songList.get(count);
            LoadInfoSong(song, count);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void next(View view) {

        try {
            count = musicService.position;

            if (count == musicService.songList.size() - 1) {
                count = 0;
            } else {
                count++;
            }
            musicService.next(musicService.songList, count);
            song = musicService.songList.get(count);

            LoadInfoSong(song, count);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setListaSong(List<Song> songList){ this.listaSong = songList; }

    public void setVisibleLayout(){
        RelativeLayout bottomLayout = findViewById(R.id.bottomLayout);
        bottomLayout.setVisibility(View.VISIBLE);
    }

    public void LoadInfoSong(Song song, int position){

        count = position;

        txtTitle = (TextView)findViewById(R.id.titleMin);
        txtArtist = (TextView)findViewById(R.id.singer);
        img = (ImageView)findViewById(R.id.img);
        btnPlayPause = (ImageButton)findViewById(R.id.btnPlayPause);

        txtTitle.setText(song.getTitle());
        txtArtist.setText(song.getArtist());

        Bitmap bmAlbum = daoSongs.getAlbumArt(song.getAlbumId());
        if(bmAlbum != null) img.setImageBitmap(bmAlbum);
        else img.setImageResource(R.drawable.default_record_album);

        btnPlayPause.setBackgroundResource(R.drawable.ic_pause);

        if (musicService.mediaPlayer.isPlaying() && musicService.mediaPlayer != null) {
            btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
        } else {
            btnPlayPause.setBackgroundResource(R.drawable.ic_play);
        }
    }

    public void PlayerMusic(View view){
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }
}