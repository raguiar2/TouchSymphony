package edu.stanford.cs108.touchsymphony;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SongPlayer extends AppCompatActivity {


    //Plays songs with the playsong button.

    //This code is sort of the "shell" activity code for the view.

    private  PlayView playView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        playView = (PlayView) findViewById(R.id.playview);
    }

    public void playSong(View view){
        playView.setStartTime(System.currentTimeMillis());
        playView.playSong();
    }

}
