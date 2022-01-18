package com.example.biggers;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputLayout;

/* Classe per implementare un TextWatcher personalizzato
 * che rimuove l'errore del campo in cui l'utente sta scrivendo
 */
public class CustomTextWatcher implements TextWatcher {
    private TextInputLayout _textInputLayout;

    public CustomTextWatcher(TextInputLayout textInputLayout) {
        _textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        _textInputLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

