package com.naqelexpress.naqelpointer.Activity.DeliveryOFD;

import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class DataAdapter
        extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<String> BarcodeList;

    public DataAdapter(ArrayList<String> PieceBarcode) {
        this.BarcodeList = PieceBarcode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliverythirdfragmentitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.txtBarCode.setText(BarcodeList.get(i));
        viewHolder.txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanBarcodeLength)});
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