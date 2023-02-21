package com.example.moneytracker.mainScreen;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.example.moneytracker.R;
import com.example.moneytracker.contract.CustomAction;
import com.example.moneytracker.contract.HasCustomAction;
import com.example.moneytracker.contract.HasCustomTitle;
import com.example.moneytracker.contract.NavigatorMain;
import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.databinding.ActivityMainBinding;
import com.example.moneytracker.util.Constants;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigatorMain {

    private ActivityMainBinding binding;
    private Fragment currentFragment;
    private FragmentManager.FragmentLifecycleCallbacks fragmentListener ;

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);
        startFragmentDashboard(savedInstanceState);


        fragmentListener= new FragmentManager.FragmentLifecycleCallbacks() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        currentFragment = getCurrentFragment();

        if (currentFragment instanceof HasCustomTitle) {
            binding.mainToolbar.setTitle(getString(((HasCustomTitle) currentFragment).getTitleRes()));
        } else {
            binding.mainToolbar.setTitle(getString(R.string.app_name));
        }

        // Clear all menu items before adding new ones
        menu.clear();

        if (currentFragment instanceof HasCustomAction) {
            CustomAction customAction = ((HasCustomAction) currentFragment).getCustomAction();
            if (customAction != null) {
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
    public void goBack() {

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
}