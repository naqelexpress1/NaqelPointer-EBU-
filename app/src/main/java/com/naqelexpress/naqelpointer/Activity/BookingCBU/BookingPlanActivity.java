package com.naqelexpress.naqelpointer.Activity.BookingCBU;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

public class BookingPlanActivity
        extends AppCompatActivity
        implements OnMapReadyCallback {
    private GoogleMap mMap;
    Marker now;
    double Latitude = 0;
    double Longitude = 0;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingplan);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
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

        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.AccessFindLocation)) {
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.AccessFindLocation);
            finish();
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
    }

    public void CallConsignee(View view) {
        GlobalVar.GV().makeCall("0535638988", getWindow().getDecorView().getRootView(), BookingPlanActivity.this);
    }

    public void Delivered(View view) {
        Intent intent = new Intent(this, com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity.class);
//        intent.putExtra("WaybillNo",60038400);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        mMap.setOnMarkerClickListener(this);

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

        if (!GlobalVar.GV().checkPermission(BookingPlanActivity.this, GlobalVar.PermissionType.Camera)) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(BookingPlanActivity.this, GlobalVar.PermissionType.Camera);
        } else
            mMap.setMyLocationEnabled(true);
//        ShowShipmentMarker();
    }

//    private void ShowShipmentMarker()
//    {
////        LatLng latLng = new LatLng(24.730439, 46.821037);
////        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pickupmarker);
////        Marker marker = mMap.addMarker(new MarkerOptions()
////                .position(latLng)
////                .icon(icon)
////                .title("1708135010116"));
//    }
}
