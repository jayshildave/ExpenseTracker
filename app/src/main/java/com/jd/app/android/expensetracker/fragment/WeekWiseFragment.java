package com.jd.app.android.expensetracker.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jd.app.android.expensetracker.R;
import com.jd.app.android.expensetracker.entity.DailyExpense;
import com.jd.app.android.expensetracker.entity.Expense;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeekWiseFragment extends BaseFragment {

    static final String ARG_START_TIME = "starttime";

    static final String ARG_END_TIME = "endtime";

    long startTime;

    long endTime;

    long startTimeReceived;

    long endTimeReceived;

    List<DailyExpense> expenseList = new ArrayList<>();

    ListView weekExpenseListView;

    View fragmentView;

    WeekAdapter weekAdapter;

    TextView dateTextView;

    Button previousButton;

    Button nextButton;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public WeekWiseFragment() {
        // Required empty public constructor
    }

    public static WeekWiseFragment newInstance(long startTime, long endTime) {
        WeekWiseFragment fragment = new WeekWiseFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_START_TIME, startTime);
        args.putLong(ARG_END_TIME, endTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startTime = getArguments().getLong(ARG_START_TIME);
            startTimeReceived = startTime;

            endTime = getArguments().getLong(ARG_END_TIME);
            endTimeReceived = endTime;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_day_wise, container, false);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        weekExpenseListView = (ListView) fragmentView.findViewById(R.id.expense_list);
        weekAdapter = new WeekAdapter();
        weekExpenseListView.setAdapter(weekAdapter);

        FetchExpenseAsyncTask fetchExpenseAsyncTask = new FetchExpenseAsyncTask();
        fetchExpenseAsyncTask.execute(startTime, endTime);

        dateTextView = (TextView) fragmentView.findViewById(R.id.date_textview);

        previousButton = (Button) fragmentView.findViewById(R.id.previous_button);
        previousButton.setTag(R.id.previous_button);
        previousButton.setOnClickListener(new ButtonClickListener());

        nextButton = (Button) fragmentView.findViewById(R.id.next_button);
        nextButton.setTag(R.id.next_button);
        nextButton.setEnabled(false);
        nextButton.setOnClickListener(new ButtonClickListener());

        updateUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void updateUI() {
        String dateText = simpleDateFormat.format(new Date(startTime)) + " - " + simpleDateFormat.format(new Date(endTime));
        dateTextView.setText(dateText);
    }

    class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int tag = (Integer) v.getTag();
            if (tag == R.id.previous_button) {
                nextButton.setEnabled(true);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                startTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_YEAR, 6);
                endTime = calendar.getTimeInMillis();
            } else if (tag == R.id.next_button) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(endTime);
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_YEAR, -6);
                startTime = calendar.getTimeInMillis();
                if (endTime == endTimeReceived) {
                    nextButton.setEnabled(false);
                }
            }
            updateUI();
            FetchExpenseAsyncTask fetchExpenseAsyncTask = new FetchExpenseAsyncTask();
            fetchExpenseAsyncTask.execute(startTime, endTime);
        }
    }

    class FetchExpenseAsyncTask extends AsyncTask<Long, Void, List<DailyExpense>> {

        @Override
        protected List<DailyExpense> doInBackground(Long... time) {

            List<DailyExpense> dailyExpenseList = new ArrayList<>();
            List<Expense> expenseList = Select.from(Expense.class).where(Condition.prop("time").gt(time[0]-1),
                    Condition.prop("time").lt(time[1]+1)).orderBy("time DESC").list();

            DailyExpense dailyExpense = null;
            double dailyPrice = 0.0d;
            for (Expense expense:expenseList) {
                long expenseTime = expense.getTime();
                String expenseDate = simpleDateFormat.format(new Date(expenseTime));
                if (dailyExpense == null || !dailyExpense.getDate().equalsIgnoreCase(expenseDate)) {
                    if (dailyExpense !=null) {
                        dailyExpense.setDailyPrice(dailyPrice);
                        dailyExpenseList.add(dailyExpense);
                    }
                    dailyExpense = new DailyExpense();
                    dailyExpense.setDate(expenseDate);
                    dailyPrice = 0.0d;
                }
                dailyPrice = dailyPrice + expense.getPrice();
            }

            if (dailyExpense != null) {
                dailyExpense.setDailyPrice(dailyPrice);
                dailyExpenseList.add(dailyExpense);
            }
            return dailyExpenseList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(getString(R.string.fetching_expense_progress));
        }

        @Override
        protected void onPostExecute(List<DailyExpense> expenseList) {
            super.onPostExecute(expenseList);
            dismissProgress();
            WeekWiseFragment.this.expenseList = expenseList;
            weekAdapter.notifyDataSetChanged();
        }
    }

    class WeekAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return expenseList.size();
        }

        @Override
        public Object getItem(int position) {
            return expenseList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_week, parent, false);
            }

            TextView date = (TextView) convertView.findViewById(R.id.date);

            TextView price = (TextView) convertView.findViewById(R.id.price);

            DailyExpense expense = expenseList.get(position);

            price.setText(getString(R.string.price_value, expense.getDailyPrice()));

            date.setText(expense.getDate());

            return convertView;
        }
    }
}
