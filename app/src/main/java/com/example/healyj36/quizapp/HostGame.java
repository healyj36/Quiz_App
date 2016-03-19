package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private final DBFunc DB_FUNC = new DBFunc(this);

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

        Bundle extras = getIntent().getExtras();
        // receive subject and number of questions from the previous activity
        String numberOfQuestionsString = "0";
        String subject = "0";
        if (extras != null) {
            numberOfQuestionsString = extras.getString("numberOfQuestionsKey");
            subject = extras.getString("subjectKey");
            // if user chose all questions...
            if (numberOfQuestionsString.equals("All Questions")) {
                // ...get the total number of questions in the database
                numberOfQuestionsString = String.valueOf(DB_FUNC.getTotalNumberOfQuestions(subject));
            }
        }
        numberOfQuestions = Integer.parseInt(numberOfQuestionsString);

        //pull n questions from the database
        allQuestions = DB_FUNC.getQuestionsRandom(numberOfQuestions, subject);

        //run the AsyncTask
        MyHostTask myHostTask = new MyHostTask();
        myHostTask.execute();

        //Set up progress bars
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

        //display host IP on the screen to allow host to tell client where to connect
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
    }

    private String getQuestionAndAnswers(int index) {
        HashMap<String, String> question = allQuestions.get(index);

        questionText = question.get("question");
        option1 = question.get("option1");
        option2 = question.get("option2");
        option3 = question.get("option3");
        option4 = question.get("option4");

        // return the question and its options in the form of a string package
        return "[" + questionText +
                ", " + option1 +
                ", " + option2 +
                ", " + option3 +
                ", " + option4 + "]";
    }

    private String getQuestion(int index) {
        HashMap<String, String> question = allQuestions.get(index);

        return question.get("question");
    }

    public void getResponse(View view) {
        hostChoice = ((Button) view).getText().toString();
    }

    public class MyHostTask extends AsyncTask<Void, String, Void> {

        static final int SocketServerPORT = 8080;
        String finalScores = "";

        @Override
        protected Void doInBackground(Void... params) {
            socket = null;
            dataInputStream = null;
            dataOutputStream = null;
            try {
                //Open socket listening on 8080, and accept a connection
                serverSocket = new ServerSocket(SocketServerPORT);
                socket = serverSocket.accept();

                //Initialise Data Input/Output streams
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());


                int i = 0;
                //get question
                questionAndOptions = getQuestionAndAnswers(i);
                //Get answer to question
                correctAnswer = DB_FUNC.getAnswer(getQuestion(i));

                dataOutputStream.writeInt(numberOfQuestions);
                dataOutputStream.flush();

                i++;
                hostChoice = null;
                clientChoice = null;
                isHostCorrect = false;
                isClientCorrect = false;
                while (i < numberOfQuestions) {
                    //Push question out to client, get response from them
                    Thread clThread = new Thread(new clientThread());
                    clThread.start();

                    //In the meantime, ask host question
                    publishProgress(questionAndOptions);

                    // get choice from button press
                    // would prefer a better method than busy waiting
                    while (hostChoice == null) {
                    }
                    //Check if host correct
                    isHostCorrect = checkAnswer(hostChoice, correctAnswer);

                    if (isHostCorrect) {
                        hostScore++;
                    }
                    //reset host choice to null
                    hostChoice = null;

                    //at this point, join the client thread.
                    //the host waits here until the client thread has finished executing
                    clThread.join();
                    if (isClientCorrect) {
                        clientScore++;
                    }
                    isFirstQuestion = false;
                    //get question and answer
                    questionAndOptions = getQuestionAndAnswers(i);
                    correctAnswer = DB_FUNC.getAnswer(getQuestion(i));

                    i++;
                }
                //Perform same as you would in the loop
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

                //Except now, instead of fetching the next question (which would be impossible),
                //display the scores of both players
                finalScores = "[" + hostScore + ", " + clientScore +"]";
                dataOutputStream.writeUTF(finalScores);
                dataOutputStream.flush();
            } catch (IOException | InterruptedException e) {
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
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            ProgressBar local_player_progress = (ProgressBar) findViewById(R.id.local_player_progress);
            ProgressBar opponent_progress = (ProgressBar) findViewById(R.id.opponent_progress);
            if(isFirstQuestion) {
                //set the number of sections in the progress bars on the first question
                local_player_progress.setMax(numberOfQuestions);
                opponent_progress.setMax(numberOfQuestions);
            }
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
                //After the first question, display if the previous answer was correct or not
                TextView chosen_answer_text_view = (TextView) findViewById(R.id.chosen_answer_text_view);
                String str = "Your previous answer was " + isHostCorrect;
                chosen_answer_text_view.setText(str);

                //if one/both of the answers was correct, increment the progress bar
                if(isHostCorrect) {
                    local_player_progress.incrementProgressBy(1);
                }
                if(isClientCorrect) {
                    opponent_progress.incrementProgressBy(1);
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent returnIntent = new Intent();
            //Return back to OnlineStart, send out some data to display the scores
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

    //To let the client know their progress and the progress of the host,
    //add 2 ints to the end, corresponding to the scores of the players
    private String packageForClient(String str) {
        str = str.substring(0, str.length()-1);
        str = str + ", " + hostScore + ", " + clientScore +"]";
        //return [q, o1, o2, o3, o4, hostScore, clientScore]
        return str;
    }

    private class clientThread implements Runnable {
        public void run() {
            try {
                //output question data
                dataOutputStream.writeUTF(packageForClient(questionAndOptions));
                dataOutputStream.flush();
                //wait for response
                clientChoice = dataInputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //check if its the right answer
            isClientCorrect = checkAnswer(clientChoice, correctAnswer);
        }
    }

    private String getIpAddress(){
        // This method filters through all the IP Addresses associated to the device and it's environment
        //And finds the devices ACTUAL IP
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