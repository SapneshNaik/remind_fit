package com.kerneldev.remindfit;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;


import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.tittojose.www.timerangepicker_library.TimeRangePickerDialog;

public class ProfileActivity extends AppCompatActivity implements TimeRangePickerDialog.OnTimeRangeSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 0;
    @BindView(R.id.select_time_period)
    Button _timePeriodSelectButton;
    @BindView(R.id.save_prof_details)
    Button _saveProfDetails;
    @BindView(R.id.male)
    RadioButton _maleRadio;
    @BindView(R.id.female)
    RadioButton _femaleRadio;
    @BindView(R.id.blood_group)
    Spinner _bloodGroups;
    @BindView(R.id.age)
    TextInputEditText _ageInput;
    @BindView(R.id.user_weight)
    TextInputEditText _weightInput;
    @BindView(R.id.user_height)
    TextInputEditText _heightInput;
    @BindView(R.id.app_name)
    TextView _appName;
    @BindView(R.id.uploadProfileImage)
    FloatingActionButton _uploadImageB;
    @BindView(R.id.profile_image)
    CircleImageView _profileImage;


    SharedPreferences sharedpreferences;
    DBManager database;
    String sex = "male";
    Boolean dataExists = false;

    Uri imageUri;

    int userId;

    public static final String TIMERANGEPICKER_TAG = "timerangepicker";

    View.OnClickListener showTimePicker = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final TimeRangePickerDialog timePickerDialog = TimeRangePickerDialog.newInstance(
                    ProfileActivity.this, false);
            timePickerDialog.show(getSupportFragmentManager(), TIMERANGEPICKER_TAG);
        }
    };

    View.OnClickListener saveDetails = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (validate()) {
                int weight = Integer.valueOf(_weightInput.getText().toString());
                int height = Integer.valueOf(_heightInput.getText().toString());
                int age = Integer.valueOf(_ageInput.getText().toString());
                String bloodGroup = _bloodGroups.getSelectedItem().toString();

                String[] times = _timePeriodSelectButton.getText().toString().split(" - ");

                String startTime = times[0];
                String endTime = times[1];


                userId = sharedpreferences.getInt("logged_in_user", -1);

                if (userId != -1) {
                    database = new DBManager(getApplicationContext());
                    database.open();

                    if (!dataExists) {
                        //add for the first time
                        if (database.insertUserDetails(userId, sex, weight, height, bloodGroup, age, startTime, endTime) != -1) {
                            dataExists = true;
                            onSaveDetailSuccess();
                        } else {
                            onSaveDetailFail();
                        }


                    } else {
                        //update
                        if (database.updateUserDetails(userId, sex, weight, height, bloodGroup, age, startTime, endTime) != -1) {
                            onUpdateDetailSuccess();
                        } else {
                            onSaveDetailFail();
                        }
                    }

                    database.close();

                    AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 777, intent, 0);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 6);
                    calendar.set(Calendar.MINUTE, 58);

                    // setRepeating() lets you specify a precise custom interval--in this case,
                    // 20 minutes.

                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 1, alarmIntent);
                } else {
                    onSaveDetailFail();
                }


            } else {
                Toast.makeText(getApplicationContext(), "Invalid details", Toast.LENGTH_LONG).show();
            }
        }

    };


    View.OnClickListener uploadImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();

                _profileImage.setImageURI(imageUri);

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    askStoragePermission();
                } else {

                    try {
                        saveProfileImage(imageUri);
                    } catch (IOException e) {
                        Toast.makeText(this, "There was an error", Toast.LENGTH_LONG).show();
                        Log.e("Imageerror", "exception", e);
                    }
                }
            }
    }


    void askStoragePermission() {
        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            // No explanation needed; request the permission
            // MY_PERMISSIONS_REQUEST_WRITE_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    try {
                        saveProfileImage(imageUri);
                    } catch (IOException e) {
                        Toast.makeText(this, "There was an error", Toast.LENGTH_LONG).show();
                        Log.e("Imageerror", "exception", e);
                    }


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Please provide storage permission", Toast.LENGTH_LONG).show();

                    Log.e("StoragePerm", "denied");

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    //create data/files/profile_images folder for profile picture storage
    String createProfileImagesFolder() {
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + getString(R.string.app_profile_folder);
        File projDir = new File(dirPath);
        if (!projDir.exists())
            projDir.mkdirs();
        return dirPath;
    }

    private void saveProfileImage(Uri imageUri) throws IOException {

        String dir = createProfileImagesFolder();

        String name = sharedpreferences.getInt("logged_in_user", -1)+ ".jpg";

        final int chunkSize = 1024;  // We'll read in one kB at a time
        byte[] imageData = new byte[chunkSize];

        File file = new File(dir, name);

        try (InputStream in = getContentResolver().openInputStream(imageUri); OutputStream out = new FileOutputStream(file)) {
            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }

        } catch (Exception ex) {
            Log.e("Something went wrong.", "sd", ex);
        }
    }

    void onSaveDetailSuccess() {
        Toast.makeText(getApplicationContext(), "Details Saved", Toast.LENGTH_SHORT).show();
    }

    void onUpdateDetailSuccess() {
        Toast.makeText(getApplicationContext(), "Details Updated", Toast.LENGTH_SHORT).show();
    }

    void onSaveDetailFail() {
        Toast.makeText(getApplicationContext(), "There was an error!", Toast.LENGTH_SHORT).show();
    }

    boolean validate() {

        boolean valid = true;

        String weight = _weightInput.getText().toString();
        String height = _heightInput.getText().toString();
        String age = _ageInput.getText().toString();

        if (weight.isEmpty()) {
            _weightInput.setError("Please enter your Weight");
            valid = false;
        } else {
            _weightInput.setError(null);
        }

        if (height.isEmpty()) {
            _heightInput.setError("Please enter your Height");
            valid = false;
        } else {
            _heightInput.setError(null);
        }

        if (age.isEmpty()) {
            _ageInput.setError("Please enter your Age");
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

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "RockSalt.ttf");
        _appName.setTypeface(custom_font);

        _timePeriodSelectButton.setOnClickListener(showTimePicker);
        _saveProfDetails.setOnClickListener(saveDetails);
        sharedpreferences = getSharedPreferences("remindfit", Context.MODE_PRIVATE);

        //selecting male by default
        _maleRadio.setChecked(true);

        _maleRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "male";

                if (_femaleRadio.isChecked()) {
                    _femaleRadio.setChecked(false);
                }
            }
        });

        _femaleRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "female";
                if (_maleRadio.isChecked()) {
                    _maleRadio.setChecked(false);
                }
            }
        });

        _uploadImageB.setOnClickListener(uploadImage);


    }

    @Override
    protected void onResume() {
        super.onResume();
        populateDetails();
        setProfileImages();

    }


    private void setProfileImages() {
        String name =  sharedpreferences.getInt("logged_in_user", -1)+ ".jpg";

        String dirPath = getFilesDir().getAbsolutePath() + File.separator + getString(R.string.app_profile_folder);

        name = dirPath + File.separator + name;

        File file = new File(name);
        if(file.exists()){
            Uri uri = Uri.parse(name);

            _profileImage.setImageURI(uri);
//            _uploadImageB.setVisibility(View.GONE);
        }

    }

    @Override
    public void onTimeRangeSelected(int startHour, int startMin, int endHour, int endMin) {
        String startTime = startHour + ":" + startMin;
        String endTime = endHour + ":" + endMin;
        _timePeriodSelectButton.setText(startTime + " - " + endTime);
    }


    /*
    Populate existing user details
     */
    void populateDetails() {

        database = new DBManager(getApplicationContext());
        database.open();
        Cursor cursor = database.fetchUserDetails(sharedpreferences.getInt("logged_in_user", -1));
        if ((cursor != null) && (cursor.getCount() > 0)) {

            dataExists = true;
            //set gender(sex)
            if (cursor.getString(cursor.getColumnIndex(DBHelper.SEX)).equals("male")) {
                _maleRadio.setChecked(true);
                _femaleRadio.setChecked(false);
            } else {
                _maleRadio.setChecked(false);
                _femaleRadio.setChecked(true);
            }

            _weightInput.setText(cursor.getString(cursor.getColumnIndex(DBHelper.WEIGHT)));
            _heightInput.setText(cursor.getString(cursor.getColumnIndex(DBHelper.HEIGHT)));
            _ageInput.setText(cursor.getString(cursor.getColumnIndex(DBHelper.AGE)));

            String time = cursor.getString(cursor.getColumnIndex(DBHelper.START_TIME)) + " - " + cursor.getString(cursor.getColumnIndex(DBHelper.END_TIME));

            _timePeriodSelectButton.setText(time);

            //set blood group spinner value
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood_groups, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            _bloodGroups.setAdapter(adapter);

            int spinnerPosition = adapter.getPosition(cursor.getString(cursor.getColumnIndex(DBHelper.BLOOD_GROUP)));
            _bloodGroups.setSelection(spinnerPosition);


        }

        cursor.close();
        database.close();
    }

}
