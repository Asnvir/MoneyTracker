package com.example.moneytracker.data;

import com.example.moneytracker.Transaction;
import com.example.moneytracker.util.Utils;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DataModifier {

    private DataModifier() {
    }

//    public static ModifiedData modifyData(DataSnapshot dataSnapshot) {
//        ArrayList<TransactionModel> transactions = getTransactions(dataSnapshot.child("transactions"));
//        sortTransactions(transactions);
////        Map<String, String> expenseIncomeBalance = calculateExpenseIncomeBalance(transactions);
////        modifyTransactionAmounts(transactions);
//
////        String expense = expenseIncomeBalance.get("expense");
////        String income = expenseIncomeBalance.get("income");
////        String balance = expenseIncomeBalance.get("balance");
//
////        return new ModifiedData(transactions, expense, income, balance);
//        return new ModifiedData(transactions);
//    }

    private static ArrayList<Transaction> getTransactions(DataSnapshot dataSnapshot) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()) {
            Transaction model = transactionSnapshot.getValue(Transaction.class);
            transactions.add(model);
        }
        return transactions;
    }

    private static void sortTransactions(ArrayList<Transaction> transactions) {
        Collections.sort(transactions, sortByDateAndTime);
    }

    private static Comparator<Transaction> sortByDateAndTime = (o1, o2) -> {
        int dateCompare = o2.getDate().compareTo(o1.getDate());
        if (dateCompare == 0) {
            return o2.getTime().compareTo(o1.getTime());
        } else {
            return dateCompare;
        }
    };

    private static Map<String, Double> calculateExpenseIncomeBalance(ArrayList<Transaction> transactions) {
        double generalExpense = 0;
        double generalIncome = 0;
        double generalBalance;
        for (Transaction model : transactions) {
            double amount = model.getAmount();
            String type = model.getType();
            if (type.equalsIgnoreCase(Utils.EXPENSE)) {
                generalExpense += amount;
            } else if (type.equalsIgnoreCase(Utils.INCOME)) {
                generalIncome += amount;
            }
        }
        generalBalance = generalIncome - generalExpense;

        Map<String, Double> result = new HashMap<>();
        result.put(Utils.EXPENSE, generalExpense);
        result.put(Utils.INCOME, generalIncome);
        result.put(Utils.BALANCE, generalBalance);
        return result;
    }

//    private static void modifyTransactionAmounts(ArrayList<TransactionModel> transactions) {
//        for (TransactionModel model : transactions) {
//            String type = model.getType();
//            double amount = Double.parseDouble(model.getAmount());
//            if (type.equals("expense")) {
//                model.setAmount("-" + String.valueOf(amount));
//            } else if (type.equals("income")) {
//                model.setAmount("+" + String.valueOf(amount));
//            }
//        }
//    }

}