package com.example.moneytracker;

import android.widget.Toast;

import com.example.moneytracker.util.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserService {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private OnUserCreationListener listener;

    public UserService(OnUserCreationListener listener) {
        this.listener = listener;
    }

    public void initializeUser(String uid) {
//        DatabaseReference userRef = firebaseDatabase.getReference("users/" + uid + "/amount");
        DatabaseReference userRef = firebaseDatabase.getReference(Utils.NODE_USERS + "/" + uid + "/" + Utils.NODE_AMOUNT);
//
//        Map<String, Object> values = new HashMap<>();
//        values.put(Utils.INCOME, 0.0);
//        values.put(Utils.BALANCE, 0.0);
//        values.put(Utils.EXPENSE, 0.0);
        userRef.child(Utils.INCOME).setValue(0.0);
        userRef.child(Utils.BALANCE).setValue(0.0);
        userRef.child(Utils.EXPENSE).setValue(0.0);

    }

    public void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                        initializeUser(uid);
                        listener.onSuccess();
                    } else {
                        Exception exception = task.getException();
                        assert exception != null;
                        listener.onError(exception.getMessage());
                    }
                });
    }
}

