package com.example.moneytracker.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneytracker.DatabaseHandler;
import com.example.moneytracker.MySignal;
import com.example.moneytracker.OnTransactionLongClickListener;
import com.example.moneytracker.TransactionModel;
import com.example.moneytracker.interfaces.DownloadCallback;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentDashboardViewModel extends AndroidViewModel {
    private MutableLiveData<List<TransactionModel>> transactions;
    protected OnTransactionLongClickListener onTransactionLongClickListener = this::onTransactionLongClick;
    private DownloadCallback downloadTransactionCallback = new DownloadCallback() {
        @Override
        public void onDownloadSuccess(DataSnapshot dataSnapshot) {
            handleData(dataSnapshot);
        }

        @Override
        public void onDownloadError(String error) {
            MySignal.getInstance().toast(error);
        }
    };



    public FragmentDashboardViewModel(@NonNull Application application) {
        super(application);
        transactions = new MutableLiveData<>();
        downloadTransactions();
    }

    private void downloadTransactions() {
        DatabaseHandler.getInstance().downloadTransaction(downloadTransactionCallback);
    }

    private void handleData(DataSnapshot dataSnapshot) {
        List<TransactionModel> transactionList = new ArrayList<>();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            TransactionModel transaction = childSnapshot.getValue(TransactionModel.class);
            transactionList.add(transaction);
        }
//        transactionList = removeInvalidTransactions(transactionList);
        transactions.setValue(transactionList);
    }

//    private List<TransactionModel> removeInvalidTransactions(List<TransactionModel> transactions) {
//        List<TransactionModel> updatedTransactions = new ArrayList<>();
//        for (TransactionModel transaction : transactions) {
//        }
//        return updatedTransactions;
//    }

    public void onTransactionLongClick(String jsonTransaction) {
        MySignal.getInstance().toast(jsonTransaction);
    }

    public LiveData<List<TransactionModel>> getTransactions() {
        return transactions;
    }


}


