package com.jd.app.android.expensetracker.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jd.app.android.expensetracker.R;
import com.jd.app.android.expensetracker.entity.Expense;
import com.jd.app.android.expensetracker.fragment.DayWiseFragment;
import com.jd.app.android.expensetracker.fragment.WeekWiseFragment;
import com.jd.app.android.expensetracker.utils.PermissionUtil;
import com.jd.app.android.expensetracker.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends BaseActivity implements DayWiseFragment.OnDayWiseFragmentInteractionListener, WeekWiseFragment.OnWeekWiseFragmentInteractionListener {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private WeekWiseFragment weekWiseFragment;
    private DayWiseFragment dayWiseFragment;
    private String[] STORAGE_PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    static final int REQUEST_EMAIL_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("DayWise"));
        tabLayout.addTab(tabLayout.newTab().setText("WeekWise"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onExpenseClicked(long expenseId) {
        Intent intent = new Intent(WelcomeActivity.this, AddExpenseActivity.class);
        intent.putExtra("expenseId", expenseId);
        startActivity(intent);
    }

    @Override
    public void onDayClicked(String date) {
        try {
            dayWiseFragment.setTime(simpleDateFormat.parse(date).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager.setCurrentItem(0, true);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return getDayFragment();
            } else {
                return getWeekFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Day Wise View";
                case 1:
                    return "Week Wise View";
            }
            return null;
        }
    }

    public Fragment getDayFragment() {
        if (dayWiseFragment == null) {
            try {
                String date = simpleDateFormat.format(new Date());
                dayWiseFragment = DayWiseFragment.newInstance(simpleDateFormat.parse(date).getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dayWiseFragment;
    }

    public Fragment getWeekFragment() {
        if (weekWiseFragment == null) {
            try {
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.add(Calendar.DAY_OF_WEEK,
                        startCalendar.getFirstDayOfWeek() - startCalendar.get(Calendar.DAY_OF_WEEK));
                Calendar endCalendar = (Calendar) startCalendar.clone();
                endCalendar.add(Calendar.DAY_OF_YEAR, 6);

                String startDate = simpleDateFormat.format(startCalendar.getTime());
                String endDate = simpleDateFormat.format(endCalendar.getTime());
                weekWiseFragment = WeekWiseFragment.newInstance(simpleDateFormat.parse(startDate).getTime(), simpleDateFormat.parse(endDate).getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return weekWiseFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.welcome_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(WelcomeActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_export) {
            // export to JSON
            boolean isPermissionAvailable = PermissionUtil.checkAndRequestForPermission(this, getString(R.string.storage_permission), STORAGE_PERMISSION);
            if (isPermissionAvailable) {
                FetchAllExpenseAsyncTask fetchAllExpenseAsyncTask = new FetchAllExpenseAsyncTask();
                fetchAllExpenseAsyncTask.execute();
            }
        } else if (id == R.id.action_delete) {
            deleteAllExpenses();
        }

        return super.onOptionsItemSelected(item);
    }

    class FetchAllExpenseAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            List<Expense> expenseList = Expense.listAll(Expense.class);
            createFiles(expenseList);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(getString(R.string.fetching_expense_progress));
        }

        @Override
        protected void onPostExecute(Void args) {
            super.onPostExecute(args);
            dismissProgress();
        }
    }

    void createFiles(List<Expense> expenseList) {
        if (!Util.isExternalStorageWritable()) {
            Toast.makeText(this, R.string.file_export_error, Toast.LENGTH_LONG).show();
            return;
        }
        SimpleDateFormat tempSimpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_hh:mm:ss", Locale.getDefault());
        String backupLabel = tempSimpleDateFormat.format(new Date());
        backupLabel = getString(R.string.backup_label, backupLabel);
        File directory = Util.getDocumentStorageDir(this, backupLabel);
        JSONArray jsonArray = new JSONArray();
        for (Expense expense : expenseList) {
            try {
                jsonArray.put(Expense.getJSONObject(expense));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        File file = new File(directory, "backup.txt");
        try {

            PrintWriter out = new PrintWriter(file);
            out.println(jsonArray.toString());
            out.close();
            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            String subject = getString(R.string.email_subject_label, simpleDateFormat.format(new Date()));
                            String mail = getString(R.string.email_content);
                            Util.email(WelcomeActivity.this, subject, mail, path, REQUEST_EMAIL_CODE);
                        }
                    });
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
        }
    }

    void deleteAllExpenses() {
        DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Expense.deleteAll(Expense.class);
                viewPager.setCurrentItem(0);
            }
        };

        Util.showConfirmationDialog(this, getString(R.string.delete_expenses_message), getString(R.string.delete_label), getString(R.string.cancel_label), positiveButtonListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionAvailable = PermissionUtil.isPermissionProvided(requestCode, permissions, grantResults);
        if (isPermissionAvailable) {
            FetchAllExpenseAsyncTask fetchAllExpenseAsyncTask = new FetchAllExpenseAsyncTask();
            fetchAllExpenseAsyncTask.execute();
        } else {
            DialogInterface.OnClickListener negativeButtonOnClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(WelcomeActivity.this, R.string.storage_permission_declined, Toast.LENGTH_LONG).show();
                }
            };
            PermissionUtil.showPermissionErrorDialog(this, getString(R.string.storage_permission_required), getString(R.string.setting_label), getString(R.string.cancel_label), negativeButtonOnClickListener, getPackageName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EMAIL_CODE) {
            deleteAllExpenses();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
