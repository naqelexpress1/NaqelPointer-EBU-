package com.naqelexpress.naqelpointer.Activity.MyRouteCBUPaperless;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RouteListAdapterPaperlessDS
        extends RecyclerView.Adapter<RouteListAdapterPaperlessDS.MyViewHolder> implements Filterable {
    private Context context;
    private List<MyRouteShipments> itemList;
    private String class_;
    private List<MyRouteShipments> itemListFiltered;
    private MyRouteActivity_PaperlessDS listener;

    public RouteListAdapterPaperlessDS(Context context, List<MyRouteShipments> itemList, String calss_,
                                       MyRouteActivity_PaperlessDS listener) {
        this.context = context;
        this.itemList = itemList;
        itemListFiltered = itemList;
        this.class_ = calss_;
        this.listener = listener;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup convertView, int position) {

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.routeitem, convertView, false);

        return new MyViewHolder(itemView);


//        return convertView;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        MyRouteShipments item = itemListFiltered.get(position);


        holder.lbSerial.setText(String.valueOf(getItemId(position) + 1));
        if (class_.equals("CourierKpi")) {
            holder.txtWaybill.setText(item.ItemNo);
            Integer typeID = itemListFiltered.get(position).TypeID;
            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
            String dateString = fmt.print(itemListFiltered.get(position).ExpectedTime);
            holder.txtExpectedTime.setText(item.ExistUser);


            if (itemListFiltered.get(position).HasComplaint) {
                holder.imgHasComplaint.setVisibility(View.VISIBLE);
                holder.imgHasComplaint.setImageResource(R.drawable.redstar);
                holder.imgHasComplaint.refreshDrawableState();


            } else {
                holder.imgHasComplaint.setVisibility(View.GONE);

            }

            if (itemListFiltered.get(position).HasDeliveryRequest) {
                holder.cl.setBackgroundColor(Color.BLUE);
                holder.imgHasDeliveryRequest.setVisibility(View.VISIBLE);
                holder.imgHasDeliveryRequest.setImageResource(R.drawable.request);
                holder.imgHasComplaint.refreshDrawableState();
            } else {
                holder.imgHasDeliveryRequest.setVisibility(View.GONE);

            }
            if (itemListFiltered.get(position).BGColor.equals("CurrentOne")) {
                ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.cl);
                StartAsyncTaskInParallel(updatemessages, itemListFiltered.get(position).BGColor);
            } else if (!itemListFiltered.get(position).BGColor.equals("0")) {
                ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.cl);
                StartAsyncTaskInParallel(updatemessages, itemListFiltered.get(position).BGColor);
            } else if (itemListFiltered.get(position).HasDeliveryRequest) {
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

            if (item.DsOrderNo != 0)
                holder.lbDeliveryDate.setText("DS Order No : - " + String.valueOf(item.DsOrderNo)); // + " - " + String.valueOf(item.IsNotifyWaybillNo)
            else
                holder.lbDeliveryDate.setVisibility(View.INVISIBLE);

            if (item.IsPaid == 1) {
                holder.ispaid.setText("PAID");
                holder.ispaid.setTextColor(Color.GREEN);
            } else if (item.IsPaid == 2) {
                holder.ispaid.setText("A");
                holder.ispaid.setTextColor(Color.BLUE);
            } else if (item.IsPaid == 0) {
                holder.ispaid.setText("NOT PAID");
                holder.ispaid.setTextColor(Color.RED);
            } else {
                holder.ispaid.setText("");
                holder.ispaid.setTextColor(Color.RED);
            }


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
            holder.imgHasComplaint.setVisibility(View.GONE);
            holder.imgHasLocation.setVisibility(View.GONE);
            holder.txtExpectedTime.setVisibility(View.GONE);
            holder.lbDeliveryDate.setVisibility(View.GONE);
            holder.txtAmount.setVisibility(View.GONE);

        }

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtWaybill, txtType, lbSerial, txtExpectedTime, lbDeliveryDate, txtAmount, header, ispaid;
        ImageView imgHasLocation, imgHasComplaint, imgHasDeliveryRequest;
        ConstraintLayout cl;
        //TextView panel;

        public MyViewHolder(View view) {
            super(view);
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
            lbDeliveryDate = (TextView) view.findViewById(R.id.lbDeliveryDate);
            txtWaybill = (TextView) view.findViewById(R.id.txtWaybilll);
            lbSerial = (TextView) view.findViewById(R.id.lbSerial);
            txtType = (TextView) view.findViewById(R.id.txtAmount);
            txtExpectedTime = (TextView) view.findViewById(R.id.txtExpectedTime);
            ispaid = (TextView) view.findViewById(R.id.ispaid);
            //panel = (TextView) view.findViewById(R.id.panel);
            imgHasLocation = (ImageView) view.findViewById(R.id.imgHasLocation);

            imgHasComplaint = (ImageView) view.findViewById(R.id.imgHasComplaint);
            imgHasDeliveryRequest = (ImageView) view.findViewById(R.id.imgHasRequest);
            header = (TextView) view.findViewById(R.id.changesheader);
            cl = (ConstraintLayout) view.findViewById(R.id.changeView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onItemSelected(itemListFiltered.get(getAdapterPosition()), getAdapterPosition());
                }
            });


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

            @SuppressLint("WrongThread") ConstraintLayout textView = new ConstraintLayout(context);

            if (params[0].equals("CurrentOne"))
                textView.setBackgroundColor(Color.parseColor("#64D179"));
            else if (params[0].equals("1"))
                textView.setBackgroundColor(Color.parseColor("#F6F600"));
            else if (params[0].equals("0"))
                textView.setBackgroundColor(Color.parseColor("#FFdbdcdd"));
            else
                textView.setBackgroundColor(Color.parseColor(params[0]));


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
        void onItemSelected(MyRouteShipments contact, int position);
    }
}