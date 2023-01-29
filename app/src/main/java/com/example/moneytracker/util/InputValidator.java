package com.example.moneytracker.util;

public  class InputValidator {
    

    public static boolean isValidInput(double amount,String  note,String category,String type) {
        boolean isValid = true;

        String errorMessage = "";
        if (amount <= 0) {
            errorMessage = errorMessage + "Please enter a valid amount\n";
            isValid = false;
        }
        if (note.isEmpty()) {
            errorMessage = errorMessage + "Please enter a valid note\n";
            isValid = false;
        }
        if (category == null || category.equals("Select")) {
            errorMessage = errorMessage + "Please select a valid category\n";
            isValid = false;
        }
        if (type.isEmpty()) {
            errorMessage = errorMessage + "Please select an expense or income\n";
            isValid = false;
        }

        MySignal.getInstance().toast(errorMessage);

        return isValid;
    }

}
