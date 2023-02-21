package com.example.moneytracker.startScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker.contract.NavigatorStart;
import com.example.moneytracker.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private RegisterViewModel viewModel;
    private NavigatorStart navigatorStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        navigatorStart = NavigatorStart.getNavigator(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel = null;
        navigatorStart = null;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.registerBTNRegister.setOnClickListener(v ->registerAccount());
        viewModel.getRegistrationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                viewModel.onRegistrationComplete();
                navigatorStart.navigateToDashboard();
            }
        });

        binding.registerTXTLogin.setOnClickListener(v -> viewModel.goToLogin());
        viewModel.getGoToLoginSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value) {
                    viewModel.onGoToLoginComplete();
                    navigatorStart.navigateToLogin();
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.registerPGBProgressbar.setVisibility(View.VISIBLE);
            } else {
                binding.registerPGBProgressbar.setVisibility(View.GONE);
            }
        });
    }

    private void registerAccount() {
        String fullName = binding.registerTXTName.getText().toString();
        String email = binding.registerTXTEmail.getText().toString();
        String password = binding.registerTXTPassword.getText().toString();
        String confirmPassword = binding.registerTXTConfirmPassword.getText().toString();
        viewModel.createUser(fullName, email, password, confirmPassword);
    }
}
