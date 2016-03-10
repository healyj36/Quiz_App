package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Jordan on 10/03/2016.
 */
public class ChatRoom extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);


    }

    public void gotoserver(View view) {
        Intent intent = new Intent(ChatRoom.this, ServerActivity.class);
        startActivity(intent);
    }

    public void gotoclient(View view) {
        Intent intent = new Intent(ChatRoom.this, ClientActivity.class);
        startActivity(intent);
    }
}
