package com.naqelexpress.naqelpointer.Activity.CheckCOD;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;


public class CheckCODAdapter
        extends RecyclerView.Adapter<CheckCODAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> itemlist;

    public CheckCODAdapter(ArrayList<HashMap<String, String>> itemlist) {
        this.itemlist = itemlist;
    }

    @Override
    public CheckCODAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pendingmoneyitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckCODAdapter.ViewHolder holder, int i) {


        holder.txtWaybill.setText(itemlist.get(i).get("WayBillNo"));
        holder.lbSerial.setText(String.valueOf(i + 1));

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MMM/yyyy");
        //String dateString = fmt.print(item.DeliveryDate);
        //holder.txtDeliveryDate.setText(dateString);
        holder.txtAmount.setText(itemlist.get(i).get("CODAMOUNT"));
        holder.lbDeliveryDate.setVisibility(View.GONE);
        holder.txtDeliveryDate.setText(itemlist.get(i).get("POS"));
    }

//    public void addItem(String country) {
//        BarcodeList.add(country);
//        notifyItemInserted(BarcodeList.size());
//    }


    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtWaybill, txtAmount, lbSerial, txtDeliveryDate, lbDeliveryDate;

        private ViewHolder(View view) {
            super(view);

            txtWaybill = (TextView) view.findViewById(R.id.txtWaybilll);
            lbSerial = (TextView) view.findViewById(R.id.lbSerial);
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
            txtDeliveryDate = (TextView) view.findViewById(R.id.txtDeliveryDate);
            lbDeliveryDate = (TextView) view.findViewById(R.id.lbDeliveryDate);


        }
    }
}