package com.example.healyj36.quizapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Jordan on 09/03/2016.
 */
public class ServerClientDialogFragment extends DialogFragment {

    public static ServerClientDialogFragment newInstance(int title) {
        ServerClientDialogFragment frag = new ServerClientDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(R.string.server_client_dialog_message)
                .setPositiveButton(R.string.client_button_text_view,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what to do when Server is chosen
                                // open server activity
                                Intent serverAct = new Intent(getActivity(), ClientActivity.class);

                                // final int result = 1; // signal

                                // call activity to run and don't expect data to be sent back
                                startActivity(serverAct);

                                // call activity to run and retrieve data back
                                // startActivityForResult(viewAllQues, result);
                            }
                        }
                )
                .setNegativeButton(R.string.server_button_text_view,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what to do when client is chosen
                                // open client activity
                                Intent clientAct = new Intent(getActivity(), ServerActivity.class);

                                // final int result = 1; // signal

                                // call activity to run and don't expect data to be sent back
                                startActivity(clientAct);

                                // call activity to run and retrieve data back
                                // startActivityForResult(viewAllQues, result);
                            }
                        }
                )
                .create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // what to do when user presses outside of box
        super.onCancel(dialog);
    }
}
