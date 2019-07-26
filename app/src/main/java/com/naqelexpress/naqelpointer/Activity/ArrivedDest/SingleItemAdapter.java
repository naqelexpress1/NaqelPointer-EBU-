package com.naqelexpress.naqelpointer.Activity.ArrivedDest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class SingleItemAdapter
        extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> itemList;

    public SingleItemAdapter(ArrayList<HashMap<String, String>> itemList, Context context) {
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
            convertView = View.inflate(context, R.layout.palletadapter_atdest, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        HashMap<String, String> item = getItem(position);

        holder.waybillno.setText(String.valueOf(item.get("PalletNo")));


        //if(item.get("bgcolor").equals("1"))
        //    holder.waybillno.setTextColor(Color.parseColor("#00cc00"));

        ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.priority, holder.waybillno);
        StartAsyncTaskInParallel(updatemessages, item.get("bgcolor"), item.get("LoadType"));

        return convertView;
    }

    class ViewHolder {
        TextView waybillno;
        ImageView priority;

        public ViewHolder(View view) {
            waybillno = (TextView) view.findViewById(R.id.waybillno);
            priority = (ImageView) view.findViewById(R.id.priority);

            view.setTag(this);
        }
    }


    private void StartAsyncTaskInParallel(ImageDownloaderTask asynthread, String keys, String Loadtype) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keys, Loadtype);
        else
            asynthread.execute(keys, Loadtype);

    }

    class ImageDownloaderTask extends AsyncTask<String, Void, int[]> {
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<TextView> textViewReference;

        public ImageDownloaderTask(ImageView imageView, TextView textView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            textViewReference = new WeakReference<TextView>(textView);
        }

        @Override
        protected int[] doInBackground(String... params) {

            //ImageView imageview = new ImageView(context);
            int color[] = new int[2];
            if (params[1].equals("1"))
                color[0] = 1;
            else if (params[1].equals("2"))
                color[0] = 2;
            else if (params[1].equals("3") || params[1].equals("0"))
                color[0] = 3;

            if (params[0].equals("0"))
                color[1] = Color.parseColor("#000000");
            else if (params[0].equals("1"))
                color[1] = Color.parseColor("#FF00b339");


            //imageview.setImageBitmap(bitmap);

            return color;
        }

        @Override
        protected void onPostExecute(int[] colors) {
            if (isCancelled()) {
                colors = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                TextView textView = textViewReference.get();
                if (imageView != null) {
                    if (colors != null) {

                        Bitmap bitmap = null;
                        if (colors[0] == 1)
                            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.red);
                        else if (colors[0] == 2)
                            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow);
                        else if (colors[0] == 3)
                            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.green);
                        imageView.setImageBitmap(bitmap);

                        if (textView != null)
                            textView.setTextColor(colors[1]);

                        imageView.setImageBitmap(bitmap);


                    }
                }
            }
        }
    }

}