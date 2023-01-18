package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTransactionActivity extends AppCompatActivity {
    EditText txt_amount;
    Spinner spnr_category;
    EditText txt_note;
    CheckBox chbx_expense;
    CheckBox chbx_income;
    Button btn_addtransaction;
    String[] categories = {"Food", "Other", "Salary", "Shopping", "Subscription", "Transportation"};


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        initFireBase();

        findViews();
        initCategories();
        registerlListeners();

    }

    private void initFireBase() {
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }


    private void findViews() {
        txt_amount = findViewById(R.id.addtransaction_TXT_amount);
        spnr_category = findViewById(R.id.addtransaction_SPINNER_category);
        txt_note = findViewById(R.id.addtransaction_TXT_Note);
        chbx_expense = findViewById(R.id.addtransaction_CHBX_expense);
        chbx_income = findViewById(R.id.addtransaction_CHBX_income);
        btn_addtransaction = findViewById(R.id.addtransaction_BTN_add);
    }

    private void initCategories() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_category.setAdapter(adapter);
    }

    private void registerlListeners() {
        registerExpenseCheckBox();
        registerIncomeCheckBox();
        registerAddAction();
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

    private void registerAddAction() {
        AddTransactionCallback callback = new AddTransactionCallback(AddTransactionActivity.this, txt_amount, txt_note, spnr_category, chbx_expense, chbx_income, database, dbRef);

        btn_addtransaction.setOnClickListener(callback);
    }


}