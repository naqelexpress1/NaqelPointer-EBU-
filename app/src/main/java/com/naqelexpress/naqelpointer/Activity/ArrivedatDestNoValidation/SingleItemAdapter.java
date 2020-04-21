package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNoValidation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class SingleItemAdapter
        extends BaseAdapter {
    private Context context;
    private ArrayList<String> itemList;
    private Activity activity;

    public SingleItemAdapter(ArrayList<String> itemList, Context context, Activity activity) {
        this.context = context;
        this.itemList = itemList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public String getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.piececodeadapter, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.waybillno.setText(getItem(position));

        holder.waybillno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemovePieceBarcode(getItem(position), position);

            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView waybillno;

        public ViewHolder(View view) {
            waybillno = (TextView) view.findViewById(R.id.waybillno);
            view.setTag(this);
        }
    }


    private void RemovePieceBarcode(final String message, final int position) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Delete Confirm");
            alertDialog.setMessage("Do you want to remove this Piece(" + message + ") Barcode?.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DBConnections dbConnections = new DBConnections(context, null);
                            dbConnections.deleteAtDestPiece(message, context);
                            dbConnections.close();
                            itemList.remove(message);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            System.out.println(e);
        }


    }

}