package com.example.moneytracker.screens.mainScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditTransactionViewModel extends ViewModel {

    private final TransactionModel oldTransaction;
    private final DatabaseHandler databaseHandler;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<Double> amountLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> categoryLiveData = new MutableLiveData<>();
    private final MediatorLiveData<String> selectedCategoryLiveData = new MediatorLiveData<>();
    private final MutableLiveData<String> noteLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> typeLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> isUploadSuccessful = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> isLoading = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> isDeleteSuccessful = new SingleLiveEvent<>();


    public EditTransactionViewModel(TransactionModel oldTransaction) {
        this.oldTransaction = oldTransaction;
        databaseHandler = new DatabaseHandler();
        isUploadSuccessful.setValue(false);
        initTransactionElements();
    }

    private void initTransactionElements() {
        if (oldTransaction != null) {
            downloadCategories();
            amountLiveData.setValue(oldTransaction.getAmount());
            categoryLiveData.setValue(oldTransaction.getCategory());
            noteLiveData.setValue(oldTransaction.getNote());
            typeLiveData.setValue(oldTransaction.getType());
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
                            errorMessage.setValue("Error downloading categories" + error.getMessage());
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

        TransactionModel newTransaction = new TransactionModel()
                .setTransactionID(oldTransaction.getTransactionID())
                .setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()))
                .setTime(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()))
                .setAmount(amount)
                .setCategory(category)
                .setNote(note)
                .setType(type);

        if (newTransaction.equalsIgnoreDateTime(oldTransaction)) {
            isUploadSuccessful.setValue(false);
            errorMessage.setValue("Transaction not changed. No update required.");
            return;
        }
        updateTransaction(oldTransaction,newTransaction);
    }

    private void updateTransaction(TransactionModel oldTransaction,TransactionModel newTransaction) {
        showProgressBar();
        Disposable disposable = databaseHandler.updateTransaction(oldTransaction, newTransaction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    isUploadSuccessful.setValue(true);
                    hideProgressBar();
                }, error -> {
                    isUploadSuccessful.setValue(false);
                    errorMessage.setValue("Error updating transaction: " + error.getMessage());
                    hideProgressBar();
                });
        compositeDisposable.add(disposable);
    }

    public void deleteTransaction() {
        showProgressBar();
        Disposable disposable = databaseHandler.deleteTransaction(oldTransaction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    isDeleteSuccessful.setValue(true);
                    hideProgressBar();
                }, error -> {
                    isDeleteSuccessful.setValue(false);
                    errorMessage.setValue("Error deleting transaction: " + error.getMessage());
                    hideProgressBar();
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

    public void deleteComplete() {
        isDeleteSuccessful.setValue(false);
    }

    public void uploadComplete() {
        isUploadSuccessful.setValue(false);
    }
}
