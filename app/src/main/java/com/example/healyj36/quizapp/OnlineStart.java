package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OnlineStart extends Activity {
    private final DBFunc DB_FUNC = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_game);
        initCustomTypeFace();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            // check if Bundle is null first
            // will be null on first open
            String scoreText = extras.getString("scoreKey");
            // if scoreText is empty then the quiz finished unexpectedly
            if (!scoreText.equals("")) {
                scoreText = scoreText.substring(1, scoreText.length() - 1);
                String[] scores = scoreText.split(", ");

                boolean wasHost = extras.getBoolean("wasHostKey", false);

                int numQuestions = extras.getInt("numQuestionsKey", 0);
                // 0 here is the default value
                // if the number of questions cant be received from the intent
                // numQuestions will be 0

                int yourScore, theirScore;
                String str = "The game is finished.\n";
                str += "There was " + numQuestions + " questions.\n";
                String yourName;
                String theirName;
                if (wasHost) {
                    yourName = extras.getString("nicknameHostKey");
                    yourScore = Integer.parseInt(scores[0]);
                    theirName = extras.getString("nicknameClientKey");
                    theirScore = Integer.parseInt(scores[1]);
                } else {
                    yourName = extras.getString("nicknameClientKey");
                    yourScore = Integer.parseInt(scores[1]);
                    theirName = extras.getString("nicknameHostKey");
                    theirScore = Integer.parseInt(scores[0]);
                }

                str += "\"" + yourName + "\" correctly answered " + yourScore + " questions.\n";
                str += "\"" + theirName + "\" correctly answered " + theirScore + " questions.\n";
                TextView a = (TextView) findViewById(R.id.online_game_mode_score);
                a.setText(str);
            }
        }

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
                for (int i = (maxNumber - 1); i > 0; i--) {
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
        EditText nNameEditText = (EditText) findViewById(R.id.online_enter_name);
        if(!nNameEditText.getText().toString().equals("")) {
            Spinner spinner1 = (Spinner) findViewById(R.id.spinner_subjects);
            Spinner spinner2 = (Spinner) findViewById(R.id.number_of_questions);

            Intent hostGame = new Intent(OnlineStart.this, WaitForClient.class);

            hostGame.putExtra("subjectKey", spinner1.getSelectedItem().toString());
            hostGame.putExtra("numberOfQuestionsKey", spinner2.getSelectedItem().toString());
            hostGame.putExtra("nicknameHostKey", nNameEditText.getText().toString());

            hostGame.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(hostGame);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter your nickname", Toast.LENGTH_SHORT).show();
        }
    }

    public void joinChosen(View view) {
        EditText nNameEditText = (EditText) findViewById(R.id.online_enter_name);
        if(!nNameEditText.getText().toString().equals("")) {
            Intent joinGame = new Intent(OnlineStart.this, ListHosts.class);
            joinGame.putExtra("nicknameClientKey", nNameEditText.getText().toString());

            joinGame.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(joinGame);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter your nickname", Toast.LENGTH_SHORT).show();
        }
    }
}
