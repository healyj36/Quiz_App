package com.example.healyj36.quizapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jordan on 23/02/2016.
 */
public class InfiniteGame extends Activity {
    private final DBFunc DB_FUNC = new DBFunc(this);
    private int correctAnswers = 0;
    private ArrayList<HashMap<String, String>> allQuestions = new ArrayList<>();
    private int i = 0;
    private int numberOfQuestions;
    private ProgressBar timer;
    private MyCountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_entry);
        // hide the progress bars, not used in this game mode
        ProgressBar lpp = (ProgressBar) findViewById(R.id.local_player_progress);
        ProgressBar op = (ProgressBar) findViewById(R.id.opponent_progress);
        lpp.setVisibility(View.GONE);
        op.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        // receive subject and number of questions from the previous activity
        String numberOfQuestionsString = "0";
        String subject = "0";
        if (extras != null) {
            subject = extras.getString("subjectKey");
        }

        numberOfQuestions = DB_FUNC.getTotalNumberOfQuestions(subject);
        //pull n questions from the database
        allQuestions = DB_FUNC.getQuestionsRandom(numberOfQuestions, subject);

        // show the first question
        showQuestion(i);
        i++;

        timer = (ProgressBar) findViewById(R.id.countdown_timer);
        // set colour of bar to green (#00cc00)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // for lollipop and above versions. as this method only works for lollipop or above
            timer.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#00cc00")));
        } else {
            // do something for devices running an SDK lower than lollipop
            timer.getProgressDrawable().setColorFilter(Color.parseColor("#00cc00"), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        // timer of 10 seconds counting in intervals of 1 second
            // 10000 ms = 10s
            // 1000 ms = 1s
        countDownTimer = new MyCountDownTimer(10000, 500);
        countDownTimer.start();
    }

    @Override
    public void onBackPressed() {
        // pause timer
        countDownTimer.cancel();
        // block question when dialog is open
        // 0xff444444 is the colour of the questions TextView (#444444)
        getWindow().setBackgroundDrawable(new ColorDrawable(0xff444444));

        showDialog();
    }

    public void getResponse(View view) {
        Intent returnIntent = new Intent();

        String chosenAnswer = ((Button) view).getText().toString();

        TextView t = (TextView) findViewById(R.id.question_text_view);
        String ques = t.getText().toString();

        boolean isAnswer = DB_FUNC.isAnswer(ques, chosenAnswer);

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
            if(allQuestions.size() != i) { // if there are questions left
                // cancel timer
                countDownTimer.cancel();
                // reset timer
                countDownTimer = new MyCountDownTimer(10000, 500);
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

    private void showQuestion(int index) {
        // change the views of this activity to display question and options
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

    private void showDialog() {
        // create instance of the dialog box
        DialogFragment newFragment = LeavingDialogFragment.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
    // method for pressing "Yes" on dialog box
        Intent quitIntent = new Intent();
        quitIntent.putExtra("gameOverKey", "GAME OVER!");
        quitIntent.putExtra("quitKey", "You quit the game");
        setResult(Activity.RESULT_OK+3, quitIntent);

        //countDownTimer.cancel();
        // cancel is redundant here.
        // timer is already cancelled when back button is pressed
        finish();
    }

    public void doNegativeClick() {
    // method for pressing "No" on dialog box
        // 0xfff3f3f3 is the colour of the default background (#f3f3f3)
        getWindow().setBackgroundDrawable(new ColorDrawable(0xfff3f3f3));

        //countDownTimer.cancel();
        // cancel is redundant here.
        // timer is already cancelled when back button is pressed
        countDownTimer = new MyCountDownTimer(countDownTimer.millisLeft, 500);
        countDownTimer.start();
    }

    public class MyCountDownTimer extends CountDownTimer {
        long millisLeft;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished/1000);
            millisLeft = millisUntilFinished;
            timer.setProgress(timer.getMax() - progress);
            // if there's 3 seconds (or less) left
            // set colour of bar to red
            if(progress < 3) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    // for lollipop and above versions. as this method only works for lollipop or above
                    timer.setProgressTintList(ColorStateList.valueOf(Color.RED));
                } else {
                    // do something for devices running an SDK lower than lollipop
                    timer.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                }
            } else {
                // else, set colour of bar to green
                // (reset the color when the next answer appears)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    // for lollipop and above versions. as this method only works for lollipop or above
                    timer.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#00cc00")));
                } else {
                    // do something for devices running an SDK lower than lollipop
                    timer.getProgressDrawable().setColorFilter(Color.parseColor("#00cc00"), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        }

        @Override
        public void onFinish() {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("gameOverKey", "GAME OVER!");
            returnIntent.putExtra("outOfTimeKey", "You ran out of time");
            setResult(Activity.RESULT_OK+2, returnIntent);

            finish();
        }
    }

}