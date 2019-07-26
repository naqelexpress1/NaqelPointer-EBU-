package com.naqelexpress.naqelpointer.Activity.LoadtoDest;

import android.support.v7.widget.RecyclerView;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliverythirdfragmentitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripDetailsAdapter.ViewHolder viewHolder, int i) {

        viewHolder.txtBarCode.setText(BarcodeList.get(i).get("TripPlan"));
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
        private TextView txtBarCode;

        private ViewHolder(View view) {
            super(view);

            txtBarCode = (TextView) view.findViewById(R.id.txtWaybilll);
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