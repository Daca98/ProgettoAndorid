package com.example.vcv.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContractLocalDB extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "vcv.db";

    // Table user
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

    private static final String SQL_DELETE_ENTRIES_USER =
            "DROP TABLE IF EXISTS " + ContractLocalDB.TABLE_NAME_USER;

    // Table order
    //public static final String COLUMN_NAME_ORDER_ID = "ID";
    public static final String TABLE_NAME_ORDER = "CalendarOrder";
    public static final String COLUMN_NAME_ORDER_DATE = "DateCalendarOrder";
    public static final String COLUMN_NAME_ORDER_HOUR_FROM = "HourFrom";
    public static final String COLUMN_NAME_ORDER_HOUR_TO = "HourTo";
    public static final String COLUMN_NAME_ORDER_DFT_HOUR_TO_WORK = "DefaultHourToWork";
    public static final String COLUMN_NAME_ORDER_JOB = "Job";
    public static final String COLUMN_NAME_ORDER_CONFIRMED = "Confirmed";
    public static final String COLUMN_NAME_ORDER_EQUIPMENT = "Equipment";
    public static final String COLUMN_NAME_ORDER_NOTE = "Note";

    private static final String SQL_CREATE_ORDER =
            "CREATE TABLE " + ContractLocalDB.TABLE_NAME_ORDER + " (" +
                    ContractLocalDB.COLUMN_NAME_ORDER_DATE + " TEXT PRIMARY KEY," +
                    ContractLocalDB.COLUMN_NAME_ORDER_HOUR_FROM + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_HOUR_TO + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_DFT_HOUR_TO_WORK + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_JOB + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_CONFIRMED + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_EQUIPMENT + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_NOTE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES_ORDER =
            "DROP TABLE IF EXISTS " + ContractLocalDB.TABLE_NAME_ORDER;

    public ContractLocalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_ORDER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_USER);
        db.execSQL(SQL_DELETE_ENTRIES_ORDER);
        onCreate(db);
    }
}