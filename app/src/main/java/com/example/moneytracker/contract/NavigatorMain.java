package com.example.moneytracker.contract;

import androidx.fragment.app.Fragment;

import com.example.moneytracker.data.TransactionModel;

public interface NavigatorMain {
    void navigateToDashboard();
    void navigateToAddTransaction();
    void navigateToEditTransaction(TransactionModel transactionModel);
    void goBack();

    static NavigatorMain getNavigator(Fragment fragment) {
        return (NavigatorMain) fragment.requireActivity();
    }
}
