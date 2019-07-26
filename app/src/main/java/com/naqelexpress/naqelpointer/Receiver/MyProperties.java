package com.naqelexpress.naqelpointer.Receiver;

/**
 * Created by Hasna on 9/29/18.
 */

public class MyProperties {
    private static MyProperties mInstance= null;

    public boolean NewIncomingCall = false;
    public int CallId = 0;
    public String PhoneNumber = "";


    protected MyProperties(){}

    public static synchronized MyProperties getInstance() {
        if(null == mInstance){
            mInstance = new MyProperties();
        }
        return mInstance;
    }
}