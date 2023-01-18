package com.example.moneytracker;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;



public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private OnItemClickListener listener;
    private FirebaseStorage firebaseStorage;
    private StorageReference imageReference;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TransactionAdapter(Context context, ArrayList<TransactionModel> transactionModelArrayList) {
        this.context = context;
        this.transactionModelArrayList = transactionModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_recycler_view,parent,false);
        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(transactionModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView note,amount,date,time,category;
        ImageView img_category;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            note = itemView.findViewById(R.id.oneitem_TXT_note);
            amount = itemView.findViewById(R.id.oneitem_TXT_amount);
            date = itemView.findViewById(R.id.oneitem_TXT_date);
            time = itemView.findViewById(R.id.oneitem_TXT_time);
            category = itemView.findViewById(R.id.oneitem_TXT_category);
            img_category = itemView.findViewById(R.id.oneitem_IMG_category);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemLongClick(position);
                        }
                    }
                    return true;
                }
            });
        }

        public void bind(TransactionModel model) {
            note.setText(model.getNote());
            amount.setText(model.getAmount());
            date.setText(model.getDate());
            String time = model.getTime();
            time = time.split(":")[0] + ":" + time.split(":")[1];
            this.time.setText(time);

            category.setText(model.getCategory());
            setImageCategory(model);
        }

        private void setImageCategory(TransactionModel model) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            String category = model.getCategory().toLowerCase() + ".png"; //food.png
            Log.d("TAG", "category: " + category);
            StorageReference imageReference = firebaseStorage.getReference()
                    .child("images").child("categories").child(category);

            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(img_category)
                            .load(uri)
                            .into(img_category);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "Error getting download URL: " + e.getMessage());
                }
            });
        }

    }

    public void removeAt(int position) {
        transactionModelArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public TransactionModel getModelAt(int position) {
        return transactionModelArrayList.get(position);
    }

}


