package com.example.moneytracker.fragments;

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
import com.example.moneytracker.util.MySignal;
import com.example.moneytracker.databinding.FragmentAddTransactionBinding;
import com.example.moneytracker.navigation.FragmentHelper;
import com.example.moneytracker.navigation.Navigator;
import com.example.moneytracker.util.Utils;

import java.util.List;

public class AddTransactionHolder extends Fragment {

    public interface AddFragmentNavigation{
        void goBack();
        void openDashBoard();
    }
    private FragmentAddTransactionBinding binding;
    private AddTransactionViewModel viewModel;
    private Navigator navigator;
    private final AddFragmentNavigation addFragmentNavigation = new AddFragmentNavigation() {
        @Override
        public void goBack() {

        }

        @Override
        public void openDashBoard() {
            navigator.showTransactionsScreen();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AddTransactionViewModel.class);
        viewModel.setListener(addFragmentNavigation);
        navigator = FragmentHelper.navigator(this);
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
            viewModel.addTransaction(amount, note, category, type);
        });
    }

    public String getSelectedRadioButtonText() {
        int checkedId = binding.addtransactionRGExpenseIncome.getCheckedRadioButtonId();
        if (checkedId == R.id.addtransaction_RB_expense) {
            return Utils.EXPENSE;
        } else if (checkedId == R.id.addtransaction_RB_income) {
            return Utils.INCOME;
        } else {
            return "";
        }
    }


}
