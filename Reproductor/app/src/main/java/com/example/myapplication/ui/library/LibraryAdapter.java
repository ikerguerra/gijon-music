package com.example.myapplication.ui.library;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.database.DAOPlaylistsSongs;
import com.example.myapplication.database.DAOSongs;
import com.example.myapplication.models.Playlist;

import java.util.List;

public class LibraryAdapter extends BaseAdapter {

    private List<Playlist> listsLibrary;
    private LayoutInflater lists;

    DAOPlaylistsSongs daoPlaylistsSongs;
    DAOSongs daoSongs;

    public LibraryAdapter(Context context, List<Playlist> listsLibrary){
        daoPlaylistsSongs = new DAOPlaylistsSongs(context);
        daoSongs = new DAOSongs(context);
        this.lists = LayoutInflater.from(context);
        this.listsLibrary = listsLibrary;
    }

    @Override
    public int getCount() {
        return listsLibrary.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout listsLibraryLayout = (LinearLayout) lists.inflate(R.layout.items_library, parent, false);

        ImageView imagePlaylist = (ImageView)listsLibraryLayout.findViewById(R.id.imgPlaylist);
        TextView namePlaylist = (TextView)listsLibraryLayout.findViewById(R.id.txtNamePlaylist);
        TextView numSongsPlayist = (TextView)listsLibraryLayout.findViewById(R.id.txtNumSongsPlaylist);
        ImageView imageArrow = (ImageView)listsLibraryLayout.findViewById(R.id.imgArrow);

        Playlist playlist = listsLibrary.get(position);

        //Cargamos el nombre de la playlist
        namePlaylist.setText(playlist.getNamePlaylist());

        //Cargamos el número de canciones de la playlist
        int numSongs = daoPlaylistsSongs.GetNumSongsPlaylist(playlist);
        if(numSongs != 1) numSongsPlayist.setText(numSongs + " canciones");
        else numSongsPlayist.setText(daoPlaylistsSongs.GetNumSongsPlaylist(playlist)+ " canción");

        // Cargamos la imagen de la playlist
        if(playlist.getUriImagePlaylist() == null){
            imagePlaylist.setImageResource(R.drawable.img_playlist_empty);
        }else{
            imagePlaylist.setImageURI(Uri.parse(playlist.getUriImagePlaylist()));
        }

        imageArrow.setImageResource(R.drawable.baseline_navigate_next_24);

        listsLibraryLayout.setTag(position);

        return listsLibraryLayout;
    }


}
