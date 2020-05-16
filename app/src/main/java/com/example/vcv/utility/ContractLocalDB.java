package com.example.vcv.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContractLocalDB extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "vcv.db";

    public static final String TABLE_NAME_USER = "User";
    public static final String COLUMN_NAME_USER_NAME = "Name";
    public static final String COLUMN_NAME_USER_SURNAME = "Surname";
    public static final String COLUMN_NAME_USER_EMAIL = "Email";
    public static final String COLUMN_NAME_USER_BADGE_NUMBER = "BadgeNumber";
    public static final String COLUMN_NAME_USER_TELEPHONE = "Telephone";

    private static final String SQL_CREATE_USER =
            "CREATE TABLE " + ContractLocalDB.TABLE_NAME_USER + " (" +
                    ContractLocalDB.COLUMN_NAME_USER_NAME + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_USER_SURNAME + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_USER_EMAIL + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_USER_BADGE_NUMBER + " TEXT PRIMARY KEY," +
                    ContractLocalDB.COLUMN_NAME_USER_TELEPHONE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContractLocalDB.TABLE_NAME_USER;

    public ContractLocalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}