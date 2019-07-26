package com.naqelexpress.naqelpointer.Activity.PickupPieceLevel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;


public class PickupAdapter
        extends BaseAdapter {
    private ArrayList<HashMap<String, String>> itemlist;
    private Context context;

    public PickupAdapter(ArrayList<HashMap<String, String>> itemlist, Context context) {
        this.itemlist = itemlist;
        this.context = context;
    }


    @Override
    public int getCount() {
        return itemlist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view;
        if (convertView == null) {

            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_view, viewGroup, false);
            TextView loadtype = (TextView) view.findViewById(R.id.text1);

            loadtype.setText(itemlist.get(i).get("Name"));

        } else {
            view = (View) convertView;
        }

        return view;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textview;

        private ViewHolder(View view) {
            super(view);

            textview = (TextView) view.findViewById(R.id.text1);


        }
    }
}