package com.example.moneytracker;

import java.util.Objects;

public class TransactionModel {
    private String amount;
    private String note;
    private String transactionID;
    private String category;
    private String type;
    private String date;
    private String time;

    public TransactionModel() {

    }

    public TransactionModel(String amount, String note, String transactionID, String category, String type, String date, String time) {
        this.amount = amount;
        this.note = note;
        this.transactionID = transactionID;
        this.category = category;
        this.type = type;
        this.date = date;
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public TransactionModel setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getNote() {
        return note;
    }

    public TransactionModel setNote(String note) {
        this.note = note;
        return this;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public TransactionModel setTransactionID(String transactionID) {
        this.transactionID = transactionID;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public TransactionModel setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getType() {
        return type;
    }

    public TransactionModel setType(String type) {
        this.type = type;
        return this;
    }

    public String getDate() {
        return date;
    }

    public TransactionModel setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTime() {
        return time;
    }

    public TransactionModel setTime(String time) {
        this.time = time;
        return this;
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
