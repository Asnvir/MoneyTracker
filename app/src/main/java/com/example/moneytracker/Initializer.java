package com.example.moneytracker;

import android.app.Application;

import com.example.moneytracker.data.DatabaseHandler;
import com.example.moneytracker.data.StorageHandler;
import com.example.moneytracker.util.MyImage;
import com.example.moneytracker.util.MySignal;
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
