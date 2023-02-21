package com.example.moneytracker.mainScreen;

import android.app.AlertDialog;
import android.graphics.Color;
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

import com.example.moneytracker.R;
import com.example.moneytracker.TransactionAdapter;
import com.example.moneytracker.contract.CustomAction;
import com.example.moneytracker.contract.HasCustomAction;
import com.example.moneytracker.contract.HasCustomTitle;
import com.example.moneytracker.contract.NavigatorMain;
import com.example.moneytracker.databinding.FragmentDashboardBinding;
import com.example.moneytracker.util.Constants;
import com.example.moneytracker.util.MySignal;
import com.example.moneytracker.util.SortCriteria;


public class DashboardFragment extends Fragment implements HasCustomAction, HasCustomTitle {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private TransactionAdapter adapter;
    private NavigatorMain navigatorMain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        adapter = new TransactionAdapter(viewModel.getTransactionActionListener());
        navigatorMain = NavigatorMain.getNavigator(this);
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
        initTopBalance();
        initRecycleView();
        initUpAddButton();
        trackActionTransaction();
    }

    private void trackActionTransaction() {
        viewModel.getIsEditTransaction().observe(getViewLifecycleOwner(),value -> {
            if(value){
                viewModel.onEditTransactionComplete();
                navigatorMain.navigateToEditTransaction(viewModel.getTransactionToEdit());
            }
        });
    }


    @Override
    public CustomAction getCustomAction() {
        return new CustomAction(R.drawable.ic_sort, R.string.action_sort, this::onSortPressed);
    }

    private void onSortPressed() {
        String[] sortingOptions = getResources().getStringArray(R.array.sort_options);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.sort_title));
        builder.setItems(sortingOptions, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    Log.d(Constants.DashboardFragment_TAG, "Sorting by DATE_ASCENDING");
                    viewModel.setSortingCriteria(SortCriteria.DATE_ASCENDING);
                    break;
                case 1:
                    Log.d(Constants.DashboardFragment_TAG, "Sorting by DATE_DESCENDING");
                    viewModel.setSortingCriteria(SortCriteria.DATE_DESCENDING);
                    break;
                case 2:
                    Log.d(Constants.DashboardFragment_TAG, "Sorting by AMOUNT_ASCENDING");
                    viewModel.setSortingCriteria(SortCriteria.AMOUNT_ASCENDING);
                    break;
                case 3:
                    Log.d(Constants.DashboardFragment_TAG, "Sorting by AMOUNT_DESCENDING");
                    viewModel.setSortingCriteria(SortCriteria.AMOUNT_DESCENDING);
                    break;
                case 4:
                    Log.d(Constants.DashboardFragment_TAG, "Sorting by CATEGORY_ASCENDING");
                    viewModel.setSortingCriteria(SortCriteria.CATEGORY_ASCENDING);
                    break;
                case 5:
                    Log.d(Constants.DashboardFragment_TAG, "Sorting by CATEGORY_DESCENDING");
                    viewModel.setSortingCriteria(SortCriteria.CATEGORY_DESCENDING);
                    break;
                case 6:
                    Log.d(Constants.DashboardFragment_TAG, "Sorting by TYPE_ASCENDING");
                    viewModel.setSortingCriteria(SortCriteria.TYPE_ASCENDING);
                    break;
                case 7:
                    Log.d(Constants.DashboardFragment_TAG, "Sorting by TYPE_DESCENDING");
                    viewModel.setSortingCriteria(SortCriteria.TYPE_DESCENDING);
                    break;
            }
        });
        builder.show();
    }


    @Override
    public int getTitleRes() {
        return R.string.dashboard_title;
    }


    private void initTopBalance() {
        binding.dashBoardTXTIncome.setTextColor(Color.GREEN);
        binding.dashBoardTXTBalance.setTextColor(Color.BLACK);
        binding.dashBoardTXTExpense.setTextColor(Color.RED);

        viewModel.getIncomeLiveData().observe(getViewLifecycleOwner(), income -> {
            binding.dashBoardTXTIncome.setText(income);
        });

        viewModel.getBalanceLiveData().observe(getViewLifecycleOwner(), balance -> {
            binding.dashBoardTXTBalance.setText(balance);
        });

        viewModel.getExpenseLiveData().observe(getViewLifecycleOwner(), expense -> {
            binding.dashBoardTXTExpense.setText(expense);
        });

    }

    private void initRecycleView() {
        binding.dashBoardRVRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.dashBoardRVRecycleView.setAdapter(adapter);
        viewModel.getTransactionsLiveData().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
        });

    }

    private void initUpAddButton() {
        binding.dashBoardBTNAdd.setOnClickListener(v -> {
            if (navigatorMain != null) {
                Log.d("Run", "Add button clicked in dashboard fragment");
                navigatorMain.navigateToAddTransaction();
            }
        });
    }

}

