package com.example.moneytracker.mainScreen;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.model.DatabaseHandler;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.InputValidator;
import com.example.moneytracker.util.SingleLiveEvent;
import com.example.moneytracker.util.ValidationResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditTransactionViewModel extends ViewModel {

    private final TransactionModel transactionModel;
    private final DatabaseHandler databaseHandler;
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<Double> amountLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> categoryLiveData = new MutableLiveData<>();
    private final MediatorLiveData<String> selectedCategoryLiveData = new MediatorLiveData<>();
    private final MutableLiveData<String> noteLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> typeLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> isUploadSuccessful = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> isDeleteSuccessful =new SingleLiveEvent<>();


    public EditTransactionViewModel(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
        databaseHandler = new DatabaseHandler();
        compositeDisposable = new CompositeDisposable();
        initTransactionElements();
        isUploadSuccessful.setValue(false);
    }


    private void initTransactionElements() {
        if (transactionModel != null) {
            downloadCategories();
            amountLiveData.setValue(transactionModel.getAmount());
            categoryLiveData.setValue(transactionModel.getCategory());
            noteLiveData.setValue(transactionModel.getNote());
            typeLiveData.setValue(transactionModel.getType());
        }
    }

    private void downloadCategories() {
        Disposable disposable = databaseHandler.downloadCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        categoriesLiveData::setValue,
                        error -> {
                            isUploadSuccessful.setValue(false);
                            errorMessage.setValue("Error downloading categories" + error);
                        }
                );
        compositeDisposable.add(disposable);
    }

    public void initTransaction(double amount, String note, String category, String type) {

        ValidationResult validationResult = InputValidator.validateInput(amount, note, category, type);

        if (!validationResult.isValid()) {
            errorMessage.setValue(validationResult.getErrorMessage());
            return;
        }

        TransactionModel currentTransaction = new TransactionModel()
                .setTransactionID(transactionModel.getTransactionID())
                .setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()))
                .setTime(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()))
                .setAmount(amount)
                .setCategory(category)
                .setNote(note)
                .setType(type);

        if (currentTransaction.equalsIgnoreDateTime(transactionModel)) {
            isUploadSuccessful.setValue(false);
            errorMessage.setValue("Transaction not changed. No update required.");
            return;
        }
        uploadTransaction(currentTransaction);
    }

    private void uploadTransaction(TransactionModel transaction) {
        Disposable disposable = databaseHandler.uploadTransaction(transaction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    isUploadSuccessful.setValue(true);
                }, error -> {
                    isUploadSuccessful.setValue(false);
                    errorMessage.setValue(error.getMessage());
                });
        compositeDisposable.add(disposable);
    }

    public void deleteTransaction() {
        Disposable disposable = databaseHandler.deleteTransaction(transactionModel.getTransactionID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    isDeleteSuccessful.setValue(true);
                }, error -> {
                    isDeleteSuccessful.setValue(false);
                    errorMessage.setValue(error.getMessage());
                });
        compositeDisposable.add(disposable);
    }



    public LiveData<List<String>> getCategories() {
        return categoriesLiveData;
    }

    public MediatorLiveData<String> getSelectedCategory() {
        return selectedCategoryLiveData;
    }

    public LiveData<Double> getAmount() {
        return amountLiveData;
    }

    public LiveData<String> getCategory() {
        return categoryLiveData;
    }

    public LiveData<String> getNote() {
        return noteLiveData;
    }

    public LiveData<String> getType() {
        return typeLiveData;
    }

    public LiveData<Boolean> getIsUploadSuccessful() {
        return isUploadSuccessful;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public SingleLiveEvent<Boolean> getIsDeleteSuccessful() {
        return isDeleteSuccessful;
    }
}
