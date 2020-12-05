package com.naqelexpress.naqelpointer.Activity.GoogleMap;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naqelexpress.naqelpointer.Activity.MapList.DirectionFinder;
import com.naqelexpress.naqelpointer.Activity.MapList.DirectionFinderListener;
import com.naqelexpress.naqelpointer.Activity.MapList.Route;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ShipmentsMapActivity
        extends AppCompatActivity
        implements OnMapReadyCallback,
        DirectionFinderListener,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    double Latitude = 0;
    double Longitude = 0;
    double Zoom = 14;
    private List<MyRouteShipments> MarkerList = new ArrayList<>();
    Marker now;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    LatLng latLng;

    private final String KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE";
    private final String KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE";
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 100.0f;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipmentsmap);


//        GlobalVar.GV().makeCall("0596988144");
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        UpdateCurrentLocation();

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

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//                CameraPosition cameraPosition =  mMap.getCameraPosition();
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

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (GlobalVar.GV().checkPermission(ShipmentsMapActivity.this, GlobalVar.PermissionType.Camera) == false) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(ShipmentsMapActivity.this, GlobalVar.PermissionType.Camera);
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
        if (!ServicesOK())
            finish();

//        geofenceTransitionService = new GeofenceTransitionService();
//        Intent intent = new Intent( this, GeofenceTransitionService.class);
//        startService(intent);

        createGoogleApi();
    }

    // Create GoogleApiClient instance
    private void createGoogleApi() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

    private boolean ServicesOK() {
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, isAvailable, 9001);
            dialog.show();
        } else
            Toast.makeText(this, "Can't connect to mapping service.", Toast.LENGTH_LONG).show();

        return false;
    }

    private void DrawPath() {
        String myLocation = Latitude + "," + Longitude; //GlobalVar.GV().itemList.get(0).Latitude +"," + GlobalVar.GV().itemList.get(0).Longitude;
        String myDestination = GlobalVar.GV().myRouteShipmentList.get(GlobalVar.GV().myRouteShipmentList.size() - 1).Latitude + "," + GlobalVar.GV().myRouteShipmentList.get(GlobalVar.GV().myRouteShipmentList.size() - 1).Longitude;

        try {
            new DirectionFinder(this, myLocation, myDestination, GlobalVar.GV().myRouteShipmentList).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
//        {
//            @Override
//            public void onInfoWindowClick(Marker marker)
//            {
//                Intent intent = new Intent(ShipmentsMapActivity.this,ShipmentPickingSelectingDialog.class);
//                startActivity(intent);
//
//
//            }
//        });

//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
//        {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker)
//            {
//                Intent i = new Intent(ShipmentsMapActivity.this, ShipmentPickingSelectingDialog.class);
//                startActivity(i);
//
//                View view = getLayoutInflater().inflate(R.layout.shipmentmapinfowindow,null);
////                view.setForegroundGravity(10);
//                Button btn = (Button) view.findViewById(R.id.btnTest);
//                btn.setText(marker.getTitle());
//                Button btnCallConsignee, btnAddShipment, btnCancelShipment;
//                btnCallConsignee = (Button) view.findViewById(R.id.btnCallConsignee);
//                btnCallConsignee.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        GlobalVar.GV().makeCall("0535638988");
//                    }
//                });
//
//                return view;
//            }
//        });

        mMap.setOnMarkerClickListener(this);

        mMap.setMapType(googleMap.MAP_TYPE_HYBRID);
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

        if (GlobalVar.GV().checkPermission(ShipmentsMapActivity.this, GlobalVar.PermissionType.Camera) == false) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(ShipmentsMapActivity.this, GlobalVar.PermissionType.Camera);
        } else
            mMap.setMyLocationEnabled(true);
        ShowShipmentsMarker();
    }

    private void UpdateCurrentLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (GlobalVar.GV().checkPermission(ShipmentsMapActivity.this, GlobalVar.PermissionType.Camera) == false) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(ShipmentsMapActivity.this, GlobalVar.PermissionType.Camera);
        } else
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Latitude = location.getLatitude();
                        Longitude = location.getLongitude();
                        DrawPath();
                        LatLng myHome = new LatLng(Latitude, Longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myHome, 14));
                    }
                }
            });
    }

    private void ShowShipmentsMarker() {
        GlobalVar.GV().UserID = 1;
        for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
            LatLng latLng = new LatLng(Double.parseDouble(GlobalVar.GV().myRouteShipmentList.get(i).Latitude), Double.parseDouble(GlobalVar.GV().myRouteShipmentList.get(i).Longitude));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.deliverymarker);
            if (GlobalVar.GV().myRouteShipmentList.get(i).TypeID == 2)
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pickupmarker);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(icon)
                    .title(GlobalVar.GV().myRouteShipmentList.get(i).ItemNo));

            drawGeofence(marker);
//            mMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(icon)
//                    .title(GlobalVar.GV().itemList.get(i).ItemNo));
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.", "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routess) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route1 : routess) {
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.currentlocation))
                    .title(route1.startAddress)
                    .position(route1.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination))
                    .title(route1.endAddress)
                    .position(route1.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route1.points.size(); i++)
                polylineOptions.add(route1.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this,
                marker.getTitle() + ":" + marker.getId() + " : " + marker.getAlpha() +
                        " has been clicked " + 1 + " times.",
                Toast.LENGTH_SHORT).show();
        return false;
    }


    private Circle geoFenceLimits;

    private void drawGeofence(Marker geoFenceMarker) {
//        if ( geoFenceLimits != null )
//            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center(geoFenceMarker.getPosition())
                .strokeColor(Color.RED)
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(GEOFENCE_RADIUS);
        geoFenceLimits = mMap.addCircle(circleOptions);
    }

    private void removeGeofenceDraw(Marker geoFenceMarker) {
        if (geoFenceMarker != null)
            geoFenceMarker.remove();
        if (geoFenceLimits != null)
            geoFenceLimits.remove();
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent createGeofencePendingIntent() {
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

//    private Marker geoFenceMarker;
//    private void markerForGeofence(LatLng latLng)
//    {
//        String title = latLng.latitude + ", " + latLng.longitude;
//        // Define marker options
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(latLng)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                .title(title);
//        if ( mMap!=null )
//        {
//            // Remove last geoFenceMarker
//            if (geoFenceMarker != null)
//                geoFenceMarker.remove();
//
//            geoFenceMarker = mMap.addMarker(markerOptions);
//        }
//    }

    // Check for permission to access Location
    private boolean checkPermission() {
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
//            saveGeofence();
//            drawGeofence();
        } else {
            // inform about fail
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastKnownLocation();
    }

    // Get last known location
    private void getLastKnownLocation() {
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                writeLastLocation();
//                startLocationUpdates();
            } else {
//                startLocationUpdates();
            }
        } else askPermission();
    }

    private final int REQ_PERMISSION = 999;

    // Asks for permission
    private void askPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private void writeActualLocation(Location location) {
//        textLat.setText( "Lat: " + location.getLatitude() );
//        textLong.setText( "Long: " + location.getLongitude() );

        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private Marker locationMarker;

    private void markerLocation(LatLng latLng) {
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if (mMap != null) {
            if (locationMarker != null)
                locationMarker.remove();
            locationMarker = mMap.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}