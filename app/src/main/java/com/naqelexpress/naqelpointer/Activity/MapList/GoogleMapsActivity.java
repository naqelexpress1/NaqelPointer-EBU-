package com.naqelexpress.naqelpointer.Activity.MapList;

//import org.apache.http.client.methods.HttpPost;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback, DirectionFinderListener {
    private GoogleMap mMap;
    Marker now;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //sendRequest();
//        try
//        {
//            //Preparing The Data
//            data.MobileNo = "966535638988";
//            data.AppTypeID = 1;
//            data.AppVersion = "1.0";
//            data.LanguageID = 1;
//
//            JSONObject obj = new JSONObject();
//
//            obj.put("MobileNo",data.MobileNo.toString());
//            obj.put("AppVersion",data.AppVersion.toString());
//            obj.put("LanguageID",data.LanguageID.toString());
//            obj.put("AppTypeID",data.AppTypeID.toString());
//
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
    }

    private void sendRequest() {
//        String origin = etOrigin.getText().toString();
//        String destination = etDestination.getText().toString();
//        if (origin.isEmpty()) {
//            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (destination.isEmpty()) {
//            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        //LatLng myHome = new LatLng(24.692445, 46.839279);
        //LatLng myWork = new LatLng(1,1);

        String myHome = "24.692445, 46.839279";
        String myWork = "24.655291, 46.789706";

//        try
//        {
//            new DirectionFinder(this, myHome,myWork).execute();
//        }
//        catch (UnsupportedEncodingException e)
//        {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(googleMap.MAP_TYPE_SATELLITE);

        // Add a marker in Sydney and move the camera
        LatLng myHome = new LatLng(24.692445, 46.839279);
        //mMap.addMarker(new MarkerOptions().position(myHome).title("Marker in Sydney"));
//        mMap.addMarker(new MarkerOptions()
//                            .position(myHome)
//                            .title("Location Details")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

        LatLng myWork = new LatLng(1, 1);


        //Get Current Location
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//
//        Location location = locationManager.getLastKnownLocation(locationManager
//                .getBestProvider(criteria, false));
//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();

        //http://maps.googleapis.com/maps/api/directions/json?origin=24.692445,%2046.839279&destination=24.657279,%2046.788840&sensor=false&units=metric&mode=driving
        //mMap.addPolyline(new PolylineOptions().add(myHome,myWork).width(10).color(getResources().getColor(R.color.NaqelRed,null)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myHome, 18));
        if (GlobalVar.GV().checkPermission(GoogleMapsActivity.this, GlobalVar.PermissionType.Camera) == false) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(GoogleMapsActivity.this, GlobalVar.PermissionType.Camera);
        } else
            mMap.setMyLocationEnabled(true);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myHome, 18));
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

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
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 14));
            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

//    public void postData(String valueIWantToSend) {
//        HttpClient httpclient = new DefaultHttpClient();
//        // specify the URL you want to post to
//        HttpPost httppost = new HttpPost("http://somewebsite.com/receiver.php");
//        try {
//            // create a list to store HTTP variables and their values
//            List nameValuePairs = new ArrayList();
//            // add an HTTP variable and value pair
//            nameValuePairs.add(new BasicNameValuePair("myHttpData", valueIwantToSend));
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            // send the variable and value, in other words post, to the URL
//            HttpResponse response = httpclient.execute(httppost);
//        } catch (ClientProtocolException e) {
//            // process execption
//        } catch (IOException e) {
//            // process execption
//        }
//    }

    public void FindPath(View view) {
        sendRequest();
    }

    //@Override
    public void onLocationChanged(Location location) {
        if (now != null) {
            now.remove();
        }

        //TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        now = mMap.addMarker(new MarkerOptions().position(latLng));
        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}