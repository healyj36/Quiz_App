package com.example.healyj36.quizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jordan on 23/02/2016.
 */
public class InfiniteGame extends Activity {
    DBFunc dbFunc = new DBFunc(this);
    private int correctAnswers = 0;
    ArrayList<HashMap<String, String>> allQuestions = new ArrayList<HashMap<String,String>>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_entry);

        try {
            dbFunc.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        String numberOfQuestionsString = "0";
        if (extras != null) {
            numberOfQuestionsString = extras.getString("numberOfQuestionsKey");
            if (numberOfQuestionsString.equals("All Questions")) {
                numberOfQuestionsString = String.valueOf(dbFunc.getTotalNumberOfQuestions("questions"));
            }
        }

        int numberOfQuestions = Integer.parseInt(numberOfQuestionsString);
        allQuestions = dbFunc.getQuestionsRandom(numberOfQuestions);

        showQuestion(i);
        i++;

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leaving Game")
                .setMessage("Are you sure you want to leave this game?\nYour data will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void getAnswer(View view) {
        Intent returnIntent = new Intent();

        String chosenAnswer = ((Button) view).getText().toString();

        TextView t = (TextView) findViewById(R.id.question_text_view);
        String ques = t.getText().toString();

        boolean isAnswer = dbFunc.isAnswer(ques, chosenAnswer);

        if(!isAnswer) { // if user chose wrong answer
            // return number of correct answers
            String correctAnswersString = String.valueOf(correctAnswers);
            correctAnswersString = "Your score is " + correctAnswersString;
            returnIntent.putExtra("correctAnswersKey",correctAnswersString);
            setResult(Activity.RESULT_OK, returnIntent);
            // exit game (close activity) (return to previous activity)
            this.finish();
        } else { // if user chose right answer
            correctAnswers++; // increment number of correct answers
            if(allQuestions.size() != i) {
                showQuestion(i); // show next question
                i++;
            }
            else { // if user chose right answer, but no more questions left
                // return number of correct answers
                String correctAnswersString = String.valueOf(correctAnswers);
                correctAnswersString = "Congratulations, you answered all the questions!\nYour score is " + correctAnswersString;
                returnIntent.putExtra("correctAnswersKey", correctAnswersString);
                setResult(Activity.RESULT_OK, returnIntent);
                // exit game (close activity) (return to previous activity)
                this.finish();
            }
        }
    }

    public void showQuestion(int index) {
        HashMap<String,String> question = allQuestions.get(index);

        String questionName = question.get("question");
        String option1 = question.get("option1");
        String option2 = question.get("option2");
        String option3 = question.get("option3");
        String option4 = question.get("option4");

        TextView question_text_view = (TextView) findViewById(R.id.question_text_view);
        question_text_view.setText(questionName);
        TextView option1TextView = (TextView) findViewById(R.id.option1_button_text_view);
        option1TextView.setText(option1);
        TextView option2TextView = (TextView) findViewById(R.id.option2_button_text_view);
        option2TextView.setText(option2);
        TextView option3TextView = (TextView) findViewById(R.id.option3_button_text_view);
        option3TextView.setText(option3);
        TextView option4TextView = (TextView) findViewById(R.id.option4_button_text_view);
        option4TextView.setText(option4);
    }
}