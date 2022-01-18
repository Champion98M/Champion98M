package com.example.biggers;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.Serializable;

// Activity che mostra la home dopo aver effetuato il login
public class HomeActivity extends AppCompatActivity {

    //Dichirazione variabili dei widget da recuperare nel layout xml
    private Button logoutButton, managerButton;
    private TextView changePasswordText;
    private TextView usernameData, passwordData, townData, dateData, welcomeMessage;
    private ImageView adminImage;
    private Account account;

    private static final String ACCOUNT_EXTRA = "com.example.biggers.Account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent externalIntent = getIntent();
        Serializable obj = externalIntent.getSerializableExtra(LoginActivity.ACCOUNT_EXTRA);
        account = (Account) obj;

        //Mappa i widget ai loro ids
        findWidgetIds();
        //Aggiorna le informazioni mostrate su schermo
        updateTextViews();
        //Attiva lo scroll alle TextViews
        setScrollingTextViews();

        //Quando il pulsante di logout viene premuto passa all'activity di login
        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            //Imposta i flags per pulire lo stack delle activity per non tornarci premendo il back button di android
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        /*
         * Quando il pulsante di cambio password viene
         * premuto passa all'activity di cambio password
         */
        changePasswordText.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ChangePasswordActivity.class);
            intent.putExtra(ACCOUNT_EXTRA, account);
            startActivity(intent);
            finish();
        });

        /*
         * Quando il pulsante di gestione utenti, visibile solo dagli
         *  admin viene premuto, passa all'activity corrispondente
         */
        managerButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, AdminManagerActivity.class);
            intent.putExtra(ACCOUNT_EXTRA, account);
            startActivity(intent);
            finish();
        });
    }

    // Override del metodo per comportamento il comportamento del back button di android
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        //Imposta i flags per pulire lo stack delle activity per non tornarci premendo il back button di android
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Assegna alle variabili il widget tramite il loro ID
    private void findWidgetIds() {
        logoutButton = (Button) findViewById(R.id.logout);
        managerButton = (Button) findViewById(R.id.manager);
        changePasswordText = (TextView) findViewById(R.id.change_password);
        usernameData = (TextView) findViewById(R.id.username_user);
        passwordData = (TextView) findViewById(R.id.password_user);
        townData = (TextView) findViewById(R.id.town_user);
        dateData = (TextView) findViewById(R.id.date_user);
        welcomeMessage = (TextView) findViewById(R.id.welcome_message);
        adminImage = (ImageView) findViewById(R.id.imageView);
    }

    /*
     * -Imposta la visibilità dei pulsanti e degli avvisi
     * in base al fatto che l'account sia admin o meno.
     * -Imposta i dati dell'account da mostrare alle
     * TextViews opportune
     */
    private void updateTextViews() {
        // Cambia la visibilità dei pulsanti per admin
        if(account.isAdmin()) {
            adminImage.setVisibility(View.VISIBLE);
            managerButton.setVisibility(View.VISIBLE);
        } else {
            adminImage.setVisibility(View.GONE);
        }

        /*
         * Se l'account che ha effetuato il login è l'admin
         * disabilita la possibilità di cambiare password
         */
        if(account.equals(Account.admin)) {
            changePasswordText.setVisibility(View.GONE);
        } else {
            changePasswordText.setVisibility(View.VISIBLE);
        }

        // Setta le TextViews
        String welcome = "Benvenuto " + account.getUsername() + "!";
        welcomeMessage.setText(welcome);
        usernameData.setText(account.getUsername());
        passwordData.setText(account.getPassword());
        townData.setText(account.getTown());
        dateData.setText(account.getDate());
    }

    // Attiva lo scroll orizzontale alle TextViews con i dati dell'account
    private void setScrollingTextViews() {
        usernameData.setMovementMethod(new ScrollingMovementMethod());
        usernameData.setHorizontallyScrolling(true);

        passwordData.setMovementMethod(new ScrollingMovementMethod());
        passwordData.setHorizontallyScrolling(true);

        townData.setMovementMethod(new ScrollingMovementMethod());
        townData.setHorizontallyScrolling(true);
    }
}