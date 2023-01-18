package com.example.moneytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TransactionCallback implements ValueEventListener {

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<TransactionModel> transactionModelArrayList;

    public TransactionCallback(Context context, RecyclerView recyclerView, ArrayList<TransactionModel> transactionModelArrayList) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.transactionModelArrayList = transactionModelArrayList;
    }

    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        addTransactionsToList(dataSnapshot);
        sortTransactions();
        updateUI();
    }

    private void addTransactionsToList(DataSnapshot dataSnapshot) {
        for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()) {
            TransactionModel model = transactionSnapshot.getValue(TransactionModel.class);
            if (!transactionModelArrayList.contains(model)) {
                transactionModelArrayList.add(model);
            }
        }
    }

    private void sortTransactions() {
        Collections.sort(transactionModelArrayList, sortByDateAndTime);
    }

    private void updateUI() {
        updateAmounts();
        TransactionAdapter transactionAdapter = new TransactionAdapter(context, transactionModelArrayList);
        transactionAdapter.setOnItemClickListener(new MyOnItemClickListener(transactionAdapter,context));
        recyclerView.setAdapter(transactionAdapter);

    }

    private void updateAmounts() {
        TextView txt_income = (TextView) ((Activity) context).findViewById(R.id.dashboard_TXT_income);
        TextView txt_balance = (TextView) ((Activity) context).findViewById(R.id.dashboard_TXT_balance);
        TextView txt_expense = (TextView) ((Activity) context).findViewById(R.id.dashboard_TXT_expense);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        DatabaseReference userRef = firebaseDatabase.getReference("users/" + uid + "/amount");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, String> values = (Map<String, String>) dataSnapshot.getValue();
                String expense = values.get("expense");
                String income = values.get("income");
                String balance = values.get("balance");
                // Use the values here in your app
                txt_income.setText(income);
                txt_balance.setText(balance);
                txt_expense.setText(expense);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private Comparator<TransactionModel> sortByDateAndTime = (o1, o2) -> {
        int dateCompare = o2.getDate().compareTo(o1.getDate());
        if(dateCompare == 0){
            return o2.getTime().compareTo(o1.getTime());
        }else{
            return dateCompare;
        }
    };

    @Override
    public void onCancelled(DatabaseError error) {
        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
