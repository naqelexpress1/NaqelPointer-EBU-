package com.naqelexpress.naqelpointer.Activity.ShipmentPicking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naqelexpress.naqelpointer.JSON.DataSync;
import com.naqelexpress.naqelpointer.R;

import static com.naqelexpress.naqelpointer.GlobalVar.AlertType;
import static com.naqelexpress.naqelpointer.GlobalVar.GV;
import static com.naqelexpress.naqelpointer.GlobalVar.PermissionType;

public class ShipmentPickingActivity
        extends AppCompatActivity
        implements OnMapReadyCallback {
    private GoogleMap mMap;
    Button btnRefresh;
    Marker now;
    double Latitude = 0;
    double Longitude = 0;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipmentpicking);

//
//        DataSync dataSync = new DataSync();
//        dataSync.GetShipmentForPicking();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShowShipmentsMarker();
                btnRefresh.setVisibility(View.INVISIBLE);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        android.location.LocationListener locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (now != null)
                    now.remove();

                Latitude = location.getLatitude();
                Longitude = location.getLongitude();

                // Creating a LatLng object for the current location
                latLng = new LatLng(Latitude, Longitude);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
                now = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(icon));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (!GV().checkPermission(ShipmentPickingActivity.this, PermissionType.Camera)) {
            GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), AlertType.Error);
            GV().askPermission(ShipmentPickingActivity.this, PermissionType.Camera);
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(ShipmentPickingActivity.this, ShipmentPickingSelectingDialog.class);
                startActivity(i);
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.shipmentpickingseletingdialog, null);
                TextView txt = (TextView) view.findViewById(R.id.txtWeight);
                double distance = CalculationByDistance(latLng, marker.getPosition());
                txt.setText(String.valueOf(distance));
                return view;
            }
        });

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        if (!GV().checkPermission(ShipmentPickingActivity.this, PermissionType.Camera)) {
            GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), AlertType.Error);
            GV().askPermission(ShipmentPickingActivity.this, PermissionType.Camera);
        } else
            mMap.setMyLocationEnabled(true);
    }

//    private void ShowShipmentsMarker()
//    {
//        LatLng latLng;
//        for (int i = 0;i < GV().GetShipmentForPickingResultList.size(); i++)
//        {
//            latLng = new LatLng(Double.parseDouble(GV().GetShipmentForPickingResultList.get(i).locationCoordinate.Latitude),
//                    Double.parseDouble(GV().GetShipmentForPickingResultList.get(i).locationCoordinate.Longitude));
//            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.deliverymarker);
//
////            try
////            {
////                Marker marker =
////                        mMap.addMarker(new MarkerOptions()
////                                .position(latLng)
////                                .icon(icon)
////                                .title(String.valueOf(GlobalVar.GV().GetShipmentForPickingResultList.get(i).WaybillNo)));
////            }
////            catch (Exception e)
////            {
////                e.printStackTrace();
////            }
//        }
//    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        float[] results = new float[5];
        Location.distanceBetween(StartP.latitude, StartP.longitude, EndP.latitude, EndP.longitude, results);

        Location locationA = new Location("point A");

        locationA.setLatitude(StartP.latitude);
        locationA.setLongitude(StartP.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(EndP.latitude);
        locationB.setLongitude(EndP.longitude);

//        double result = locationA.distanceTo(locationB);
//        return  result;

        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
//        double valueResult = Radius * c;
//        double km = valueResult / 1;
//        DecimalFormat newFormat = new DecimalFormat("####");
//        int kmInDec = Integer.valueOf(newFormat.format(km));
//        double meter = valueResult % 1000;
//        int meterInDec = Integer.valueOf(newFormat.format(meter));
//        String str = "Radius Value", "" + valueResult + "   KM  " + kmInDec          + " Meter   " + meterInDec;

        return Radius * c;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ShipmentPickingActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}