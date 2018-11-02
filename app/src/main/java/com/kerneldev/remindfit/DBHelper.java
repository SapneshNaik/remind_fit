package com.kerneldev.remindfit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //User Table Name
    static final String USER_TABLE = "users";
    static final String USER_DETAILS_TABLE = "user_details";
    static final String USER_ACTIVITY_TABLE = "user_activities";
    static final String ACTIVITY_TABLE = "activities";

    //User Table columns
    static final String _ID = "_id";
    static final String NAME = "name";
    static final String EMAIL = "email";
    static final String MOBILE = "mobile";
    static final String PASSWORD = "password";


    //User Detail Table columns
    static final String USER_ID = "user_id";
    static final String SEX = "sex";
    static final String HEIGHT = "height";
    static final String WEIGHT = "weight";
    static final String BLOOD_GROUP = "blood_group";
    static final String AGE = "age";
    static final String START_TIME = "start_time";
    static final String END_TIME = "end_time";

    //Activity Table Columns
    static final String ACTIVITY_RESOURCE = "resource";
    static final String ACTIVITY_TYPE = "type";

    //User Activity Table columns
    static final String ACTIVITY_ID = "activity_id";
    static final String ACTIVITY_COMPLETED_AT = "completed_at";



    // Database Information
    private static final String DB_NAME = "REMIND_FIT.DB";

    // database version
    private static final int DB_VERSION = 1;

    // Creating users table query
    private static final String CREATE_USER_TABLE = "create table "+USER_TABLE+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+"  TEXT NOT NULL, "+EMAIL+" TEXT NOT NULL UNIQUE, "+MOBILE+" TEXT NOT NULL UNIQUE, "+PASSWORD+" TEXT NOT NULL)";

    // Creating activity table query
    private static final String CREATE_ACTIVITY_TABLE = "create table "+ACTIVITY_TABLE+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+"  TEXT NOT NULL UNIQUE, "+ACTIVITY_RESOURCE+" TEXT NOT NULL, "+ACTIVITY_TYPE+" TEXT NOT NULL)";

    // Creating user detail table query
    private static final String CREATE_USER_DETAILS_TABLE = "create table "+USER_DETAILS_TABLE+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_ID+" INTEGER REFERENCES "+USER_TABLE+"("+_ID+"), "+SEX+"  TEXT NOT NULL, "+HEIGHT+" INTEGER NOT NULL, "+WEIGHT+" INTEGER NOT NULL, "+AGE+" INTEGER NOT NULL, "+BLOOD_GROUP+" TEXT NOT NULL, "+START_TIME+" TEXT NOT NULL, "+END_TIME+" TEXT NOT NULL)";

    // Creating user activity list table query
    private static final String CREATE_USER_ACTIVITY_LIST_TABLE = "create table "+USER_ACTIVITY_TABLE+" ( "+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_ID+" INTEGER REFERENCES "+USER_TABLE+"("+_ID+"), "+ACTIVITY_ID+" INTEGER REFERENCES "+ACTIVITY_TABLE+"("+_ID+"), "+ACTIVITY_COMPLETED_AT+"  DATE NOT NULL)";



    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create all tables on app install

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_USER_DETAILS_TABLE);
        db.execSQL(CREATE_ACTIVITY_TABLE);
        db.execSQL(CREATE_USER_ACTIVITY_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop all tables

        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_DETAILS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_ACTIVITY_TABLE);

        //create them again
        onCreate(db);
    }
}

