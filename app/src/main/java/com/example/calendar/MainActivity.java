package com.example.calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<AdapterContainer>> {

    Context mContext = null;
    private final static String TAG = "MainActivity";
    private ListView mListView;
    List<String> dates;
    List<AdapterContainer> containerList = new ArrayList<AdapterContainer>();
    public static final String ACTION_RELOAD = "action_reload";
    EventListAdapter eventListAdapter;
    MainActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mInstance = this;
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(mContext, AddActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ACTION_RELOAD));

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    List<AdapterContainer> createContainerList() {
        dates =  DateTimeUtils.getDatesTwoYears();
        DataStore dataStore = DataStore.getInstance(this);
        dataStore.open();
        HashMap<String, List<Event>> eventMap = dataStore.getAllEvents();
        dataStore.close();
        for (String str : dates) {
            DayEventsData daysData = new DayEventsData();

            if (eventMap.containsKey(str)) {
                daysData.isNoEvent = false;
                daysData.eventList = eventMap.get(str);
                Collections.sort(daysData.eventList, new EventComparator());
            }

            AdapterContainer headerContainer = new AdapterContainer();
            headerContainer.isHeader = true;
            headerContainer.header = str;
            containerList.add(headerContainer);

            if (daysData.isNoEvent) {
                AdapterContainer noEventContainer = new AdapterContainer();
                noEventContainer.isNoEvent = true;
                containerList.add(noEventContainer);
            } else {
                for (Event event : daysData.eventList)
                {
                    AdapterContainer eventContainer = new AdapterContainer();
                    eventContainer.event = event;
                    containerList.add(eventContainer);
                }
            }

        }

        return containerList;
    }


    public Loader<List<AdapterContainer>> onCreateLoader(int id, Bundle args) {
        return new AgendaLoader(this);
    }

    public void onLoadFinished(Loader<List<AdapterContainer>> loader, List<AdapterContainer> data) {
        containerList = data;
        setListView();
    }

    public void onLoaderReset(Loader<List<AdapterContainer>> loader) {
        containerList = null;
    }


    public static class AgendaLoader extends AsyncTaskLoader<List<AdapterContainer>> {

        List<AdapterContainer> adapterContainerList;
        MainActivity activity;
        public AgendaLoader(MainActivity activity) {
            super(activity);
            this.activity = activity;
        }

        public List<AdapterContainer> loadInBackground() {
            return activity.createContainerList();
        }

        public void deliverResult(List<AdapterContainer> containers) {

            adapterContainerList = containers;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(containers);
            }
        }

        protected void onStartLoading() {
            if (adapterContainerList != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(adapterContainerList);
            }
            forceLoad();

        }
    }


    class EventComparator implements Comparator<Event>
    {
        public int compare(Event a, Event b)
        {
            if (a.isAllDay && !b.isAllDay)
                return -1;
            if (!a.isAllDay && b.isAllDay)
                return 1;
            if (a.isAllDay && b.isAllDay)
                return 0;

            Calendar first = new GregorianCalendar(a.startYear,a.startMonth, a.startDay, a.startHour, a.startMinute);
            Calendar second = new GregorianCalendar(b.startYear,b.startMonth, b.startDay, b.startHour, b.startMinute);

            if (first.before(second))
                return -1;
            if (second.before(first))
                return 1;
            return 0;
        }
    }

    private void setListView()
    {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        int day, month, year, dayOfWeek;
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        eventListAdapter = new EventListAdapter(this, containerList);
        setListAdapter(eventListAdapter);
        String str = DateTimeUtils.formattedDate(dayOfWeek, month, day, year);
        int index = 1;
        for (int i = 0; i < containerList.size(); i++)
        {
            if (containerList.get(i).isHeader)
            {
                if (containerList.get(i).header.equals(str))
                {
                    index = i;
                    break;
                }

            }
        }
        getListView().setSelection(index);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                AdapterContainer container = containerList.get(position);
                if (container.isNoEvent)
                {
                    Toast.makeText(mContext, "This is NO EVENT!",
                            Toast.LENGTH_LONG).show();
                } else if (!container.isNoEvent && !container.isHeader)
                {
                    Toast.makeText(mContext, "This is EVENT!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            containerList.clear();
            getLoaderManager().restartLoader(0, null, mInstance);
            eventListAdapter.notifyDataSetChanged();

        }
    };

    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
