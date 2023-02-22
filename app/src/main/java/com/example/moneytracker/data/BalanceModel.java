package com.example.moneytracker.data;

import com.example.moneytracker.util.Constants;
import com.google.firebase.database.PropertyName;

public class BalanceModel {
    @PropertyName(Constants.NODE_INCOME)
    private double income;
    @PropertyName(Constants.NODE_BALANCE)
    private double balance;
    @PropertyName(Constants.NODE_EXPENSE)
    private double expense;

    public BalanceModel() {
    }

    public BalanceModel(double income, double balance, double expense) {
        this.income = income;
        this.balance = balance;
        this.expense = expense;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

}
