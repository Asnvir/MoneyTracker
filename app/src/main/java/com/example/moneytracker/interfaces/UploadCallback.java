package com.example.moneytracker.interfaces;

public interface UploadCallback {
    void onUploadSuccess();
    void onUploadError(String error);
}