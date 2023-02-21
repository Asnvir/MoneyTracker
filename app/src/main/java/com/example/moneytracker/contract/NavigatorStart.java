package com.example.moneytracker.contract;

import android.app.Activity;

import androidx.fragment.app.Fragment;

public interface NavigatorStart {
    void navigateToStart();
    void navigateToLogin();
    void navigateToRegister();
    void navigateToDashboard();
    void goBack();

    static NavigatorStart getNavigator(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity instanceof NavigatorStart) {
            return (NavigatorStart) activity;
        } else {
            throw new RuntimeException("Associated activity does not implement NavigatorStart interface");
        }
    }

}
