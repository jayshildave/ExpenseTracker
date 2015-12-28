package com.jd.app.android.expensetracker.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jd.app.android.expensetracker.R;
import com.jd.app.android.expensetracker.fragment.DayWiseFragment;
import com.jd.app.android.expensetracker.fragment.WeekWiseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity implements DayWiseFragment.OnDayWiseFragmentInteractionListener {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private WeekWiseFragment weekWiseFragment;
    private DayWiseFragment dayWiseFragment;

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
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
