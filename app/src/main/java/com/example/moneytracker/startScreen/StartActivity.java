package com.example.moneytracker.startScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.moneytracker.R;
import com.example.moneytracker.databinding.ActivityLaunchBinding;
import com.example.moneytracker.mainScreen.MainActivity;
import com.example.moneytracker.contract.NavigatorStart;
import com.example.moneytracker.util.Constants;

public class StartActivity extends AppCompatActivity implements NavigatorStart {

    private ActivityLaunchBinding binding;

    private final FragmentManager.FragmentLifecycleCallbacks fragmentListener = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
            // TODO: 17.02.2023 нужно ли вообще
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startFragment(savedInstanceState);
    }

    private void startFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.start_fragment_container, new WelcomeFragment())
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
                .replace(R.id.start_fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentListener);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
    }

    @Override
    public void navigateToStart() {
        launchFragment(new WelcomeFragment());
    }

    @Override
    public void navigateToLogin() {
        launchFragment(new LoginFragment());
    }

    @Override
    public void navigateToDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void navigateToRegister() {
        launchFragment(new RegisterFragment());
    }

    @Override
    public void goBack() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            Log.d(Constants.StartActivityGoBack_TAG, "Popping back stack");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(Constants.OnBackPressed_TAG, "Back button pressed");
        goBack();
    }


}