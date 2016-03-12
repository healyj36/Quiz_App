package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    int score;
    Socket socket;
    int numQuestions;

    String choice;

    TextView textResponse;
    EditText editTextAddress;
    Button buttonSend;

    EditText clientMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_activity);

        editTextAddress = (EditText) findViewById(R.id.address);
        buttonSend = (Button) findViewById(R.id.send);
        textResponse = (TextView) findViewById(R.id.response);
        // TODO remove message edit view
        clientMsg = (EditText)findViewById(R.id.client_msg);
        clientMsg.setVisibility(View.GONE);
    }

    public void sendMessage(View view) {
        String tMsg = "";
        setContentView(R.layout.question_entry);
        MyClientTask myClientTask = new MyClientTask(editTextAddress.getText().toString(), 8080, tMsg);
        myClientTask.execute();

    }

    public class MyClientTask extends AsyncTask<Void, String, Void> {

        String dstAddress;
        int dstPort;
        String questionWithOptions = "";
        String msgToServer;

        MyClientTask(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
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

                numQuestions = dataInputStream.readInt();
                for(int i = 0; i < numQuestions; i++) {
                    questionWithOptions = dataInputStream.readUTF();
                    publishProgress(questionWithOptions);

                    // get choice from button press
                    // writeUTF(answer)
                    // flush
                    // TODO think of a better method than busy waiting
                    while (choice == null) {
                    }
                    dataOutputStream.writeUTF(choice);
                    dataOutputStream.flush();

                    choice = null;
                }
                score = dataInputStream.readInt();


            } catch (UnknownHostException e) {
                e.printStackTrace();
                questionWithOptions = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                questionWithOptions = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("scoreKey", "Your score was " + score);
            returnIntent.putExtra("numQuestionsKey", numQuestions);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

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

            if(ary.length == 6) {//you need to say if the last response was correct or no
                TextView chosen_answer_text_view = (TextView) findViewById(R.id.chosen_answer_text_view);
                String str = "Your previous answer was " + ary[5];
                chosen_answer_text_view.setText(str);
            }
        }
    }

    private void changeView(String qAnda) {
        qAnda = qAnda.substring(1, qAnda.length()-1);
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
    }

    public void getAnswer(View view) {
        choice = null;
        choice = ((Button) view).getText().toString();
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
