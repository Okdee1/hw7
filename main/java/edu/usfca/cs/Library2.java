package edu.usfca.cs;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Library2 extends Entity {

    private static ArrayList<Song> songs;
    private static ArrayList<Artist> artists;
    private static ArrayList<Album> albums;

    ArrayList<Integer> elID;
    protected boolean liked;

    public Library2() {
        songs = new ArrayList<Song>();
        artists = new ArrayList<Artist>();
        albums = new ArrayList<Album>();
    }

    public void addSongs(Song s) {
        if (!songs.contains(s)) {
            if(!isDuplicate(s)){
                songs.add(s);
            }
            else{
                System.out.println("cant add the song '" + s.name + "' with entityID '" + s.entityID +"' cos its a duplicate");
            }
        }
    }

    public boolean isDuplicate(Song theSong){
        elID = new ArrayList<>();
        if (elID.contains(theSong.entityID)) {
            return false;
        }
        else {
            for(int i = 0; i < songs.size(); i++){
                Song s = songs.get(i);
                if (theSong.entityID != s.entityID) {
                    //if the song names equal AND (the albums equal or artists equal)
                    if (theSong.getName().equals(s.getName()) &&
                            (theSong.getAlbum().getName().equals(s.getAlbum().getName()) ||
                                    theSong.getArtist().getName().equals(s.getArtist().getName()))) {
                        elID.add(s.entityID);

                        return true;
                    }//if the albums equal and artists equal and song names equal
                    else if (theSong.getAlbum().getName().equals(s.getAlbum().getName()) &&
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

    public void duplicates() {
        Boolean duplicatesBool = songs.stream().anyMatch(s -> isDuplicate(s));

        if (duplicatesBool == true) {
            Scanner userInputScanner = new Scanner(System.in);

            boolean ask = true;
            while(ask){
                System.out.println("Remove duplicates? Type y or n");
                String userInput = userInputScanner.nextLine();
                if (userInput.equals("y")) {
                    songs.removeIf(sng -> elID.contains(sng.entityID));
                    ask = false;
                }
                else if(userInput.equals("n")){
                    ask = false;
                    return;
                }
                else{
                    System.out.println("bruh y or n");
                }
            }
        } else {
            System.out.println("no duplicates");
        }
    }

    public boolean findSongs(Song s) {
        return songs.contains(s);
    }

    public boolean getLiked(boolean liked) {
        if (liked == true) {
            return true;
        }
        return false;
    }


    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public static void main(String[] args) {
        Library2 lib1;
        lib1 = new Library2();
        Song s1, s2, s3, s4;
        Artist a1, a2, a3;
        Album al1, al2, al3;

        s1 = new Song("Happy Birthday ", 184, "na", "some guy");
        s2 = new Song("Helter Skelter", 184, "whiteAlbum", "theBeatles");
        s3 = new Song("Helter Skelter", 184, "whiteAlbum", "theBeatles");
        s4 = new Song("Sad but true", 184, "blackAlbum", "metallica");
        songs.add(s1);
        songs.add(s2);
        songs.add(s3);
        songs.add(s4);
        System.out.println(songs.size());
        lib1.duplicates();

        for (int i = 0; i < songs.size(); i++) {
            System.out.println(songs.get(i).getName());
        }

        lib1.writeXML();
    }

    public void writeXML(){
        try{
            FileWriter theWriter = new FileWriter("theCreatedXML.xml");
            theWriter.write("<Library>\n");

            theWriter.write("<songs>\n");
            for(Song s: songs){
                theWriter.write(s.toXML() + "\n");
            }
            theWriter.write("</songs>\n");

            theWriter.write("<artists>\n");
            for(Artist a: artists){
                theWriter.write(a.toXML() + "\n");
            }
            theWriter.write("</artists>\n");

            theWriter.write("<albums>\n");
            for(Album a: albums){
                theWriter.write(a.toXML() + "\n");
            }
            theWriter.write("</albums>\n");

            theWriter.write("</Library>");
            theWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeJSON(){
        try{
            FileWriter theWriter = new FileWriter("theCreatedJSON.json");
            theWriter.write("{\n");

            int len = 0;

            theWriter.write("\"songs\": {\n");
            for(Song s: songs){
                theWriter.write("\t" +s.toJSON() + "\n");
                len++;
                if(len <= songs.size() - 1){
                    theWriter.write(",");
                }
            }
            theWriter.write("],\n");

            len = 0;

            theWriter.write("\" artists\" : [\n");
            for(Artist a : artists){
                theWriter.write("\t" + a.toJSON() + "\n");
                len++;
                if(len <= artists.size() -1){
                    theWriter.write(",");
                }
            }
            theWriter.write("],\n");

            len = 0;

            theWriter.write("\t" + "\" albums\": [\n");
            for(Album a: albums){
                theWriter.write(a.toJSON() + "\n");
                len++;
                if(len <= albums.size() - 1){
                    theWriter.write(",");
                }
            }
            theWriter.write("]\n}");
            theWriter.close();
        }

        catch(IOException e){
            e.printStackTrace();
        }
    }
}


