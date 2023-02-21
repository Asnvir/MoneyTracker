package com.example.moneytracker.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
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

    public void vibrate() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
    }
}