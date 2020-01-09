package com.naqelexpress.naqelpointer.PhoneState;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.itextpdf.text.Utilities;

/**
 * Created by Hasna on 3/10/19.
 */

class EndCallListener extends PhoneStateListener {


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (TelephonyManager.CALL_STATE_RINGING == state) {
            Log.i("", "RINGING, number: " + incomingNumber);
        }
        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
            Log.i("", "OFFHOOK");
        }
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            Log.i("", "IDLE");


        }
    }
}