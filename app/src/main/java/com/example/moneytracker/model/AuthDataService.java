package com.example.moneytracker.model;

import androidx.annotation.NonNull;

import com.example.moneytracker.data.BalanceModel;
import com.example.moneytracker.data.UserInfoModel;
import com.example.moneytracker.util.Constants;
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

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AuthDataService {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public AuthDataService() {
    }

    public Completable createUser(String fullName, String email, String password) {
        return Completable.create(emitter -> {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    assert user != null;

                                    Disposable disposable = checkInitUserInDatabase(user.getUid(), fullName, email)
                                            .subscribe(
                                                    emitter::onComplete,
                                                    emitter::onError
                                            );
                                    compositeDisposable.add(disposable);
                                } else {
                                    emitter.onError(task.getException());
                                }
                            });
                })
                .doOnDispose(compositeDisposable::clear)
                .doFinally(compositeDisposable::clear);
    }


    public Completable signIn(String email, String password) {
        return Completable.create(emitter -> {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = getCurrentUserUid();
                            Disposable disposable = getCurrentUserNameOrUseDefault(uid)
                                    .flatMapCompletable(name -> {
                                        return checkInitUserInDatabase(uid, name, email);
                                    })
                                    .subscribe(emitter::onComplete, emitter::onError);
                            compositeDisposable.add(disposable);
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }


    private Single<String> getCurrentUserNameOrUseDefault(String uid) {
        DatabaseReference userRef = firebaseDatabase.getReference().child(Constants.NODE_USERS).child(uid).child(Constants.NODE_USERS_INFO).child(Constants.NODE_NAME);

        return Single.create((SingleEmitter<String> emitter) -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null) {
                        emitter.onSuccess(name);
                    } else {
                        emitter.onSuccess(Constants.DEFAULT_NAME);
                    }
                    userRef.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                    userRef.removeEventListener(this);
                }
            };
            userRef.addListenerForSingleValueEvent(listener);
            emitter.setCancellable(() -> {
                userRef.removeEventListener(listener);
            });
        }).subscribeOn(Schedulers.io());
    }


    public Single<FirebaseUser> signInWithGoogle(String idToken) {
        GoogleAuthCredential credential = (GoogleAuthCredential) GoogleAuthProvider.getCredential(idToken, null);

        return Single.create(emitter ->
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                Disposable disposable = checkInitUserInDatabase(user.getUid(), user.getDisplayName(), user.getEmail())
                                        .subscribe(() -> {
                                            emitter.onSuccess(user);
                                        }, emitter::onError);
                                compositeDisposable.add(disposable);
                            } else {
                                emitter.onError(task.getException());
                            }
                        })
        );
    }

    public String getCurrentUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }

    private Completable checkInitUserInDatabase(String uid, String name, String email) {
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
                        emitter.onComplete();
                    } else {
                        emitter.onError(databaseError.toException());
                    }
                }
            });
        })).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io());
    }


    private Completable initUserInfo(DatabaseReference userRef, String uid, String name, String email) {
        return Completable.create(emitter -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                        userRef.child(Constants.NODE_USERS_INFO).setValue(userInfoModel)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        emitter.onComplete();
                                    } else {
                                        emitter.onError(task.getException());
                                    }
                                });
                    } else {
                        emitter.onComplete();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                }
            };

            userRef.child(Constants.NODE_USERS_INFO).addListenerForSingleValueEvent(listener);
            emitter.setCancellable(() -> userRef.child(Constants.NODE_USERS_INFO).removeEventListener(listener));
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
                                        emitter.onComplete();
                                    } else {
                                        emitter.onError(task.getException());
                                    }
                                });
                    } else {
                        emitter.onComplete();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                }
            };

            userRef.child(Constants.NODE_USERS_BALANCE).addValueEventListener(listener);

            emitter.setCancellable(() -> userRef.child(Constants.NODE_USERS_BALANCE).removeEventListener(listener));
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
