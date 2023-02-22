package com.example.moneytracker.screens.mainScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.model.DatabaseHandler;
import com.example.moneytracker.util.InputValidator;
import com.example.moneytracker.util.SingleLiveEvent;
import com.example.moneytracker.util.ValidationResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddTransactionViewModel extends ViewModel {

    private final DatabaseHandler databaseHandler = new DatabaseHandler();
    private final MutableLiveData<List<String>> categoryLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> isUploadSuccessful = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> isLoading = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    public AddTransactionViewModel() {
        downloadCategories();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void downloadCategories() {
        Disposable disposable = databaseHandler.downloadCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        categoryLiveData::setValue,
                        error -> errorMessage.setValue("Error downloading categories" + error.getMessage())
                );
        compositeDisposable.add(disposable);
    }


    public void initTransaction(double amount, String note, String category, String type) {
        ValidationResult validationResult = InputValidator.validateInput(amount, note, category, type);

        if (!validationResult.isValid()) {
            errorMessage.setValue(validationResult.getErrorMessage());
            return;
        }

        TransactionModel transactionModel = new TransactionModel()
                .setTransactionID(UUID.randomUUID().toString())
                .setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()))
                .setTime(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()))
                .setAmount(amount)
                .setCategory(category)
                .setNote(note)
                .setType(type);

        uploadTransaction(transactionModel);
    }

    private void uploadTransaction(TransactionModel transaction) {
        showProgressBar();

        Disposable disposable = databaseHandler.uploadTransaction(transaction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {

                                isUploadSuccessful.setValue(true);
                                hideProgressBar();

                        }
                        , error -> {
                            isUploadSuccessful.setValue(false);
                            hideProgressBar();
                            errorMessage.setValue("Error uploading transaction" + error.getMessage());
                        });
        compositeDisposable.add(disposable);
    }

    public LiveData<Boolean> getIsUploadTransactionLiveData() {
        return isUploadSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<String>> getCategories() {
        return categoryLiveData;
    }

    public void uploadTransactionComplete() {
        isUploadSuccessful.setValue(false);
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
}
