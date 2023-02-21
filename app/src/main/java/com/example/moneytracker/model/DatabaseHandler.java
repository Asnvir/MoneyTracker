package com.example.moneytracker.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.moneytracker.data.BalanceModel;
import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.data.UserData;
import com.example.moneytracker.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Cancellable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DatabaseHandler {

    private final FirebaseDatabase database;
    private final FirebaseAuth auth;
    private final CompositeDisposable compositeDisposable;

    public DatabaseHandler() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        compositeDisposable = new CompositeDisposable();
    }


    public String getCurrentUserId() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.getUid();
        } else {
            return null;
        }
    }


    public Flowable<UserData> downloadData() {
        Log.d(Constants.DatabaseHandler_TAG, "downloadData: called");

        String userUID = getCurrentUserId();
        if (userUID != null) {
            DatabaseReference dataRef = database.getReference().child(Constants.NODE_USERS).child(userUID);

            return Flowable.create(emitter -> {
                Log.d(Constants.DatabaseHandler_TAG, "downloadData: Flowable.create");
                ValueEventListener valueEventListener = createUserDataValueEventListener(emitter);
                dataRef.addValueEventListener(valueEventListener);
                emitter.setCancellable(createCancelActionUserDataDownloads(dataRef, valueEventListener));
            }, BackpressureStrategy.BUFFER);
        }

        Log.d(Constants.DatabaseHandler_TAG, "downloadData: empty");
        return Flowable.empty();
    }

    @NonNull
    private Cancellable createCancelActionUserDataDownloads(DatabaseReference dataRef, ValueEventListener listener) {
        return () -> {
            Log.d(Constants.DatabaseHandler_TAG, "downloadData: setCancellable");
            dataRef.removeEventListener(listener);
        };
    }

    @NonNull
    private ValueEventListener createUserDataValueEventListener(FlowableEmitter<UserData> emitter) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(Constants.DatabaseHandler_TAG, "downloadData: onDataChange");
                ArrayList<TransactionModel> transactionModels = parseTransactions(dataSnapshot);
                BalanceModel balanceModel = parseBalance(dataSnapshot);
                UserData userData = new UserData(transactionModels, balanceModel);
                emitter.onNext(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(Constants.DatabaseHandler_TAG, "downloadData: onCancelled");
                emitter.onError(new Exception(databaseError.getMessage()));
            }
        };
    }


    public Single<List<String>> downloadCategories() {
        DatabaseReference categoriesRef = database.getReference().child(Constants.NODE_CATEGORIES);

        return Single.<List<String>>create(emitter -> {
                    ValueEventListener valueEventListener = createCategoriesValueEventListener(emitter);
                    categoriesRef.addValueEventListener(valueEventListener);
                    emitter.setCancellable(createCancelActionCategoriesDownloads(categoriesRef, valueEventListener));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private ValueEventListener createCategoriesValueEventListener(SingleEmitter<List<String>> emitter) {

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = getCategoryNames(snapshot);
                emitter.onSuccess(categories);
                Log.d(Constants.DatabaseHandler_TAG, "Categories downloaded: " + categories.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                emitter.onError(error.toException());
                Log.e(Constants.DatabaseHandler_TAG, "Error downloading categories", error.toException());
            }
        };
    }

    private Cancellable createCancelActionCategoriesDownloads(DatabaseReference categoriesRef, ValueEventListener valueEventListener) {
        return () -> {
            Log.d(Constants.DatabaseHandler_TAG, "downloadCategories: setCancellable");
            categoriesRef.removeEventListener(valueEventListener);
        };
    }


    private List<String> getCategoryNames(DataSnapshot snapshot) {
        List<String> categoryNames = new ArrayList<>();
        for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
            String category = categorySnapshot.getValue(String.class);
            categoryNames.add(category);
        }
        return categoryNames;
    }


    private BalanceModel parseBalance(DataSnapshot dataSnapshot) {
        DataSnapshot snapshotBalance = dataSnapshot.child(Constants.NODE_USERS_BALANCE);

        if (snapshotBalance.exists()) {
            return snapshotBalance.getValue(BalanceModel.class);
        }
        return new BalanceModel(0.0, 0.0, 0.0);
    }

    private ArrayList<TransactionModel> parseTransactions(DataSnapshot dataSnapshot) {
        DataSnapshot snapshotTransactions = dataSnapshot.child(Constants.NODE_TRANSACTIONS);
        ArrayList<TransactionModel> transactionModels = new ArrayList<>();


        if (snapshotTransactions.exists()) {
            for (DataSnapshot snapshot : snapshotTransactions.getChildren()) {
                TransactionModel transaction = snapshot.getValue(TransactionModel.class);
                transactionModels.add(transaction);
            }
        } else {
            return transactionModels;
        }
        return transactionModels;
    }


    public Completable uploadTransaction(TransactionModel transaction) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return Completable.error(new Throwable("User not logged in"));
        }
        String userUID = currentUser.getUid();
        DatabaseReference transactionsRef = getTransactionsRefForUserAndTransaction(userUID, transaction);


        return Completable.create(emitter -> transactionsRef
                .setValue(transaction)
                .addOnSuccessListener(aVoid -> {
                    Log.d(Constants.DatabaseHandler_TAG, "Transaction uploaded: " + transaction);
                    emitter.onComplete();
                })
                .addOnFailureListener(emitter::onError));
    }

    private DatabaseReference getTransactionsRefForUserAndTransaction(String userUID, TransactionModel transaction) {
        return database.getReference()
                .child(Constants.NODE_USERS)
                .child(userUID)
                .child(Constants.NODE_TRANSACTIONS)
                .child(transaction.getTransactionID());
    }

    public Completable deleteTransaction(String transactionID) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return Completable.error(new Throwable("User not logged in"));
        }
        String userUID = currentUser.getUid();
        DatabaseReference transactionsRef = getTransactionsRefForUser(userUID);

        return Completable.create(emitter -> {
            transactionsRef.child(transactionID).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    private DatabaseReference getTransactionsRefForUser(String userUID) {
        return database.getReference().child(Constants.NODE_USERS).child(userUID).child(Constants.NODE_TRANSACTIONS);
    }


}



