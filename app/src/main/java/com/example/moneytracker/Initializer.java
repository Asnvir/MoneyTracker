package com.example.moneytracker;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class Initializer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MyImage.init();
        MySignal.init(this);
        FirebaseApp.initializeApp(this);
        DatabaseHandler.init();
        StorageHandler.init();
    }
}
