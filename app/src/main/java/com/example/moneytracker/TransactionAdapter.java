package com.example.moneytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class TransactionAdapter extends RecyclerView.Adapter<MyViewHolder> {


    private  Context context;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemLongClick(int position);
    }

    public TransactionAdapter(Context context, ArrayList<TransactionModel> transactionModelArrayList) {
        this.transactionModelArrayList = transactionModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_recycler_view,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(view, context);
        setOnLongClickListener(myViewHolder);
        return myViewHolder;
    }

    private void setOnLongClickListener(final MyViewHolder myViewHolder) {
        myViewHolder.itemView.setOnLongClickListener(v -> {
            int position = myViewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemLongClick(position);
            }
            return true;
        });
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(transactionModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList.size();
    }

    public TransactionModel getModelAt(int position) {
        return transactionModelArrayList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }




}
