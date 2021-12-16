package com.naqelexpress.naqelpointer.utils;

import android.content.Context;
import android.content.Intent;

public class RestartService {


    public static void RestartOnDeliveryService(Context context) {
        context.stopService(
                new Intent(context,
                        com.naqelexpress.naqelpointer.service.PartialDelivery.class));
        context.startService(
                new Intent(context,
                        com.naqelexpress.naqelpointer.service.PartialDelivery.class));
    }
}


