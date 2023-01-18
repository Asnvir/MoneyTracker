package com.example.moneytracker;

interface OnUserCreationListener {
    void onError(String errorMessage);
    void onSuccess();
}
