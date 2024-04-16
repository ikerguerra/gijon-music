package com.example.myapplication.ui.home;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.InfoActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.services.MusicService;
import com.example.myapplication.R;
import com.example.myapplication.database.DAOSongs;
import com.example.myapplication.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Song> songList = new ArrayList();

    ListView lvSongs;
    TextView txtNumSongs;
    ImageView img;
    ImageButton btnAbout;
    Button btnShuffle;

    View view;

    Song song;

    DAOSongs daoSongs;

    int count = 0;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Intent intent = new Intent(getContext(), MusicService.class);
        getContext().bindService(intent, connection, BIND_AUTO_CREATE);

        view = inflater.inflate(R.layout.fragment_home, container, false);

        //songList = new ArrayList<>();

        lvSongs = (ListView) view.findViewById(R.id.lvSongs);
        txtNumSongs = (TextView)view.findViewById(R.id.txtNumSongs);
        img = (ImageView)view.findViewById(R.id.img);
        btnAbout = (ImageButton) view.findViewById(R.id.btnAbout);
        btnShuffle = (Button) view.findViewById(R.id.btnShuffle);

        daoSongs = new DAOSongs(getContext());

        // Cargamos las canciones del dispositivo en la base de datos y recuperamos la lista
        daoSongs.LoadSongs();
        songList = daoSongs.GetSongs();

        // Mostramos número de canciones actuales del dispositivo
        if(songList.size() != 1) txtNumSongs.setText(songList.size() + " canciones");
        else txtNumSongs.setText(songList.size() + " canción");

        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                return song1.getTitle().compareTo(song2.getTitle());
            }
        });

        LocalMusicAdapter localMusicAdapter = new LocalMusicAdapter(getContext(), songList);
        lvSongs.setAdapter(localMusicAdapter);

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Copiamos a una lista temporal la lista de reproducción actual
                List<Song> shuffleList = new ArrayList<>(songList);

                //Permuta aleatoriamente la lista.
                Collections.shuffle(shuffleList);

                // Comprobamos que la lista no esté vacía para reproducir
                if(songList.size() != 0){
                    if (musicService != null) {
                        musicService.stop();
                    }
                    try {
                        musicService.playNewSong(shuffleList, 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Song song = shuffleList.get(0);

                    ((MainActivity) getActivity()).LoadInfoSong(song, 0);
                    ((MainActivity) getActivity()).setVisibleLayout();
                }else Toast.makeText(getContext(), "Lista de reproducción vacía", Toast.LENGTH_SHORT).show();
            }
        });

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                musicService.startService(intent);

                song = songList.get(position);

                musicService.song = song;

                if(musicService != null) {
                    musicService.stop();
                }
                try {
                    musicService.playNewSong(songList, position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                count = position;
                song = songList.get(position);

                ((MainActivity)getActivity()).LoadInfoSong(song, position);
                ((MainActivity)getActivity()).setListaSong(songList);
                ((MainActivity)getActivity()).setVisibleLayout();
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InfoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}