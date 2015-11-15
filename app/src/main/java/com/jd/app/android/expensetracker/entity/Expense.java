package com.jd.app.android.expensetracker.entity;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Expense
 * <p/>
 * Created by Jayshil Dave
 * 14/11/15
 * <p/>
 */
public class Expense extends SugarRecord<Expense> {

    public enum ExpenseType {
        TRAVEL,
        GROCERY,
        HOTEL,
        MOVIES,
        GIFTS,
        SHOPPING,
        BILLS,
        MISCELLANEOUS;
    }

    String date;

    String time;

    double price;

    ExpenseType expenseType;

    String expenseTypeMiscellaneous;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    public String getExpenseTypeMiscellaneous() {
        return expenseTypeMiscellaneous;
    }

    public void setExpenseTypeMiscellaneous(String expenseTypeMiscellaneous) {
        this.expenseTypeMiscellaneous = expenseTypeMiscellaneous;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", price=" + price +
                ", expenseType=" + expenseType +
                ", expenseTypeMiscellaneous='" + expenseTypeMiscellaneous + '\'' +
                '}';
    }
}
