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

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertUser(String name, String email, String mobile, String password) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NAME, name);
        contentValue.put(DBHelper.EMAIL, email);
        contentValue.put(DBHelper.MOBILE, mobile);
        contentValue.put(DBHelper.PASSWORD, password);
        if(database.insert(DBHelper.USER_TABLE, null, contentValue) == -1) return false;
        else return true;
    }

    public Cursor fetchUser(String email) {
        String[] columns = new String[] { DBHelper._ID, DBHelper.NAME, DBHelper.EMAIL, DBHelper.MOBILE };
        Cursor cursor = database.query(DBHelper.USER_TABLE, columns,  DBHelper.EMAIL+"=?", new String[] { email }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void fetchAll() {
        String[] columns = new String[] { DBHelper._ID, DBHelper.NAME, DBHelper.EMAIL, DBHelper.MOBILE };
        Cursor cursor = database.query(DBHelper.USER_TABLE, columns,  null,null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
        cursor.close();
    }

    public Cursor validUser(String email, String password) {
        String[] columns = new String[] { DBHelper._ID, DBHelper.NAME, DBHelper.EMAIL, DBHelper.MOBILE };
        Cursor cursor = database.query(DBHelper.USER_TABLE, columns,  DBHelper.EMAIL+"=? and "+DBHelper.PASSWORD+"=?", new String[] { email, password }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean emailExists(String email) {
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

}

