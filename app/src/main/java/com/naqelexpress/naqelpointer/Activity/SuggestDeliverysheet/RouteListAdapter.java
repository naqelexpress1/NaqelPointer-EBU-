package com.naqelexpress.naqelpointer.Activity.SuggestDeliverysheet;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.DBObjects.SuggestMyRouteShipments;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class RouteListAdapter
        extends BaseAdapter {
    private Context context;
    private List<SuggestMyRouteShipments> itemList;
    private String class_;

    public RouteListAdapter(Context context, List<SuggestMyRouteShipments> itemList, String calss_) {
        this.context = context;
        this.itemList = itemList;
        this.class_ = calss_;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public SuggestMyRouteShipments getItem(int position) {
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
            convertView = View.inflate(context, R.layout.routeitem, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        SuggestMyRouteShipments item = getItem(position);


        holder.lbSerial.setText(String.valueOf(getItemId(position) + 1));
        if (class_.equals("MyRouteActivity")) {
            holder.txtWaybill.setText(item.ItemNo);
            Integer typeID = itemList.get(position).TypeID;
            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
            String dateString = fmt.print(itemList.get(position).ExpectedTime);
//            holder.txtExpectedTime.setText(dateString);
            holder.txtExpectedTime.setText(item.ExistUser);
            //if (position %4 == 0)
            //holder.panel.setBackgroundColor(Color.RED);

            if (itemList.get(position).HasComplaint) {
                holder.imgHasComplaint.setVisibility(View.VISIBLE);
                holder.imgHasComplaint.setImageResource(R.drawable.redstar);
                holder.imgHasComplaint.refreshDrawableState();
            } else
                holder.imgHasComplaint.setVisibility(View.GONE);

            //holder.header.setText(item.PODDetail);

            if (itemList.get(position).HasDeliveryRequest) {
                holder.imgHasDeliveryRequest.setVisibility(View.VISIBLE);
                holder.imgHasDeliveryRequest.setImageResource(R.drawable.greenstar);
                holder.imgHasComplaint.refreshDrawableState();
            } else
                holder.imgHasDeliveryRequest.setVisibility(View.GONE);

            if (item.Latitude.length() > 3 && item.Longitude.length() > 3) {
                holder.imgHasLocation.setVisibility(View.VISIBLE);
                holder.imgHasLocation.setImageResource(R.drawable.marker);
            } else
                holder.imgHasLocation.setVisibility(View.INVISIBLE);

            if (typeID == 1) {
//                holder.txtType.setText("Delivery");
            } else if (typeID == 2) {
//                holder.txtType.setText("PickUp");
            }
            if (item.IsDelivered) {
                holder.txtType.setText("Delivered");
                holder.txtType.setTextColor(Color.parseColor("#118211"));
            } else {
                if (item.NotDelivered) {
                    holder.txtType.setText("Not Delivered");
                    holder.txtType.setTextColor(Color.parseColor("#FBD904"));
                } else {
                    holder.txtType.setText("Not Delivered");
                    holder.txtType.setTextColor(Color.parseColor("#B01300"));
                }
            }

        } else {
            holder.txtWaybill.setText("Waybill No\n" + item.ItemNo);
            holder.imgHasComplaint.setVisibility(View.GONE);
            holder.imgHasLocation.setVisibility(View.GONE);
            holder.txtExpectedTime.setVisibility(View.GONE);
            holder.lbDeliveryDate.setVisibility(View.GONE);
            holder.txtAmount.setVisibility(View.GONE);

        }
        return convertView;
    }

    class ViewHolder {
        TextView txtWaybill, txtType, lbSerial, txtExpectedTime, lbDeliveryDate, txtAmount, header;
        ImageView imgHasLocation, imgHasComplaint, imgHasDeliveryRequest;
        //TextView panel;

        public ViewHolder(View view) {
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
            lbDeliveryDate = (TextView) view.findViewById(R.id.lbDeliveryDate);
            txtWaybill = (TextView) view.findViewById(R.id.txtWaybilll);
            lbSerial = (TextView) view.findViewById(R.id.lbSerial);
            txtType = (TextView) view.findViewById(R.id.txtAmount);
            txtExpectedTime = (TextView) view.findViewById(R.id.txtExpectedTime);
            //panel = (TextView) view.findViewById(R.id.panel);
            imgHasLocation = (ImageView) view.findViewById(R.id.imgHasLocation);

            imgHasComplaint = (ImageView) view.findViewById(R.id.imgHasComplaint);
            imgHasDeliveryRequest = (ImageView) view.findViewById(R.id.imgHasRequest);
            header = (TextView) view.findViewById(R.id.changesheader);
            view.setTag(this);
        }
    }


//    private int dp2px(int dp)
//    {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                getResources().getDisplayMetrics());
//    }
}