package com.example.moneytracker.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moneytracker.data.DatabaseHandler;
import com.example.moneytracker.util.InputValidator;
import com.example.moneytracker.util.MySignal;
import com.example.moneytracker.Transaction;
import com.example.moneytracker.interfaces.DownloadCallback;
import com.example.moneytracker.interfaces.UploadCallback;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddTransactionViewModel extends AndroidViewModel{

    private final MutableLiveData<List<String>> categoriesLiveData = new MutableLiveData<>();
    private AddTransactionHolder.AddFragmentNavigation addFragmentNavigation;

    private final UploadCallback uploadTransactionCallback = new UploadCallback() {
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




    public AddTransactionViewModel(@NonNull Application application) {
        super(application);
        downloadCategories();
    }

    private void downloadCategories() {
        DatabaseHandler.getInstance().downloadCategories(downloadCategoriesCallback);
    }

    public void addTransaction(double amount, String note, String category, String type) {
        if (InputValidator.isValidInput(amount, note, category, type)) {
            Transaction transaction = new Transaction()
                    .setTransactionID()
                    .setDate()
                    .setTime()
                    .setAmount(amount)
                    .setCategory(category)
                    .setNote(note)
                    .setType(type);

           DatabaseHandler.getInstance().uploadTransaction(transaction, uploadTransactionCallback);

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


    public void setListener(AddTransactionHolder.AddFragmentNavigation addFragmentNavigation) {
        this.addFragmentNavigation = addFragmentNavigation;
    }
}
