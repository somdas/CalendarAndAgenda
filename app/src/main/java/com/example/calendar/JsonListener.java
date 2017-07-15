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
    private static final String TAG_TEMPERATURE = "temp";
    private static final String TAG_MIN_TEMP = "min";
    private static final String TAG_MAX_TEMP = "max";
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
        mCache.put(url, data);
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

        try {
            JSONArray dates = response.getJSONArray(TAG_DATE_LIST);
            for (int i = 0; i < dates.length(); i++) {
                JSONObject date = dates.getJSONObject(i);
                long time = date.getLong(TAG_DATE) * 1000;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(time);
                if (cal.get(Calendar.MONTH) != mDate.get(Calendar.MONTH) || cal.get(Calendar.DAY_OF_MONTH) != mDate.get(Calendar.DAY_OF_MONTH) || cal.get(Calendar.YEAR) != mDate.get(Calendar.YEAR)) {
                    String url = mTracker.get(mTextView);
                    continue;
                } else {
                    JSONObject temp = date.getJSONObject(TAG_TEMPERATURE);
                    int currTemp = 0;
                    if (temp != null) {
                        Double min = temp.getDouble(TAG_MIN_TEMP);
                        Double max = temp.getDouble(TAG_MAX_TEMP);
                        int kelvinTemp = (int) ((min + max) / 2);
                        currTemp = kelvinTemp - 273;
                    }
                    JSONArray weather = date.getJSONArray(TAG_WEATHER);
                    String icon = weather.getJSONObject(0).getString(TAG_ICON_ID);
                    WeatherData data = new WeatherData();
                    data.temp = currTemp;
                    data.code = icon;
                    return data;
                }
            }

        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    static int getResourceId(String code) {

        if (code.equals("01d"))
            return R.drawable.ic_weather_sunny_grey600_24dp;
        if (code.equals("02d"))
            return R.drawable.ic_weather_partlycloudy_grey600_24dp;
        if (code.equals("03d"))
            return R.drawable.ic_weather_partlycloudy_grey600_24dp;
        if (code.equals("04d"))
            return R.drawable.ic_weather_cloudy_grey600_24dp;
        if (code.equals("09d"))
            return R.drawable.ic_weather_pouring_grey600_24dp;
        if (code.equals("10d"))
            return R.drawable.ic_weather_rainy_grey600_24dp;
        if (code.equals("11d"))
            return R.drawable.ic_weather_lightning_rainy_grey600_24dp;
        if (code.equals("13d"))
            return R.drawable.ic_weather_snowy_rainy_grey600_24dp;
        if (code.equals("50d"))
            return R.drawable.ic_weather_fog_grey600_24dp;
        return R.drawable.ic_weather_sunny_grey600_24dp;
    }
}
