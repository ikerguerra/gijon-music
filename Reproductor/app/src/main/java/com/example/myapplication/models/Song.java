package com.example.myapplication.models;

import android.graphics.Bitmap;
import android.hardware.biometrics.BiometricManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    // Variables de la canción
    int songID;
    String title;
    String artist;
    int albumId;
    String album;
    String duration;
    String songUri;

    // Métodos Getter
    public int getSongID() { return songID; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getAlbumId() { return albumId; }
    public String getAlbum() { return album; }
    public String getDuration() { return duration; }
    public String getSongUri() { return songUri; }

    // Métodos Setter
    public void setSongID(int songID) { this.songID = songID;}
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbumId(int albumId) { this.albumId = albumId; }
    public void setAlbum(String album) { this.album = album; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setSongUri(String songUri) { this.songUri = songUri; }

    // Constructor sin parámetros
    public Song() { }

    // Constructor con parámetros
    public Song(int songID, String title, String artist, int albumId, String album, String duration, String songUri) {
        this.songID = songID;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
        this.album = album;
        this.duration = duration;
        this.songUri = songUri;
    }

    public Song(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(songID);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeInt(albumId);
        dest.writeString(album);
        dest.writeString(duration);
        dest.writeString(songUri);
    }

    public void readFromParcel(Parcel in) {
        songID = in.readInt();
        title = in.readString();
        artist = in.readString();
        albumId = in.readInt();
        album = in.readString();
        duration = in.readString();
        songUri = in.readString();
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
