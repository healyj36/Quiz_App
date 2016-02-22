package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jordan on 20/02/2016.
 */


public class ViewQuestion extends Activity {
    /*
    TextView question = (TextView) findViewById(R.id.question);
    TextView option1 = (TextView) findViewById(R.id.option1);
    TextView option2 = (TextView) findViewById(R.id.option2);
    TextView option3 = (TextView) findViewById(R.id.option3);
    TextView option4 = (TextView) findViewById(R.id.option4);
    */

    DBFunc dbFunc = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_entry);

        Intent calledFromMain = getIntent();

        String questionChosen = calledFromMain.getExtras().getString("questionKey");

        TextView question_text_view = (TextView) findViewById(R.id.question_text_view);
        question_text_view.setText(questionChosen);

        ArrayList<String> options = dbFunc.findOptionsByQuestion(questionChosen);

        TextView option1 = (TextView) findViewById(R.id.option1_button_text_view);
        option1.setText(options.get(0));
        TextView option2 = (TextView) findViewById(R.id.option2_button_text_view);
        option2.setText(options.get(1));
        TextView option3 = (TextView) findViewById(R.id.option3_button_text_view);
        option3.setText(options.get(2));
        TextView option4 = (TextView) findViewById(R.id.option4_button_text_view);
        option4.setText(options.get(3));

/*
        question = (TextView) findViewById(R.id.question);
        option1 = (TextView) findViewById(R.id.option1);
        option2 = (TextView) findViewById(R.id.option2);
        option3 = (TextView) findViewById(R.id.option3);
        option4 = (TextView) findViewById(R.id.option4);
*/
/*
        question.setText("no question");
        option1.setText("no option1");
        option2.setText("no option2");
        option3.setText("no option3");
        option4.setText("no option4");

        Intent theIntent = getIntent();

        String questionId = theIntent.getStringExtra("questionId");

        HashMap<String,String> questionList = dbFunc.getQuestionInfo(questionId);

        if(questionList.size() != 0) {
            // put the values questionList returned into the textviews
            question.setText(questionList.get("question"));
            option1.setText(questionList.get("option1"));
            option2.setText(questionList.get("option2"));
            option3.setText(questionList.get("option3"));
            option4.setText(questionList.get("option4"));
        }
        */
    }

    public void callMainActivity(View view) {
        Intent objIntent = new Intent(getApplication(), MainActivity.class);
        startActivity(objIntent);
    }
}
