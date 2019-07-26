package com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CourierAdapter
        extends BaseAdapter {
    private ArrayList<HashMap<String, String>> waybills;
    private Context context;
//    ResultInterface myinterface;

    public CourierAdapter(ArrayList<HashMap<String, String>> waybills, Context context) {
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
    public HashMap getItem(int i) {
        return waybills.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        if (convertView == null) {

            //           convertView = LayoutInflater.from(context).inflate(R.layout.waybillheader, parent, false);
            //    viewHolder = new ViewHolder(convertView);
//            convertView.setTag(viewHolder);

            convertView = View.inflate(context, R.layout.waybillheader, null);
            new ViewHolder(convertView);

        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        HashMap item = getItem(i);
        //String waybill = String.valueOf(item.get("WaybillNo"));
        holder.waybillno.setText(item.get("WaybillNo").toString());
        if (item.get("bgcolor").equals("1"))
            holder.waybillno.setBackgroundColor(Color.parseColor("#00b339"));

        // new BGColor(holder.waybillno, i).execute(holder);

        return convertView;
    }

    class ViewHolder {
        TextView waybillno;

        public ViewHolder(View view) {
            waybillno = (TextView) view.findViewById(R.id.waybill);
            view.setTag(this);
        }
    }


}