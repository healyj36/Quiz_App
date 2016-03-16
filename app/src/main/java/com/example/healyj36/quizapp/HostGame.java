package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    // TODO add "are you sure you want to quit?"
    private final DBFunc DB_FUNC = new DBFunc(this);

    private TextView msg;
    private ServerSocket serverSocket = null;
    private Socket socket;
    private int numberOfQuestions;

    private String questionText, option1, option2, option3, option4;

    private ArrayList<HashMap<String, String>> allQuestions = new ArrayList<>();

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private String clientChoice;
    private boolean isClientCorrect;
    private String correctAnswer;
    private String questionAndOptions;
    private boolean isHostCorrect;
    private String hostChoice;
    private boolean isFirstQuestion = true;
    private int hostScore = 0;
    private int clientScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_entry);
        // hide views
            // hide local player progress bar
            // hide opponent progress bar
            // hide timer progress bar
            // hide button1 progress bar
            // hide button2 progress bar
            // hide button3 progress bar
            // hide button4 progress bar
        // set isConnected == false

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

        MyHostTask myHostTask = new MyHostTask();
        myHostTask.execute();

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
    }                                                                       public class HostGame extends Activity {

                                            // TODO add "are you sure you want to quit?"
    private String getQnt to quit?"
    private     private final DBFunc DB_FUNC = new DBFunc(this);
        HashMap<Strate final DBFunc DB_FUNC = new DBFunc(this);

        HashMap<String, String> question = allQuestions.    private TextView msg;
        questionText = que
         private ServerSocket serverSocket = null;
        option1 = question   private TextView msg;                              private Socket socket;
        option2 = questiontion.get("question");                                 private int numberOfQuestions;
        option3 = question

      option1 = question.get("option1");                                        private String questionText, option1, option2, option3, option4;

        return "[" + queststion.get("option2");                                 private ArrayList<HashMap<String, String>> allQuestions = new ArrayL
        option3 = question.
et("option3");                                                                  private DataInputStream dataInputStream;
                ", " + opttion.get("option4");                                  private DataOutputStream dataOutputStream;
                ", " + op

                                                                                private String clientChoice;

        return "[" + questionText +     private boolean isClientCorrect;
    private String getQuesashMap<String, String>> allQuestions = new ArrayLi    private String correctAnswer;
        HashMap<String, St                                                      private String questionAndOptions;
                           " + option2 +                                        private boolean isHostCorrect;
        return question.ge
                ", " + opt    private String hostChoice;
    }                     vate DataOutputStream dataOutputStream;               private boolean isFirstQuestion = true;
                                                                                private int hostScore = 0;
    public void getRespons}                                                     private int clientScore = 0;
        hostChoice = ((But
    }                                                                           @Override
                          ct;
    pr    protected void onCreate(Bundle savedInstanceState) {
    public clas  private String correctAnswer;                                      super.onCreate(savedInstanceState);
                          get(index);             private String questionAnd        setContentView(R.layout.question_entry);
        static final in                                                             // hide views
        String finalScores
        return question            // hide local player progress bar
                          hostChoice;                                                   // hide opponent progress bar
        @Override                             private boolean isFirstQuestio            // hide timer progress bar
        protected Void doI                                                              // hide button1 progress bar
            socket = null;
    public void getResponse(View vie            // hide button2 progress bar
            dataInputStrea = 0;
            // hide button3 progress bar
            dataOutputStre                                                              // hide button4 progress bar
            try {                                                          @        // set isConnected == false
                // set isC

                                                                         pro        Bundle extras = getIntent().getExtras();
                // set   public class MyHostTask extends AsyncTask<Void, Str        String numberOfQuestionsString = "0";
                serverSock
                                                                                    setContentView(R.layout.question_entry);
        static final int SocketServerPORT = 8080;                                   // hide views
        String finalScores = "";                                                        // hide local player progress bar
                                                                                        // hide opponent progress bar
        @Override                                                                       // hide timer progress bar
        protected Void doInBackground(Void... params) {                                 // hide button1 progress bar
            socket = null;                                                              // hide button2 progress bar
            dataInputStream = null;                                                     // hide button3 progress bar
            dataOutputStream = null;                                                    // hide button4 progress bar
            try {                                                                   // set isConnected == false
                // set isConnected == true
                // call on progress update                                          Bundle extras = getIntent().getExtras();
                // set isConnected == false                                         String numberOfQuestionsString = "0";
                serverSocket = new ServerSocket(SocketServerPORT);
                socket = serverSocket.accept();

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                int i = 0;
                questionAndOptions = getQuestionAndAnswers(i);
                correctAnswer = DB_FUNC.getAnswer(getQuestion(i));

                dataOutputStream.writeInt(numberOfQuestions);
                dataOutputStream.flush();

                i++;                                                                                            public class HostGame extends Activity {
                hostChoice = null;                                                                                  // TODO add "are you sure you want to quit?"
                clientChoice = null;                                                                                private final DBFunc DB_FUNC = new DBFunc(this);
                isHostCorrect = false;
                isClientCorrect = false;                                                                            private TextView msg;
                while (i < numberOfQuestions) {                                                                     private ServerSocket serverSocket = null;
                    Thread clThread = new Thread(new clientThread());                                               private Socket socket;
                    clThread.start();                                                                               private int numberOfQuestions;
                    /*
                                                                                                                    private String questionText, option1, option2, option3, option4;
                    try {
                        dataOutputStream.writeUTF(questionAndOptions);                                              private ArrayList<HashMap<String, String>> allQuestions = new ArrayList<
                        dataOutputStream.writeBoolean(isClientCorrect);
                        dataOutputStream.flush();                                                                   private DataInputStream dataInputStream;
                                                                                                                    private DataOutputStream dataOutputStream;
                        clientChoice = dataInputStream.readUTF();
                        } catch (IOException e) {                                                                   private String clientChoice;
                            e.printStackTrace();                                                                    private boolean isClientCorrect;
                        }                                                                                           private String correctAnswer;
                        isClientCorrect = checkAnswer(clientChoice, correctAnswer);                                 private String questionAndOptions;
                    }                                                                                               private boolean isHostCorrect;
                     */                                                                                             private String hostChoice;
                                                                                                                    private boolean isFirstQuestion = true;
                    //ask host q and get response                                                                   private int hostScore = 0;
                    publishProgress(questionAndOptions);                                                            private int clientScore = 0;

                    // get choice from button press                                                                 @Override
                    // TODO think of a better method than busy waiting                                              protected void onCreate(Bundle savedInstanceState) {
                    while (hostChoice == null) {                                                                        super.onCreate(savedInstanceState);
                    }                                                                                                   setContentView(R.layout.question_entry);
                                                                                                                        // hide views
                    isHostCorrect = checkAnswer(hostChoice, correctAnswer);                                                 // hide local player progress bar
                                                                                                                            // hide opponent progress bar
                    if (isHostCorrect) {                                                                                    // hide timer progress bar
                        hostScore++;                                                                                        // hide button1 progress bar
                    }                                                                                                       // hide button2 progress bar
                                                                                                                            // hide button3 progress bar
                    hostChoice = null;                                                                                      // hide button4 progress bar
                                                                                                                        // set isConnected == false
                    clThread.join();
                    if (isClientCorrect) {                                                                              Bundle extras = getIntent().getExtras();
                        clientScore++;                                                                                  String numberOfQuestionsString = "0";
                    }
                    isFirstQuestion = false;
                    questionAndOptions = getQuestionAndAnswers(i);
                    correctAnswer = DB_FUNC.getAnswer(getQuestion(i));

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
                clThread.join();
                if (isClientCorrect) {
                    clientScore++;
                }

                finalScores = "[" + hostScore + ", " + clientScore +"]";
                dataOutputStream.writeUTF(finalScores);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
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
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }                                                                                                                                       public class HostGame extends Activi
            return null;                                                                                                                                // TODO add "are you sure you wa
        }                                                                                                                                               private final DBFunc DB_FUNC = n

        @Override                                                                                                                                       private TextView msg;
        protected void onProgressUpdate(String... values) {                                                                                             private ServerSocket serverSocke
            super.onProgressUpdate(values);                                                                                                             private Socket socket;
            // if isConnected == false                                                                                                                  private int numberOfQuestions;
                // show local player progress bar
                // show opponent progress bar                                                                                                           private String questionText, opt
                // show timer progress bar
                // show button1 progress bar                                                                                                            private ArrayList<HashMap<String
                // show button2 progress bar
                // show button3 progress bar                                                                                                            private DataInputStream dataInpu
                // show button4 progress bar                                                                                                            private DataOutputStream dataOut

            ProgressBar local_player_progress = (ProgressBar) findViewById(R.id.local_player_progress);                                                 private String clientChoice;
            ProgressBar opponent_progress = (ProgressBar) findViewById(R.id.opponent_progress);                                                         private boolean isClientCorrect;
            if(isFirstQuestion) {                                                                                                                       private String correctAnswer;
                local_player_progress.setMax(numberOfQuestions);                                                                                        private String questionAndOption
                opponent_progress.setMax(numberOfQuestions);                                                                                            private boolean isHostCorrect;
            }                                                                                                                                           private String hostChoice;
            TextView question_text_view = (TextView) findViewById(R.id.question_text_view);                                                             private boolean isFirstQuestion
            question_text_view.setText(questionText);                                                                                                   private int hostScore = 0;
            TextView option1TextView = (TextView) findViewById(R.id.option1_button_text_view);                                                          private int clientScore = 0;
            option1TextView.setText(option1);
            TextView option2TextView = (TextView) findViewById(R.id.option2_button_text_view);                                                          @Override
            option2TextView.setText(option2);                                                                                                           protected void onCreate(Bundle s
            TextView option3TextView = (TextView) findViewById(R.id.option3_button_text_view);                                                              super.onCreate(savedInstance
            option3TextView.setText(option3);                                                                                                               setContentView(R.layout.ques
            TextView option4TextView = (TextView) findViewById(R.id.option4_button_text_view);                                                              // hide views
            option4TextView.setText(option4);                                                                                                                   // hide local player pro
                                                                                                                                                                // hide opponent progres
            if(!isFirstQuestion) {                                                                                                                              // hide timer progress b
                TextView chosen_answer_text_view = (TextView) findViewById(R.id.chosen_answer_text_view);                                                       // hide button1 progress
                String str = "Your previous answer was " + isHostCorrect;                                                                                       // hide button2 progress
                chosen_answer_text_view.setText(str);                                                                                                           // hide button3 progress
                                                                                                                                                                // hide button4 progress
                if(isHostCorrect) {                                                                                                                         // set isConnected == false
                    local_player_progress.incrementProgressBy(1);
                }                                                                                                                                           Bundle extras = getIntent().
                                                                                                                                                            String numberOfQuestionsStri
                if(isClientCorrect) {
                    opponent_progress.incrementProgressBy(1);
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("scoreKey", finalScores);
            returnIntent.putExtra("numQuestionsKey", numberOfQuestions);
            returnIntent.putExtra("wasHostKey", true);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }



    private boolean checkAnswer(String a, String b) {
        return a.equals(b);
    }

    private String packageForClient(String str) {
        str = str.substring(0, str.length()-1);
        str = str + ", " + hostScore + ", " + clientScore +"]";
        //return [q, o1, o2, o3, o4, hostScore, clientScore]
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