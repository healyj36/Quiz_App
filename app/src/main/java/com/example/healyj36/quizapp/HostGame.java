package com.example.healyj36.quizapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by Jordan on 11/03/2016.
 */
public class HostGame extends Activity {
    private DBFunc DB_FUNC = new DBFunc(this);

    EditText serverMsg;

    TextView infoIp, msg;
    ServerSocket serverSocket;
    Socket socket;
    int numberOfQuestions;

    private ArrayList<HashMap<String, String>> allQuestions = new ArrayList<>();

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);

        infoIp = (TextView) findViewById(R.id.infoIp);
        msg = (TextView) findViewById(R.id.msg);
        serverMsg = (EditText)findViewById(R.id.title);
        serverMsg.setVisibility(View.GONE);

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

        infoIp.setText(getIpAddress());

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // in case finally block isn't reached (doesn't run)
        // close sockets here
        if (serverSocket != null) {
            try {
                serverSocket.close();
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
        if (dataInputStream != null) {
            try {
                dataInputStream.close();
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
    }

    private String getQuestionAndAnswers(int index) {
        HashMap<String,String> question = allQuestions.get(index);

        String q = question.get("question");
        String o1 = question.get("option1");
        String o2 = question.get("option2");
        String o3 = question.get("option3");
        String o4 = question.get("option4");

        return "[" + q +
                ", " + o1 +
                ", " + o2 +
                ", " + o3 +
                ", " + o4 + "]";
    }

    private String getQuestionAndAnswers(int index, boolean previousAnswer) {
        HashMap<String,String> question = allQuestions.get(index);

        String q = question.get("question");
        String o1 = question.get("option1");
        String o2 = question.get("option2");
        String o3 = question.get("option3");
        String o4 = question.get("option4");

        return "[" + q +
                ", " + o1 +
                ", " + o2 +
                ", " + o3 +
                ", " + o4 +
                ", " + previousAnswer + "]";
    }

    private String getQuestion(int index) {
        HashMap<String, String> question = allQuestions.get(index);

        return question.get("question");
    }



    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            socket = null;
            dataInputStream = null;
            dataOutputStream = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    String msgReply = count + ": DEFAULT MESSAGE";

                    /*
                    for(question in allQuestions) {
                        cast it to a string like [q, o1, o2, o3, o4]
                        writeUTF it
                        flush it
                        wait for a response
                        writeUTF "correct" or "wrong"
                        flush
                    endfor
                    */
                    boolean isAnswer;

                    dataOutputStream.writeInt(numberOfQuestions);
                    dataOutputStream.flush();

                    int i = 0;
                    String q = getQuestionAndAnswers(i);
                    i++;
                    String choice;
                    int score =0;
                    while (i < numberOfQuestions) {
                        dataOutputStream.writeUTF(q);
                        dataOutputStream.flush();

                        choice = dataInputStream.readUTF();
                        isAnswer = DB_FUNC.isAnswer(getQuestion(i-1), choice);
                        if(isAnswer){
                            score++;
                        }
                        q = getQuestionAndAnswers(i, isAnswer);
                        i++;
                    }

                    dataOutputStream.writeUTF(q);
                    dataOutputStream.flush();
                    choice = dataInputStream.readUTF();
                    isAnswer = DB_FUNC.isAnswer(getQuestion(i-1), choice);
                    if(isAnswer) {
                        score++;
                    }
                    dataOutputStream.writeInt(score);
                    dataOutputStream.flush();


                }
            } catch (IOException e) {
                e.printStackTrace();
                final String errMsg = e.toString();
                HostGame.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText(errMsg);
                    }
                });

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
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

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private String getIpAddress(){
        // TODO for finding address at home
        /*
        String ip = "My IP address is: ";
        String addr = "UNAVAILABLE";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        addr = inetAddress.getHostAddress();
                    }

                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip + addr;
        */

        // TODO for finding address in DCU
        String ip = "My IP address is: ";
        String addr = "UNAVAILABLE";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while(enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (!(inetAddress.isLoopbackAddress() || inetAddress.isSiteLocalAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isMulticastAddress())) {
                        if (inetAddress.getHostAddress().matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")) {
                            addr = inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip + addr;
    }
}
