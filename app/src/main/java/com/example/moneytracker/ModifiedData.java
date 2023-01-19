package com.example.moneytracker;

import java.util.ArrayList;

public class ModifiedData {
    private ArrayList<TransactionModel> transactions;
    private String expense;
    private String income;
    private String balance;

    public ModifiedData(ArrayList<TransactionModel> transactions, String expense, String income, String balance) {
        this.transactions = transactions;
        this.expense = expense;
        this.income = income;
        this.balance = balance;
    }

    public ArrayList<TransactionModel> getTransactions() {
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
