package com.example.moneytracker.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneytracker.data.DatabaseHandler;
import com.example.moneytracker.util.MySignal;
import com.example.moneytracker.not_use.OnTransactionLongClickListener;
import com.example.moneytracker.Transaction;
import com.example.moneytracker.interfaces.DownloadCallback;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();;
//    private String title = Utils.DASHBOARD;
    private String title = "CHEEEEEEEK";
    protected OnTransactionLongClickListener onTransactionLongClickListener = this::onTransactionLongClick;
    private final DownloadCallback downloadTransactionCallback = new DownloadCallback() {
        @Override
        public void onDownloadSuccess(DataSnapshot dataSnapshot) {
            handleData(dataSnapshot);
        }

        @Override
        public void onDownloadError(String error) {
            MySignal.getInstance().toast(error);
        }
    };



    public DashboardViewModel(@NonNull Application application) {
        super(application);

        downloadTransactions();
    }

    public String getTitle() {
        return title;
    }


    private void downloadTransactions() {
        DatabaseHandler.getInstance().downloadTransaction(downloadTransactionCallback);
    }

    private void handleData(DataSnapshot dataSnapshot) {
        List<Transaction> transactionList = new ArrayList<>();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            Transaction transaction = childSnapshot.getValue(Transaction.class);
            transactionList.add(transaction);
        }

        transactions.setValue(transactionList);
    }



    public void onTransactionLongClick(String jsonTransaction) {
        MySignal.getInstance().toast(jsonTransaction);
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }


}


