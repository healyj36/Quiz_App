package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goTo1v1(View view) {
        Intent view1v1 = new Intent(MainActivity.this, OnlineStart.class);

        // call activity to run and don't expect data to be sent back
        startActivity(view1v1);
    }

    public void goToAllQuestions(View view) {
        Intent viewAllQues = new Intent(MainActivity.this, AllQuestions.class);

        // call activity to run and don't expect data to be sent back
        startActivity(viewAllQues);
    }

    public void goToInfiniteGameMode(View view) {
        Intent viewInfinite = new Intent(MainActivity.this, InfiniteModeStart.class);

        // call activity to run and don't expect data to be sent back
        startActivity(viewInfinite);
    }

    public void goToChatRoom(View view) {
        Intent viewChatRoom = new Intent(MainActivity.this, ChatRoom.class);

        // call activity to run and don't expect data to be sent back
        startActivity(viewChatRoom);
    }
}

