package com.example.biggers;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/*
 * Classe che implementa i metodi per il caricamento e
 * salvataggio dei dati tramite l'uso di SharedPreferences
 * e degli strumenti disponibili della libreria Gson di Google
 */
public class SaveDataClass {

    //Tag usato nel file di SharedPreferences
    public static final String TEXT = "Account list";

    public static final String DATA_FILE_PATH = "/data/data/com.example.biggers/shared_prefs/shared.xml";

    //Costruttore
    public SaveDataClass() {
    }

    /*
     * Metodo che implementa il salvataggio di un ArrayList
     * di Account su un file di SharedPreferences dopo averlo
     * convertito in una stringa in formato Json
     */
    public static void saveData(SharedPreferences sharedPreferences, ArrayList<Account> accountList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //accountList.sort(Account::compareTo);
        Collections.sort(accountList, Account::compareTo);
        Gson gson = new Gson();
        String json = gson.toJson(accountList);
        editor.putString(TEXT, json);
        editor.apply();
    }

    /*
     * Metodo che implementa il caricamento dei dati presenti
     * su un file di SharedPreferences, controllando prima se esiste,
     * salvandoli su un ArrayList di Account dopo averli convertiti
     * in java objects da una stringa in formato Json
     */
    public static ArrayList<Account> loadData(SharedPreferences sharedPreferences) {
        if(new File(DATA_FILE_PATH).exists()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(TEXT, null);
            Type type = new TypeToken<ArrayList<Account>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }
}
