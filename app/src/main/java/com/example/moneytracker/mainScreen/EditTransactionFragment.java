package com.example.moneytracker.mainScreen;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker.R;
import com.example.moneytracker.contract.NavigatorMain;
import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.databinding.FragmentEditTransactionBinding;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.EditTransactionViewModelFactory;
import com.example.moneytracker.util.MySignal;


import java.util.List;
import java.util.Locale;


public class EditTransactionFragment extends Fragment {


    private FragmentEditTransactionBinding binding;
    private EditTransactionViewModel viewModel;
    private NavigatorMain navigatorMain;


    public static EditTransactionFragment newInstance(TransactionModel transactionModel) {
        EditTransactionFragment fragment = new EditTransactionFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.TAG_BUNDLE_TRANSACTION_MODEL, transactionModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransactionModel transactionModel = getArguments() != null
                ? getArguments().getParcelable(Constants.TAG_BUNDLE_TRANSACTION_MODEL)
                : null;

        if (transactionModel != null) {
            EditTransactionViewModelFactory viewModelFactory = new EditTransactionViewModelFactory(transactionModel);
            viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(EditTransactionViewModel.class);
        } else {
            viewModel = null;
        }

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
        binding = FragmentEditTransactionBinding.inflate(inflater, container, false);
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

        if (viewModel != null) {
            observeViewModel();


            binding.eddTransactionBTNEdit.setOnClickListener(v -> {
                double amount = 0;
                try {
                    String amountString = binding.eddTransactionTXTAmount.getText().toString().replace(",", ".");
                    amount = Double.parseDouble(amountString);
                } catch (NumberFormatException ignored) {
                }
                String note = binding.eddTransactionTXTNote.getText().toString();
                String category = (String) binding.eddTransactionSPINNERCategory.getSelectedItem();
                String type = getSelectedRadioButtonText();
                viewModel.initTransaction(amount, note, category, type);
            });

            binding.eddTransactionBTNDelete.setOnClickListener(v -> {
                viewModel.deleteTransaction();
            });

            viewModel.getIsDeleteSuccessful().observe(getViewLifecycleOwner(), isSuccessful -> {
                if (isSuccessful) {
                    navigatorMain.navigateToDashboard();
                }
            });


            viewModel.getIsUploadSuccessful().observe(getViewLifecycleOwner(), isSuccessful -> {
                if (isSuccessful) {
                    navigatorMain.navigateToDashboard();
                }
            });


            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    MySignal.getInstance().toast(errorMessage);
                }
            });

        }
    }

    private void observeViewModel() {


        viewModel.getAmount().observe(getViewLifecycleOwner(), amount -> {
            String amountText = String.format(Locale.getDefault(), "%.2f", amount);
            binding.eddTransactionTXTAmount.setText(amountText);
        });

        // Create an empty adapter and set it on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.eddTransactionSPINNERCategory.setAdapter(adapter);

        // Observe the categories LiveData in the ViewModel and update the adapter
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            adapter.clear();
            adapter.addAll(categories);
            adapter.notifyDataSetChanged();

            // Set the selected category after updating the adapter
            String selectedCategory = viewModel.getSelectedCategory().getValue();
            if (selectedCategory != null) {
                binding.eddTransactionSPINNERCategory.setSelection(adapter.getPosition(selectedCategory));
            }
        });


        viewModel.getNote().observe(getViewLifecycleOwner(), note -> {
            binding.eddTransactionTXTNote.setText(note);
        });

        viewModel.getType().observe(getViewLifecycleOwner(), type -> {
            if (type.equals(Constants.NODE_EXPENSE)) {
                binding.eddTransactionRBExpense.setChecked(true);
            } else {
                binding.eddTransactionRBIncome.setChecked(true);
            }
        });
    }

    public String getSelectedRadioButtonText() {
        int checkedId = binding.eddTransactionRGExpenseIncome.getCheckedRadioButtonId();
        if (checkedId == R.id.edd_transaction_RB_expense) {
            return Constants.NODE_EXPENSE;
        } else if (checkedId == R.id.edd_transaction_RB_income) {
            return Constants.NODE_INCOME;
        } else {
            return "";
        }
    }


}




