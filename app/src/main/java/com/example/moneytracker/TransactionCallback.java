package com.example.moneytracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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

        TransactionAdapter transactionAdapter = new TransactionAdapter(context, transactionModelArrayList);
        transactionAdapter.setOnItemClickListener(new MyOnItemClickListener(transactionAdapter,context));
        recyclerView.setAdapter(transactionAdapter);

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
