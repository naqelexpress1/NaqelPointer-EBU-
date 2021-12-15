package com.naqelexpress.naqelpointer;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.pusher.chatkit.CurrentUser;
import com.pusher.chatkit.rooms.Room;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Hasna on 11/26/18.
 */

public class ApplicationController extends Application {
    private static ApplicationController thisInstance;
    private RequestQueue mRequestQueue;
    static CurrentUser currentUser;
    static Room room;
    private static final String PROPERTIES_FILE = "app.properties";
    private static Properties properties = new Properties();

    @Override
    public void onCreate() {
        super.onCreate();
        thisInstance = this;

        try {
            loadProperties(getBaseContext());
        } catch (Exception e) {

        }
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

    public static void loadProperties(Context context) throws Exception {
        InputStream input = context.getResources().openRawResource(R.raw.app);
        if (input == null) {
            throw new Exception(PROPERTIES_FILE + " not found in the CLASSPATH.");
        }
        properties.load(input);
//        Log.d(TAG, properties.toString());
        input.close();
//        TAG = properties.getProperty("APP_PREFIX") + properties.getProperty("TAG");
    }

    public static String getPOSAppPackageName() {
        return properties.getProperty("POS_APP_PACKAGE_NAME");
    }

    public static int getPOS_FETCH_REGISTRATION_DETAILS() {
        return Integer.parseInt(properties.getProperty("POS_FETCH_REGISTRATION_DETAILS"));
    }

    public static String getPOS_TerminalID() {
        return properties.getProperty("POS_TerminalID");
    }

    public static int getPOS_SOFTPOS_REGISTRATION_CODE() {
        return Integer.parseInt(properties.getProperty("POS_SOFTPOS_REGISTRATION_CODE"));
    }

    public static int getPOS_PAYMENT_REQUEST_CODE() {
        return Integer.parseInt(properties.getProperty("POS_PAYMENT_REQUEST_CODE"));
    }
}