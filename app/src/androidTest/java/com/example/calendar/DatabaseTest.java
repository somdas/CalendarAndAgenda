package com.example.calendar;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    DataStore store;

    @Before
    public void setUp() {
        store = DataStore.getInstance(InstrumentationRegistry.getTargetContext());
        store.open();
        store.deleteAll();
    }

    @After
    public void finish() {
        store.close();
    }

    @Test
    public void testDatabaseInsertBasic() throws Exception {
        store.createAgenda(1, 0, 2017, 1, 0, 1, 0, 2017, 2, 0, "TestTitle1", "TestLoc1", false, "", 15);
        HashMap<String, List<Event>> map = store.getAllEvents();
        assertThat(map.size(), is(1));
        Calendar calendar = new GregorianCalendar(2017, 0, 1);
        String formattedDate = DateTimeUtils.formattedDate(InstrumentationRegistry.getTargetContext(), calendar.get(Calendar.DAY_OF_WEEK), 0, 1, 2017);
        List<Event> eventList = map.get(formattedDate);
        assertThat(eventList, is(notNullValue()));
        store.deleteAll();
    }

    @Test
    public void testDatabaseInsertAllDay() throws Exception {
        store.createAgenda(2, 0, 2017, 1, 0, 4, 0, 2017, 2, 0, "TestTitle2", "TestLoc2", true, "", 15);
        HashMap<String, List<Event>> map = store.getAllEvents();
        Calendar calendar = new GregorianCalendar(2017, 0, 2);
        String formattedDate = DateTimeUtils.formattedDate(InstrumentationRegistry.getTargetContext(), calendar.get(Calendar.DAY_OF_WEEK), 0, 2, 2017);
        List<Event> eventList = map.get(formattedDate);
        assertThat(eventList, is(notNullValue()));
        assertThat(eventList.get(0).startDay, is(2));
        calendar.add(Calendar.DATE, 1);
        formattedDate = DateTimeUtils.formattedDate(InstrumentationRegistry.getTargetContext(), calendar.get(Calendar.DAY_OF_WEEK), 0, 3, 2017);
        eventList = map.get(formattedDate);
        assertThat(eventList.get(0).startDay, is(3));
        calendar.add(Calendar.DATE, 1);
        formattedDate = DateTimeUtils.formattedDate(InstrumentationRegistry.getTargetContext(), calendar.get(Calendar.DAY_OF_WEEK), 0, 4, 2017);
        eventList = map.get(formattedDate);
        assertThat(eventList.get(0).startDay, is(4));
        calendar.add(Calendar.DATE, 1);
        formattedDate = DateTimeUtils.formattedDate(InstrumentationRegistry.getTargetContext(), calendar.get(Calendar.DAY_OF_WEEK), 0, 5, 2017);
        eventList = map.get(formattedDate);
        assertThat(eventList, is(nullValue()));
        assertThat(map.size(), is(3));
        store.deleteAll();
    }

    @Test
    public void testDatabaseNotificationData() throws Exception {
        store.createAgenda(1, 0, 2017, 1, 0, 1, 0, 2017, 2, 0, "TestTitle1", "TestLoc1", false, "", 15);
        store.createAgenda(3, 0, 2017, 1, 30, 3, 0, 2017, 2, 30, "TestTitle2", "TestLoc2", false, "", 30);
        store.createAgenda(2, 0, 2017, 1, 0, 4, 0, 2017, 2, 0, "TestTitle3", "TestLoc3", true, "", 15);
        List<NotificationData> notificationList = store.getNotificationData();
        assertThat(notificationList.size(), is(3));
        assertThat(notificationList.get(0).title, is("TestTitle1"));
        assertThat(notificationList.get(1).reminderTime, is(30));
        assertThat(notificationList.get(2).startHour, is(0));
        store.deleteAll();
    }

    @Test
    public void testDatabaseLastColumnID() throws Exception {
        store.createAgenda(1, 0, 2017, 1, 0, 1, 0, 2017, 2, 0, "TestTitle1", "TestLoc1", false, "", 15);
        store.createAgenda(3, 0, 2017, 1, 30, 3, 0, 2017, 2, 30, "TestTitle2", "TestLoc2", false, "", 30);
        int id1 = store.getLastColumnID();
        store.createAgenda(2, 0, 2017, 1, 0, 4, 0, 2017, 2, 0, "TestTitle3", "TestLoc3", true, "", 15);
        int id2 = store.getLastColumnID();
        store.deleteEvent(id1);
        store.createAgenda(3, 0, 2017, 2, 15, 3, 0, 2017, 2, 30, "TestTitle4", "TestLoc2", false, "", 30);
        int id3 = store.getLastColumnID();
        assertThat(id3 > id2, is(true));
        store.deleteAll();
    }

    @Test
    public void testDatabaseDeleteEvent() throws Exception {
        store.createAgenda(1, 0, 2017, 1, 0, 1, 0, 2017, 2, 0, "TestTitle1", "TestLoc1", false, "", 15);
        store.createAgenda(3, 0, 2017, 1, 30, 3, 0, 2017, 2, 30, "TestTitle2", "TestLoc2", false, "", 30);
        int id1 = store.getLastColumnID();
        store.createAgenda(5, 0, 2017, 1, 0, 5, 0, 2017, 2, 0, "TestTitle3", "TestLoc3", false, "", 15);
        Calendar calendar = new GregorianCalendar(2017, 0, 3);
        HashMap<String, List<Event>> map = store.getAllEvents();
        String formattedDate = DateTimeUtils.formattedDate(InstrumentationRegistry.getTargetContext(), calendar.get(Calendar.DAY_OF_WEEK), 0, 3, 2017);
        List<Event> eventList = map.get(formattedDate);
        assertThat(eventList, is(notNullValue()));
        store.deleteEvent(id1);
        map = store.getAllEvents();
        eventList = map.get(formattedDate);
        assertThat(eventList, is(nullValue()));
        store.deleteAll();
    }

}