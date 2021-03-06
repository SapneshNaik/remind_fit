package com.kerneldev.remindfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

class DBManager {
    private DBHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    DBManager(Context c) {
        context = c;
    }

    void open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    void close() {
        dbHelper.close();
    }

    int insertUser(String name, String email, String mobile, String password) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NAME, name);
        contentValue.put(DBHelper.EMAIL, email);
        contentValue.put(DBHelper.MOBILE, mobile);
        contentValue.put(DBHelper.PASSWORD, password);
        long userID = database.insert(DBHelper.USER_TABLE, null, contentValue);

        return (int) userID;
    }

    Cursor fetchUser(int id) {
        String[] columns = new String[] { DBHelper._ID, DBHelper.NAME, DBHelper.EMAIL };
        Cursor cursor = database.query(DBHelper.USER_TABLE, columns,  DBHelper._ID+"=?", new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    int validUser(String email, String password) {
        String[] columns = new String[] { DBHelper._ID };
        Cursor cursor = database.query(DBHelper.USER_TABLE, columns,  DBHelper.EMAIL+"=? and "+DBHelper.PASSWORD+"=?", new String[] { email, password }, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            int userID = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor.close();
            return userID;
        } else {
            cursor.close();
            return -1;
        }
    }

    boolean emailExists(String email) {
        String sql = "SELECT EXISTS (SELECT * FROM "+DBHelper.USER_TABLE+" WHERE "+DBHelper.EMAIL+"='"+email+"' LIMIT 1)";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getInt(0) == 1) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    boolean mobileExists(String mobile) {
        String sql = "SELECT EXISTS (SELECT * FROM "+DBHelper.USER_TABLE+" WHERE "+DBHelper.MOBILE+"='"+mobile+"' LIMIT 1)";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getInt(0) == 1) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }


    int insertUserDetails(int user_id,  String sex, int weight, int height, String blood_group, int age, String start_time, String end_time ) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.USER_ID, user_id);
        contentValue.put(DBHelper.SEX, sex);
        contentValue.put(DBHelper.WEIGHT, weight);
        contentValue.put(DBHelper.HEIGHT, height);
        contentValue.put(DBHelper.BLOOD_GROUP, blood_group);
        contentValue.put(DBHelper.AGE, age);
        contentValue.put(DBHelper.START_TIME, start_time);
        contentValue.put(DBHelper.END_TIME, end_time);

        long user_details_id = database.insert(DBHelper.USER_DETAILS_TABLE, null, contentValue);
        return (int) user_details_id;
    }

    int updateUserDetails(int user_id,  String sex, int weight, int height, String blood_group, int age, String start_time, String end_time ) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.SEX, sex);
        contentValue.put(DBHelper.WEIGHT, weight);
        contentValue.put(DBHelper.HEIGHT, height);
        contentValue.put(DBHelper.BLOOD_GROUP, blood_group);
        contentValue.put(DBHelper.AGE, age);
        contentValue.put(DBHelper.START_TIME, start_time);
        contentValue.put(DBHelper.END_TIME, end_time);

        long user_details_id = database.update(DBHelper.USER_DETAILS_TABLE, contentValue, ""+DBHelper.USER_ID+"="+user_id, null);
        return (int) user_details_id;
    }

    Cursor fetchUserDetails(int userID) {
        String[] columns = new String[] { DBHelper.SEX, DBHelper.WEIGHT, DBHelper.HEIGHT, DBHelper.BLOOD_GROUP, DBHelper.AGE, DBHelper.START_TIME, DBHelper.END_TIME };
        Cursor cursor = database.query(DBHelper.USER_DETAILS_TABLE, columns,  DBHelper.USER_ID+"=?", new String[] { String.valueOf(userID) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    int insertNewActivity(String name, String resource) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NAME, name);
        contentValue.put(DBHelper.ACTIVITY_RESOURCE, resource);
        contentValue.put(DBHelper.ACTIVITY_TYPE, "fitness");
        long user_details_id = -1;

        try {
            user_details_id = database.insertOrThrow(DBHelper.ACTIVITY_TABLE, null, contentValue);
        } catch (android.database.sqlite.SQLiteConstraintException e){
            Log.e("insertNewActivity", "Resource: "+name + " already exists");
        }

        return (int) user_details_id;
    }


    //SELECT * FROM 'activities' where _id NOT IN ( SELECT activity_id FROM user_activities WHERE user_id=1);

    Cursor fetchNextActivity(int userID){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        Log.v("DEBUG", String.valueOf(userID));

        String sql = "SELECT * FROM 'activities' where _id NOT IN ( SELECT activity_id FROM user_activities WHERE user_id="+userID+"  AND completed_at='"+date+"' ) LIMIT 1";
        Log.v("sql query",sql);

        Cursor cursor = database.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    void insertNewUserActivity(int userID, int activityID, String completedAT ) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.USER_ID, userID);
        contentValue.put(DBHelper.ACTIVITY_ID, activityID);
        contentValue.put(DBHelper.ACTIVITY_COMPLETED_AT, completedAT);
        database.insert(DBHelper.USER_ACTIVITY_TABLE, null, contentValue);
    }

    int getTotalActivities(){
        String countQuery = "SELECT  * FROM " + DBHelper.ACTIVITY_TABLE;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    int getUserCompletedActivities(int userID, String date){
        String sql = "SELECT * FROM user_activities WHERE user_id="+userID+"  AND completed_at='"+date+"'";
        Cursor cursor = database.rawQuery(sql, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

}

