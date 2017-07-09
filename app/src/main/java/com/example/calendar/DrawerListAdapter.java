
package com.example.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * * Created by hp1 on 28-12-2014.
 */
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;

    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles
                                 // Value from MainActivity.javaprivate int
                                 // mIcons[]; // Int Array to store the
                                 // passed icons resource value from
                                 // MainActivity.java
    private int mIcons[];

    RecyclerView mRecyclerView;

    static Context mContext;
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int Holderid;

        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;

        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.rowText);
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
            Holderid = 1;
        }

        @Override
        public void onClick(View v)
        {

        }
    }

    DrawerListAdapter(String Titles[], int Icons[], RecyclerView recyclerView, Context context) {
        mNavTitles = Titles;
        mIcons = Icons;
        mRecyclerView = recyclerView;
        mContext = context;
    }

    @Override
    public DrawerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent,
                false); // Inflating the layout
        ViewHolder vhItem = new ViewHolder(v, viewType); // Creating
                                                         // ViewHolder and
                                                         // passing the
                                                         // object of type
        // v.setOnClickListener(mOnClickListener) ; // view
        return vhItem; // Returning the created object

    }

    @Override
    public void onBindViewHolder(DrawerListAdapter.ViewHolder holder, int position) {
        holder.textView.setText(mNavTitles[position]); // Setting the
                                                           // Text with the
                                                           // array of our
                                                           // Titles
        holder.imageView.setImageResource(mIcons[position]);
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}
