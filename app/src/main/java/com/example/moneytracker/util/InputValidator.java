package com.example.moneytracker.util;

public  class InputValidator {
    public static ValidationResult validateInput(double amount, String note, String category, String type) {
        boolean isValid = true;
        String errorMessage = "";

        if (amount <= 0) {
            errorMessage = errorMessage + "Please enter a valid amount";
            isValid = false;
        }
        if (note.isEmpty()) {
            errorMessage = errorMessage + "\nPlease enter a valid note";
            isValid = false;
        }
        if (category == null || category.equals("Select")) {
            errorMessage = errorMessage + "\nPlease select a valid category";
            isValid = false;
        }
        if (type.isEmpty()) {
            errorMessage = errorMessage + "\nPlease select an expense or income";
            isValid = false;
        }

        return new ValidationResult(isValid, errorMessage);
    }
}
