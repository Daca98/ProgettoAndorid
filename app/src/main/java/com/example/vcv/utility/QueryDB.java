package com.example.vcv.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QueryDB {
    private ContractLocalDB dbHelper;

    public QueryDB(Context ctx) {
        dbHelper = new ContractLocalDB(ctx);
    }

    // Query for user
    public long insertUserData(User user) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ContractLocalDB.COLUMN_NAME_USER_NAME, user.name);
        values.put(ContractLocalDB.COLUMN_NAME_USER_SURNAME, user.surname);
        values.put(ContractLocalDB.COLUMN_NAME_USER_EMAIL, user.email);
        values.put(ContractLocalDB.COLUMN_NAME_USER_BADGE_NUMBER, user.badgeNumber);
        values.put(ContractLocalDB.COLUMN_NAME_USER_TELEPHONE, user.telephone);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ContractLocalDB.TABLE_NAME_USER, null, values);
        return newRowId;
    }

    public User readUser() {
        User user = null;

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + ContractLocalDB.TABLE_NAME_USER;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable
                user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(4), cursor.getString(3), cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractLocalDB.COLUMN_NAME_USER_NAME, user.name);
        values.put(ContractLocalDB.COLUMN_NAME_USER_SURNAME, user.surname);
        values.put(ContractLocalDB.COLUMN_NAME_USER_TELEPHONE, user.telephone);

        int rowUpdated = db.update(ContractLocalDB.TABLE_NAME_USER, values, ContractLocalDB.COLUMN_NAME_USER_BADGE_NUMBER + " = ?", new String[]{user.badgeNumber});

        return rowUpdated;
    }

    public void cleanLogout() {
        cleanUser();
    }

    private void cleanUser() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM " + ContractLocalDB.TABLE_NAME_USER);
    }

    // Query for calendar order
    public long insertSingleCalendarOrderData(CalendarOrder calendarOrder) {
        long newRowCount = 0;

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_DATE, calendarOrder.dateCalendarOrder);
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_HOUR_FROM, calendarOrder.hourFrom);
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_HOUR_TO, calendarOrder.hourTo);
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_DFT_HOUR_TO_WORK, calendarOrder.defaultHourToWork);
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_JOB, calendarOrder.job);
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_CONFIRMED, calendarOrder.confirmed);
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_EQUIPMENT, calendarOrder.equipment);
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_NOTE, calendarOrder.note);

        // Insert the new row, returning the primary key value of the new row
        newRowCount = db.insert(ContractLocalDB.TABLE_NAME_ORDER, null, values);

        return newRowCount;
    }

    public CalendarOrder readCalendarOrderSingleDay(Date date) {
        CalendarOrder calendarOrder = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  * " +
                "FROM " + ContractLocalDB.TABLE_NAME_ORDER + " " +
                "WHERE " + ContractLocalDB.COLUMN_NAME_ORDER_DATE + "='" + (new SimpleDateFormat("yyyy-MM-dd").format(date)) + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    calendarOrder = new CalendarOrder(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Boolean.getBoolean(cursor.getString(5)), cursor.getString(6), cursor.getString(7));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return calendarOrder;
    }

}