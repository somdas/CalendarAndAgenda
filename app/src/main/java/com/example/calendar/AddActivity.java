package com.example.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AddActivity extends AppCompatActivity {

    TextDatePicker datePicker = null;
    TextDatePicker datePicker1;
    TextDatePicker datePicker2;

    TextTimePicker timePicker1;
    TextTimePicker timePicker2;
    TextView time1;
    TextView time2;
    private boolean isEdit = false;
    private boolean isDateFixed = false;
    private String mTitle, mLocation, mDescription;
    private int mStartDay, mStartMonth, mStartYear, mStartHour, mStartMinute, mEndDay, mEndMonth, mEndYear, mEndHour, mEndMinute;
    private boolean mIsAllDay = false;
    private int mEventId = 0;
    private int mReminder;
    private static final int NO_ERR = 0;
    private static final int ERR_START_AFTER_END = 1;
    private static final int ERR_TIME_MORE_24 = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setTitle(getString(R.string.new_event));

        ArrayAdapter<String> adapter = null;
        String items[] = getResources().getStringArray(R.array.spinnerItems);

        adapter = new ArrayAdapter<String>(this, R.layout.spinnerview, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        android.support.v7.widget.AppCompatSpinner spinner = (android.support.v7.widget.AppCompatSpinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);


        Bundle existingInfo = getIntent().getExtras();
        if (existingInfo != null) {
            isEdit = existingInfo.getBoolean(Constants.KEY_IS_EDIT);
            isDateFixed = existingInfo.getBoolean(Constants.KEY_IS_DATE_FIXED);
            if (isEdit) {
                mTitle = existingInfo.getString(Constants.KEY_TITLE);
                mLocation = existingInfo.getString(Constants.KEY_LOCATION);
                mDescription = existingInfo.getString(Constants.KEY_DESC);
                mIsAllDay = existingInfo.getBoolean(Constants.KEY_IS_ALL_DAY);
                mStartDay = existingInfo.getInt(Constants.KEY_START_DAY);
                mStartMonth = existingInfo.getInt(Constants.KEY_START_MONTH);
                mStartYear = existingInfo.getInt(Constants.KEY_START_YEAR);
                mStartHour = existingInfo.getInt(Constants.KEY_START_HOUR);
                mStartMinute = existingInfo.getInt(Constants.KEY_START_MINUTE);

                mEndDay = existingInfo.getInt(Constants.KEY_END_DAY);
                mEndMonth = existingInfo.getInt(Constants.KEY_END_MONTH);
                mEndYear = existingInfo.getInt(Constants.KEY_END_YEAR);
                mEndHour = existingInfo.getInt(Constants.KEY_END_HOUR);
                mEndMinute = existingInfo.getInt(Constants.KEY_END_MINUTE);
                mEventId = existingInfo.getInt(Constants.KEY_EVENT_ID);
                mReminder = existingInfo.getInt(Constants.KEY_REMINDER);
            } else if (isDateFixed) {
                mStartDay = existingInfo.getInt(Constants.KEY_START_DAY);
                mStartMonth = existingInfo.getInt(Constants.KEY_START_MONTH);
                mStartYear = existingInfo.getInt(Constants.KEY_START_YEAR);
                mStartHour = existingInfo.getInt(Constants.KEY_START_HOUR);
                mStartMinute = existingInfo.getInt(Constants.KEY_START_MINUTE);
            }
        }
        setDateTime();
        if (isEdit)
            fillForm();
        handleSwitchChange();
    }

    private void setDateTime() {
        int day, month, year, dayOfWeek, hour, minute;
        Calendar cal;
        if (!isEdit && !isDateFixed)
            cal = Calendar.getInstance();
        else
            cal = new GregorianCalendar(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        TextView date1 = (TextView)findViewById(R.id.date1);
        date1.setText(DateTimeUtils.formattedDate(this, dayOfWeek, month, day, year));
        time1 = (TextView)findViewById(R.id.time1);
        time1.setText((DateTimeUtils.formattedTime(hour, minute)));
        datePicker1 = new TextDatePicker(this, R.id.date1);
        datePicker1.day = day;
        datePicker1.month = month;
        datePicker1.year = year;
        timePicker1 = new TextTimePicker(this, R.id.time1);
        timePicker1.hour = hour;
        timePicker1.minute = minute;

        if (isEdit) {
            cal = new GregorianCalendar(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute);
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH);
            year = cal.get(Calendar.YEAR);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
        }

        TextView date2 = (TextView) findViewById(R.id.date2);
        date2.setText(DateTimeUtils.formattedDate(this, dayOfWeek, month, day, year));
        time2 = (TextView) findViewById(R.id.time2);
        time2.setText((DateTimeUtils.formattedTime(hour, minute)));


        datePicker2 = new TextDatePicker(this, R.id.date2);
        datePicker2.day = day;
        datePicker2.month = month;
        datePicker2.year = year;

        timePicker2 = new TextTimePicker(this, R.id.time2);
        timePicker2.hour = hour;
        timePicker2.minute = minute;

        if (isEdit && mIsAllDay) {
            timePicker1.removeClickListener();
            time1.setText("");
            timePicker2.removeClickListener();
            time2.setText("");
        }
    }

    private void fillForm() {
        EditText title = (EditText) findViewById(R.id.editTitle);
        EditText location = (EditText) findViewById(R.id.editLocation);
        EditText description = (EditText) findViewById(R.id.editDesc);
        Switch isAllDay = (Switch) findViewById(R.id.toggBtn);
        title.setText(mTitle);
        location.setText(mLocation);
        description.setText(mDescription);
        isAllDay.setChecked(mIsAllDay);
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        int pos = getPosition(mReminder);
        mySpinner.setSelection(pos);
    }

    private void handleSwitchChange() {
        Switch onOffSwitch = (Switch)  findViewById(R.id.toggBtn);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AddActivity.this.time1.post(new Runnable() {
                        public void run() {
                            timePicker1.removeClickListener();
                            time1.setText("");
                        }
                    });

                    AddActivity.this.time2.post(new Runnable() {
                        public void run() {
                            timePicker2.removeClickListener();
                            time2.setText("");
                        }
                    });
                    mIsAllDay = true;
                } else {
                    mIsAllDay = false;
                    setDateTime();
                }
            }

        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.done) {
            handleSaveClick();
        }

        return true;
    }

    private void handleSaveClick() {
        int res = isFormValid();
        if (res == ERR_START_AFTER_END) {
            Toast.makeText(this, getResources().getString(R.string.end_before_start),
                    Toast.LENGTH_LONG).show();
        } else if (res == ERR_TIME_MORE_24) {
            Toast.makeText(this, getResources().getString(R.string.time_more_than_24),
                    Toast.LENGTH_LONG).show();
        } else {
            Calendar start = new GregorianCalendar(datePicker1.year, datePicker1.month, datePicker1.day, timePicker1.hour, timePicker1.minute);
            Calendar end = new GregorianCalendar(datePicker1.year, datePicker1.month, datePicker1.day, timePicker1.hour, timePicker1.minute);
            Calendar now = Calendar.getInstance(TimeZone.getDefault());
            EditText title = (EditText) findViewById(R.id.editTitle);
            EditText location = (EditText) findViewById(R.id.editLocation);
            EditText description = (EditText) findViewById(R.id.editDesc);
            Switch isAllDay = (Switch) findViewById(R.id.toggBtn);

            DataStore dataStore = DataStore.getInstance(this);
            dataStore.open();
            if (isEdit)
                dataStore.deleteEvent(mEventId);

            Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
            int pos = mySpinner.getSelectedItemPosition();
            int reminderInSec = getReminderTime(pos);
            dataStore.createAgenda(datePicker1.day, datePicker1.month, datePicker1.year, timePicker1.hour, timePicker1.minute, datePicker2.day, datePicker2.month, datePicker2.year, timePicker2.hour, timePicker2.minute, title.getText().toString(), location.getText().toString(), isAllDay.isChecked(), description.getText().toString(), reminderInSec);
            int columnId = dataStore.getLastColumnID();
            dataStore.close();


            if (reminderInSec != -1) {
                start.add(Calendar.MINUTE, -reminderInSec);
                if (start.after(now)) {
                    Intent alarmIntent = new Intent(this,
                            AlarmReceiver.class);
                    String titleStr = title.getText().toString();
                    alarmIntent.putExtra(Constants.KEY_TITLE, titleStr);
                    alarmIntent.putExtra(Constants.KEY_TIME, DateTimeUtils.formattedTime(timePicker1.hour, timePicker1.minute).toString());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this, columnId,
                            alarmIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            start.getTimeInMillis(),
                            pendingIntent);
                    if (isEdit) {
                        PendingIntent oldIntent = PendingIntent.getBroadcast(
                                this, mEventId,
                                alarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.cancel(oldIntent);
                    }
                }
            }

            Intent intent = new Intent(MainActivity.ACTION_RELOAD);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            finish();
        }
    }

    private int getReminderTime(int pos) {
        if (pos == 0)
            return -1;
        if (pos == 1)
            return 15;
        if (pos == 2)
            return 30;
        if (pos == 3)
            return 60;
        if (pos == 4)
            return 120;
        if (pos == 5)
            return 300;
        return -1;
    }

    private int getPosition(int minute) {
        if (minute == -1)
            return 0;
        if (minute == 15)
            return 1;
        if (minute == 30)
            return 2;
        if (minute == 60)
            return 3;
        if (minute == 120)
            return 4;
        if (minute == 300)
            return 5;
        return -1;
    }

    private int isFormValid() {
        Calendar start = new GregorianCalendar(datePicker1.year, datePicker1.month, datePicker1.day, timePicker1.hour, timePicker1.minute);
        Calendar end = new GregorianCalendar(datePicker2.year, datePicker2.month, datePicker2.day, timePicker2.hour, timePicker2.minute);
        if (end.before(start))
            return ERR_START_AFTER_END;
        Switch onOffSwitch = (Switch) findViewById(R.id.toggBtn);
        if (!onOffSwitch.isChecked()) {
            long seconds = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
            if (seconds > 86400)
                return ERR_START_AFTER_END;
        }
        return NO_ERR;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }
}
