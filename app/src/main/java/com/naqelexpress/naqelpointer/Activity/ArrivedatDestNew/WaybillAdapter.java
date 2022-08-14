package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNew;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class WaybillAdapter
        extends BaseAdapter {
    private Context context;
    private Activity activity;
    private ArrayList<HashMap<String, String>> itemList;

    public WaybillAdapter(ArrayList<HashMap<String, String>> itemList, Context context, Activity activity) {
        this.context = context;
        this.itemList = itemList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
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
            convertView = View.inflate(context, R.layout.waybillheader, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        final HashMap<String, String> item = getItem(position);

        holder.waybillno.setText(String.valueOf(item.get("WaybillNo")));
        holder.waybillno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.get("bgcolor").equals("1"))
                    RemoveWayBillNo(item.get("WaybillNo"), position);
                // Toast.makeText(context, String.valueOf(position) + " " + item.get("bgcolor"), Toast.LENGTH_SHORT).show();

            }
        });

//        if (item.get("bgcolor").equals("1"))
//            convertView.setBackgroundColor(Color.parseColor("#00b339"));

        ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.waybillno);
        StartAsyncTaskInParallel(updatemessages, item.get("bgcolor"));

        return convertView;
    }

    class ViewHolder {
        TextView waybillno;

        public ViewHolder(View view) {
            waybillno = (TextView) view.findViewById(R.id.waybill);

            view.setTag(this);
        }
    }


    private void StartAsyncTaskInParallel(ImageDownloaderTask asynthread, String keys) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keys);
        else
            asynthread.execute(keys);

    }

    class ImageDownloaderTask extends AsyncTask<String, Void, TextView> {
        private final WeakReference<TextView> imageViewReference;

        public ImageDownloaderTask(TextView imageView) {
            imageViewReference = new WeakReference<TextView>(imageView);
        }

        @Override
        protected TextView doInBackground(String... params) {

            TextView textView = new TextView(context);
            textView.setTextColor(Color.parseColor("#292a2b"));
            if (params[0].equals("1"))
                textView.setTextColor(Color.parseColor("#FF00b339"));
            else
                textView.setTextColor(Color.parseColor("#FFdbdcdd"));

            return textView;
        }

        @Override
        protected void onPostExecute(TextView bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                TextView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        //imageView.setBackgroundColor(Color.parseColor("#00b339"));
                        String text = bitmap.getText().toString();
                        ColorDrawable cd = (ColorDrawable) bitmap.getBackground();
                        int colorCode = bitmap.getCurrentTextColor();//cd.getColor();
                        //  imageView.setText(text);
                        //  imageView.setTextColor(Color.parseColor("#FF292a2b"));
                        imageView.setBackgroundColor(colorCode);

                    }
                }
            }
        }
    }


    private void RemoveWayBillNo(final String message, final int position) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Do you want to remove WaybillNo(" + message + ")?");
            alertDialog.setMessage("Related Piece Barcode also will be remove.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            RemoveWaybillwithPiece(message, position);
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

    private void RemoveWaybillwithPiece(String rmwaybillno, int position) {

        //Remove WayBill From List
        HashSet<Integer> removewaybill = new HashSet<>();
        ArrayList<Integer> reversewaybill = new ArrayList<>();
        for (int i = 0; i < Waybill.Selectedwaybill.size(); i++) {
            String waybillno = Waybill.Selectedwaybill.get(i).get("WaybillNo");
            if (rmwaybillno.equals(waybillno)) {
                String rmwaybill = Waybill.Selectedwaybill.get(i).get("WaybillNo");
                Waybill.validatewaybillist.remove(rmwaybill);
                removewaybill.add(i);
            }
        }
        reversewaybill.addAll(removewaybill);
        Collections.reverse(reversewaybill);

        for (int i = 0; i < reversewaybill.size(); i++) {
            int rmpos = reversewaybill.get(i);
            Waybill.Selectedwaybill.remove(rmpos);
        }


        ////Remove Pieces Related Waybill From List
        HashSet<Integer> remove = new HashSet<>();
        ArrayList<Integer> reverse = new ArrayList<>();
        for (int i = 0; i < SingleItem.SelectedSingleLoad.size(); i++) {
            String waybillno = SingleItem.SelectedSingleLoad.get(i).get("WaybillNo");
            if (rmwaybillno.equals(waybillno)) {
                String rmpiece = SingleItem.SelectedSingleLoad.get(i).get("BarCode");
                SingleItem.ValidateBarCodeList.remove(rmpiece);
                remove.add(i);
            }
        }
        reverse.addAll(remove);
        Collections.reverse(reverse);

        for (int i = 0; i < reverse.size(); i++) {
            int rmpos = reverse.get(i);
            SingleItem.SelectedSingleLoad.remove(rmpos);
        }

        //Change Pieces BG

        for (int j = 0; j < SingleItem.SingleLoad.size(); j++) {
            String waybillno = SingleItem.SingleLoad.get(j).get("WaybillNo");
            if (rmwaybillno.equals(waybillno)) {
                SingleItem.SingleLoad.get(j).put("bgcolor", "0");
            }
        }

        SingleItem.adapter.notifyDataSetChanged();

        //Changed Waybill BgColor
        itemList.get(position).put("bgcolor", "0");
        notifyDataSetChanged();

    }
}