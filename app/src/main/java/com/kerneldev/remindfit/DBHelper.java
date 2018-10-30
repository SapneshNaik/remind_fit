package com.kerneldev.remindfit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //User Table Name
    public static final String USER_TABLE = "users";
    public static final String USER_DETAILS_TABLE = "users_details";

    //User Table columns
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String PASSWORD = "password";


    //
    public static final String USER_ID = "user_id";
    public static final String SEX = "sex";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String AGE = "age";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";

    //
    // Database Information
    static final String DB_NAME = "REMIND_FIT.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_USER_TABLE = "create table "+USER_TABLE+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+"  TEXT NOT NULL, "+EMAIL+" TEXT NOT NULL UNIQUE, "+MOBILE+" TEXT NOT NULL UNIQUE, "+PASSWORD+" TEXT NOT NULL)";
    private static final String CREATE_USER_DETAILS_TABLE = "create table "+USER_DETAILS_TABLE+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_ID+" INTEGER REFERENCES "+USER_TABLE+"("+_ID+"), "+SEX+"  TEXT NOT NULL, "+HEIGHT+" INTEGER NOT NULL, "+WEIGHT+" INTEGER NOT NULL, "+AGE+" INTEGER NOT NULL, "+BLOOD_GROUP+" TEXT NOT NULL, "+START_TIME+" TEXT NOT NULL, "+END_TIME+" TEXT NOT NULL)";



    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_USER_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_DETAILS_TABLE);
        onCreate(db);
    }
}

