package com.example.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sbandyop on 7/6/2017.
 */
public class AgendaSQLiteHelper extends SQLiteOpenHelper {

    private static final String AGENDA_DB = "agenda.db";
    public static final String TABLE_NAME = "agenda";
    private static final int DATABASE_VERSION = 1;
    public static final String COLUMN_ID = "id";
    public static final String START_TIME = "star_time";
    public static final String END_TIME = "end_time";
    public static final String TITLE = "title";
    public static final String LOCATION = "location";
    public static final String IS_ALL_DAY = "is_all_day";
    public static final String ALL_DAYS_NUMBER = "all_day_number";
    public static final String DESCRIPTION = "description";
    public static final String REMINDER = "reminder";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "( " + COLUMN_ID
            + " integer primary key autoincrement, " + START_TIME
            + " integer, " + END_TIME + " integer, " + TITLE + " text, " + LOCATION + " text, " + IS_ALL_DAY + " integer, " + ALL_DAYS_NUMBER + " integer, " + DESCRIPTION + " text, " + REMINDER + " integer);";

    public AgendaSQLiteHelper(Context context) {
        super(context, AGENDA_DB, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
