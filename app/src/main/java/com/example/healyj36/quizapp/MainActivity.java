package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCustomTypeFace(R.id.app_title);

    }

    private void initCustomTypeFace(int textView) {
        TextView txt = (TextView) findViewById(textView);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/agency-fb.ttf");
        txt.setTypeface(font);
    }

    public void goTo1v1(View view) {
        Intent view1v1 = new Intent(MainActivity.this, OnlineStart.class);

        // final int result = 1; // signal

        // call activity to run and don't expect data to be sent back
        startActivity(view1v1);

        // call activity to run and retrieve data back
        // startActivityForResult(viewAllQues, result);
    }

    public void goToAllQuestions(View view) {
        Intent viewAllQues = new Intent(MainActivity.this, AllQuestions.class);

        // final int result = 1; // signal

        // call activity to run and don't expect data to be sent back
        startActivity(viewAllQues);

        // call activity to run and retrieve data back
        // startActivityForResult(viewAllQues, result);
    }

    public void goToInfiniteGameMode(View view) {
        Intent viewInfinite = new Intent(MainActivity.this, InfiniteModeStart.class);

        // final int result = 1; // signal

        // call activity to run and don't expect data to be sent back
        startActivity(viewInfinite);

        // call activity to run and retrieve data back
        // startActivityForResult(viewInfinite, result);
    }

    public void goToSettings(View view) {
        Intent viewSettings = new Intent(MainActivity.this, Settings.class);

        // final int result = 1; // signal

        // call activity to run and don't expect data to be sent back
        startActivity(viewSettings);

        // call activity to run and retrieve data back
        // startActivityForResult(viewInfinite, result);
    }
}

