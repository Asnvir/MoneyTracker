package com.example.moneytracker.screens.mainScreen;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.moneytracker.R;
import com.example.moneytracker.databinding.OneItemRecyclerViewBinding;
import com.example.moneytracker.data.TransactionModel;
import com.example.moneytracker.util.TransactionDiffCallback;
import com.example.moneytracker.util.Constants;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnLongClickListener {

    private List<TransactionModel> currentTransactionList;
    private DashboardViewModel.TransactionActionListener actionListener;

    public TransactionAdapter(DashboardViewModel.TransactionActionListener actionListener) {
        this.actionListener = actionListener;
        currentTransactionList = new ArrayList<>();
    }

    public void setTransactions(List<TransactionModel> newTransactionList) {
        TransactionDiffCallback diffCallback = new TransactionDiffCallback(currentTransactionList, newTransactionList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        currentTransactionList = new ArrayList<>(newTransactionList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_recycler_view, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public boolean onLongClick(View v) {
        showDeleteConfirmationAlert(v);
        return true;
    }


    private void showDeleteConfirmationAlert(View v) {
        TransactionModel transactionModel = (TransactionModel) v.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are you sure you want to edit this transaction?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.onTransactionDelete(transactionModel);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransactionViewHolder transactionViewHolder = (TransactionViewHolder) holder;
        TransactionModel transactionModel = currentTransactionList.get(position);
        transactionViewHolder.bind(transactionModel);
        transactionViewHolder.binding.getRoot().setTag(transactionModel);
        transactionViewHolder.itemView.setOnLongClickListener(this);

    }

    @Override
    public int getItemCount() {
        return currentTransactionList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final OneItemRecyclerViewBinding binding;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OneItemRecyclerViewBinding.bind(itemView);
        }

        public void bind(TransactionModel transactionModel) {
            setNote(binding, transactionModel.getNote());
            setAmount(binding, transactionModel.getAmount(), transactionModel.getType());
            setDate(binding, transactionModel.getDate());
            setTime(binding, transactionModel.getTime());
            setCategory(binding, transactionModel.getCategory());
            loadImage(binding, transactionModel.getCategory());
        }

        private void setNote(@NonNull OneItemRecyclerViewBinding binding, String new_note) {
            binding.oneitemTXTNote.setText(new_note);
        }

        private void setAmount(@NonNull OneItemRecyclerViewBinding binding, Double amount, String type) {
            int color = Color.BLACK;
            String sign = "";
            switch (type) {
                case Constants.NODE_INCOME:
                    color = Color.parseColor("#006400");
                    sign = "+";
                    break;
                case Constants.NODE_EXPENSE:
                    color =Color.parseColor("#8B0000");
                    sign = "-";
                    break;
            }
            String formattedAmount = String.format(Locale.getDefault(), "%s%.2f", sign, Math.abs(amount));
            binding.oneitemTXTAmount.setText(formattedAmount);
            binding.oneitemTXTAmount.setTextColor(color);
        }


        private void setDate(@NonNull OneItemRecyclerViewBinding binding, String date) {
            binding.oneitemTXTDate.setText(date);
        }

        private void setTime(@NonNull OneItemRecyclerViewBinding binding, String time) {
            String timeToSet = time.split(":")[0] + ":" + time.split(":")[1];
            binding.oneitemTXTTime.setText(timeToSet);
        }

        private void setCategory(@NonNull OneItemRecyclerViewBinding binding, String category) {
            binding.oneitemTXTCategory.setText(category);
        }

        private void loadImage(@NonNull OneItemRecyclerViewBinding binding, String imageName) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String imagePath = Constants.IMAGES_PATH + imageName.toLowerCase() + Constants.IMAGE_EXTENSION;
            StorageReference imageRef = storage.getReference(imagePath);

            binding.oneitemPBLoading.setVisibility(View.VISIBLE);
            RequestOptions requestOptions = new RequestOptions()
                    .circleCrop()
                    .override(110, 110)
                    .error(R.drawable.default_category_image); // Set the default image here

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(binding.getRoot().getContext())
                        .asBitmap()
                        .load(uri)
                        .apply(requestOptions)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                binding.oneitemPBLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                binding.oneitemPBLoading.setVisibility(View.GONE);
                                binding.oneitemIMGCategory.setImageBitmap(resource);
                                return false;
                            }
                        })
                        .into(binding.oneitemIMGCategory);
            }).addOnFailureListener(exception -> {
                // Handle the failure to get download URL
                binding.oneitemPBLoading.setVisibility(View.GONE);
                binding.oneitemIMGCategory.setImageResource(R.drawable.default_category_image);
            });
        }

    }
}





