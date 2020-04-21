package com.naqelexpress.naqelpointer.Activity.CourierKPIEBU;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteListAdapter
        extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> itemList;
    private String class_;

    public RouteListAdapter(Context context, ArrayList<HashMap<String, String>> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.courierkpi, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        HashMap<String, String> item = getItem(position);


        holder.lbSerial.setText(String.valueOf(getItemId(position) + 1 + " . "));

        holder.txtempname.setText(item.get("Name") + " - " + item.get("EmployID"));
        holder.txtDeliverCount.setText(item.get("DeliveryCount") + " / " + item.get("DeliverySheetCount"));
        holder.txtPickupCount.setText(item.get("PickupCount"));

        return convertView;
    }

    class ViewHolder {
        TextView txtempname, txtEmpID, lbSerial, txtDeliverCount, txtPickupCount;

        public ViewHolder(View view) {
            txtempname = (TextView) view.findViewById(R.id.empname);
            lbSerial = (TextView) view.findViewById(R.id.lbSerial);
            txtDeliverCount = (TextView) view.findViewById(R.id.delivercount);
            txtPickupCount = (TextView) view.findViewById(R.id.pickupcount);

            view.setTag(this);
        }
    }


//    private int dp2px(int dp)
//    {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                getResources().getDisplayMetrics());
//    }
}