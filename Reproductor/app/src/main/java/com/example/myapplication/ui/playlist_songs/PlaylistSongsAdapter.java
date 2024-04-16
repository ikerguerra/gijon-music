package com.example.myapplication.ui.playlist_songs;

import android.content.Context;
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

import com.example.myapplication.R;
import com.example.myapplication.database.DAOPlaylistsSongs;
import com.example.myapplication.database.DAOSongs;
import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.util.List;

public class PlaylistSongsAdapter extends BaseAdapter {
    private List<Song> songList;
    private LayoutInflater songInfo;
    DAOSongs daoSongs;
    DAOPlaylistsSongs daoPlaylistsSongs;

    Playlist playlist;
    PopupMenu popupMenu;
    PlaylistSongsFragment playlistSongsFragment;

    Context context;


    public PlaylistSongsAdapter(Context context, List<Song> songs, Playlist playlist){
        this.context = context;
        songInfo = LayoutInflater.from(context);
        this.songList = songs;
        this.playlist = playlist;
        daoSongs = new DAOSongs(context);
        daoPlaylistsSongs = new DAOPlaylistsSongs(context);
    }

    @Override
    public int getCount() {
        return songList.size();
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

        Song song = songList.get(position);

        // Cargamos la portada del álbum en caso de que tenga una imagen
        Bitmap bmAlbum = daoSongs.getAlbumArt(song.getAlbumId());
        if(bmAlbum != null) imgViewAlbum.setImageBitmap(bmAlbum);
        else imgViewAlbum.setImageResource(R.drawable.default_record_album);

        // Cargamos el nombre de la canción, del artista y del álbum
        txtTitle.setText(song.getTitle());
        txtArtistAlbum.setText(song.getArtist() + " - " + song.getAlbum());
        btnOptions.setFocusable(false);
        btnOptions.setClickable(false);

        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_playlist_songs, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menuRemove:
                                playlistSongsFragment = new PlaylistSongsFragment();
                                playlistSongsFragment.DeletePlaylistSong(context, playlist, song, parent);
                                break;
                        }
                        return false;
                    }
                });
            }
        });

        songLayout.setTag(position);

        return songLayout;
    }
}
