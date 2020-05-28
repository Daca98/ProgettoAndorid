package com.example.vcv.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class QueryDB {
    private ContractLocalDB dbHelper;

    /**
     * Constructor
     *
     * @param ctx
     */
    public QueryDB(Context ctx) {
        dbHelper = new ContractLocalDB(ctx);
    }

    // Query for user

    /**
     * Method used to insert User data in local DB
     *
     * @param user
     * @return long that represent the number of the row in table user
     */
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

        // Insert the new row, returning the number of the row
        long newRowId = db.insert(ContractLocalDB.TABLE_NAME_USER, null, values);
        Log.i("SQL_LITE_DATABASE", "Insert user successfully");
        return newRowId;
    }

    /**
     * Method to read User logged data
     *
     * @return User logged
     */
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

    /**
     * Method used to update User logged data
     *
     * @param user
     * @return int that represent row updated
     */
    public int updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractLocalDB.COLUMN_NAME_USER_NAME, user.name);
        values.put(ContractLocalDB.COLUMN_NAME_USER_SURNAME, user.surname);
        values.put(ContractLocalDB.COLUMN_NAME_USER_TELEPHONE, user.telephone);

        int rowUpdated = db.update(ContractLocalDB.TABLE_NAME_USER, values, ContractLocalDB.COLUMN_NAME_USER_BADGE_NUMBER + " = ?", new String[]{user.badgeNumber});
        Log.i("SQL_LITE_DATABASE", "Updated user successfully");
        return rowUpdated;
    }

    /**
     * Method used to clean tables when user logout
     */
    public void cleanLogout() {
        cleanUser();
        cleanOrders();
    }

    /**
     * Method to clean user table
     */
    private void cleanUser() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM " + ContractLocalDB.TABLE_NAME_USER);
        Log.i("SQL_LITE_DATABASE", "Deleted table user successfully");
    }

    // Query for calendar order

    /**
     * Method to clean calendarOrder table
     */
    private void cleanOrders() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM " + ContractLocalDB.TABLE_NAME_ORDER);
        Log.i("SQL_LITE_DATABASE", "Deleted table order successfully");
    }

    /**
     * Method used to insert single calendarOrder in local DB
     *
     * @param calendarOrder
     * @return long that represent the number of the row in table calendarOrder
     */
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

        // Insert the new row, returning the number of the row
        newRowCount = db.insert(ContractLocalDB.TABLE_NAME_ORDER, null, values);
        Log.i("SQL_LITE_DATABASE", "Insert calendarOrder successfully");
        return newRowCount;
    }

    /**
     * Method to retrieve CalendarOrder of a specific date
     *
     * @param date
     * @return CalendarOrder object
     */
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
                    calendarOrder = new CalendarOrder(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5).equals("1"), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return calendarOrder;
    }

    // Query for recap

    /**
     * Method to retrieve RecapHour
     *
     * @param id
     * @return RecapHour object
     */
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

    /**
     * Method used to insert recap hour in local DB
     *
     * @param recapHours
     * @return long that represent the number of the row in table recap
     */
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

            // Insert the new row, returning the number of the row
            newRowId = db.insert(ContractLocalDB.TABLE_NAME_RECAP, null, values);
            Log.i("SQL_LITE_DATABASE", "Insert recap successfully");
        } else {
            newRowId = 0;
        }

        return newRowId;
    }
}