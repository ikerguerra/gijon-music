package com.example.myapplication.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable {

    String playlistID;
    String namePlaylist;
    String uriImagePlaylist;
    Bitmap imagePlaylist;

    public Playlist(String playlistID, String namePlaylist){
        this.playlistID = playlistID;
        this.namePlaylist = namePlaylist;
    }

    public Playlist(String playlistID, String namePlaylist, String uriImagePlaylist){
        this.playlistID = playlistID;
        this.namePlaylist = namePlaylist;
        this.uriImagePlaylist = uriImagePlaylist;
    }

    public Playlist() {

    }

    public String getPlaylistID(){ return playlistID; }
    public String getNamePlaylist(){ return namePlaylist; }
    public String getUriImagePlaylist(){ return uriImagePlaylist; }
    public Bitmap getImagePlaylist(){ return imagePlaylist; }

    public void setPlaylistID(String playlistID){ this.playlistID = playlistID; }
    public void setNamePlaylist(String namePlaylist){ this.namePlaylist = namePlaylist; }
    public void setUriImagePlaylist(String uriImagePlaylist){ this.uriImagePlaylist = uriImagePlaylist; }
    public void setImagePlaylist(Bitmap imagePlaylist){ this.imagePlaylist = imagePlaylist; }

    public Playlist(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playlistID);
        dest.writeString(namePlaylist);
        dest.writeString(uriImagePlaylist);
    }

    public void readFromParcel(Parcel in) {
        playlistID = in.readString();
        namePlaylist = in.readString();
        uriImagePlaylist = in.readString();
    }

    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}
