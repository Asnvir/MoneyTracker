package com.example.moneytracker.screens.mainScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moneytracker.data.BalanceModel;
import com.example.moneytracker.data.UserInfoModel;
import com.example.moneytracker.model.DatabaseHandler;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.SingleLiveEvent;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AccountViewModel extends ViewModel {

    private final DatabaseHandler databaseHandler = new DatabaseHandler();
    private final MutableLiveData<String> nameLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> emailLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> balanceLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> incomeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> expenseLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> isLogOut = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> isDelete = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    public AccountViewModel() {
        isLogOut.setValue(false);
        isDelete.setValue(false);
        downloadItemsValues();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    private void downloadItemsValues() {
        Disposable disposable = databaseHandler.downloadUserInformation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userFullInfo -> {
                            UserInfoModel userInfo = userFullInfo.getUserInfo();
                            BalanceModel balance = userFullInfo.getBalance();
                            setLiveDataValues(userInfo, balance);
                        },
                        error -> {
                            errorMessage.setValue("Error downloading user's full information: " + error.getMessage());
                        }
                );
        compositeDisposable.add(disposable);
    }

    private void setLiveDataValues(UserInfoModel userInfo, BalanceModel balance) {
        nameLiveData.setValue(userInfo.getName());
        emailLiveData.setValue(userInfo.getEmail());
        updateAmounts(balance.getBalance(),balance.getIncome(),balance.getExpense());
    }

    private void updateAmounts(double balance, double income, double expense) {
        String formattedIncomeValue = formatAmount(income, Constants.DASHBOARD_INCOME, "+");
        String formattedExpenseValue = formatAmount(expense, Constants.DASHBOARD_EXPENSE, "-");
        String formattedBalanceValue = formatBalance(balance);

        balanceLiveData.setValue(formattedBalanceValue);
        incomeLiveData.setValue(formattedIncomeValue);
        expenseLiveData.setValue(formattedExpenseValue);
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




    public void logout() {
        FirebaseAuth.getInstance().signOut();
        isLogOut.setValue(true);
    }

    public void deleteAccount() {
        Disposable disposable = databaseHandler.deleteAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> isDelete.setValue(true),
                        error -> errorMessage.setValue("Error deleting account: " + error.getMessage())
                );
        compositeDisposable.add(disposable);
    }


    public LiveData<String> getNameLiveData() {
        return nameLiveData;
    }

    public LiveData<String> getEmailLiveData() {
        return emailLiveData;
    }

    public LiveData<String> getBalanceLiveData() {
        return balanceLiveData;
    }

    public LiveData<String> getIncomeLiveData() {
        return incomeLiveData;
    }

    public LiveData<String> getExpenseLiveData() {
        return expenseLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public SingleLiveEvent<Boolean> getIsLogOut() {
        return isLogOut;
    }

    public void completeLogOut(){
        isLogOut.setValue(false);
    }

    public SingleLiveEvent<Boolean> getIsDelete() {
        return isDelete;
    }

    public void completeDelete() {
        isDelete.setValue(false);
    }
}


