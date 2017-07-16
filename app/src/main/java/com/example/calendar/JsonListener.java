package com.example.calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sbandyop on 7/15/2017.
 */
public class JsonListener implements Response.Listener<JSONObject>, Response.ErrorListener {

    WeatherLoader loader;
    TextView mTextView;
    ImageView mImageView;
    HashMap<String, WeatherData> mCache;
    Map<TextView, String> mTracker;
    String mUrl;
    Calendar mDate;
    private static final String TAG_DATE_LIST = "list";
    private static final String TAG_DATE = "dt";
    private static final String TAG_TEMPERATURE = "main";
    private static final String TAG_MIN_TEMP = "temp_min";
    private static final String TAG_MAX_TEMP = "temp_max";
    private static final String TAG_CURR_TEMP = "temp";
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_ICON_ID = "icon";
    private static final String CENTIGRADE_UNICODE = "℃";

    public JsonListener(TextView textView, ImageView imageView, Map<TextView, String> tracker, HashMap<String, WeatherData> cache, String url, Calendar date) {
        mTextView = textView;
        mImageView = imageView;
        mCache = cache;
        mTracker = tracker;
        mUrl = url;
        mDate = date;
    }

    @Override
    public void onResponse(JSONObject response) {
        String url = mTracker.get(mTextView);
        WeatherData data = getWeatherData(response);
        // Update the cache
        mCache.put(url, data);
        /* Check whether the textView has been reused by some other ListItem. In case
           it has been reused then do not update the view.
        */
        if (url == null || !url.equals(mUrl))
            return;
        mTextView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);
        populateUI(mTextView, mImageView, data);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mTextView.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
    }

    static void populateUI(TextView textView, ImageView imageView, WeatherData data) {
        if (data != null) {
            textView.setText(data.temp + CENTIGRADE_UNICODE);
            imageView.setImageResource(getResourceId(data.code));
        } else {
            textView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    WeatherData getWeatherData(JSONObject response) {
        long minimum = Long.MAX_VALUE;
        int minTemp = 0;
        String minIcon = "";

        try {
            JSONArray dates = response.getJSONArray(TAG_DATE_LIST);
            for (int i = 0; i < dates.length(); i++) {
                JSONObject date = dates.getJSONObject(i);
                long time = date.getLong(TAG_DATE) * 1000;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(time);
                JSONObject temp = date.getJSONObject(TAG_TEMPERATURE);
                int currTemp = 0;
                if (temp != null) {
                    int kelvinTemp = (int) temp.getDouble(TAG_CURR_TEMP);
                    currTemp = kelvinTemp - 273;
                }
                JSONArray weather = date.getJSONArray(TAG_WEATHER);
                String icon = weather.getJSONObject(0).getString(TAG_ICON_ID);
                if (Math.abs(cal.getTimeInMillis() - mDate.getTimeInMillis()) < minimum) {
                    minTemp = currTemp;
                    minIcon = icon;
                }
            }

        } catch (JSONException e) {
            return null;
        }
        WeatherData data = new WeatherData();
        data.temp = minTemp;
        data.code = minIcon;
        return data;
    }

    static int getResourceId(String code) {

        if (code.equals("01d"))
            return R.drawable.ic_weather_sunny_grey600_24dp;
        if (code.equals("01n"))
            return R.drawable.ic_weather_night_grey600_24dp;
        if (code.equals("02d") || code.equals("02n"))
            return R.drawable.ic_weather_partlycloudy_grey600_24dp;
        if (code.equals("03d") || code.equals("03n"))
            return R.drawable.ic_weather_partlycloudy_grey600_24dp;
        if (code.equals("04d") || code.equals("04n"))
            return R.drawable.ic_weather_cloudy_grey600_24dp;
        if (code.equals("09d") || code.equals("09n"))
            return R.drawable.ic_weather_pouring_grey600_24dp;
        if (code.equals("10d") || code.equals("10n"))
            return R.drawable.ic_weather_rainy_grey600_24dp;
        if (code.equals("11d") || code.equals("11n"))
            return R.drawable.ic_weather_lightning_rainy_grey600_24dp;
        if (code.equals("13d") || code.equals("13n"))
            return R.drawable.ic_weather_snowy_rainy_grey600_24dp;
        if (code.equals("50d") || code.equals("50n"))
            return R.drawable.ic_weather_fog_grey600_24dp;
        return R.drawable.ic_weather_sunny_grey600_24dp;
    }
}
