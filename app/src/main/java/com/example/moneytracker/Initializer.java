package com.example.moneytracker;

import android.app.Application;

import com.example.moneytracker.MyImage;
import com.example.moneytracker.MySignal;
import com.example.moneytracker.DatabaseHandler;
import com.example.moneytracker.StorageHandler;
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
