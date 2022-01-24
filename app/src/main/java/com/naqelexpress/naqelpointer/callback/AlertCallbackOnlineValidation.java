package com.naqelexpress.naqelpointer.callback;

import android.app.Activity;

import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;

public interface AlertCallbackOnlineValidation {

    void returnOk(int ok, Activity activity, OnLineValidation onLineValidation);


}
