package edu.stanford.cs108.touchsymphony;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Configuration;

//"Main" activity. Configures setting up buttons, etc

public class OnOpen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_open);
        TextView request = (TextView) findViewById(R.id.request_text);
        int config = getResources().getConfiguration().orientation;
        if(config==(Configuration.ORIENTATION_LANDSCAPE)){
            request.setText("");
        }
        if(config==(Configuration.ORIENTATION_PORTRAIT)){
            request.setText(getApplicationContext().getString(R.string.request_text));
        }
    }

    public void toCompose(View view){
        Intent intent = new Intent(this, ComposeActivity.class);
        startActivity(intent);
    }
    public void toSongsMain(View view){
        Intent intent = new Intent(this, SongList.class);
        startActivity(intent);
    }
}

