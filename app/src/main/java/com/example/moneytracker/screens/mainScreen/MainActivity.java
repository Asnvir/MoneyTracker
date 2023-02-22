package com.example.moneytracker.screens.mainScreen;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.moneytracker.R;
import com.example.moneytracker.contract.CustomAction;
import com.example.moneytracker.contract.HasCustomAction;
import com.example.moneytracker.contract.HasCustomTitle;
import com.example.moneytracker.contract.NavigatorMain;
import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.databinding.ActivityMainBinding;
import com.example.moneytracker.screens.startScreen.StartActivity;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigatorMain {

    private ActivityMainBinding binding;
    private FragmentManager.FragmentLifecycleCallbacks fragmentListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);
        startFragmentDashboard(savedInstanceState);


        fragmentListener = new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                updateUi(binding.mainToolbar.getMenu());
            }
        };

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentListener, false);
    }

    private void startFragmentDashboard(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, new DashboardFragment())
                    .commit();
        }
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

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        updateUi(menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateUi(Menu menu) {
        Fragment currentFragment = getCurrentFragment();

        if (currentFragment instanceof HasCustomTitle) {
            binding.mainToolbar.setTitle(getString(((HasCustomTitle) currentFragment).getTitleRes()));
        } else {
            binding.mainToolbar.setTitle(getString(R.string.app_name));
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }


            menu.clear();

            if (currentFragment instanceof HasCustomAction) {
                List<CustomAction> customActions = ((HasCustomAction) currentFragment).getCustomActions();

                for (CustomAction customAction : customActions) {
                    MenuItem customMenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, customAction.getTextRes());
                    customMenuItem.setIcon(customAction.getIconRes());
                    customMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    customMenuItem.setOnMenuItemClickListener(item -> {
                        customAction.getOnCustomAction().run();
                        return true;
                    });
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentListener);
    }

    @Override
    public void navigateToDashboard() {
        launchFragment(new DashboardFragment());
    }

    @Override
    public void navigateToAddTransaction() {
        launchFragment(new AddTransactionFragment());
    }

    @Override
    public void navigateToEditTransaction(TransactionModel transactionModel) {
        launchFragment(EditTransactionFragment.newInstance(transactionModel));
    }

    @Override
    public void navigateToSettings() {
        launchFragment(new AccountFragment());
    }

    @Override
    public void goToWelcome() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

}