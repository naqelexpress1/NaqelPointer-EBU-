package com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.List;

public class BookingListAdapter extends BaseAdapter implements Filterable {
    private Activity context;
    private ArrayList<BookingModel> itemList;
    private String class_;
    private List<BookingModel> itemListFiltered;

    public BookingListAdapter(Activity context, ArrayList<BookingModel> itemList, String class_) {
        this.context = context;
        this.itemList = itemList;
        this.class_ = class_;
        itemListFiltered = itemList;
    }

    @Override
    public int getCount() {
        return itemListFiltered.size();
    }

    @Override
    public BookingModel getItem(int position) {
        return itemListFiltered.get(position);
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
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFiltered = itemList;
                } else {
                    List<BookingModel> filteredList = new ArrayList<>();
                    for (BookingModel row : itemList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (String.valueOf(row.WaybillNo).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    itemListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemListFiltered = (ArrayList<BookingModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @SuppressLint({"SetTextI18n", "WrongConstant"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //convertView = View.inflate(context, R.layout.bookingitempickupsheet, null);
            convertView = View.inflate(context, R.layout.spasrregular_bookinglist, null);
            new ViewHolder(convertView);
        } else {
            // holdeconvertView = (ViewHolder) convertView.getTag();
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final BookingModel item = getItem(position);

        holder.byPickup.setText(String.valueOf(item.getBKHeader()));

        if (item.getisSPL()) {
            holder.puidawbno.setText("Pu ID / AWB : " + item.getPickupSheetID());
            holder.location.setText(item.getSPOfficeName());
            holder.mno1.setText(item.getSPMobile());
            holder.numberofwaybills.setText(String.valueOf(item.getWaybillcount()));
            holder.mno2ll.setVisibility(View.GONE);
            holder.clientll.setVisibility(View.GONE);
            holder.contactll.setVisibility(View.GONE);

        } else {

            holder.locationll.setVisibility(View.GONE);
            holder.numberofwaybillsll.setVisibility(View.GONE);
            holder.consname.setText(item.getConsigneeName());
            holder.clientname.setText(item.getClientName());
            holder.puidawbno.setText("Pu ID / AWB : " + String.valueOf(item.getWaybillNo()));
            holder.mno1.setText(item.getPhoneNo());
            holder.mno2.setText(item.getMobileNo());

        }


//        holder.sno.setText(String.valueOf(item.getsNo()));
//        holder.waybillno.setText(String.valueOf(item.getWaybillNo()));
//
//
//        DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        try {
//            Date dt = formatter.parse(item.getDate());
//            DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            String dte = dfmt.format(dt);
//
//
//            holder.date.setText(dte);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        holder.consname.setText(item.getConsigneeName());
//        holder.orgstation.setText(item.OrgCode);
//        holder.deststation.setText(item.getDestCode());
//        holder.billtype.setText(item.getCode());
//
//        if (item.isPickedup == 0) {
//            holder.ispickedup.setText("Not Attempted");
//            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.NaqelRed));
//        } else if (item.isPickedup == 1) {
//            holder.ispickedup.setText("Attempted");
//            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.main_orange_color));
//        } else if (item.isPickedup == 2) {
//            holder.ispickedup.setText("Picked Up");
//            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.main_green_color));
//        }
//
//        if (item.getLat() != null && item.getLat().length() > 3 && !item.getLat().equals("0"))
//            holder.islocation.setVisibility(View.VISIBLE);
//        else
//            holder.islocation.setVisibility(View.GONE);

        final int acceptposition = position;


        return convertView;
    }

    class ViewHolder {
        TextView byPickup, puidawbno, location, mno1, mno2, consname, clientname, numberofwaybills;
        LinearLayout clientll, locationll, contactll, mno2ll, numberofwaybillsll;
//                , waybillno, date, consname, orgstation,
//                deststation, billtype, sno, ispickedup;
//        ImageView islocation;
        //TextView panel;

        public ViewHolder(View view) {
            byPickup = (TextView) view.findViewById(R.id.byPickup);
            puidawbno = (TextView) view.findViewById(R.id.puidawbno);
            location = (TextView) view.findViewById(R.id.location);
            clientll = (LinearLayout) view.findViewById(R.id.clientll);
            locationll = (LinearLayout) view.findViewById(R.id.locationll);
            mno2ll = (LinearLayout) view.findViewById(R.id.mno2ll);
            contactll = (LinearLayout) view.findViewById(R.id.contactll);
            mno1 = (TextView) view.findViewById(R.id.mno1);
            mno2 = (TextView) view.findViewById(R.id.mno2);
            clientname = (TextView) view.findViewById(R.id.clientname);
            consname = (TextView) view.findViewById(R.id.contactname);
            numberofwaybills = (TextView) view.findViewById(R.id.numberofwaybills);
            numberofwaybillsll = (LinearLayout) view.findViewById(R.id.numberofwaybillsll);
//            islocation = (ImageView) view.findViewById(R.id.islocation);
//            sno = (TextView) view.findViewById(R.id.sno);
//            ispickedup = (TextView) view.findViewById(R.id.ispickedup);
//            waybillno = (TextView) view.findViewById(R.id.waybillno);
//            date = (TextView) view.findViewById(R.id.date);
//            consname = (TextView) view.findViewById(R.id.consname);
//            orgstation = (TextView) view.findViewById(R.id.orgstation);
//            deststation = (TextView) view.findViewById(R.id.deststation);
//            // contactnumber  = (TextView) view.findViewById(R.id.lblContactNo);
//            billtype = (TextView) view.findViewById(R.id.billtype);

            view.setTag(this);
        }
    }


    //update the status


}