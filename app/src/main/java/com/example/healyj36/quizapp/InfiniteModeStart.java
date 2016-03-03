package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

        Spinner dropdown_subject = (Spinner) findViewById(R.id.spinner_subjects);
        ArrayList<String> subjects = DB_FUNC.getSubjects();
        // add "All Subjects" to top of list / spinner
        subjects.add(0, "All Subjects");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjects);
        dropdown_subject.setAdapter(adapter1);

        final Spinner dropdown_number = (Spinner) findViewById(R.id.number_of_questions);

        dropdown_subject.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String subjectName = parent.getItemAtPosition(position).toString();
                        int maxNumber = DB_FUNC.getNumberOfQuestionsBySubject(subjectName);
                        ArrayList<String> numbers = new ArrayList<>();
                        numbers.add("All Questions"); // first element = "All Questions"
                        for(int i=(maxNumber-1); i>0; i--){
                            String elem = String.valueOf(i); // convert number to string
                            numbers.add(elem); // add it to ArrayList
                        }

                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(InfiniteModeStart.this, android.R.layout.simple_spinner_dropdown_item, numbers);
                        dropdown_number.setAdapter(adapter2);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO do nothing
                    }
                });

        // on item selected {
        //String subjectName = dropdown_subject.getSelectedItem().toString();

        // }
    }

    public void startInfiniteGame(View view) {
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner_subjects);
        Spinner spinner2 = (Spinner) findViewById(R.id.number_of_questions);

        Intent startGame = new Intent(InfiniteModeStart.this, InfiniteGame.class);

        final int requestCode = 1; // signal
        startGame.putExtra("subjectKey", spinner1.getSelectedItem().toString());
        startGame.putExtra("numberOfQuestionsKey", spinner2.getSelectedItem().toString());

        // call activity to run and don't expect data to be sent back
        //startActivity(startGame);

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
                a.setText(correctAnswers);
                // TODO create scoreboard table & add in results (with username)
            }
            // resultCode == Activity.RESULT_OK+2, game finished cleanly, ran out of time
            if(resultCode == Activity.RESULT_OK+2) {
                // RESULT_OK+2 is value of 1
                String gameOver = data.getStringExtra("outOfTimeKey");
                TextView a = (TextView) findViewById(R.id.infinite_game_mode_description);
                a.setText(gameOver);
            }
            // resultCode == 0, do nothing. if user exited game (activity)
        }
    }
}