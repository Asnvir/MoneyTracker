package com.example.moneytracker.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.mainScreen.EditTransactionViewModel;

public class EditTransactionViewModelFactory implements ViewModelProvider.Factory {

    private final TransactionModel transactionModel;

    public EditTransactionViewModelFactory(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EditTransactionViewModel.class)) {
            T viewModel = modelClass.cast(new EditTransactionViewModel(transactionModel));
            if (viewModel != null) {
                return viewModel;
            } else {
                throw new NullPointerException("Failed to cast ViewModel to " + modelClass.getSimpleName());
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
