package com.example.moneytracker;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Objects;

public class DashBoardActivity extends AppCompatActivity {


    CardView dashboard_BTN_add;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    ArrayList<TransactionModel>  transactionModelArrayList;
    TransactionAdapter transactionAdapter;
    RecyclerView dashboard_VIEW_recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        findViews();
        inits();


        registerListeners();
        loadData();
    }

    private void findViews() {
        dashboard_BTN_add = findViewById(R.id.dashboard_BTN_add);
        dashboard_VIEW_recyclerview = findViewById(R.id.dashboard_VIEW_recyclerview);
    }

    private void inits() {
        initFireBase();
        initPreLoadingData();
    }

    private void initFireBase() {
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void initPreLoadingData() {
        transactionModelArrayList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this,transactionModelArrayList);
        dashboard_VIEW_recyclerview.setAdapter(transactionAdapter);
        dashboard_VIEW_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        dashboard_VIEW_recyclerview.setHasFixedSize(true);
    }

    private void registerListeners() {
        dashboard_BTN_add.setOnClickListener(v -> openAddTransactionActivity());
    }

    private void openAddTransactionActivity() {
        Intent intent = new Intent(DashBoardActivity.this,AddTransactionActivity.class);
        finish();
        startActivity(intent);
    }

    private void loadData() {
        transactionModelArrayList = new ArrayList<>();
        String currentUserUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        dbRef = database.getReference().child("users").child(currentUserUID).child("transactions");
        TransactionCallback callback = new TransactionCallback(DashBoardActivity.this, dashboard_VIEW_recyclerview, transactionModelArrayList);
        dbRef.addValueEventListener(callback);
    }
}