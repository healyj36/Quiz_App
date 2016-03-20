package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jordan on 23/02/2016.
 */
public class InfiniteModeStart extends Activity {
    private final DBFunc DB_FUNC = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infinite_start);
        initCustomTypeFace();


        Spinner dropdown_subject = (Spinner) findViewById(R.id.spinner_subjects);
        ArrayList<String> subjects = DB_FUNC.getSubjects();
        // add "All Subjects" to top of list / spinner
        subjects.add(0, "All Subjects");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjects);
        dropdown_subject.setAdapter(adapter1);



        dropdown_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String subjectName = parent.getItemAtPosition(position).toString();
                int maxNumber = DB_FUNC.getNumberOfQuestionsBySubject(subjectName);

                TextView numQuestionsView = (TextView) findViewById(R.id.infinite_game_mode_number_of_questions_text_view);
                if(subjectName.equals("All Subjects")) {
                    String numQuestions = "There are " + maxNumber + " questions in total";
                    numQuestionsView.setText(numQuestions);
                } else {
                    String numQuestions = "There are " + maxNumber + " questions in the subject " + subjectName;
                    numQuestionsView.setText(numQuestions);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

    }

    public void startInfiniteGame(View view) {
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner_subjects);

        Intent startGame = new Intent(InfiniteModeStart.this, InfiniteGame.class);

        final int requestCode = 1; // signal

        startGame.putExtra("subjectKey", spinner1.getSelectedItem().toString());

        // call activity to run and retrieve data back
        startActivityForResult(startGame, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // return result from infinite game
        // requestCode == 1, means coming from activity InfiniteGame.class
        if (requestCode == 1) {
            // resultCode == Activity.RESULT_OK, game finished cleanly,
            // user answered a question wrong or answered all questions
            if(resultCode == Activity.RESULT_OK){
                // RESULT_OK is value of -1
                String correctAnswers = data.getStringExtra("correctAnswersKey");
                TextView a = (TextView) findViewById(R.id.infinite_game_mode_description);
                TextView b = (TextView) findViewById(R.id.infinite_game_mode_out_of_time);
                a.setText(correctAnswers);
                b.setText("");
            }
            // resultCode == Activity.RESULT_OK+2, game finished cleanly, ran out of time
            if(resultCode == Activity.RESULT_OK+2) {
                // RESULT_OK+2 is value of 1
                String gameOver = data.getStringExtra("gameOverKey");
                String outOfTime = data.getStringExtra("outOfTimeKey");
                TextView a = (TextView) findViewById(R.id.infinite_game_mode_description);
                TextView b = (TextView) findViewById(R.id.infinite_game_mode_out_of_time);
                a.setText(gameOver);
                b.setText(outOfTime);
            }
            // resultCode == Activity.RESULT_OK+3, game finished cleanly, quit game
            if(resultCode == Activity.RESULT_OK+3) {
                // RESULT_OK+3 is value of 2
                String gameOver = data.getStringExtra("gameOverKey");
                String quit = data.getStringExtra("quitKey");
                TextView a = (TextView) findViewById(R.id.infinite_game_mode_description);
                TextView b = (TextView) findViewById(R.id.infinite_game_mode_out_of_time);
                a.setText(gameOver);
                b.setText(quit);
            }
            // resultCode == 0, do nothing. if user exited game (activity)
        }
    }

    private void initCustomTypeFace() {
        TextView txt = (TextView) findViewById(R.id.infinite_game_mode_intro_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/agency-fb.ttf");
        txt.setTypeface(font);
    }
}