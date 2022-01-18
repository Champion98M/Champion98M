package com.example.biggers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteDialog extends DialogFragment {

    public static final String TAG = "ConfirmDeleteDialog";
    private int position;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle mPosition = getArguments();
        position = mPosition.getInt(AdminManagerActivity.BUNDLE_TAG);

        return new AlertDialog.Builder(requireContext())
                .setMessage("Sei sicuro di voler eliminare questo account?")
                .setPositiveButton("Elimina", (dialog, wich) -> {
                    ((AdminManagerActivity)getActivity()).doPositiveClick();
                })
                .setNegativeButton("Annulla", (dialog, which) -> {
                    ((AdminManagerActivity)getActivity()).doNegativeClick(position);
                })
                .create();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AdminManagerActivity) getActivity()).controlDelete(position);
    }
}
