package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

public class Online extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_game);

        new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Jordan"));
    }
}
