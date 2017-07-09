package com.example.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TextTimePicker implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    TextView dateView;
    int hour;
    int minute;
    private Context context;

    public TextTimePicker(Context context, int dateViewID)
    {
        Activity act = (Activity)context;
        this.dateView = (TextView)act.findViewById(dateViewID);
        this.dateView.setOnClickListener(this);
        this.context = context;
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

        hour = selectedHour;
        minute = selectedMinute;
        updateDisplay();
    }
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        TimePickerDialog dialog = new TimePickerDialog(context, this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        dialog.show();

    }

    public void removeClickListener()
    {
        this.dateView.setOnClickListener(null);
    }

    // updates the date in the birth date EditText
    private void updateDisplay() {
        dateView.setText(DateTimeUtils.formattedTime(hour, minute));
    }


}