package com.example.healyj36.quizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    int numberOfQuestions;
    ProgressBar timer;
    MyCountDownTimer countDownTimer;

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

        numberOfQuestions = Integer.parseInt(numberOfQuestionsString);
        allQuestions = dbFunc.getQuestionsRandom(numberOfQuestions);

        showQuestion(i);
        i++;

        timer = (ProgressBar) findViewById(R.id.countdown_timer);
        // 10000 ms = 10s
        // 1000 ms = 1s
        // timer of 10 seconds counting in intervals of 1 second
        countDownTimer = new MyCountDownTimer(10000, 500);
        countDownTimer.start();

    }

    @Override
    public void onBackPressed() {
        // TODO don't allow user to press back button here
        // (or if they do, it doesn't do anything)
        // pause timer
        countDownTimer.pause();

        // block question when dialog is open
        // 0xff444444 is the colour of the questions textview (#444444)
        getWindow().setBackgroundDrawable(new ColorDrawable(0xff444444));
        new AlertDialog.Builder(this)
                .setTitle("Leaving Game")
                .setMessage("Are you sure you want to leave this game?\nYour data will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO when user leaves the previous message is still there. Maybe reset textview?
                        // if user plays game, finishes with a score of 3/5
                        // then the user plays again and quits using the back button
                        // the previous message ("Your score is 3/5") is still there
                        countDownTimer.cancel();
                        finish();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO this is hardcoded. need method to find value
                        // 0xfff3f3f3 is the colour of the default background (#f3f3f3)
                        getWindow().setBackgroundDrawable(new ColorDrawable(0xfff3f3f3));
                        countDownTimer.resume();
                    }
                })
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
            correctAnswersString = "Your score is " + correctAnswersString + "/" + numberOfQuestions;
            returnIntent.putExtra("correctAnswersKey",correctAnswersString);
            setResult(Activity.RESULT_OK, returnIntent);
            // exit game (close activity) (return to previous activity)
            this.finish();
        } else { // if user chose right answer
            correctAnswers++; // increment number of correct answers
            if(allQuestions.size() != i) {
                // cancel and start to reset timer
                countDownTimer.cancel();
                countDownTimer.start();
                showQuestion(i); // show next question
                i++;
            }
            else { // if user chose right answer, but no more questions left
                // return number of correct answers
                String correctAnswersString = String.valueOf(correctAnswers);
                correctAnswersString = "Congratulations, you answered all the questions!\nYour score is "
                        + correctAnswersString + "/" + numberOfQuestions;
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

    public class MyCountDownTimer extends CountDownTimer {
        private long millisLeft;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished/1000);
            millisLeft = millisUntilFinished;
            timer.setProgress(timer.getMax()-progress);
        }

        @Override
        public void onFinish() {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("outOfTimeKey", "GAME OVER!\nYou ran out of time");
            // TODO re-structure how this message looks on infinite_start.xml
            setResult(Activity.RESULT_OK+2, returnIntent);

            finish();
        }

        public void pause() {
            // delete / stop this timer
            this.cancel();
        }

        public void resume() {
            // create a new timer from the last tick
            // TODO 500ms is hardcoded
            // interval will always be 500ms
            // not really an issue for our implementation
            MyCountDownTimer newTimer = new MyCountDownTimer(millisLeft, 500);
            // start new timer
            newTimer.start();
        }
    }
}
