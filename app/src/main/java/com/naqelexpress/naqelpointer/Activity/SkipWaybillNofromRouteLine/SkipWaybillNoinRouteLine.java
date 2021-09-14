package com.naqelexpress.naqelpointer.Activity.SkipWaybillNofromRouteLine;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.NotificationModels;
import com.naqelexpress.naqelpointer.Models.Request.NotificationRequest;
import com.naqelexpress.naqelpointer.Models.SkipRouteLineSeqWaybillnoReasonModels;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.NotificationApi;
import com.naqelexpress.naqelpointer.utils.utilities;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;


public class SkipWaybillNoinRouteLine
        extends AppCompatActivity implements AlertCallback, AdapterView.OnItemSelectedListener {

    private AlertCallback alertCallback;
    Observable<Boolean> observable;
    EditText empID_txt, deliverysheetID_txt, waybillNo_txt, lat_txt, longi_txt;
    Spinner skipreason;
    List<SkipRouteLineSeqWaybillnoReasonModels> skipReasonList = new ArrayList<>();
    int isSelectedReasonID = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skipwabillinrouteline);

        //initialisation layout
//        init();
        empID_txt = (EditText) findViewById(R.id.empid);
        deliverysheetID_txt = (EditText) findViewById(R.id.deliverysheetID);
        skipreason = (Spinner) findViewById(R.id.reason);
        skipreason.setOnItemSelectedListener(this);

        waybillNo_txt = (EditText) findViewById(R.id.waybillno);
        lat_txt = (EditText) findViewById(R.id.latitude);
        longi_txt = (EditText) findViewById(R.id.longitude);


        FetchReason();

    }

    private void FetchReason() {
        GlobalVar.GV().alertMsgAll("Info", "Please wait to fetch Skip Reasons.", SkipWaybillNoinRouteLine.this,
                Enum.PROGRESS_TYPE , "SkipWaybillNoinRouteLine");

        NotificationApi.skipRouteLineReason(new Callback<List<SkipRouteLineSeqWaybillnoReasonModels>>() {
            @Override
            public void returnResult(List<SkipRouteLineSeqWaybillnoReasonModels> result) {
                System.out.println();
                skipReasonList.addAll(result);
                initSpinner();
            }

            @Override
            public void returnError(String message) {
                //mView.showError(message);
                System.out.println(message);
            }
        });
    }

    public void submit(View view) {

        switch (view.getId()) {
            case R.id.submit:
                submittoServer();
                break;

        }


    }

    private void submittoServer() {
        if (isValidForm()) {
            GlobalVar.GV().alertMsgAll("Info", "Please wait...", SkipWaybillNoinRouteLine.this,
                    Enum.PROGRESS_TYPE , "SkipWaybillNoinRouteLine");

            NotificationApi.skipRouteLineSeq(new Callback<NotificationModels>() {
                @Override
                public void returnResult(NotificationModels result) {
                    if (result.getIsSync()) {
                        GlobalVar.GV().alertMsgAll("Info", "Your request has been completed.", SkipWaybillNoinRouteLine.this,
                                Enum.SUCCESS_TYPE,"SkipWaybillNoinRouteLine");
                        //finish();
                    } else
                        GlobalVar.GV().alertMsgAll("Error", "Something went wrong, please try again later.", SkipWaybillNoinRouteLine.this,
                                Enum.ERROR_TYPE,"SkipWaybillNoinRouteLine");
                }

                @Override
                public void returnError(String message) {
                    //mView.showError(message);
                    System.out.println(message);
                }
            }, setValues());
        }
    }

    private void initSpinner() {


        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                utilities.ModelstoList(skipReasonList));

        // Step 3: Tell the spinner about our adapter
        skipreason.setAdapter(spinnerArrayAdapter);
        spinnerArrayAdapter.notifyDataSetChanged();
        exitdialog();
//
//        Observable<CharSequence> empID =
//                RxTextView.textChanges(passwordEditText);
    }

    private boolean isValid() {
        boolean value = true;


        return true;
    }

    private void exitdialog() {
        if (alertDialog != null) {
            alertDialog.dismissWithAnimation();
            alertDialog = null;
        }
    }

//    private boolean isValid() {
//        Observable empIDObservable = Observable.just()
//
//                RxV
//        Observable<String> empIDObservable = RxTextView.textChanges(empID).skip(1).
//                map(new Function<CharSequence, String>() {
//                    @Override
//                    public String apply(CharSequence charSequence) throws Exception {
//                        return charSequence.toString();
//                    }
//                });
//
//        Observable<String> deliverysheetIDObservable = RxTextView.textChanges(empID).skip(1).
//                map(new Function<CharSequence, String>() {
//                    @Override
//                    public String apply(CharSequence charSequence) throws Exception {
//                        return charSequence.toString();
//                    }
//                });
//
//
//        observable = Observable.combineLatest(empIDObservable, deliverysheetIDObservable,
//                new BiFunction<String, String, Boolean>() {
//                    @Override
//                    public Boolean apply(String s, String s2) throws Exception {
//                        return isValidForm(s, s2);
//                    }
//                });
//
//        return false;
//    }

    public boolean isValidForm() {
        boolean empid = empID_txt.getText().toString().length() > 0;
        boolean deliverysheetid = deliverysheetID_txt.getText().toString().length() > 0;
        boolean lata = lat_txt.getText().toString().length() > 0;
        boolean longia = longi_txt.getText().toString().length() > 0;
        boolean waybillno = waybillNo_txt.getText().toString().length() > 0;
        boolean lataC = lat_txt.getText().toString().length() > 0 && lat_txt.getText().toString().contains(".");
        boolean longiaC = longi_txt.getText().toString().length() > 0 && longi_txt.getText().toString().contains(".");
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setValid(true);
        if (!empid) {
            empID_txt.setError("Please enter valid Employee ID");
            notificationRequest.setValid(false);
        } else if (!deliverysheetid) {
            deliverysheetID_txt.setError("Please enter valid DeliverysheetID");
            notificationRequest.setValid(false);
        } else if (!lata || !lataC) {
            lat_txt.setError("Please enter Valid Latitude");
            notificationRequest.setValid(false);
        } else if (!longia || !longiaC) {
            longi_txt.setError("Please enter Valid Longitude");
            notificationRequest.setValid(false);
        } else if (!waybillno) {
            waybillNo_txt.setError("Please enter Valid WaybillNo");
            notificationRequest.setValid(false);
        }
        if (notificationRequest.getValid())
            setValues();

        return notificationRequest.getValid();
    }

    private NotificationRequest setValues() {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setValid(true);
        notificationRequest.setWayBillNo(waybillNo_txt.getText().toString());
        notificationRequest.setByEmployID(GlobalVar.GV().EmployID);
        notificationRequest.setDeliverySheetID(Integer.parseInt(deliverysheetID_txt.getText().toString()));
        notificationRequest.setEmployID(Integer.parseInt(empID_txt.getText().toString()));
        notificationRequest.setLat(lat_txt.getText().toString());
        notificationRequest.setLong(longi_txt.getText().toString());
        notificationRequest.setReasonID(isSelectedReasonID);

        return notificationRequest;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void setSavedInstance(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");

        }
    }

//    public SkipWaybillNoinRouteLine(AlertCallback alertCallback) {
//        this.alertCallback = alertCallback;
//    }

    static SweetAlertDialog alertDialog;

    @Override
    public void returnOk(final int value, final Activity activity) {

        this.runOnUiThread(new Runnable() {
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        exitdialog();
                        if (Enum.SUCCESS_TYPE.getValue() == value)
                            activity.finish();

                    }
                });

            }
        });
    }

    @Override
    public void returnCancel(int cancel, SweetAlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        System.out.println("onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        isSelectedReasonID = (int) skipReasonList.get(i).getID();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}