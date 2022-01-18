package com.example.biggers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.ArrayList;

public class AdminManagerActivity extends AppCompatActivity {

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

    private boolean isDeleteConfirmed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manager);

        sharedPreferences = getSharedPreferences(SHARED, MODE_PRIVATE);
        mAccountList = SaveDataClass.loadData(sharedPreferences);

        Intent externalIntent = getIntent();
        Serializable obj = externalIntent.getSerializableExtra(LoginActivity.ACCOUNT_EXTRA);
        account = (Account) obj;

        buildRecyclerView();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                mToChange = mAccountList.get(position);
                mAccountList.remove(position);
                mAdapter.notifyItemRemoved(position);
                showConfirmDialog(position);
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminManagerActivity.this, HomeActivity.class);
        intent.putExtra(ACCOUNT_EXTRA, account);
        startActivity(intent);
        finish();
    }

    public void removeAccount() {
        SaveDataClass.saveData(sharedPreferences, mAccountList);
        if(mAccountList.isEmpty()) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListTextView.setVisibility(View.GONE);
        }
        if (mToChange.equals(account)) {
            Intent intent = new Intent(AdminManagerActivity.this, LoginActivity.class);
            //Imposta i flags per pulire lo stack delle activity per non tornarci premendo il back button di android
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void showConfirmDialog(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TAG, position);
        ConfirmDeleteDialog popUp = new ConfirmDeleteDialog();
        popUp.setArguments(bundle);
        popUp.show(getSupportFragmentManager(), ConfirmDeleteDialog.TAG);
    }

    public void doPositiveClick() {
        isDeleteConfirmed = true;
        removeAccount();
    }

    public void doNegativeClick(int position) {
        mAccountList.add(position, mToChange);
        mAdapter.notifyItemInserted(position);
    }

    public void controlDelete(int position) {
        if(!isDeleteConfirmed) {
            if(position >= mAccountList.size() || !mAccountList.get(position).equals(mToChange)) {
                doNegativeClick(position);
            }
        }
    }

    public void buildRecyclerView() {
        mSearchEditText = findViewById(R.id.search_edit_text);
        mRecyclerView = findViewById(R.id.recyclerView);
        mEmptyListTextView = findViewById(R.id.empty_list_text);
        mHomeButton = findViewById(R.id.home);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new AccountAdaper(mAccountList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if(mAccountList.isEmpty()) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListTextView.setVisibility(View.GONE);
        }

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

        mAdapter.setOnItemClickListener(position -> {
            mToChange = mAccountList.get(position);
            mToChange.setAdmin(!mAccountList.get(position).isAdmin());
            SaveDataClass.saveData(sharedPreferences, mAccountList);

            if(mToChange.equals(account)) {
                Intent intent = new Intent(AdminManagerActivity.this, HomeActivity.class);
                intent.putExtra(ACCOUNT_EXTRA, mToChange);
                startActivity(intent);
                finish();
            }
        });

        mHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManagerActivity.this, HomeActivity.class);
            intent.putExtra(ACCOUNT_EXTRA, account);
            startActivity(intent);
            finish();
        });
    }

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
