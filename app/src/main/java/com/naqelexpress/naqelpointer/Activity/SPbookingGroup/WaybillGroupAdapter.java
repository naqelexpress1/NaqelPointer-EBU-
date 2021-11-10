package com.naqelexpress.naqelpointer.Activity.SPbookingGroup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingModel;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.List;

public class WaybillGroupAdapter extends BaseAdapter implements Filterable {
    private Activity context;
    private ArrayList<BookingModel> itemList;
    private String class_;
    private List<BookingModel> itemListFiltered;

    public WaybillGroupAdapter(Activity context, ArrayList<BookingModel> itemList, String class_) {
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
            convertView = View.inflate(context, R.layout.spasrregular_groupwaybillnos, null);
            new ViewHolder(convertView);
        } else {
            // holdeconvertView = (ViewHolder) convertView.getTag();
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final BookingModel item = getItem(position);

        holder.txtWaybillNo.setText(String.valueOf(item.getWaybillNo()));
        holder.txtdescription.setText(String.valueOf(item.getClientName()));

        final int acceptposition = position;


        return convertView;
    }

    class ViewHolder {
        TextView txtWaybillNo, txtdescription;


        public ViewHolder(View view) {
            txtWaybillNo = (TextView) view.findViewById(R.id.txtWaybillNo);
            txtdescription = (TextView) view.findViewById(R.id.txtdescription);


            view.setTag(this);
        }
    }


    //update the status


}