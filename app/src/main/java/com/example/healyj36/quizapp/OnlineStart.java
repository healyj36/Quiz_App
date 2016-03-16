package com.example.healyj36.quizapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;
/*
public class OnlineStart extends Activity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
*/

public class OnlineStart extends Activity {
    private final DBFunc DB_FUNC = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_game);
        initCustomTypeFace(R.id.online_game_mode_title);
        EditText myTextBox = (EditText) findViewById(R.id.laptop_ip_edit_text);
        myTextBox.setText(R.string.server_ip_auto_fill);
/*
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);


        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();
*/

        Spinner dropdown_subject = (Spinner) findViewById(R.id.spinner_subjects);
        ArrayList<String> subjects = DB_FUNC.getSubjects();
        // add "All Subjects" to top of list / spinner
        subjects.add(0, "All Subjects");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjects);
        dropdown_subject.setAdapter(adapter1);

        final Spinner dropdown_number = (Spinner) findViewById(R.id.number_of_questions);
        dropdown_subject.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String subjectName = parent.getItemAtPosition(position).toString();
                        int maxNumber = DB_FUNC.getNumberOfQuestionsBySubject(subjectName);
                        ArrayList<String> numbers = new ArrayList<>();
                        numbers.add("All Questions"); // first element = "All Questions"
                        for(int i=(maxNumber-1); i>0; i--){
                            String elem = String.valueOf(i); // convert number to string
                            numbers.add(elem); // add it to ArrayList
                        }

                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(OnlineStart.this, android.R.layout.simple_spinner_dropdown_item, numbers);
                        dropdown_number.setAdapter(adapter2);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        // do nothing
                    }
                });
    }

    private void initCustomTypeFace(int textView) {
        TextView txt = (TextView) findViewById(textView);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/agency-fb.ttf");
        txt.setTypeface(font);
    }

    public void hostChosen(View view) {
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner_subjects);
        Spinner spinner2 = (Spinner) findViewById(R.id.number_of_questions);
        Intent hostGame = new Intent(OnlineStart.this, HostGame.class);

        final int result = 1; // signal

        hostGame.putExtra("subjectKey", spinner1.getSelectedItem().toString());
        hostGame.putExtra("numberOfQuestionsKey", spinner2.getSelectedItem().toString());
        // call activity to run and don't expect data to be sent back
        //startActivity(hostGame);

        // call activity to run and retrieve data back
        startActivityForResult(hostGame, result);
    }

    public void joinChosen(View view) {
        Intent joinGame = new Intent(OnlineStart.this, JoinGame.class);

        final int result = 1; // signal

        // call activity to run and don't expect data to be sent back
        //startActivity(joinGame);

        // call activity to run and retrieve data back
        startActivityForResult(joinGame, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // return result from online game
        // if host game returns
        if (requestCode == 1) {
            // resultCode == Activity.RESULT_OK, game finished cleanly
            if (resultCode == Activity.RESULT_OK) {
                String scoreText = data.getStringExtra("scoreKey");
                scoreText = scoreText.substring(1, scoreText.length() - 1);
                String[] scores = scoreText.split(", ");

                boolean wasHost = data.getBooleanExtra("wasHostKey", false);

                int numQuestions = data.getIntExtra("numQuestionsKey", 0);
                // 0 here is the default value
                // if the number of questions cant be received from the intent
                // numQuestions will be 0

                int yourScore, theirScore;
                String str = "The game is finished.\n";
                str += "There was " + numQuestions + " questions.\n";
                if(wasHost) {
                    yourScore=Integer.parseInt(scores[0]);
                    theirScore=Integer.parseInt(scores[1]);
                } else {
                    yourScore=Integer.parseInt(scores[1]);
                    theirScore=Integer.parseInt(scores[0]);
                }

                str += "You correctly answered " + yourScore + " questions.\n";
                str += "Your opponent correctly answered " + theirScore + " questions.\n";
                TextView a = (TextView) findViewById(R.id.online_game_mode_score);
                a.setText(str);
            }
        }
    }
/*
    @Override
    public void onConnected(Bundle connectionHint) {
        // show sign-out button, hide the sign-in button
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        // (your code here: update UI, enable functionality that depends on sign in, etc)

        // Dialog fragment: would you like to be server or client
        showDialog();
    }

    private void showDialog() {
        DialogFragment newFragment = ServerClientDialogFragment.newInstance(
                R.string.server_client_dialog_title);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            String msg = getResources().getString(R.string.signin_other_error);
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, msg)) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void signInClicked(View view) {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    public void signOutClicked(View view) {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
        else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            mSignInClicked = false;
            Games.signOut(mGoogleApiClient);

            // show sign-in button, hide the sign-out button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }
    */
}
