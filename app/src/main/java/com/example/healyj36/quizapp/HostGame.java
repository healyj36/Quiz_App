package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    ServerSocket serverSocket = null;
    Socket socket;
    int numberOfQuestions;

    private String questionText, option1, option2, option3, option4;

    private ArrayList<HashMap<String, String>> allQuestions = new ArrayList<>();

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    String clientChoice;
    boolean isClientCorrect;
    String correctAnswer;
    String questionAndOptions;
    boolean isHostCorrect;
    String hostChoice;
    boolean isFirstQuestion = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);

        infoIp = (TextView) findViewById(R.id.infoIp);
        msg = (TextView) findViewById(R.id.msg);
        serverMsg = (EditText) findViewById(R.id.title);
        serverMsg.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        String numberOfQuestionsString = "0";
        String subject = "0";
        if (extras != null) {
            numberOfQuestionsString = extras.getString("numberOfQuestionsKey");
            subject = extras.getString("subjectKey");
            if (numberOfQuestionsString.equals("All Questions")) {
                numberOfQuestionsString = String.valueOf(DB_FUNC.getTotalNumberOfQuestions("questions", subject));
            }
        }
        numberOfQuestions = Integer.parseInt(numberOfQuestionsString);

        allQuestions = DB_FUNC.getQuestionsRandom(numberOfQuestions, subject);

        infoIp.setText(getIpAddress());
/*
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        */
        MyHostTask myHostTask = new MyHostTask();
        myHostTask.execute();
        setContentView(R.layout.question_entry);
        TextView question_text_view = (TextView) findViewById(R.id.question_text_view);
        question_text_view.setText(getIpAddress());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // in case finally block isn't reached (doesn't run)
        // close sockets here
        if (serverSocket != null) {
            try {
                Log.d(HostGame.class.getSimpleName(), "ON_DESPRTOT_CLOSING SERVERSOCKET");
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                Log.d(HostGame.class.getSimpleName(), "ON_DESPRTOT_CLOSING SOCKET");
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
        HashMap<String, String> question = allQuestions.get(index);

        questionText = question.get("question");
        option1 = question.get("option1");
        option2 = question.get("option2");
        option3 = question.get("option3");
        option4 = question.get("option4");

        return "[" + questionText +
                ", " + option1 +
                ", " + option2 +
                ", " + option3 +
                ", " + option4 + "]";
    }

    private String getQuestionAndAnswers(int index, boolean previousAnswer) {
        HashMap<String, String> question = allQuestions.get(index);

        questionText = question.get("question");
        option1 = question.get("option1");
        option2 = question.get("option2");
        option3 = question.get("option3");
        option4 = question.get("option4");

        return "[" + questionText +
                ", " + option1 +
                ", " + option2 +
                ", " + option3 +
                ", " + option4 +
                ", " + isClientCorrect + "]";
    }

    private String getQuestion(int index) {
        HashMap<String, String> question = allQuestions.get(index);

        return question.get("question");
    }

    public void getAnswer(View view) {
        hostChoice = ((Button) view).getText().toString();
    }

    public class MyHostTask extends AsyncTask<Void, String, Void> {

        static final int SocketServerPORT = 8080;
        int count = 0;
        String finalScores = "";

        @Override
        protected Void doInBackground(Void... params) {

            socket = null;
            dataInputStream = null;
            dataOutputStream = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                    socket = serverSocket.accept();

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String msgReply = count + ": DEFAULT MESSAGE";

                    int i = 0;
                    questionAndOptions = getQuestionAndAnswers(i);
                    correctAnswer = DB_FUNC.getAnswer(getQuestion(i));

                    dataOutputStream.writeInt(numberOfQuestions);
                    dataOutputStream.flush();

                    i++;
                    hostChoice = null;
                    clientChoice = null;
                    isHostCorrect = false;
                    isClientCorrect = false;
                    int hostScore = 0;
                    int clientScore = 0;
                    while (i < numberOfQuestions) {
                        Thread clThread = new Thread(new clientThread());
                        clThread.start();
                        /*

                        try {
                            dataOutputStream.writeUTF(questionAndOptions);
                            dataOutputStream.writeBoolean(isClientCorrect);
                            dataOutputStream.flush();

                            clientChoice = dataInputStream.readUTF();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            isClientCorrect = checkAnswer(clientChoice, correctAnswer);
                        }
                         */

                        //ask host q and get response
                        publishProgress(questionAndOptions);

                        // get choice from button press
                        // TODO think of a better method than busy waiting
                        while (hostChoice == null) {
                        }

                        isHostCorrect = checkAnswer(hostChoice, correctAnswer);
                        Log.d(HostGame.class.getSimpleName(), "lets check is host right");
                        Log.d(HostGame.class.getSimpleName(), "quesiton is " + questionAndOptions);
                        Log.d(HostGame.class.getSimpleName(), "answer is " + correctAnswer);
                        Log.d(HostGame.class.getSimpleName(), "host chooses " + hostChoice);
                        Log.d(HostGame.class.getSimpleName(), "host is " + isHostCorrect);

                        if (isHostCorrect) {
                            hostScore++;
                        }

                        hostChoice = null;

                        clThread.join();
                        if (isClientCorrect) {
                            clientScore++;
                        }
                        isFirstQuestion = false;
                        questionAndOptions = getQuestionAndAnswers(i);
                        Log.d(HostGame.class.getSimpleName(), questionAndOptions);
                        correctAnswer = DB_FUNC.getAnswer(getQuestion(i));
                        Log.d(HostGame.class.getSimpleName(), correctAnswer);

                        i++;
                    }

                    Thread clThread = new Thread(new clientThread());
                    clThread.start();

                    publishProgress(questionAndOptions);

                    while (hostChoice == null) {
                    }

                    isHostCorrect = checkAnswer(hostChoice, correctAnswer);
                    if (isHostCorrect) {
                        hostScore++;
                    }
                    if (isClientCorrect) {
                        clientScore++;
                    }

                    finalScores = "[hostScore: " + hostScore + ", clientScore: " + clientScore +"]";
                    dataOutputStream.writeUTF(finalScores);
    //                dataOutputStream.flush();
            } catch (IOException e) {
                Log.d(HostGame.class.getSimpleName(), "IOEXCEPTIONLOL");
                e.printStackTrace();
                final String errMsg = e.toString();
                HostGame.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText(errMsg);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
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
                        Log.d(HostGame.class.getSimpleName(), "CLOSING DATAINSTREMA");
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        Log.d(HostGame.class.getSimpleName(), "CLOSING DATAOUTSTREAM");
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d(HostGame.class.getSimpleName(), "RETURNING NU.LLLL");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            TextView question_text_view = (TextView) findViewById(R.id.question_text_view);
            question_text_view.setText(questionText);
            TextView option1TextView = (TextView) findViewById(R.id.option1_button_text_view);
            option1TextView.setText(option1);
            TextView option2TextView = (TextView) findViewById(R.id.option2_button_text_view);
            option2TextView.setText(option2);
            TextView option3TextView = (TextView) findViewById(R.id.option3_button_text_view);
            option3TextView.setText(option3);
            TextView option4TextView = (TextView) findViewById(R.id.option4_button_text_view);
            option4TextView.setText(option4);

            if(!isFirstQuestion) {
                TextView chosen_answer_text_view = (TextView) findViewById(R.id.chosen_answer_text_view);
                String str = "Your previous answer was " + isHostCorrect;
                chosen_answer_text_view.setText(str);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("scoreKey", finalScores);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();


           /* super.onPostExecute(result);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("scoreKey", scores);
            returnIntent.putExtra("numQuestionsKey", numQuestions);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();*/
        }
    }



    public boolean checkAnswer(String a, String b) {
        return a.equals(b);
    }

    private String packageForClient(String str) {
        String bool = String.valueOf(isClientCorrect);
        str = str.substring(0, str.length()-1);
        str = str + ", " + bool + "]";
        return str;
    }

    private class clientThread implements Runnable {
        public void run() {
            try {
                dataOutputStream.writeUTF(packageForClient(questionAndOptions));
                dataOutputStream.flush();

                clientChoice = dataInputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isClientCorrect = checkAnswer(clientChoice, correctAnswer);
        }
    }

    private void changeView() {

        TextView question_text_view = (TextView) findViewById(R.id.question_text_view);
        question_text_view.setText(questionText);
        TextView option1TextView = (TextView) findViewById(R.id.option1_button_text_view);
        option1TextView.setText(option1);
        TextView option2TextView = (TextView) findViewById(R.id.option2_button_text_view);
        option2TextView.setText(option2);
        TextView option3TextView = (TextView) findViewById(R.id.option3_button_text_view);
        option3TextView.setText(option3);
        TextView option4TextView = (TextView) findViewById(R.id.option4_button_text_view);
        option4TextView.setText(option4);

        //if(isHostCorrect != null) {
            TextView chosen_answer_text_view = (TextView) findViewById(R.id.chosen_answer_text_view);
            String str = "Your previous answer was " + isHostCorrect;
            chosen_answer_text_view.setText(str);
        //}

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