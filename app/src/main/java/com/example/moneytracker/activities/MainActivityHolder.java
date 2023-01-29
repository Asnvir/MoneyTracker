package com.example.moneytracker.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.moneytracker.R;
import com.example.moneytracker.databinding.ActivityMainBinding;
import com.example.moneytracker.fragments.AddTransactionHolder;
import com.example.moneytracker.fragments.DashboardHolder;
import com.example.moneytracker.interfaces.HasTitle;
import com.example.moneytracker.navigation.Navigator;

import java.text.DecimalFormat;
import java.util.Locale;


public class MainActivityHolder extends AppCompatActivity implements Navigator {

    private ActivityMainBinding binding;
    private MainActivityModel viewModel;
    private final FragmentManager.FragmentLifecycleCallbacks fragmentListener = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
            updateUI();
        }
    };

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);

        viewModel = new ViewModelProvider(this).get(MainActivityModel.class);

         updateIncomeBalanceExpense();
        startFragmentDashboard(savedInstanceState);
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentListener, false);
    }


    private void updateIncomeBalanceExpense() {
        viewModel.getIncome().observe(this, income -> {
            setText(binding.mainTXTIncome, R.string.income_message, income);
        });
        viewModel.getBalance().observe(this, balance -> {
            setText(binding.mainTXTBalance, R.string.balance_message, balance);
        });
        viewModel.getExpense().observe(this, expense -> {
            setText(binding.mainTXTExpense, R.string.expense_message, expense);
        });
    }


    private void updateUI() {
        Fragment fragment = getCurrentFragment();
        setTitle(fragment);

    }


    private void setTitle(Fragment fragment) {
        binding.mainToolbar.setTitleCentered(true);
        if (fragment instanceof HasTitle) {
            binding.mainToolbar.setTitle(((HasTitle) fragment).getTitle());
        } else {
            binding.mainToolbar.setTitle(R.string.main_title);
        }
    }


    private void setText(TextView textView, int resId, double value) {
        String message = getString(resId);
        String formattedMessage = String.format(Locale.getDefault(), message, value);
        textView.setText(formattedMessage);
    }

    private void startFragmentDashboard(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, new DashboardHolder())
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
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }

    @Override
    public void showOptionsScreen() {

    }

    @Override
    public void showAddTransactionScreen() {
        launchFragment(new AddTransactionHolder());
    }

    @Override
    public void showEditTransactionScreen(String jsonTransaction) {

    }

    @Override
    public void showTransactionsScreen() {
        launchFragment(new DashboardHolder());
    }

    @Override
    public void goBack() {

    }
}