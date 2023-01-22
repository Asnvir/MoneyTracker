package com.example.moneytracker;

import com.google.firebase.database.DataSnapshot;


public class DataRetriever {
    public interface Callback {
        void onDataReceived(DataSnapshot dataSnapshot);
    }

    public static void downloadData(Callback callback) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.downloadTransaction(dataSnapshot -> callback.onDataReceived(dataSnapshot));
    }
}
