package com.naqelexpress.naqelpointer.Activity.LoadtoDest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.HashMap;
import java.util.List;

public class DataAdapterForThird
        extends BaseAdapter {
    private List<HashMap<String, String>> waybills;
    private Context context;

    public DataAdapterForThird(List<HashMap<String, String>> waybills, Context context) {
        this.waybills = waybills;
        this.context = context;
    }


    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position % 2;
    }

    @Override
    public int getCount() {
        return waybills.size();
    }

    @Override
    public HashMap<String, String> getItem(int i) {
        return waybills.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.barcodeheader, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        HashMap<String, String> item = getItem(i);

        holder.barcode.setText(item.get("BarCode"));


        return convertView;
    }

    class ViewHolder {
        TextView barcode;

        public ViewHolder(View view) {
            barcode = (TextView) view.findViewById(R.id.barcode);

            view.setTag(this);
        }
    }
}