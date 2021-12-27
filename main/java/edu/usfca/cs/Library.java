package edu.usfca.cs;

import java.util.ArrayList;

public class Library {
    private ArrayList<Song> songs;

    public Library() {
        songs = new ArrayList<Song>();
    }
    public boolean findSong(Song s) {
        return songs.contains(s);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void addSong(Song s) {
        songs.add(s);
    }


}
