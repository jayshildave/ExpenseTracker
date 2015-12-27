package com.jd.app.android.expensetracker.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jd.app.android.expensetracker.R;
import com.jd.app.android.expensetracker.entity.Expense;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

}
