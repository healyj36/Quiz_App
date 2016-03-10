package com.example.healyj36.quizapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Jordan on 26/02/2016.
 */
public class LeavingDialogFragment extends DialogFragment {

    public static LeavingDialogFragment newInstance(int title) {
        LeavingDialogFragment frag = new LeavingDialogFragment();
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
                .setMessage(R.string.leaving_game_dialog_message)
                .setPositiveButton(R.string.leaving_game_dialog_positive_action,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((InfiniteGame) getActivity()).doPositiveClick();
                    }
                }
        )
                .setNegativeButton(R.string.leaving_game_dialog_negative_action,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((InfiniteGame) getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        ((InfiniteGame)getActivity()).doNegativeClick();
        super.onCancel(dialog);
    }
}
