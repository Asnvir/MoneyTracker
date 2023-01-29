package com.example.moneytracker.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.moneytracker.data.StorageHandler;

public class MyImage {
    @SuppressLint("StaticFieldLeak")
    private static MyImage instance;


    private MyImage() {

    }

    public static void init() {
        if (instance == null) {
            instance = new MyImage();
        }
    }

    public static MyImage getInstance() {
        return instance;
    }

    public void loadCategoryImage(Context context, ImageView imageView, String categoryName, ProgressBar progressBar) {
        String category = categoryName.toLowerCase() + ".png";
        String baseRef = "images/categories";
        downloadFileFromStorage(baseRef, category, image -> setImage(context, imageView, image, progressBar));
    }

    private void downloadFileFromStorage(String baseRef, String category, StorageHandler.ImageCallback callback) {
        StorageHandler.getInstance().downloadFile(baseRef, category, callback);
    }

    private void setImage(Context context, ImageView imageView, Bitmap image, ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
        Glide.with(context).load(image).override(110,110).into(imageView);
    }



}





