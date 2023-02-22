package com.example.moneytracker.screens.startScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.model.AuthDataService;
import com.example.moneytracker.util.InputValidator;
import com.example.moneytracker.util.SingleLiveEvent;
import com.example.moneytracker.util.ValidationResult;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterViewModel extends ViewModel {

    private final AuthDataService authDataService = new AuthDataService();
    private final SingleLiveEvent<Boolean> registrationSuccess = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> loginLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public RegisterViewModel() {

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void createUser(String fullName, String email, String password, String confirmPassword) {
        ValidationResult validationResult = InputValidator.validateUserCredentials(fullName, email, password, confirmPassword);

        if (!validationResult.isValid()) {
            errorMessage.setValue(validationResult.getErrorMessage());
            return;
        }


        showProgressBar();
        Disposable disposable = authDataService.createUser(fullName, email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    registrationSuccess.setValue(true);
                    hideProgressBar();
                }, throwable -> {
                    errorMessage.setValue("Error creating user: " + throwable.getMessage());
                    registrationSuccess.setValue(false);
                    hideProgressBar();
                });
        compositeDisposable.add(disposable);

    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private void showProgressBar() {
        isLoading.setValue(true);
    }

    private void hideProgressBar() {
        isLoading.setValue(false);
    }

    public LiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }

    public LiveData<Boolean> getGoToLoginSuccess() {
        return loginLiveData;
    }

    public void goToLogin() {
        loginLiveData.setValue(true);
    }

    public void onRegistrationComplete() {
        registrationSuccess.setValue(false);
    }

    public void onGoToLoginComplete() {
        loginLiveData.setValue(false);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
