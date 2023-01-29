package com.example.moneytracker.data;

import androidx.annotation.NonNull;

import com.example.moneytracker.Transaction;
import com.example.moneytracker.interfaces.DownloadCallback;
import com.example.moneytracker.interfaces.UploadCallback;
import com.example.moneytracker.util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DatabaseHandler {
    private static DatabaseHandler instance;
    private DatabaseReference dbRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();



    private DatabaseHandler() {
    }

    public static void init() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
    }

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseHandler must be initialized first by calling init() method.");
        }
        return instance;
    }

    public void uploadTransaction(Transaction transaction, UploadCallback callback) {
        HashMap<String, Object> transactionToDB = convertTransaction(transaction);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String currentUserUID = user.getUid();
            dbRef = database.getReference()
                    .child(Utils.NODE_USERS)
                    .child(currentUserUID)
                    .child(Utils.NODE_TRANSACTIONS)
                    .child(transaction.getTransactionID());

             dbRef.setValue(transactionToDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            updateBalance(callback,transaction);

                        }else{
                            Exception exception = task.getException();
                            if (exception != null) {
                                String errorMessage = exception.getMessage();
                                if (callback != null) {
                                    callback.onUploadError(errorMessage);
                                }
                            }
                        }
                 }
             });
        }

    }

    private void updateBalance(UploadCallback callback, Transaction transaction){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String currentUserUID = user.getUid();
            dbRef = database.getReference()
                    .child(Utils.NODE_USERS)
                    .child(currentUserUID)
                    .child(Utils.NODE_AMOUNT);
            retrieveAmountFromDB(callback, transaction);
        }
    }

    private void retrieveAmountFromDB(UploadCallback callback,  Transaction transaction) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double balance = getDoubleValue(dataSnapshot, Utils.NODE_BALANCE);
                double expense = getDoubleValue(dataSnapshot, Utils.NODE_EXPENSE);
                double income = getDoubleValue(dataSnapshot, Utils.NODE_INCOME);
                updateAmountValues(callback, transaction, balance, expense, income);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) {
                    callback.onUploadError(error.getMessage());
                }
            }
        });
    }

    private double getDoubleValue(DataSnapshot dataSnapshot, String fieldName) {
        Double value = dataSnapshot.child(fieldName).getValue(Double.class);
        if(value == null){
            return 0.0;
        }
        return value;
    }





    private void updateAmountValues(UploadCallback callback, Transaction transaction, double balance, double expense, double income) {
        if (transaction.getType().equals(Utils.EXPENSE)){
            balance = balance - transaction.getAmount();
            expense = expense + transaction.getAmount();
        }else if(transaction.getType().equals(Utils.INCOME)){
            balance = balance + transaction.getAmount();
            income = income + transaction.getAmount();
        }
        Map<String, Object> amountValues = new HashMap<>();
        amountValues.put(Utils.NODE_BALANCE, balance);
        amountValues.put(Utils.NODE_EXPENSE, expense);
        amountValues.put(Utils.NODE_INCOME, income);
        updateAmountInDB(callback, amountValues);
    }

    private void updateAmountInDB(UploadCallback callback, Map<String, Object> amountValues) {
        dbRef.updateChildren(amountValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (callback != null) {
                        callback.onUploadSuccess();
                    }
                }else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        String errorMessage = exception.getMessage();
                        if (callback != null) {
                            callback.onUploadError(errorMessage);
                        }
                    }
                }
            }
        });
    }

    private HashMap<String, Object> convertTransaction(Transaction transaction) {
        HashMap<String, Object> transactionToDB = new HashMap<>();
        transactionToDB.put(Utils.TRANSACTIONID, transaction.getTransactionID());
        transactionToDB.put(Utils.DATE, transaction.getDate());
        transactionToDB.put(Utils.TIME, transaction.getTime());
        transactionToDB.put(Utils.AMOUNT, transaction.getAmount());
        transactionToDB.put(Utils.CATEGORY, transaction.getCategory());
        transactionToDB.put(Utils.NOTE, transaction.getNote());
        transactionToDB.put(Utils.TYPE, transaction.getType());
        return transactionToDB;
    }



    public void downloadTransaction(DownloadCallback callback) {
        String currentUserUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        dbRef = database.getReference().child(Utils.NODE_USERS).child(currentUserUID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (callback != null){
                    callback.onDownloadSuccess(dataSnapshot);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null){
                    callback.onDownloadError(error.getMessage());
                }

            }
        });
    }


    public void downloadCategories(DownloadCallback callback){
        dbRef = database.getReference().child(Utils.NODE_CATEGORIES);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (callback != null){
                    callback.onDownloadSuccess(dataSnapshot);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null){
                    callback.onDownloadError(error.getMessage());
                }

            }
        });
    }

    public void downloadAmounts(DownloadCallback callback) {
        String currentUserUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        dbRef = database.getReference().child(Utils.NODE_USERS).child(currentUserUID).child(Utils.NODE_AMOUNT);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (callback != null){
                    callback.onDownloadSuccess(dataSnapshot);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null){
                    callback.onDownloadError(error.getMessage());
                }

            }
        });
    }



    public Task<Void> uploadData(Map<String, Object> data, String child) {
        dbRef = database.getReference().child(child);
        return dbRef.setValue(data);
    }


    public void deleteData(String child) {
        dbRef = database.getReference().child(child);
        dbRef.removeValue();
    }



}

