package com.example.moneytracker.util;

import androidx.recyclerview.widget.DiffUtil;

import com.example.moneytracker.data.TransactionModel;

import java.util.List;
import java.util.Objects;

public class TransactionDiffCallback extends DiffUtil.Callback {

    private final List<TransactionModel> oldTransactionModels;
    private final List<TransactionModel> newTransactionModels;

    public TransactionDiffCallback(List<TransactionModel> oldTransactionModels, List<TransactionModel> newTransactionModels) {
        this.oldTransactionModels = oldTransactionModels;
        this.newTransactionModels = newTransactionModels;
    }

    @Override
    public int getOldListSize() {
        return oldTransactionModels.size();
    }

    @Override
    public int getNewListSize() {
        return newTransactionModels.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        TransactionModel oldTransactionModel = oldTransactionModels.get(oldItemPosition);
        TransactionModel newTransactionModel = newTransactionModels.get(newItemPosition);
        return Objects.equals(oldTransactionModel.getTransactionID(), newTransactionModel.getTransactionID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        TransactionModel oldTransactionModel = oldTransactionModels.get(oldItemPosition);
        TransactionModel newTransactionModel = newTransactionModels.get(newItemPosition);
        return oldTransactionModel.equals(newTransactionModel);
    }
}
