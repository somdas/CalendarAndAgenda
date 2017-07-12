package com.example.calendar;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MainActivity.LoaderData> {

    Context mContext = null;
    private final static String TAG = "MainActivity";
    private ListView mListView;
    private GridView mGridView;
    private GridView mHeaderView;
    List<AdapterContainer> containerList = new ArrayList<AdapterContainer>();
    List<CalendarData> calendarDataList = new ArrayList<CalendarData>();
    public static final String ACTION_RELOAD = "action_reload";
    public static final String ACTION_DATE_CLICK = "action_date_click";
    public static final String CAL_POSITION = "calendar_position";
    EventListAdapter eventListAdapter;
    CalendarDateAdapter calendarDateAdapter;
    GridHeaderAdapter headerAdapter;
    MainActivity mInstance;
    private int mGridIndex;
    Calendar mStartDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mInstance = this;
        mStartDate = Calendar.getInstance(TimeZone.getDefault());
        mStartDate.add(Calendar.YEAR, -1);
        while (mStartDate.get(Calendar.DAY_OF_WEEK) != 1)
            mStartDate.add(Calendar.DATE, -1);
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_IS_EDIT, false);
                intent.setClass(mContext, AddActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RELOAD);
        filter.addAction(ACTION_DATE_CLICK);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                filter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    LoaderData createContainerList() {
        List<AdapterContainer> containerListLocal = new ArrayList<AdapterContainer>();
        List<CalendarData> calendarDataLisLocal = new ArrayList<CalendarData>();
        calendarDataLisLocal = DateTimeUtils.getCalendarTwoYears(mContext);
        DataStore dataStore = DataStore.getInstance(this);
        dataStore.open();
        HashMap<String, List<Event>> eventMap = dataStore.getAllEvents();
        dataStore.close();
        for (CalendarData data : calendarDataLisLocal) {
            DayEventsData daysData = new DayEventsData();

            if (eventMap.containsKey(data.formattedDate)) {
                daysData.isNoEvent = false;
                daysData.eventList = eventMap.get(data.formattedDate);
                Collections.sort(daysData.eventList, new EventComparator());
            }

            AdapterContainer headerContainer = new AdapterContainer();
            headerContainer.isHeader = true;
            headerContainer.header = data.formattedDate;
            containerListLocal.add(headerContainer);

            if (daysData.isNoEvent) {
                AdapterContainer noEventContainer = new AdapterContainer();
                noEventContainer.isNoEvent = true;
                containerListLocal.add(noEventContainer);
            } else {
                for (Event event : daysData.eventList)
                {
                    AdapterContainer eventContainer = new AdapterContainer();
                    eventContainer.event = event;
                    containerListLocal.add(eventContainer);
                }
            }

        }
        LoaderData data = new LoaderData();
        data.adapterContainerList = containerListLocal;
        data.calendarDataList = calendarDataLisLocal;

        return data;
    }


    public Loader<LoaderData> onCreateLoader(int id, Bundle args) {
        return new AgendaLoader(this);
    }

    public void onLoadFinished(Loader<LoaderData> loader, LoaderData data) {
        containerList = data.adapterContainerList;
        calendarDataList = data.calendarDataList;
        setListView();
    }

    public void onLoaderReset(Loader<LoaderData> loader) {
    }


    public static class AgendaLoader extends AsyncTaskLoader<LoaderData> {

        LoaderData data;
        MainActivity activity;
        public AgendaLoader(MainActivity activity) {
            super(activity);
            this.activity = activity;
        }

        public LoaderData loadInBackground() {
            return activity.createContainerList();
        }

        public void deliverResult(LoaderData loaderData) {

            data = loaderData;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(loaderData);
            }
        }

        protected void onStartLoading() {
            if (data != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(data);
            }
            forceLoad();

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

        List<String> strList = new ArrayList<String>();
        strList.add(mContext.getString(R.string.short_sunday));
        strList.add(mContext.getString(R.string.short_monday));
        strList.add(mContext.getString(R.string.short_tuesday));
        strList.add(mContext.getString(R.string.short_wednesday));
        strList.add(mContext.getString(R.string.short_thursday));
        strList.add(mContext.getString(R.string.short_friday));
        strList.add(mContext.getString(R.string.short_saturday));

        eventListAdapter = new EventListAdapter(this, containerList);
        calendarDateAdapter = new CalendarDateAdapter(this, calendarDataList);
        headerAdapter = new GridHeaderAdapter(this, strList);
        setListAdapter(eventListAdapter);
        setGridAdapter(calendarDateAdapter);
        setHeaderAdapter(headerAdapter);
        final TextView monthHead = (TextView) findViewById(R.id.monthHead);

        getGridView().setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstVisiblePostion;

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstVisiblePostion = getGridView().getFirstVisiblePosition();

                runOnUiThread(new Runnable() {
                    public void run() {
                        CalendarData data = calendarDataList.get(firstVisiblePostion);
                        String month = DateTimeUtils.convertMonthIntToStringFull(mContext, data.calendar.get(Calendar.MONTH));
                        monthHead.setText(month);

                    }
                });
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final GridView lw = getGridView();

                if (scrollState == 0)
                    return;


                if (view.getId() == lw.getId()) {
                    getGridView().setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, (int) mContext.getResources().getDimension(R.dimen.long_cal_height)));
                }
            }
        });


        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {

            int firstVisiblePostion;

            public void onScroll(AbsListView view, final int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                firstVisiblePostion = getListView().getFirstVisiblePosition();

                runOnUiThread(new Runnable() {
                    public void run() {
                        if (containerList.size() > 0) {
                            while (!containerList.get(firstVisiblePostion).isHeader)
                                firstVisiblePostion = firstVisiblePostion - 1;

                            Calendar calendar = DateTimeUtils.parseDate(mContext, containerList.get(firstVisiblePostion).header);
                            int gridIndex = 1;
                            gridIndex = DateTimeUtils.getDurationInDays(mStartDate, calendar);
                            calendarDataList.get(gridIndex).isCurrent = true;
                            if (mGridIndex != gridIndex)
                                calendarDataList.get(mGridIndex).isCurrent = false;
                            mGridIndex = gridIndex;
                            calendarDateAdapter.notifyDataSetChanged();

                            getGridView().post(new Runnable() {
                                @Override
                                public void run() {
                                    getGridView().setSelection(mGridIndex);
                                }
                            });
                        }
                    }
                });
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final ListView lw = getListView();

                if (scrollState == 0)
                    return;

                if (view.getId() == lw.getId()) {
                    getGridView().setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, (int) mContext.getResources().getDimension(R.dimen.short_cal_height)));

                }
            }
        });

        String str = DateTimeUtils.formattedDate(mContext, dayOfWeek, month, day, year);
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

        int gridIndex = DateTimeUtils.getDurationInDays(mStartDate, cal);

        getGridView().setSelection(gridIndex);
        mGridIndex = gridIndex;
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                AdapterContainer container = containerList.get(position);
                if (container.isNoEvent) {
                    String header = containerList.get(position - 1).header;
                    Calendar calendar = DateTimeUtils.parseDate(mContext, header);
                    Calendar current = Calendar.getInstance();
                    calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                    Intent intent = new Intent();
                    intent.setClass(mContext, AddActivity.class);
                    intent.putExtra(Constants.KEY_IS_EDIT, false);
                    intent.putExtra(Constants.KEY_IS_DATE_FIXED, true);
                    intent.putExtra(Constants.KEY_START_DAY, calendar.get(Calendar.DAY_OF_MONTH));
                    intent.putExtra(Constants.KEY_START_MONTH, calendar.get(Calendar.MONTH));
                    intent.putExtra(Constants.KEY_START_YEAR, calendar.get(Calendar.YEAR));
                    intent.putExtra(Constants.KEY_START_HOUR, current.get(Calendar.HOUR));
                    intent.putExtra(Constants.KEY_START_MINUTE, current.get(Calendar.MINUTE));
                    startActivity(intent);
                } else if (!container.isNoEvent && !container.isHeader) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, AddActivity.class);
                    intent.putExtra(Constants.KEY_IS_EDIT, true);
                    if (!container.event.isAllDay) {
                        intent.putExtra(Constants.KEY_START_DAY, container.event.startDay);
                        intent.putExtra(Constants.KEY_START_MONTH, container.event.startMonth);
                        intent.putExtra(Constants.KEY_START_YEAR, container.event.startYear);

                    } else {
                        intent.putExtra(Constants.KEY_START_DAY, container.event.allDayStartDay);
                        intent.putExtra(Constants.KEY_START_MONTH, container.event.allDayStartMonth);
                        intent.putExtra(Constants.KEY_START_YEAR, container.event.allDayStartYear);
                    }
                    intent.putExtra(Constants.KEY_START_HOUR, container.event.startHour);
                    intent.putExtra(Constants.KEY_START_MINUTE, container.event.startMinute);
                    intent.putExtra(Constants.KEY_END_DAY, container.event.endDay);
                    intent.putExtra(Constants.KEY_END_MONTH, container.event.endMonth);
                    intent.putExtra(Constants.KEY_END_YEAR, container.event.endYear);
                    intent.putExtra(Constants.KEY_END_HOUR, container.event.endHour);
                    intent.putExtra(Constants.KEY_END_MINUTE, container.event.endMinute);

                    intent.putExtra(Constants.KEY_TITLE, container.event.title);
                    intent.putExtra(Constants.KEY_LOCATION, container.event.location);
                    intent.putExtra(Constants.KEY_DESC, container.event.description);
                    intent.putExtra(Constants.KEY_IS_ALL_DAY, container.event.isAllDay);
                    intent.putExtra(Constants.KEY_EVENT_ID, container.event.eventID);
                    startActivity(intent);
                }
            }
        });

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_RELOAD)) {
                containerList.clear();
                getLoaderManager().restartLoader(0, null, mInstance);
                eventListAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(ACTION_DATE_CLICK)) {
                int gridPos = intent.getIntExtra(CAL_POSITION, 0);
                Calendar calendar = calendarDataList.get(gridPos).calendar;
                String str = DateTimeUtils.formattedDate(mContext, calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
                int index = 1;
                for (int i = 0; i < containerList.size(); i++) {
                    if (containerList.get(i).isHeader) {
                        if (containerList.get(i).header.equals(str)) {
                            index = i;
                            break;
                        }

                    }
                }
                getListView().setSelection(index);
            }

        }
    };

    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }

    protected GridView getGridView() {
        if (mGridView == null) {
            mGridView = (GridView) findViewById(R.id.gridView1);
        }
        return mGridView;
    }

    protected GridView getHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = (GridView) findViewById(R.id.gridViewHeader);
        }
        return mHeaderView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected void setGridAdapter(ListAdapter adapter) {
        getGridView().setAdapter(adapter);
    }

    protected void setHeaderAdapter(ListAdapter adapter) {
        getHeaderView().setAdapter(adapter);
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
            getGridView().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int) mContext.getResources().getDimension(R.dimen.short_cal_height)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class LoaderData {
        List<AdapterContainer> adapterContainerList;
        List<CalendarData> calendarDataList;
    }

    class EventComparator implements Comparator<Event> {
        public int compare(Event a, Event b) {
            if (a.isAllDay && !b.isAllDay)
                return -1;
            if (!a.isAllDay && b.isAllDay)
                return 1;
            if (a.isAllDay && b.isAllDay)
                return 0;

            Calendar first = new GregorianCalendar(a.startYear, a.startMonth, a.startDay, a.startHour, a.startMinute);
            Calendar second = new GregorianCalendar(b.startYear, b.startMonth, b.startDay, b.startHour, b.startMinute);

            if (first.before(second))
                return -1;
            if (second.before(first))
                return 1;
            return 0;
        }
    }
}
