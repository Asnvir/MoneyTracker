package com.example.moneytracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.moneytracker.util.MySignal;
import com.example.moneytracker.R;
import com.example.moneytracker.TransactionAdapter;
import com.example.moneytracker.databinding.FragmentDashboardBinding;
import com.example.moneytracker.interfaces.HasTitle;
import com.example.moneytracker.navigation.FragmentHelper;
import com.example.moneytracker.navigation.Navigator;

public class DashboardHolder extends Fragment implements HasTitle {
    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private TransactionAdapter adapter;
    private Navigator navigator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        adapter = new TransactionAdapter();
        navigator = FragmentHelper.navigator(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
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
        setupRecycleView();
        setUpBottomAppBar();
        setUpAddButton();
    }

    private void setupRecycleView() {
        binding.fragmentBoardVIEWFragmentBoard.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter.setOnTransactionLongClickListener(viewModel.onTransactionLongClickListener);

        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.submitList(transactions);
        });

        if (adapter.getTransactionModelArrayList() != null) {
            binding.fragmentBoardVIEWFragmentBoard.setAdapter(adapter);
        }
    }


    private void setUpBottomAppBar() {
        binding.fragmentBoardBARBottomAppBar.setOnMenuItemClickListener(onBottomAppBarMenuItemClickListener());
    }

    private Toolbar.OnMenuItemClickListener onBottomAppBarMenuItemClickListener() {
        return item -> {
            int id = item.getItemId();
            if (id == R.id.btmmenu_item_home) {
                MySignal.getInstance().toast("HOME CLICKED");
                return true;
            }
            return false;
        };
    }


    private void setUpAddButton() {
        binding.fragmentBoardBTNAdd.setOnClickListener(v -> {
            if (navigator != null) {
                Log.d("Run","Add button clicked in dashboard fragment");
                navigator.showAddTransactionScreen();
            }
        });
    }


    @Override
    public String getTitle() {
        return viewModel.getTitle();
    }
}

//package com.example.moneytracker;

//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.moneytracker.databinding.FragmentDashboardBinding;
//import com.google.firebase.database.DataSnapshot;
//import com.google.gson.Gson;
//
//public class FragmentDashboard extends Fragment {
//
//    private FragmentDashboardBinding binding;
//    private String jsonNavigator;
//    private Navigator navigator;
//    private DashBoardViewModel viewModel;
//
//    private OnTransactionLongClickListener onTransactionLongClickListener = jsonTransaction -> observeNavigator(jsonTransaction);
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        viewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
//
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentDashboardBinding.inflate(inflater, container, false);
//        setUpRecyclerView();
//        downloadData();
//        setUpBottomAppBar();
//        setUpAddButton();
//        return binding.getRoot();
//    }
//
//
//    private void setUpRecyclerView() {
//        binding.fragmentBoardVIEWFragmentBoard.setLayoutManager(new LinearLayoutManager(this.getContext()));
//    }
//
//    private void downloadData() {
//
//        DataRetriever.downloadData(this::displayData);
//    }
//
//    private void setUpBottomAppBar() {
//        binding.fragmentBoardBARBottomAppBar.setOnMenuItemClickListener(onBottomAppBarMenuItemClickListener());
//    }
//
//    private void observeNavigator(String jsonTransaction) {
//        viewModel.getNavigator().observe(getViewLifecycleOwner(), navigator -> {
//            if (navigator != null) {
//                // use the callback here
//
//                navigator.showEditTransactionScreen(jsonTransaction);
//            }
//        });
//
//    }
//
//
//    private void displayData(DataSnapshot dataSnapshot) {
//        ModifiedData modifiedData = DataModifier.modifyData(dataSnapshot);
//        binding.fragmentBoardVIEWFragmentBoard.setAdapter(new TransactionAdapter(this.getContext(), modifiedData.getTransactions(), onTransactionLongClickListener));
//    }
//
//    private View.OnClickListener onAddButtonClickListener() {
//        return onAddButtonClick();
//    }
//
//        private void setUpAddButton() {
//        binding.fragmentBoardBTNAdd.setOnClickListener(onAddButtonClickListener());
//    }
//    private View.OnClickListener onAddButtonClick() {
//        return v -> {
//            MySignal.getInstance().toast("ADD CLICKED");
//
//        };
//    }
//
//    private Toolbar.OnMenuItemClickListener onBottomAppBarMenuItemClickListener() {
//        return this::onBottomAppBarMenuItemClick;
//    }
//
//    public boolean onBottomAppBarMenuItemClick(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.btmmenu_item_home) {
//            MySignal.getInstance().toast("HOME CLICKED");
//            return true;
//        }
//        return false;
//    }
//}
//
//
//
////package com.example.moneytracker.fragments;
////
////
////
////import android.os.Bundle;
////
////import androidx.annotation.NonNull;
////import androidx.annotation.Nullable;
////import androidx.appcompat.widget.Toolbar;
////import androidx.fragment.app.Fragment;
////import androidx.lifecycle.ViewModelProvider;
////import androidx.lifecycle.ViewModelProviders;
////import androidx.recyclerview.widget.LinearLayoutManager;
////
////import android.view.LayoutInflater;
////import android.view.MenuItem;
////import android.view.View;
////import android.view.ViewGroup;
////
////import com.example.moneytracker.data.DataModifier;
////import com.example.moneytracker.DataRetriever;
////import com.example.moneytracker.util.ModifiedData;
////import com.example.moneytracker.util.MySignal;
////import com.example.moneytracker.not_use.MyViewModel;
////import com.example.moneytracker.Navigator;
////import com.example.moneytracker.OnTransactionLongClickListener;
////import com.example.moneytracker.R;
////import com.example.moneytracker.TransactionAdapter;
////import com.example.moneytracker.databinding.FragmentDashboardBinding;
////import com.google.firebase.database.DataSnapshot;
////import com.google.gson.Gson;
////
////import java.util.Objects;
////
////public class FragmentDashboard extends Fragment {
////
////    private FragmentDashboardBinding binding;
////    private String jsonNavigator;
////    private Navigator navigator;
////    MyViewModel viewModel;
////
////    private OnTransactionLongClickListener onTransactionLongClickListener = jsonTransaction -> observeNavigator(jsonTransaction);
////
////
//////    public static FragmentDashboard newInstance() {
//////        FragmentDashboard fragment = new FragmentDashboard();
//////        Bundle args = new Bundle();
//////        fragment.setArguments(args);
//////        return fragment;
//////    }
////
////    @Override
////    public void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
////
////
////    }
////
////    @Override
////    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        binding = FragmentDashboardBinding.inflate(inflater, container, false);
////        setUpRecyclerView();
////        downloadData();
////        setUpBottomAppBar();
////        setUpAddButton();
////        return binding.getRoot();
////    }
////
////    @Override
////    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
////        super.onViewCreated(view, savedInstanceState);
////
////    }
////
////    private void setUpRecyclerView() {
////        binding.fragmentBoardVIEWFragmentBoard.setLayoutManager(new LinearLayoutManager(this.getContext()));
////    }
////
////    private void downloadData() {
////
////        DataRetriever.downloadData(this::displayData);
////    }
////
////    private void setUpBottomAppBar() {
////        binding.fragmentBoardBARBottomAppBar.setOnMenuItemClickListener(onBottomAppBarMenuItemClickListener());
////    }
////
////    private void setUpAddButton() {
////        binding.fragmentBoardBTNAdd.setOnClickListener(onAddButtonClickListener());
////    }
////
////    private void observeNavigator(String jsonTransaction) {
////        viewModel.getNavigator().observe(getViewLifecycleOwner(), navigator -> {
////            if (navigator != null) {
////                // use the callback here
////
////                navigator.showEditTransactionScreen(jsonTransaction);
////            }
////        });
////
////    }
////
////
////
////    private void displayData(DataSnapshot dataSnapshot) {
////        ModifiedData modifiedData = DataModifier.modifyData(dataSnapshot);
////
////
////
////
////        binding.fragmentBoardVIEWFragmentBoard.setAdapter(new TransactionAdapter(this.getContext(), modifiedData.getTransactions(),  onTransactionLongClickListener));
////    }
////
////    private View.OnClickListener onAddButtonClickListener() {
////        return onAddButtonClick();
////    }
////
////    private View.OnClickListener onAddButtonClick() {
////        return v -> {
////            MySignal.getInstance().toast("ADD CLICKED");
////
////        };
////    }
////
////    private Toolbar.OnMenuItemClickListener onBottomAppBarMenuItemClickListener() {
////        return this::onBottomAppBarMenuItemClick;
////    }
////
////    public boolean onBottomAppBarMenuItemClick(MenuItem item) {
////        int id = item.getItemId();
////        if (id == R.id.btmmenu_item_home) {
////            MySignal.getInstance().toast("HOME CLICKED");
////            return true;
////        } else if (id == R.id.btmmenu_item_settings) {
////            MySignal.getInstance().toast("SETTINGS CLICKED");
////            return true;
////        } else
////            return false;
////    }
////
////
////}
////
////
