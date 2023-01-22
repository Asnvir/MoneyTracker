package com.example.moneytracker;

import android.os.Parcelable;

import java.io.Serializable;

public interface Navigator  {
    void showOptionsScreen();
    void showAddTransactionScreen();
    void showEditTransactionScreen(String jsonTransaction);
    void showTransactionsScreen();
    void goBack();


}
