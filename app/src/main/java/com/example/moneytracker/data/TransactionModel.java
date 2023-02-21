package com.example.moneytracker.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.moneytracker.util.Constants;
import com.google.firebase.database.PropertyName;

import java.util.Objects;


public class TransactionModel implements Parcelable {
    @PropertyName(Constants.NODE_TRANSACTION_ID)
    private String transactionID;
    @PropertyName(Constants.NODE_DATE)
    private String date;
    @PropertyName(Constants.NODE_TIME)
    private String time;
    @PropertyName(Constants.NODE_AMOUNT)
    private double amount;
    @PropertyName(Constants.NODE_CATEGORY)
    private String category;
    @PropertyName(Constants.NODE_NOTE)
    private String note;
    @PropertyName(Constants.NODE_TYPE)
    private String type;


    public TransactionModel() {
    }


    protected TransactionModel(Parcel in) {
        transactionID = in.readString();
        date = in.readString();
        time = in.readString();
        amount = in.readDouble();
        category = in.readString();
        note = in.readString();
        type = in.readString();
    }

    public static final Creator<TransactionModel> CREATOR = new Creator<TransactionModel>() {
        @Override
        public TransactionModel createFromParcel(Parcel in) {
            return new TransactionModel(in);
        }

        @Override
        public TransactionModel[] newArray(int size) {
            return new TransactionModel[size];
        }
    };

    public TransactionModel setTransactionID(String transactionID) {
        this.transactionID = transactionID;
        return this;
    }

    public TransactionModel setDate(String date) {
        this.date = date;
        return this;
    }

    public TransactionModel setTime(String time) {
        this.time = time;
        return this;
    }

    public TransactionModel setAmount(double amount) {
        this.amount = (double) Math.round(amount * 100) / 100;
        return this;
    }

    public TransactionModel setCategory(String category){
        this.category = category;
        return this;
    }

    public TransactionModel setNote(String note) {
        this.note = note;
        return this;
    }

    public TransactionModel setType(String type) {
        this.type = type;
        return this;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionModel that = (TransactionModel) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(transactionID, that.transactionID) &&
                Objects.equals(date, that.date) &&
                Objects.equals(time, that.time) &&
                Objects.equals(category, that.category) &&
                Objects.equals(note, that.note) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionID, date, time, amount, category, note, type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(transactionID);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeDouble(amount);
        dest.writeString(category);
        dest.writeString(note);
        dest.writeString(type);
    }

    public boolean equalsIgnoreDateTime(TransactionModel other) {
        if (other == null) {
            return false;
        }

        return Double.compare(amount, other.amount) == 0 &&
                Objects.equals(category, other.category) &&
                Objects.equals(note, other.note) &&
                Objects.equals(type, other.type);
    }

}
