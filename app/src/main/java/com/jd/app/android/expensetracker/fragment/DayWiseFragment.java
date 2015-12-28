package com.jd.app.android.expensetracker.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jd.app.android.expensetracker.R;
import com.jd.app.android.expensetracker.entity.Expense;
import com.orm.query.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayWiseFragment extends BaseFragment {

    static final String ARG_TIME = "time";

    long time;

    long timeReceived;

    List<Expense> expenseList = new ArrayList<>();

    ListView dayExpenseListView;

    View fragmentView;

    DayAdapter dayAdapter;

    TextView dateTextView;

    Button previousButton;

    Button nextButton;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private OnDayWiseFragmentInteractionListener onDayWiseFragmentInteractionListener;

    public DayWiseFragment() {
        // Required empty public constructor
    }

    public static DayWiseFragment newInstance(long time) {
        DayWiseFragment fragment = new DayWiseFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TIME, time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            time = getArguments().getLong(ARG_TIME);
            timeReceived = time;
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
        dayExpenseListView = (ListView) fragmentView.findViewById(R.id.expense_list);
        dayAdapter = new DayAdapter();
        dayExpenseListView.setAdapter(dayAdapter);
        dayExpenseListView.setOnItemClickListener(new ExpenseItemClicked());

        FetchExpenseAsyncTask fetchExpenseAsyncTask = new FetchExpenseAsyncTask();
        fetchExpenseAsyncTask.execute(time);

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
        try {
            onDayWiseFragmentInteractionListener = (OnDayWiseFragmentInteractionListener ) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDayWiseFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void updateUI() {
        String dateText = simpleDateFormat.format(new Date(time));
        dateTextView.setText(dateText);
    }

    class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int tag = (Integer) v.getTag();
            if (tag == R.id.previous_button) {
                nextButton.setEnabled(true);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                calendar.add(Calendar.DATE, -1);
                time = calendar.getTimeInMillis();
            } else if (tag == R.id.next_button) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                calendar.add(Calendar.DATE, 1);
                time = calendar.getTimeInMillis();
                if (time == timeReceived) {
                    nextButton.setEnabled(false);
                }
            }
            updateUI();
            FetchExpenseAsyncTask fetchExpenseAsyncTask = new FetchExpenseAsyncTask();
            fetchExpenseAsyncTask.execute(time);
        }
    }

    class FetchExpenseAsyncTask extends AsyncTask<Long, Void, List<Expense>> {

        @Override
        protected List<Expense> doInBackground(Long... time) {
            return Select.from(Expense.class).where("time=" + time[0]).list();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(getString(R.string.fetching_expense_progress));
        }

        @Override
        protected void onPostExecute(List<Expense> expenseList) {
            super.onPostExecute(expenseList);
            dismissProgress();
            DayWiseFragment.this.expenseList = expenseList;
            dayAdapter.notifyDataSetChanged();
        }
    }

    class DayAdapter extends BaseAdapter {

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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_day, parent, false);
            }

            TextView type = (TextView) convertView.findViewById(R.id.type);

            TextView comment = (TextView) convertView.findViewById(R.id.comment);

            TextView price = (TextView) convertView.findViewById(R.id.price);

            Expense expense = expenseList.get(position);

            price.setText(getString(R.string.price_value, expense.getPrice()));

            comment.setText(expense.getComments());

            if (expense.getExpenseType().toString().equalsIgnoreCase(getString(R.string.miscellaneous_label))) {
                type.setText(getString(R.string.miscellaneous_value, expense.getExpenseTypeMiscellaneous()));
            } else {
                type.setText(expense.getExpenseType().toString());
            }

            convertView.setTag(expense);
            return convertView;
        }
    }

    class ExpenseItemClicked implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Expense expense = (Expense) view.getTag();
            onDayWiseFragmentInteractionListener.onExpenseClicked(expense.getId());
        }
    }

    public interface OnDayWiseFragmentInteractionListener {
        void onExpenseClicked(long expenseId);
    }
}
