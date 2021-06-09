package com.naqelexpress.naqelpointer.Activity.BookingCBU;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.bookingitempickupsheet, null);
            new ViewHolder(convertView);
        } else {
            // holdeconvertView = (ViewHolder) convertView.getTag();
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final BookingModel item = getItem(position);

        holder.sno.setText(String.valueOf(item.getsNo()));
        holder.waybillno.setText(String.valueOf(item.getWaybillNo()));


        DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date dt = formatter.parse(item.getDate());
            DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String dte = dfmt.format(dt);


            holder.date.setText(dte);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.consname.setText(item.getConsigneeName());
        holder.orgstation.setText(item.OrgCode);
        holder.deststation.setText(item.getDestCode());
        holder.billtype.setText(item.getCode());

        if (item.isPickedup == 0) {
            holder.ispickedup.setText("Not Attempted");
            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.NaqelRed));
        } else if (item.isPickedup == 1) {
            holder.ispickedup.setText("Attempted");
            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.main_orange_color));
        } else if (item.isPickedup == 2) {
            holder.ispickedup.setText("Picked Up");
            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.main_green_color));
        }

        if (item.getLat() != null && item.getLat().length() > 3 && !item.getLat().equals("0"))
            holder.islocation.setVisibility(View.VISIBLE);
        else
            holder.islocation.setVisibility(View.GONE);

        final int acceptposition = position;


        return convertView;
    }

    class ViewHolder {
        TextView waybillno, date, consname, orgstation,
                deststation, billtype, sno, ispickedup;
        ImageView islocation;
        //TextView panel;

        public ViewHolder(View view) {
            islocation = (ImageView) view.findViewById(R.id.islocation);
            sno = (TextView) view.findViewById(R.id.sno);
            ispickedup = (TextView) view.findViewById(R.id.ispickedup);
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