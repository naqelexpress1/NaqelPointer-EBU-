package com.naqelexpress.naqelpointer.TerminalHandling;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class UndoNclAdapter
        extends RecyclerView.Adapter<UndoNclAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> BarcodeList;
    private String GetData;

    public UndoNclAdapter(ArrayList<HashMap<String, String>> Selectedbarcodedetailse) {
        this.BarcodeList = Selectedbarcodedetailse;
    }

    @Override
    public UndoNclAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.undoncladapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UndoNclAdapter.ViewHolder viewHolder, int i) {

        if (i == 0) {
            viewHolder.txtBarCode.setBackgroundResource(R.color.NaqelBlue);
            viewHolder.txtBarCode.setTextColor(Color.parseColor("#FFFFFF"));
        } else
            viewHolder.tl.setBackgroundResource(R.color.NaqelGray);
        viewHolder.txtBarCode.setText(BarcodeList.get(i).get("NclNo"));
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
        private TableLayout tl;

        private ViewHolder(View view) {
            super(view);
            tl = (TableLayout) view.findViewById(R.id.tl);
            txtBarCode = (TextView) view.findViewById(R.id.txtWaybilll);
        }
    }
}