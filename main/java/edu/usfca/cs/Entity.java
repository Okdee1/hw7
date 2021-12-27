package edu.usfca.cs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Entity {
    protected String name;
    protected static int counter = 0;
    protected int entityID;
    protected Date dateCreated;

    public Entity() {
        this.name = "";
        counter++;
        this.entityID = counter;
        dateCreated = new Date();
    }

    public boolean equals(Entity otherEntity) {
        return entityID == otherEntity.entityID;
    }


    public Entity(String name) {
        this.name = name;
        if(incrementCounter){
            counter++;
        }
        this.entityID = counter;
        dateCreated = new Date();
        incrementCounter = true;
    }
    boolean incrementCounter = true;
    public void setIncrementCounter(boolean theBool){
        incrementCounter = theBool;
    }

    public void setEntityID(int theEntID){
        entityID = theEntID;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public Integer getID(){
        return entityID;
    }

    public Artist getArtist(){
        return null;
    }

    public Album getAlbum(){
        return null;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Name: " + this.name + " Entity ID: " + this.entityID;
    }
    public String toHTML() {
        return "<b>" + this.name + "</b><i> " + this.entityID + "</i>";
    }
    public String toXML() {
        return "<entity><name>" + this.name + "</name><ID> " + this.entityID + "</ID></entity>";
    }

    public String toSQL(){
        return "aa";
    }

    public void fromSQL(ResultSet rs) throws SQLException {
    }
}
