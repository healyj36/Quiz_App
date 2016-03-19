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

import java.util.ArrayList;

public class OnlineStart extends Activity {
    private final DBFunc DB_FUNC = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_game);
        initCustomTypeFace();

        Spinner dropdown_subject = (Spinner) findViewById(R.id.spinner_subjects);
        ArrayList<String> subjects = DB_FUNC.getSubjects();
        // add "All Subjects" to top of list / spinner
        subjects.add(0, "All Subjects");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjects);
        dropdown_subject.setAdapter(adapter1);

        final Spinner dropdown_number = (Spinner) findViewById(R.id.number_of_questions);
        dropdown_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String subjectName = parent.getItemAtPosition(position).toString();
                int maxNumber = DB_FUNC.getNumberOfQuestionsBySubject(subjectName);
                ArrayList<String> numbers = new ArrayList<>();
                numbers.add("All Questions"); // first element = "All Questions"
                for(int i=(maxNumber-1); i>0; i--){
                    String elem = String.valueOf(i); // convert number to string
                    numbers.add(elem); // add it to ArrayList
                }

                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(OnlineStart.this, android.R.layout.simple_spinner_dropdown_item, numbers);
                dropdown_number.setAdapter(adapter2);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void initCustomTypeFace() {
        TextView txt = (TextView) findViewById(R.id.online_game_mode_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/agency-fb.ttf");
        txt.setTypeface(font);
    }

    public void hostChosen(View view) {
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner_subjects);
        Spinner spinner2 = (Spinner) findViewById(R.id.number_of_questions);
        Intent hostGame = new Intent(OnlineStart.this, HostGame.class);

        final int result = 1; // signal

        hostGame.putExtra("subjectKey", spinner1.getSelectedItem().toString());
        hostGame.putExtra("numberOfQuestionsKey", spinner2.getSelectedItem().toString());

        // call activity to run and retrieve data back
        startActivityForResult(hostGame, result);
    }

    public void joinChosen(View view) {
        Intent joinGame = new Intent(OnlineStart.this, JoinGame.class);

        final int result = 1; // signal

        // call activity to run and retrieve data back
        startActivityForResult(joinGame, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // return result from online game
        // if host game returns
        if (requestCode == 1) {
            // resultCode == Activity.RESULT_OK, game finished cleanly
            if (resultCode == Activity.RESULT_OK) {
                String scoreText = data.getStringExtra("scoreKey");
                scoreText = scoreText.substring(1, scoreText.length() - 1);
                String[] scores = scoreText.split(", ");

                boolean wasHost = data.getBooleanExtra("wasHostKey", false);

                int numQuestions = data.getIntExtra("numQuestionsKey", 0);
                // 0 here is the default value
                // if the number of questions cant be received from the intent
                // numQuestions will be 0

                int yourScore, theirScore;
                String str = "The game is finished.\n";
                str += "There was " + numQuestions + " questions.\n";
                if(wasHost) {
                    yourScore=Integer.parseInt(scores[0]);
                    theirScore=Integer.parseInt(scores[1]);
                } else {
                    yourScore=Integer.parseInt(scores[1]);
                    theirScore=Integer.parseInt(scores[0]);
                }

                str += "You correctly answered " + yourScore + " questions.\n";
                str += "Your opponent correctly answered " + theirScore + " questions.\n";
                TextView a = (TextView) findViewById(R.id.online_game_mode_score);
                a.setText(str);
            }
        }
    }
}
