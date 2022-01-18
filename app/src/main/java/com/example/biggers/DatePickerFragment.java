package com.example.biggers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/*
 * Classe che implementa il Fragment che mostra il
 * DatePicker per inserire la data in fase di registrazione
 */
public class DatePickerFragment extends DialogFragment {

    public static String TAG = "DatePickerDialog";

    public DatePickerDialog datePickerDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Conferma", datePickerDialog);
        return datePickerDialog;
    }
}

