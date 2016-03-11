package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Jordan on 10/03/2016.
 */
public class Settings extends Activity {
    // add other values to list to make them appear in listview
    private String[] list = {"Chat Room"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        ListView listView = (ListView) findViewById(R.id.settings_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // TODO this will open the chatroom activity when any item in the list is clicked
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openChatRoom = new Intent(Settings.this, ChatRoom.class);
                startActivity(openChatRoom);
            }
        });

    }
}
