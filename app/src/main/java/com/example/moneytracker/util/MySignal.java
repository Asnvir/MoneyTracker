package com.example.moneytracker.util;

import android.content.Context;
import android.widget.Toast;

public class MySignal {

    private static MySignal instance;
    private final Context context;

    private MySignal(Context context) {
        this.context = context.getApplicationContext();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new MySignal(context);
        }
    }

    public static MySignal getInstance() {
        return instance;
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}