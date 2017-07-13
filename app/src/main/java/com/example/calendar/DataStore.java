package com.example.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
    Context mContext;
    public static int notificationCount;
    private DataStore(Context context) {
        mDbHelper = new AgendaSQLiteHelper(context);
        mContext = context;
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

    public void createAgenda(int startDay, int startMonth, int startYear, int startHour, int startMinute, int endDay, int endMonth, int endYear, int endHour, int endMinute, String title, String location, boolean isAllDay, String description, int reminderTime) {


        Calendar start = new GregorianCalendar(startYear, startMonth, startDay, startHour, startMinute);
        Calendar end = new GregorianCalendar(endYear, endMonth, endDay, endHour, endMinute);
        long startTime = start.getTimeInMillis();
        long endTime = end.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put(AgendaSQLiteHelper.START_TIME, startTime);
        values.put(AgendaSQLiteHelper.END_TIME, endTime);
        values.put(AgendaSQLiteHelper.TITLE, title);
        values.put(AgendaSQLiteHelper.LOCATION, location);
        values.put(AgendaSQLiteHelper.DESCRIPTION, description);
        values.put(AgendaSQLiteHelper.REMINDER, reminderTime);

        if (!isAllDay) {
            values.put(AgendaSQLiteHelper.IS_ALL_DAY, 0);
            values.put(AgendaSQLiteHelper.ALL_DAYS_NUMBER, 0);
        }
        else {
            values.put(AgendaSQLiteHelper.IS_ALL_DAY, 1);
            int days = DateTimeUtils.daysSince(start, end);
            values.put(AgendaSQLiteHelper.ALL_DAYS_NUMBER, days);
        }
        mDatabase.insert(AgendaSQLiteHelper.TABLE_NAME, null,
                values);
    }

    public HashMap<String, List<Event>> getAllEvents() {
        HashMap<String, List<Event>> eventMap = new HashMap<String, List<Event>>();
        List<Event> currEventList = new ArrayList<Event>();
        String selectQuery = "SELECT  * FROM " + AgendaSQLiteHelper.TABLE_NAME;
        int startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, endMinute, startDayOfWeek;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        String key = null;

        if (cursor.moveToFirst()) {
            do {
                int eventID = cursor.getInt(0);
                long startTime = cursor.getLong(1);
                long endTime = cursor.getLong(2);
                String title = cursor.getString(3);
                String location = cursor.getString(4);
                boolean isAllDay = cursor.getInt(5) == 1? true : false;
                int daysLeft = cursor.getInt(6);
                String description = cursor.getString(7);
                int reminder = cursor.getInt(8);

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

                if (!isAllDay) {
                    Event event = new Event(eventID, startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, endMinute, title, location, isAllDay, daysLeft, description, reminder);

                    key = DateTimeUtils.formattedDate(mContext, startDayOfWeek, startMonth, startDay, startYear);
                    if (!eventMap.containsKey(key)) {
                        List<Event> emptyList = new ArrayList<Event>();
                        eventMap.put(key, emptyList);
                    }

                    currEventList = eventMap.get(key);
                    currEventList.add(event);
                } else {
                    Calendar cal = new GregorianCalendar(startYear, startMonth, startDay);
                    Calendar orig = (Calendar) cal.clone();
                    for (int i = 0; i < daysLeft; i++) {
                        Event event = new Event(eventID, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), 0, 0, endDay, endMonth, endYear, 0, 0, title, location, isAllDay, daysLeft - i, description, reminder);
                        event.allDayStartDay = orig.get(Calendar.DAY_OF_MONTH);
                        event.allDayStartMonth = orig.get(Calendar.MONTH);
                        event.allDayStartYear = orig.get(Calendar.YEAR);
                        key = DateTimeUtils.formattedDate(mContext, cal.get(Calendar.DAY_OF_WEEK), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR));
                        if (!eventMap.containsKey(key)) {
                            List<Event> emptyList = new ArrayList<Event>();
                            eventMap.put(key, emptyList);
                        }
                        currEventList = eventMap.get(key);
                        currEventList.add(event);
                        cal.add(Calendar.DATE, 1);
                    }
                }

            } while (cursor.moveToNext());
        }
        return eventMap;
    }

    public List<NotificationData> getNotificationData() {
        List<NotificationData> notificationDataList = new ArrayList<NotificationData>();
        String selectQuery = "SELECT  * FROM " + AgendaSQLiteHelper.TABLE_NAME;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        String title;
        boolean isAllDay;
        long startTime;
        int startHour, startMinute;
        int reminderTime;
        int columnID;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        if (cursor.moveToFirst()) {
            do {
                columnID = cursor.getInt(0);
                title = cursor.getString(3);
                startTime = cursor.getLong(1);
                reminderTime = cursor.getInt(8);
                isAllDay = cursor.getInt(5) == 1 ? true : false;
                calendar.setTimeInMillis(startTime);
                if (!isAllDay) {
                    startHour = calendar.get(Calendar.HOUR_OF_DAY);
                    startMinute = calendar.get(Calendar.MINUTE);
                } else {
                    startHour = startMinute = 0;
                }
                NotificationData data = new NotificationData();
                data.eventID = columnID;
                data.title = title;
                data.time = DateTimeUtils.formattedTime(startHour, startMinute).toString();
                data.startDay = calendar.get(Calendar.DAY_OF_MONTH);
                data.startMonth = calendar.get(Calendar.MONTH);
                data.startYear = calendar.get(Calendar.YEAR);
                data.startHour = startHour;
                data.startMinute = startMinute;
                data.reminderTime = reminderTime;
                notificationDataList.add(data);
            } while (cursor.moveToNext());
        }
        return notificationDataList;
    }

    public void deleteEvent(int eventID) {
        mDatabase.execSQL("DELETE FROM " + AgendaSQLiteHelper.TABLE_NAME + " WHERE " + AgendaSQLiteHelper.COLUMN_ID + "= " + eventID);
    }

    public int getLastColumnID() {
        String query = "SELECT " + AgendaSQLiteHelper.COLUMN_ID + " from " + AgendaSQLiteHelper.TABLE_NAME + " order by " + AgendaSQLiteHelper.COLUMN_ID + " DESC limit 1";
        Cursor c = mDatabase.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            return c.getInt(0);
        }
        return 0;
    }
}
