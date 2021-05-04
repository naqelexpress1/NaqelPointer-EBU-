package com.naqelexpress.naqelpointer.callback;

import android.app.Activity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public interface AlertCallback {

    void returnOk(int ok , Activity activity);

    void returnCancel(int cancel , SweetAlertDialog alertDialog);
}
