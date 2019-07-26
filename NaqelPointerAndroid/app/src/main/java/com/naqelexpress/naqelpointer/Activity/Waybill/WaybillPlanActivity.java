package com.naqelexpress.naqelpointer.Activity.Waybill;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naqelexpress.naqelpointer.Classes.ConsingeeMobileSpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

public class WaybillPlanActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private GoogleMap mMap;
    Marker now;
    TextView txtWaybillNo, txtShipperName, txtConsigneeName, txtMobileNo, txtBillingType, txtCODAmount, txtPODType, txtPhoneNo;
    TextView lbPODType;
    ConsingeeMobileSpinnerDialog spinnerDialog;
    //Button btnDelivered, btnNotDeliverd, btnCall;
    private Bundle bundle;
    MyRouteShipments myRouteShipments;
    String ConsigneeLatitude, ConsigneeLongitude;
    SupportMapFragment mapFragment;
    public double Latitude = 0;
    public double Longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.waybillplan);
        bundle = getIntent().getExtras();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                GlobalVar.GV().makeCall(txtMobileNo.getTag().toString(), getWindow().getDecorView().getRootView(), WaybillPlanActivity.this);
            }
        });
        btnCallMobile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVar.GV().makeCall(txtPhoneNo.getTag().toString(), getWindow().getDecorView().getRootView(), WaybillPlanActivity.this);
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
                    GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg)
                            + " " + txtWaybillNo.getText().toString() + "." + getString(R.string.watsappPredefinedMsg1)
                            + txtWaybillNo.getText().toString(), getApplicationContext());
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
                    GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg)
                            + " " + txtWaybillNo.getText().toString() + "." + getString(R.string.watsappPredefinedMsg1)
                            + txtWaybillNo.getText().toString(), getApplicationContext());
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


//        if (!GlobalVar.GV().checkPermission(WaybillPlanActivity.this, GlobalVar.PermissionType.AccessFindLocation)) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedLocationPermision), GlobalVar.AlertType.Error);
//            GlobalVar.GV().askPermission(WaybillPlanActivity.this, GlobalVar.PermissionType.AccessFindLocation);
//        }
    }

    private void setSavedInstance(Bundle savedInstanceState) {
        GlobalVar.GV().myRouteShipmentList = savedInstanceState.getParcelableArrayList("myRouteShipmentList");
    }

    private void requestLocation() {
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
        Intent intent = new Intent(this, com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void NotDelivered() {
        Intent intent = new Intent(this, com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GlobalVar.GV().ChangeMapSettings(mMap, WaybillPlanActivity.this, getWindow().getDecorView().getRootView());

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
        now = mMap.addMarker(new MarkerOptions().position(GlobalVar.GV().currentLocation)
                .icon(icon)
                .title(getString(R.string.MyLocation)));

        ShowShipmentMarker();
    }

    private void ShowShipmentMarker() {
        if (ConsigneeLongitude.length() > 3 && ConsigneeLongitude.length() > 3) {
            LatLng latLng = new LatLng(GlobalVar.GV().getDoubleFromString(ConsigneeLatitude), GlobalVar.GV().getDoubleFromString(ConsigneeLongitude));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.deliverymarker);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(icon)
                    .title(txtWaybillNo.getText().toString()));
        }
//        else
//            mapFragment.getView().setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("myRouteShipmentList", GlobalVar.GV().myRouteShipmentList);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        GlobalVar.GV().myRouteShipmentList = savedInstanceState.getParcelableArrayList("myRouteShipmentList");
        GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
        GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
        GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
        GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
        GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
        GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
        GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
        GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
    }
}