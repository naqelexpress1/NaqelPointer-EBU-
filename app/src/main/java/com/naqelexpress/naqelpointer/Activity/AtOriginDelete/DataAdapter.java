package com.naqelexpress.naqelpointer.Activity.AtOriginDelete;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAdapter
        extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> BarcodeList;
    private String GetData;

    public DataAdapter(ArrayList<HashMap<String, String>> Selectedbarcodedetailse, String GetData) {
        this.BarcodeList = Selectedbarcodedetailse;
        this.GetData = GetData;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliverythirdfragmentitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {


        viewHolder.txtBarCode.setText(BarcodeList.get(i).get(GetData));
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