package com.example.moneytracker;

import android.app.Application;

import com.example.moneytracker.util.MySignal;
import com.google.firebase.FirebaseApp;

public class Initializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MySignal.init(this);
        FirebaseApp.initializeApp(this);

    }
}
