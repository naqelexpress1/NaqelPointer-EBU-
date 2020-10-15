package com.naqelexpress.naqelpointer.Receiver;

/**
 * Created by Hasna on 9/26/18.
 */

import android.Manifest;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Activity.History.CallRecordTrigger;
import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.joda.time.DateTime;

import static android.content.Context.TELEPHONY_SERVICE;

public class MyReceiver extends BroadcastReceiver {

    boolean isbooton = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction()) ||
                intent.getAction().equalsIgnoreCase("android.intent.action.LOCKED_BOOT_COMPLETED")) {

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(new Intent(context, PhoneState.class));
//            } else {
//                context.startService(new Intent(context, PhoneState.class));
//            }

            //startAllService(context);

            DBConnections dbConnections = new DBConnections(context, null);

            dbConnections.InsertDeviceActivity(context, 2);
            dbConnections.close();


            dbConnections.updateMyRouteShipmentsIsRestarted(context);

            context.startService(
                    new Intent(context,
                            com.naqelexpress.naqelpointer.service.DeviceActivity.class));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!CallRecordTrigger.isScheduled(context)) {
                    JobInfo.Builder builder = new JobInfo.Builder(4002,
                            new ComponentName("com.naqelexpress.naqelpointer", CallRecordTrigger.class.getName()));
                    builder.addTriggerContentUri(new JobInfo.TriggerContentUri(
                            CallLog.Calls.CONTENT_URI_WITH_VOICEMAIL,
                            JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
                    builder.addTriggerContentUri(new JobInfo.TriggerContentUri(CallLog.Calls.CONTENT_URI, 0));
                    JobInfo JOB_INFO = builder.build();
                    JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
                    jobScheduler.schedule(builder.build());
                }
                // CallRecord.scheduleJob(getApplicationContext());
            }
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

            //Toast.makeText(context.getApplicationContext(), "test by is " + number, Toast.LENGTH_LONG).show();

        } else if (intent.getAction().equalsIgnoreCase("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

        } else if (Intent.ACTION_SHUTDOWN.equalsIgnoreCase(intent.getAction()) || Intent.ACTION_AIRPLANE_MODE_CHANGED.equalsIgnoreCase(intent.getAction()) ||
                Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED.equalsIgnoreCase(intent.getAction())) {
            DBConnections dbConnections = new DBConnections(context, null);
            dbConnections.updateMyRouteShipmentsIsRestarted(context);
            if (Intent.ACTION_SHUTDOWN.equals(intent.getAction()))
                dbConnections.InsertDeviceActivity(context, 1);
            else if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction().intern()))
                dbConnections.InsertDeviceActivity(context, 3);
            else if (Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED.equals(intent.getAction()))
                dbConnections.InsertDeviceActivity(context, 5);
            else if (ConnectivityManager.CONNECTIVITY_ACTION
                    .equalsIgnoreCase(intent.getAction())) {
                // some operation
            }
            Toast.makeText(context.getApplicationContext(), "test by is " + intent.getAction(), Toast.LENGTH_LONG).show();

            dbConnections.close();
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


        String couriernumber = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            couriernumber = tm.getLine1Number();
            if (couriernumber == null || couriernumber.length() == 0) {
                couriernumber = tm.getSimSerialNumber();
                if (couriernumber == null || couriernumber.length() == 0) {
                    couriernumber = tm.getDeviceId();
                }
            }
        } catch (Exception e) {

        }

        String CustNo =
                (phNumber.length() >= 9) ? phNumber.substring(phNumber.length() - 9) : "Not";

        DBConnections dbConnections = new DBConnections(context, null);
        String WayBillNo = dbConnections.getWaybillByMobileNo(CustNo, context);
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