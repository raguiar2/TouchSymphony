package edu.stanford.cs108.touchsymphony;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class SongList extends AppCompatActivity {
    private PersistentSaver saver;
    private ListView listView;

    //List of songs with custom adapter.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        listView=(ListView) findViewById(R.id.songTableView);
        saver = (PersistentSaver) PersistentSaver.getInstance(getApplicationContext());
        refreshListView();

    }

    public void refreshListView(){
        ArrayList<String> songs = saver.getSongs();
        CustomAdapter adapter = new CustomAdapter(songs, this,true,null);
        listView.setAdapter(adapter);
    }


}
