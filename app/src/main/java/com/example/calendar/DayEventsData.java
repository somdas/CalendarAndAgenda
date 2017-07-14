package com.example.calendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbandyop on 7/5/2017.
 */
// Container for all events (if any) of a particular day
public class DayEventsData
{
    public boolean isNoEvent = true;
    public List<Event> eventList = new ArrayList<Event>();
}
