package com.example.biggers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Objects;

// Activity principale, permette il login
public class LoginActivity extends AppCompatActivity {

    //Dichirazione variabili dei widget da recuperare nel layout xml
    private Button loginButton;
    private TextView signUpText;
    private TextInputLayout usernameInput, passwordInput;
        public static String ACCOUNT_EXTRA = "com.example.biggers.Account";
    private Account account;
    private long backPressedTime;
    private Toast exitToast;

    //Dichiara un ArrayList di Account
    private ArrayList<Account> mAccountList = new ArrayList<>();

    //Nome del file di SharedPreferences
    private static final String SHARED = "shared";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
         * Controlla la versione di android del sistema: se minore di Android 10 (Q)
         * imposta il tema scuro di default. Questo perché la dark mode è stata
         * aggiunta in Android Q
         */
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
       
        //Apre il file SharedPreferences usato per il salvataggio dei dati
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED, MODE_PRIVATE);
        //Carica i file salvati e gli inserire in un ArrayList
        mAccountList = SaveDataClass.loadData(sharedPreferences);

        account = new Account();

        //Mappa i widget ai loro ids
        findWidgetIds();
        //Controlla i cambiamenti che avvengono negli EditText
        setTextListeners();

        /*
        * Quando il pulsante "login" viene premuto controlla
        * i dati inseriti e passa alla home activity
         */
        loginButton.setOnClickListener(view -> {
            //Cerca l'account corrispondente tra quelli salvati
            account = getAccount();
            //Se l'account esiste passa alla home activity
            if(account != null) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra(ACCOUNT_EXTRA, account);
                startActivity(intent);
                finish();
            }
        });

        /*
         * Quando la TextView con id "signUp" viene premuta
         * passa all'activity per la registrazione
         */
        signUpText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    // Override del metodo per modificare il comportamento del back button di android
    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            exitToast.cancel();
            super.onBackPressed();
            return;
        } else {
            exitToast = Toast.makeText(getBaseContext(), "Premi ancora per uscire", Toast.LENGTH_SHORT);
            exitToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }



    // Assegna alle variabili il widget tramite il loro ID
    private void findWidgetIds() {
        loginButton = (Button) findViewById(R.id.login);
        signUpText = (TextView) findViewById(R.id.signUp);
        usernameInput = (TextInputLayout) findViewById(R.id.username);
        passwordInput = (TextInputLayout) findViewById(R.id.password);
    }

    // Imposta dei TextListeners agli EditTexts
    private void setTextListeners() {
        //TextWatcher per attivare il pulsante di login quando username e password non sono vuoti
        Objects.requireNonNull(usernameInput.getEditText()).addTextChangedListener(loginTextWatcher);
        Objects.requireNonNull(passwordInput.getEditText()).addTextChangedListener(loginTextWatcher);
        //TextWatcher che disattiva gli errori quando si sta scrivendo
        Objects.requireNonNull(usernameInput.getEditText()).addTextChangedListener(new CustomTextWatcher(usernameInput));
        Objects.requireNonNull(passwordInput.getEditText()).addTextChangedListener(new CustomTextWatcher(passwordInput));
    }

    /*
     * Disattiva il pulsante di login "loginButton"
     * se i campi username e password sono vuoti.
     * In caso contrario lo attiva
     */
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Dati inseriti dall'utente
            String username = usernameInput.getEditText().getText().toString().trim();
            String password = passwordInput.getEditText().getText().toString().trim();

            //Abilita il pulsante solo se entrambi i campi non sono vuoti
            loginButton.setEnabled(!username.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /*
     * Controlla che i dati inseriti corrispondano ad un account
     * presente in memoria o che sia l'admin
     */
    public Account getAccount() {
        //Dati inseriti dall'utente
        String usernameText = usernameInput.getEditText().getText().toString().trim();
        String passwordText = passwordInput.getEditText().getText().toString().trim();

        //Controlla se sono i dati dell'account admin e imposta i relativi errori se ci sono
        if(usernameText.equals(Account.admin.getUsername()) &&
                passwordText.equals(Account.admin.getPassword())) {
            return Account.admin;
        } else if (usernameText.equals(Account.admin.getUsername())) {
            passwordInput.setError("Password errata");
            return null;
        }

        //Cerca tra i dati salvati in memoria e imposta i relativi errori se presenti
        for(Account item : mAccountList) {
            if(item.getUsername().equals(usernameText) && item.getPassword().equals(passwordText)) {
                return item;
            } else if(item.getUsername().equals(usernameText)) {
                passwordInput.setError("Password errata");
                return null;
            }
        }

        //Se l'username non è presente imposta un errore
        if(!usernameText.equals(Account.admin.getUsername())) {
            usernameInput.setError("Account inesistente");
        }
        return null;
    }
}