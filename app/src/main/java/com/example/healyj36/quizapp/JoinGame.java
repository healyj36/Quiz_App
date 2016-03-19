package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Jordan on 11/03/2016.
 */
public class JoinGame extends Activity {
    private Socket socket;
    private int numQuestions;

    private String clientChoice;

    private EditText editTextAddress;
    private int hostScore = 0;
    private int clientScore = 0;

    private boolean isFirstQuestion = true;
    String clientNickname;
    String hostNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_entry);

        ProgressBar local_player_progress = (ProgressBar) findViewById(R.id.local_player_progress);
        ProgressBar opponent_progress = (ProgressBar) findViewById(R.id.opponent_progress);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // for lollipop and above versions. as this method only works for lollipop or above
            local_player_progress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#008800")));
            opponent_progress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#AA8D00")));
        } else {
            // do something for devices running an SDK lower than lollipop
            local_player_progress.getProgressDrawable().setColorFilter(Color.parseColor("#008800"), android.graphics.PorterDuff.Mode.MULTIPLY);
            opponent_progress.getProgressDrawable().setColorFilter(Color.parseColor("#AA8D00"), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        Bundle extras = getIntent().getExtras();
        String hostIp = extras.getString("hostIpKey");
        clientNickname = extras.getString("nicknameClientKey");
        hostNickname = extras.getString("nicknameHostKey");


        MyClientTask myClientTask = new MyClientTask(hostIp);
        myClientTask.execute();
    }

    public class MyClientTask extends AsyncTask<Void, String, Void> {

        final String dstAddress;
        final int dstPort = 8080;
        String questionWithOptions = "";
        String scores = "";


        MyClientTask(String addr) {
            dstAddress = addr;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                dataOutputStream.writeUTF(clientNickname);
                dataOutputStream.flush();

                //The first thing sent by the host is the number of questions
                numQuestions = dataInputStream.readInt();

                for(int i = 0; i < numQuestions; i++) {
                    //read the question data
                    questionWithOptions = dataInputStream.readUTF();
                    //display it
                    publishProgress(questionWithOptions);
                    // get clientChoice from button press
                    // writeUTF(answer)
                    // flush

                    // Would prefer a better method than busy waiting
                    while (clientChoice == null) {
                    }
                    isFirstQuestion = false;
                    //writeout the answer
                    dataOutputStream.writeUTF(clientChoice);
                    dataOutputStream.flush();

                    clientChoice = null;
                }
                //after the loop, read the scores
                scores = dataInputStream.readUTF();//hostscore, clientscore

            } catch (UnknownHostException e) {
                e.printStackTrace();
                questionWithOptions = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                questionWithOptions = "IOException: " + e.toString();
            } finally {
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent returnScoresClient = new Intent(JoinGame.this, OnlineStart.class);

            returnScoresClient.putExtra("scoreKey", scores);
            returnScoresClient.putExtra("numQuestionsKey", numQuestions);
            returnScoresClient.putExtra("wasHostKey", false);
            returnScoresClient.putExtra("nicknameHostKey", hostNickname);
            returnScoresClient.putExtra("nicknameClientKey", clientNickname);

            returnScoresClient.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(returnScoresClient);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ProgressBar local_player_progress = (ProgressBar) findViewById(R.id.local_player_progress);
            ProgressBar opponent_progress = (ProgressBar) findViewById(R.id.opponent_progress);
            if(isFirstQuestion) {
                //set the number of sections in the progress bars on the first question
                local_player_progress.setMax(numQuestions);
                opponent_progress.setMax(numQuestions);
            }
            // lading dialog. waiting for question to be displayed
                //loadingDialog.show();

            String qAnda = values[0];
            qAnda = qAnda.substring(1, qAnda.length() - 1);
            String[] ary = qAnda.split(", ");
            String questionName = ary[0];
            String option1 = ary[1];
            String option2 = ary[2];
            String option3 = ary[3];
            String option4 = ary[4];

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

            // finished loading
                //loadingDialog.dismiss();

            //return [q, o1, o2, o3, o4, hostScore, clientScore]
            if(!isFirstQuestion) {
                TextView chosen_answer_text_view = (TextView) findViewById(R.id.chosen_answer_text_view);

                local_player_progress = (ProgressBar) findViewById(R.id.local_player_progress);
                opponent_progress = (ProgressBar) findViewById(R.id.opponent_progress);

                String str = "Your previous answer was ";

                //After the first question, display if the previous answer was correct or not
                //Then increment the progress bars if needed
                if(clientScore != Integer.parseInt(ary[6])) {//if client score has gone up
                    clientScore = Integer.parseInt(ary[6]);
                    str += "true";
                    local_player_progress.incrementProgressBy(1);
                }
                else {
                    str += "false";
                }
                chosen_answer_text_view.setText(str);

                if(hostScore != Integer.parseInt(ary[5])) {//if host score has gone up
                    hostScore = Integer.parseInt(ary[5]);
                    opponent_progress.incrementProgressBy(1);
                }
            }
        }
    }

    public void getResponse(View view) {
        clientChoice = null;
        clientChoice = ((Button) view).getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
