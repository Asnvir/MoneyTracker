package com.example.moneytracker;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;


public class DatabaseHandler {
    private static DatabaseHandler instance;
    private DatabaseReference dbRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public interface DataChangeCallback{
        void onDataChanged(DataSnapshot dataSnapshot);
    }

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

    public void downloadTransaction(DataChangeCallback callback) {
        String currentUserUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        dbRef = database.getReference().child("users").child(currentUserUID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MySignal.getInstance().toast(error.getMessage());
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

