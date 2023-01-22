package com.example.moneytracker;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytracker.databinding.FragmentDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.util.Objects;

public class FragmentDashboard extends Fragment {

    private FragmentDashboardBinding binding;
    private String jsonNavigator;
    private Navigator navigator;
    MyViewModel viewModel;

    private OnTransactionLongClickListener onTransactionLongClickListener = jsonTransaction -> observeNavigator(jsonTransaction);


//    public static FragmentDashboard newInstance() {
//        FragmentDashboard fragment = new FragmentDashboard();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        setUpRecyclerView();
        downloadData();
        setUpBottomAppBar();
        setUpAddButton();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setUpRecyclerView() {
        binding.fragmentBoardVIEWFragmentBoard.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void downloadData() {

        DataRetriever.downloadData(this::displayData);
    }

    private void setUpBottomAppBar() {
        binding.fragmentBoardBARBottomAppBar.setOnMenuItemClickListener(onBottomAppBarMenuItemClickListener());
    }

    private void setUpAddButton() {
        binding.fragmentBoardBTNAdd.setOnClickListener(onAddButtonClickListener());
    }

    private void observeNavigator(String jsonTransaction) {
        viewModel.getNavigator().observe(getViewLifecycleOwner(), navigator -> {
            if (navigator != null) {
                // use the callback here

                navigator.showEditTransactionScreen(jsonTransaction);
            }
        });

    }



    private void displayData(DataSnapshot dataSnapshot) {
        ModifiedData modifiedData = DataModifier.modifyData(dataSnapshot);




        binding.fragmentBoardVIEWFragmentBoard.setAdapter(new TransactionAdapter(this.getContext(), modifiedData.getTransactions(),  onTransactionLongClickListener));
    }

    private View.OnClickListener onAddButtonClickListener() {
        return onAddButtonClick();
    }

    private View.OnClickListener onAddButtonClick() {
        return v -> {
            MySignal.getInstance().toast("ADD CLICKED");

        };
    }

    private Toolbar.OnMenuItemClickListener onBottomAppBarMenuItemClickListener() {
        return this::onBottomAppBarMenuItemClick;
    }

    public boolean onBottomAppBarMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btmmenu_item_home) {
            MySignal.getInstance().toast("HOME CLICKED");
            return true;
        } else if (id == R.id.btmmenu_item_settings) {
            MySignal.getInstance().toast("SETTINGS CLICKED");
            return true;
        } else
            return false;
    }


}


