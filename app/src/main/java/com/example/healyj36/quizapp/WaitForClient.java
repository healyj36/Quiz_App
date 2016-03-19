package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Jordan on 19/03/2016.
 */
public class WaitForClient extends Activity {
    final String SERVER_ADDR = "136.206.254.6";
    String subjectChosen;
    String numQuestionChosen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_clients);

        Bundle extras = getIntent().getExtras();

        subjectChosen = extras.getString("subjectKey");
        numQuestionChosen = extras.getString("numberOfQuestionsKey");
        String hostNickname = extras.getString("nicknameHostKey");

        WaitForClientTask waitForClientTask = new WaitForClientTask(SERVER_ADDR, hostNickname);
        waitForClientTask.execute();
    }

    public class WaitForClientTask extends AsyncTask<Void, String, Void> {
        String dstAddr;
        String hostNickname;
        // port for client is 1234
        // port for hosts is 2345
        final int dstPort = 2345;
        WaitForClientTask(String addr, String hNname) {
            dstAddr = addr;
            hostNickname = hNname;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket socket = new Socket(dstAddr, dstPort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(hostNickname);
                dataOutputStream.flush();
                // start game. go to host game
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent hostGame = new Intent(WaitForClient.this, HostGame.class);

            hostGame.putExtra("subjectKey", subjectChosen);
            hostGame.putExtra("numberOfQuestionsKey", numQuestionChosen);
            hostGame.putExtra("nicknameHostKey", hostNickname);

            hostGame.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            // call activity to run and retrieve data back
            startActivity(hostGame);
        }
    }
}
