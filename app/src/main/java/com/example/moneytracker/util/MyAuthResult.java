package com.example.moneytracker.util;

public class MyAuthResult {
    private boolean isSuccess;
    private String errorMessage;

    public MyAuthResult(boolean isSuccess, String errorMessage) {
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
