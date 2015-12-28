package com.jd.app.android.expensetracker.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jd.app.android.expensetracker.R;
import com.jd.app.android.expensetracker.entity.Expense;
import com.orm.query.Select;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    Spinner expenseTypeSpinner;

    TextInputLayout expenseMiscellaneousInputLayout;

    TextView dateTextView;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    EditText priceEditText;

    EditText commentsEditText;

    EditText miscellaneousTypeEditText;
    private long expenseId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        setTitle(getString(R.string.add_expense_title));
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUI();

        expenseId = getIntent().getLongExtra("expenseId", expenseId);

        if (expenseId > 0) {
            FetchExpenseAsyncTask fetchExpenseAsyncTask = new FetchExpenseAsyncTask();
            fetchExpenseAsyncTask.execute(expenseId);
        }
    }

    void updateUI() {
        expenseMiscellaneousInputLayout = (TextInputLayout) findViewById(R.id.expense_miscellaneous_text_input_layout);

        expenseTypeSpinner = (Spinner) findViewById(R.id.expense_type_spinner);
        expenseTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] expenseTypeArray = getResources().getStringArray(R.array.expense_type_array);
                if (position == expenseTypeArray.length - 1) {
                    expenseMiscellaneousInputLayout.setVisibility(View.VISIBLE);
                } else {
                    expenseMiscellaneousInputLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        dateTextView = (TextView) findViewById(R.id.date_textview);

        String date = simpleDateFormat.format(new Date());
        date = getString(R.string.date_label, date);

        updateDateText(date);

        priceEditText = (EditText) findViewById(R.id.expense_price_edittext);

        miscellaneousTypeEditText = (EditText) findViewById(R.id.expense_miscellaneous_edittext);

        commentsEditText = (EditText) findViewById(R.id.expense_comment_edittext);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" + (monthOfYear + 1) + "/" + year;
        date = getString(R.string.date_label, date);
        updateDateText(date);
    }

    void updateDateText(String text) {
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddExpenseActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMaxDate(Calendar.getInstance());
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, 5, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        dateTextView.setText(spannableString);
        dateTextView.setMovementMethod(LinkMovementMethod.getInstance());
        dateTextView.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_expense_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveExpenseObject();
        }

        return super.onOptionsItemSelected(item);
    }

    void saveExpenseObject() {
        String priceText = priceEditText.getText().toString().trim();

        String commentsText = commentsEditText.getText().toString().trim();

        String miscText = miscellaneousTypeEditText.getText().toString().trim();

        String[] expenseTypeArr = getResources().getStringArray(R.array.expense_type_array);

        int selectedPos = expenseTypeSpinner.getSelectedItemPosition();

        String selectedExpenseType = expenseTypeArr[selectedPos];


        if (selectedExpenseType.equals(expenseTypeArr[expenseTypeArr.length - 1])) {
            // Misc text is compulsory in this case
            if (TextUtils.isEmpty(miscText) || miscText.length() < 5) {
                miscellaneousTypeEditText.setError(getString(R.string.miscellaneous_error));
                return;
            }
        }

        if (TextUtils.isEmpty(priceText)) {
            priceEditText.setError(getString(R.string.price_error_1));
            return;
        }

        double price = 0.0d;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            priceEditText.setError(getString(R.string.price_error_2));
        }

        String dateText = dateTextView.getText().toString().trim();
        dateText = dateText.substring(5, dateText.length());
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long time = date.getTime();

        Expense expense = new Expense();
        expense.setTime(time);
        if (expenseId >= 0) {
            expense.setId(expenseId);
        }
        expense.setComments(commentsText);
        expense.setExpenseTypeMiscellaneous(miscText);
        expense.setExpenseType(getExpenseType(selectedPos));
        expense.setPrice(price);

        SaveExpenseAsyncTask saveExpenseAsyncTask = new SaveExpenseAsyncTask();
        saveExpenseAsyncTask.execute(expense);
    }

    class SaveExpenseAsyncTask extends AsyncTask<Expense, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(getString(R.string.saving_expense_progress));
        }

        @Override
        protected Void doInBackground(Expense... params) {
            Expense expense = params[0];
            expense.save();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgress();
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    Expense.ExpenseType getExpenseType(int expenseType) {
        switch (expenseType) {
            case 0:
                return Expense.ExpenseType.TRAVEL;

            case 1:
                return Expense.ExpenseType.GROCERY;

            case 2:
                return Expense.ExpenseType.HOTEL;

            case 3:
                return Expense.ExpenseType.MOVIES;

            case 4:
                return Expense.ExpenseType.GIFTS;

            case 5:
                return Expense.ExpenseType.SHOPPING;

            case 6:
                return Expense.ExpenseType.BILLS;

            case 7:
                return Expense.ExpenseType.MISCELLANEOUS;

            default:
                return Expense.ExpenseType.MISCELLANEOUS;
        }
    }

    class FetchExpenseAsyncTask extends AsyncTask<Long, Void, Expense> {

        @Override
        protected Expense doInBackground(Long... expenseId) {
            return Select.from(Expense.class).where("id=" + expenseId[0]).first();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(getString(R.string.fetching_expense_progress));
        }

        @Override
        protected void onPostExecute(Expense expense) {
            super.onPostExecute(expense);
            dismissProgress();
            fillValues(expense);
        }
    }

    void fillValues(Expense expense) {
        miscellaneousTypeEditText.setText(expense.getExpenseTypeMiscellaneous());

        commentsEditText.setText(expense.getComments());

        priceEditText.setText(getString(R.string.price_detail_text, expense.getPrice()));

        String date = getString(R.string.date_label, simpleDateFormat.format(new Date(expense.getTime())));
        updateDateText(date);

        expenseTypeSpinner.setSelection(getExpenseTypeSpinnerPos(expense.getExpenseType()));

        if (TextUtils.isEmpty(expense.getExpenseTypeMiscellaneous())) {
            miscellaneousTypeEditText.setVisibility(View.VISIBLE);
        }
    }


    int getExpenseTypeSpinnerPos(Expense.ExpenseType expenseType) {
        switch (expenseType) {
            case TRAVEL:
                return 0;

            case GROCERY:
                return 1;

            case HOTEL:
                return 2;

            case MOVIES:
                return 3;

            case GIFTS:
                return 4;

            case SHOPPING:
                return 5;

            case BILLS:
                return 6;

            case MISCELLANEOUS:
                return 7;

            default:
                return 7;
        }
    }
}
