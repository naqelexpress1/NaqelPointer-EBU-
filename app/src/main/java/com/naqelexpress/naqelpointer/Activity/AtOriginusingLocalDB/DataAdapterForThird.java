package com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.HashMap;
import java.util.List;

public class DataAdapterForThird
        extends RecyclerView.Adapter<DataAdapterForThird.ViewHolder>
{
    private List<HashMap<String, String>> BarcodeList;

    public DataAdapterForThird(List<HashMap<String, String>> PieceBarcode)
    {
        this.BarcodeList = PieceBarcode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliverythirdfragmentitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.txtBarCode.setText(BarcodeList.get(i).get("BarCode"));
    }


    public void removeItem(int position)
    {
        BarcodeList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, BarcodeList.size());
    }

    @Override
    public int getItemCount()
    {
        return BarcodeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtBarCode;
        private ViewHolder(View view)
        {
            super(view);

            txtBarCode = (TextView)view.findViewById(R.id.txtWaybilll);
        }
    }
}