package com.naqelexpress.naqelpointer.Activity.GoogleMap;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionService extends IntentService {

    private static final String TAG = GeofenceTransitionService.class.getSimpleName();

    public static final int GEOFENCE_NOTIFICATION_ID = 0;

    public GeofenceTransitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type is of interest
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(geoFenceTransition, triggeringGeofences );

            // Send notification details as a String
            sendNotification( geofenceTransitionDetails );
        }
    }


    private String getGeofenceTransitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "Entering ";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Exiting ";
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }

    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
//        Intent notificationIntent = MainActivity.makeNotificationIntent(
//                getApplicationContext(), msg
//        );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));

    }

    // Create notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_action_location)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("Geofence Notification!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }


    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}

//
//import android.app.IntentService;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.TaskStackBuilder;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.support.v4.app.NotificationCompat;
//import android.app.AlertDialog;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.google.android.gms.location.Geofence;
//import com.google.android.gms.location.GeofenceStatusCodes;
//import com.google.android.gms.location.GeofencingEvent;
//import com.naqelexpress.routeoptimization.Activity.Login.ForgotPasswordActivity;
//import com.naqelexpress.routeoptimization.Activity.MainPage.MainPageActivity;
//import com.naqelexpress.routeoptimization.Activity.PickUp.PickUpActivity;
//import com.naqelexpress.routeoptimization.Classes.GlobalVar;
//import com.naqelexpress.routeoptimization.OnGeofencyEnter;
//import com.naqelexpress.routeoptimization.OnSpinerItemClick;
//import com.naqelexpress.routeoptimization.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by delaroy on 4/18/17.
// */
//public class GeofenceTransitionService extends IntentService
//{
//    private static final String TAG = GeofenceTransitionService.class.getSimpleName();
//    public static final int GEOFENCE_NOTIFICATION_ID = 0;
//
//    public GeofenceTransitionService()
//    {
//        super(TAG);
//    }
//
////    OnGeofencyEnter onGeofencyEnter;
////    public GeofenceTransitionService(OnGeofencyEnter onGeofencyEnter1)
////    {
////        super(TAG);
////        this.onGeofencyEnter = onGeofencyEnter1;
////    }
//
////    @Override
////    protected void onHandleIntent(Intent intent)
////    {
////        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
////        //geofencingEvent.getTriggeringLocation()
////        if ( geofencingEvent.hasError() )
////        {
////            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
////            Log.e( TAG, errorMsg );
////            return;
////        }
////
////        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
////        // Check if the transition type is of interest
////        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
//////            || geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
////        {
////            // Get the geofence that were triggered
////            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
////            try
////            {
////                GlobalVar.GV().MakeSound(GlobalVar.GV().context,R.raw.reacheddestination);
////                onGeofencyEnter.onClick("test",34);
//////                onSpinerItemClick.onClick("",geofencingEvent.getTriggeringLocation());
////            }
////            catch (Exception ex)
////            {
////                ex.printStackTrace();
////            }
////        }
////    }
//
//
//    @Override
//    protected void onHandleIntent(Intent intent)
//    {
//        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//        //geofencingEvent.getTriggeringLocation()
//        // Handling errors
//        if ( geofencingEvent.hasError() )
//        {
//            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
//            Log.e( TAG, errorMsg );
//            return;
//        }
//
//        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
//        // Check if the transition type is of interest
//        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
////            || geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
//        {
//            // Get the geofence that were triggered
//            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
////            String geofenceTransitionDetails = getGeofenceTransitionDetails(geoFenceTransition, triggeringGeofences );
//            // Send notification details as a String
////            sendNotification( geofenceTransitionDetails );
//            try
//            {
////                Intent shipmenstMap = new Intent(getApplicationContext(), ShipmentsMapActivity.class);
////                shipmenstMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(shipmenstMap);
//
//                GlobalVar.GV().MakeSound(GlobalVar.GV().context,R.raw.reacheddestination);
////                ShowAlert(intent);
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
//        }
//    }
//
////    private String getGeofenceTransitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences)
////    {
////        // get the ID of each geofence triggered
////        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
////        for ( Geofence geofence : triggeringGeofences )
////        {
////            triggeringGeofencesList.add( geofence.getRequestId() );
////        }
////
////        String status = null;
////        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
////            status = "Entering ";
////        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
////            status = "Exiting ";
////        return status + TextUtils.join( ", ", triggeringGeofencesList);
////    }
//
////    private void sendNotification( String msg )
////    {
////        Log.i(TAG, "sendNotification: " + msg );
////
////        // Intent to start the main Activity
////        Intent notificationIntent = MainActivity.makeNotificationIntent(
////                getApplicationContext(), msg
////        );
////
////        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
////        stackBuilder.addParentStack(MainActivity.class);
////        stackBuilder.addNextIntent(notificationIntent);
////        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
////
////        // Creating and sending Notification
////        NotificationManager notificatioMng =
////                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
////        notificatioMng.notify(
////                GEOFENCE_NOTIFICATION_ID,
////                createNotification(msg, notificationPendingIntent));
////
////        try
////        {
//////            Intent shipmenstMap = new Intent(getApplicationContext(), ShipmentsMapActivity.class);
//////            startActivity(shipmenstMap);
//////            ShowAlert();
////        }
////        catch (Exception ex)
////        {
////            ex.printStackTrace();
////        }
////    }
//
////    private final String NOTIFICATION_MSG = "NOTIFICATION MSG";
////    public Intent makeNotificationIntent(Context context, String msg)
////    {
////        Intent shipmenstMap = new Intent(getApplicationContext(), ShipmentsMapActivity.class);
////            startActivity(shipmenstMap);
////
////        return shipmenstMap;
//////        Intent intent = new Intent( context, MainActivity.class );
//////        intent.putExtra( NOTIFICATION_MSG, msg );
//////
//////        return intent;
////    }
//
////    private void ShowAlert(Intent intent)
////    {
////        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalVar.GV().context);
////        builder.setTitle("Exit PickUp")
////                .setMessage("Are you sure you want to exit without saving?")
////                .setPositiveButton("OK",new DialogInterface.OnClickListener()
////                {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface,int which)
////                    {
////
//////                        PickUpActivity.super.onBackPressed();
////                    }
////                }).setNegativeButton("Cancel",null).setCancelable(false);
////        AlertDialog alertDialog = builder.create();
////        alertDialog.show();
////    }
//
//    // Create notification
////    private Notification createNotification(String msg, PendingIntent notificationPendingIntent)
////    {
////        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
////        notificationBuilder
////                .setSmallIcon(R.drawable.ic_action_location)
////                .setColor(Color.RED)
////                .setContentTitle(msg)
////                .setContentText("Reached the place!")
////                .setContentIntent(notificationPendingIntent)
////                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
////                .setAutoCancel(true);
////
////        return notificationBuilder.build();
////    }
//
//    private static String getErrorString(int errorCode)
//    {
//        switch (errorCode)
//        {
//            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
//                return "GeoFence not available";
//
//            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
//                return "Too many GeoFences";
//
//            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
//                return "Too many pending intents";
//
//            default:
//                return "Unknown error.";
//        }
//    }
//}