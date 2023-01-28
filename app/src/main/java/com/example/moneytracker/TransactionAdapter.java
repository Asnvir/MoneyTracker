package com.example.moneytracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytracker.databinding.OneItemRecyclerViewBinding;
import com.example.moneytracker.util.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TransactionAdapter extends ListAdapter<TransactionModel, TransactionAdapter.TransactionViewHolder> {


    private Context context;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private OnTransactionLongClickListener onTransactionLongClickListener;

//    public TransactionAdapter(Context context, ArrayList<TransactionModel> transactionModelArrayList, OnTransactionLongClickListener onTransactionLongClickListener) {
////        this.context = context;
//        this.transactionModelArrayList = transactionModelArrayList;
//        this.onTransactionLongClickListener = onTransactionLongClickListener;
//
//    }

    public TransactionAdapter() {
        super(DIFF_CALLBACK);
    }
    private static final DiffUtil.ItemCallback<TransactionModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<TransactionModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull TransactionModel oldItem, @NonNull TransactionModel newItem) {
            return Objects.equals(oldItem.getTransactionID(), newItem.getTransactionID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TransactionModel oldItem, @NonNull TransactionModel newItem) {
            return oldItem.equals(newItem);
        }
    };


    public void setTransactions(List<TransactionModel> transactionModelArrayList) {
        this.transactionModelArrayList = (ArrayList<TransactionModel>) transactionModelArrayList;
    }

    public ArrayList<TransactionModel> getTransactionModelArrayList() {
        return transactionModelArrayList;
    }

    public void setOnTransactionLongClickListener(OnTransactionLongClickListener onTransactionLongClickListener) {
        this.onTransactionLongClickListener = onTransactionLongClickListener;
    }

//    @NonNull
//    @Override
//    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_recycler_view, parent, false);
//        context = parent.getContext();
//        return new TransactionViewHolder(view);
//    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_recycler_view, parent, false);
        context = parent.getContext();
        return new TransactionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int currentPosition = holder.getBindingAdapterPosition();
                TransactionModel model = transactionModelArrayList.get(currentPosition);
                Gson gson = new Gson();
                String jsonTransaction = gson.toJson(model);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select an option");
                builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            onTransactionLongClickListener.onTransactionLongClick(jsonTransaction);
                            break;
                        case 1:
                            // Delete option selected
                            // code to delete the transactionModel
                            break;
                    }
                });
                builder.create().show();

                return true;
            }
        });
        TransactionModel transactionModel = transactionModelArrayList.get(position);
        setNote(holder, transactionModel.getNote());
        setAmount(holder, transactionModel.getAmount(),transactionModel.getCategory());
        setDate(holder, transactionModel.getDate());
        setTime(holder, transactionModel.getTime());
        setCategory(holder, transactionModel.getCategory());
        setImageCategory(holder, transactionModel);
    }

    private void setNote(@NonNull TransactionViewHolder holder, String new_note) {
        holder.binding.oneitemTXTNote.setText(new_note);
    }

    @SuppressLint("SetTextI18n")
    private void setAmount(@NonNull TransactionViewHolder holder, Double new_amount, String new_category) {
        holder.binding.oneitemTXTAmount.setText(new_amount.toString());

        if (new_category.equalsIgnoreCase(Utils.INCOME)) {
            holder.binding.oneitemTXTAmount.setTextColor(Color.GREEN);
        } else if (new_category.equalsIgnoreCase(Utils.EXPENSE)) {
            holder.binding.oneitemTXTAmount.setTextColor(Color.RED);
        } else {
            holder.binding.oneitemTXTAmount.setTextColor(Color.BLACK);
        }
    }

    private void setDate(@NonNull TransactionViewHolder holder, String new_date) {
        holder.binding.oneitemTXTDate.setText(new_date);
    }

    private void setTime(@NonNull TransactionViewHolder holder, String new_time) {

        String time = new_time.split(":")[0] + ":" + new_time.split(":")[1];
        holder.binding.oneitemTXTTime.setText(time);
    }

    private void setCategory(@NonNull TransactionViewHolder holder, String new_category) {
        holder.binding.oneitemTXTCategory.setText(new_category);
    }

    private void setImageCategory(@NonNull TransactionViewHolder holder, TransactionModel model) {
        String category = model.getCategory().toLowerCase();
        MyImage.getInstance().loadCategoryImage(context, holder.binding.oneitemIMGCategory, category, holder.binding.oneitemPBLoading);
    }


    @Override
    public int getItemCount() {
        return transactionModelArrayList != null ? transactionModelArrayList.size() : 0;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        private final OneItemRecyclerViewBinding binding;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OneItemRecyclerViewBinding.bind(itemView);
        }


    }
}

