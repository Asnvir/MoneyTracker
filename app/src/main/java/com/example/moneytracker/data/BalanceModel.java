package com.example.moneytracker.data;

import com.example.moneytracker.util.Constants;
import com.google.firebase.database.PropertyName;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.NODE_INCOME, income);
        map.put(Constants.NODE_EXPENSE, expense);
        map.put(Constants.NODE_BALANCE, balance);
        return map;
    }

}
