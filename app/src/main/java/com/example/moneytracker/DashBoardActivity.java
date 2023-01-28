package com.example.moneytracker;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.example.moneytracker.databinding.ActivityDashBoardBinding;
import com.example.moneytracker.fragments.FragmentAddTransactionViewHolder;
import com.example.moneytracker.fragments.FragmentDashboardViewHolder;
import com.example.moneytracker.navigation.Navigator;


public class DashBoardActivity extends AppCompatActivity implements Navigator {

    private ActivityDashBoardBinding binding;
    private DashBoardViewModel viewModel;
    private final FragmentManager.FragmentLifecycleCallbacks fragmentListener = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
//            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(DashBoardViewModel.class);
        updateBalanceIncome();
        startFragmentDashboard(savedInstanceState);
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentListener, false);
    }

    private void updateBalanceIncome() {
        viewModel.getIncome().observe(this, integer -> binding.dashboardTXTIncome.setText("Income: " + integer));
        viewModel.getBalance().observe(this, integer -> binding.dashboardTXTBalance.setText("Balance: " + integer));
    }

    private void startFragmentDashboard(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.dahsboard_fragmentContainer, new FragmentDashboardViewHolder())
                    .commit();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentListener);
    }


    private void launchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                )
                .addToBackStack(null)
                .replace(R.id.dahsboard_fragmentContainer, fragment)
                .commit();
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.dahsboard_fragmentContainer);
    }


    @Override
    public void showOptionsScreen() {

    }

    @Override
    public void showAddTransactionScreen() {
        launchFragment(new FragmentAddTransactionViewHolder());
    }

    @Override
    public void showEditTransactionScreen(String jsonTransaction) {

    }

    @Override
    public void showTransactionsScreen() {
        launchFragment(new FragmentDashboardViewHolder());
    }

    @Override
    public void goBack() {

    }
}