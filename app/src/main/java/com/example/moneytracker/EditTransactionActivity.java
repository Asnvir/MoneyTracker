package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class EditTransactionActivity extends AppCompatActivity {

    EditText txt_amount;
    Spinner  spnr_category;
    EditText txt_note;
    CheckBox chbx_expense;
    CheckBox chbx_income;
    Button   btn_eddittransaction;
    String curr_txt_amount;
    String curr_txt_category;
    String curr_txt_note;
    String curr_txt_type;
    String curr_txt_transactionID;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference dbRef;


    String[] categories = {"Food", "Other", "Salary", "Shopping","Subscription","Transportation"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        initFireBase();
        findViews();
        getCurrentValues();
        inputCurrentValues();
        registerlListeners();

    }



    private void initFireBase() {
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }


    private void findViews() {
        txt_amount = findViewById(R.id.edittransaction_TXT_amount);
        spnr_category = findViewById(R.id.edittransaction_SPINNER_category);
        txt_note = findViewById(R.id.edittransaction_TXT_Note);
        chbx_expense = findViewById(R.id.edittransaction_CHBX_expense);
        chbx_income = findViewById(R.id.edittransaction_CHBX_income);
        btn_eddittransaction = findViewById(R.id.edittransaction_BTN_edit);
    }

    private void getCurrentValues() {
        curr_txt_amount = getIntent().getStringExtra("amount");
        curr_txt_category = getIntent().getStringExtra("category");
        curr_txt_note = getIntent().getStringExtra("note");
        curr_txt_type = getIntent().getStringExtra("type");
        curr_txt_transactionID = getIntent().getStringExtra("transactionID");
    }

    private void inputCurrentValues() {
        txt_amount.setText(String.valueOf(curr_txt_amount));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_category.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(curr_txt_category);
        spnr_category.setSelection(spinnerPosition);

        txt_note.setText(curr_txt_note);

        if(curr_txt_type.equals("Expense")){
            chbx_expense.setChecked(true);
        }else{
            chbx_income.setChecked(true);
        }
    }

    private void registerlListeners() {
        registerExpenseCheckBox();
        registerIncomeCheckBox();
        registerEditAction();
    }

    private void registerExpenseCheckBox() {
        chbx_expense.setOnClickListener(v -> {
            chbx_expense.setChecked(true);
            chbx_income.setChecked(false);
        });
    }

    private void registerIncomeCheckBox() {
        chbx_income.setOnClickListener(v -> {
            chbx_expense.setChecked(false);
            chbx_income.setChecked(true);
        });
    }

    private void registerEditAction() {
        EditTransactionCallback callback = new EditTransactionCallback(EditTransactionActivity.this, txt_amount, txt_note, spnr_category, chbx_expense, chbx_income, database, dbRef,curr_txt_transactionID);
        btn_eddittransaction.setOnClickListener(callback);
    }

}




