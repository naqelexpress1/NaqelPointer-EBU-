package com.naqelexpress.naqelpointer.Activity.routeMap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hasna on 7/24/18.
 */

public class RouteMapAdapter extends RecyclerView.Adapter<RouteMapAdapter.ViewHolder> {

    private ArrayList<HashMap<String,String>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RouteMapAdapter(Context context, ArrayList<HashMap<String,String>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.mapview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String km = mData.get(position).get("km");
        holder.serialNo.setText(String.valueOf(position +1 ));
        holder.time.setText(mData.get(position).get("time"));
        holder.address.setText(mData.get(position).get("address"));
        holder.kilometer.setText(km);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView address,kilometer,serialNo,time;

        ViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            kilometer = itemView.findViewById(R.id.kilometer);
            serialNo = itemView.findViewById(R.id.sno);
            time = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).get("km");
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}