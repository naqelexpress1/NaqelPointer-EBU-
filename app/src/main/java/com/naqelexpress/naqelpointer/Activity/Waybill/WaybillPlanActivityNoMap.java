package com.naqelexpress.naqelpointer.Activity.Waybill;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity;
import com.naqelexpress.naqelpointer.Classes.ConsingeeMobileSpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WaybillPlanActivityNoMap extends AppCompatActivity {
   //private GoogleMap mMap;
    Marker now;
    TextView txtWaybillNo, txtShipperName, txtConsigneeName, txtMobileNo, txtBillingType, txtCODAmount, txtPODType, txtPhoneNo;
    TextView lbPODType;
    ConsingeeMobileSpinnerDialog spinnerDialog;
    //Button btnDelivered, btnNotDeliverd, btnCall;
    private Bundle bundle;
    MyRouteShipments myRouteShipments;
    String ConsigneeLatitude, ConsigneeLongitude;
   // SupportMapFragment mapFragment;
    public double Latitude = 0;
    public double Longitude = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.waybillplannomap);
        try {

            bundle = getIntent().getExtras();

//             mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map);
//
//            mapFragment.getMapAsync(this);
//            mapFragment.getMapAsync(this);

            txtWaybillNo = (TextView) findViewById(R.id.txtWaybilll);
            txtShipperName = (TextView) findViewById(R.id.txtShipperName);
            txtConsigneeName = (TextView) findViewById(R.id.txtConsigneeName);
            txtMobileNo = (TextView) findViewById(R.id.txtMobileNo);
            txtBillingType = (TextView) findViewById(R.id.txtBillingType);
            txtCODAmount = (TextView) findViewById(R.id.txtCODAmount);
            txtPODType = (TextView) findViewById(R.id.txtPODType);
            lbPODType = (TextView) findViewById(R.id.lbPODType);
            txtPhoneNo = (TextView) findViewById(R.id.txtPhoneNo);

            AppCompatImageButton btnCallMobile, btnCallMobile1, btnWhatsApp, btnWhatsApp1, sms, sms1;
            btnCallMobile = (AppCompatImageButton) findViewById(R.id.btnCall);
            btnWhatsApp = (AppCompatImageButton) findViewById(R.id.btnWhatsapp);
            btnCallMobile1 = (AppCompatImageButton) findViewById(R.id.btnCall1);
            btnWhatsApp1 = (AppCompatImageButton) findViewById(R.id.btnWhatsapp1);
            sms = (AppCompatImageButton) findViewById(R.id.sms);
            sms1 = (AppCompatImageButton) findViewById(R.id.sms1);


            btnCallMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(txtMobileNo.getTag().toString(), getWindow().getDecorView().getRootView(), WaybillPlanActivityNoMap.this);
                }
            });
            btnCallMobile1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(txtPhoneNo.getTag().toString(), getWindow().getDecorView().getRootView(), WaybillPlanActivityNoMap.this);
                }
            });

            btnWhatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobileno = txtMobileNo.getTag().toString();
                    if (!mobileno.equals("null") && mobileno != null && mobileno.length() > 0) {
                        if (mobileno.length() == 10) {
                            String validate = mobileno.substring(0, 1);
                            if (validate.equals("0"))
                                mobileno = mobileno.replaceFirst("0", "+966");
                        } else {
                            if (mobileno.length() > 10) {
                                if (mobileno.contains("00966"))
                                    mobileno = mobileno.replaceFirst("00966", "+966");
                            } else if (mobileno.length() == 9) {
                                mobileno = "+966" + mobileno;
                            }
                        }

//                    GlobalVar.GV().MessageWhatsApp(rootView.getContext(),txtMobileNo.getTag().toString(),"hellow");

                        showPopup(mobileno);
                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Is not valid mobile number.", GlobalVar.AlertType.Warning);
                }
            });

            btnWhatsApp1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mobileno = txtPhoneNo.getTag().toString();
                    if (!mobileno.equals("null") && mobileno != null && mobileno.length() > 0) {
                        if (mobileno.length() == 10) {
                            String validate = mobileno.substring(0, 1);
                            if (validate.equals("0"))
                                mobileno = mobileno.replaceFirst("0", "+966");
                        } else {
                            if (mobileno.length() > 10) {
                                //String validate = mobileno.substring(0, 2);
                                if (mobileno.contains("00966"))
                                    mobileno = mobileno.replaceFirst("00966", "+966");
                            } else if (mobileno.length() == 9) {
                                mobileno = "+966" + mobileno;
                            }
                        }

                        showPopup(mobileno);

                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Is not valid mobile number.", GlobalVar.AlertType.Warning);
                }
            });

            sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().sendSMS("+966580679791", "text", getApplicationContext());

                }
            });
            sms1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().sendSMS("+966580679791", "text", getApplicationContext());
                }
            });


            if (savedInstanceState != null)
                setSavedInstance(savedInstanceState);

            if (bundle != null) {

                int position = bundle.getInt("position");

                myRouteShipments = GlobalVar.GV().myRouteShipmentList.get(position);

                txtWaybillNo.setText(myRouteShipments.ItemNo);
                txtShipperName.setText(myRouteShipments.ClientName);
                txtConsigneeName.setText(myRouteShipments.ConsigneeName);
                txtMobileNo.setText(myRouteShipments.ConsigneeMobile);
                txtMobileNo.setTag(myRouteShipments.ConsigneeMobile);
                txtBillingType.setText(myRouteShipments.BillingType);
                txtCODAmount.setText(String.valueOf(myRouteShipments.CODAmount));
                txtPhoneNo.setText(myRouteShipments.ConsigneePhoneNumber);
                txtPhoneNo.setTag(myRouteShipments.ConsigneePhoneNumber);
                ConsigneeLatitude = myRouteShipments.Latitude;
                ConsigneeLongitude = myRouteShipments.Longitude;

                if (myRouteShipments.PODNeeded)
                    txtPODType.setText(myRouteShipments.PODTypeCode);
                else {
                    txtPODType.setVisibility(View.GONE);
                    lbPODType.setVisibility(View.GONE);
                }


            }


            spinnerDialog = new ConsingeeMobileSpinnerDialog(WaybillPlanActivityNoMap.this, txtPhoneNo.getText().toString(),
                    txtMobileNo.getText().toString(), getWindow().getDecorView().getRootView());

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                requestLocation();


            } else {
                ActivityCompat.requestPermissions(WaybillPlanActivityNoMap.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                finish();
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
        }

//        if (!GlobalVar.GV().checkPermission(WaybillPlanActivity.this, GlobalVar.PermissionType.AccessFindLocation)) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedLocationPermision), GlobalVar.AlertType.Error);
//            GlobalVar.GV().askPermission(WaybillPlanActivity.this, GlobalVar.PermissionType.AccessFindLocation);
//        }
    }

    private void setSavedInstance(Bundle savedInstanceState) {
        GlobalVar.GV().myRouteShipmentList = savedInstanceState.getParcelableArrayList("kpi");
    }

    private void requestLocation() {
        try {


            Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
            if (location != null) {
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();

                if (now != null)
                    now.remove();

                // Creating a LatLng object for the current location
                LatLng latLng = new LatLng(Latitude, Longitude);
                GlobalVar.GV().currentLocation = latLng;


            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnuwaybilldetails, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ConsigneeAddrss:
                ConsigneeAddress();
                return true;
            case R.id.CallConsignee:
                spinnerDialog.showSpinerDialog();
                return true;
            case R.id.Delivered:
                Delivered();
                return true;
            case R.id.NotDelivered:

                NotDelivered();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    public void MakeCall() {
//        GlobalVar.GV().makeCall(txtMobileNo.getText().toString());
//    }

    public void ConsigneeAddress() {
        //Intent intent = new Intent( this, com.naqelexpress.naqelpointer.Activity.Waybill.ConsigneeAddressTranslation.class );
        Intent intent = new Intent(getApplicationContext(), ConsigneeAddressTranslationActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void Delivered() {

        if (GetDivision()) {
            Intent intent = new Intent(this, com.naqelexpress.naqelpointer.Activity.DeliveryOFD.DeliveryActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void NotDelivered() {

        if (GetDivision()) {
            String waybillno = txtWaybillNo.getText().toString().substring(0, 8);
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            Cursor result = dbConnections.Fill("select PiecesCount from MyRouteShipments Where ItemNo = '"
                            + waybillno + "'",
                    getApplicationContext());

            if (result.getCount() > 0) {
                result.moveToFirst();
                if (result.getInt(result.getColumnIndex("PiecesCount")) > 1) {
                    Intent notDelivered = new Intent(getApplicationContext(),
                            com.naqelexpress.naqelpointer.Activity.NotDeliveredCBU.NotDeliveredActivity.class);
                    notDelivered.putExtras(bundle);
                    startActivity(notDelivered);
                } else {
                    Intent notDelivered = new Intent(getApplicationContext(),
                            com.naqelexpress.naqelpointer.Activity.NotDeliveredSinglePiece.NotDeliveredActivity.class);
                    notDelivered.putExtras(bundle);
                    startActivity(notDelivered);
                }


            }
        } else {
            Intent notDelivered = new Intent(getApplicationContext(), NotDeliveredActivity.class);
            startActivity(notDelivered);
        }

//        Intent intent = new Intent(this, com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }


    private boolean GetDivision() {
        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);

        if (division.equals("Express"))
            return false;
        else
            return true;

    }





    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("kpi", GlobalVar.GV().myRouteShipmentList);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        try {
            outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
            outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);

        } catch (Exception e) {

        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        GlobalVar.GV().myRouteShipmentList = savedInstanceState.getParcelableArrayList("kpi");
        GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
        GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
        GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
        GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
        GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
        GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
        GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
        GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
    }

    PopupWindow popup;

    private void showPopup(final String mobileno) {


        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.messagedrafts, viewGroup);

        final TextView customerlocation = (TextView) layout.findViewById(R.id.customerlocation);
        final TextView frontofthedoor = (TextView) layout.findViewById(R.id.frontofthedoor);
        final TextView cssupport = (TextView) layout.findViewById(R.id.cssupport);
        final TextView resndotp = (TextView) layout.findViewById(R.id.resendotp);

        final String arabic = "عزيزي العميل, n\n  رجاء قم بمشاركة موقعك على الرابط المرفق أدناه لنقوم بتوصيل شحنتك.(" + " " + txtWaybillNo.getText().toString()
                + ") من)" + txtShipperName.getText().toString();

        customerlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg)
                        + " " + txtWaybillNo.getText().toString() + getString(R.string.watsappPredefinedMsg1)
                        + txtShipperName.getText().toString() + "\n\n\n" + arabic + getString(R.string.watsappPredefinedMsg2)
                        + txtWaybillNo.getText().toString(), getApplicationContext());
                popup.dismiss();
            }
        });
        frontofthedoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.doorPredefinedMsg)
                        + " " + txtWaybillNo.getText().toString() + getString(R.string.doorPredefinedMsg1)
                        + txtShipperName.getText().toString() + getString(R.string.doorPredefinedMsg2)
                        + txtWaybillNo.getText().toString() + "\n\n\n" + arabic, getApplicationContext());
                popup.dismiss();
            }
        });
        cssupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.csPredefinedMsg) + "\n\n\n" + arabic, getApplicationContext());
                popup.dismiss();
            }
        });

        resndotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" + txtWaybillNo.getText().toString() + "'",
                        getApplicationContext());

                int otpno = 0;
                if (result.getCount() > 0) {
                    result.moveToFirst();
                    otpno = result.getInt(result.getColumnIndex("OTPNo"));
                    if (otpno == 9081988)
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Dont have OTP No for this waybillno " + txtWaybillNo.getText().toString() + ", kindly contact Supervisor", GlobalVar.AlertType.Error);
                    else {
                        //GlobalVar.GV().SendSMSbydefault(mobileno, "Your Delivery ID Verification Code is :" + String.valueOf(otpno), getApplicationContext());
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("WaybillNo", Integer.parseInt(txtWaybillNo.getText().toString()));
                            jsonObject.put("EmployID", result.getInt(result.getColumnIndex("DeliverySheetID")));
                            jsonObject.put("Message", otpno);
                            jsonObject.put("MobileNo", mobileno);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new ResendOtpNo().execute(jsonObject.toString());
                    }
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Dont have OTP No for this waybillno " + txtWaybillNo.getText().toString() + ", kindly contact Supervisor", GlobalVar.AlertType.Error);

                //GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.csPredefinedMsg) +"\n\n\n" + arabic, getApplicationContext());
                popup.dismiss();
            }
        });


        popup = new PopupWindow(WaybillPlanActivityNoMap.this);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    Point p;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        TextView txtMobileNo = (TextView) findViewById(R.id.txtMobileNo);

        txtMobileNo.getLocationOnScreen(location);

        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    private class ResendOtpNo extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            progressDialog = ProgressDialog.show(WaybillPlanActivityNoMap.this,
                    "Please wait.", "Your Request has been process, kindly be patient  ", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "ResendOTPtoConsignee");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(GlobalVar.GV().ConnandReadtimeout);
                httpURLConnection.setReadTimeout(GlobalVar.GV().ConnandReadtimeout);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return String.valueOf(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            super.onPostExecute(String.valueOf(finalJson));

            if (finalJson != null) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(finalJson);


                    if (jsonObject.getInt("ID") == 103) {

                        new SweetAlertDialog(WaybillPlanActivityNoMap.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Info")
                                .setContentText(jsonObject.getString("Name"))
                                .show();
                    } else {
                        new SweetAlertDialog(WaybillPlanActivityNoMap.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Info")
                                .setContentText(jsonObject.getString("Name"))
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            } else {
                new SweetAlertDialog(WaybillPlanActivityNoMap.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Info")
                        .setContentText("something went wrong/check your Internet/server is busy,kindly try again later")
                        .show();
            }
            progressDialog.dismiss();
        }
    }

}