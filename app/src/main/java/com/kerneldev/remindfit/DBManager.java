package com.kerneldev.remindfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
    private DBHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    DBManager(Context c) {
        context = c;
    }

    DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
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
        long userid = database.insert(DBHelper.USER_TABLE, null, contentValue);

        return (int) userid;
    }

    public Cursor fetchUser(int id) {
        String[] columns = new String[] { DBHelper._ID, DBHelper.NAME, DBHelper.EMAIL, DBHelper.MOBILE };
        Cursor cursor = database.query(DBHelper.USER_TABLE, columns,  DBHelper._ID+"=?", new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

//    void fetchAll() {
//        String[] columns = new String[] { DBHelper._ID, DBHelper.NAME, DBHelper.EMAIL, DBHelper.MOBILE };
//        Cursor cursor = database.query(DBHelper.USER_TABLE, columns,  null,null, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
//        cursor.close();
//    }

    int validUser(String email, String password) {
        String[] columns = new String[] { DBHelper._ID };
        Cursor cursor = database.query(DBHelper.USER_TABLE, columns,  DBHelper.EMAIL+"=? and "+DBHelper.PASSWORD+"=?", new String[] { email, password }, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            int userid = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor.close();
            return userid;
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

    public boolean mobileExists(String mobile) {
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

    public Cursor fetchUserDetails(int userID) {
        String[] columns = new String[] { DBHelper.SEX, DBHelper.WEIGHT, DBHelper.HEIGHT, DBHelper.BLOOD_GROUP, DBHelper.AGE, DBHelper.START_TIME, DBHelper.END_TIME };
        Cursor cursor = database.query(DBHelper.USER_DETAILS_TABLE, columns,  DBHelper.USER_ID+"=?", new String[] { String.valueOf(userID) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

}

