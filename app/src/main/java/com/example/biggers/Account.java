package com.example.biggers;

import androidx.annotation.NonNull;
import java.io.Serializable;

/*
 * Classe per gestire i dati degli account registrati
 */
public class Account implements Serializable, Comparable<Account> {

    /*
     * Dichiarazione dei campi di ogni account
     */
    private String username, password, town, date;
    private boolean isAdmin;

    // Dichiarazione dell'account admin fisso non modificabile
    public static final Account admin = new Account(
            "admin",
            "admin",
            "Villanova Strisaili",
            "21 Novembre 1998",
            true);
    /*
     * Costruttori di oggetti Account
     */
    public Account(String username, String password, String town, String date, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.town = town;
        this.date = date;
        this.isAdmin = isAdmin;
    }

    public Account() {
        this.username = "";
        this.password = "";
        this.town = "";
        this.isAdmin = false;
    }

    // Metodo che confronta 2 Objects di tipo Account tramite l'username
    public boolean equals(@NonNull Account account) {
        return this.username.equals(account.getUsername());
    }

    //Implementa il comparatore per ordinare la lista in base all'username
    @Override
    public int compareTo(Account account) {
        return this.username.compareToIgnoreCase(account.getUsername());
    }

    /*
     * Definizione dei Getter e dei Setter
     */
    public String getUsername() {
        return username.trim();
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getPassword() {
        return password.trim();
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }

    public String getTown() {
        return town.trim();
    }

    public void setTown(String town) {
        this.town = town.trim();
    }

    public String getDate() {
        return date.trim();
    }

    public void setDate(String date) {
        this.date = date.trim();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

}
