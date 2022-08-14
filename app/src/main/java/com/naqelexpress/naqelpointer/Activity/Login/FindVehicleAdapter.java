package com.naqelexpress.naqelpointer.Activity.Login;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBObjects.FindVehilceObject;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.List;

public class FindVehicleAdapter
        extends RecyclerView.Adapter<FindVehicleAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<FindVehilceObject> itemList;
    private List<FindVehilceObject> itemListFiltered;
    private RouteAdapterListener listener;

    public FindVehicleAdapter(Context context, List<FindVehilceObject> itemList, RouteAdapterListener listener) {
        this.context = context;
        this.itemList = itemList;
        itemListFiltered = itemList;
        this.listener = listener;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup convertView, int position) {

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.searchresults, convertView, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        FindVehilceObject item = itemListFiltered.get(position);
        holder.truckname.setText(item.Name);


    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView truckname;


        public MyViewHolder(View view) {
            super(view);

            truckname = (TextView) view.findViewById(R.id.results);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onItemSelected(itemListFiltered.get(getAdapterPosition()));
                }
            });


            view.setTag(this);
        }
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
                    List<FindVehilceObject> filteredList = new ArrayList<>();
                    for (FindVehilceObject row : itemList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.Name.toLowerCase().contains(charString.toLowerCase())) {
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
                itemListFiltered = (ArrayList<FindVehilceObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface RouteAdapterListener {
        void onItemSelected(FindVehilceObject contact);
    }
}