package edu.usfca.cs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class XMLandJSONParser {

    public static void main(String[] args){
        parseXML("music-library.xml");
        //parseJSON("music-library.json");
    }

    public static String getContent(Node n){
        StringBuilder sb = new StringBuilder();
        Node child = n.getFirstChild();
        sb.append(child.getNodeValue());
        return sb.toString();
    }

    public static void parseJSON(String theString){
        String s;
        String fileName = theString;
        try {
            Scanner sc = new Scanner(new File(fileName));
            sc.useDelimiter("\\Z");
            s = sc.next();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(s);
            JSONObject jsonObject = (JSONObject)obj;

            Song currentSong;
            Album currentAlbum;
            Artist currentArtist;
            Library theLib = new Library();

            HashMap<Integer, Artist> theArtistMap = new HashMap<Integer, Artist>();
            HashMap<Integer, Album> theAlbumMap = new HashMap<Integer, Album>();

            JSONArray songArray = (JSONArray)jsonObject.get("songs");
            for (Object song : songArray) {
                JSONObject jsong = (JSONObject)song;
                currentSong = new Song();
                currentSong.entityID = Integer.parseInt(jsong.get("id").toString());
                currentSong.setName(jsong.get("title").toString());

                Map artist = (Map)jsong.get("artist");
                int artistId = Integer.parseInt(artist.get("id").toString());
                String artistString = artist.get("name").toString();
                if(theArtistMap.containsKey(artistId) == false){
                    currentArtist = new Artist(artistString);
                    currentArtist.entityID = artistId;
                    currentSong.setArtist(currentArtist);
                    theArtistMap.put(artistId, currentArtist);
                }
                else{
                    currentSong.setArtist(theArtistMap.get(artistId));
                }

                Map album = (Map)jsong.get("album");
                int albumId = Integer.parseInt(album.get("id").toString());
                String albumString = album.get("name").toString();
                if(theAlbumMap.containsKey(albumId) == false){
                    currentAlbum = new Album(albumString);
                    currentAlbum.entityID = albumId;
                    currentSong.setAlbum(currentAlbum);
                    theAlbumMap.put(albumId, currentAlbum);
                }
                else{
                    currentSong.setAlbum(theAlbumMap.get(albumId));
                }

                theLib.addSong(currentSong);
                System.out.println(currentSong);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + e);
        } catch (ParseException e1) {
            System.out.println("Parser error");
        }
    }

    public static void parseXML(String theString){
        String fileName = theString;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(fileName));

            Element root = doc.getDocumentElement();
            System.out.println("theRoot is: " + root);
            NodeList songs = root.getElementsByTagName("song");
            Node currentNode, subNode;

            Song currentSong;
            Artist currentArtist;
            Album currentAlbum;
            Library theLib = new Library();

            HashMap<Integer, Artist> theArtistMap = new HashMap<Integer, Artist>();

            for(int i = 0; i < songs.getLength(); i++){
                currentNode = songs.item(i);
                Element num = (Element) currentNode;
                String id = num.getAttribute("id");
                //System.out.println("id: "  + id);
                NodeList children = currentNode.getChildNodes();
                currentSong = new Song();
                currentSong.entityID = Integer.parseInt(id);
                for(int j = 0; j < children.getLength(); j++){
                    subNode = children.item(j);
                    if(subNode.getNodeType() == Node.ELEMENT_NODE){
                        Element name = (Element)subNode;
                        if(name.getNodeName().equals("title")){
                            currentSong.setName(getContent(name).trim());
                            //System.out.println(currentSong.getName());
                        }
                        else if(name.getNodeName().equals("artist")){
                            id = name.getAttribute("id");
                            if(theArtistMap.containsKey(Integer.parseInt(id)) == false){
                                currentArtist = new Artist(getContent(subNode).trim());
                                currentArtist.entityID = Integer.parseInt(id);
                                currentSong.setArtist(currentArtist);
                                theArtistMap.put(Integer.parseInt(id), currentArtist);
                            }
                            else{
                                currentSong.setArtist(theArtistMap.get(Integer.parseInt(id)));
                            }
                        }
                        else if(name.getNodeName().equals("album")){
                            id = name.getAttribute("id");
                            currentAlbum = new Album(getContent(subNode).trim());
                            currentAlbum.entityID = Integer.parseInt(id);
                            currentSong.setAlbum(currentAlbum);
                        }
                    }
                }
                theLib.addSong(currentSong);
                System.out.println(currentSong);
            }
            //songs, artists, albums
        } catch (Exception e) {
            System.out.println("Parsing error:" + e);
        }
    }

}
