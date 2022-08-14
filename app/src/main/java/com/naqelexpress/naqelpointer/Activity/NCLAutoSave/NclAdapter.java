package com.naqelexpress.naqelpointer.Activity.NCLAutoSave;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class NclAdapter  extends RecyclerView.Adapter<NclAdapter.ViewHolder>
{
    private ArrayList<ScanNclWaybillFragment.PieceDetail> BarcodeList;

    public NclAdapter(ArrayList<ScanNclWaybillFragment.PieceDetail> PieceBarcode)
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

        viewHolder.txtBarCode.setText(BarcodeList.get(i).Barcode);
    }

    public void addItem(ScanNclWaybillFragment.PieceDetail country)
    {
        BarcodeList.add(country);
        notifyItemInserted(BarcodeList.size());
    }
    public String GetWaybillNo(int position)
    {
        return BarcodeList.get(position).Waybill;
    }
    public void removeItem(int position)
    {
        BarcodeList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, BarcodeList.size());
    }

    public void clearAll() {
        final int size = BarcodeList.size();
        BarcodeList.clear();
        notifyItemRangeRemoved(0, size);
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