package com.example.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TextDatePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    TextView dateView;
    int day;
    int month;
    int year;
    int dayOfWeek;
    private Context context;
    TextDatePicker endDatePicker;

    public TextDatePicker(Context context, int dateViewID, TextDatePicker endDatePicker) {
        Activity act = (Activity)context;
        this.dateView = (TextView)act.findViewById(dateViewID);
        this.dateView.setOnClickListener(this);
        this.context = context;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        this.endDatePicker = endDatePicker;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        month = monthOfYear;
        day = dayOfMonth;
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (endDatePicker != null) {
            endDatePicker.day = day;
            endDatePicker.month = month;
            endDatePicker.year = year;
        }

        updateDisplay();
    }

    @Override
    public void onClick(View v) {
        //Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String dateSet = dateView.getText().toString();
        Calendar calendar = DateTimeUtils.parseDate(context, dateSet);

        DatePickerDialog dialog = new DatePickerDialog(context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void updateDisplay() {
        dateView.setText(DateTimeUtils.formattedDate(context, dayOfWeek, month, day, year));
        if (endDatePicker != null)
            endDatePicker.dateView.setText(DateTimeUtils.formattedDate(context, dayOfWeek, month, day, year));
    }


}