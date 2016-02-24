package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jordan on 23/02/2016.
 */
public class InfiniteModeStart extends Activity {
    DBFunc dbFunc = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infinite_start);

        Spinner dropdown = (Spinner) findViewById(R.id.number_of_questions);
        int maxNumber = dbFunc.getTotalNumberOfQuestions("questions");
        ArrayList<String> items = new ArrayList<String>();
        items.add("All Questions"); // first element = "All Questions"
        for(int i=(maxNumber-1); i>0; i--){
            String elem = String.valueOf(i); // convert number to string
            items.add(elem); // add it to ArrayList
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }


    public void startInfiniteGame(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.number_of_questions);

        Intent startGame = new Intent(InfiniteModeStart.this, InfiniteGame.class);

        final int result = 1; // signal
        startGame.putExtra("numberOfQuestionsKey", spinner.getSelectedItem().toString());

        // call activity to run and don't expect data to be sent back
        //startActivity(startGame);

        // call activity to run and retrieve data back
        startActivityForResult(startGame, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // return result from infinite game
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String correctAnswers = data.getStringExtra("correctAnswersKey");
                TextView a = (TextView) findViewById(R.id.infinite_game_mode_description);
                a.setText(correctAnswers);
                // TODO create scoreboard table & add in results (with username)
            }
        }
    }
}