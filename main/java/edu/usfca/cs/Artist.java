package edu.usfca.cs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Artist extends Entity {

    protected ArrayList<Song> songs;
    protected ArrayList<Album> albums;

    public Artist(){
        super();
    }

    public Artist(String name) {
        super(name);
    }

    protected ArrayList<Song> getSongs() {
        return songs;
    }

    protected void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    protected ArrayList<Album> getAlbums() {
        return albums;
    }

    protected void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public void addSong(Song s) {
        songs.add(s);
    }

    public String toXML(){
        return"\t<artist id= \"" + this.entityID + "\">\n" +
                "\t\t<name>" + this.name + "</name>\n" +
                "\t</artist>";
    }

    public String toJSON(){
        return"{" +
                "\t\"id\": + \"" + this.entityID + "\", \n" +
                "\t\"name\": \"" + this.name + "\"\n" +
                "}\n";
    }

    public String toSQL(){
        return "insert into artists(id, name) values(" + this.entityID + ", '"
                + this.name + "');";
    }

    @Override
    public void fromSQL(ResultSet rs) throws SQLException {
        this.entityID = rs.getInt("id");
        this.name = rs.getString("name");
    }

}
