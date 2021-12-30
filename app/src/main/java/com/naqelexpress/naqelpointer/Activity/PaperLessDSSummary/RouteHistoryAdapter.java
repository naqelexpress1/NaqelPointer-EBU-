package com.naqelexpress.naqelpointer.Activity.PaperLessDSSummary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RouteHistoryAdapter
        extends RecyclerView.Adapter<RouteHistoryAdapter.MyViewHolder> {

    private Context context;
    private List<HashMap<String, String>> itemList;
    private String class_;
    private RouteHistory listener;

    public RouteHistoryAdapter(Context context, List<HashMap<String, String>> itemList, String calss_,
                               RouteHistory listener) {
        this.context = context;
        this.itemList = itemList;

        this.class_ = calss_;
        this.listener = listener;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup convertView, int position) {

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.routehistoryitem, convertView, false);

        return new MyViewHolder(itemView);


//        return convertView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        HashMap<String, String> item = itemList.get(position);

        if (Objects.equals(item.get("isDone"), "1")) {
            holder.sno.setText(item.get("SNo"));
            holder.waybillno.setText(item.get("WaybillNo"));
            holder.remarks.setText(item.get("Remarks"));
            holder.status.setText(item.get("WStatus"));
            holder.status.setTextColor(Color.parseColor(item.get("Color")));
            holder.ispaid.setTextColor(Color.parseColor(item.get("Color")));
            holder.ispaid.setText(item.get("isPaid"));

        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sno, waybillno, status, remarks, ispaid;

        public MyViewHolder(View view) {
            super(view);
            sno = (TextView) view.findViewById(R.id.sno);
            waybillno = (TextView) view.findViewById(R.id.waybillno);
            status = (TextView) view.findViewById(R.id.status);
            remarks = (TextView) view.findViewById(R.id.remarks);
            ispaid = (TextView) view.findViewById(R.id.ispaid);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onItemSelected(itemList.get(getAdapterPosition()), getAdapterPosition());
                }
            });


            view.setTag(this);
        }
    }


    public interface RouteAdapterListener {
        void onItemSelected(HashMap<String, String> hashMap, int position);
    }
}