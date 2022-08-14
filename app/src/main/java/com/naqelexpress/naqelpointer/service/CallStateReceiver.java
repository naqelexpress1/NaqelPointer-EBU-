package com.naqelexpress.naqelpointer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.naqelexpress.naqelpointer.R;

/**
 * Created by Hasna on 3/12/19.
 */

public class CallStateReceiver extends Service {
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    NotificationChannel notificationChannel;
    String NOTIFICATION_CHANNEL_ID = "17";

    private BroadcastReceiver mCallBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String PhoneNumber = "UNKNOWN";
            Log.d("RECEIVER :  ", "IS UP AGAIN....");

            try {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (state == null) {
                    PhoneNumber = "UNKNOWN";
                } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    PhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.d("RECEIVER : ", "Incoming number : " + PhoneNumber);
                }
                if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                    PhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    Log.d("RECEIVER : ", "Outgoing number : " + PhoneNumber);
                }
                if (!PhoneNumber.contentEquals("UNKNOWN")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        context.startForegroundService(new Intent(context, CatchNumbers.class));
                    } else {
//                        context.startService(new Intent(context, CatchNumbers.class));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("RECEIVER : ", "Exception is : ", e);
            }
        }
    };

    public CallStateReceiver() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("WatchMan : ", "\nOnCreate...");

        IntentFilter CallFilter = new IntentFilter();
        CallFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        CallFilter.addAction("android.intent.action.PHONE_STATE");
        this.registerReceiver(mCallBroadcastReceiver, CallFilter);

        Log.d("WatchMan : ", "\nmCallBroadcastReceiver Created....");

        mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this, null);
        mBuilder.setContentTitle("Insta Promo")
                .setContentText("Checking New Numbers")
                .setTicker("Checking New Numbers")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(Notification.PRIORITY_LOW)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotifyManager.createNotificationChannel(notificationChannel);

            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            startForeground(17, mBuilder.build());
        } else {
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            //startForeground(17, mBuilder.build());
            mNotifyManager.notify(17, mBuilder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.d("WatchMan : ", "\nmCallBroadcastReceiver Listening....");

//        return super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(mCallBroadcastReceiver);
        Log.d("WatchMan : ", "\nDestroyed....");
        Log.d("WatchMan : ", "\nWill be created again....");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
