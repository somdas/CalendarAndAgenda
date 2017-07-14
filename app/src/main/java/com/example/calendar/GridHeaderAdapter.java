package com.example.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sbandyop on 7/5/2017.
 */
public class GridHeaderAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mContainerList;
    private final static int TYPE_HEADER = 0;
    private final static int TYPE_NO_EVENT = 1;
    private final static int TYPE_EVENT = 2;
    private final static int TOTAL_TYPES = 3;

    public GridHeaderAdapter(Context context, List<String> containerList) {
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder mHolder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.date_cell, null);
            TextView date = (TextView) convertView.findViewById(R.id.cell);
            mHolder.date = date;
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.date.setText(mContainerList.get(position));
        return convertView;

    }

    static class ViewHolder {
        TextView date;
    }
}
