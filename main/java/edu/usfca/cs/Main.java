package edu.usfca.cs;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static String musicdbTestURL = "jdbc:sqlite:musicTest.db";
    /**
     * returns an arrayList of songs of an artist, filled with stuff from the database
     * @param theURL
     * @param theArtistName
     * @return
     */
    public static ArrayList getSongsFromArtistFromSQL(String theURL, String theArtistName){
        Connection connection = null;
        ArrayList<Song> returnList = new ArrayList<>();
        try{
            connection = DriverManager.getConnection(theURL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            ResultSet rsArtists = statement.executeQuery("select * from artists");
            int theArtistID = 0;
            while(rsArtists.next()){
                if(rsArtists.getString("name").equals(theArtistName)){
                    theArtistID = rsArtists.getInt("id");
                }
            }

            ResultSet rsSongs = statement.executeQuery("select * from songs");
            while(rsSongs.next()){
                if(rsSongs.getInt("artist") == theArtistID){
                    Song theSong = new Song();
                    theSong.fromSQL(rsSongs);
                    returnList.add(theSong);
                }
            }
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
        finally {
            try{
                if(connection != null){
                    connection.close();
                }
            }
            catch (SQLException e){
                System.err.println(e.getMessage());
            }
        }
        return returnList;
    }

    //returns an arrayList of entities, filled with stuff from the SQL

    /**
     * returns an arraylist of entities, filled with stuff from the database
     * @param theURL
     * @param whatEntity
     * @return
     */
    public static ArrayList getFromSQL2(String theURL, String whatEntity){
        Connection connection = null;
        String theString = null;
        ArrayList<Entity> entitiesReturn = new ArrayList<>();
        try{
            connection = DriverManager.getConnection(theURL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            if(whatEntity.equals("songs")){
                theString = "songs";
            }
            else if(whatEntity.equals("artists")){
                theString = "artists";
            }
            else if(whatEntity.equals("albums")){
                theString = "albums";
            }
            ResultSet rs = statement.executeQuery("select * from " + theString);
            while(rs.next()){
                Entity theEntity = new Entity();
                if(theString.equals("songs")){
                    theEntity = new Song();
                }
                else if(theString.equals("artists")){
                    theEntity = new Artist();
                }
                else if(theString.equals("albums")){
                    theEntity = new Album();
                }
                theEntity.fromSQL(rs);
                entitiesReturn.add(theEntity);
            }
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
        finally {
            try{
                if(connection != null){
                    connection.close();
                }
            }
            catch (SQLException e){
                System.err.println(e.getMessage());
            }
        }
        return entitiesReturn;
    }

    /**
     * writes stuff from theEntity into the database
     * @param theURL
     * @param theEntity
     */
    public static void writeToSQL(String theURL, Entity theEntity){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(theURL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            String theString = null;
            //ONLY WORKS FOR SONGS RIGHT NOW, add others later
            if(theEntity instanceof Song){
                theString = "songs";
                ResultSet rsSongs = statement.executeQuery("select * from " + theString);
                //checking if the song is already in the db
                while(rsSongs.next()){
                    //if the song is already in the db, tell us why and just fuckin exit the func
                    if(rsSongs.getString("name").equals(theEntity.getName())
                            || rsSongs.getInt("id") == theEntity.getID()){
                        System.out.println("not adding '" + theEntity.name + "' cos its already in the db");
                        if(rsSongs.getString("name").equals(theEntity.getName())){
                            System.out.println("cos the names are equal");
                            System.out.println("we trying to add " + theEntity.getName());
                            System.out.println("but we already have " + rsSongs.getString("name") + " at " + rsSongs.getInt("id"));
                        }
                        else if(rsSongs.getInt("id") == theEntity.getID()){
                            System.out.println("cos the entity ids are equal");
                        }
                        return;
                    }
                }
                //check if the artist is in the db
                ResultSet rsArtists = statement.executeQuery("select * from artists");
                boolean artistInDB = false;
                while(rsArtists.next()){
                    //if the artist is in the db, get the id from artist and assign it
                    if(rsArtists.getString("name").equals(theEntity.getArtist().name)){
                        String selectString = "select id from artists where name = '" +  theEntity.getArtist().name + "'";
                        ResultSet theRS = statement.executeQuery(selectString);
                        int theArtistID = theRS.getInt("id");
                        theEntity.getArtist().entityID = theArtistID;
                        System.out.println("the artist " + theEntity.getArtist() +" is already in the db");
                        System.out.println("assigned the artistID: " + theArtistID +" to " + theEntity.name);
                        artistInDB = true;
                        break;
                    }
                }
                if(!artistInDB){
                    //if the artist isnt in the db, put the artist into the SQL
                    System.out.println("the artist + " + theEntity.getArtist() +" isnt already in the db");
                    theEntity.getArtist().setEntityID(getLastID(musicdbTestURL, "artists") + 1);
                    String addToSQLStatement = theEntity.getArtist().toSQL();
                    statement.executeUpdate(addToSQLStatement);
                    System.out.println("executing update with: " + addToSQLStatement);
                }

                //check if the album is in the db
                ResultSet rsAlbums = statement.executeQuery("select * from albums");
                boolean albumInDB = false;
                while(rsAlbums.next()){
                    //if the album is in the db, get the id from the album and assign it
                    if(rsAlbums.getString("name").equals(((Song) theEntity).getAlbum().name)){
                        String selectString = "select id from albums where name = '" + ((Song) theEntity).getAlbum().name + "'";
                        ResultSet theRS = statement.executeQuery(selectString);
                        int theAlbumID = theRS.getInt("id");
                        theEntity.getAlbum().entityID = theAlbumID;
                        System.out.println("the album " + theEntity.getAlbum() +" is already in the db");
                        System.out.println("assigned the albumID: " + theAlbumID +" to " + theEntity.name);
                        albumInDB = true;
                        break;
                    }
                }
                if(!albumInDB){
                    //if the album isnt in the db, put the album into the SQL
                    System.out.println("the album " + theEntity.getAlbum() + " isnt already in the db");
                    theEntity.getAlbum().setEntityID(getLastID(musicdbTestURL, "albums") + 1);
                    String addToSQLStatement = theEntity.getAlbum().toSQL();
                    statement.executeUpdate(addToSQLStatement);
                    System.out.println("executing update with: " + addToSQLStatement);
                }
                //if the song isnt in the db, put the song into the SQL
                System.out.println("the song " + theEntity + " isnt already in the db");
                String addToSQLStatement = theEntity.toSQL(); //runtime polymorphism
                statement.executeUpdate(addToSQLStatement);
                System.out.println("executing update with: " + addToSQLStatement);
            }

        }
        catch(SQLException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        finally {
            try{
                if(connection != null){
                    connection.close();
                }
            }
            catch (SQLException e){
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args){
        Library2 lib = new Library2();
        Scanner inputScanner = new Scanner(System.in);

        ArrayList<Entity> arrOfEntities = new ArrayList<>();

        System.out.println("Welcome");
        boolean goAgain = true;
        while(goAgain){
            System.out.println("type songs, artists, or albums to view");
            System.out.println("or type g to generate playlist");
            System.out.println("or type new entry to enter a new entry into the database");
            System.out.println("or type end to end");
            String userInput = inputScanner.nextLine();
            if(userInput.equals("songs")){
                arrOfEntities = getFromSQL2(musicdbTestURL, "songs");
                for(int i = 0; i < arrOfEntities.size(); i++){
                    System.out.println("name: " + arrOfEntities.get(i).getName()
                            + ", id: " + arrOfEntities.get(i).getID()
                            + ", artist: " + arrOfEntities.get(i).getArtist().getName());
                }
            }
            else if(userInput.equals("artists")){
                arrOfEntities = getFromSQL2(musicdbTestURL, "artists");
                for(int i = 0; i < arrOfEntities.size(); i++){
                    System.out.println("name: " + arrOfEntities.get(i).getName()
                            + ", id: " + arrOfEntities.get(i).getID());
                }
            }
            else if(userInput.equals("albums")){
                arrOfEntities = getFromSQL2(musicdbTestURL, "albums");
                for(int i = 0; i < arrOfEntities.size(); i++){
                    System.out.println("name: " + arrOfEntities.get(i).getName()
                            + ", id: " + arrOfEntities.get(i).getID()
                            + ", artist: " + arrOfEntities.get(i).getArtist().getName());
                }
            }
            else if(userInput.equals("g")){
                System.out.println("type an artist you want for a playlist");
                String userArtist = inputScanner.nextLine();
                boolean askAgain = true;
                while(askAgain){
                    System.out.println("type r for a random playlist or n for a normal playlist");
                    System.out.println("or type back to go to previous menu");
                    String userRorN = inputScanner.nextLine();
                    if(userRorN.equals("r")){
                        Playlist allSongsPlaylist = new Playlist("allSongsPlaylist");
                        ArrayList<Song> allSongs = getFromSQL2(musicdbTestURL, "songs");
                        //making a playlist of all the songs
                        for(int i = 0; i < allSongs.size(); i++){
                            allSongsPlaylist.addSong(allSongs.get(i));
                        }
                        System.out.println("---songs in the random playlist!!!----");
                        Playlist theRandPlayList2 = new Playlist("therandlist2", allSongsPlaylist.randomListArtist(userArtist));
                        //randomising the playlist
                        for(int i = 0; i < theRandPlayList2.getSongs().size(); i++){
                            System.out.println(theRandPlayList2.getSongs().get(i).name);
                        }
                        ArrayList<Song> randList = theRandPlayList2.getSongs();
                        for(int i = 0; i < randList.size(); i++){
                            lib.addSongs(randList.get(i));
                        }
                        lib.writeXML();
                        System.out.println("playlist saved as 'theCreatedXML.xml'");
                    }
                    else if(userRorN.equals("n")){
                        ArrayList<Song> songsFromArtist = getSongsFromArtistFromSQL(musicdbTestURL, userArtist);
                        Playlist artistsPlaylist = new Playlist("the " + userArtist + " playlist");
                        System.out.println("---songs in the playlist!!!!----");
                        for(int i = 0; i < songsFromArtist.size(); i++){
                            artistsPlaylist.addSong(songsFromArtist.get(i));
                            System.out.println("added: " + songsFromArtist.get(i).name);
                        }
                        for(int i = 0; i < songsFromArtist.size(); i++){
                            lib.addSongs(songsFromArtist.get(i));
                        }
                        lib.writeXML();
                        System.out.println("playlist saved as 'theCreatedXML.xml'");
                    }
                    else if (userRorN.equals("back")){
                        askAgain = false;
                    }
                }
            }
            else if(userInput.equals("new entry")){
                boolean askAgain2 = true;
                while(askAgain2){
                    System.out.println("type 1 if you know song name, artist, and need the album");
                    System.out.println("type 2 if you know song name, album, and need the artist");
                    System.out.println("type 3 if you know song name, artist, and album");
                    System.out.println("or type back to go back to the prev menu");
                    String userString = inputScanner.nextLine();
                    if(userString.equals("1")){
                        System.out.println("whats the song name?");
                        String userSongName = inputScanner.nextLine();
                        userSongName = userSongName.replaceAll("\\s+","%20");
                        System.out.println("whats the artist name?");
                        String userArtistName = inputScanner.nextLine();
                        userArtistName = userArtistName.replaceAll("\\s+","%20");
                        String theAlbumName = getInfo1.songArtistReturnAlbum(userSongName, userArtistName);
                        System.out.println(theAlbumName);
                        Song userSong = new Song(userSongName, theAlbumName, userArtistName);

                        //get the last ID from song
                        userSong.setEntityID(getLastID(musicdbTestURL, "songs") + 1);

                        writeToSQL(musicdbTestURL, userSong);
                        System.out.println("wrote [" + userSong + "] to the database");
                    }
                    else if(userString.equals("2")){
                        System.out.println("whats the song name?");
                        String userSongName = inputScanner.nextLine();
                        userSongName = userSongName.replaceAll("\\s+","%20");
                        System.out.println("whats the album name?");
                        String userAlbumName = inputScanner.nextLine();
                        userAlbumName = userAlbumName.replaceAll("\\s+","%20");
                        String theArtistName = getInfo1.albumReturnArtist(userAlbumName);
                        System.out.println(theArtistName);
                        Song userSong = new Song(userSongName, userAlbumName, theArtistName);
                        userSong.setEntityID(getLastID(musicdbTestURL, "songs") + 1);
                        writeToSQL(musicdbTestURL, userSong);
                        System.out.println("wrote [" + userSong + "] to the database");
                    }
                    else if(userString.equals("3")){
                        System.out.println("whats the song name?");
                        String userSongName = inputScanner.nextLine();
                        System.out.println("whats the artist name?");
                        String userArtistName = inputScanner.nextLine();
                        System.out.println("whats the album name?");
                        String userAlbumName = inputScanner.nextLine();
                        Song userSong = new Song(userSongName, userAlbumName, userArtistName);
                        userSong.setEntityID(getLastID(musicdbTestURL, "songs") + 1);
                        writeToSQL(musicdbTestURL, userSong);
                        System.out.println("wrote [" + userSong + "] to the database");

                    }
                    else if(userString.equals("back")){
                        askAgain2 = false;
                    }
                }
            }
            else if(userInput.equals("end")){
                goAgain = false;
            }
        }



    }

    /**
     * used to get the entityID of the last row of the songs, artists, or albums
     * @return the entityID of the last row for whichever parameter is whatEntity (either song, artist, album)
     */
    public static int getLastID(String theURL, String whatEntity){
        //get the last ID from song
        int lastID = 0;
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(theURL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("select * from " + whatEntity);
            boolean nextAvail = false;
            nextAvail = rs.next();
            while(nextAvail){
                lastID = rs.getInt("id");
                if (rs.next()){
                }
                else{
                    nextAvail = false;
                }
            }
            System.out.println(lastID);
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
        finally {
            try{
                if(connection != null){
                    connection.close();
                }
            }
            catch (SQLException e){
                System.err.println(e.getMessage());
            }
        }
        return lastID;
    }
}
