package com.example.healyj36.quizapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Jordan on 09/03/2016.
 */
public class ClientActivity extends Activity {

    Socket socket;

    TextView textResponse;
    EditText editTextAddress;
    Button buttonSend;
    Button connectButton;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String messages = "";

    EditText clientMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_activity);

        editTextAddress = (EditText) findViewById(R.id.address);
        buttonSend = (Button) findViewById(R.id.send);
        textResponse = (TextView) findViewById(R.id.response);
        connectButton = (Button) findViewById(R.id.connect);

        clientMsg = (EditText) findViewById(R.id.client_msg);
        buttonSend.setVisibility(View.GONE);
        clientMsg.setVisibility(View.GONE);
    }

    public void sendMessage(View view) {
        String tMsg = clientMsg.getText().toString();
        if(tMsg.equals("")){
            tMsg = null;
            Toast.makeText(ClientActivity.this, "No message sent", Toast.LENGTH_SHORT).show();
        }

        try {
            if(tMsg != null) {
                dataOutputStream.writeUTF(tMsg);
                dataOutputStream.flush();
                messages += "You: " + tMsg + "\n";
                textResponse.setText(messages);
            }
            clientMsg.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectToServer(View view) {
        // must be pressed first
        // disable other buttons
        MyClientTask myClientTask = new MyClientTask(editTextAddress
                .getText().toString(), 8080,
                null);
        myClientTask.execute();
        //re-enable buttons after connection
        buttonSend.setVisibility(View.VISIBLE);
        clientMsg.setVisibility(View.VISIBLE);
        editTextAddress.setVisibility(View.GONE);
        connectButton.setVisibility(View.GONE);
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String msgToServer;

        MyClientTask(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            socket = null;
            dataOutputStream = null;
            dataInputStream = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                ClientActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClientActivity.this, "Connected to " + dstAddress, Toast.LENGTH_SHORT).show();
                    }
                });
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                String str;
                while(true) {
                    str = dataInputStream.readUTF();
                    messages += dstAddress + ": " + str + "\n";

                    ClientActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textResponse.setText(messages);
                        }
                    });
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                messages = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                finish();
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
            textResponse.setText(messages);
            super.onPostExecute(result);
        }

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