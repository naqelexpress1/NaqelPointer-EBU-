package com.naqelexpress.naqelpointer.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.joda.time.DateTime;

/**
 * Created by Hasna on 3/7/19.
 */

public class CallCaptureforAllDevice extends Service {

    static final Uri CallLog_URI = CallLog.Calls.CONTENT_URI;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Cursor managedCursor = getContentResolver().query(CallLog_URI, null,
                null, null, CallLog.Calls.DATE + " DESC limit 1;"); //android.provider.CallLog.Calls.DATE + " DESC limit 1;"


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
        DateTime endtime = DateTime.now();
        String dir = null;
        int dircode = Integer.parseInt(callType);
        switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                endtime = DateTime.now().minusSeconds(10);
                dir = "OUTGOING";
                break;

            case CallLog.Calls.INCOMING_TYPE:
                endtime = DateTime.now().plusSeconds(Integer.parseInt(callDuration));
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
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            couriernumber = tm.getLine1Number();
            if (couriernumber == null || couriernumber.length() == 0) {
                couriernumber = tm.getSimSerialNumber();
                if (couriernumber == null || couriernumber.length() == 0) {
                    couriernumber = tm.getDeviceId();
                }
            }
        } catch (Exception e) {

        }
//                    sb.append("C No - " + couriernumber);
//                    sb.append("type " + dir);
//                    sb.appendd("Call No - " + phNumber);
//                    Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

        String CustNo =
                (phNumber.length() >= 9) ? phNumber.substring(phNumber.length() - 9) : "Not";
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        String WayBillNo = dbConnections.getWaybillByMobileNo(CustNo, getApplicationContext());
        dbConnections.InsertCallLog(dir, phNumber, callDuration, WayBillNo, callDayTime,
                callDayTime.toString(), endtime.toString(), couriernumber, getApplicationContext());

        dbConnections.close();

        CallRecordAbove26.start(getApplicationContext());


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
