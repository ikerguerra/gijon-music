package com.example.myapplication.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.DAOPlaylists;
import com.example.myapplication.database.DAOPlaylistsSongs;
import com.example.myapplication.database.DAOSongs;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.util.List;

public class LocalMusicAdapter extends BaseAdapter {

    private List<Song> songs;
    private LayoutInflater songInfo;
    DAOSongs daoSongs;
    DAOPlaylists daoPlaylists;
    DAOPlaylistsSongs daoPlaylistsSongs;

    Song song;
    PopupMenu popupMenu;

    public LocalMusicAdapter(Context context, List<Song> songs){
        songInfo = LayoutInflater.from(context);

        this.songs = songs;

        daoSongs = new DAOSongs(context);
        daoPlaylists = new DAOPlaylists(context);
        daoPlaylistsSongs = new DAOPlaylistsSongs(context);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) { return null; }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLayout = (LinearLayout) songInfo.inflate(R.layout.item_local_music, parent, false);

        ImageView imgViewAlbum = (ImageView)songLayout.findViewById(R.id.imgViewAlbum);
        TextView txtTitle = (TextView)songLayout.findViewById(R.id.song_title);
        TextView txtArtistAlbum = (TextView) songLayout.findViewById(R.id.song_artist_album);
        ImageButton btnOptions = (ImageButton) songLayout.findViewById(R.id.btnOptions);

        Song song = songs.get(position);

        Bitmap bmAlbum = daoSongs.getAlbumArt(song.getAlbumId());
        if(bmAlbum != null) imgViewAlbum.setImageBitmap(bmAlbum);
        else imgViewAlbum.setImageResource(R.drawable.default_record_album);

        txtTitle.setText(song.getTitle());
        txtArtistAlbum.setText(song.getArtist() + " - " + song.getAlbum());
        btnOptions.setFocusable(false);
        btnOptions.setClickable(false);

        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsList(position, v);
            }
        });

        songLayout.setTag(position);

        return songLayout;
    }

    public void OptionsList(int position, View view){
        song = songs.get(position);

        popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuAddToPlaylist:

                        AlertDialog.Builder optionsPlaylists = new AlertDialog.Builder(view.getContext());
                        optionsPlaylists.setTitle("Añadir a playlist");

                        List<Playlist> namePlaylists = daoPlaylists.LoadPlaylists();
                        CharSequence[] arrayPlaylist = new CharSequence[namePlaylists.size()];
                        for(int i = 0; i < namePlaylists.size(); i++){
                            arrayPlaylist[i] = namePlaylists.get(i).getNamePlaylist(); // Whichever string you wanna store here from custom object
                        }

                        optionsPlaylists.setItems(arrayPlaylist, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Playlist playlist = daoPlaylists.GetPlaylistByPosition(which);

                                daoPlaylistsSongs.AddSongToPlaylist(playlist, song);

                                Toast.makeText(view.getContext(), "Canción añadida a " + playlist.getNamePlaylist(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        optionsPlaylists.show();

                        break;
                }

                return false;
            }
        });
    }
}
