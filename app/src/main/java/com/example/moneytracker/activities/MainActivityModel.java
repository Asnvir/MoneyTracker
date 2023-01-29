package com.example.moneytracker.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneytracker.data.DatabaseHandler;
import com.example.moneytracker.interfaces.DownloadCallback;
import com.example.moneytracker.util.MySignal;
import com.example.moneytracker.util.Utils;
import com.google.firebase.database.DataSnapshot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivityModel extends AndroidViewModel {

    private final MutableLiveData<Double> income = new MutableLiveData<>();
    private final MutableLiveData<Double> balance = new MutableLiveData<>();
    private final MutableLiveData<Double> expense = new MutableLiveData<>();

    private final DownloadCallback downloadAmountsCallback = new DownloadCallback() {
        @Override
        public void onDownloadSuccess(DataSnapshot dataSnapshot) {
            updateAmounts(dataSnapshot);
        }

        @Override
        public void onDownloadError(String error) {
            MySignal.getInstance().toast(error);
        }
    };


    public MainActivityModel(@NonNull Application application) {
        super(application);
        downloadIncomeBalanceExpense();
    }

    private void updateAmounts(DataSnapshot dataSnapshot) {
        double incomeValue = 0.0;
        double balanceValue = 0.0;
        double expenseValue = 0.0;

        if (dataSnapshot.exists()) {
            incomeValue = getDoubleValue(dataSnapshot, Utils.NODE_INCOME);
            balanceValue = getDoubleValue(dataSnapshot, Utils.NODE_BALANCE);
            expenseValue = getDoubleValue(dataSnapshot, Utils.NODE_EXPENSE);
        }

        income.setValue(incomeValue);
        balance.setValue(balanceValue);
        expense.setValue(expenseValue);
    }

    private double getDoubleValue(DataSnapshot dataSnapshot, String fieldName) {
        if (dataSnapshot.hasChild(fieldName) && dataSnapshot.child(fieldName).getValue() != null) {
            Double value = dataSnapshot.child(fieldName).getValue(Double.class);
            if(value!=null)
                return value;
        }
        return 0.0;
    }

    private void downloadIncomeBalanceExpense() {
        DatabaseHandler.getInstance().downloadAmounts(downloadAmountsCallback);
    }

    public LiveData<Double> getExpense() {
        return expense;
    }

    public LiveData<Double> getIncome() {
        return income;
    }

    public LiveData<Double> getBalance() {
        return balance;
    }
}
