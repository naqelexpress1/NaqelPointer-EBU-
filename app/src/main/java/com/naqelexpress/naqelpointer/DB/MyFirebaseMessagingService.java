package com.naqelexpress.naqelpointer.DB;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String NOTIFICATION_ID_EXTRA = "notificationId";
    private static final String IMAGE_URL_EXTRA = "imageUrl";
    private static final String ADMIN_CHANNEL_ID = "admin_channel";
    private NotificationManager notificationManager;
    private String title = "";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {


            Intent notificationIntent = new Intent(this, com.naqelexpress.naqelpointer.Activity.MyrouteCBU.MyRouteActivity.class);

            //        if(StartActivity.isAppRunning){
//            //Some action
//        }else{
//            //Show notification as usual
//        }

            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0 /* Request code */, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            //You should use an actual ID instead
            int notificationId = new Random().nextInt(60000);

            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Bitmap bitmap = getBitmapfromUrl(remoteMessage.getData().get("image"));
            title = remoteMessage.getData().get("title");
            String type = remoteMessage.getData().get("type");
            int image = 0;
            String waybillno = "0";

            try {

                if (type.equals("Booking")) {
                    image = R.drawable.booking;
                } else if (type.equals("Complaint")) {
                    image = R.drawable.complaint;
                    updateComplaint(remoteMessage.getData().get("message"));
                } else if (type.equals("Location")) {
                    image = R.drawable.userlocation;
                    updateLocation(remoteMessage.getData().get("message"), remoteMessage.getData().get("Lat"), remoteMessage.getData().get("Long"));
                } else if (type.equals("Request")) {
                    image = R.drawable.request;
                    updateRequest(remoteMessage.getData().get("message"));
                } else if (type.equals("Reschedule")) {
                    image = R.drawable.request;
                    updateBooking(remoteMessage.getData().get("message"));
                } else if (type.equals("IsPaid")) {
                    image = R.drawable.pendingcod;
                    updatePaid(remoteMessage.getData().get("message"));
                } else
                    image = R.mipmap.ic_launcher;


            } catch (Exception e) {
                System.out.println(e);
            }

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels();
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                            .setLargeIcon(bitmap)
                            .setSmallIcon(image)
                            .setContentTitle(remoteMessage.getData().get("title"))
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .setSummaryText(remoteMessage.getData().get("message"))
                                    .bigPicture(bitmap))/*Notification with Image*/
                            .setContentText(remoteMessage.getData().get("message"))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            // .setDefaults(Notification.DEFAULT_SOUND) //.setSound(defaultSoundUri)
//                        .addAction(R.drawable.ic_favorite_true,
//                                getString(R.string.notification_add_to_cart_button),likePendingIntent)
                            .setContentIntent(pendingIntent);

            notificationManager.notify(notificationId, notificationBuilder.build());
        } catch (Exception e) {
            Log.d("Notify Error", e.toString());
            System.out.println(e);
        }
    }


    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        try {
            CharSequence adminChannelName = "";
            String adminChannelDescription = "";

            NotificationChannel adminChannel;
            adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, title, NotificationManager.IMPORTANCE_LOW);
            adminChannel.setDescription(adminChannelDescription);
            adminChannel.enableLights(true);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(adminChannel);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void updateLocation(String Waybillno, String Lat, String Lng) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = " + Waybillno, getApplicationContext());
        if (result.getCount() > 0) {
            dbConnections.updateMyRouteShipments(getApplicationContext(), Waybillno, "Location", Lat, Lng);
        }
        result.close();
        dbConnections.close();

    }

    private void updateComplaint(String Waybillno) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = " + Waybillno, getApplicationContext());
        if (result.getCount() > 0) {
            updateComplaint();
            dbConnections.updateMyRouteShipments(getApplicationContext(), Waybillno, "Complaint", "", "");
        }
        result.close();
        dbConnections.close();
    }

    private void updatePaid(String Waybillno) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = " + Waybillno,
                getApplicationContext());
        if (result.getCount() > 0) {
            updateComplaint();
            dbConnections.updateMyRouteShipmentsIsPaid(getApplicationContext(), Waybillno);
        }
        result.close();
        dbConnections.close();
    }

    private void updateRequest(String Waybillno) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = " + Waybillno, getApplicationContext());
        if (result.getCount() > 0) {
            dbConnections.updateMyRouteShipments(getApplicationContext(), Waybillno, "Request", "", "");
            updateRequest();
        }
        result.close();
        dbConnections.close();
    }

    private void updateBooking(String Waybillno) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = " + Waybillno, getApplicationContext());
        if (result.getCount() > 0) {
            dbConnections.updateMyRouteShipments(getApplicationContext(), Waybillno, "Request", "", "");
            updateRequest();
        }
        result.close();
        dbConnections.close();
    }

    private void updateComplaint() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("SELECT *  FROM  Complaint WHERE Date = '" + GlobalVar.getDate() + "' and Attempted = "
                + GlobalVar.getlastlogin(getApplicationContext()), getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            int totalComplaint = result.getInt(result.getColumnIndex("TotalComp"));
            int id = result.getInt(result.getColumnIndex("ID"));
            dbConnections.updateComplaintsReq(getApplicationContext(), totalComplaint + 1, id, "Complaint");

        }
    }

    private void updateRequest() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("SELECT *  FROM  Complaint WHERE Date = '" + GlobalVar.getDate() + "' and Attempted = "
                + GlobalVar.getlastlogin(getApplicationContext()), getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            int totalReq = result.getInt(result.getColumnIndex("Request"));
            int id = result.getInt(result.getColumnIndex("ID"));
            dbConnections.updateComplaintsReq(getApplicationContext(), totalReq + 1, id, "Request");

        }
    }
}