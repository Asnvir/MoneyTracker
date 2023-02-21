package com.example.moneytracker.startScreen;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.model.AuthDataService;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.MyAuthResult;
import com.example.moneytracker.util.MySignal;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class LoginViewModel extends ViewModel {

    private final AuthDataService authDataService;
    private final MutableLiveData<MyAuthResult> authResultLiveData;
    private final MutableLiveData<Boolean> isLoading;


    private final CompositeDisposable compositeDisposable;

    public LoginViewModel() {
        authDataService = new AuthDataService();
        authResultLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<MyAuthResult> getAuthResultLiveData() {
        return authResultLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void signIn(String email, String password) {
        showProgressBar();
        Log.d(Constants.SignInViewModel_TAG, "signIn called with email: " + email);
        Disposable disposable = authDataService.signIn(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(Constants.SignInViewModel_TAG, "signIn successful");
                    authResultLiveData.postValue(new MyAuthResult(true, null));
                    hideProgressBar();
                }, throwable -> {
                    Log.e(Constants.SignInViewModel_TAG, "signIn failed: " + throwable.getMessage());
                    authResultLiveData.postValue(new MyAuthResult(false, throwable.getMessage()));
                    hideProgressBar();
                });

        compositeDisposable.add(disposable);

        Log.d(Constants.SignInViewModel_TAG, "Added disposable to compositeDisposable: " + disposable.toString());
        Log.d(Constants.SignInViewModel_TAG, "Size of compositeDisposable: " + compositeDisposable.size());
    }


    public void signInWithGoogle(String idToken) {
        showProgressBar();
        Disposable disposable = authDataService.signInWithGoogle(idToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(firebaseUser -> {
                    // Sign-in with Google succeeded, do something with the user
                    authResultLiveData.postValue(new MyAuthResult(true, null));
                    Log.d(Constants.SignInViewModel_TAG, "Google sign in OK"); // TODO: 19.02.2023
                    MySignal.getInstance().toast("Google sign in OK");// TODO: 19.02.2023
                    hideProgressBar();
                }, throwable -> {
                    // Sign-in with Google failed, handle the error
                    MySignal.getInstance().toast("Google sign in failed");// TODO: 19.02.2023
                    Log.e(Constants.SignInViewModel_TAG, "Google sign in failed", throwable);// TODO: 19.02.2023
                    authResultLiveData.postValue(new MyAuthResult(false, throwable.getMessage()));
                    hideProgressBar();
                });
        compositeDisposable.add(disposable);
    }

    public void sendPasswordResetEmail(String email) {
        Disposable disposable = authDataService.sendPasswordResetEmail(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    MySignal.getInstance().toast("Password reset email sent to " + email);
                }, throwable -> {
                    MySignal.getInstance().toast("Failed to send password reset email: " + throwable.getMessage());
                });
        compositeDisposable.add(disposable);
    }

    private void showProgressBar() {
        isLoading.setValue(true);
    }

    private void hideProgressBar() {
        isLoading.setValue(false);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        authDataService.disposeAll();
    }


    public void onAuthComplete() {
        authResultLiveData.postValue(new MyAuthResult(false, null));
    }
}


