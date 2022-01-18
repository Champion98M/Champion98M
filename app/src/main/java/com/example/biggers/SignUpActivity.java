package com.example.biggers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import com.google.android.material.textfield.TextInputLayout;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Pattern;

// Activity che permette la registrazione di nuovi utenti
public class SignUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    //Dichirazione variabili dei widget da recuperare nel layout xml
    private TextInputLayout username, password, confirmPass, town, date;
    private Button signUp;

    private Account account;

    private Calendar inputDate;

    //Regular Expression per verificare i requisiti della password
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@%$£!&#?]).{8,}");
    private static final Pattern PASSWORD_DIGIT_PATTERN = Pattern.compile("^(?=.*\\d).*");
    private static final Pattern PASSWORD_LOWERCASE_PATTERN = Pattern.compile("^(?=.*[a-z]).*");
    private static final Pattern PASSWORD_UPPERCASE_PATTERN = Pattern.compile("^(?=.*[A-Z]).*");
    private static final Pattern PASSWORD_SPECIAL_CHAR_PATTERN = Pattern.compile("^(?=.*[@%$£!&#?]).*");


    //Dichiara un ArrayList di Account
    private ArrayList<Account> mAccountList = new ArrayList<>();
    //Nome del file di SharedPreferences
    public static final String SHARED = "shared";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Apre il file SharedPreferences usato per il salvataggio dei dati
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED, MODE_PRIVATE);
        //Carica i file salvati e gli inserire in un ArrayList
        mAccountList = SaveDataClass.loadData(sharedPreferences);

        //Mappa i widget ai loro ids
        findWidgetIds();

        account = new Account();

        //Rimuove gli errori dalle EditTexts
        removeErrorsEditTexts();

        /*
         * Apre il DatePicker ogni volta che L'EditText corrispondente
         * viene premuta senza dover cambiare il focus
         */
        date.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                //Rimuove la tastiera dallo schermo
                date.getEditText().setRawInputType(InputType.TYPE_NULL);
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                //Mostra il DatePicker
                new DatePickerFragment().show(getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        });

        //Apre il DatePicker quando premuta l'EditText corrispondente
        date.getEditText().setOnClickListener(v -> {
            date.getEditText().setRawInputType(InputType.TYPE_NULL);
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), DatePickerFragment.TAG);
        });

        /*
         * Quando viene premuto il pulsante di registrazione
         * esegue il controllo sui campi e passa alla
         * activity di login
         */
        signUp.setOnClickListener(view -> {
            //Controlla i campi inseriti e se l'username non sia gia presente
            if (checkInput() & !isUsernameAlreadyPresent()) {
                //Setta i dati inseriti a un oggetto Account
                updateAccount();
                //Aggiunge il nuovo accounnt alla lista
                mAccountList.add(account);
                //Salva la nuova lista sul file di SharedPreferences
                SaveDataClass.saveData(sharedPreferences, mAccountList);
                //Passa alla nuova activity
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Assegna alle variabili il widget tramite il loro ID
    private void findWidgetIds() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirmPass);
        town = findViewById(R.id.town);
        date = findViewById(R.id.date);
        signUp = findViewById(R.id.signUp);
    }

    // Rimuove gli errori dalle EditTexts da compilare quando il testo cambia
    private void removeErrorsEditTexts() {
        Objects.requireNonNull(username.getEditText()).addTextChangedListener(new CustomTextWatcher(username));
        Objects.requireNonNull(password.getEditText()).addTextChangedListener(new CustomTextWatcher(password));
        Objects.requireNonNull(confirmPass.getEditText()).addTextChangedListener(new CustomTextWatcher(confirmPass));
        Objects.requireNonNull(town.getEditText()).addTextChangedListener(new CustomTextWatcher(town));
        Objects.requireNonNull(date.getEditText()).addTextChangedListener(new CustomTextWatcher(date));
    }

    // Assegna la data inserita tramite il DataPicker al campo corrispondente
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        inputDate = calendar;
        String currentDateString = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());

        date.getEditText().setText(currentDateString);
    }

    /*
     * Metodo che controlla se i campi inseriti
     * durante la fase di registrazione sono corretti
     */
    private boolean checkInput() {
        int errors = 0;

        //Salva la data attuale
        Calendar rightNow = Calendar.getInstance();
        rightNow.set(Calendar.YEAR, rightNow.get(Calendar.YEAR) - 18);

        //Controllo sull'username
        if(username.getEditText().getText().toString().trim().isEmpty()) {
            errors++;
            username.setError("Inserisci un username");
        } else {
            username.setError(null);
        }

        //Controllo sulla città di provenienza
        if(town.getEditText().getText().toString().trim().isEmpty()) {
            errors++;
            town.setError("Inserisci la tua città di residenza");
        } else {
            town.setError(null);
        }

        //Controllo sulla data per verificare anche che l'utente sia maggiorenne
        if(date.getEditText().getText().toString().trim().isEmpty()) {
            errors++;
            date.setError("Inserisci la tua data di nascita");
        } else if (!(inputDate.before(rightNow))) {
            errors++;
            date.setError("Devi essere maggiorenne per iscriverti");
        } else { ;
            date.setError(null);
        }

        //Torna true solo se non sono presenti errori sulla password e sugli altri campi
        return  checkPassword(password, confirmPass) && errors == 0;
    }

    /*
     * Controlla che le specifiche sulla password
     * inserita siano rispettate e che i campi
     * "Password" e "conferma password" siano uguali
     */
    public static boolean checkPassword(TextInputLayout password, TextInputLayout confirmPassword) {
        //Dati inseriti dall'utente
        String pass = password.getEditText().getText().toString().trim();
        String confPass = confirmPassword.getEditText().getText().toString().trim();

        //Controlla che la password non sia vuota e soddisfi i requisiti minimi
        if (pass.isEmpty()) {
            password.setError("Inserisci una password");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pass).matches()) {
            if (!PASSWORD_DIGIT_PATTERN.matcher(pass).matches()) {
                password.setError("Inserisci almeno un numero");
                return false;
            } else if (!PASSWORD_LOWERCASE_PATTERN.matcher(pass).matches()) {
                password.setError("Inserisci almeno una lettera minuscola");
                return false;
            } else if (!PASSWORD_UPPERCASE_PATTERN.matcher(pass).matches()) {
                password.setError("Inserisci almeno una lettera maiuscola");
                return false;
            } else if (!PASSWORD_SPECIAL_CHAR_PATTERN.matcher(pass).matches()) {
                password.setError("Inserisci almeno un carattere tra: @%$£!&#?");
                return false;
            } else {
                password.setError("La password deve contenere almeno 8 caratteri");
                return false;
            }
        } else {
            password.setError(null);
        }

        //Controlla se il campo conferma password corrisponda al campo password
        if(!pass.equals(confPass)) {
            confirmPassword.setError("La password non coincide");
            return false;
        } else {
            confirmPassword.setError(null);
        }

        return true;
    }

    // Assegna i dati inseriti a un oggetto di tipo Account
    private void updateAccount() {
        account.setUsername(username.getEditText().getText().toString());
        account.setPassword(password.getEditText().getText().toString());
        account.setTown(town.getEditText().getText().toString());
        account.setDate(date.getEditText().getText().toString());
    }

    // Controlla se l'username inserito in fase di registrazione non sia già registrato
    private boolean isUsernameAlreadyPresent() {
        //Username inserito dall'utente
        String newUsername = username.getEditText().getText().toString().trim();

        for(Account item : mAccountList) {
            if(newUsername.equals(item.getUsername()) || newUsername.equals(Account.admin.getUsername())) {
                username.setError("Username già esistente");
                return true;
            }
        }
        return false;
    }
}