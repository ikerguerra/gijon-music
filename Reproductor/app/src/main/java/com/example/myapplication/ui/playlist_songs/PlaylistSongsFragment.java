package com.example.myapplication.ui.playlist_songs;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DAOPlaylists;
import com.example.myapplication.database.DAOPlaylistsSongs;
import com.example.myapplication.database.DAOSongs;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;
import com.example.myapplication.services.MusicService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistSongsFragment extends Fragment {

    File fileImage = null;

    View view;

    PopupMenu popupMenu;

    Playlist playlist;
    List<Song> songList = new ArrayList<>();

    EditText etxtNamePlaylist;
    TextView txtNamePlaylist;
    ListView lvPlaylistSongs;
    ImageButton btnBack, btnMore;
    ImageView imagePlaylist;
    LinearLayout lEmpytList;
    Button btnShuffle;

    Context context;

    PlaylistSongsAdapter playlistSongsAdapter;

    DAOPlaylists daoPlaylists;
    DAOPlaylistsSongs daoPlaylistsSongs;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_playlist_songs, container, false);

        // Elementos View
        txtNamePlaylist = (TextView)view.findViewById(R.id.txtNamePlaylist);
        lvPlaylistSongs = view.findViewById(R.id.lvPlaylistSongs);
        btnBack = (ImageButton)view.findViewById(R.id.btnBack);
        btnMore = (ImageButton)view.findViewById(R.id.btnMore);
        imagePlaylist = (ImageView)view.findViewById(R.id.imagePlaylist);
        lEmpytList = (LinearLayout) view.findViewById(R.id.lEmpytList);
        btnShuffle = view.findViewById(R.id.btnShuffle);

        // Objetos instanciados
        daoPlaylists = new DAOPlaylists(getContext());
        daoPlaylistsSongs = new DAOPlaylistsSongs(getContext());
        daoSongs = new DAOSongs(getContext());

        Intent intent = new Intent(getContext(), MusicService.class);
        getContext().bindService(intent, connection, BIND_AUTO_CREATE);

        // Recuperamos la playlist que llamó al fragmento y las canciones de dicha playlist
        playlist = getArguments().getParcelable("Playlist");
        songList = (List<Song>) getArguments().getSerializable("Songs");

        // Cargar imagen de la playlist
        if(playlist.getUriImagePlaylist() == null){
            imagePlaylist.setImageResource(R.drawable.img_playlist_empty);
        }else{
            imagePlaylist.setImageURI(Uri.parse(playlist.getUriImagePlaylist()));
        }

        // Cargar título de la playlist
        txtNamePlaylist.setText(playlist.getNamePlaylist());

        // Cargar canciones de la playlist
        playlistSongsAdapter = new PlaylistSongsAdapter(getContext(), songList, playlist);
        lvPlaylistSongs.setAdapter(playlistSongsAdapter);

        lvPlaylistSongs.setEmptyView(lEmpytList);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_playlist, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menuRemove:
                                // Eliminamos la playlist seleccionada
                                daoPlaylists.DeletePlaylist(playlist);

                                // Eliminamos la miniatura asociada a la playlist
                                if(playlist.getUriImagePlaylist() != null) {
                                    fileImage = new File(playlist.getUriImagePlaylist());
                                    fileImage.delete();
                                }

                                getFragmentManager().popBackStack();

                                break;
                            case R.id.menuName:

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                // Get the layout inflater
                                LayoutInflater inflater = requireActivity().getLayoutInflater();

                                View vAlertDialog = inflater.inflate(R.layout.dialog_add_playlist, null);

                                // Inflate and set the layout for the dialog
                                // Pass null as the parent view because its going in the dialog layout
                                builder.setView(vAlertDialog)
                                        .setTitle("Cambiar nombre de la playlist")
                                        // Add action buttons
                                        .setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {

                                                // Recogemos el nombre insertado en el diálogo
                                                etxtNamePlaylist = vAlertDialog.findViewById(R.id.playlistName);

                                                if(!etxtNamePlaylist.getText().toString().isEmpty()){
                                                    playlist.setNamePlaylist(etxtNamePlaylist.getText().toString().trim());

                                                    daoPlaylists.UpdateInfoPlaylist(playlist);

                                                    txtNamePlaylist.setText(etxtNamePlaylist.getText().toString().trim());
                                                }else Toast.makeText(getContext(), "Se debe introducir un nombre para la lista", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                builder.show();

                                break;
                            case R.id.menuImage:

                                OpenGallery();

                                break;
                        }

                        return false;
                    }
                });
            }
        });

        imagePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songList = daoPlaylistsSongs.GetPlaylistSongs(playlist);

                // Copiamos a una lista temporal la lista de reproducción actual
                List<Song> shuffleList = new ArrayList<>(songList);

                // Permuta aleatoriamente la lista.
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

        lvPlaylistSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Song song = songList.get(position);

                musicService.song = song;

                if(musicService != null) {
                    musicService.stop();
                }
                try {
                    musicService.playNewSong(songList, position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int count = position;
                song = songList.get(position);

                ((MainActivity)getActivity()).LoadInfoSong(song, position);
                ((MainActivity)getActivity()).setListaSong(songList);
                ((MainActivity)getActivity()).setVisibleLayout();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Date: 19/11/2021
    public void OpenGallery(){

        if(isAdded()){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, 123);
        }else {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    // Date: 19/11/2021
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        FileOutputStream fos = null;

        if (requestCode == 123 && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();

            imagePlaylist.setImageURI(imageUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(bitmap != null) {
                fileImage = new File(getContext().getCacheDir(), playlist.getPlaylistID());
            }

            try {
                fos = new FileOutputStream(fileImage);

                // Generamos una miniatura de la imagen seleccionada para comprimirla y guardarla en caché
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 256, 256);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);

                fos.close();
            }
            catch (IOException e) {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            playlist.setUriImagePlaylist(fileImage.toString());

            daoPlaylists.UpdateInfoPlaylist(playlist);

        }
    }

    // Elimina una canción perteneciente a una playlist
    public void DeletePlaylistSong(Context context, Playlist playlist, Song song, ViewGroup parent){

        daoPlaylistsSongs = new DAOPlaylistsSongs(context);
        lvPlaylistSongs = parent.findViewById(R.id.lvPlaylistSongs);

        // Eliminamos la canción de la playlist
        daoPlaylistsSongs.RemoveSongFromPlaylist(playlist, song);

        // Recuperamos la nueva lista de canciones para la playlist
        songList = daoPlaylistsSongs.GetPlaylistSongs(playlist);

        playlistSongsAdapter = new PlaylistSongsAdapter(context.getApplicationContext(), songList, playlist);
        lvPlaylistSongs.setAdapter(playlistSongsAdapter);
    }
}