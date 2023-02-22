package com.example.moneytracker.screens.startScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneytracker.contract.NavigatorStart;
import com.example.moneytracker.databinding.FragmentWelcomeBinding;


public class WelcomeFragment extends Fragment {
    private FragmentWelcomeBinding binding;
    private WelcomeViewModel viewModel;
    private NavigatorStart navigatorStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WelcomeViewModel.class);
        navigatorStart = NavigatorStart.getNavigator(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerActions();
        observeViewModel();
    }

    private void registerActions() {
        binding.startBTNLogin.setOnClickListener(v -> viewModel.goToLogin());
        binding.startBTNRegister.setOnClickListener(v -> viewModel.goToRegister());
    }

    private void observeViewModel() {
        viewModel.getLoginLiveData().observe(getViewLifecycleOwner(), value -> {
            if (value) {
                viewModel.onLoginNavigationComplete();
                navigatorStart.navigateToLogin();
            }
        });

        viewModel.getRegisterLiveData().observe(getViewLifecycleOwner(), value -> {
            if (value) {
                viewModel.onRegisterNavigationComplete();
                navigatorStart.navigateToRegister();
            }
        });
    }
}
