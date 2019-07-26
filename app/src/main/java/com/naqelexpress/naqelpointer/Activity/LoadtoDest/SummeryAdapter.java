package com.naqelexpress.naqelpointer.Activity.LoadtoDest;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class SummeryAdapter
        extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> itemList;

    public SummeryAdapter(ArrayList<HashMap<String, String>> itemList, Context context) {
        this.context = context;
        this.itemList = itemList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.summeryadapter, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        HashMap<String, String> item = getItem(position);

        holder.waybillno.setText(String.valueOf(item.get("WaybillNo")));
        holder.piece.setText(String.valueOf(item.get("PieceCount")));
        holder.scnpiece.setText(String.valueOf(item.get("ScannedPC")));
        holder.pending.setText(String.valueOf(item.get("pending")));

        ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.piece, holder.waybillno);
        StartAsyncTaskInParallel(updatemessages, item.get("bgcolor"), item.get("SW"));

        return convertView;
    }

    class ViewHolder {
        TextView waybillno, piece, scnpiece, pending;

        public ViewHolder(View view) {
            waybillno = (TextView) view.findViewById(R.id.waybillno);
            piece = (TextView) view.findViewById(R.id.pieces);
            scnpiece = (TextView) view.findViewById(R.id.scnpieces);
            pending = (TextView) view.findViewById(R.id.notscan);

            view.setTag(this);
        }
    }


    private void StartAsyncTaskInParallel(ImageDownloaderTask asynthread, String keys, String sw) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keys, sw);
        else
            asynthread.execute(keys);

    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Integer[]> {
        private final WeakReference<TextView> imageViewReference;
        private final WeakReference<TextView> waybillRef;

        public ImageDownloaderTask(TextView imageView, TextView waybillno) {
            imageViewReference = new WeakReference<TextView>(imageView);
            waybillRef = new WeakReference<TextView>(waybillno);
        }

        @Override
        protected Integer[] doInBackground(String... params) {

//            TextView textView[] = new TextView[2];
//
//            textView[0] = new TextView(context);
//            textView[0].setTextColor(Color.parseColor("#292a2b"));
//            if (params[0].equals("1"))
//                textView[0].setBackgroundColor(Color.parseColor("#FF00b339"));
//            else
//                textView[0].setBackgroundColor(Color.parseColor("#FFdbdcdd"));
//
            Integer color[] = new Integer[2];
            if (params[0].equals("1"))
                color[0] = Color.parseColor("#FF00b339");
            else
                color[0] = Color.parseColor("#EA1908");

            if (params[1].equals("1"))
                color[1] = 0;
            else
                color[1] = Color.parseColor("#EA1908");


            return color;
        }

        @Override
        protected void onPostExecute(Integer[] colorcode) {
            if (isCancelled()) {
                colorcode = null;
            }

            if (imageViewReference != null) {
                TextView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (colorcode != null) {
                        imageView.setBackgroundColor(colorcode[0]);
                    }
                }
            }

            if (waybillRef != null) {
                TextView waybillbg = waybillRef.get();
                if (waybillbg != null) {
                    if (colorcode != null) {
                        waybillbg.setBackgroundColor(colorcode[1]);

                    }
                }
            }

        }
    }
}