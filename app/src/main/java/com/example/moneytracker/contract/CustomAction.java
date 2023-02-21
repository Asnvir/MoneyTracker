package com.example.moneytracker.contract;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class CustomAction {
    @DrawableRes
    private final int iconRes;
    @StringRes
    private final int textRes;
    private final Runnable onCustomAction;

    public CustomAction(int iconRes, int textRes, Runnable onCustomAction) {
        this.iconRes = iconRes;
        this.textRes = textRes;
        this.onCustomAction = onCustomAction;
    }

    @DrawableRes
    public int getIconRes() {
        return iconRes;
    }

    @StringRes
    public int getTextRes() {
        return textRes;
    }

    public Runnable getOnCustomAction() {
        return onCustomAction;
    }
}
