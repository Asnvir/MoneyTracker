package com.example.moneytracker.navigation;

import androidx.fragment.app.Fragment;

public class FragmentHelper {
    public static Navigator navigator(Fragment fragment) {
        return (Navigator) fragment.requireActivity();
    }
}