package com.jd.app.android.expensetracker.entity;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Expense
 * <p/>
 * Created by Jayshil Dave
 * 14/11/15
 * <p/>
 */
public class Expense extends SugarRecord<Expense> {

    @Ignore
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

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

    long time;

    double price;

    ExpenseType expenseType;

    String expenseTypeMiscellaneous;

    String comments;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Expense{" +
                ", time='" + simpleDateFormat.format(time) + '\'' +
                ", price=" + price +
                ", expenseType=" + expenseType +
                ", expenseTypeMiscellaneous='" + expenseTypeMiscellaneous + '\'' +
                ", comments='" + comments + '\'' +
                "} \n";
    }
}
