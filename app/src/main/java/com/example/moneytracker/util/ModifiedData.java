package com.example.moneytracker.util;

import com.example.moneytracker.data.TransactionModel;

import java.util.ArrayList;

public class ModifiedData {
    private ArrayList<TransactionModel> transactionModels;
    private String expense;
    private String income;
    private String balance;

    public ModifiedData(ArrayList<TransactionModel> transactionModels, String expense, String income, String balance) {
        this.transactionModels = transactionModels;
        this.expense = expense;
        this.income = income;
        this.balance = balance;
    }

    public ArrayList<TransactionModel> getTransactions() {
        return transactionModels;
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
