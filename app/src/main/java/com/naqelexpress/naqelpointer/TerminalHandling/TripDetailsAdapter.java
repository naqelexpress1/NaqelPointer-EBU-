package com.naqelexpress.naqelpointer.TerminalHandling;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TripDetailsAdapter
        extends RecyclerView.Adapter<TripDetailsAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> BarcodeList;
    private ItemClickListener clickListener;

    public TripDetailsAdapter(ArrayList<HashMap<String, String>> PieceBarcode) {
        this.BarcodeList = PieceBarcode;
    }

    @Override
    public TripDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tripdetailsitemcbu, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripDetailsAdapter.ViewHolder viewHolder, int i) {


        viewHolder.originid.setText(BarcodeList.get(i).get("Origin"));
        viewHolder.destination.setText(BarcodeList.get(i).get("Destination"));
        viewHolder.dtime.setText(BarcodeList.get(i).get("ETA"));
        viewHolder.vendorname.setText(BarcodeList.get(i).get("Vendor"));
        if (BarcodeList.get(i).get("function").equals("0")) {
            viewHolder.departarrival.setText("Deprt Time : ");
            viewHolder.tripcode.setText(BarcodeList.get(i).get("TripCode"));
        } else {
            viewHolder.departarrival.setText("Arrival Time : ");
            String tripid = "";
            if(!BarcodeList.get(i).get("TripID").equals("0"))
                tripid =  " - " + BarcodeList.get(i).get("TripID");
            viewHolder.tripcode.setText(BarcodeList.get(i).get("TripCode") + tripid);
        }


    }


    public void removeItem(int position) {
        BarcodeList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, BarcodeList.size());
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return BarcodeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tripcode, originid, destination, dtime, vendorname, departarrival;

        private ViewHolder(View view) {
            super(view);

            tripcode = (TextView) view.findViewById(R.id.tripcode);
            originid = (TextView) view.findViewById(R.id.originid);
            destination = (TextView) view.findViewById(R.id.destination);
            dtime = (TextView) view.findViewById(R.id.eta);
            vendorname = (TextView) view.findViewById(R.id.vendorname);
            departarrival = (TextView) view.findViewById(R.id.dtime);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}