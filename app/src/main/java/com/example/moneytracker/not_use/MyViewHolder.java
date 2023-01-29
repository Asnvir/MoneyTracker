package com.example.moneytracker.not_use;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytracker.util.MyImage;
import com.example.moneytracker.R;
import com.example.moneytracker.Transaction;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView note, amount, date, time, category;
    private ImageView img_category;
    private ProgressBar progressBar;
    private Context context;

    public MyViewHolder(@NonNull View itemView,Context context) {
        super(itemView);
        findViews(itemView);
        this.context = context;
    }

    private void findViews(@NonNull View itemView) {
        note = itemView.findViewById(R.id.oneitem_TXT_note);
        amount = itemView.findViewById(R.id.oneitem_TXT_amount);
        date = itemView.findViewById(R.id.oneitem_TXT_date);
        time = itemView.findViewById(R.id.oneitem_TXT_time);
        category = itemView.findViewById(R.id.oneitem_TXT_category);
        img_category = itemView.findViewById(R.id.oneitem_IMG_category);
        progressBar = itemView.findViewById(R.id.oneitem_PB_loading);
    }


    public void bind(Transaction model) {
        setNote(model);
//        setAmount(model);
        setDate(model);
        setTime(model);
        setCategory(model);
        setImageCategory(context,model);
    }

    private void setNote(Transaction model) {
        note.setText(model.getNote());
    }

//    private void setAmount(TransactionModel model) {
//        amount.setText(model.getAmount());
//
//        if (model.getAmount().startsWith("+")) {
//            amount.setTextColor(Color.GREEN);
//        } else if (model.getAmount().startsWith("-")) {
//            amount.setTextColor(Color.RED);
//        } else {
//            amount.setTextColor(Color.BLACK);
//        }
//    }

    private void setDate(Transaction model) {
        date.setText(model.getDate());
    }

    private void setTime(Transaction model) {
        String time = model.getTime();
        time = time.split(":")[0] + ":" + time.split(":")[1];
        this.time.setText(time);
    }

    private void setCategory(Transaction model) {
        category.setText(model.getCategory());
    }

    private void setImageCategory(Context context, Transaction model) {
        String category = model.getCategory().toLowerCase();
        MyImage.getInstance().loadCategoryImage(context,img_category, category, progressBar);
    }



}
