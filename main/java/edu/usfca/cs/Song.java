package edu.usfca.cs;

import java.sql.*;
import java.util.ArrayList;

public class Song extends Entity {
    protected Album album;
    protected Artist artist;
    protected SongInterval theSongInterval;
    protected String genre;
    boolean liked;

    public Song(){
        super();
    }
    public Song(String name) {
        super(name);
    }
    public Song(String name, int length) {
        super(name);
        theSongInterval = new SongInterval(length);
        genre = "";
    }
    public Song(String name, String album, String artist) {
        super(name);
        this.artist = new Artist(artist);
        this.album = new Album(album, this.artist);
        genre = "";
    }
    public Song(String name, int length, String album, String artist) {
        super(name);
        this.theSongInterval = new SongInterval(length);
        this.artist = new Artist(artist);
        this.album = new Album(album, this.artist);
        genre = "";
    }

    public Song(String name, String album, String artist, String genre){
        super(name);
        this.artist = new Artist(artist);
        this.album = new Album(album, this.artist);
        this.genre = genre;
    }

    public Song(String name, int length, String album, String performer, String theGenre) {
        super(name);
        this.theSongInterval = new SongInterval(length);
        this.artist = new Artist(performer);
        this.album = new Album(album, artist);
        this.genre = theGenre;
    }

    public Song(String name, int length, String album, String performer, boolean liked, String theGenre) {
        super(name);
        this.theSongInterval = new SongInterval(length);
        this.artist = new Artist(performer);
        this.album = new Album(album, artist);
        this.liked = liked;
        this.genre = theGenre;
    }

    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setLength(int length) {
        theSongInterval = new SongInterval(length);
   }

   public String showLength() {
        return theSongInterval.toString();
   }
    @Override
    public Album getAlbum() {
        return album;
    }
    protected void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String toString() {
        return super.toString() + " " + this.artist + " " + this.album + " ";
        //return super.toString() + " " + this.artist + " " + this.album + " " + this.theSongInterval;
    }

    public String toXML(){
        return"\t<song id=\"" + this.entityID + "\">n" +
                "\t\t<title>" + this.name + "</title>\n" +
                "\t\t<artist id=\"" + this.artist.entityID + "\">" + this.artist.name + "</artist>\n" +
                "\t\t<album id=\"" + this.album.entityID + "\">" + this.album.name + "</album> </song>";
    }

    public String toJSON(){
        return "{\n" +
                "\t\"id\": \"" + this.entityID + "\",\n" +
                "\t\"title\": \"" + this.name + "\", \n" +
                "\t\"artist\": :\n" +
                "\t\t\"id\": \"" + this.artist.entityID + "\",\n" +
                "\t\t\"name\": \"" + this.artist.name +"\"\n" +
                "\t}, \n" +
                "\t\t\"id\": \"" + this.album.entityID + "\", \n" +
                "\t\t\"name\": \"" + this.album.name + "\"\n" +
                "\t}\n" +
                "}\n";
    }

    public String toSQL(){
        return "insert into songs(id, name, album, artist) values(" + this.entityID + ", '" + this.name + "', " + album.entityID + ", "
            + artist.entityID + ");";
    }


    public void fromSQL(ResultSet rs) throws SQLException {
        this.entityID = rs.getInt("id");
        this.name = rs.getString("name");

        //getting the artistID from song, looking the ID in the artist table
        // getting the artist's name from the table, putting that into this.artist
        int theArtistID = rs.getInt("artist");
        Connection theConnection = rs.getStatement().getConnection();
        Statement theStatement = theConnection.createStatement();
        ResultSet rsArtists = theStatement.executeQuery("select * from artists");
        String theArtistName = null;
        while (rsArtists.next()){
            if(rsArtists.getInt("id") == theArtistID){
                theArtistName = rsArtists.getString("name");
            }
        }
        Artist theArtist = new Artist(theArtistName);
        this.artist = theArtist;


        int theAlbumID = rs.getInt("album");
        theConnection = rs.getStatement().getConnection();
        theStatement = theConnection.createStatement();
        ResultSet rsAlbums = theStatement.executeQuery("select * from albums");
        String theAlbumName = null;
        while(rsAlbums.next()){
            if(rsAlbums.getInt("id") == theAlbumID){
                theAlbumName = rsAlbums.getString("name");
            }
        }
        Album theAlbum = new Album(theAlbumName);
        this.album = theAlbum;
    }

    public boolean getLiked(){
        return liked;
    }
}
