package com.example.myapplication.ui.search;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.database.DAOSongs;
import com.example.myapplication.services.MusicService;
import com.example.myapplication.PlayerActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.Song;
import com.example.myapplication.ui.home.LocalMusicAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<Song> songList = new ArrayList<>();

    // Elementos View
    View view;
    ListView lvSongs;
    SearchView search;

    Song song;
    int count;

    DAOSongs daoSongs;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Intent intent = new Intent(getContext(), MusicService.class);
        getContext().bindService(intent, connection, BIND_AUTO_CREATE);

        view = inflater.inflate(R.layout.fragment_search, container, false);

        search = view.findViewById(R.id.search);
        lvSongs = view.findViewById(R.id.lvSongs);

        daoSongs = new DAOSongs(getContext());

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                musicService.startService(intent);

                song = songList.get(position);

                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra("song", song);
                startActivity(intent);

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

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getSongsSearch();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void getSongsSearch() {
        songList.clear();

        Context context = getActivity().getApplicationContext();
        ContentResolver resolver = context.getContentResolver();

        String query = search.getQuery().toString();

        if(query.isEmpty()){
            songList.clear();

        }else {
            Cursor musicCursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.DURATION,
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.ALBUM_ID,
                            MediaStore.Audio.Media.ALBUM},
                    MediaStore.Audio.Media.TITLE + " LIKE ? OR " + MediaStore.Audio.Media.ARTIST + " LIKE ? OR " + MediaStore.Audio.Media.ALBUM + " LIKE ?",
                    new String[]{"%" + search.getQuery() + "%", "%" + search.getQuery() + "%", "%" + search.getQuery() + "%"},
                    null);

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
                String album = musicCursor.getString(albumColumn);
                String duration = musicCursor.getString(durationColumn);
                String songUri = musicCursor.getString(songUriColumn);

                int albumId = musicCursor.getInt(albumIdColumn);

                Song song = new Song(idSong, title, artist, albumId, album, daoSongs.setCorrectDuration(duration), songUri);
                songList.add(song);
            }
        }

        LocalMusicAdapter localMusicAdapter = new LocalMusicAdapter(getContext(), songList);
        lvSongs.setAdapter(localMusicAdapter);
    }
}