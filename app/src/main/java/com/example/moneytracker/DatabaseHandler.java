package com.example.moneytracker;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DatabaseHandler {
    private DatabaseReference dbRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public void downloadData(DataChangeCallback callback) {
        String currentUserUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        dbRef = database.getReference().child("users").child(currentUserUID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //handle error
            }
        });
    }

    public interface DataChangeCallback{
        void onDataChanged(DataSnapshot dataSnapshot);
    }
}
