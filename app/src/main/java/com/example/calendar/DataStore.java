package com.example.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by sbandyop on 7/6/2017.
 */
public class DataStore {

    private AgendaSQLiteHelper mDbHelper;
    private SQLiteDatabase mDatabase;
    private static DataStore mInstance;

    private DataStore(Context context) {
        mDbHelper = new AgendaSQLiteHelper(context);
    }

    public static DataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataStore(context);
        }
        return mInstance;

    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
       mDbHelper.close();
    }

    public void createAgenda(int startDay, int startMonth, int startYear, int startHour, int startMinute, int endDay, int endMonth, int endYear, int endHour, int endMinute, String title, String location, boolean isAllDay, String description) {


        if (!isAllDay) {
            Calendar start = new GregorianCalendar(startYear, startMonth, startDay, startHour, startMinute);
            Calendar end = new GregorianCalendar(endYear, endMonth, endDay, endHour, endMinute);

            long startTime = start.getTimeInMillis();
            long endTime = end.getTimeInMillis();

            ContentValues values = new ContentValues();
            values.put(AgendaSQLiteHelper.START_TIME, startTime);
            values.put(AgendaSQLiteHelper.END_TIME, endTime);
            values.put(AgendaSQLiteHelper.TITLE, title);
            values.put(AgendaSQLiteHelper.LOCATION, location);
            values.put(AgendaSQLiteHelper.IS_ALL_DAY, 0);
            values.put(AgendaSQLiteHelper.ALL_DAYS_NUMBER, 0);
            values.put(AgendaSQLiteHelper.DESCRIPTION, description);

            mDatabase.insert(AgendaSQLiteHelper.TABLE_NAME, null,
                    values);
        }
        else {
            Calendar start = new GregorianCalendar(startYear, startMonth, startDay);
            Calendar end = new GregorianCalendar(endYear, endMonth, endDay);

            long endTime = end.getTimeInMillis();

            int days = DateTimeUtils.daysSince(start, end);
            while (days > 0) {
                long startTime = start.getTimeInMillis();

                ContentValues values = new ContentValues();
                values.put(AgendaSQLiteHelper.START_TIME, startTime);
                values.put(AgendaSQLiteHelper.END_TIME, endTime);
                values.put(AgendaSQLiteHelper.TITLE, title);
                values.put(AgendaSQLiteHelper.LOCATION, location);
                values.put(AgendaSQLiteHelper.IS_ALL_DAY, 1);
                values.put(AgendaSQLiteHelper.ALL_DAYS_NUMBER, days);
                values.put(AgendaSQLiteHelper.DESCRIPTION, description);

                mDatabase.insert(AgendaSQLiteHelper.TABLE_NAME, null,
                        values);
                days--;
                start.add(Calendar.DAY_OF_MONTH, 1);
            }

        }
    }

    public HashMap<String, List<Event>> getAllEvents()
    {
        HashMap<String, List<Event>> eventMap = new HashMap<String, List<Event>>();
        List<Event> currEventList = new ArrayList<Event>();
        String selectQuery = "SELECT  * FROM " + AgendaSQLiteHelper.TABLE_NAME;
        int startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, endMinute, startDayOfWeek;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        String key = null;

        if (cursor.moveToFirst()) {
            do {
                long startTime = cursor.getLong(1);
                long endTime = cursor.getLong(2);
                String title = cursor.getString(3);
                String location = cursor.getString(4);
                boolean isAllDay = cursor.getInt(5) == 1? true : false;
                int daysLeft = cursor.getInt(6);
                String description = cursor.getString(7);

                calendar.setTimeInMillis(startTime);
                startDay = calendar.get(Calendar.DAY_OF_MONTH);
                startMonth = calendar.get(Calendar.MONTH);
                startYear = calendar.get(Calendar.YEAR);
                startHour = calendar.get(Calendar.HOUR_OF_DAY);
                startMinute = calendar.get(Calendar.MINUTE);
                startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                calendar.setTimeInMillis(endTime);
                endDay = calendar.get(Calendar.DAY_OF_MONTH);
                endMonth = calendar.get(Calendar.MONTH);
                endYear = calendar.get(Calendar.YEAR);
                endHour = calendar.get(Calendar.HOUR_OF_DAY);
                endMinute = calendar.get(Calendar.MINUTE);

                Event event = new Event(startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, endMinute, title, location, isAllDay, daysLeft, description);
                key = DateTimeUtils.formattedDate(startDayOfWeek, startMonth, startDay, startYear);
                if (!eventMap.containsKey(key))
                {
                    List<Event> emptyList = new ArrayList<Event>();
                    eventMap.put(key, emptyList);
                }

                currEventList = eventMap.get(key);
                currEventList.add(event);

            } while (cursor.moveToNext());
        }
        return eventMap;
    }
}