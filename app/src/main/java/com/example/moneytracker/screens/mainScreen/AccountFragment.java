package com.example.moneytracker.screens.mainScreen;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker.R;
import com.example.moneytracker.contract.HasCustomTitle;
import com.example.moneytracker.contract.NavigatorMain;
import com.example.moneytracker.databinding.FragmentAccountBinding;
import com.example.moneytracker.util.MySignal;


public class AccountFragment extends Fragment implements HasCustomTitle {

    private FragmentAccountBinding binding;
    private AccountViewModel viewModel;
    private NavigatorMain navigatorMain;

    public AccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        navigatorMain = NavigatorMain.getNavigator(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel = null;
        navigatorMain = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
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

        observeViewModel();
        registerButtonActions();
    }

    private void registerButtonActions() {
        binding.accountBTNLogout.setOnClickListener(v -> {
            showDialog("Logout", "Are you sure you want to logout?", () -> {
                viewModel.logout();
            });
        });

        binding.accountBTNDelete.setOnClickListener(v -> {
            showDialog("Delete Account", "Are you sure you want to delete your account?", () -> {
                viewModel.deleteAccount();
            });
        });
    }

    private void showDialog(String title, String message, Runnable onPositiveClick) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    onPositiveClick.run();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void observeViewModel() {
        viewModel.getNameLiveData().observe(getViewLifecycleOwner(), name -> {
            if (name != null) {
                binding.accountTXTName.setText(name);
            }
        });

        viewModel.getEmailLiveData().observe(getViewLifecycleOwner(), email -> {
            if (email != null) {
                binding.accountTXTEmail.setText(email);
            }
        });

        viewModel.getBalanceLiveData().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                binding.accountTXTBalance.setText(balance);
            }
        });

        viewModel.getIncomeLiveData().observe(getViewLifecycleOwner(), income -> {
            if (income != null) {
                binding.accountTXTIncome.setText(income);
            }
        });

        viewModel.getExpenseLiveData().observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                binding.accountTXTExpense.setText(expense);
            }
        });


        viewModel.getIsLogOut().observe(getViewLifecycleOwner(), value -> {
            if (value) {
                viewModel.completeLogOut();
                navigatorMain.goToWelcome();
            }
        });

        viewModel.getIsDelete().observe(getViewLifecycleOwner(), value -> {
            if (value) {
                viewModel.completeDelete();
                navigatorMain.goToWelcome();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                MySignal.getInstance().toast(errorMessage);
            }
        });
    }

    @Override
    public int getTitleRes() {
        return R.string.settings_title;
    }
}