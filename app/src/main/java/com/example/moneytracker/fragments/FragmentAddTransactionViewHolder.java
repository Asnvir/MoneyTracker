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


import com.example.moneytracker.MySignal;
import com.example.moneytracker.databinding.FragmentAddTransactionBinding;
import com.example.moneytracker.navigation.FragmentHelper;
import com.example.moneytracker.navigation.Navigator;

import java.util.List;

public class FragmentAddTransactionViewHolder extends Fragment {

    public interface AddFragmentNavigation{
        void goBack();
        void openDashBoard();
    }
    private FragmentAddTransactionBinding binding;
    private FragmentAddTransactionViewModel viewModel;
    private Navigator navigator;
    private AddFragmentNavigation addFragmentNavigation = new AddFragmentNavigation() {
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
        viewModel = new ViewModelProvider(requireActivity()).get(FragmentAddTransactionViewModel.class);
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

        binding.addtransactionCHBXExpense.setOnClickListener(v -> {
            binding.addtransactionCHBXExpense.setChecked(true);
            binding.addtransactionCHBXIncome.setChecked(false);
        });

        binding.addtransactionCHBXIncome.setOnClickListener(v -> {
            binding.addtransactionCHBXExpense.setChecked(false);
            binding.addtransactionCHBXIncome.setChecked(true);
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
            Boolean isExpense = binding.addtransactionCHBXExpense.isChecked();
            Boolean isIncome = binding.addtransactionCHBXIncome.isChecked();
            viewModel.addTransaction(amount, note, category, isExpense, isIncome);
        });
    }

}
