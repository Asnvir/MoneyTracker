package com.example.moneytracker.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface DownloadCallback {
    void onDownloadSuccess(DataSnapshot dataSnapshot);
    void onDownloadError(String error);
}
