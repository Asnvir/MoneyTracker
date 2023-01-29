package com.example.moneytracker.not_use;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

//public class AddTransactionCallback extends BaseTransactionCallback implements View.OnClickListener {
//
//    public AddTransactionCallback(Activity activity, EditText txt_amount, EditText txt_note, Spinner spnr_category, CheckBox chbx_expense, CheckBox chbx_income, FirebaseDatabase database, DatabaseReference dbRef) {
//        super(activity, txt_amount, txt_note, spnr_category, chbx_expense, chbx_income, database, dbRef);
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (!isValidUserInput()) {
//            return;
//        }
//        Map<String, Object> transaction = prepareTransaction();
//        uploadTransaction(transaction);
//    }
//}
