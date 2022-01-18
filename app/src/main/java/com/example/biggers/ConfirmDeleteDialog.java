package com.example.biggers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteDialog extends DialogFragment {

    public static final String TAG = "ConfirmDeleteDialog";
    private int position;

    /*
     * Crea il dialogo di richiesta di conferma
     *  di eliminazione e i relativi pulsanti
     */
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

    /*
     * Il metodo onStop viene chiamato quando
     * il dialogo viene chiuso il qualunque modo
     * Chiama prima il metodo che controlla se
     * l'eliminazione è confermata poi il metodo
     * che resetta il boolean
     */
    @Override
    public void onStop() {
        super.onStop();
        ((AdminManagerActivity) getActivity()).controlDelete(position);
        ((AdminManagerActivity) getActivity()).setBooleanToFalse();
    }
}
