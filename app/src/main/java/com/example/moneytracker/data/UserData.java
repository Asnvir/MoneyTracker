package com.example.moneytracker.data;

import java.util.List;

public class UserData {

    private final List<TransactionModel> transactionModels;
    private final BalanceModel balanceModel;

    public UserData(List<TransactionModel> transactionModels, BalanceModel balanceModel) {
        this.transactionModels = transactionModels;
        this.balanceModel = balanceModel;
    }

    public   List<TransactionModel> getTransactions() {
        return transactionModels;
    }

    public BalanceModel getAmount() {
        return balanceModel;
    }
}
