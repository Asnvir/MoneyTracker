package com.example.moneytracker.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moneytracker.data.BalanceModel;
import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.data.UserData;
import com.example.moneytracker.data.UserFullInfo;
import com.example.moneytracker.data.UserInfoModel;
import com.example.moneytracker.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.disposables.DisposableContainer;
import io.reactivex.rxjava3.functions.Cancellable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DatabaseHandler {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public DatabaseHandler() {
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

        String userUID = getCurrentUserId();
        if (userUID != null) {
            DatabaseReference dataRef = database.getReference().child(Constants.NODE_USERS).child(userUID);

            return Flowable.create(emitter -> {
                ValueEventListener valueEventListener = createUserDataValueEventListener(emitter);
                dataRef.addValueEventListener(valueEventListener);
                emitter.setCancellable(createCancelActionUserDataDownloads(dataRef, valueEventListener));
            }, BackpressureStrategy.BUFFER);
        }

        return Flowable.empty();
    }

    public Single<UserFullInfo> downloadUserInformation() {
        String userUID = getCurrentUserId();
        if (userUID != null) {
            DatabaseReference dataRef = database.getReference().child(Constants.NODE_USERS).child(userUID);

            return Single.create(emitter -> {
                ValueEventListener valueEventListener = createUserDataValueEventListener(emitter);
                dataRef.addValueEventListener(valueEventListener);
                emitter.setCancellable(() -> dataRef.removeEventListener(valueEventListener));
            });
        }

        return Single.error(new Throwable("User UID is null"));
    }

    private ValueEventListener createUserDataValueEventListener(SingleEmitter<UserFullInfo> emitter) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInfoModel userInfoModel = parseUserInfo(snapshot.child(Constants.NODE_USERS_INFO));
                BalanceModel balanceModel = parseBalance(snapshot.child(Constants.NODE_USERS_BALANCE));
                UserFullInfo userFullInfo = new UserFullInfo(userInfoModel, balanceModel);
                emitter.onSuccess(userFullInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                emitter.onError(error.toException());
            }
        };
    }

    private UserInfoModel parseUserInfo(DataSnapshot snapshot) {
        if (snapshot.exists()) {
            return snapshot.getValue(UserInfoModel.class);
        }
        return new UserInfoModel("no uid", "no name", "no email");
    }


    @NonNull
    private Cancellable createCancelActionUserDataDownloads(DatabaseReference dataRef, ValueEventListener listener) {
        return () -> dataRef.removeEventListener(listener);
    }

    @NonNull
    private ValueEventListener createUserDataValueEventListener(FlowableEmitter<UserData> emitter) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<TransactionModel> transactionModels = parseTransactions(dataSnapshot);
                BalanceModel balanceModel = parseBalance(dataSnapshot.child(Constants.NODE_USERS_BALANCE));
                UserData userData = new UserData(transactionModels, balanceModel);
                emitter.onNext(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                emitter.onError(error.toException());
            }
        };
    }

    private Cancellable createCancelActionCategoriesDownloads(DatabaseReference categoriesRef, ValueEventListener valueEventListener) {
        return () -> categoriesRef.removeEventListener(valueEventListener);
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
        if (dataSnapshot.exists()) {
            return dataSnapshot.getValue(BalanceModel.class);
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

    private DatabaseReference getTrancationIDref(String userUID, TransactionModel transaction) {
        return database.getReference()
                .child(Constants.NODE_USERS)
                .child(userUID)
                .child(Constants.NODE_TRANSACTIONS)
                .child(transaction.getTransactionID());
    }

//    public Completable deleteTransaction(TransactionModel transaction) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) {
//            return Completable.error(new Throwable("User not logged in"));
//        }
//        String userUID = currentUser.getUid();
//        DatabaseReference transactionsRef = getTransactionsRefForUser(userUID);
//
//        return Completable.create(emitter -> {
//            transactionsRef.child(transaction).removeValue()
//                    .addOnSuccessListener(aVoid -> {
//                        emitter.onComplete();
//                    })
//                    .addOnFailureListener(emitter::onError);
//        });
//    }

    public Completable deleteTransaction(TransactionModel transaction) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return Completable.error(new Throwable("User not logged in"));
        }
        String userUID = currentUser.getUid();
        DatabaseReference transactionsRef = getTransactionsRefForUser(userUID);

        Completable updateBalanceCompletable = Completable.create(emitter -> {
            DatabaseReference balanceRef = getBalanceRefForUser(userUID);
            balanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    BalanceModel balanceModel = snapshot.getValue(BalanceModel.class);

                    assert balanceModel != null;
                    double newBalance = balanceModel.getBalance();
                    double newIncome = balanceModel.getIncome();
                    double newExpense = balanceModel.getExpense();

                    if (transaction.getType().equals(Constants.NODE_INCOME)) {
                        newBalance -= transaction.getAmount();
                        newIncome -= transaction.getAmount();
                    } else if (transaction.getType().equals(Constants.NODE_EXPENSE)) {
                        newBalance += transaction.getAmount();
                        newExpense -= transaction.getAmount();
                    }

                    balanceModel.setBalance(newBalance);
                    balanceModel.setIncome(newIncome);
                    balanceModel.setExpense(newExpense);

                    balanceRef.setValue(balanceModel)
                            .addOnSuccessListener(aVoid -> emitter.onComplete())
                            .addOnFailureListener(emitter::onError);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                }
            });
        });

        Completable deleteTransactionCompletable = Completable.create(emitter -> {
            transactionsRef.child(transaction.getTransactionID()).removeValue()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });

        return Completable.mergeArray(updateBalanceCompletable, deleteTransactionCompletable);
    }

    private DatabaseReference getBalanceRefForUser(String userUID) {
        return database.getReference()
                .child(Constants.NODE_USERS)
                .child(userUID)
                .child(Constants.NODE_USERS_BALANCE);
    }


    private DatabaseReference getTransactionsRefForUser(String userUID) {
        return database.getReference().child(Constants.NODE_USERS).child(userUID).child(Constants.NODE_TRANSACTIONS);
    }


    public Completable uploadTransaction(TransactionModel transaction) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        assert currentUser != null;
        String userUID = currentUser.getUid();

        DatabaseReference transactionsRef = database.getReference().child(Constants.NODE_USERS).child(userUID).child(Constants.NODE_TRANSACTIONS);
        DatabaseReference balanceRef = database.getReference().child(Constants.NODE_USERS).child(userUID).child(Constants.NODE_USERS_BALANCE);

        Log.d("TAG", "Uploading transaction...");

        return Completable.create(emitter -> {
            // Upload transaction
            transactionsRef.child(transaction.getTransactionID()).setValue(transaction)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TAG", "Transaction upload success.");

                        // Update balance
                        balanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                BalanceModel balanceModel = parseBalance(snapshot);

                                double newBalance = balanceModel.getBalance();
                                double newIncome = balanceModel.getIncome();
                                double newExpense = balanceModel.getExpense();

                                if (transaction.getType().equals(Constants.NODE_INCOME)) {
                                    newBalance += transaction.getAmount();
                                    newIncome += transaction.getAmount();
                                } else if (transaction.getType().equals(Constants.NODE_EXPENSE)) {
                                    newBalance -= transaction.getAmount();
                                    newExpense += transaction.getAmount();
                                }

                                balanceModel.setBalance(newBalance);
                                balanceModel.setIncome(newIncome);
                                balanceModel.setExpense(newExpense);

                                balanceRef.setValue(balanceModel)
                                        .addOnSuccessListener(aVoid1 -> {
                                            Log.d("TAG", "Balance update success.");
                                            emitter.onComplete();
                                        })
                                        .addOnFailureListener(error -> {
                                            Log.e("TAG", "Balance update failed: " + error.getMessage());
                                            emitter.onError(error);
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("TAG", "Balance update failed: " + error.getMessage());
                                emitter.onError(error.toException());
                            }
                        });
                    })
                    .addOnFailureListener(error -> {
                        Log.e("TAG", "Transaction upload failed: " + error.getMessage());
                        emitter.onError(error);
                    });
        }).subscribeOn(Schedulers.io());
    }


    public Completable updateTransaction(TransactionModel oldTransaction, TransactionModel newTransaction) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        assert currentUser != null;
        String userUID = currentUser.getUid();

        DatabaseReference transactionsRef = database.getReference().child(Constants.NODE_USERS).child(userUID).child(Constants.NODE_TRANSACTIONS);
        DatabaseReference balanceSummaryRef = database.getReference().child(Constants.NODE_USERS).child(userUID).child(Constants.NODE_USERS_BALANCE);

        return Completable.create(emitter -> {
            // Remove old transaction
            transactionsRef.child(oldTransaction.getTransactionID()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Upload new transaction
                        transactionsRef.child(newTransaction.getTransactionID()).setValue(newTransaction)
                                .addOnSuccessListener(aVoid1 -> {
                                    // Update balance
                                    balanceSummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            BalanceModel balanceModel = parseBalance(snapshot);

                                            double newBalance = balanceModel.getBalance();
                                            double newIncome = balanceModel.getIncome();
                                            double newExpense = balanceModel.getExpense();

                                            if (oldTransaction.getType().equals(Constants.NODE_INCOME)) {
                                                newBalance -= oldTransaction.getAmount();
                                                newIncome -= oldTransaction.getAmount();
                                            } else if (oldTransaction.getType().equals(Constants.NODE_EXPENSE)) {
                                                newBalance += oldTransaction.getAmount();
                                                newExpense -= oldTransaction.getAmount();
                                            }

                                            if (newTransaction.getType().equals(Constants.NODE_INCOME)) {
                                                newBalance += newTransaction.getAmount();
                                                newIncome += newTransaction.getAmount();
                                            } else if (newTransaction.getType().equals(Constants.NODE_EXPENSE)) {
                                                newBalance -= newTransaction.getAmount();
                                                newExpense += newTransaction.getAmount();
                                            }

                                            balanceModel.setBalance(newBalance);
                                            balanceModel.setIncome(newIncome);
                                            balanceModel.setExpense(newExpense);

                                            balanceSummaryRef.setValue(balanceModel)
                                                    .addOnSuccessListener(aVoid2 -> emitter.onComplete())
                                                    .addOnFailureListener(emitter::onError);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            emitter.onError(error.toException());
                                        }
                                    });
                                })
                                .addOnFailureListener(emitter::onError);
                    })
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io());
    }





    public Completable deleteAccount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return Completable.error(new Throwable("User not logged in"));
        }
        DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_USERS).child(currentUser.getUid());

        return Completable.create(emitter -> {
            ValueEventListener valueEventListener = createValueEventListener(emitter);
            userDataRef.addListenerForSingleValueEvent(valueEventListener);
            emitter.setCancellable(() -> userDataRef.removeEventListener(valueEventListener));
        });
    }

    private ValueEventListener createValueEventListener(CompletableEmitter emitter) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete()
                                    .addOnSuccessListener(aVoid1 -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError);
                        })
                        .addOnFailureListener(emitter::onError);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                emitter.onError(error.toException());
            }
        };
    }



}



