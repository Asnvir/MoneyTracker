package com.example.moneytracker.util;

import com.example.moneytracker.Transaction;

import java.util.ArrayList;

public class ModifiedData {
    private ArrayList<Transaction> transactions;
    private String expense;
    private String income;
    private String balance;

    public ModifiedData(ArrayList<Transaction> transactions, String expense, String income, String balance) {
        this.transactions = transactions;
        this.expense = expense;
        this.income = income;
        this.balance = balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public String getExpense() {
        return expense;
    }

    public String getIncome() {
        return income;
    }

    public String getBalance() {
        return balance;
    }
}
