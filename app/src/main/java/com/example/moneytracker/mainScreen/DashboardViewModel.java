package com.example.moneytracker.mainScreen;

import static com.example.moneytracker.util.SortCriteria.AMOUNT_ASCENDING;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.data.BalanceModel;
import com.example.moneytracker.model.DatabaseHandler;
import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.data.UserData;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.MySignal;
import com.example.moneytracker.util.SortCriteria;
import com.example.moneytracker.util.TransactionComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DashboardViewModel extends ViewModel {

    public void onEditTransactionComplete() {
        isEditTransaction.setValue(false);
    }

    public interface TransactionActionListener {
        void onTransactionDelete(TransactionModel transactionModel);
    }

    public interface UserDataUpdatedCallback {
        void onTransactionsUpdated(UserData userData);
    }

    private SortCriteria sortCriteria;
    private final MutableLiveData<String> income;
    private final MutableLiveData<String> balance;
    private final MutableLiveData<String> expense;
    private final MutableLiveData<List<TransactionModel>> transactions ;
    private final DatabaseHandler databaseHandler;
    private final CompositeDisposable compositeDisposable;
    private final UserDataUpdatedCallback transactionsListener = this::updateUserData;
    private final MutableLiveData<Boolean> isEditTransaction = new MutableLiveData<>();
    private TransactionModel transactionToEdit = null;
    private final TransactionActionListener transactionActionListener = new TransactionActionListener() {
        @Override
        public void onTransactionDelete(TransactionModel transactionModel) {
            transactionToEdit = transactionModel;
            isEditTransaction.setValue(true);
        }
    };

    public TransactionActionListener getTransactionActionListener() {
        return transactionActionListener;
    }


    public DashboardViewModel() {
        databaseHandler = new DatabaseHandler();
        income = new MutableLiveData<>();
        balance = new MutableLiveData<>();
        expense = new MutableLiveData<>();
        transactions=  new MutableLiveData<>();
        sortCriteria = SortCriteria.DATE_ASCENDING;
        compositeDisposable = new CompositeDisposable();
        downloadData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void downloadData() {
        Log.d(Constants.DashboardViewModel_TAG, "Downloading data...");
        Disposable disposable = databaseHandler.downloadData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userData -> {
                    Log.d(Constants.DashboardViewModel_TAG, "Data downloaded successfully.");
                    updateUserData(userData);
                }, throwable -> {
                    Log.e(Constants.DashboardViewModel_TAG, "Error downloading data: " + throwable.getMessage());
                });
        compositeDisposable.add(disposable);
    }


    public void setSortingCriteria(SortCriteria sortCriteria) {
        Log.d(Constants.DashboardViewModel_TAG, "Setting sorting criteria to: " + sortCriteria);
        this.sortCriteria = sortCriteria;
        List<TransactionModel> sortedTransactions = sortTransactions(transactions.getValue());
        transactions.postValue(sortedTransactions);
    }

    private List<TransactionModel> sortTransactions(List<TransactionModel> transactions) {
        Log.d(Constants.DashboardViewModel_TAG, "Sorting transactions by: " + sortCriteria);
        List<TransactionModel> sortedTransactions = new ArrayList<>(transactions);
        switch (sortCriteria) {
            case DATE_DESCENDING:
                sortedTransactions.sort(new TransactionComparator(SortCriteria.DATE_DESCENDING));
                break;
            case AMOUNT_ASCENDING:
                sortedTransactions.sort(new TransactionComparator(SortCriteria.AMOUNT_ASCENDING));
                break;
            case AMOUNT_DESCENDING:
                sortedTransactions.sort(new TransactionComparator(SortCriteria.AMOUNT_DESCENDING));
                break;
            case CATEGORY_ASCENDING:
                sortedTransactions.sort(new TransactionComparator(SortCriteria.CATEGORY_ASCENDING));
                break;
            case CATEGORY_DESCENDING:
                sortedTransactions.sort(new TransactionComparator(SortCriteria.CATEGORY_DESCENDING));
                break;
            case TYPE_ASCENDING:
                sortedTransactions.sort(new TransactionComparator(SortCriteria.TYPE_ASCENDING));
                break;
            case TYPE_DESCENDING:
                sortedTransactions.sort(new TransactionComparator(SortCriteria.TYPE_DESCENDING));
                break;
            default:
                sortedTransactions.sort(new TransactionComparator(SortCriteria.DATE_ASCENDING));
                break;
        }
        return sortedTransactions;
    }



    private void updateUserData(UserData userData) {
        updateTransactions(userData.getTransactions());
        updateAmounts(userData.getAmount());
    }

    private void updateTransactions(List<TransactionModel> transactions) {
        List<TransactionModel> sortedTransactions = sortTransactions(transactions);
        this.transactions.setValue(sortedTransactions);
    }

    private void updateAmounts(BalanceModel amount) {
        String formattedIncomeValue = formatAmount(amount.getIncome(), Constants.DASHBOARD_INCOME, "+");
        String formattedExpenseValue = formatAmount(amount.getExpense(), Constants.DASHBOARD_EXPENSE, "-");
        String formattedBalanceValue = formatBalance(amount.getBalance());

        this.income.postValue(formattedIncomeValue);
        this.expense.postValue(formattedExpenseValue);
        this.balance.postValue(formattedBalanceValue);
    }

    private String formatAmount(double amount, String label, String symbol) {
        String formattedAmount = String.format(Locale.getDefault(), "%.2f", amount);
        if (amount > 0) {
            formattedAmount = symbol + formattedAmount;
        }
        return label + ": " + formattedAmount;
    }

    private String formatBalance(double balance) {
        if (balance == 0) {
            return String.format(Locale.getDefault(), "%s: %.2f", Constants.DASHBOARD_BALANCE, balance);
        } else {
            String symbol = balance > 0 ? "+" : "-";
            return String.format(Locale.getDefault(), "%s: %s%.2f", Constants.DASHBOARD_BALANCE, symbol, Math.abs(balance));
        }
    }

    private void deleteTransaction(TransactionModel transactionModel) {
        MySignal.getInstance().toast("NEED TO IMPLEMENT");
    }

    public LiveData<String> getIncomeLiveData() {
        return income;
    }

    public LiveData<String> getBalanceLiveData() {
        return balance;
    }

    public LiveData<String> getExpenseLiveData() {
        return expense;
    }

    public LiveData<List<TransactionModel>> getTransactionsLiveData() {
        return transactions;
    }

    public LiveData<Boolean> getIsEditTransaction() {
        return isEditTransaction;
    }

    public TransactionModel getTransactionToEdit() {
        return transactionToEdit;
    }
}
