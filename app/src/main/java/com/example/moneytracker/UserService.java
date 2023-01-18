package com.example.moneytracker;

import android.widget.Toast;

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
        DatabaseReference userRef = firebaseDatabase.getReference("users/" + uid + "/amount");
        Map<String, Object> values = new HashMap<>();
        values.put("expense", "0");
        values.put("income", "0");
        values.put("balance", "0");
        userRef.updateChildren(values);
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

