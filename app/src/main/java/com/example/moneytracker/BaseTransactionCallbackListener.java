package com.example.moneytracker;

import com.google.android.gms.tasks.Task;

public interface BaseTransactionCallbackListener {
    void onTransactionComplete(Task<Void> task);
    void onTransactionFailed(Task<Void> task);

}
