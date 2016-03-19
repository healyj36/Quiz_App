package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Jordan on 10/03/2016.
 */
public class ChatRoom extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        initCustomTypeFace();

    }

    private void initCustomTypeFace() {
        TextView txt = (TextView) findViewById(R.id.chat_room_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/agency-fb.ttf");
        txt.setTypeface(font);
    }

    public void goToServer(View view) {
        Intent intent = new Intent(ChatRoom.this, ServerActivity.class);
        startActivity(intent);
    }

    public void goToClient(View view) {
        Intent intent = new Intent(ChatRoom.this, ClientActivity.class);
        startActivity(intent);
    }
}
