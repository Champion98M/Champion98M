package com.example.biggers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import java.io.Serializable;
import java.util.ArrayList;

public class AdminManagerActivity extends AppCompatActivity {

    //Dichirazione variabili dei widget da recuperare nel layout xml
    private RecyclerView mRecyclerView;
    private AccountAdaper mAdapter;
    private TextInputLayout mSearchEditText;
    private TextView mEmptyListTextView;
    private Button mHomeButton;


    public ArrayList<Account> mAccountList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    public static final String SHARED = "shared";

    private Account account = new Account();
    private Account mToChange = new Account();

    private static final String ACCOUNT_EXTRA = "com.example.biggers.Account";

    public static final String BUNDLE_TAG = "Position";

    //Boolean di controllo sull'eliminazione
    private boolean isDeleteConfirmed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager);

        //Apre il file SharedPreferences usato per il salvataggio dei dati
        sharedPreferences = getSharedPreferences(SHARED, MODE_PRIVATE);
        //Carica i file salvati e gli inserire in un ArrayList
        mAccountList = SaveDataClass.loadData(sharedPreferences);

        Intent externalIntent = getIntent();
        Serializable obj = externalIntent.getSerializableExtra(LoginActivity.ACCOUNT_EXTRA);
        account = (Account) obj;

        //Costruisce il recyclerView e le funzionalità dell'activity
        buildRecyclerView();

         /*
          * Implementa lo swype bidirezionale LEFT RIGHT degli item mostrati
          * dal recyclerView e l'azione corrispondente, la cancellazione
          */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //Trova l'indice dell'item su cui si è fatto swipe
                int position = viewHolder.getBindingAdapterPosition();
                //Estrae l'account corrispondente dalla lista
                mToChange = mAccountList.get(position);
                //Chiama la funzione che esegue la rimozione dell'account
                mAccountList.remove(position);
                //Genera l'animazione di rimozione sul recyclerView
                mAdapter.notifyItemRemoved(position);
                //Mostra il dialog di conferma o annullamento dell'eliminazione
                showConfirmDialog(position);
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    // Override del metodo per modificare il comportamento del back button di android
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminManagerActivity.this, HomeActivity.class);
        intent.putExtra(ACCOUNT_EXTRA, account);
        startActivity(intent);
        finish();
    }

    /*
     * Metodo che salva l'eliminazione di un account sul file e controlla
     *  se l'utente ha eliminato il proprio account
     */
    public void removeAccount() {
        SaveDataClass.saveData(sharedPreferences, mAccountList);
        //Se non ci sono piu utenti mostra l'avviso
        if(mAccountList.isEmpty()) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListTextView.setVisibility(View.GONE);
        }

        //Se l'utente ha eliminato il proprio account torna all'activity di login
        if (mToChange.equals(account)) {
            Intent intent = new Intent(AdminManagerActivity.this, LoginActivity.class);
            //Imposta i flags per pulire lo stack delle activity per non tornarci premendo il back button di android
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    // Genera il dialog con la richiesta di conferma di eliminazione di un account
    private void showConfirmDialog(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TAG, position);
        ConfirmDeleteDialog popUp = new ConfirmDeleteDialog();
        popUp.setArguments(bundle);
        popUp.show(getSupportFragmentManager(), ConfirmDeleteDialog.TAG);
    }

    // Se la risposta al dialog di conferma è positiva elimina l'account
    public void doPositiveClick() {
        isDeleteConfirmed = true;
        removeAccount();
    }

    // Se la risposta al dialog di conferma non è negativa ripristina l'account
    public void doNegativeClick(int position) {
        mAccountList.add(position, mToChange);
        mAdapter.notifyItemInserted(position);
    }

    /*
     * Controlla se l'eliminazione non è stata confermata,
     * in quel caso ripristina l'account. Questo metodo è
     * necessario quando il dialog di richiesta di conferma
     * dell'eliminazione è stato chiuso NON tramite i suoi
     * pulsanti di annulla (doNegativeClick())
     * e di conferma (doPositiveClick())
     */
    public void controlDelete(int position) {
        if(!isDeleteConfirmed) {
            if(position >= mAccountList.size() || !mAccountList.get(position).equals(mToChange)) {
                doNegativeClick(position);
            }
        }
    }

    // Medoto che setta nuovamente il boolean di conferma su false per evitare bug
    public void setBooleanToFalse() {
        isDeleteConfirmed = false;
    }


    /*
     * Questo metodo assegna le variabili ai widget del layout,
     * costruisce il recyclerView per la visualizzazione degli
     * account salvati, implementa la barra di ricerca e il
     * filtro sulla lista in base alla ricerca eseguita, imposta
     * il comportamento del button che abilita/disabilita gli admin
     */
    public void buildRecyclerView() {
        //Mappa i widget ai loro ids
        mSearchEditText = findViewById(R.id.search_edit_text);
        mRecyclerView = findViewById(R.id.recyclerView);
        mEmptyListTextView = findViewById(R.id.empty_list_text);
        mHomeButton = findViewById(R.id.home);

        //Costruisce il RecyclerView
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new AccountAdaper(mAccountList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //Se la lista di utenti è vuota rende la textView visibile
        if(mAccountList.isEmpty()) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListTextView.setVisibility(View.GONE);
        }

        /*
         * Aggiunge un textWatcher alla barra di ricerca
         * ed esegue la ricerca in base al testo inserito
         */
        mSearchEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        //Imposta il comportamento del button che abilita/disabilita gli admin
        mAdapter.setOnItemClickListener(position -> {
            //Trova l'indice dell'account selezionato
            mToChange = mAccountList.get(position);
            //Inverte il boolean di admin
            mToChange.setAdmin(!mAccountList.get(position).isAdmin());
            //Salva le modifiche
            SaveDataClass.saveData(sharedPreferences, mAccountList);

            /*
             * Se l'utente disabilita il proprio account
             * dalla funzionalità admin chiude questa activity
             */
            if(mToChange.equals(account)) {
                Intent intent = new Intent(AdminManagerActivity.this, HomeActivity.class);
                intent.putExtra(ACCOUNT_EXTRA, mToChange);
                startActivity(intent);
                finish();
            }
        });

        //Imposta il comportamento del button per tornare all'home
        mHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManagerActivity.this, HomeActivity.class);
            intent.putExtra(ACCOUNT_EXTRA, account);
            startActivity(intent);
            finish();
        });
    }

    //Genera un ArrayList filtrato tramite la ricerca che ha eseguito l'utente
    private void filter(String text) {
        ArrayList<Account> filteredList = new ArrayList<>();

        for(Account item : mAccountList) {
            if(item.getUsername().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        mAdapter.filterList(filteredList);
    }
}
