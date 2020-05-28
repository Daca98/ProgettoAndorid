package com.example.vcv.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
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
    public static final String TABLE_NAME_ORDER = "CalendarOrder";
    public static final String COLUMN_NAME_ORDER_DATE = "DateCalendarOrder";
    public static final String COLUMN_NAME_ORDER_HOUR_FROM = "HourFrom";
    public static final String COLUMN_NAME_ORDER_HOUR_TO = "HourTo";
    public static final String COLUMN_NAME_ORDER_DFT_HOUR_TO_WORK = "DefaultHourToWork";
    public static final String COLUMN_NAME_ORDER_JOB = "Job";
    public static final String COLUMN_NAME_ORDER_CONFIRMED = "Confirmed";
    public static final String COLUMN_NAME_ORDER_EQUIPMENT = "Equipment";
    public static final String COLUMN_NAME_ORDER_NOTE = "Note";
    public static final String COLUMN_NAME_ORDER_REAL_HOUR_FROM = "RealHourFrom";
    public static final String COLUMN_NAME_ORDER_REAL_HOUR_TO = "RealHourTo";

    private static final String SQL_CREATE_ORDER =
            "CREATE TABLE " + ContractLocalDB.TABLE_NAME_ORDER + " (" +
                    ContractLocalDB.COLUMN_NAME_ORDER_DATE + " TEXT PRIMARY KEY," +
                    ContractLocalDB.COLUMN_NAME_ORDER_HOUR_FROM + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_HOUR_TO + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_DFT_HOUR_TO_WORK + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_JOB + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_CONFIRMED + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_EQUIPMENT + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_NOTE + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_REAL_HOUR_FROM + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_ORDER_REAL_HOUR_TO + " TEXT)";

    private static final String SQL_DELETE_ENTRIES_ORDER =
            "DROP TABLE IF EXISTS " + ContractLocalDB.TABLE_NAME_ORDER;


    // Table recap
    public static final String TABLE_NAME_RECAP = "RecapHours";
    public static final String COLUMN_NAME_RECAP_ID = "ID";
    public static final String COLUMN_NAME_RECAP_TO_CALCULATION = "ToCalculation";
    public static final String COLUMN_NAME_RECAP_TOT_HOURS_SHOULD_WORK = "TotHoursShouldWork";
    public static final String COLUMN_NAME_RECAP_TOT_HOURS_WORKED = "TotHoursWorked";
    public static final String COLUMN_NAME_RECAP_TOT_EXTRA = "TotExtra";

    private static final String SQL_CREATE_RECAP =
            "CREATE TABLE " + ContractLocalDB.TABLE_NAME_RECAP + " (" +
                    ContractLocalDB.COLUMN_NAME_RECAP_ID + " TEXT PRIMARY KEY," +
                    ContractLocalDB.COLUMN_NAME_RECAP_TO_CALCULATION + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_RECAP_TOT_HOURS_SHOULD_WORK + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_RECAP_TOT_HOURS_WORKED + " TEXT," +
                    ContractLocalDB.COLUMN_NAME_RECAP_TOT_EXTRA + " TEXT )";

    private static final String SQL_DELETE_ENTRIES_RECAP =
            "DROP TABLE IF EXISTS " + ContractLocalDB.TABLE_NAME_RECAP;

    /**
     * Constructor
     *
     * @param context
     */
    public ContractLocalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Method used to create DB tables
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_ORDER);
        db.execSQL(SQL_CREATE_RECAP);
        Log.i("SQL_LITE_DB", "Created tables with success");
    }

    /**
     * Method used to upgrade the DB version
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_USER);
        db.execSQL(SQL_DELETE_ENTRIES_ORDER);
        db.execSQL(SQL_DELETE_ENTRIES_RECAP);
        onCreate(db);
    }
}