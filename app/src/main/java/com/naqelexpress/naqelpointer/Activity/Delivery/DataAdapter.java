package com.naqelexpress.naqelpointer.Activity.Delivery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.ArrivedatDestNew.Waybill;
import com.naqelexpress.naqelpointer.Activity.DeliverysheetEBU.DeliverySheetActivity;
import com.naqelexpress.naqelpointer.Activity.SuggestDeliverysheet.DeliverySheet;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class DataAdapter
        extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<String> BarcodeList;

    public DataAdapter(ArrayList<String> PieceBarcode) {
        this.BarcodeList = PieceBarcode;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliverythirdfragmentitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.txtBarCode.setText(BarcodeList.get(i));
    }

    public void addItem(String country) {
        BarcodeList.add(country);
        notifyItemInserted(BarcodeList.size());
    }

    public void removeItem(int position) {
        BarcodeList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, BarcodeList.size());
    }

    @Override
    public int getItemCount() {
        return BarcodeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtBarCode;

        private ViewHolder(View view) {
            super(view);

            txtBarCode = (TextView) view.findViewById(R.id.txtWaybilll);
        }
    }

}