package edu.stanford.cs108.touchsymphony;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static android.content.Context.MODE_PRIVATE;
import java.util.ArrayList;


/**
 * Created by ruiaguiar1 on 3/26/17.
 */

//Singleton for storing data and carrying song names across activities.
class PersistentSaver {
    private final String NOTES_TABLE = "Notes";
    private final String SONGS_TABLE = "Songs";
    private final String DB_NAME = "TouchSymphonyDB.db";


    private String currSongName;



    private SQLiteDatabase db;

    private static PersistentSaver SingleInstanceOfDatabase;

    static PersistentSaver getInstance(Context context) {
        if (SingleInstanceOfDatabase == null){
            SingleInstanceOfDatabase = new PersistentSaver(context);
        }
        return SingleInstanceOfDatabase;
    }

    private PersistentSaver(Context context) {
        String query = "";
        db = context.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        query = "CREATE TABLE IF NOT EXISTS "+NOTES_TABLE+" (pressx REAL, pressy REAL, note STRING, song STRING, time REAL);";
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS "+SONGS_TABLE+" (name STRING);";
        db.execSQL(query);
        currSongName="";

    }
    //gets x coordinates of notes for song event.
    public ArrayList<Float> getPressXCoordinates(String song){
        ArrayList<Float> xCoordinates = new ArrayList<>();
        String query = "SELECT pressx FROM "+ NOTES_TABLE+ " WHERE song = " +"'"+song+"';";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for (int i=0; i<cursor.getCount(); i++){
                Float xCoordinate = cursor.getFloat(cursor.getColumnIndex("pressx"));
                xCoordinates.add(xCoordinate);
                // Get all pages contained in this world
                cursor.moveToNext();
            }
        }
        cursor.close();

        return xCoordinates;

    }

    //gets y coordinates of notes for song event.
    public ArrayList<Float> getPressYCoordinates(String song){
        ArrayList<Float> yCoordinates = new ArrayList<>();
        String query = "SELECT pressy FROM "+ NOTES_TABLE+ " WHERE song = " +"'"+song+"';";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for (int i=0; i<cursor.getCount(); i++){
                Float yCoordinate = cursor.getFloat(cursor.getColumnIndex("pressy"));
                yCoordinates.add(yCoordinate);
                // Get all pages contained in this world
                cursor.moveToNext();
            }
        }
        cursor.close();

        return yCoordinates;

    }

    //gets times of notes for song event.
    public ArrayList<Long> getTimesForNotes(String song){
        ArrayList<Long> Times = new ArrayList<>();
        String query = "SELECT time FROM "+ NOTES_TABLE+ " WHERE song = " +"'"+song+"';";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for (int i=0; i<cursor.getCount(); i++){
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                Times.add(time);
                // Get all pages contained in this world
                cursor.moveToNext();
            }
        }
        cursor.close();

        return Times;

    }
    //gets notes for song
    public ArrayList<String> getNotesForSong(String song){
        ArrayList<String> Notes = new ArrayList<>();
        String query = "SELECT note FROM "+ NOTES_TABLE+ " WHERE song = " +"'"+song+"';";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for (int i=0; i<cursor.getCount(); i++){
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Notes.add(note);
                // Get all pages contained in this world
                cursor.moveToNext();
            }
        }
        cursor.close();
        return Notes;

    }


    public void insertSong(String name){
        //store in songs table- also replace all notes
        String insert = "INSERT INTO Songs VALUES " + "('"+name+"')" + ";";
        db.execSQL(insert);
        insert = "UPDATE Notes SET song = "+ "'"+name+"'"+ " WHERE song = ' ';";
        db.execSQL(insert);

    }
    //Inserts a note into the notes table.
    public void insertNote(String note_name, float pressx, float pressy, long time){
        String songname = " ";
        String insert = "INSERT INTO "+ NOTES_TABLE+ " VALUES " +
                "("+"'"+pressx+"',"
                +"'"+pressy+"',"
                + "'"+note_name+"',"
                + "'"+songname+"',"
                +  "'"+time+"'"+
                ")" + ";";
        db.execSQL(insert);
    }

    //Deletes song from song and notes table.
    public void deleteSong(String song){
        String deleteQuery = "DELETE FROM Songs WHERE name = " +"'"+song+"';";
        db.execSQL(deleteQuery);
        deleteQuery = "DELETE FROM Notes WHERE song = " +"'"+song+"';";
        db.execSQL(deleteQuery);

    }


    public ArrayList<String> getSongs(){
        ArrayList<String> songs = new ArrayList<>();
        String query = "SELECT * FROM songs;";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for (int i=0; i<cursor.getCount(); i++){
                String songName = cursor.getString(cursor.getColumnIndex("name"));
                songs.add(songName);
                // Get all pages contained in this world
                cursor.moveToNext();
            }
        }
        cursor.close();

        return songs;
    }

    //Checks if song is unique

   public boolean uniqueSong(String song){
       String query = "SELECT name FROM "+SONGS_TABLE+" WHERE name = " +"'"+song+"'";
       Cursor cursor = db.rawQuery(query, null);
       if(cursor.getCount()==0) return true;
       return false;
    }

    public String getCurrSongName() {
        return currSongName;
    }

    public void setCurrSongName(String currSongName) {
        this.currSongName = currSongName;

    }
}
