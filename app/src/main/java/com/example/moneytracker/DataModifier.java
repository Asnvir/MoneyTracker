package com.example.moneytracker;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DataModifier {

    private DataModifier() {
    }

    public static ModifiedData modifyData(DataSnapshot dataSnapshot) {
        ArrayList<TransactionModel> transactions = getTransactions(dataSnapshot.child("transactions"));
        sortTransactions(transactions);
        Map<String, String> expenseIncomeBalance = calculateExpenseIncomeBalance(transactions);
        modifyTransactionAmounts(transactions);

        String expense = expenseIncomeBalance.get("expense");
        String income = expenseIncomeBalance.get("income");
        String balance = expenseIncomeBalance.get("balance");

        return new ModifiedData(transactions, expense, income, balance);
    }

    private static ArrayList<TransactionModel> getTransactions(DataSnapshot dataSnapshot) {
        ArrayList<TransactionModel> transactions = new ArrayList<>();
        for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()) {
            TransactionModel model = transactionSnapshot.getValue(TransactionModel.class);
            transactions.add(model);
        }
        return transactions;
    }

    private static void sortTransactions(ArrayList<TransactionModel> transactions) {
        Collections.sort(transactions, sortByDateAndTime);
    }

    private static Comparator<TransactionModel> sortByDateAndTime = (o1, o2) -> {
        int dateCompare = o2.getDate().compareTo(o1.getDate());
        if (dateCompare == 0) {
            return o2.getTime().compareTo(o1.getTime());
        } else {
            return dateCompare;
        }
    };

    private static Map<String, String> calculateExpenseIncomeBalance(ArrayList<TransactionModel> transactions) {
        double generalExpense = 0;
        double generalIncome = 0;
        double generalBalance;
        for (TransactionModel model : transactions) {
            double amount = Double.parseDouble(model.getAmount());
            String type = model.getType();
            if (type.equals("expense")) {
                generalExpense += amount;
            } else if (type.equals("income")) {
                generalIncome += amount;
            }
        }
        generalBalance = generalIncome - generalExpense;
        String expense = generalExpense == 0 ? "0" : "- " + String.valueOf(generalExpense);
        String income = generalIncome == 0 ? "0" : "+ " + String.valueOf(generalIncome);
        String balance = generalBalance == 0 ? "0" : generalBalance < 0 ? String.valueOf(generalBalance) : "+ " + String.valueOf(generalBalance);

        Map<String, String> result = new HashMap<>();
        result.put("expense", expense);
        result.put("income", income);
        result.put("balance", balance);
        return result;
    }

    private static void modifyTransactionAmounts(ArrayList<TransactionModel> transactions) {
        for (TransactionModel model : transactions) {
            String type = model.getType();
            double amount = Double.parseDouble(model.getAmount());
            if (type.equals("expense")) {
                model.setAmount("-" + String.valueOf(amount));
            } else if (type.equals("income")) {
                model.setAmount("+" + String.valueOf(amount));
            }
        }
    }

}