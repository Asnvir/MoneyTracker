package com.example.moneytracker.fragments;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.moneytracker.TransactionModel;
import com.google.gson.Gson;


import com.example.moneytracker.databinding.FragmentEditTransactionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//public class FragmentEditTransaction extends Fragment implements BaseTransactionCallbackListener
public class FragmentEditTransactionViewHolder extends Fragment {

    EditText txt_amount;
    Spinner spnr_category;
    EditText txt_note;
    CheckBox chbx_expense;
    CheckBox chbx_income;
    Button btn_eddittransaction;
    String curr_txt_amount;
    String curr_txt_category;
    String curr_txt_note;
    String curr_txt_type;
    String curr_txt_transactionID;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference dbRef;

    private FragmentEditTransactionBinding binding;


    private TransactionModel transactionModel;


    String[] categories = {"Food", "Other", "Salary", "Shopping", "Subscription", "Transportation"};

    public static FragmentEditTransactionViewHolder newInstance(String jsonTransaction) {
        FragmentEditTransactionViewHolder fragment = new FragmentEditTransactionViewHolder();
        Bundle args = new Bundle();
        args.putString("jsonTransaction", jsonTransaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            String jsonTransaction = getArguments().getString("jsonTransaction");
            transactionModel = gson.fromJson(jsonTransaction, TransactionModel.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditTransactionBinding.inflate(inflater, container, false);


        getCurrentValues();
        inputCurrentValues();

//        setUpRecyclerView();
//        downloadData();
//        setUpBottomAppBar();
//        setUpAddButton();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


//
//    private void initFireBase() {
//        database = FirebaseDatabase.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseUser = firebaseAuth.getCurrentUser();
//    }
//

    private void getCurrentValues() {


//        curr_txt_amount = transactionModel.getAmount();
        curr_txt_category = transactionModel.getCategory();
        curr_txt_note = transactionModel.getNote();
        curr_txt_type = transactionModel.getType();
        curr_txt_transactionID = transactionModel.getTransactionID();
    }

    private void inputCurrentValues() {
        setAmount();
        setCategory();
        setNote();
        setType();
    }

    private void setAmount() {
        curr_txt_amount = curr_txt_amount.substring(1);
        binding.edittransactionTXTAmount.setText(curr_txt_amount);
    }

    private void setCategory() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.edittransactionSPINNERCategory.setAdapter(adapter);
        binding.edittransactionSPINNERCategory.setSelection(adapter.getPosition(curr_txt_category));
    }

    private void setNote() {
        binding.edittransactionTXTNote.setText(curr_txt_note);
    }

    private void setType() {
        if (curr_txt_type.equals("expense")) {
            binding.edittransactionCHBXExpense.setChecked(true);
            binding.edittransactionCHBXIncome.setChecked(false);
        } else {
            binding.edittransactionCHBXExpense.setChecked(false);
            binding.edittransactionCHBXIncome.setChecked(true);
        }
    }


    private void registerlListeners() {
        registerExpenseCheckBox();
        registerIncomeCheckBox();
        registerEditAction();
    }

    private void registerExpenseCheckBox() {
        binding.edittransactionCHBXExpense.setOnClickListener(v -> {
            binding.edittransactionCHBXExpense.setChecked(true);
            binding.edittransactionCHBXIncome.setChecked(false);
        });
    }

    private void registerIncomeCheckBox() {
        binding.edittransactionCHBXIncome.setOnClickListener(v -> {
            binding.edittransactionCHBXExpense.setChecked(false);
            binding.edittransactionCHBXIncome.setChecked(true);
        });
    }

    private void registerEditAction() {
//        EditTransactionCallback callback = new EditTransactionCallback(FragmentEditTransaction.this, txt_amount, txt_note, spnr_category, chbx_expense, chbx_income, database, dbRef,curr_txt_transactionID);
//        callback.setListener(this);
//        btn_eddittransaction.setOnClickListener(callback);
    }
//
//    @Override
//    public void onTransactionUploadComplete(Task<Void> task) {
//        Intent intent = new Intent(FragmentEditTransaction.this, DashBoardActivity.class);
//        finish();
//        Toast.makeText(FragmentEditTransaction.this, "EDIT TRANSACTION OK", Toast.LENGTH_LONG).show();
//        startActivity(intent);
//    }
//
//    @Override
//    public void onTransactionUploadFailed(Task<Void> task) {
//
//        String errorMessage = task.getException().getMessage();
//        Toast.makeText(FragmentEditTransaction.this, errorMessage, Toast.LENGTH_LONG).show();
//    }

}




