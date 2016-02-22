package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by Jordan on 20/02/2016.
 */


public class ViewQuestion extends Activity {
    TextView question = (TextView) findViewById(R.id.question);
    TextView option1 = (TextView) findViewById(R.id.option1);
    TextView option2 = (TextView) findViewById(R.id.option2);
    TextView option3 = (TextView) findViewById(R.id.option3);
    TextView option4 = (TextView) findViewById(R.id.option4);

    DBFunc dbFunc = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_entry);
/*
        question = (TextView) findViewById(R.id.question);
        option1 = (TextView) findViewById(R.id.option1);
        option2 = (TextView) findViewById(R.id.option2);
        option3 = (TextView) findViewById(R.id.option3);
        option4 = (TextView) findViewById(R.id.option4);
*/
        question.setText("no question");
        option1.setText("no option1");
        option2.setText("no option2");
        option3.setText("no option3");
        option4.setText("no option4");

        Intent theIntent = getIntent();

        String questionId = theIntent.getStringExtra("questionId");

        HashMap<String,String> questionList = dbFunc.getQuestionInfo(questionId);
/*
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
