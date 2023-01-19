package com.example.moneytracker;


import android.app.Activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Map;



public class EditTransactionCallback extends BaseTransactionCallback implements View.OnClickListener {
    private final String curr_txt_transactionID;

    public EditTransactionCallback(Activity activity, EditText txt_amount, EditText txt_note, Spinner spnr_category, CheckBox chbx_expense, CheckBox chbx_income, FirebaseDatabase database, DatabaseReference dbRef, String curr_txt_transactionID) {
        super(activity, txt_amount, txt_note, spnr_category, chbx_expense, chbx_income, database, dbRef);
        this.curr_txt_transactionID = curr_txt_transactionID;
    }

    @Override
    protected Map<String, Object> prepareTransaction() {
        Map<String, Object> transaction = super.prepareTransaction();
        transaction.put("transactionID", curr_txt_transactionID);
        return transaction;
    }

    @Override
    protected void uploadTransaction(Map<String, Object> transaction) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String currentUserUID = user.getUid();
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUID).child("transactions").child(curr_txt_transactionID);
        transactionRef.setValue(transaction).addOnCompleteListener(this::onTransactionComplete);

    }

    @Override
    public void onClick(View v) {
        if(!isValidUserInput()) {
            return;
        }
        Map<String, Object> transaction = prepareTransaction();
        uploadTransaction(transaction);
    }
}



