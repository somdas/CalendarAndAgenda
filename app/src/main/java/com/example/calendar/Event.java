package com.example.calendar;

/**
 * Created by sbandyop on 7/5/2017.
 */
// Container for event data
public class Event
{
    public Event(int eventID, int startDay, int startMonth, int startYear, int startHour, int startMinute, int endDay, int endMonth, int endYear, int endHour, int endMinute, String title, String location, boolean isAllDay, int daysLeft, String description, int reminder)
    {
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.startYear = startYear;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.title = title;
        this.location = location;
        this.isAllDay = isAllDay;
        this.daysLeft = daysLeft;
        this.description = description;
        this.eventID = eventID;
        this.reminder = reminder;
    }

    public int eventID;
    public int startDay, startMonth, startYear, startHour, startMinute;
    public int endDay, endMonth, endYear, endHour, endMinute;
    public String title;
    public String location;
    public boolean isAllDay;
    public int daysLeft;
    public String description;
    public int allDayStartDay;
    public int allDayStartMonth;
    public int allDayStartYear;
    public int reminder;
}
