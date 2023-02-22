package com.example.moneytracker.screens.startScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.util.SingleLiveEvent;

public class WelcomeViewModel extends ViewModel {
    private final SingleLiveEvent<Boolean> loginLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> registerLiveData = new SingleLiveEvent<>();

    public LiveData<Boolean> getLoginLiveData() {
        return loginLiveData;
    }

    public LiveData<Boolean> getRegisterLiveData() {
        return registerLiveData;
    }

    public void goToLogin() {
        if (loginLiveData.getValue() == null || !loginLiveData.getValue()) {
            loginLiveData.setValue(true);
        }
    }

    public void goToRegister() {
        if (registerLiveData.getValue() == null || !registerLiveData.getValue()) {
            registerLiveData.setValue(true);
        }
    }

    public void onLoginNavigationComplete() {
        loginLiveData.setValue(false);
    }

    public void onRegisterNavigationComplete() {
        registerLiveData.setValue(false);
    }
}

