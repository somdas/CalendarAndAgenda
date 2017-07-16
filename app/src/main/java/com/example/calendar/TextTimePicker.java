package com.example.calendar;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TextTimePicker implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    TextView timeView;
    int hour;
    int minute;
    private Context context;
    TextTimePicker endTimePicker;

    public TextTimePicker(Context context, int dateViewID, TextTimePicker endTimePicker) {
        Activity act = (Activity)context;
        this.timeView = (TextView) act.findViewById(dateViewID);
        this.timeView.setOnClickListener(this);
        this.context = context;
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        this.endTimePicker = endTimePicker;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        hour = selectedHour;
        minute = selectedMinute;
        if (endTimePicker != null) {
            endTimePicker.hour = hour;
            endTimePicker.minute = minute;
        }
        updateDisplay();
    }

    @Override
    public void onClick(View v) {
        String timeSet = timeView.getText().toString();
        Calendar calendar = DateTimeUtils.parseTime(timeSet);
        TimePickerDialog dialog = new TimePickerDialog(context, this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        dialog.show();
    }

    public void removeClickListener()
    {
        this.timeView.setOnClickListener(null);
    }

    private void updateDisplay() {
        timeView.setText(DateTimeUtils.formattedTime(hour, minute));
        if (endTimePicker != null)
            endTimePicker.timeView.setText(DateTimeUtils.formattedTime(hour, minute));
    }
}