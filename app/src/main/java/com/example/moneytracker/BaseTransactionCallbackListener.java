package com.example.moneytracker;

import com.google.android.gms.tasks.Task;

public interface BaseTransactionCallbackListener {
    void onTransactionUploadComplete(Task<Void> task);
    void onTransactionUploadFailed(Task<Void> task);


}
