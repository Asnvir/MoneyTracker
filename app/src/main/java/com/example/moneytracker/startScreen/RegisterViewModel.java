package com.example.moneytracker.startScreen;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.model.AuthDataService;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterViewModel extends ViewModel {

    private final AuthDataService authDataService;
    private final MutableLiveData<String> fullNameError;
    private final MutableLiveData<String> emailError;
    private final MutableLiveData<String> passwordError;
    private final MutableLiveData<String> confirmPasswordError;
    private final MutableLiveData<Boolean> registrationSuccess;
    private final MutableLiveData<Boolean> loginLiveData;
    private final MutableLiveData<Boolean> isLoading;
    private final CompositeDisposable compositeDisposable;

    public RegisterViewModel() {
        authDataService = new AuthDataService();
        fullNameError = new MutableLiveData<>();
        emailError = new MutableLiveData<>();
        passwordError = new MutableLiveData<>();
        confirmPasswordError = new MutableLiveData<>();
        registrationSuccess = new MutableLiveData<>();
        loginLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void createUser(String fullName, String email, String password, String confirmPassword) {
        boolean isValid = validateUserData(fullName, email, password, confirmPassword);
        if (isValid) {
            showProgressBar();
            Disposable disposable = authDataService.createUser(fullName, email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        registrationSuccess.setValue(true);
                        hideProgressBar();
                    }, throwable -> {
                        // Handle error
                        Log.e("SignUpViewModel", "Error creating user: " + throwable.getMessage());
                        hideProgressBar();
                    });
            compositeDisposable.add(disposable);
        }
    }

    private boolean validateUserData(String fullName, String email, String password, String confirmPassword) {
        boolean isValid = true;

        if (fullName.trim().isEmpty()) {
            fullNameError.setValue("Full name cannot be empty");
            isValid = false;
        }
        if (email.trim().isEmpty()) {
            emailError.setValue("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.setValue("Please enter a valid email address");
            isValid = false;
        }
        if (password.trim().isEmpty()) {
            passwordError.setValue("Password cannot be empty");
            isValid = false;
        } else if (password.length() < 6) {
            passwordError.setValue("Password must be at least 6 characters");
            isValid = false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordError.setValue("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
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

    private void showProgressBar() {
        isLoading.setValue(true);
    }

    private void hideProgressBar() {
        isLoading.setValue(false);
    }

    public void onRegistrationComplete() {
        registrationSuccess.setValue(false);
    }

    public void onGoToLoginComplete() {
        loginLiveData.setValue(false);
    }
}
