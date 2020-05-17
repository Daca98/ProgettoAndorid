package com.example.vcv.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QueryDB {
    private ContractLocalDB dbHelper;

    public QueryDB(Context ctx) {
        dbHelper = new ContractLocalDB(ctx);
    }

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
}
