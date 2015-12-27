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
    }

    public int add() {
        return 1;
    }
}
