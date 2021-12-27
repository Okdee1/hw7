package edu.usfca.cs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Album extends Entity {
    protected ArrayList<Song> songs;
    protected Artist artist;

    public Album(){
        super();
    }

    public Album(String name) {
        super(name);
    }

    public Album(String name, Artist artist){
        super(name);
        setArtist(artist);
    }

    public String getName() {
        //System.out.println("this is an album" + super.getName());
        return name;
    }

    public boolean equals(Album otherAlbum) {
        if ((this.artist.equals(otherAlbum.getArtist())) &&
                (this.name.equals(otherAlbum.getName()))) {
            return true;
        } else {
            return false;
        }
    }



    protected ArrayList<Song> getSongs() {
        return songs;
    }

    protected void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String toXML(){
        return "\t<album id= \"" + this.entityID + "\">\n" +
                "\t\t<title>" + this.name + "</title>\n" +
                "\t</album>";
    }

    public String toJSON(){
        return "{" +
                "\t\"id\": \"" + this.entityID + "\",\n" +
                "\t\"name\": \"" + this.name + "\"n" +
                "}\n";
    }

    public String toSQL(){
        return "insert into albums(id, name, artist) values(" + this.entityID + ", '"
                + this.name + "', " + artist.entityID + ");";
    }

    @Override
    public void fromSQL(ResultSet rs) throws SQLException {
        this.entityID = rs.getInt("id");
        this.name = rs.getString("name");

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
    }
}
