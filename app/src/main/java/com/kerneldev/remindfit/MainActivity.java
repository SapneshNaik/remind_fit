package com.kerneldev.remindfit;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.ham_menu) ImageView _hamMenu;
    DrawerLayout _navDrawer;
    @BindView(R.id.app_name) TextView _appName;
    @BindView(R.id.nav_view) NavigationView _navigationView;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        _navDrawer = findViewById(R.id.drawer_layout);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "RockSalt.ttf");

        _appName.setTypeface(custom_font);

        sharedpreferences = getSharedPreferences("remindfit", Context.MODE_PRIVATE);

        _hamMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!_navDrawer.isDrawerOpen(GravityCompat.START)) _navDrawer.openDrawer(Gravity.START);
                else _navDrawer.closeDrawer(Gravity.END);
            }
        });


        _navigationView.setNavigationItemSelectedListener(this);


    }


    @Override
    protected void onResume() {
        boolean isLoggedIn = sharedpreferences.getBoolean("is_logged_in", false);

        if(isLoggedIn){
            Toast.makeText(getBaseContext(), "Welcome Back!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        setUserPreferences();
        setNavDrawer();
        super.onResume();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        //logout option click
//        if (id == R.id.action_logout) {
//            logout();
//        }
//        return super.onOptionsItemSelected(item);
//    }


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
        } else if (id == R.id.nav_profile){
            Intent profileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(profileActivity);
        }

        return true;
    }

    void logout(){

        unSetUserPreferences();

        Toast.makeText(getBaseContext(), "Logged out", Toast.LENGTH_SHORT).show();

        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
    }



    void setUserPreferences(){
        DBManager database = new DBManager(getApplicationContext());
        database.open();

        int id = sharedpreferences.getInt("logged_in_user", -1);

        Cursor cursor = database.fetchUser(id);

        if (cursor.getCount() > 0){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("user_name", cursor.getString(cursor.getColumnIndex("name")));
            editor.putString("user_email", cursor.getString(cursor.getColumnIndex("email")));
            editor.putString("user_mobile", cursor.getString(cursor.getColumnIndex("mobile")));
            editor.apply();
        } else {
            unSetUserPreferences();
        }
        cursor.close();

    }


    void unSetUserPreferences(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.putInt("logged_in_user", -1);
        editor.putString("user_name", null);
        editor.putString("user_email", null);
        editor.putString("user_mobile", null);
        editor.apply();
    }


    void setNavDrawer(){

        View headerView = _navigationView.getHeaderView(0);
        TextView navDrawerName =  headerView.findViewById(R.id.nav_drawer_name);
        TextView navDrawerEmail =  headerView.findViewById(R.id.nav_drawer_email);
        navDrawerEmail.setText(sharedpreferences.getString("user_email", null));
        navDrawerName.setText(sharedpreferences.getString("user_name", null));
    }



}
