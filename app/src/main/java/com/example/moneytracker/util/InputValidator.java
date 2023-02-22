package com.example.moneytracker.util;

import android.util.Patterns;

public class InputValidator {
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

    public static ValidationResult validateUserCredentials(String fullName, String email, String password, String confirmPassword) {
        boolean isValid = true;
        String errorMessage = "";

        if (fullName.trim().isEmpty()) {
            errorMessage = errorMessage + "Full name cannot be empty";
            isValid = false;
        }
        if (email.trim().isEmpty()) {
            errorMessage = errorMessage + "\nEmail cannot be empty";
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = errorMessage + "\nPlease enter a valid email address";
            isValid = false;
        }
        if (password.trim().isEmpty()) {
            errorMessage = errorMessage + "\nPassword cannot be empty";
            isValid = false;
        } else if (password.length() < 6) {
            errorMessage = errorMessage + "\nPassword must be at least 6 characters";
            isValid = false;
        }
        if (!password.equals(confirmPassword)) {
            errorMessage = errorMessage + "\nPasswords do not match";
            isValid = false;
        }

        return new ValidationResult(isValid, errorMessage);
    }
}
