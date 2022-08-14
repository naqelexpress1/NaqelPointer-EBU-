package com.naqelexpress.naqelpointer.DB;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.naqelexpress.naqelpointer.Activity.Login.SplashScreenActivity;
import com.naqelexpress.naqelpointer.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService_old extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";

    String imgUrl = "";
    String appImgUrl = "";
    String notifTitle = "";
    String notifText = "";
    RemoteViews contentViewBig, contentViewSmall;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (null != remoteMessage.getNotification().getClickAction()) {
            startActivity(remoteMessage.getNotification().getClickAction(), null, this);
        }

        if (remoteMessage.getData().size() > 0) {
            Bundle extras = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                extras.putString(entry.getKey(), entry.getValue());
                //Check for your custom Key Value Pairs
                if (entry.getKey().equals("img_url")) {
                    imgUrl = entry.getValue();
                }

                if (entry.getKey().equals("notif_title")) {
                    notifTitle = entry.getValue();
                }
                if (entry.getKey().equals("notif_text")) {
                    notifText = entry.getValue();
                }
            }

            int notificationId = new Random().nextInt(60000);
            CreateNaqelNotification(notifTitle, notifText, imgUrl, this, notificationId, "NaqelPointer");
        } else {
            sendNotification(remoteMessage);
        }


    }

    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void startActivity(String className, Bundle extras, Context context) {
        Class cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            //means you made a wrong input in firebase console
        }
        Intent intent = new Intent(context, cls);
        if (null != extras) {
            intent.putExtras(extras);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void CreateNaqelNotification(String title, String body, String image_url, Context context, int notificationsId, String single_id) {
        Intent notificationIntent;

        long when = System.currentTimeMillis();
        int id = (int) System.currentTimeMillis();

        Bitmap bitmap = getBitmapFromURL(image_url);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null))
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationsId, notification);
    }

    public static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

