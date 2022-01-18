package com.example.biggers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

// Activity per la modifica della password
public class ChangePasswordActivity extends AppCompatActivity {

    //Dichirazione variabili dei widget da recuperare nel layout xml
    private TextView usernameUser, passwordUser, checkText;
    private TextInputLayout password, confirmPassword;
    private Button updatePassword, home;
    private Account account;

    private static final String ACCOUNT_EXTRA = "com.example.biggers.Account";

    //Dichiara un ArrayList di Account
    private ArrayList<Account> mAccountList = new ArrayList<>();

    //Nome del file di SharedPreferences
    public static final String SHARED = "shared";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //Apre il file SharedPreferences usato per il salvataggio dei dati
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED, MODE_PRIVATE);
        //Carica i file salvati e gli inserire in un ArrayList
        mAccountList = SaveDataClass.loadData(sharedPreferences);

        /*
         * Controlla se il Bundle in parametro alla onCreate è null o contiene dati,
         * se non li contiene gli acquisisce tramite l'intent, altrimenti gli
         * preleva dal bundle
         */
        if(savedInstanceState == null) {
            Intent externalIntent = getIntent();
            Serializable obj = externalIntent.getSerializableExtra(LoginActivity.ACCOUNT_EXTRA);
            account = (Account) obj;
        } else {
            account = (Account) savedInstanceState.getSerializable(ACCOUNT_EXTRA);
        }

        //Mappa i widget ai loro ids
        findWidgetIds();
        //Aggiorna le informazioni mostrate su schermo
        updateTextViews();
        //Rimuove gli errori dagli EditTexts
        removeErrorsEditTexts();
        //Attiva lo scroll alle TextViews
        setScrollingTextViews();

        //Se la nuova password soddisfa i requisiti salva la modifica
        updatePassword.setOnClickListener(view -> {
            checkText.setVisibility(View.GONE);
            if (SignUpActivity.checkPassword(password, confirmPassword) && checkOldPassword()) {
                //Rimuove la tastiera dallo schermo
                updatePassword.setRawInputType(InputType.TYPE_NULL);
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(updatePassword.getWindowToken(), 0);

                //Trova l'account in cui modificare la password
                account = getAccount();
                //Setta la nuova password
                account.setPassword(password.getEditText().getText().toString());
                //Mostra il messaggio di feedback
                checkText.setVisibility(View.VISIBLE);
                //Aggiorna le informazioni mostrate su schermo
                updateTextViews();
                //Salva i dati
                SaveDataClass.saveData(sharedPreferences, mAccountList);
            }
        });

        // Passa alla schermata di home se viene premuto il pulsante "home"
        home.setOnClickListener(view -> {
            Intent intent = new Intent(ChangePasswordActivity.this, HomeActivity.class);
            intent.putExtra(ACCOUNT_EXTRA, account);
            startActivity(intent);
        });
    }

    // Override del metodo per modificare il comportamento del back button di android
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChangePasswordActivity.this, HomeActivity.class);
        intent.putExtra(ACCOUNT_EXTRA, account);
        startActivity(intent);
        finish();
    }

    // Assegna alle variabili il widget tramite il loro ID
    private void findWidgetIds() {
        usernameUser = (TextView) findViewById(R.id.username_user);
        passwordUser = (TextView) findViewById(R.id.password_user);
        checkText = (TextView) findViewById(R.id.check_text);
        password = (TextInputLayout) findViewById(R.id.password);
        confirmPassword = (TextInputLayout) findViewById(R.id.confirm_password);
        updatePassword = (Button) findViewById(R.id.update_password);
        home = (Button) findViewById(R.id.home);
    }

    // Rimuove gli errori dai campi da compilare quando il testo cambia
    private void removeErrorsEditTexts() {
        Objects.requireNonNull(password.getEditText()).addTextChangedListener(new CustomTextWatcher(password));
        Objects.requireNonNull(confirmPassword.getEditText()).addTextChangedListener(new CustomTextWatcher(confirmPassword));
    }

    // Controlla se la nuova password inserita è uguale a quella attuale
    private boolean checkOldPassword() {
        if(!password.getEditText().getText().toString().trim().equals(account.getPassword())) {
            return true;
        }
        password.setError("Uguale alla password attuale");
        return false;
    }

    //Cerca e ritorna un account ricercandolo nell'ArrayList tramite Username
    private Account getAccount() {
        for(Account item : mAccountList) {
            if(account.getUsername().equals(item.getUsername())) {
                return item;
            }
        }
        return null;
    }

    //Aggiorna le TextViews e le EditTexts
    private void updateTextViews() {
        usernameUser.setText(account.getUsername());
        passwordUser.setText(account.getPassword());

        Objects.requireNonNull(password.getEditText()).setText("");
        Objects.requireNonNull(confirmPassword.getEditText()).setText("");

        //Rimuove il focus dalle EditTexts
        getWindow().getDecorView().clearFocus();
    }

    // Attiva lo scroll orizzontale alle TextViews con i dati dell'account
    private void setScrollingTextViews() {
        usernameUser.setMovementMethod(new ScrollingMovementMethod());
        usernameUser.setHorizontallyScrolling(true);

        passwordUser.setMovementMethod(new ScrollingMovementMethod());
        passwordUser.setHorizontallyScrolling(true);
    }

    // Inserisce l'account attuale in un bundle per essere preso all'onCreate
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ACCOUNT_EXTRA, account);
    }
}