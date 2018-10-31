package com.kerneldev.remindfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseActivity extends AppCompatActivity {


    DBManager database;
    SharedPreferences sharedPreferences;
    @BindView(R.id.app_name) TextView _appName;
    @BindView(R.id.activity_image) ImageView _activityImage;
    @BindView(R.id.activity_completed) Button _activityCompButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "RockSalt.ttf");
        _appName.setTypeface(custom_font);

        Intent intent = getIntent();
        final int activityID = intent.getIntExtra("ActivityID", -1);
        final String name = intent.getStringExtra("ActivityName");
        final String res = intent.getStringExtra("ActivitySource");
        Log.v("ExerciseActivity", "activityID: "+activityID + "name: " + name + "res: " +res);

        _activityCompButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activityID != -1){
                    sharedPreferences = getSharedPreferences("remindfit", Context.MODE_PRIVATE);

                    int userID = sharedPreferences.getInt("logged_in_user", -1 );
                    if( userID != -1){
                        database = new DBManager(getApplicationContext());
                        database.open();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = sdf.format(new Date());

                        _activityImage.setImageResource(getResources().getIdentifier(res, "drawable", getPackageName()));

                        if(database.insertNewUserActivity(userID, activityID, date) != -1){
                            Toast.makeText(getApplicationContext(), "Done!!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            finish();
                        }

                        database.close();
                    } else {
                        finish();
                    }


                }
            }
        });


    }
}
