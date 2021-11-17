package com.naqelexpress.naqelpointer.Activity.SPbookingException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Activity.BookingCBU.PickupSheetReasonModel;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class WaybillExceptionAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<String> itemList;
    private String class_;
    //    private ArrayList<String> itemListFiltered;
    private ArrayList<PickupSheetReasonModel> pickupSheetReasonModels;

    public WaybillExceptionAdapter(Activity context, ArrayList<String> itemList, ArrayList<PickupSheetReasonModel> pickupSheetReasonModels, String class_) {
        this.context = context;
        this.itemList = itemList;
        this.class_ = class_;
//        itemListFiltered = itemList;
        this.pickupSheetReasonModels = pickupSheetReasonModels;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public String getItem(int position) {
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


   /* @Override
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
*/

    @SuppressLint({"SetTextI18n", "WrongConstant"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //convertView = View.inflate(context, R.layout.bookingitempickupsheet, null);
            convertView = View.inflate(context, R.layout.sp_exceptionlist, null);
            new ViewHolder(convertView);
        } else {
            // holdeconvertView = (ViewHolder) convertView.getTag();
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final String item = getItem(position);

        holder.txtWaybillNo.setText(item);

        final SpinAdapter adapter = new SpinAdapter(context,
                android.R.layout.simple_spinner_item,
                pickupSheetReasonModels);
        holder.spinner.setBackgroundResource(android.R.drawable.spinner_dropdown_background);
        holder.spinner.setAdapter(adapter); // Set the custom adapter to the spinner
        holder.spinner.setSelection(0, false);
        // You can create an anonymous listener to handle the event when is selected an spinner item
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                PickupSheetReasonModel pickupSheetReasonModel = adapter.getItem(position);
//                HashMap<String, Integer> exception = new HashMap<>();

//                exception.put("id", pickupSheetReasonModel.getID());
                // HashSet<String> stringHashSet = new HashSet<>();
                // stringHashSet.add(holder.txtWaybillNo.getText().toString() + "_" + String.valueOf(pickupSheetReasonModel.getID()));
                String value = SpWaybillException.exceptionHashmap.get(holder.txtWaybillNo.getText().toString());
                if (value != null)
                    if (SpWaybillException.exceptionIDs.contains(holder.txtWaybillNo.getText().toString() + "_" + value)) {

                        SpWaybillException.exceptionIDs.remove(holder.txtWaybillNo.getText().toString() + "_" + value);
                    }

                SpWaybillException.exceptionIDs.add(holder.txtWaybillNo.getText().toString() + "_" + String.valueOf(pickupSheetReasonModel.getID()));

                SpWaybillException.exceptionHashmap.put(holder.txtWaybillNo.getText().toString(),
                        String.valueOf(pickupSheetReasonModel.getID()));
                // Here you can do the action you want to...
                Toast.makeText(context, "ID: " + pickupSheetReasonModel.getID() + "\nName: " + pickupSheetReasonModel.getName(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

//        holder.txtdescription.setText(String.valueOf(item.getGoodDesc()));


        return convertView;
    }

    class ViewHolder {
        TextView txtWaybillNo;
        Spinner spinner;


        public ViewHolder(View view) {
            txtWaybillNo = (TextView) view.findViewById(R.id.piececode);
            spinner = (Spinner) view.findViewById(R.id.exception);


            view.setTag(this);
        }
    }


    //update the status


}