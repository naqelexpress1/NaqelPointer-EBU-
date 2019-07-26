package com.naqelexpress.naqelpointer.Activity.PendingMoney;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.JSON.Results.CheckPendingCODResult;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

class PendingListAdapter
        extends BaseAdapter
{
    private Context context;
    private List<CheckPendingCODResult> itemList;

    PendingListAdapter(Context context, List<CheckPendingCODResult> itemList)
    {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount()
    {
        return itemList.size();
    }

    @Override
    public CheckPendingCODResult getItem(int position)
    {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount()
    {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        // current menu type
        return position % 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = View.inflate(context,R.layout.pendingmoneyitem, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        CheckPendingCODResult item = getItem(position);
        holder.txtWaybill.setText(String.valueOf(item.WaybillNo));
        holder.lbSerial.setText(String.valueOf(getItemId(position) + 1));

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MMM/yyyy");
        String dateString = fmt.print(item.DeliveryDate);
        holder.txtDeliveryDate.setText(dateString);
        holder.txtAmount.setText(String.valueOf(item.Amount));

        return convertView;
    }

    private class ViewHolder
    {
        TextView txtWaybill, txtAmount, lbSerial,txtDeliveryDate;
        ViewHolder(View view)
        {
            txtWaybill = (TextView) view.findViewById(R.id.txtWaybilll);
            lbSerial = (TextView) view.findViewById(R.id.lbSerial);
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
            txtDeliveryDate = (TextView) view.findViewById(R.id.txtDeliveryDate);
            view.setTag(this);
        }
    }

//    private int dp2px(int dp)
//    {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                getResources().getDisplayMetrics());
//    }
}