package com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class DataAdapterForThird
        extends RecyclerView.Adapter<DataAdapterForThird.ViewHolder>
{
    private ArrayList<String> BarcodeList;

    public DataAdapterForThird(ArrayList<String> PieceBarcode)
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

        viewHolder.txtBarCode.setText(BarcodeList.get(i));
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