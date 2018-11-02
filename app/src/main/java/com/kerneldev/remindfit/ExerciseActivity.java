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
import pl.droidsonroids.gif.GifImageView;

/*
Display activity animation on notification click
 */
public class ExerciseActivity extends AppCompatActivity {

    DBManager database;
    SharedPreferences sharedPreferences;
    @BindView(R.id.app_name)
    TextView _appName;
    @BindView(R.id.activity_image)
    GifImageView _activityImage;
    @BindView(R.id.activity_completed)
    Button _activityCompButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        //set RockSalt font to logo
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "RockSalt.ttf");
        _appName.setTypeface(custom_font);

        //get activityID and resource name from intent
        Intent intent = getIntent();
        final int activityID = intent.getIntExtra("ActivityID", -1);
        final String res = intent.getStringExtra("ActivitySource");

        //set animated gif
        _activityImage.setImageResource(getResources().getIdentifier(res, "drawable", getPackageName()));

        //write to DB as activity completed on click
        _activityCompButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityID != -1) {
                    sharedPreferences = getSharedPreferences("remindfit", Context.MODE_PRIVATE);

                    int userID = sharedPreferences.getInt("logged_in_user", -1);
                    if (userID != -1) {
                        database = new DBManager(getApplicationContext());
                        database.open();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = sdf.format(new Date());


                        database.insertNewUserActivity(userID, activityID, date);
                        Toast.makeText(getApplicationContext(), "Done!!", Toast.LENGTH_LONG).show();
                        database.close();
                        finish();
                    } else {
                        finish();
                    }
                }
            }
        });


    }
}
