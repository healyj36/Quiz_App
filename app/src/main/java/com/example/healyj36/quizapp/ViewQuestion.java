package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jordan on 20/02/2016.
 */


public class ViewQuestion extends Activity {
    private final DBFunc DB_FUNC = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_entry);
        // hide progress bars, not used in this activity
        ProgressBar lpp = (ProgressBar) findViewById(R.id.local_player_progress);
        ProgressBar op = (ProgressBar) findViewById(R.id.opponent_progress);
        ProgressBar cdt = (ProgressBar) findViewById(R.id.countdown_timer);
        lpp.setVisibility(View.GONE);
        op.setVisibility(View.GONE);
        cdt.setVisibility(View.GONE);


        Intent calledFromMain = getIntent();

        // get question chosen from ViewQuestions ListView
        String questionChosen = calledFromMain.getExtras().getString("questionKey");

        TextView question_text_view = (TextView) findViewById(R.id.question_text_view);
        question_text_view.setText(questionChosen);

        ArrayList<String> options = DB_FUNC.findOptionsByQuestion(questionChosen);

        // display questions and options
        TextView option1 = (TextView) findViewById(R.id.option1_button_text_view);
        option1.setText(options.get(0));
        TextView option2 = (TextView) findViewById(R.id.option2_button_text_view);
        option2.setText(options.get(1));
        TextView option3 = (TextView) findViewById(R.id.option3_button_text_view);
        option3.setText(options.get(2));
        TextView option4 = (TextView) findViewById(R.id.option4_button_text_view);
        option4.setText(options.get(3));

    }

    public void getResponse(View view) {
        // was the users choice correct
        String chosenAnswer = ((Button) view).getText().toString();

        TextView t = (TextView) findViewById(R.id.question_text_view);
        String ques = t.getText().toString();

        boolean isAnswer = DB_FUNC.isAnswer(ques, chosenAnswer);
        TextView a = (TextView) findViewById(R.id.chosen_answer_text_view);
        a.setText(String.valueOf(isAnswer));
    }
}
