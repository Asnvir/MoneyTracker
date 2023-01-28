package com.example.moneytracker.navigation;

public interface Navigator {

//    void showBoxSelectionScreen(String options);
//
//    void showAboutScreen();
//
//    void showCongratulationsScreen();
//
//    void goBack();
//
//    void goToMenu();


    void showOptionsScreen();

    void showAddTransactionScreen();

    void showEditTransactionScreen(String jsonTransaction);

    void showTransactionsScreen();

    void goBack();
}