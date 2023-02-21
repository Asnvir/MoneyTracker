package com.example.moneytracker.mainScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.example.moneytracker.R;
import com.example.moneytracker.contract.NavigatorMain;
import com.example.moneytracker.databinding.FragmentAddTransactionBinding;
import com.example.moneytracker.util.MySignal;

import com.example.moneytracker.util.Constants;

import java.util.List;


public class AddTransactionFragment extends Fragment {


    private FragmentAddTransactionBinding binding;
    private AddTransactionViewModel viewModel;
    private NavigatorMain navigatorMain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddTransactionViewModel.class);
        navigatorMain = NavigatorMain.getNavigator(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel = null;
        navigatorMain = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categories) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.addtransactionSPINNERCategory.setAdapter(adapter);
                MySignal.getInstance().toast("DOWNLOADED CATEGORIES");
            }
        });


        binding.addtransactionBTNAdd.setOnClickListener(v -> {

            double amount = 0;
            try {
                amount = Double.parseDouble(binding.addtransactionTXTAmount.getText().toString());
            } catch (NumberFormatException e) {
                MySignal.getInstance().toast(e.getMessage());
            }
            String note = binding.addtransactionTXTNote.getText().toString();
            String category = (String) binding.addtransactionSPINNERCategory.getSelectedItem();
            String type = getSelectedRadioButtonText();
            viewModel.initTransaction(amount, note, category, type);
        });



        viewModel.getIsUploadTransactionLiveData().observe(getViewLifecycleOwner(),value ->{
            if(value){
                navigatorMain.navigateToDashboard();
            }
        });
    }

    public String getSelectedRadioButtonText() {
        int checkedId = binding.addtransactionRGExpenseIncome.getCheckedRadioButtonId();
        if (checkedId == R.id.addtransaction_RB_expense) {
            return Constants.NODE_EXPENSE;
        } else if (checkedId == R.id.addtransaction_RB_income) {
            return Constants.NODE_INCOME;
        } else {
            return "";
        }
    }


}
