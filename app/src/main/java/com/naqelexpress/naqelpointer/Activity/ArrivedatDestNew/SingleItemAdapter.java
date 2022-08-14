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

public class SingleItemAdapter
        extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> itemList;
    private Activity activity;

    public SingleItemAdapter(ArrayList<HashMap<String, String>> itemList, Context context, Activity activity) {
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
            convertView = View.inflate(context, R.layout.piececodeadapter, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        final HashMap<String, String> item = getItem(position);

        holder.waybillno.setText(String.valueOf(item.get("BarCode")));

        holder.waybillno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.get("bgcolor").equals("1"))
                    RemovePieceBarcode(item.get("BarCode"), position);
                // Toast.makeText(context, String.valueOf(position) + " " + item.get("bgcolor"), Toast.LENGTH_SHORT).show();

            }
        });

        //if(item.get("bgcolor").equals("1"))
        //    holder.waybillno.setTextColor(Color.parseColor("#00cc00"));

        ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.waybillno);
        StartAsyncTaskInParallel(updatemessages, item.get("bgcolor"), "3");

        return convertView;
    }

    class ViewHolder {
        TextView waybillno;

        public ViewHolder(View view) {
            waybillno = (TextView) view.findViewById(R.id.waybillno);
            view.setTag(this);
        }
    }


    private void StartAsyncTaskInParallel(ImageDownloaderTask asynthread, String keys, String Loadtype) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keys, Loadtype);
        else
            asynthread.execute(keys, Loadtype);

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

    private void RemovePieceBarcode(final String message, final int position) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Delete Confirm");
            alertDialog.setMessage("Do you want to remove this Piece(" + message + ") Barcode?.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
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

    private void RemoveWaybillwithPiece(String rmPieceBarcode, int position) {

        ////Remove Pieces Related Waybill From List
        HashSet<Integer> remove = new HashSet<>();
        ArrayList<Integer> reverse = new ArrayList<>();
        for (int i = 0; i < SingleItem.SelectedSingleLoad.size(); i++) {
            String piecebarcode = SingleItem.SelectedSingleLoad.get(i).get("BarCode");
            if (rmPieceBarcode.equals(piecebarcode)) {
                remove.add(i);
                break;
            }
        }
        reverse.addAll(remove);
        Collections.reverse(reverse);

        for (int i = 0; i < reverse.size(); i++) {
            int rmpos = reverse.get(i);
            SingleItem.SelectedSingleLoad.remove(rmpos);
        }

        SingleItem.ValidateBarCodeList.remove(rmPieceBarcode);
        SingleItem.SingleLoad.get(position).put("bgcolor", "0");
        notifyDataSetChanged();


    }
//    class ImageDownloaderTask extends AsyncTask<String, Void, int[]> {
//        private final WeakReference<TextView> textViewReference;
//
//        public ImageDownloaderTask(TextView textView) {
//            textViewReference = new WeakReference<TextView>(textView);
//        }
//
//        @Override
//        protected int[] doInBackground(String... params) {
//
//            int color[] = new int[2];
//            if (params[1].equals("1"))
//                color[0] = 1;
//            else if (params[1].equals("2"))
//                color[0] = 2;
//            else if (params[1].equals("3") || params[1].equals("0"))
//                color[0] = 3;
//
//            if (params[0].equals("0"))
//                color[1] = Color.parseColor("#000000");
//            else if (params[0].equals("1"))
//                color[1] = Color.parseColor("#FF00b339");
//            else if (params[0].equals("3"))
//                color[1] = Color.parseColor("#FFdbdcdd");
//
//
//            return color;
//        }
//
//        @Override
//        protected void onPostExecute(int[] colors) {
//            if (isCancelled()) {
//                colors = null;
//            }
//
//            if (textViewReference != null) {
//                TextView textView = textViewReference.get();
//                if (textView != null) {
//                    if (colors != null) {
//                        if (textView != null)
//                            textView.setBackgroundColor(colors[1]);
//                    }
//                }
//            }
//        }
//    }

}