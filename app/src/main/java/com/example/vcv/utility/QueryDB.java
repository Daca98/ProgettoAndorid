package com.example.vcv.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
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
        cleanOrders();
    }

    private void cleanUser() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM " + ContractLocalDB.TABLE_NAME_USER);
    }

    // Query for calendar order
    private void cleanOrders() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM " + ContractLocalDB.TABLE_NAME_ORDER);
    }

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
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_REAL_HOUR_FROM, calendarOrder.realHourFrom);
        values.put(ContractLocalDB.COLUMN_NAME_ORDER_REAL_HOUR_TO, calendarOrder.realHourTo);

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
                    calendarOrder = new CalendarOrder(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), !Boolean.getBoolean(cursor.getString(5)), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return calendarOrder;
    }

    // Query for recap
    public RecapHours readRecap(String id) {
        RecapHours recap = null;

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + ContractLocalDB.TABLE_NAME_RECAP + " WHERE " + ContractLocalDB.COLUMN_NAME_RECAP_ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // get the data into array, or class variable
                recap = new RecapHours(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return recap;
    }

    public long insertRecap(RecapHours recapHours) {
        String[] ids = recapHours.toCalculation.split("-");
        long newRowId = -1;

        if (readRecap(ids[0] + "-" + ids[1]) == null) {
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(ContractLocalDB.COLUMN_NAME_RECAP_ID, ids[0] + "-" + ids[1]);
            values.put(ContractLocalDB.COLUMN_NAME_RECAP_TO_CALCULATION, recapHours.toCalculation);
            values.put(ContractLocalDB.COLUMN_NAME_RECAP_TOT_HOURS_SHOULD_WORK, recapHours.totHoursShouldWork);
            values.put(ContractLocalDB.COLUMN_NAME_RECAP_TOT_HOURS_WORKED, recapHours.totHoursWorked);
            values.put(ContractLocalDB.COLUMN_NAME_RECAP_TOT_EXTRA, recapHours.totExtra);

            // Insert the new row, returning the primary key value of the new row
            newRowId = db.insert(ContractLocalDB.TABLE_NAME_RECAP, null, values);
        } else {
            newRowId = 0;
        }

        return newRowId;
    }
}