package com.example.calendar;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by sbandyop on 7/4/2017.
 */
public class DateTimeUtils {

    static String convertDayIntToString(Context context, int day) {
        if (day == 1)
            return context.getString(R.string.short_sunday);
        if (day == 2)
            return context.getString(R.string.short_monday);
        if (day == 3)
            return context.getString(R.string.short_tuesday);
        if (day == 4)
            return context.getString(R.string.short_wednesday);
        if (day == 5)
            return context.getString(R.string.short_thursday);
        if (day == 6)
            return context.getString(R.string.short_friday);
        if (day == 7)
            return context.getString(R.string.short_saturday);
        return "INVALID";
    }

    static String convertMonthIntToStringShort(Context context, int month) {
        if (month == 0)
            return context.getString(R.string.short_january);
        if (month == 1)
            return context.getString(R.string.short_february);
        if (month == 2)
            return context.getString(R.string.short_march);
        if (month == 3)
            return context.getString(R.string.short_april);
        if (month == 4)
            return context.getString(R.string.short_may);
        if (month == 5)
            return context.getString(R.string.short_june);
        if (month == 6)
            return context.getString(R.string.short_july);
        if (month == 7)
            return context.getString(R.string.short_august);
        if (month == 8)
            return context.getString(R.string.short_september);
        if (month == 9)
            return context.getString(R.string.short_october);
        if (month == 10)
            return context.getString(R.string.short_november);
        if (month == 11)
            return context.getString(R.string.short_december);

        return "INVALID";
    }

    static int convertMonthStringToInt(Context context, String month) {
        if (month.equals(context.getString(R.string.short_january)))
            return 0;
        if (month.equals(context.getString(R.string.short_february)))
            return 1;
        if (month.equals(context.getString(R.string.short_march)))
            return 2;
        if (month.equals(context.getString(R.string.short_april)))
            return 3;
        if (month.equals(context.getString(R.string.short_may)))
            return 4;
        if (month.equals(context.getString(R.string.short_june)))
            return 5;
        if (month.equals(context.getString(R.string.short_july)))
            return 6;
        if (month.equals(context.getString(R.string.short_august)))
            return 7;
        if (month.equals(context.getString(R.string.short_september)))
            return 8;
        if (month.equals(context.getString(R.string.short_october)))
            return 9;
        if (month.equals(context.getString(R.string.short_november)))
            return 10;
        if (month.equals(context.getString(R.string.short_december)))
            return 11;
        return -1;
    }


    static String convertMonthIntToStringFull(Context context, int month) {
        if (month == 0)
            return context.getString(R.string.full_january);
        if (month == 1)
            return context.getString(R.string.full_february);
        if (month == 2)
            return context.getString(R.string.full_march);
        if (month == 3)
            return context.getString(R.string.full_april);
        if (month == 4)
            return context.getString(R.string.full_may);
        if (month == 5)
            return context.getString(R.string.full_june);
        if (month == 6)
            return context.getString(R.string.full_july);
        if (month == 7)
            return context.getString(R.string.full_august);
        if (month == 8)
            return context.getString(R.string.full_september);
        if (month == 9)
            return context.getString(R.string.full_october);
        if (month == 10)
            return context.getString(R.string.full_november);
        if (month == 11)
            return context.getString(R.string.full_december);

        return "INVALID";
    }

    static String formattedDate(Context context, int dayOfWeek, int month, int dayOfMonth, int year) {
        return new StringBuilder()
                .append(convertDayIntToString(context, dayOfWeek)).append(", ").append(convertMonthIntToStringShort(context, month)).append(" ").append(dayOfMonth).append(", ").append(year).toString();
    }

    // Convert formatted Date to Calendar Object
    static Calendar parseDate(Context context, String date) {
        String month = date.substring(5, 8);
        int monInt = convertMonthStringToInt(context, month);
        int loc = date.indexOf(",", 8);
        int day = Integer.parseInt(date.substring(9, loc));
        int year = Integer.parseInt(date.substring(loc + 2));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monInt);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }

    static Calendar parseTime(String time) {
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        String AM_PM = time.substring(6);
        if (AM_PM.equals("PM"))
            hour = hour + 12;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal;
    }

    static StringBuilder formattedTime(int hourOfDay, int minute) {
        String AM_PM ;
        String minuteStr;
        String hourStr;
        if(hourOfDay < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
            hourOfDay = hourOfDay - 12;
        }

        if (hourOfDay < 10) {
            hourStr = "0" + hourOfDay;
        } else {
            hourStr = "" + hourOfDay;
        }

        if (minute < 10) {
            minuteStr = "0" + minute;
        }
        else {
            minuteStr = ""+minute;
        }

        return new StringBuilder()
                .append(hourStr).append(":").append(minuteStr).append(" ").append(AM_PM);
    }

    // Get Calendar Data for 2 years - One year before the current date to one year after the current date.
    static List<CalendarData> getCalendarTwoYears(Context context) {
        String date;
        List<CalendarData> calendarList = new ArrayList<CalendarData>();
        Calendar today = Calendar.getInstance(TimeZone.getDefault());
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int targetYear = currentYear + 1;
        calendar.add(Calendar.YEAR, -1);

        while (calendar.get(Calendar.DAY_OF_WEEK) != 1)
            calendar.add(Calendar.DATE, -1);

        while (calendar.get(Calendar.YEAR) != targetYear || calendar.get(Calendar.MONTH) != currentMonth || calendar.get(Calendar.DAY_OF_MONTH) != currentDay) {
            CalendarData data = new CalendarData();
            data.calendar = (Calendar) calendar.clone();
            boolean sameDay = today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
            if (sameDay)
                data.isCurrent = true;
            data.formattedDate = formattedDate(context, calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
            calendarList.add(data);
            calendar.add(Calendar.DATE, 1);
        }
        return calendarList;
    }

    // Get duration (less than 24 hours) in Formatted form.
    static String getDurationInFormattedString(Calendar start, Calendar end) {
        StringBuilder res = new StringBuilder();
        int minutes = 0;
        long seconds = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
        int hours = (int) (seconds / 3600);
        int remSecond = (int)(seconds % 3600);
        if (remSecond % 60 == 0) {
            minutes = remSecond / 60;
        }
        if (hours != 0) {
            res.append(hours).append(" h");
            if (minutes != 0) {
                res.append(" ").append(minutes).append(" m");
            }
        }
        else if (minutes != 0) {
            res.append(minutes).append(" m");
        }

        return res.toString();
    }

    static int getDurationInDays(Calendar start, Calendar end) {
        long seconds = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
        int days = (int) (seconds / 86400);
        return days;
    }

    public static int daysSince(Calendar startDate, Calendar endDate) {
        Calendar presentDate = (Calendar) startDate.clone();
        presentDate.set(Calendar.HOUR_OF_DAY, 0);
        presentDate.set(Calendar.MINUTE, 0);
        Calendar closeDate = (Calendar) endDate.clone();
        closeDate.set(Calendar.HOUR_OF_DAY, 0);
        closeDate.set(Calendar.MINUTE, 0);

        int daysSince = 0;

        while (presentDate.before(closeDate)) {
            presentDate.add(Calendar.DATE, 1);
            daysSince++;
        }
        return daysSince + 1;
    }
}
