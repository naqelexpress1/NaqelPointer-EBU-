package com.naqelexpress.naqelpointer.Activity.BookingCBU;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookingListAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<Booking> itemList;
    private String class_;

    public BookingListAdapter(Activity context, ArrayList<Booking> itemList, String class_) {
        this.context = context;
        this.itemList = itemList;
        this.class_ = class_;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Booking getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected static class ViewHolderItems {
        private TextView ItemNo;
        private TextView TypeID;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.bookingitempickupsheet, null);
            new ViewHolder(convertView);
        } else {
            // holdeconvertView = (ViewHolder) convertView.getTag();
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final Booking item = getItem(position);

        holder.waybillno.setText(item.RefNo);


        DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date dt = formatter.parse(itemList.get(position).PickUpReqDT);
            DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String dte = dfmt.format(dt);


            holder.date.setText(dte);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.consname.setText(itemList.get(position).ContactPerson);
        holder.orgstation.setText(itemList.get(position).Orgin);
        holder.deststation.setText(itemList.get(position).Destination);
        holder.billtype.setText(itemList.get(position).BillType);

        final int acceptposition = position;


        return convertView;
    }

    class ViewHolder {
        TextView waybillno, date, consname, orgstation,
                deststation, billtype ;
        //TextView panel;

        public ViewHolder(View view) {
            waybillno = (TextView) view.findViewById(R.id.waybillno);
            date = (TextView) view.findViewById(R.id.date);
            consname = (TextView) view.findViewById(R.id.consname);
            orgstation = (TextView) view.findViewById(R.id.orgstation);
            deststation = (TextView) view.findViewById(R.id.deststation);
           // contactnumber  = (TextView) view.findViewById(R.id.lblContactNo);
            billtype = (TextView) view.findViewById(R.id.billtype);
            view.setTag(this);
        }
    }

    //update the status


}