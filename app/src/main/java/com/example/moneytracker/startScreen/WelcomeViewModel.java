package com.example.moneytracker.startScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WelcomeViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loginLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerLiveData = new MutableLiveData<>();

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

