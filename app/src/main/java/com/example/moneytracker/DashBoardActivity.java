package com.example.moneytracker;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.load.Options;
import com.example.moneytracker.databinding.ActivityDashBoardBinding;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;


public class DashBoardActivity extends AppCompatActivity implements Navigator{


    private ActivityDashBoardBinding binding;


    private Fragment currentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.dahsboard_fragmentContainer);
    }

    private FragmentManager.FragmentLifecycleCallbacks fragmentListener = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
//            updateUi();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyViewModel viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        viewModel.setNavigator(this);


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.dahsboard_fragmentContainer, new FragmentDashboard())
                    .commit();
        }

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentListener, false);

    }



    private void updateUi() {

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


    @Override
    public void showOptionsScreen() {

    }

    @Override
    public void showAddTransactionScreen() {

    }

    @Override
    public void showEditTransactionScreen(String jsonTransaction) {
        MySignal.getInstance().toast(jsonTransaction);
        launchFragment(FragmentEditTransaction.newInstance(jsonTransaction));
    }

    @Override
    public void showTransactionsScreen() {

    }

    @Override
    public void goBack() {

    }

}



