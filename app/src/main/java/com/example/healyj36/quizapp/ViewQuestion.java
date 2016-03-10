package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        Intent calledFromMain = getIntent();

        String questionChosen = calledFromMain.getExtras().getString("questionKey");

        TextView question_text_view = (TextView) findViewById(R.id.question_text_view);
        question_text_view.setText(questionChosen);

        ArrayList<String> options = DB_FUNC.findOptionsByQuestion(questionChosen);

        TextView option1 = (TextView) findViewById(R.id.option1_button_text_view);
        option1.setText(options.get(0));
        TextView option2 = (TextView) findViewById(R.id.option2_button_text_view);
        option2.setText(options.get(1));
        TextView option3 = (TextView) findViewById(R.id.option3_button_text_view);
        option3.setText(options.get(2));
        TextView option4 = (TextView) findViewById(R.id.option4_button_text_view);
        option4.setText(options.get(3));

    }

    public void getAnswer(View view) {
        String chosenAnswer = ((Button) view).getText().toString();

        TextView t = (TextView) findViewById(R.id.question_text_view);
        String ques = t.getText().toString();

        boolean isAnswer = DB_FUNC.isAnswer(ques, chosenAnswer);
        TextView a = (TextView) findViewById(R.id.chosen_answer_text_view);
        a.setText(String.valueOf(isAnswer));


        // this.finish(); //to close activity when button is pressed (goes back to main activity)
        /*
        TODO function to add to record of previous correct or incorrect answers
         */
    }
}
