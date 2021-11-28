package com.naqelexpress.naqelpointer.Activity.FuelModelEBU;

import android.annotation.SuppressLint;
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

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.FuelSupplierTypeModels;
import com.naqelexpress.naqelpointer.Models.Request.FuelRequest;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.ExpFuelApi;
import com.naqelexpress.naqelpointer.utils.utilities;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;


public class Fuel
        extends AppCompatActivity implements AlertCallback, AdapterView.OnItemSelectedListener {

    private AlertCallback alertCallback;
    Observable<Boolean> observable;
    EditText odometer, fuleprice, liters;
    Spinner fueltype, suppliertype;
    //List<FuelTypeModels> fuelTypeModelsList = new ArrayList<>();
    List<com.naqelexpress.naqelpointer.Models.FuelTypeModel.FuelType> fuelTypeModelsList = new ArrayList<>();
    List<com.naqelexpress.naqelpointer.Models.FuelTypeModel.SupplierType> supplierTypeList = new ArrayList<>();
    FuelSupplierTypeModels fuelSupplierTypeModelsList = new FuelSupplierTypeModels();
    int isSelectedReasonID = 0, supplierID = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuelmodel);


        odometer = (EditText) findViewById(R.id.odometer);

        fuleprice = (EditText) findViewById(R.id.fuleprice);
        fueltype = (Spinner) findViewById(R.id.fueltype);
        fueltype.setOnItemSelectedListener(this);
        suppliertype = (Spinner) findViewById(R.id.suppliertype);
        suppliertype.setOnItemSelectedListener(this);
        liters = (EditText) findViewById(R.id.liters);


        FetchReason();

    }

//    public void submit(View view) {
//
//    }


  /*  private void FetchReason() {
        GlobalVar.GV().alertMsgAll("Info", "Please wait to fetch Fuel Type.", Fuel.this,
                Enum.PROGRESS_TYPE, "Fuel");

        ExpFuelApi.FetchFuelType(new Callback<List<FuelTypeModels>>() {
            @Override
            public void returnResult(List<FuelTypeModels> result) {
                System.out.println();
                fuelTypeModelsList.addAll(result);
                initSpinner();
            }

            @Override
            public void returnError(String message) {
                //mView.showError(message);
                System.out.println(message);
            }
        });
    }*/

    private void FetchReason() {
        GlobalVar.GV().alertMsgAll("Info", "Please wait to fetch Fuel Type.", Fuel.this,
                Enum.PROGRESS_TYPE, "Fuel");

        ExpFuelApi.FetchFuelSupplierType(new Callback<FuelSupplierTypeModels>() {
            @Override
            public void returnResult(FuelSupplierTypeModels result) {
                System.out.println();
                fuelTypeModelsList.addAll(result.FuelType);
                supplierTypeList.addAll(result.SupplierType);
                //fuelSupplierTypeModelsList.addAll(result);
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
            GlobalVar.GV().alertMsgAll("Info", "Please wait...", Fuel.this,
                    Enum.PROGRESS_TYPE, "Fuel");


            ExpFuelApi.FuelApi(new Callback<CommonResult>() {
                @Override
                public void returnResult(CommonResult result) {
                    if (!result.getHasError()) {
                        GlobalVar.GV().alertMsgAll("Info", "Your request has been completed.", Fuel.this,
                                Enum.SUCCESS_TYPE, "Fuel");
                        //finish();
                    } else
                        GlobalVar.GV().alertMsgAll("Error", "Something went wrong, please try again later.", Fuel.this,
                                Enum.ERROR_TYPE, "Fuel");
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
                utilities.FuelModelstoList(fuelTypeModelsList));

        // Step 3: Tell the spinner about our adapter
        fueltype.setAdapter(spinnerArrayAdapter);
        spinnerArrayAdapter.notifyDataSetChanged();

        ArrayAdapter suppliertypeadapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                utilities.SupplierModelstoList(supplierTypeList));

        // Step 3: Tell the spinner about our adapter
        suppliertype.setAdapter(suppliertypeadapter);
        suppliertypeadapter.notifyDataSetChanged();


        exitdialog();

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

        boolean isok = true;
        boolean om = odometer.getText().toString().length() > 0;
        boolean fp = fuleprice.getText().toString().length() > 0;
        boolean lt = liters.getText().toString().length() > 0;


        if (!om) {
            odometer.setError("Please enter valid Odometer ID");
            isok = false;
        } else if (!fp) {
            fuleprice.setError("Please enter valid Fuel Price");
            isok = false;

        } else if (!lt) {
            liters.setError("Please enter Valid Liters");
            isok = false;
        }

        return isok;
    }

    private FuelRequest setValues() {
        FuelRequest fuelRequest = new FuelRequest();
        fuelRequest.setEmployID(GlobalVar.GV().EmployID);
        fuelRequest.setFuelPrice(fuleprice.getText().toString());
        fuelRequest.setFuelTypeID(isSelectedReasonID);
        fuelRequest.setLitres(liters.getText().toString());
        fuelRequest.setOdometer(odometer.getText().toString());
        fuelRequest.setTruckId(DBConnections.GetTruckID(GlobalVar.GV().EmployID, getApplicationContext()));
        fuelRequest.setFuelSupplierID(supplierID);

        return fuelRequest;
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


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId()) {
            case R.id.fueltype:
                isSelectedReasonID = (int) fuelTypeModelsList.get(i).getID();
                break;
            case R.id.suppliertype:
                supplierID = (int) supplierTypeList.get(i).getID();
                break;

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}