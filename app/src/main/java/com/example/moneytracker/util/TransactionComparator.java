package com.example.moneytracker.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.moneytracker.data.TransactionModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;

public class TransactionComparator implements Comparator<TransactionModel> {

    private final SortCriteria sortCriteria;

    public TransactionComparator(SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compare(TransactionModel o1, TransactionModel o2) {
        int result = 0;

        // Compare date
        if (sortCriteria == SortCriteria.DATE_ASCENDING || sortCriteria == SortCriteria.DATE_DESCENDING) {
            try {
                LocalDateTime dateTime1 = LocalDateTime.parse(o1.getDate() + " " + o1.getTime(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                LocalDateTime dateTime2 = LocalDateTime.parse(o2.getDate() + " " + o2.getTime(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                result = dateTime1.compareTo(dateTime2);
            } catch (DateTimeParseException e) {
                MySignal.getInstance().toast(e.getMessage());
            }
        }

        // Compare amount
        if (result == 0 && (sortCriteria == SortCriteria.AMOUNT_ASCENDING || sortCriteria == SortCriteria.AMOUNT_DESCENDING)) {
            double amount1 = o1.getType().equals(Constants.NODE_EXPENSE) ? -o1.getAmount() : o1.getAmount();
            double amount2 = o2.getType().equals(Constants.NODE_EXPENSE) ? -o2.getAmount() : o2.getAmount();
            result = Double.compare(amount1, amount2);
        }


        // Compare category
        if (result == 0 && (sortCriteria == SortCriteria.CATEGORY_ASCENDING || sortCriteria == SortCriteria.CATEGORY_DESCENDING)) {
            result = o1.getCategory().compareTo(o2.getCategory());
        }

        // Compare type
        if (result == 0 && (sortCriteria == SortCriteria.TYPE_ASCENDING || sortCriteria == SortCriteria.TYPE_DESCENDING)) {
            result = o1.getType().compareTo(o2.getType());
        }

        // Reverse the result if descending order
        if (sortCriteria == SortCriteria.DATE_DESCENDING ||
                sortCriteria == SortCriteria.AMOUNT_DESCENDING ||
                sortCriteria == SortCriteria.CATEGORY_DESCENDING ||
                sortCriteria == SortCriteria.TYPE_DESCENDING) {
            result = -result;
        }

        return result;
    }
}
