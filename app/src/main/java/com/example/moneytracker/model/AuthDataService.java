package com.example.moneytracker.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.moneytracker.data.BalanceModel;
import com.example.moneytracker.data.UserInfoModel;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.MySignal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


import java.util.Objects;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AuthDataService {
    private final FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public AuthDataService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public Completable createUser(String fullName, String email, String password) {
        return Completable.create(emitter -> {
                    Log.d(Constants.AuthDataService_TAG, "createUser: Starting createUserWithEmailAndPassword");

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    assert user != null;

                                    Log.d(Constants.AuthDataService_TAG, "createUser: User created successfully, initializing user info");

                                    Disposable disposable = checkInitUserInDatabase(user.getUid(), fullName, email)
                                            .subscribe(
                                                    () -> {
                                                        Log.d(Constants.AuthDataService_TAG, "createUser: User info initialized successfully");
                                                        emitter.onComplete();
                                                    },
                                                    error -> {
                                                        Log.e(Constants.AuthDataService_TAG, "createUser: Error initializing user info: " + error.getMessage(), error);
                                                        emitter.onError(error);
                                                    }
                                            );
                                    compositeDisposable.add(disposable);
                                } else {
                                    Log.e(Constants.AuthDataService_TAG, "createUser: Error creating user: " + Objects.requireNonNull(task.getException()).getMessage(), task.getException());
                                    emitter.onError(task.getException());
                                }
                            });
                })
                .doOnDispose(() -> {
                    Log.d(Constants.AuthDataService_TAG, "createUser: Completable disposed, clearing CompositeDisposable");
                    compositeDisposable.clear();
                })
                .doFinally(() -> {
                    Log.d(Constants.AuthDataService_TAG, "createUser: Completable completed or errored, clearing CompositeDisposable");
                    compositeDisposable.clear();
                });
    }



    public Completable signIn(String email, String password) {
        Log.d(Constants.AuthDataService_TAG, "signIn called with email: " + email);

        return Completable.create(emitter -> {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(Constants.AuthDataService_TAG, "FirebaseAuth signInWithEmailAndPassword succeeded");
                            String uid = getCurrentUserUid();
                            Log.d(Constants.AuthDataService_TAG, "UID passed to getCurrentUserNameOrUseDefault: " + uid);

                            Disposable disposable = getCurrentUserNameOrUseDefault(uid)
                                    .flatMapCompletable(name -> {
                                        Log.d(Constants.AuthDataService_TAG, "checkInitUserInDatabase called with name: " + name + " and email: " + email);
                                        return checkInitUserInDatabase(uid, name, email);
                                    })
                                    .subscribe(() -> {
                                        Log.d(Constants.AuthDataService_TAG, "signIn successful");
                                        emitter.onComplete();
                                    }, error -> {
                                        Log.e(Constants.AuthDataService_TAG, "signIn failed: " + error.getMessage(), error);
                                        emitter.onError(error);
                                    });
                            compositeDisposable.add(disposable);
                        } else {
                            Log.e(Constants.AuthDataService_TAG, "FirebaseAuth signInWithEmailAndPassword failed: " + Objects.requireNonNull(task.getException()).getMessage(), task.getException());
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    private Single<String> getCurrentUserNameOrUseDefault(String uid) {

        DatabaseReference userRef = firebaseDatabase.getReference().child(Constants.NODE_USERS).child(uid).child(Constants.NODE_USERS_INFO).child(Constants.NODE_NAME);
        Log.d(Constants.AuthDataService_TAG, "userRef to getCurrentUserNameOrUseDefault: " + userRef);

        return Single.create((SingleEmitter<String> emitter) -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null) {
                        Log.d(Constants.AuthDataService_TAG, "Current user name: " + name);
                        emitter.onSuccess(name);
                    } else {
                        Log.d(Constants.AuthDataService_TAG, "Using default name");
                        emitter.onSuccess(Constants.DEFAULT_NAME);
                    }
                    userRef.removeEventListener(this); // Unregister the listener
                    Log.d(Constants.AuthDataService_TAG, "onDataChange listener unregistered");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(Constants.AuthDataService_TAG, "Error getting current user name: " + error.getMessage(), error.toException());
                    emitter.onError(error.toException());
                    userRef.removeEventListener(this); // Unregister the listener
                    Log.d(Constants.AuthDataService_TAG, "onCancelled listener unregistered");
                }
            };
            userRef.addListenerForSingleValueEvent(listener);
            emitter.setCancellable(() -> {
                Log.d(Constants.AuthDataService_TAG, "Current user name request cancelled");
                userRef.removeEventListener(listener);
                Log.d(Constants.AuthDataService_TAG, "Listener removed on emitter dispose");
            }); // Dispose of the listener
            Log.d(Constants.AuthDataService_TAG, "Listener registered");
        }).subscribeOn(Schedulers.io());
    }



    public Single<FirebaseUser> signInWithGoogle(String idToken) {
        // create GoogleAuthCredential object
        GoogleAuthCredential credential = (GoogleAuthCredential) GoogleAuthProvider.getCredential(idToken, null);
        Log.d(Constants.AuthDataService_TAG, "GoogleAuthCredential created");

        // sign in with credential
        return Single.create(emitter ->
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                Log.d(Constants.AuthDataService_TAG, "FirebaseAuth signInWithCredential succeeded for user: " + user.getUid());

                                // call checkInitUserInDatabase and add disposable to compositeDisposable
                                Disposable disposable = checkInitUserInDatabase(user.getUid(), user.getDisplayName(), user.getEmail())
                                        .subscribe(() -> {
                                            emitter.onSuccess(user);
                                        }, emitter::onError);
                                compositeDisposable.add(disposable);
                            } else {
                                Log.e(Constants.AuthDataService_TAG, "FirebaseAuth signInWithCredential failed: " + Objects.requireNonNull(task.getException()).getMessage(), task.getException());
                                emitter.onError(task.getException());
                            }
                        })
        );
    }


    public String getCurrentUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d(Constants.AuthDataService_TAG, "Current user is authenticated with UID: " + currentUser.getUid());
            return currentUser.getUid();
        } else {
            Log.d(Constants.AuthDataService_TAG, "Current user is not authenticated");
            return null;
        }
    }



    private Completable checkInitUserInDatabase(String uid, String name, String email) {
        Log.d(Constants.AuthDataService_TAG, "checkInitUserInDatabase called with UID: " + uid);
        DatabaseReference userRef = firebaseDatabase.getReference().child(Constants.NODE_USERS).child(uid);
        return Completable.mergeArray(
                        initUserInfo(userRef, uid, name, email),
                        initBalance(userRef)
                ).andThen(Completable.create(emitter -> {
                    userRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            mutableData.setValue(mutableData.getValue());
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            if (committed && databaseError == null) {
                                Log.d(Constants.AuthDataService_TAG, "User initialized successfully");
                                emitter.onComplete();
                            } else {
                                Log.e(Constants.AuthDataService_TAG, "Failed to run transaction: " + databaseError.getMessage(), databaseError.toException());
                                emitter.onError(databaseError.toException());
                            }
                        }
                    });
                })).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .doOnError(throwable -> Log.e(Constants.AuthDataService_TAG, "Failed to initialize user: " + throwable.getMessage(), throwable));
    }



    private Completable initUserInfo(DatabaseReference userRef, String uid, String name, String email) {
        return Completable.create(emitter -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(Constants.AuthDataService_TAG, "onDataChange: " + snapshot);

                    UserInfoModel userInfoModel = snapshot.getValue(UserInfoModel.class);

                    boolean isUpdated = false;
                    if (userInfoModel == null) {
                        userInfoModel = new UserInfoModel(uid, name, email);
                        isUpdated = true;
                    } else {
                        if (!snapshot.hasChild(Constants.NODE_NAME) || userInfoModel.getName().isEmpty()) {
                            userInfoModel.setName(name);
                            isUpdated = true;
                        }
                        if (!snapshot.hasChild(Constants.NODE_EMAIL) || userInfoModel.getEmail().isEmpty()) {
                            userInfoModel.setEmail(email);
                            isUpdated = true;
                        }
                        if (!snapshot.hasChild(Constants.NODE_UID) || !uid.equals(userInfoModel.getUid())) {
                            userInfoModel.setUid(uid);
                            isUpdated = true;
                        }
                    }

                    if (isUpdated) {
                        Log.d(Constants.AuthDataService_TAG, "User info is updated");

                        userRef.child(Constants.NODE_USERS_INFO).setValue(userInfoModel)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(Constants.AuthDataService_TAG, "User info initialized successfully");
                                        emitter.onComplete();
                                    } else {
                                        Log.e(Constants.AuthDataService_TAG, "Failed to initialize user info: " + task.getException().getMessage(), task.getException());
                                        emitter.onError(task.getException());
                                    }
                                });
                    } else {
                        Log.d(Constants.AuthDataService_TAG, "User info is not updated");
                        emitter.onComplete();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(Constants.AuthDataService_TAG, "Failed to initialize user info: " + error.getMessage(), error.toException());
                    emitter.onError(error.toException());
                }
            };

            userRef.child(Constants.NODE_USERS_INFO).addListenerForSingleValueEvent(listener);

            emitter.setCancellable(() -> {
                userRef.child(Constants.NODE_USERS_INFO).removeEventListener(listener);
                Log.d(Constants.AuthDataService_TAG, "User info listener removed");
            });
        }).subscribeOn(Schedulers.io());
    }



    private Completable initBalance(DatabaseReference userRef) {
        return Completable.create(emitter -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    BalanceModel balanceModel = snapshot.getValue(BalanceModel.class);

                    boolean isUpdated = false;
                    if (balanceModel == null) {
                        balanceModel = new BalanceModel(0.0, 0.0, 0.0);
                        isUpdated = true;
                    } else {
                        if (!snapshot.hasChild(Constants.NODE_BALANCE)) {
                            balanceModel.setBalance(0.0);
                            isUpdated = true;
                        }
                        if (!snapshot.hasChild(Constants.NODE_EXPENSE)) {
                            balanceModel.setExpense(0.0);
                            isUpdated = true;
                        }
                        if (!snapshot.hasChild(Constants.NODE_INCOME)) {
                            balanceModel.setIncome(0.0);
                            isUpdated = true;
                        }
                    }

                    if (isUpdated) {
                        userRef.child(Constants.NODE_USERS_BALANCE).setValue(balanceModel)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(Constants.AuthDataService_TAG, "Balance initialized successfully");
                                        emitter.onComplete();
                                    } else {
                                        Log.e(Constants.AuthDataService_TAG, "Failed to initialize balance: " + task.getException().getMessage(), task.getException());
                                        emitter.onError(task.getException());
                                    }
                                });
                    } else {
                        emitter.onComplete();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(Constants.AuthDataService_TAG, "Error getting balance: " + error.getMessage(), error.toException());
                    emitter.onError(error.toException());
                }
            };

            userRef.child(Constants.NODE_USERS_BALANCE).addValueEventListener(listener);

            emitter.setCancellable(() -> {
                Log.d(Constants.AuthDataService_TAG, "Balance initialization cancelled");
                userRef.child(Constants.NODE_USERS_BALANCE).removeEventListener(listener);
            });
        }).subscribeOn(Schedulers.io());
    }


    public Completable sendPasswordResetEmail(String email) {
        return Completable.create(emitter -> {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onComplete();
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        }).subscribeOn(Schedulers.io());
    }



    public void disposeAll() {
        compositeDisposable.dispose();
    }
}
