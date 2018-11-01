package com.kerneldev.remindfit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CHANNEL_ID = "remind_fit";
    @BindView(R.id.ham_menu)
    ImageView _hamMenu;
    DrawerLayout _navDrawer;
    @BindView(R.id.app_name)
    TextView _appName;
    @BindView(R.id.nav_view)
    NavigationView _navigationView;
    @BindView(R.id.bar_chart)
    BarChart barChart;

    TextView _taskToday;
    TextView _taskCompleted;
    TextView _taskPending;


    SharedPreferences sharedpreferences;

    DBManager database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        createNotificationChannel();

        _navDrawer = findViewById(R.id.drawer_layout);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "RockSalt.ttf");

        _appName.setTypeface(custom_font);

        sharedpreferences = getSharedPreferences("remindfit", Context.MODE_PRIVATE);

        _hamMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!_navDrawer.isDrawerOpen(GravityCompat.START))
                    _navDrawer.openDrawer(Gravity.START);
                else _navDrawer.closeDrawer(Gravity.END);
            }
        });


        _navigationView.setNavigationItemSelectedListener(this);

        populateActivityTable();


        setActivityBarChart();
    }

    private int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = new int[2];
//
//        colors[0] = R.attr.colorPrimary;
        System.arraycopy(ColorTemplate.MATERIAL_COLORS, 0, colors, 0, 2);

        return colors;
    }

    @Override
    protected void onResume() {
        boolean isLoggedIn = sharedpreferences.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            Toast.makeText(getBaseContext(), "Welcome Back!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        setUserPreferences();
        setNavDrawer();
        super.onResume();
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    void setActivityBarChart() {

        boolean isLoggedIn = sharedpreferences.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            database = new DBManager(getApplicationContext());
            database.open();

            int userID = sharedpreferences.getInt("logged_in_user", -1);


            int totalActivities = database.getTotalActivities();
            int userCompltetedActivities;

            _taskToday = findViewById(R.id.task_today);
            _taskToday.setText(String.valueOf(totalActivities));



            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String[] datesX = new String[8];

            SimpleDateFormat xFormat = new SimpleDateFormat("dd MMM");

            datesX[0] = "";


            int j;

            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();


            for (j = 1; j <= 7; j++) {

                Calendar c = Calendar.getInstance();
                c.setTime(new Date()); // Now use today date.
                c.add(Calendar.DATE, -(7-j));
                String date = sdf.format(c.getTime());
                datesX[j] = xFormat.format(c.getTime());

                userCompltetedActivities = database.getUserCompletedActivities(userID, date);

                Log.v("Actcomp", "On "+date+" "+userCompltetedActivities);

                yVals1.add(new BarEntry(
                        j,
                        new float[]{userCompltetedActivities, totalActivities - userCompltetedActivities},
                        null));

                if(j == 7){

                    _taskCompleted = findViewById(R.id.task_completed);
                    _taskPending = findViewById(R.id.task_pending);

                    _taskCompleted.setText(String.valueOf(userCompltetedActivities));
                    _taskPending.setText(String.valueOf(totalActivities - userCompltetedActivities));
                }
            }

            database.close();


            BarDataSet set1;

            if (barChart.getData() != null &&
                    barChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
                set1.setValues(yVals1);
                barChart.getData().notifyDataChanged();
                barChart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(yVals1, "[Activity Statistics]");
                set1.setDrawIcons(false);
                set1.setColors(getColors());
                set1.setStackLabels(new String[]{"Completed", "Missed",});

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);
//                data.setValueFormatter(new MyValueFormatter());
                data.setValueTextColor(Color.BLACK);

                Description d = new Description();
                d.setText("Last 7 Days");
                barChart.setDescription(d);

                XAxis xAxis = barChart.getXAxis();
//                xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE );



                xAxis.setValueFormatter(new MyXAxisValueFormatter(datesX));

                barChart.setData(data);

            }

            barChart.setFitBars(true);
            barChart.invalidate();
        }

    }


    @Override
    public void onBackPressed() {
        _navDrawer = findViewById(R.id.drawer_layout);

        if (_navDrawer.isDrawerOpen(GravityCompat.START)) {
            _navDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        _navDrawer = findViewById(R.id.drawer_layout);

        _navDrawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            logout();
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            Intent profileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(profileActivity);
        }

        return true;
    }

    void logout() {

        unSetUserPreferences();

        Toast.makeText(getBaseContext(), "Logged out", Toast.LENGTH_SHORT).show();

        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
    }


    void setUserPreferences() {
        DBManager database = new DBManager(getApplicationContext());
        database.open();

        int id = sharedpreferences.getInt("logged_in_user", -1);

        Cursor cursor = database.fetchUser(id);

        if (cursor.getCount() > 0) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("user_name", cursor.getString(cursor.getColumnIndex("name")));
            editor.putString("user_email", cursor.getString(cursor.getColumnIndex("email")));
            editor.putString("user_mobile", cursor.getString(cursor.getColumnIndex("mobile")));
            editor.apply();
        } else {
            unSetUserPreferences();
        }
        cursor.close();
        database.close();

    }


    void unSetUserPreferences() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.putInt("logged_in_user", -1);
        editor.putString("user_name", null);
        editor.putString("user_email", null);
        editor.putString("user_mobile", null);
        editor.apply();
    }


    void setNavDrawer() {

        View headerView = _navigationView.getHeaderView(0);
        TextView navDrawerName = headerView.findViewById(R.id.nav_drawer_name);
        TextView navDrawerEmail = headerView.findViewById(R.id.nav_drawer_email);
        navDrawerEmail.setText(sharedpreferences.getString("user_email", null));
        navDrawerName.setText(sharedpreferences.getString("user_name", null));
    }


    void populateActivityTable() {
        DBManager database = new DBManager(getApplicationContext());
        database.open();

        String[] activities = {"Drink Water", "Meditate", "Do Push Ups", "Do Pull Ups", "Short Run", "Eye Exercise"};

        for (String activity : activities) {
            //resource string is activity name without spaces and lowercase
            if (database.insertNewActivity(activity, activity.replaceAll("[^A-Za-z]+", "").toLowerCase(), "fitness") == -1) {
                Log.e("populateActivityTable", "Error inseting " + activity);
            }
        }

        database.close();
    }


}
