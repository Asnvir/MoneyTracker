package com.example.moneytracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

class AddTransactionCallback implements View.OnClickListener {
    private Activity activity;
    private EditText txt_amount;
    private EditText txt_note;
    private Spinner spnr_category;
    private CheckBox chbx_expense;
    private CheckBox chbx_income;

    FirebaseDatabase database;
    private DatabaseReference dbRef;

    public AddTransactionCallback(Activity activity, EditText txt_amount, EditText txt_note, Spinner spnr_category, CheckBox chbx_expense, CheckBox chbx_income, FirebaseDatabase database, DatabaseReference dbRef) {
        this.activity = activity;
        this.txt_amount = txt_amount;
        this.spnr_category = spnr_category;
        this.txt_note = txt_note;
        this.chbx_expense = chbx_expense;
        this.chbx_income = chbx_income;
        this.database = database;
        this.dbRef = dbRef;
    }

    @Override
    public void onClick(View v) {
        if(!isValidUserInput()) {
            return;
        }
        Map<String, Object> transaction = prepareTransaction();
        uploadTransaction(transaction);
    }


    private boolean isValidUserInput(){
        InputValidator validator = new InputValidator(txt_amount, txt_note, spnr_category, chbx_expense, chbx_income);
        return validator.isValidInput();
    }

    private Map<String, Object> prepareTransaction(){
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionID", UUID.randomUUID().toString());
        transaction.put("amount", txt_amount.getText().toString());
        transaction.put("category",spnr_category.getSelectedItem().toString());
        transaction.put("note", txt_note.getText().toString());
        transaction.put("type", chbx_expense.isChecked() ? "expense" : "income");

        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String dateString = dateFormat.format(date);
        String timeString = timeFormat.format(date);
        transaction.put("date", dateString);
        transaction.put("time", timeString);

        return transaction;
    }

    private void uploadTransaction(Map<String, Object> transaction) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String currentUserUID = user.getUid();
            String transactionID = (String) transaction.get("transactionID");
            if(transactionID != null){
                dbRef = database.getReference().child("users").child(currentUserUID).child("transactions").child(transactionID);
                dbRef.setValue(transaction).addOnCompleteListener(this::onTransactionComplete);
            }
        }
    }

    private void onTransactionComplete(Task<Void> task) {
        if (task.isSuccessful()) {
            Toast.makeText(activity, "Added success", Toast.LENGTH_SHORT).show();
            openDashBoardActivity();
        } else {
            Toast.makeText(activity, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDashBoardActivity() {
        Intent intent = new Intent(activity, DashBoardActivity.class);
        activity.finish();
        activity.startActivity(intent);
    }


}


