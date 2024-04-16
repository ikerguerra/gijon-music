package com.example.myapplication.ui.library;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.database.DAOPlaylists;
import com.example.myapplication.database.DAOPlaylistsSongs;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;
import com.example.myapplication.ui.playlist_songs.PlaylistSongsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LibraryFragment extends Fragment {

    TextView txtLibrary, txtNumPlaylists;
    ListView lvPlaylists;
    ListView lvLibrary;
    EditText etxtAddPlaylist;
    ImageView imgPlaylist;
    FloatingActionButton fab;

    List<Playlist> listPlaylist = new ArrayList<>();
    LibraryAdapter libraryAdapter;

    DAOPlaylists daoPlaylists;
    DAOPlaylistsSongs daoPlaylistsSongs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // Elementos View
        txtLibrary = view.findViewById(R.id.txtLibrary);
        txtNumPlaylists = view.findViewById(R.id.txtNumPlaylists);
        lvPlaylists = view.findViewById(R.id.lvPlaylists);
        lvLibrary = view.findViewById(R.id.lvPlaylists);
        imgPlaylist = view.findViewById(R.id.imgPlaylist);
        fab = view.findViewById(R.id.fab);

        daoPlaylists = new DAOPlaylists(getContext());
        daoPlaylistsSongs = new DAOPlaylistsSongs(getContext());

        // Asignamos el número de listas para mostrarlo por pantalla
        setNumPlaylists(daoPlaylists.GetPlaylists().size());

        listPlaylist = daoPlaylists.LoadPlaylists();
        libraryAdapter = new LibraryAdapter(getContext(), listPlaylist);
        lvPlaylists.setAdapter(libraryAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = requireActivity().getLayoutInflater();

                View vAlertDialog = inflater.inflate(R.layout.dialog_add_playlist, null);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(vAlertDialog)
                        .setTitle("Ponle nombre a tu playlist")
                        // Add action buttons
                        .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                // Recogemos el nombre insertado en el diálogo
                                etxtAddPlaylist = vAlertDialog.findViewById(R.id.playlistName);

                                if(!etxtAddPlaylist.getText().toString().isEmpty()){
                                    // Creamos la playlist
                                    CreatePlaylist(etxtAddPlaylist.getText().toString().trim());

                                    // Actualizamos el número de listas de reproducción
                                    setNumPlaylists(daoPlaylists.GetPlaylists().size());
                                }else Toast.makeText(getContext(), "Se debe introducir un nombre para la lista", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getContext(), "Se debe introducir un nombre para la lista", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
        });

        lvPlaylists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist playlist = listPlaylist.get(position);
                List<Song> songs = daoPlaylistsSongs.GetPlaylistSongs(playlist);

                PlaylistSongsFragment playlistSongsFragment = new PlaylistSongsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Playlist", playlist);
                bundle.putSerializable("Songs", (Serializable) songs);
                playlistSongsFragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, playlistSongsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        LibraryAdapter libraryAdapter = new LibraryAdapter(getContext(), listPlaylist);
        lvPlaylists.setAdapter(libraryAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void CreatePlaylist(String namePlaylist){

        if (!namePlaylist.isEmpty()){
            String uniqueID = UUID.randomUUID().toString();
            Playlist playlist = new Playlist(uniqueID, namePlaylist, null);

            daoPlaylists.InsertPlaylist(playlist);

            listPlaylist = daoPlaylists.LoadPlaylists();

            LibraryAdapter libraryAdapter = new LibraryAdapter(getContext(), listPlaylist);
            lvPlaylists.setAdapter(libraryAdapter);
        }
    }

    private void setNumPlaylists(int numPlaylists){
        // Mostramos en pantalla el número de playlist actuales formateando al salida
        if(numPlaylists != 1) txtNumPlaylists.setText(numPlaylists + " listas de reproducción");
        else txtNumPlaylists.setText(numPlaylists + " lista de reproducción");
    }

}