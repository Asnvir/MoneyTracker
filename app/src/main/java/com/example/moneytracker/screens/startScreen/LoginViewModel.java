package com.example.moneytracker.screens.startScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.model.AuthDataService;
import com.example.moneytracker.util.SingleLiveEvent;
import com.example.moneytracker.util.ValidationResult;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class LoginViewModel extends ViewModel {

    private final AuthDataService authDataService = new AuthDataService();
    private final MutableLiveData<ValidationResult> authResultLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> isAuth = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> isLoading = new SingleLiveEvent<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();

    public LoginViewModel() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        authDataService.disposeAll();
    }

    public void signIn(String email, String password) {
        showProgressBar();

        Disposable disposable = authDataService.signIn(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    isAuth.setValue(true);
                    hideProgressBar();
                }, throwable -> {
                    errorMessage.setValue("SignIn failed: " + throwable.getMessage());
                    isAuth.setValue(false);
                    authResultLiveData.postValue(new ValidationResult(false, throwable.getMessage()));
                    hideProgressBar();
                });

        compositeDisposable.add(disposable);
    }


    public void signInWithGoogle(String idToken) {
        showProgressBar();

        Disposable disposable = authDataService.signInWithGoogle(idToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(firebaseUser -> {
                    isAuth.setValue(true);
                    hideProgressBar();
                }, throwable -> {
                    errorMessage.setValue("Google sign in failed" + throwable.getMessage());
                    isAuth.setValue(false);
                    hideProgressBar();
                });
        compositeDisposable.add(disposable);
    }

    public void sendPasswordResetEmail(String email) {
        Disposable disposable = authDataService.sendPasswordResetEmail(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    errorMessage.setValue("Password reset email sent to " + email);
                }, throwable -> {
                    errorMessage.setValue("Failed to send password reset email: " + throwable.getMessage());
                });
        compositeDisposable.add(disposable);
    }

    private void showProgressBar() {
        isLoading.setValue(true);
    }

    private void hideProgressBar() {
        isLoading.setValue(false);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public SingleLiveEvent<Boolean> getIsAuth() {
        return isAuth;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}


