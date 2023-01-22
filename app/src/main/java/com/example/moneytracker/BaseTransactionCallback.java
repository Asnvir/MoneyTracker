package com.example.moneytracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

abstract class BaseTransactionCallback {
    private Activity activity;
    private EditText txt_amount;
    private Spinner spnr_category;
    private EditText txt_note;
    private CheckBox chbx_expense;
    private CheckBox chbx_income;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;



    protected BaseTransactionCallbackListener listener;

    public BaseTransactionCallback(Activity activity, EditText txt_amount, EditText txt_note, Spinner spnr_category, CheckBox chbx_expense, CheckBox chbx_income, FirebaseDatabase database, DatabaseReference dbRef) {
        this.activity = activity;
        this.txt_amount = txt_amount;
        this.spnr_category = spnr_category;
        this.txt_note = txt_note;
        this.chbx_expense = chbx_expense;
        this.chbx_income = chbx_income;
        this.database = database;
        this.dbRef = dbRef;
    }

    public void setListener(BaseTransactionCallbackListener listener) {
        this.listener = listener;
    }

    protected boolean isValidUserInput() {
        InputValidator validator = new InputValidator(txt_amount, txt_note, spnr_category, chbx_expense, chbx_income);
        return validator.isValidInput();
    }

    protected Map<String, Object> prepareTransaction() {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionID", UUID.randomUUID().toString());
        transaction.put("amount", txt_amount.getText().toString());
        transaction.put("category", spnr_category.getSelectedItem().toString());
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

    protected void uploadTransaction(Map<String, Object> transaction) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentUserUID = user.getUid();
            String transactionID = (String) transaction.get("transactionID");
            if (transactionID != null) {
                Task<Void> taskUpload = DatabaseHandler.getInstance().uploadData(transaction, "users/" + currentUserUID + "/transactions/" + transactionID );
                taskUpload.addOnCompleteListener(task -> handleTransactionUploadResult(task, listener));

            }
        }
    }

    public void handleTransactionUploadResult(Task<Void> task, BaseTransactionCallbackListener listener) {
        if (task.isSuccessful()) {
            if (listener != null) {
                listener.onTransactionUploadComplete(task);
            }
        } else {
            if (listener != null) {
                listener.onTransactionUploadFailed(task);
            }
        }
    }
}