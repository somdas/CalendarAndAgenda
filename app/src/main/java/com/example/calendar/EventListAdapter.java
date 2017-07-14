package com.example.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by sbandyop on 7/5/2017.
 */
public class EventListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<AdapterContainer> mContainerList;
    private final static int TYPE_HEADER = 0;
    private final static int TYPE_NO_EVENT = 1;
    private final static int TYPE_EVENT = 2;
    private final static int TOTAL_TYPES = 3;

    public EventListAdapter(Context context, List<AdapterContainer> containerList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mContainerList = containerList;
    }

    @Override
    public int getCount() {
        return mContainerList.size();
    }

    @Override
    public Object getItem(int i) {
        return mContainerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getItemViewType(int position) {
            AdapterContainer container = mContainerList.get(position);
            if (container.isHeader)
                return TYPE_HEADER;
            if (container.isNoEvent)
                return TYPE_NO_EVENT;
            return TYPE_EVENT;
    }

    @Override
    public int getViewTypeCount() {
        return TOTAL_TYPES;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder mHolder = new ViewHolder();
        if (convertView == null) {

            // Inflate layout based on the type of information
            if (getItemViewType(position) == TYPE_HEADER) {
                convertView = mInflater.inflate(R.layout.header_layput, null);
                TextView title = (TextView) convertView.findViewById(R.id.date_title);
                mHolder.title = title;
            } else if (getItemViewType(position) == TYPE_NO_EVENT) {
                convertView = mInflater.inflate(R.layout.no_event_layout, null);
                TextView no_event = (TextView) convertView.findViewById(R.id.no_event);
                mHolder.no_event = no_event;
            } else {
                convertView = mInflater.inflate(R.layout.event_layout, null);
                TextView curr_title = (TextView) convertView.findViewById(R.id.curr_title);
                TextView curr_location = (TextView) convertView.findViewById(R.id.curr_location);
                TextView curr_time = (TextView) convertView.findViewById(R.id.curr_time);
                TextView curr_duration = (TextView) convertView.findViewById(R.id.curr_duration);
                RelativeLayout loc_layout = (RelativeLayout) convertView.findViewById(R.id.loc_container);
                mHolder.curr_title = curr_title;
                mHolder.curr_location = curr_location;
                mHolder.curr_time = curr_time;
                mHolder.curr_duration = curr_duration;
                mHolder.loc_layout = loc_layout;

            }
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }


        if (getItemViewType(position) == TYPE_HEADER) {
            mHolder.title.setText(mContainerList.get(position).header);
        } else if (getItemViewType(position) == TYPE_NO_EVENT) {
        } else {
            Event event = mContainerList.get(position).event;
            mHolder.curr_title.setText(event.title);
            if (event.isAllDay) {
                mHolder.curr_time.setText(mContext.getResources().getString(R.string.all_day));
                mHolder.curr_duration.setText(event.daysLeft + " " + mContext.getResources().getString(R.string.day));
            } else {
                String duration = getDuration(event);
                String time = DateTimeUtils.formattedTime(event.startHour, event.startMinute).toString();
                mHolder.curr_time.setText(time);
                mHolder.curr_duration.setText(duration);
            }

            if (event.location == null || event.location.equals("")) {
                mHolder.loc_layout.setVisibility(View.INVISIBLE);
            } else {
                mHolder.curr_location.setText(event.location);
            }
        }
        return convertView;
    }

    private String getDuration(Event event) {
        Calendar start = new GregorianCalendar(event.startYear, event.startMonth, event.startDay, event.startHour, event.startMinute);
        Calendar end = new GregorianCalendar(event.endYear, event.endMonth, event.endDay, event.endHour, event.endMinute);
        return DateTimeUtils.getDurationInFormattedString(start, end);
    }

    static class ViewHolder {
        ViewFlipper flipper;
        TextView title;
        TextView no_event;
        TextView curr_title;
        TextView curr_location;
        TextView curr_time;
        TextView curr_duration;
        RelativeLayout loc_layout;
    }
}