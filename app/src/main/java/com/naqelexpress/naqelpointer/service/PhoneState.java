package com.naqelexpress.naqelpointer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.naqelexpress.naqelpointer.Activity.PhoneState.MyPhoneStateListener;
import com.naqelexpress.naqelpointer.R;

public class PhoneState extends Service {

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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(112, new Notification());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.PhoneState";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Phone State")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(112, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyPhoneStateListener phoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephonymanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.cancel(112);


        super.onDestroy();
    }


}
