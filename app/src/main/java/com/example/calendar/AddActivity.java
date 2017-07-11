package com.example.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddActivity extends AppCompatActivity {

    TextDatePicker datePicker = null;
    TextDatePicker datePicker1;
    TextDatePicker datePicker2;

    TextTimePicker timePicker1;
    TextTimePicker timePicker2;
    TextView time1;
    TextView time2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setTitle(getString(R.string.new_event));
        setDateTime();
        handleSwitchChange();
    }

    private void setDateTime() {
        int day, month, year, dayOfWeek, hour, minute;
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        TextView date1 = (TextView)findViewById(R.id.date1);
        TextView date2 = (TextView)findViewById(R.id.date2);
        date1.setText(DateTimeUtils.formattedDate(this, dayOfWeek, month, day, year));
        date2.setText(DateTimeUtils.formattedDate(this, dayOfWeek, month, day, year));

        time1 = (TextView)findViewById(R.id.time1);
        time2 = (TextView)findViewById(R.id.time2);

        time1.setText((DateTimeUtils.formattedTime(hour, minute)));
        time2.setText((DateTimeUtils.formattedTime(hour, minute)));

        datePicker1 = new TextDatePicker(this, R.id.date1);
        datePicker2 = new TextDatePicker(this, R.id.date2);

        timePicker1 = new TextTimePicker(this, R.id.time1);
        timePicker2 = new TextTimePicker(this, R.id.time2);
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
                } else {
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
            boolean isValid = isFormValid();
            if (!isValid) {
                Toast.makeText(this, getResources().getString(R.string.end_before_start),
                        Toast.LENGTH_LONG).show();
            } else {
                Calendar start = new GregorianCalendar(datePicker1.year, datePicker1.month, datePicker1.day, timePicker1.hour, timePicker1.minute);
                Calendar end = new GregorianCalendar(datePicker1.year, datePicker1.month, datePicker1.day, timePicker1.hour, timePicker1.minute);
                EditText title = (EditText) findViewById(R.id.editTitle);
                EditText location = (EditText) findViewById(R.id.editLocation);
                EditText description = (EditText) findViewById(R.id.editDesc);
                Switch isAllDay = (Switch) findViewById(R.id.toggBtn);

                DataStore dataStore = DataStore.getInstance(this);
                dataStore.open();
                dataStore.createAgenda(datePicker1.day, datePicker1.month, datePicker1.year, timePicker1.hour, timePicker1.minute, datePicker2.day, datePicker2.month, datePicker2.year, timePicker2.hour, timePicker2.minute, title.getText().toString(), location.getText().toString(), isAllDay.isChecked(), description.getText().toString());
                dataStore.close();

                Intent intent = new Intent(MainActivity.ACTION_RELOAD);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                finish();
            }

        }
        return true;
    }

    private boolean isFormValid() {
        Calendar start = new GregorianCalendar(datePicker1.year, datePicker1.month, datePicker1.day, timePicker1.hour, timePicker1.minute);
        Calendar end = new GregorianCalendar(datePicker2.year, datePicker2.month, datePicker2.day, timePicker2.hour, timePicker2.minute);
        if (end.before(start))
            return false;
        else
            return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }
}
