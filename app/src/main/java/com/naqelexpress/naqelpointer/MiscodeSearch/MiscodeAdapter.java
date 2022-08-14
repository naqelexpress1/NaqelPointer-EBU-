package com.naqelexpress.naqelpointer.MiscodeSearch;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravi on 16/11/17.
 */

public class MiscodeAdapter extends RecyclerView.Adapter<MiscodeAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<MiscodeModel> contactList;
    private List<MiscodeModel> contactListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView countrycode, cityname, citycode;


        public MyViewHolder(View view) {
            super(view);
            countrycode = view.findViewById(R.id.countrycode);
            cityname = view.findViewById(R.id.cityname);
            citycode = view.findViewById(R.id.citycode);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public MiscodeAdapter(Context context, List<MiscodeModel> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.miscodesearch, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MiscodeModel contact = contactListFiltered.get(position);
        holder.countrycode.setText(contact.getCountryCode());
        holder.cityname.setText(contact.getCityName());
        holder.citycode.setText(contact.getCityCode());

    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<MiscodeModel> filteredList = new ArrayList<>();
                    for (MiscodeModel row : contactList) {

                        // countrycode match condition. this might differ depending on your requirement
                        // here we are looking for countrycode or cityname number match
                        if (row.getCountryCode().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getCityName().toLowerCase().contains(charSequence) ||
                                row.getCityCode().toLowerCase().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<MiscodeModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(MiscodeModel contact);
    }
}
