package com.jd.app.android.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jd.app.android.expensetracker.entity.Expense;

public class DashboardActivity extends AppCompatActivity {

    static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        Expense expense = new Expense();
//        expense.setDate("2015/11/14");
//        expense.setTime("20:03");
//        expense.setExpenseType(Expense.ExpenseType.GIFTS);
//        expense.setPrice(200.00);
//
//        Log.d(TAG, "Expense created is " + expense.toString());
//
//        expense.save();
//
//        Expense expense1 = Expense.findById(Expense.class, 1L);
//
//        Log.d(TAG, "Expense retrieved is " + expense1.toString());
//
//        expense1.setPrice(3000.00);
//
//        Log.d(TAG, "Expense modified is " + expense1.toString());
//
//        expense1.save();

        Expense expense2 = Expense.findById(Expense.class, 1L);

        Log.d(TAG, "Expense retrieved post modification is " + expense2.toString());
    }

    public int add() {
        return 1;
    }
}
