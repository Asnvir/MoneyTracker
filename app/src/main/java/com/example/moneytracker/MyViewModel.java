package com.example.moneytracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private MutableLiveData<Navigator> navigatorLiveData = new MutableLiveData<>();
    public void setNavigator(Navigator navigator) {
        navigatorLiveData.setValue(navigator);
    }
    public LiveData<Navigator> getNavigator() {
        return navigatorLiveData;
    }
}
