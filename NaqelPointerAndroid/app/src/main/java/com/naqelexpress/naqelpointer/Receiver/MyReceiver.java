package com.naqelexpress.naqelpointer.Receiver;

/**
 * Created by Hasna on 9/26/18.
 */

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.joda.time.DateTime;

import static android.content.Context.TELEPHONY_SERVICE;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
            // Toast.makeText(context,"Received",Toast.LENGTH_LONG).show();
            startAllService(context);
        } else if (intent.getAction().equalsIgnoreCase("android.intent.action.PHONE_STATE")) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);

        } else if (intent.getAction().equalsIgnoreCase("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

        }
        //else if()
    }

    private void startAllService(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, com.naqelexpress.naqelpointer.service.AtOrigin.class));
        } else {
            context.startService(new Intent(context, com.naqelexpress.naqelpointer.service.AtOrigin.class));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, com.naqelexpress.naqelpointer.service.CheckPoint.class));
        } else {
            context.startService(new Intent(context, com.naqelexpress.naqelpointer.service.CheckPoint.class));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, com.naqelexpress.naqelpointer.service.NotDelivery.class));
        } else {
            context.startService(new Intent(context, com.naqelexpress.naqelpointer.service.NotDelivery.class));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, com.naqelexpress.naqelpointer.service.OnDelivery.class));
        } else {
            context.startService(new Intent(context, com.naqelexpress.naqelpointer.service.OnDelivery.class));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, com.naqelexpress.naqelpointer.service.OnLoading.class));
        } else {
            context.startService(new Intent(context, com.naqelexpress.naqelpointer.service.OnLoading.class));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, com.naqelexpress.naqelpointer.service.PickUp.class));
        } else {
            context.startService(new Intent(context, com.naqelexpress.naqelpointer.service.PickUp.class));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, com.naqelexpress.naqelpointer.service.signature.class));
        } else {
            context.startService(new Intent(context, com.naqelexpress.naqelpointer.service.signature.class));
        }


    }


    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static DateTime callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    public void onCallStateChanged(final Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new DateTime();
                savedNumber = number;
                //onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new DateTime();
                    //  onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // new DownloadJSON().execute();
                        try {

                            getCallDetails(context, callStartTime, new DateTime());
                        } catch (Exception e) {

//                            handler.postDelayed(this, 20000);
                            Log.e("Dashboard thread", e.toString());
                        }

                    }
                }, 5000);

                break;
        }
        lastState = state;
    }

//    protected void onIncomingCallStarted(Context context, String number, Date start) {
//        //     Log.d("onIncomingCallStarted", number);
//        Toast.makeText(context, " onIncomingCallStarted  " + number, Toast.LENGTH_LONG).show();
//    }
//
//    protected void onOutgoingCallStarted(Context context, String number, Date start) {
////        Log.d("onOutgoingCallStarted", number);
//        Toast.makeText(context, " onOutgoingCallStarted  " + number, Toast.LENGTH_LONG).show();
//    }
//
//    protected void onIncomingCallEnded(Context context, String number, Date start, Date end) {
//        // Log.d("onIncomingCallEnded", number);
//        Toast.makeText(context, " onIncomingCallEnded  " + number + " - " + start.toString() + " " + end.toString(), Toast.LENGTH_LONG).show();
//        MyProperties.getInstance().NewIncomingCall = true;
//        MyProperties.getInstance().PhoneNumber = number + " ";
//    }
//
//    protected void onOutgoingCallEnded(Context context, String number, Date start, Date end) {
//        Toast.makeText(context, " onOutgoingCallEnded  " + number + " - " + start.toString() + " " + end.toString(), Toast.LENGTH_LONG).show();
//        //getCallDetails(context);
//        //        Log.d("onOutgoingCallEnded", number);
//    }
//
//    protected void onMissedCall(Context context, String number, Date start) {
//        Toast.makeText(context, " onMissedCall  " + number, Toast.LENGTH_LONG).show();
//        MyProperties.getInstance().NewIncomingCall = true;
//        MyProperties.getInstance().PhoneNumber = number + " ";
//
//    }

    private void getCallDetails(Context context, final DateTime CallStarttime, final DateTime Endtime) {
        StringBuffer sb = new StringBuffer();
        Uri contacts = CallLog.Calls.CONTENT_URI;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Cursor managedCursor = context.getContentResolver().query(contacts, null,
                null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;"); //android.provider.CallLog.Calls.DATE + " DESC limit 1;"


        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        managedCursor.moveToFirst();

        // while (managedCursor.moveToNext()) {
        String phNumber = managedCursor.getString(number);
        String callType = managedCursor.getString(type);
        String callDate = managedCursor.getString(date);
        String callDayTime = new DateTime(Long.valueOf(callDate)).toString();
        String callDuration = managedCursor.getString(duration);

        String dir = null;
        int dircode = Integer.parseInt(callType);
        switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = "OUTGOING";
                break;

            case CallLog.Calls.INCOMING_TYPE:
                dir = "INCOMING";
                break;

            case CallLog.Calls.MISSED_TYPE:
                dir = "MISSED";
                break;
            case CallLog.Calls.REJECTED_TYPE:
                dir = "REJECT";
                break;
            case CallLog.Calls.BLOCKED_TYPE:
                dir = "BLOCK";
                break;

        }


        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String couriernumber = tm.getLine1Number();

        DBConnections dbConnections = new DBConnections(context, null);
        String WayBillNo = dbConnections.getWaybillByMobileNo(phNumber.substring(phNumber.length() - 9), context);
        dbConnections.InsertCallLog(dir, phNumber, callDuration, WayBillNo, callDayTime, CallStarttime.toString(), Endtime.toString(), couriernumber, context);

        dbConnections.close();
        System.out.println(sb);

        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.CallRecord.class, context)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, com.naqelexpress.naqelpointer.service.CallRecord.class));
            } else {
                context.startService(new Intent(context, com.naqelexpress.naqelpointer.service.CallRecord.class));
            }

//            context.startService(
//                    new Intent(context,
//                            com.naqelexpress.naqelpointer.service.CallRecord.class));
        }

        // break;
//            }
        //     }
        managedCursor.close();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}