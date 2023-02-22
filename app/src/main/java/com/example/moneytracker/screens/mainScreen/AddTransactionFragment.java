package com.example.moneytracker.screens.mainScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker.R;
import com.example.moneytracker.contract.HasCustomTitle;
import com.example.moneytracker.contract.NavigatorMain;
import com.example.moneytracker.databinding.FragmentAddTransactionBinding;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.MySignal;


public class AddTransactionFragment extends Fragment implements HasCustomTitle {


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

        binding.addtransactionBTNAdd.setOnClickListener(v -> {
            double amount = 0;
            try {
                amount = Double.parseDouble(binding.addtransactionTXTAmount.getText().toString());
            } catch (NumberFormatException ignored) {
            }
            String note = binding.addtransactionTXTNote.getText().toString();
            String category = (String) binding.addtransactionSPINNERCategory.getSelectedItem();
            String type = getSelectedRadioButtonText();
            viewModel.initTransaction(amount, note, category, type);
        });

        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.addtransactionSPINNERCategory.setAdapter(adapter);
        });

        viewModel.getIsUploadTransactionLiveData().observe(getViewLifecycleOwner(), value -> {
            if (value) {
                viewModel.uploadTransactionComplete();
                navigatorMain.navigateToDashboard();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.addtransactionPGBProgressbar.setVisibility(View.VISIBLE);
            } else {
                binding.addtransactionPGBProgressbar.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                MySignal.getInstance().toast(errorMessage);
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

    @Override
    public int getTitleRes() {
        return R.string.add_transaction_title;
    }
}
