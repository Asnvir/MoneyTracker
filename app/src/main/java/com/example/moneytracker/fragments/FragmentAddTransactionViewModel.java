package com.example.moneytracker.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moneytracker.DatabaseHandler;
import com.example.moneytracker.InputValidator;
import com.example.moneytracker.MySignal;
import com.example.moneytracker.TransactionModel;
import com.example.moneytracker.interfaces.DownloadCallback;
import com.example.moneytracker.interfaces.UploadCallback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentAddTransactionViewModel extends AndroidViewModel{

    private final MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();
    private FragmentAddTransactionViewHolder.AddFragmentNavigation addFragmentNavigation;

    private final UploadCallback uploadCallback = new UploadCallback() {
        @Override
        public void onUploadSuccess() {
            MySignal.getInstance().toast("ADD OK");
            addFragmentNavigation.openDashBoard();
        }

        @Override
        public void onUploadError(String error) {
            MySignal.getInstance().toast("ADD BAD");
        }
    };

    private final DownloadCallback downloadCategoriesCallback = new DownloadCallback() {
        @Override
        public void onDownloadSuccess(DataSnapshot dataSnapshot) {
            handleCategories(dataSnapshot);
        }

        @Override
        public void onDownloadError(String error) {

        }
    };




    public FragmentAddTransactionViewModel(@NonNull Application application) {
        super(application);
        downloadCategories();
    }

    private void downloadCategories() {
        DatabaseHandler.getInstance().downloadCategories(downloadCategoriesCallback);
    }

    public void addTransaction(double amount, String note, String category, Boolean isExpense, Boolean isIncome) {
        if (InputValidator.isValidInput(amount, note, category, isExpense, isIncome)) {
            TransactionModel transaction = new TransactionModel()
                    .setTransactionID()
                    .setDate()
                    .setTime()
                    .setAmount(amount)
                    .setCategory(category)
                    .setNote(note)
                    .setType(isExpense, isIncome);

           DatabaseHandler.getInstance().uploadTransaction(transaction,uploadCallback);

        }
    }

    private void handleCategories(DataSnapshot dataSnapshot) {
        List<String> categoryNames = new ArrayList<>();
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            categoryNames.add((String) child.getValue());
        }
        categoriesLiveData.setValue(categoryNames);
    }

    public MutableLiveData<List<String>> getCategories(){
        return categoriesLiveData;
    }


    public void setListener(FragmentAddTransactionViewHolder.AddFragmentNavigation addFragmentNavigation) {
        this.addFragmentNavigation = addFragmentNavigation;
    }
}
