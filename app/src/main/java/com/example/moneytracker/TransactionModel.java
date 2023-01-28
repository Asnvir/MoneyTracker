package com.example.moneytracker;

import com.example.moneytracker.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class TransactionModel {
    private String transactionID;
    private String date;
    private String time;
    private double amount;
    private String category;
    private String note;
    private String type;



    public TransactionModel() {
    }


    public TransactionModel setTransactionID() {
        transactionID = UUID.randomUUID().toString();
        return this;
    }

    public TransactionModel setDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date = dateFormat.format(new Date());
        return this;
    }

    public TransactionModel setTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        time = timeFormat.format(new Date());
        return this;
    }

    public TransactionModel setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public TransactionModel setCategory(String category){
        this.category = category;
        return this;
    }

    public TransactionModel setNote(String note) {
        this.note = note;
        return this;
    }

    public TransactionModel setType(Boolean isExpense,Boolean isIncome) {
        if(!isExpense && isIncome){
            type = Utils.INCOME;
        }else if (isExpense && !isIncome){
            type = Utils.EXPENSE;
        }
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
        TransactionModel that = (TransactionModel) o;
        return Objects.equals(transactionID, that.transactionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionID);
    }
}
