package com.example.moneytracker.contract;

import androidx.fragment.app.Fragment;

import com.example.moneytracker.data.TransactionModel;

public interface NavigatorMain {
    void navigateToDashboard();

    void navigateToAddTransaction();

    void navigateToEditTransaction(TransactionModel transactionModel);

    void navigateToSettings();

    void goToWelcome();

    static NavigatorMain getNavigator(Fragment fragment) {
        return (NavigatorMain) fragment.requireActivity();
    }
}
