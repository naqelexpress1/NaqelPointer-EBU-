package com.naqelexpress.naqelpointer.Activity.Waybill;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
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
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.CongisneeUpdatedNoRequest;
import com.naqelexpress.naqelpointer.JSON.Results.CongisneeUpdatedNoResult;
import com.naqelexpress.naqelpointer.Models.CourierNotesModels;
import com.naqelexpress.naqelpointer.Models.IsFollowSequncerModel;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.CourierNotesService;
import com.naqelexpress.naqelpointer.service.IsFollowSequencerService;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Error.ErrorReporter;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class WaybillPlanActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    Marker now;
    TextView txtWaybillNo, txtShipperName, txtConsigneeName, txtBillingType, txtCODAmount,
            txtPODType, txtCDAmount, txtTotalAmount, txtMobileNo, txtPhoneNo;
    ;
    TextView lbPODType;
    ConsingeeMobileSpinnerDialog spinnerDialog;
    //Button btnDelivered, btnNotDeliverd, btnCall;
    private Bundle bundle;
    MyRouteShipments myRouteShipments;
    String ConsigneeLatitude, ConsigneeLongitude;
    SupportMapFragment mapFragment;
    //    MapFragment mapFragment;
    public double Latitude = 0;
    public double Longitude = 0;
    int position;
    boolean isEnable = true;
    EditText txtNotes;
    AppCompatImageButton btnCallMobile, btnCallMobile1, btnWhatsApp, btnWhatsApp1, sms, sms1;
    Button togoogle;//, txtMobileNo, txtPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());

        setContentView(R.layout.waybillplan);
        try {

            bundle = getIntent().getExtras();

            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

//            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mapFragment.getMapAsync(this);

            txtWaybillNo = (TextView) findViewById(R.id.txtWaybilll);
            txtShipperName = (TextView) findViewById(R.id.txtShipperName);
            txtConsigneeName = (TextView) findViewById(R.id.txtConsigneeName);
            txtMobileNo = (TextView) findViewById(R.id.txtMobileNo);
            txtBillingType = (TextView) findViewById(R.id.txtBillingType);
            //txtCODAmount = (TextView) findViewById(R.id.txtCODAmount);
            txtCODAmount = (TextView) findViewById(R.id.tv_cod_body);
            txtPODType = (TextView) findViewById(R.id.txtPODType);
            lbPODType = (TextView) findViewById(R.id.lbPODType);
            txtPhoneNo = (TextView) findViewById(R.id.txtPhoneNo);
            //Added by Riyam
            //txtShipmentAmount = findViewById(R.id.txtShipmentAmount);
            txtCDAmount = findViewById(R.id.tv_cd_body);
            txtTotalAmount = findViewById(R.id.tv_total_amount_body);
            txtNotes = (EditText) findViewById(R.id.txtnotes);

            if (GlobalVar.GV().GetDivision(getApplicationContext()))
                txtNotes.setVisibility(View.VISIBLE);


            btnCallMobile = (AppCompatImageButton) findViewById(R.id.btnCall);
            btnWhatsApp = (AppCompatImageButton) findViewById(R.id.btnWhatsapp);
            btnCallMobile1 = (AppCompatImageButton) findViewById(R.id.btnCall1);
            btnWhatsApp1 = (AppCompatImageButton) findViewById(R.id.btnWhatsapp1);
            sms = (AppCompatImageButton) findViewById(R.id.sms);
            sms1 = (AppCompatImageButton) findViewById(R.id.sms1);
            togoogle = (Button) findViewById(R.id.toGoogle);


            btnCallMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(txtMobileNo.getTag().toString(), getWindow().getDecorView().getRootView(), WaybillPlanActivity.this);
                    // GlobalVar.GV().makeCallAwaya(txtMobileNo.getTag().toString(), getWindow().getDecorView().getRootView(), WaybillPlanActivity.this);
                }
            });
            btnCallMobile1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(txtPhoneNo.getTag().toString(), getWindow().getDecorView().getRootView(), WaybillPlanActivity.this);
                    //GlobalVar.GV().makeCallAwaya(txtMobileNo.getTag().toString(), getWindow().getDecorView().getRootView(), WaybillPlanActivity.this);
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

                double totalAmount = myRouteShipments.CODAmount;  // This is cod + custom duty
                double cdAmount = myRouteShipments.CustomDuty;  // cd
                double actualCodAmount = Math.abs(totalAmount - cdAmount);


                txtWaybillNo.setText(myRouteShipments.ItemNo);
                txtShipperName.setText(myRouteShipments.ClientName);
                txtConsigneeName.setText(myRouteShipments.ConsigneeName);
                txtMobileNo.setText(myRouteShipments.ConsigneeMobile);
                txtMobileNo.setTag(myRouteShipments.ConsigneeMobile);
                txtBillingType.setText(myRouteShipments.BillingType);
                txtCODAmount.setText(String.valueOf(actualCodAmount)); //Riyam
                txtPhoneNo.setText(myRouteShipments.ConsigneePhoneNumber);
                txtPhoneNo.setTag(myRouteShipments.ConsigneePhoneNumber);
                ConsigneeLatitude = myRouteShipments.Latitude;
                ConsigneeLongitude = myRouteShipments.Longitude;
                //Added by Riyam
                txtCDAmount.setText(String.valueOf(cdAmount));
                txtTotalAmount.setText(String.valueOf(totalAmount));


                if (myRouteShipments.PODNeeded)
                    txtPODType.setText(myRouteShipments.PODTypeCode);
                else {
                    txtPODType.setVisibility(View.GONE);
                    lbPODType.setVisibility(View.GONE);
                }


            }

            if (ConsigneeLatitude.length() == 0)
                togoogle.setVisibility(View.GONE);

            spinnerDialog = new ConsingeeMobileSpinnerDialog(WaybillPlanActivity.this, txtPhoneNo.getText().toString(),
                    txtMobileNo.getText().toString(), getWindow().getDecorView().getRootView());

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                requestLocation();


            } else {
                ActivityCompat.requestPermissions(WaybillPlanActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                finish();
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
        }

        try {
            if (bundle.containsKey("isEnable"))
                isEnable = bundle.getBoolean("isEnable");
            if (!isEnable)
                disableportion();
        } catch (Exception e) {
            isEnable = true;
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
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.save);

        if (GlobalVar.GV().GetDivision(getApplicationContext())) {
            item.setEnabled(true);
            //    item.getIcon().setAlpha(255);
        } else {
            // disabled
            item.setEnabled(false);
//            item.getIcon().setAlpha(130);
        }
        return true;
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
            case R.id.UpdatedConsigneeNo:
                PrepareGetConsigneeUpdatedNo(GlobalVar.GV().EmployID, Integer.parseInt(myRouteShipments.ItemNo));
            case R.id.save:
                insertCourierNotes();

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

//        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (GetDivision()) {
            if (ConsigneeLatitude.length() == 0) {
                ConsigneeLatitude = "0.0";
                ConsigneeLongitude = "0.0";
            }
            if (GlobalVar.GV().isSeqComplete(getApplicationContext())) {
                ConsigneeLatitude = "0.0";
                ConsigneeLongitude = "0.0";
            }
            if (GlobalVar.isCourierReachedConsigneeLocation(getApplicationContext(), Double.parseDouble(ConsigneeLatitude), Double.parseDouble(ConsigneeLongitude))) {
                Intent intent = new Intent(this, com.naqelexpress.naqelpointer.Activity.DeliveryOFD.DeliveryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else
                ErrorAlert("Info", "Kindly Reach Consignee Location before start Delivery Scan");
        } else {
            Intent intent = new Intent(this, com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void NotDelivered() {

        if (GetDivision()) {


            if (ConsigneeLatitude.length() == 0) {
                ConsigneeLatitude = "0.0";
                ConsigneeLongitude = "0.0";
            }
            if (GlobalVar.GV().isSeqComplete(getApplicationContext())) {
                ConsigneeLatitude = "0.0";
                ConsigneeLongitude = "0.0";
            }
            if (GlobalVar.isCourierReachedConsigneeLocation(getApplicationContext(), Double.parseDouble(ConsigneeLatitude), Double.parseDouble(ConsigneeLongitude))) {

                String waybillno = "";
                if (txtWaybillNo.getText().toString().length() == 8)
                    waybillno = txtWaybillNo.getText().toString().substring(0, 8);
                else

                    waybillno = txtWaybillNo.getText().toString().substring(0, GlobalVar.ScanWaybillLength);

                if (waybillno.length() == 0)
                    finish();

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
            } else
                ErrorAlert("Info", "Kindly Reach Consignee Location before start Not Deliver Scan");
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
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            GlobalVar.GV().ChangeMapSettings(mMap, WaybillPlanActivity.this, getWindow().getDecorView().getRootView());

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
            now = mMap.addMarker(new MarkerOptions().position(GlobalVar.GV().currentLocation)
                    .icon(icon)
                    .title(getString(R.string.MyLocation)));

            //mMap.getUiSettings().setMapToolbarEnabled(false);

            ShowShipmentMarker();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void ShowShipmentMarker() {
        if (ConsigneeLongitude.length() > 3 && ConsigneeLongitude.length() > 3) {
            LatLng latLng = new LatLng(GlobalVar.GV().getDoubleFromString(ConsigneeLatitude), GlobalVar.GV().getDoubleFromString(ConsigneeLongitude));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.deliverymarker);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(icon)
                    .title(txtWaybillNo.getText().toString()));
            mMap.setOnMarkerClickListener(WaybillPlanActivity.this);
        }
//        else
//            mapFragment.getView().setVisibility(View.GONE);
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
        try {
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
        } catch (Exception e) {

        }
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
                if (ConsigneeLatitude != null && ConsigneeLatitude.length() == 0)
//                    GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg)
//                            + " " + txtWaybillNo.getText().toString() + getString(R.string.watsappPredefinedMsg1)
//                            + txtShipperName.getText().toString() + "\n\n\n" + arabic + getString(R.string.watsappPredefinedMsg2)
//                            + txtWaybillNo.getText().toString(), getApplicationContext());
                    GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getLocationMsg(), getApplicationContext());
                else
                    alertforcommon();

                popup.dismiss();
            }
        });
        frontofthedoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.doorPredefinedMsg)
//                        + " " + txtWaybillNo.getText().toString() + getString(R.string.doorPredefinedMsg1)
//                        + txtShipperName.getText().toString() + getString(R.string.doorPredefinedMsg2)
//                        + txtWaybillNo.getText().toString() + "\n\n\n" + arabic, getApplicationContext());
                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getFrontDoorMsg(), getApplicationContext());
                popup.dismiss();
            }
        });
        cssupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.csPredefinedMsg) + "\n\n\n" + arabic, getApplicationContext());
                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getCsSupportMsg(), getApplicationContext());
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


        popup = new PopupWindow(WaybillPlanActivity.this);
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

    private void alertforcommon() {
        SweetAlertDialog eDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        eDialog.setCancelable(true);
        eDialog.setTitleText("Has Location");
        eDialog.setContentText("This Shipment already has Location , kindly please start to deliver");
        eDialog.show();

    }

    private void ErrorAlert(String title, String msg) {
        SweetAlertDialog eDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        eDialog.setCancelable(true);
        eDialog.setTitleText(title);
        eDialog.setContentText(msg);
        eDialog.show();

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

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (!marker.getTitle().equals("My Location")) {
            //handle click here
            toGoogle();
        }
        return false;
    }

    private class ResendOtpNo extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            progressDialog = ProgressDialog.show(WaybillPlanActivity.this,
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

                        new SweetAlertDialog(WaybillPlanActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Info")
                                .setContentText(jsonObject.getString("Name"))
                                .show();
                    } else {
                        new SweetAlertDialog(WaybillPlanActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Info")
                                .setContentText(jsonObject.getString("Name"))
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            } else {
                new SweetAlertDialog(WaybillPlanActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Info")
                        .setContentText("something went wrong/check your Internet/server is busy,kindly try again later")
                        .show();
            }
            progressDialog.dismiss();
        }
    }


    public void PrepareGetConsigneeUpdatedNo(int employID, int waybillNo) {
        try {
            CongisneeUpdatedNoRequest request = new CongisneeUpdatedNoRequest();
            request.EmployeeID = employID;
            request.WaybillNo = waybillNo;
            String jsonData = JsonSerializerDeserializer.serialize(request, true);
            new GetCongisneeUpdatedNo().execute(jsonData);
        } catch (Exception e) {
//           eog.d("test", e.toString());
        }
    }

    private class GetCongisneeUpdatedNo extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(WaybillPlanActivity.this, "Please wait.", "Checking for updates ..", true);
            // TODO : Update to production link
            DomainURL = GlobalVar.GV().NaqelPointerAPILink + "GetUpdatedConsigneeNo";
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;
            try {

                URL url = new URL(DomainURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                try {
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();
                } catch (java.net.NoRouteToHostException se) {
                    System.out.println(se);
                }
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
            } catch (Exception ignored) {
                isInternetAvailable = ignored.toString();
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
            try {
                if (finalJson != null) {

                    CongisneeUpdatedNoResult result = new CongisneeUpdatedNoResult(finalJson);

                    if (result.HasError) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), result.ErrorMessage, GlobalVar.AlertType.Error);
                    } else {

                        //check they are not null not empty .. etc then update
                        if (!result.MobileNo.equals("null") && result.MobileNo != null && !result.MobileNo.equals("0")
                                && result.MobileNo.length() > 0 && !result.PhoneNo.equals("null") && result.PhoneNo != null &&
                                !result.PhoneNo.equals("0") && result.PhoneNo.length() > 0) {

                            //Get emp country
                            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                            int countryID = 0;
                            String countryCode = "";

                            Cursor cursor = dbConnections.getStationID(GlobalVar.GV().EmployID, getApplicationContext());
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                countryID = cursor.getInt(cursor.getColumnIndex("CountryID"));
                                countryCode = cursor.getString(cursor.getColumnIndex("CountryCode"));
                            }

                            if (countryID == 1)
                                result.MobileNo = GlobalVar.ValidateMobileNo(result.MobileNo);
                            else
                                result.MobileNo = GlobalVar.ValidateMobileNoOtherCountry(result.MobileNo, countryCode);

                            if (countryID == 1)
                                result.PhoneNo = GlobalVar.ValidateMobileNo(result.PhoneNo);
                            else
                                result.PhoneNo = GlobalVar.ValidateMobileNoOtherCountry(result.PhoneNo, countryCode);

                            updateConsigneeMobileView(result.PhoneNo, result.MobileNo);
                            updateConsigneeMobileLocalDB(result.PhoneNo, result.MobileNo);
                        }

                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Consignee number is updated" + result.ErrorMessage, GlobalVar.AlertType.Info);
                    }
                } else {
                    if (isInternetAvailable.contains("No address associated with hostname")) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                    } else {
                        GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                        if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                            GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                        }

                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.servererror), GlobalVar.AlertType.Error);
                    }

                }

                progressDialog.dismiss();
                super.onPostExecute(String.valueOf(finalJson));
            } catch (Exception e) {

            }
        }
    }


    public void updateConsigneeMobileView(String phoneNo, String mobileNo) {
        txtPhoneNo.setText(phoneNo);
        txtPhoneNo.setTag(phoneNo);
        txtMobileNo.setText(mobileNo);
        txtMobileNo.setTag(mobileNo);
    }

    public void updateConsigneeMobileLocalDB(String phoneNo, String mobileNo) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.UpdateConsigneeNo(myRouteShipments.ItemNo, phoneNo, mobileNo);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //countDownTimer.cancel();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "refreshdata");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        WaybillPlanActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String getWaybillNo() {
        return txtWaybillNo.getText().toString();
    }

    private String getClientName() {
        return txtShipperName.getText().toString();
    }

    private String getLocationMsg() {
        final String locationMsgAr = getString(R.string.customerLocationMsg1Ar) + "\n" +
                getString(R.string.customerLocationMsg2Ar) + " " +
                getWaybillNo() + " " +
                getString(R.string.customerLocationMsg3Ar) + " " +
                getClientName();
        final String locationMsgEn = getString(R.string.customerLocationMsg1En) + "\n" +
                getString(R.string.customerLocationMsg2En) + " " +
                getWaybillNo() + " " +
                getString(R.string.customerLocationMsg3En) + " " +
                getClientName();
        final String infoTrackLink = getString(R.string.infotrackLocationLink) + getWaybillNo();
        return locationMsgAr + "\n\n" + locationMsgEn + "\n\n" + infoTrackLink;
    }

    private String getFrontDoorMsg() {
        final String frontDoorMsgAr = getString(R.string.frontDoorMsg1Ar) + "\n" +
                getString(R.string.frontDoorMsg2Ar) + " " +
                getWaybillNo() + " " +
                getString(R.string.frontDoorMsg3Ar) + " " +
                getClientName();
        final String frontDoorMsgEn = getString(R.string.frontDoorMsg1En) + "\n" +
                getString(R.string.frontDoorMsg2En) + " " +
                getWaybillNo() + " " +
                getString(R.string.frontDoorMsg3Ar) + " " +
                getClientName();
        return frontDoorMsgAr + "\n\n" + frontDoorMsgEn;
    }

    private String getCsSupportMsg() {
        final String csSupportMsgAr = getString(R.string.csSupportMsg1Ar) + "\n\n" +
                GlobalVar.getCSPhoneNumber() + "\n" +
                GlobalVar.getCSEmail();
        final String csSupportMsgMsgEn = getString(R.string.csSupportMsg1En) + "\n\n" +
                GlobalVar.getCSPhoneNumber() + "\n" +
                GlobalVar.getCSEmail();
        return csSupportMsgAr + "\n\n" + csSupportMsgMsgEn;
    }

    public void toGoogle(View view) {
//        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
//        IsFollowSequncerModel isFollowSequncerModel = new IsFollowSequncerModel();
//        isFollowSequncerModel.setWaybillNo(Integer.parseInt(getWaybillNo()));
//        isFollowSequncerModel.setIsFollow(1);
//        isFollowSequncerModel.setConsLatitude(ConsigneeLatitude);
//        isFollowSequncerModel.setConsLongitude(ConsigneeLongitude);
//        isFollowSequncerModel.setCourierLatitude(String.valueOf(location.getLatitude()));
//        isFollowSequncerModel.setCourierLongitude(String.valueOf(location.getLongitude()));
//        isFollowSequncerModel.setDeliverysheetID(dbConnections.GetDeliverysheetIDbyWaybillNo(getApplicationContext(), Integer.parseInt(getWaybillNo())));
//        isFollowSequncerModel.setEmployeeID(GlobalVar.GV().EmployID);
//
//        if (dbConnections.InsertIsFollowGoogle(isFollowSequncerModel, getApplicationContext())) {
//            if (!GlobalVar.isMyServiceRunning(IsFollowSequencerService.class, getApplicationContext())) {
//                startService(
//                        new Intent(WaybillPlanActivity.this,
//                                com.naqelexpress.naqelpointer.service.IsFollowSequencerService.class));
//            }
//            GlobalVar.toGoogle(ConsigneeLatitude, ConsigneeLongitude, WaybillPlanActivity.this, location);
//        } else
//            GlobalVar.ShowDialog(WaybillPlanActivity.this, "Info", "Something went wrong," +
//                    "Please try again", true);
        toGoogle();

    }

    private void toGoogle() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        IsFollowSequncerModel isFollowSequncerModel = new IsFollowSequncerModel();
        isFollowSequncerModel.setWaybillNo(Integer.parseInt(getWaybillNo()));
        isFollowSequncerModel.setIsFollow(1);
        isFollowSequncerModel.setConsLatitude(ConsigneeLatitude);
        isFollowSequncerModel.setConsLongitude(ConsigneeLongitude);
        isFollowSequncerModel.setCourierLatitude(String.valueOf(location.getLatitude()));
        isFollowSequncerModel.setCourierLongitude(String.valueOf(location.getLongitude()));
        isFollowSequncerModel.setDeliverysheetID(dbConnections.GetDeliverysheetIDbyWaybillNo(getApplicationContext(), Integer.parseInt(getWaybillNo())));
        isFollowSequncerModel.setEmployeeID(GlobalVar.GV().EmployID);

        if (dbConnections.InsertIsFollowGoogle(isFollowSequncerModel, getApplicationContext())) {
            mMap.getUiSettings().setMapToolbarEnabled(true);
            if (!GlobalVar.isMyServiceRunning(IsFollowSequencerService.class, getApplicationContext())) {
                startService(
                        new Intent(WaybillPlanActivity.this,
                                com.naqelexpress.naqelpointer.service.IsFollowSequencerService.class));
            }
//              GlobalVar.toGoogle(ConsigneeLatitude, ConsigneeLongitude, WaybillPlanActivity.this, location);
        } else
            GlobalVar.ShowDialog(WaybillPlanActivity.this, "Info", "Something went wrong," +
                    "Please try again", true);
    }

    private boolean insertCourierNotes() {
        if (txtNotes.getText().toString().length() > 1) {
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            CourierNotesModels courierNotesModels = new CourierNotesModels();
            courierNotesModels.setWaybillNo(Integer.parseInt(txtWaybillNo.getText().toString()));
            courierNotesModels.setDeliverySheetID(dbConnections.GetDeliverysheetIDbyWNo(getApplicationContext(),
                    Integer.parseInt(txtWaybillNo.getText().toString())));
            courierNotesModels.setTimeIn(new DateTime().toString());
            courierNotesModels.setUserID(GlobalVar.GV().EmployID);
            courierNotesModels.setNotes(txtNotes.getText().toString());
            boolean isnotesaved = dbConnections.insertCourierNotes(getApplicationContext(), courierNotesModels);
            if (isnotesaved) {
                GlobalVar.ShowDialog(WaybillPlanActivity.this, "Info", "Your Notes Sucessfully Saved.", true);
                if (!GlobalVar.isMyServiceRunning(CourierNotesService.class, getApplicationContext())) {
                    startService(
                            new Intent(WaybillPlanActivity.this,
                                    com.naqelexpress.naqelpointer.service.CourierNotesService.class));
                }
                sendbackData();
            }
            dbConnections.close();

            return isnotesaved;
        } else
            GlobalVar.ShowDialog(WaybillPlanActivity.this, "Error", "Please enter Notes.", true);

        return false;

    }

    private void sendbackData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "refreshdata");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void disableportion() {
        btnCallMobile.setClickable(false);
        btnCallMobile1.setClickable(false);
        btnWhatsApp.setClickable(false);
        btnWhatsApp1.setClickable(false);
        sms.setClickable(false);
        sms1.setClickable(false);
        togoogle.setClickable(false);
        txtMobileNo.setLinksClickable(false);
        // txtMobileNo.setClickable(false);
        txtPhoneNo.setLinksClickable(false);
        // txtPhoneNo.setClickable(false);


    }

}