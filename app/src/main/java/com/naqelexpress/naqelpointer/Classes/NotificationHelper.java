
package com.naqelexpress.naqelpointer.Classes;

import android.app.Notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;

    public NotificationHelper(Context ctx) {
        super(ctx);
    }


    public Notification.Builder getNotification1(String title, String body) {
        return new Notification.Builder(getApplicationContext())
                 .setContentTitle(title)
                 .setContentText(body)
                 .setSmallIcon(getSmallIcon())
                 .setAutoCancel(false);
    }



    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }


    private int getSmallIcon() {
        return android.R.drawable.stat_notify_chat;
    }


    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}
