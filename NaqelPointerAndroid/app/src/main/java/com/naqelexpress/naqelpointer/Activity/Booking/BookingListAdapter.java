package com.naqelexpress.naqelpointer.Activity.Booking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.PickUp.PickUpActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookingListAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<Booking> itemList;
    private String class_;

    public BookingListAdapter(Activity context, ArrayList<Booking> itemList, String class_) {
        this.context = context;
        this.itemList = itemList;
        this.class_ = class_;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Booking getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected static class ViewHolderItems {
        private TextView ItemNo;
        private TextView TypeID;
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
            convertView = View.inflate(context, R.layout.bookingitemnew, null);
            new ViewHolder(convertView);
        } else {
            // holdeconvertView = (ViewHolder) convertView.getTag();
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final Booking item = getItem(position);

        holder.txtReferenceNo.setText(item.RefNo);

        if (class_.equals("BookingList")) {
            DateTimeFormatter fmtRD = DateTimeFormat.forPattern("dd-mm-yyyy");
            String dateStringRD = fmtRD.print(itemList.get(position).OfficeUpTo);


            DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date dt = formatter.parse(itemList.get(position).PickUpReqDT);
                DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy");
                String dte = dfmt.format(dt);


                holder.txtRequiredDate.setText(dte);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dateStringRT = fmtRT.print(DateTime.parse(itemList.get(position).PickUpReqDT));
            // holder.txtRequiredTime.setText(dateStringRT);
            //holder.txtRequiredTime.setVisibility(View.GONE);
            holder.lblClientId.setText(itemList.get(position).ClientName);
            // holder.txtRequiredDate.setText(fmtRD.print(DateTime.parse(itemList.get(position).PickUpReqDT)));


            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
            String dateStringCD = fmt.print(itemList.get(position).OfficeUpTo);
            //  holder.txtCloseTime.setText(dateStringCD);

            final int acceptposition = position;

            holder.acceptJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DBConnections dbConnections = new DBConnections(context, null);
                    JSONObject jsonObject = new JSONObject();
                    try {


                        // jsonObject.put("EmployID", 0);
                        jsonObject.put("RefNo", itemList.get(acceptposition).RefNo); //
                        //  jsonObject.put("CurrentStatusID", 7);
                        //  jsonObject.put("AppTypeID", "");
                        //   jsonObject.put("AppVersion", "");
                        //   jsonObject.put("LanguageID", "");
                        final String jsonData = jsonObject.toString();

                        Cursor result = dbConnections.Fill("select * from PickUp where IsSync = 0 and RefNo=" +
                                itemList.get(acceptposition).RefNo,context);
                        if (result.getCount() == 0) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Info")
                                    .setMessage("Are you sure you want to update the Acknowledgement?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {

                                            new AcknowledgeBookingData(holder.acceptJob).execute(jsonData, String.valueOf(acceptposition));

                                        }
                                    }).setNegativeButton("Cancel", null).setCancelable(false);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else
                            GlobalVar.GV().ShowSnackbar(((Activity) context).getWindow().getDecorView().getRootView(),
                                    "you picked up this item, please sync data", GlobalVar.AlertType.Error);
                        dbConnections.close();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.pickup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBConnections dbConnections = new DBConnections(context, null);
                    Cursor result = dbConnections.Fill("select * from PickUp where IsSync = 0 and RefNo=" + itemList.get(position).RefNo,context);
                    if (result.getCount() == 0) {
                        Intent intent = new Intent(context, PickUpActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putParcelableArrayListExtra("value", itemList);
                        bundle.putString("class", "BookingDetailAcyivity");
                        bundle.putInt("position", position);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    } else
                        GlobalVar.GV().ShowSnackbar(context.getWindow().getDecorView().getRootView(), "you picked up this item, please sync data", GlobalVar.AlertType.Error);
                    dbConnections.close();

                }
            });
        }
        holder.txtPieces.setText(String.valueOf(item.PicesCount));
        holder.txtWeight.setText(String.valueOf(item.Weight));


        ImageDownloaderTask updatemessages = new ImageDownloaderTask(holder.acceptJob);
        StartAsyncTaskInParallel(updatemessages, String.valueOf(itemList.get(position).Status), class_);

//            holder.acceptJob.setImageResource(R.drawable.accpet_job);


        //if (position %4 == 0)
        //holder.panel.setBackgroundColor(Color.RED);

//        if (itemList.get(position).HasComplaint) {
//            holder.imgHasComplaint.setVisibility(View.VISIBLE);
//            holder.imgHasComplaint.setImageResource(R.drawable.redstar);
//            holder.imgHasComplaint.refreshDrawableState();
//        } else
//            holder.imgHasComplaint.setVisibility(View.GONE);
//
//        if (itemList.get(position).HasDeliveryRequest) {
//            holder.imgHasDeliveryRequest.setVisibility(View.VISIBLE);
//            holder.imgHasDeliveryRequest.setImageResource(R.drawable.greenstar);
//            holder.imgHasComplaint.refreshDrawableState();
//        } else
//            holder.imgHasDeliveryRequest.setVisibility(View.GONE);
//
//        if (item.Latitude.length() > 3 && item.Longitude.length() > 3) {
//            holder.imgHasLocation.setVisibility(View.VISIBLE);
//            holder.imgHasLocation.setImageResource(R.drawable.haslocation);
//        } else
//            holder.imgHasLocation.setVisibility(View.INVISIBLE);
//
//        if (typeID == 1) {
//            holder.txtType.setText("Delivery");
//        } else if (typeID == 2) {
//            holder.txtType.setText("PickUp");
//        }
        return convertView;
    }

    class ViewHolder {
        TextView txtReferenceNo, txtRequiredTime, txtRequiredDate, txtCloseTime,
                txtWeight, txtPieces, lblClientId;
        ImageView acceptJob, imgHasLocation, imgHasComplaint, imgHasDeliveryRequest, pickup;
        //TextView panel;

        public ViewHolder(View view) {
            txtReferenceNo = (TextView) view.findViewById(R.id.txtReferenceNo);
            txtRequiredTime = (TextView) view.findViewById(R.id.txtClient);
            txtRequiredDate = (TextView) view.findViewById(R.id.txtClientId);
            // txtCloseTime = (TextView) view.findViewById(R.id.txtCloseTime);
            txtWeight = (TextView) view.findViewById(R.id.txtWeight);
            txtPieces = (TextView) view.findViewById(R.id.txtPiecesCount);
            //panel = (TextView) view.findViewById(R.id.panel);
            acceptJob = (ImageView) view.findViewById(R.id.acceptjob);
            pickup = (ImageView) view.findViewById(R.id.pickup);
            lblClientId = (TextView) view.findViewById(R.id.lblClientId);

            view.setTag(this);
        }
    }

    //update the status
    private class AcknowledgeBookingData extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog pd;
        private ImageView imageView;
        int position;

        public AcknowledgeBookingData(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(context);
            pd.setMessage("please wait...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            position = Integer.parseInt(params[1]);
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "UpdatePickupAcknowledge");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return String.valueOf(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null) {
                if (finalJson.equals("201 - Created Successfully")) {
                    pd.dismiss();
                    imageView.setImageResource(R.drawable.accpet_job);
                    itemList.get(position).Status = 7;
                    itemList.set(position, itemList.get(position));
                }
            } else {
                //
                GlobalVar.GV().ShowSnackbar(((Activity) context).getWindow().getDecorView().getRootView(), ((Activity) context).getString(R.string.wentwrong), GlobalVar.AlertType.Error);
            }

            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // Bitmap bmp = ThumbnailUtils.extractThumbnail(
            //         BitmapFactory.decodeFile(params[0]), 64, 64);
            Bitmap bmp = null;
            if (params[1].equals("BookingList")) {
                if (Integer.parseInt(params[0]) == 7)
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.accpet_job);
                else
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.acceptjob);
            } else
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.jobnotsync);
            // final Bitmap bmp = BitmapFactory.decodeFile(params[0]);
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        // Drawable placeholder =
                        // imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
                        // imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }

    private void StartAsyncTaskInParallel(ImageDownloaderTask asynthread, String image, String class_) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image, class_);
        else
            asynthread.execute(image, class_);

    }


}