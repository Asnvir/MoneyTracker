package com.example.moneytracker;

import com.example.moneytracker.util.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class Transaction {
    private String transactionID;
    private String date;
    private String time;
    private double amount;
    private String category;
    private String note;
    private String type;



    public Transaction() {
    }


    public Transaction setTransactionID() {
        transactionID = UUID.randomUUID().toString();
        return this;
    }

    public Transaction setDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date = dateFormat.format(new Date());
        return this;
    }

    public Transaction setTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        time = timeFormat.format(new Date());
        return this;
    }

    public Transaction setAmount(double amount) {
        this.amount = (double) Math.round(amount * 100) / 100;
        return this;
    }

    public Transaction setCategory(String category){
        this.category = category;
        return this;
    }

    public Transaction setNote(String note) {
        this.note = note;
        return this;
    }

    public Transaction setType(String type) {
        this.type = type;
        return this;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionID, that.transactionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionID);
    }
}
