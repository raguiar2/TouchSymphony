package edu.stanford.cs108.touchsymphony;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import  android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ComposeActivity extends AppCompatActivity {
    private static final String TAG = "ComposeActivity";
    private AdView mAdView;
    touchView touchView;
    private PersistentSaver saver;
    private Button recordButton;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        touchView = (touchView) findViewById(R.id.touchview);
        View layout = (View) findViewById(R.id.mainlayout);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        saver=PersistentSaver.getInstance(getApplicationContext());
        recordButton= (Button) findViewById(R.id.record_button);
        Spinner spinner = (Spinner) findViewById(R.id.instruments_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.insturments_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    //On click to toggle recording song or not.
    //Displays a view to name song afterwards
    public void toggleRecording(View view) {
        touchView.setRecording(!touchView.isRecording());
        //end of song
        if (!touchView.isRecording()) {
            recordButton.setText("RECORD");

            AlertDialog.Builder builder = new AlertDialog.Builder(ComposeActivity.this);
            builder.setTitle("Enter new name");

            // Set up the input
            final EditText input = new EditText(getApplicationContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Name", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();

                    //Error checking for shape names
                    if (saver.uniqueSong(name) && !name.equals(" ") && !name.equals("")) {
                        saver.insertSong(name);
                       // saver.
                    }
                    else if (name.equals(" ") || name.equals("")){
                        Toast.makeText(getApplicationContext(), "Please type in a valid name.",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        //display a TOAST saying this name has already been taken
                        Toast.makeText(getApplicationContext(), "Please select a unique song name.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
        else {
            recordButton.setText("STOP");

            touchView.setStarttime(System.currentTimeMillis());
        }

    }
}
