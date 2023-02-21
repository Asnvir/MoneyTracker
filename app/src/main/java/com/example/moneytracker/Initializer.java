package com.example.moneytracker;

import android.app.Application;

import com.example.moneytracker.util.MySignal;
import com.google.firebase.FirebaseApp;

public class Initializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: 18.02.2023
//        MyImage.init();
        MySignal.init(this);
        FirebaseApp.initializeApp(this);
        // TODO: 18.02.2023
//        StorageHandler.init();
    }


}
