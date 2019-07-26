package com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.util.HashMap;
import java.util.List;

public class DataAdapter
        extends BaseAdapter {
    private List<HashMap<String, String>> waybills;
    private Context context;

    public DataAdapter(List<HashMap<String, String>> waybills, Context context) {
        this.waybills = waybills;
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position % 2;
    }

    @Override
    public int getCount() {
        return waybills.size();
    }

    @Override
    public HashMap<String, String> getItem(int i) {
        return waybills.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {


        if (convertView == null) {
            convertView = View.inflate(context, R.layout.atoriginwaybilldetails, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        HashMap<String, String> item = getItem(i);

        holder.waybillno.setText(String.valueOf(item.get("WaybillNo")));
        holder.piece.setText(waybills.get(i).get("PieceCount"));
        holder.spcount.setText(waybills.get(i).get("ScannedPC"));


        return convertView;
    }

    class ViewHolder {
        TextView waybillno, piece, spcount;

        public ViewHolder(View view) {
            waybillno = (TextView) view.findViewById(R.id.waybill);
            piece = (TextView) view.findViewById(R.id.piece);
            spcount = (TextView) view.findViewById(R.id.spcount);

            view.setTag(this);
        }
    }
}