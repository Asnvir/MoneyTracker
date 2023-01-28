package com.example.moneytracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DashBoardViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> income = new MutableLiveData<>();
    private MutableLiveData<Integer> balance = new MutableLiveData<>();


    public DashBoardViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<Integer> getIncome() {
        return income;
    }

    public LiveData<Integer> getBalance() {
        return balance;
    }

    public void setIncome(int incomeValue) {
        income.setValue(incomeValue);
    }

    public void setBalance(int balanceValue) {
        balance.setValue(balanceValue);
    }

}
