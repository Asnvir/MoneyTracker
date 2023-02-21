package com.example.moneytracker.mainScreen;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.model.DatabaseHandler;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.InputValidator;
import com.example.moneytracker.util.MySignal;
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


    private final DatabaseHandler databaseHandler;
    private final MutableLiveData<List<String>> categoryLiveData;
    private final SingleLiveEvent<Boolean> isUploadSuccessful = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();
    private final CompositeDisposable compositeDisposable;


    public AddTransactionViewModel() {
        databaseHandler = new DatabaseHandler();
        categoryLiveData = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
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
                        categories -> {
                            Log.d(Constants.AddTransactionViewModel_TAG, "Categories downloaded: " + categories.size());
                            categoryLiveData.setValue(categories);
                        },
                        error -> {
                            Log.e(Constants.AddTransactionViewModel_TAG, "Error downloading categories", error);
                            MySignal.getInstance().toast("Error downloading categories" + error);
                        }
                );
        compositeDisposable.add(disposable);
    }


    public LiveData<List<String>> getCategories() {
        return categoryLiveData;
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

        Log.d(Constants.AddTransactionViewModel_TAG, "Uploading transaction...");
        uploadTransaction(transactionModel);

    }

    private void uploadTransaction(TransactionModel transaction) {
        Disposable disposable = databaseHandler.uploadTransaction(transaction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(Constants.AddTransactionViewModel_TAG, "Transaction uploaded: " + transaction);
                    isUploadSuccessful.setValue(true);
                }, error -> {
                    Log.e(Constants.AddTransactionViewModel_TAG, "Error uploading transaction", error);
                    isUploadSuccessful.setValue(false);
                });
        compositeDisposable.add(disposable);
    }


    public LiveData<Boolean> getIsUploadTransactionLiveData() {
        return isUploadSuccessful;
    }

}
