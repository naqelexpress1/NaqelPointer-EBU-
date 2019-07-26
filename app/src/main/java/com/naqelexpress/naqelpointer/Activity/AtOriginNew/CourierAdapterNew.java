package com.naqelexpress.naqelpointer.Activity.AtOriginNew;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class CourierAdapterNew
        extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> itemList;

    public CourierAdapterNew(ArrayList<HashMap<String, String>> itemList, Context context) {
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
            convertView = View.inflate(context, R.layout.waybillheader, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        HashMap<String, String> item = getItem(position);

        holder.waybillno.setText(String.valueOf(item.get("WaybillNo")));

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
                textView.setBackgroundColor(Color.parseColor("#FF00b339"));
            else
                textView.setBackgroundColor(Color.parseColor("#FFdbdcdd"));

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
                        int colorCode = cd.getColor();
                        //  imageView.setText(text);
                        //  imageView.setTextColor(Color.parseColor("#FF292a2b"));
                        imageView.setBackgroundColor(colorCode);

                    }
                }
            }
        }
    }
}