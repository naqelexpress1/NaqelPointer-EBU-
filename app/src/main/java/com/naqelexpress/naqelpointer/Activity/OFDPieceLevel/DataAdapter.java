package com.naqelexpress.naqelpointer.Activity.OFDPieceLevel;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class DataAdapter
        extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<String> BarcodeList;

    public DataAdapter(ArrayList<String> PieceBarcode) {
        this.BarcodeList = PieceBarcode;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliverythirdfragmentitemwaybil, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        String split[] = BarcodeList.get(i).split("-");
        viewHolder.txtBarCode.setText(split[0]);
        viewHolder.txtWaybill.setText(split[1]);
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
        private TextView txtBarCode, txtWaybill;
        private TextView t;

        private ViewHolder(View view) {
            super(view);

            txtBarCode = (TextView) view.findViewById(R.id.txtbarcode);
            txtWaybill = (TextView) view.findViewById(R.id.txtWaybilll);
        }
    }
}