package com.example.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by sbandyop on 7/4/2017.
 */
public class DateTimeUtils {

    static String convertDayIntToString(int day) {
        if (day == 1)
            return "Sun";
        if (day == 2)
            return "Mon";
        if (day == 3)
            return "Tue";
        if (day == 4)
            return "Wed";
        if (day == 5)
            return "Thu";
        if (day == 6)
            return "Fri";
        if (day == 7)
            return "Sat";

        return "INVALID";
    }

    static String convertMonthIntToString(int month) {
        if (month == 0)
            return "Jan";
        if (month == 1)
            return "Feb";
        if (month == 2)
            return "Mar";
        if (month == 3)
            return "Apr";
        if (month == 4)
            return "May";
        if (month == 5)
            return "Jun";
        if (month == 6)
            return "Jul";
        if (month == 7)
            return "Aug";
        if (month == 8)
            return "Sep";
        if (month == 9)
            return "Oct";
        if (month == 10)
            return "Nov";
        if (month == 11)
            return "Dec";

        return "INVALID";
    }

    static String formattedDate(int dayOfWeek, int month, int dayOfMonth, int year) {
        return new StringBuilder()
                .append(convertDayIntToString(dayOfWeek)).append(", ").append(convertMonthIntToString(month)).append(" ").append(dayOfMonth).append(", ").append(year).toString();
    }

    static StringBuilder formattedTime(int hourOfDay, int minute) {
        String AM_PM ;
        String minuteStr;
        if(hourOfDay < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
            hourOfDay = hourOfDay - 12;
        }

        if (minute < 10) {
            minuteStr = "0" + minute;
        }
        else {
            minuteStr = ""+minute;
        }

        return new StringBuilder()
                .append(hourOfDay).append(":").append(minuteStr).append(" ").append(AM_PM);
    }


    static List<String> getDatesTwoYears() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int targetYear = currentYear + 1;
        calendar.add(Calendar.YEAR, -1);
        List<String> dates = new ArrayList<String>();

        while (calendar.get(Calendar.YEAR) != targetYear || calendar.get(Calendar.MONTH) != currentMonth || calendar.get(Calendar.DAY_OF_MONTH) != currentDay) {
            dates.add(formattedDate(calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    static String getDuration(Calendar start, Calendar end) {
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

    public static int daysSince(Calendar startDate, Calendar endDate) {
        Calendar presentDate = (Calendar) startDate.clone();

        int daysSince = 0;

        while (presentDate.before(endDate)) {
            presentDate.add(Calendar.DAY_OF_MONTH, 1);
            daysSince++;
        }
        return daysSince + 1;
    }
}
