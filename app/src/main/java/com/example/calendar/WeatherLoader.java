package com.example.calendar;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;

/**
 * Created by sbandyop on 7/15/2017.
 */
public class WeatherLoader {

    private Map<TextView, String> tracker = Collections.synchronizedMap(
            new WeakHashMap<TextView, String>());
    HashMap<String, WeatherData> cache = new HashMap<String, WeatherData>();
    RequestQueue mRequestQueue;
    Context mContext;
    private static final int MAX_SLOTS_FORECAST_AVAILABLE = 40;
    private static final String APP_ID = "0454c26fe5d814263bf77ad7e85cd440";
    private static final double NUMBER_OF_SECONDS_IN_THREE_HOURS = 10800.0;

    public WeatherLoader(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mContext = context;
    }


    public void displayWeather(String location, Calendar date, ImageView imageView, TextView textView, boolean onlyStoreRef) {
        if (onlyStoreRef) {
            tracker.put(textView, null);
            return;
        }
        int count = (int) numberOfThreeHourSlots(Calendar.getInstance(TimeZone.getDefault()), date) + 1;
        if (count > MAX_SLOTS_FORECAST_AVAILABLE || count <= 0) {
            tracker.put(textView, null);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder().append("http://api.openweathermap.org/data/2.5/forecast?q=").append(location).append("&cnt=").append(count).append("&APPID=").append(APP_ID);
        String url = stringBuilder.toString();
        tracker.put(textView, url);
        if (cache.containsKey(url)) {
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            JsonListener.populateUI(textView, imageView, cache.get(url));
        } else {
            JsonListener listener = new JsonListener(textView, imageView, tracker, cache, url, date);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(url, null, listener, listener);
            mRequestQueue.add(jsObjRequest);
        }
    }

    private long numberOfThreeHourSlots(Calendar start, Calendar end) {
        long seconds = (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
        double slots = seconds / NUMBER_OF_SECONDS_IN_THREE_HOURS;
        return Math.round(slots);
    }
}
