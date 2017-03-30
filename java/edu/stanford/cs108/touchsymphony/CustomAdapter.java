package edu.stanford.cs108.touchsymphony;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by ruiaguiar1 on 3/27/17.
 */

//custom adapter for song list

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> arrlist = new ArrayList<String>();
    private Context context;
    private PersistentSaver saver;


    public CustomAdapter(ArrayList<String> list, Context context, boolean isWorld, HashMap<String, String> pagesMaps) {
        this.arrlist = list;
        this.context = context;
        this.saver = PersistentSaver.getInstance(context);
    }

    @Override
    public int getCount() {
        return arrlist.size();
    }

    @Override
    public Object getItem(int pos) {
        return arrlist.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    //Handles what happens when user presses delete and play buttons
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_layout, null);
        }
        //Handle TextView and display string from your list
        final TextView listItemText = (TextView)view.findViewById(R.id.rowname);
        listItemText.setText(arrlist.get(position));
        listItemText.setTextColor(Color.WHITE);
        listItemText.setTextSize(25);

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button playBtn = (Button)view.findViewById(R.id.play_button);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saver.deleteSong(listItemText.getText().toString());
                arrlist.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currsong = listItemText.getText().toString();
                saver.setCurrSongName(currsong);
                Intent intent = new Intent(context, SongPlayer.class);
                context.startActivity(intent);
            }
        });


        return view;
    }



}
