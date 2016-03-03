package com.example.healyj36.quizapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
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

        try {
            DB_FUNC.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        String numberOfQuestionsString = "0";
        String subject = "0";
        if (extras != null) {
            numberOfQuestionsString = extras.getString("numberOfQuestionsKey");
            subject = extras.getString("subjectKey");
            if(numberOfQuestionsString.equals("All Questions")) {
                numberOfQuestionsString = String.valueOf(DB_FUNC.getTotalNumberOfQuestions("questions", subject));
            }
        }

        numberOfQuestions = Integer.parseInt(numberOfQuestionsString);
        allQuestions = DB_FUNC.getQuestionsRandom(numberOfQuestions, subject);

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
        // pause timer
        countDownTimer.cancel();

        // block question when dialog is open
        // 0xff444444 is the colour of the questions textview (#444444)
        getWindow().setBackgroundDrawable(new ColorDrawable(0xff444444));

        showDialog();
    }

    public void getAnswer(View view) {
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
        DialogFragment newFragment = AttemptLeaveDialogFragment.newInstance(
                R.string.leaving_game_dialog_title);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
    // method for pressing "Yes" on dialog box
        // TODO when user leaves the previous message is still there. Maybe reset textview?
        // if user plays game, finishes with a score of 3/5
        // then the user plays again and quits using the back button
        // the previous message ("Your score is 3/5") is still there

        //countDownTimer.cancel();
        // cancel is redundant here.
        // timer is already cancelled when back button is pressed
        finish();
    }

    public void doNegativeClick() {
    // method for pressing "No" on dialog box
        // TODO this is hardcoded. need method to find value
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
        }

        @Override
        public void onFinish() {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("outOfTimeKey", "GAME OVER!\nYou ran out of time");
            // TODO re-structure how this message looks on infinite_start.xml
            setResult(Activity.RESULT_OK+2, returnIntent);

            finish();
        }
    }

}