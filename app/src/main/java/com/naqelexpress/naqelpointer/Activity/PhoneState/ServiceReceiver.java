package com.naqelexpress.naqelpointer.Activity.PhoneState;

/**
 * Created by Hasna on 3/11/19.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ServiceReceiver extends BroadcastReceiver {


    public void onReceive(final Context context, Intent intent) {

//        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
//        telephony = (TelephonyManager) context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        TelephonyManager mtelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mtelephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        // CALL_STATE_RINGING
                        Log.d("MyLittleDebugger", "I'm in " + state + " and the number is " + incomingNumber);
                        Toast.makeText(context, incomingNumber,
                                Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "CALL_STATE_RINGING",
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);


    }

    public void onDestroy() {

    }

}