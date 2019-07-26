package com.naqelexpress.naqelpointer;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.pusher.chatkit.CurrentUser;
import com.pusher.chatkit.rooms.Room;

/**
 * Created by Hasna on 11/26/18.
 */

public class ApplicationController extends Application {
    private static ApplicationController thisInstance;
    private RequestQueue mRequestQueue;
    static CurrentUser currentUser;
    static Room room;

    @Override
    public void onCreate() {
        super.onCreate();
        thisInstance = this;

    }

    public static synchronized ApplicationController getInstance() {
        return thisInstance;
    }

    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

}