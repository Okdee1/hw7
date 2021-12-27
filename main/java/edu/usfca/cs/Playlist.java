package edu.usfca.cs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Playlist extends Entity{

    private ArrayList<Song> listOfSongs;
    ArrayList<Integer> elID;

    public Playlist(String name) {
        super(name);
        listOfSongs = new ArrayList<Song>();
    }

    public Playlist(String name, ArrayList<Song> theList){
        super(name);
        listOfSongs = theList;
    }


    public boolean addSong(Song s) {
        if (!listOfSongs.contains(s)) {
            if(!isDuplicate(s)){
                return listOfSongs.add(s);
            }
            else{
                System.out.println("cant add its a duplicate dude");
            }
        }
        return false;
    }

    public boolean isDuplicate(Song theSong){
        elID = new ArrayList<>();
        if (elID.contains(theSong.entityID)) {
            return false;
        }
        else {
            for(int i = 0; i < listOfSongs.size(); i++){
                Song s = listOfSongs.get(i);
                if (theSong.entityID != s.entityID) {
                    if (theSong.getName().equals(s.getName()) &&
                            (theSong.getAlbum().getName().equals(s.getAlbum().getName()) ||
                                    theSong.getArtist().getName().equals(s.getArtist().getName()))) {
                        elID.add(s.entityID);
                        return true;
                    } else if (theSong.getAlbum().getName().equals(s.getAlbum().getName()) &&
                            theSong.getArtist().getName().equals(s.getArtist().getName()) &&
                            theSong.getName().toLowerCase().replaceAll("\\p{Punct}", "").equals(s.getName().toLowerCase().replaceAll("\\p{Punct}", ""))) {
                        elID.add(s.entityID);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean deleteSong(Song s) {
        return listOfSongs.remove(s);
    }

    public boolean findSong(Song s){
        return listOfSongs.contains(s);
    }

    public List<Song> sortList() {
        List<Song> sl = listOfSongs.stream().sorted(Comparator.comparing(Song::getLiked)).collect(Collectors.toList());
        return sl;
    }

    public ArrayList<Song> getSongs(){
        ArrayList<Song> list = new ArrayList<Song>();
        list.addAll(listOfSongs);

        return list;
    }

    public ArrayList<Song> mergeLists(ArrayList<Song> otherList){

        ArrayList<Song> listTwoCopy = new ArrayList<Song>(otherList);
        listOfSongs.removeAll(otherList);
        listOfSongs.addAll(listTwoCopy);
        return listOfSongs;


    }

    public List<Song> shuffleList() {
        Collections.shuffle(listOfSongs);
        return listOfSongs;
    }

    public ArrayList<Song> randomListArtist(String a){
        ArrayList<Song> list = (ArrayList<Song>)listOfSongs.stream().filter(s -> s.artist.getName().equals(a)).collect(Collectors.toList());
        Collections.shuffle(list);
        return list;
    }
}
