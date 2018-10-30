package com.kerneldev.remindfit;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.tittojose.www.timerangepicker_library.TimeRangePickerDialog;

public class ProfileActivity extends AppCompatActivity implements TimeRangePickerDialog.OnTimeRangeSelectedListener {

    @BindView(R.id.select_time_period) Button _timePeriodSelectButton;
    @BindView(R.id.save_prof_details) Button _saveProfDetails;

    @BindView(R.id.male) RadioButton _maleRadio;
    @BindView(R.id.female) RadioButton _femaleRadio;
    @BindView(R.id.blood_group) Spinner _bloodGroups;
    @BindView(R.id.age) TextInputEditText _ageInput;
    @BindView(R.id.user_weight) TextInputEditText _weightInput;
    @BindView(R.id.user_height) TextInputEditText _heightInput;

    SharedPreferences sharedpreferences;
    DBManager database;
    String sex = "male";

    public static final String TIMERANGEPICKER_TAG = "timerangepicker";


    View.OnClickListener showTimePicker = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final TimeRangePickerDialog timePickerDialog = TimeRangePickerDialog.newInstance(
                    ProfileActivity.this, false);
            timePickerDialog.show(getSupportFragmentManager(), TIMERANGEPICKER_TAG);
        }
    };

    View.OnClickListener saveDetais = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if(validate()){
                int weight =  Integer.valueOf(_weightInput.getText().toString());
                int height =  Integer.valueOf(_heightInput.getText().toString());
                int age =  Integer.valueOf(_ageInput.getText().toString());
                String bloodGroup = _bloodGroups.getSelectedItem().toString();

                String [] times = _timePeriodSelectButton.getText().toString().split(" - ");

                String startTime = times[0];
                String endTime = times[1];

                database = new DBManager(getApplicationContext());
                database.open();

                int userId = sharedpreferences.getInt("logged_in_user", -1);

                if(userId != -1){
                    if(database.insertUserDetails(userId, sex, weight, height, bloodGroup , age, startTime, endTime) != -1){
                        onSaveDetailSuccess();
                    }
                } else {
                    onSaveDetailFail();
                }

                database.close();

            } else {
                Toast.makeText(getApplicationContext(), "Invalid details", Toast.LENGTH_LONG).show();
            }
        }

    };

    void onSaveDetailSuccess(){
        Toast.makeText(getApplicationContext(), "Details saved", Toast.LENGTH_SHORT).show();
    }

    void onSaveDetailFail(){
        Toast.makeText(getApplicationContext(), "There was an error!", Toast.LENGTH_SHORT).show();
    }

    boolean validate(){

        boolean valid = true;

        String weight =  _weightInput.getText().toString();
        String height =  _heightInput.getText().toString();
        String age =  _ageInput.getText().toString();

        if (weight.isEmpty()) {
            _weightInput.setError("Please enter your weight");
            valid = false;
        } else {
            _weightInput.setError(null);
        }

        if (height.isEmpty()) {
            _heightInput.setError("Please enter your height");
            valid = false;
        } else {
            _heightInput.setError(null);
        }

        if (age.isEmpty()) {
            _ageInput.setError("Please enter your age");
            valid = false;
        } else {
            _ageInput.setError(null);
        }

        return valid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        _timePeriodSelectButton.setOnClickListener(showTimePicker);
        _saveProfDetails.setOnClickListener(saveDetais);
        sharedpreferences = getSharedPreferences("remindfit", Context.MODE_PRIVATE);

        //selecting male by default
        _maleRadio.setChecked(true);

        _maleRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "male";

                if(_femaleRadio.isChecked()){
                    _femaleRadio.setChecked(false);
                }
            }
        });

        _femaleRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "female";
                if(_maleRadio.isChecked()){
                    _maleRadio.setChecked(false);
                }
            }
        });

        database = new DBManager(getApplicationContext());
        database.open();


        Cursor c = database.fetchUserDetails(sharedpreferences.getInt("logged_in_user", -1));

        Log.v("Userdetails",  DatabaseUtils.dumpCursorToString(c));

        c.close();
        database.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onTimeRangeSelected ( int startHour, int startMin, int endHour, int endMin){
        String startTime = startHour + ":" + startMin;
        String endTime = endHour + ":" + endMin;
        _timePeriodSelectButton.setText(startTime + " - " + endTime);
    }


}
