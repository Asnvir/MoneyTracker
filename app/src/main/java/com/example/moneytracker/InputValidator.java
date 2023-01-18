package com.example.moneytracker;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class InputValidator {
    private EditText txt_amount;
    private EditText txt_note;
    private CheckBox chbx_expense;
    private CheckBox chbx_income;
    private Spinner spnr_category;

    public InputValidator(EditText txt_amount, EditText txt_note, Spinner spnr_category, CheckBox chbx_expense, CheckBox chbx_income) {
        this.txt_amount = txt_amount;
        this.txt_note = txt_note;
        this.spnr_category = spnr_category;
        this.chbx_expense = chbx_expense;
        this.chbx_income = chbx_income;
    }

    public boolean isValidInput() {
        boolean isValid = true;
        String amount = txt_amount.getText().toString();
        String note = txt_note.getText().toString();
        String category = spnr_category.getSelectedItem().toString();
        boolean isExpenseChecked = chbx_expense.isChecked();
        boolean isIncomeChecked = chbx_income.isChecked();
        String errorMessage = "";
        if (!isPositiveDouble(amount)) {
            errorMessage = errorMessage + "Please enter a valid amount\n";
            isValid = false;
        }
        if (note.isEmpty()) {
            errorMessage = errorMessage + "Please enter a valid note\n";
            isValid = false;
        }
        if (category.equals("Select")) {
            errorMessage = errorMessage + "Please select a valid category\n";
            isValid = false;
        }
        if (!isExpenseChecked && !isIncomeChecked) {
            errorMessage = errorMessage + "Please select an expense or income\n";
            isValid = false;
        }
        if (!isValid) {
            Toast.makeText(txt_amount.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    private boolean isPositiveDouble(String number) {
        try {
            double value = Double.parseDouble(number);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
