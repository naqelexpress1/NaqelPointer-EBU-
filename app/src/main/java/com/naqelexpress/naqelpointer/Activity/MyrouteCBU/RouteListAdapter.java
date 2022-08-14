package com.naqelexpress.naqelpointer.Activity.MyrouteCBU;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RouteListAdapter
        extends BaseAdapter implements Filterable {
    private Context context;
    private List<MyRouteShipments> itemList;
    private String class_;
    private List<MyRouteShipments> itemListFiltered;
    private RouteAdapterListener listener;

    public RouteListAdapter(Context context, List<MyRouteShipments> itemList, String calss_) {
        this.context = context;
        this.itemList = itemList;
        itemListFiltered = itemList;
        this.class_ = calss_;
    }

    @Override
    public int getCount() {
        return itemListFiltered.size();
    }

    @Override
    public MyRouteShipments getItem(int position) {
        return itemListFiltered.get(position);
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
            convertView = View.inflate(context, R.layout.routeitem, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        MyRouteShipments item = getItem(position);


        holder.lbSerial.setText(String.valueOf(getItemId(position) + 1));
        if (class_.equals("CourierKpi")) {
            holder.txtWaybill.setText(item.ItemNo);
            Integer typeID = itemListFiltered.get(position).TypeID;
            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
            String dateString = fmt.print(itemListFiltered.get(position).ExpectedTime);
//            holder.txtExpectedTime.setText(dateString);
            holder.txtExpectedTime.setText(item.ExistUser);
            //if (position %4 == 0)
            //holder.panel.setBackgroundColor(Color.RED);

            if (itemListFiltered.get(position).HasComplaint) {
                holder.imgHasComplaint.setVisibility(View.VISIBLE);
                holder.imgHasComplaint.setImageResource(R.drawable.redstar);
                holder.imgHasComplaint.refreshDrawableState();

//                HasComplaint updatemessages = new HasComplaint(holder.cl);
//                StartAsyncTaskInParallelHasComplaint(updatemessages, "1");

            } else {
                holder.imgHasComplaint.setVisibility(View.GONE);
//                HasComplaint updatemessages = new HasComplaint(holder.cl);
//                StartAsyncTaskInParallelHasComplaint(updatemessages, "0");
            }
            //holder.header.setText(item.PODDetail);

            if (itemListFiltered.get(position).HasDeliveryRequest) {
//                ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.cl);
//                StartAsyncTaskInParallel(updatemessages, "1");
                holder.cl.setBackgroundColor(Color.BLUE);
                holder.imgHasDeliveryRequest.setVisibility(View.VISIBLE);
                holder.imgHasDeliveryRequest.setImageResource(R.drawable.request);
                holder.imgHasComplaint.refreshDrawableState();
            } else {
                holder.imgHasDeliveryRequest.setVisibility(View.GONE);
//                ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.cl);
//                StartAsyncTaskInParallel(updatemessages, "0");
            }

            if (itemListFiltered.get(position).HasDeliveryRequest) {
                ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.cl);
                StartAsyncTaskInParallel(updatemessages, "1");

            } else if (itemListFiltered.get(position).HasComplaint) {
                HasComplaint updatemessages = new HasComplaint(holder.cl);
                StartAsyncTaskInParallelHasComplaint(updatemessages, "1");

            } else {
                ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.cl);
                StartAsyncTaskInParallel(updatemessages, "0");
            }

            if (item.Latitude.length() > 3 && item.Longitude.length() > 3) {
                holder.imgHasLocation.setVisibility(View.VISIBLE);
                holder.imgHasLocation.setImageResource(R.drawable.marker);
            } else
                holder.imgHasLocation.setVisibility(View.INVISIBLE);

            if (typeID == 1) {
//                holder.txtType.setText("Delivery");
            } else if (typeID == 2) {
//                holder.txtType.setText("PickUp");
            }
            if (item.IsDelivered) {
                holder.txtType.setText("Delivered");
                holder.txtType.setTextColor(Color.parseColor("#118211"));
            } else {
                if (item.IsPartialDelivered) {
                    holder.txtType.setText("Partial Delivered");
                    holder.txtType.setTextColor(Color.parseColor("#9B0000"));
                } else {

                    if (item.NotDelivered) {
                        holder.txtType.setText("Not Delivered");
                        holder.txtType.setTextColor(Color.parseColor("#FBD904"));
                    } else {
                        holder.txtType.setText("Not Attempt");
                        holder.txtType.setTextColor(Color.parseColor("#F94506"));
                    }
                }
            }

        } else {
            holder.txtWaybill.setText("Waybill No\n" + item.ItemNo);
            holder.txtType.setVisibility(View.VISIBLE);
            holder.imgHasComplaint.setVisibility(View.GONE);
            holder.imgHasDeliveryRequest.setVisibility(View.VISIBLE);
            holder.txtExpectedTime.setVisibility(View.VISIBLE);

            if (item.IsDelivered) {
                holder.imgHasDeliveryRequest.setImageResource(R.drawable.accpet_job);
            } else
                holder.imgHasDeliveryRequest.setImageResource(R.drawable.jobnotsync);

            // holder.imgHasComplaint.setVisibility(View.GONE);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date dt = formatter.parse(item.ExpectedTime.toString());

                DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy");
                String dte = dfmt.format(dt);

                DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
                String time = fmt.print(item.ExpectedTime);

                holder.txtExpectedTime.setText(dte + " " + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (item.TypeID != 0) {
                holder.txtType.setVisibility(View.VISIBLE);
                holder.txtWaybill.setText("Waybillcount " + String.valueOf(item.TypeID) + "\n" + "PiecesCount " + String.valueOf(item.PiecesCount));
                holder.txtType.setText("Pieces Count " + String.valueOf(item.PiecesCount));
            }
            holder.imgHasLocation.setVisibility(View.GONE);
            holder.lbDeliveryDate.setVisibility(View.GONE);
            holder.txtAmount.setVisibility(View.GONE);

        }
        return convertView;
    }

    class ViewHolder {
        TextView txtWaybill, txtType, lbSerial, txtExpectedTime, lbDeliveryDate, txtAmount, header;
        ImageView imgHasLocation, imgHasComplaint, imgHasDeliveryRequest;
        ConstraintLayout cl;
        //TextView panel;

        public ViewHolder(View view) {
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
            lbDeliveryDate = (TextView) view.findViewById(R.id.lbDeliveryDate);
            txtWaybill = (TextView) view.findViewById(R.id.txtWaybilll);
            lbSerial = (TextView) view.findViewById(R.id.lbSerial);
            txtType = (TextView) view.findViewById(R.id.txtAmount);
            txtExpectedTime = (TextView) view.findViewById(R.id.txtExpectedTime);
            //panel = (TextView) view.findViewById(R.id.panel);
            imgHasLocation = (ImageView) view.findViewById(R.id.imgHasLocation);

            imgHasComplaint = (ImageView) view.findViewById(R.id.imgHasComplaint);
            imgHasDeliveryRequest = (ImageView) view.findViewById(R.id.imgHasRequest);
            header = (TextView) view.findViewById(R.id.changesheader);
            cl = (ConstraintLayout) view.findViewById(R.id.changeView);


            view.setTag(this);
        }
    }


    private void StartAsyncTaskInParallel(ImageDownloaderTask asynthread, String keys) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keys);
        else
            asynthread.execute(keys);

    }

    private void StartAsyncTaskInParallelHasComplaint(HasComplaint asynthread, String keys) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keys);
        else
            asynthread.execute(keys);

    }

    class ImageDownloaderTask extends AsyncTask<String, Void, ConstraintLayout> {
        private final WeakReference<ConstraintLayout> imageViewReference;

        public ImageDownloaderTask(ConstraintLayout imageView) {
            imageViewReference = new WeakReference<ConstraintLayout>(imageView);
        }

        @Override
        protected ConstraintLayout doInBackground(String... params) {

            ConstraintLayout textView = new ConstraintLayout(context);

            if (params[0].equals("1"))
                textView.setBackgroundColor(Color.parseColor("#F6F600"));
            else
                textView.setBackgroundColor(Color.parseColor("#FFdbdcdd"));


            return textView;
        }

        @Override
        protected void onPostExecute(ConstraintLayout bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ConstraintLayout imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        ColorDrawable cd = (ColorDrawable) bitmap.getBackground();
                        int colorCode = cd.getColor();
                        imageView.setBackgroundColor(colorCode);

                    }
                }
            }
        }
    }


    class HasComplaint extends AsyncTask<String, Void, ConstraintLayout> {
        private final WeakReference<ConstraintLayout> imageViewReference;

        public HasComplaint(ConstraintLayout imageView) {
            imageViewReference = new WeakReference<ConstraintLayout>(imageView);
        }

        @Override
        protected ConstraintLayout doInBackground(String... params) {

            ConstraintLayout textView = new ConstraintLayout(context);

            if (params[0].equals("1"))
                textView.setBackgroundColor(Color.parseColor("#F97E45"));
            else
                textView.setBackgroundColor(Color.parseColor("#FFdbdcdd"));


            return textView;
        }

        @Override
        protected void onPostExecute(ConstraintLayout bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ConstraintLayout imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        ColorDrawable cd = (ColorDrawable) bitmap.getBackground();
                        int colorCode = cd.getColor();
                        imageView.setBackgroundColor(colorCode);

                    }
                }
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFiltered = itemList;
                } else {
                    List<MyRouteShipments> filteredList = new ArrayList<>();
                    for (MyRouteShipments row : itemList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.ItemNo.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    itemListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemListFiltered = (ArrayList<MyRouteShipments>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface RouteAdapterListener {
        void onItemSelected(MyRouteShipments contact);
    }
}