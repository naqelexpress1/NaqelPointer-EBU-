package com.naqelexpress.naqelpointer.Activity.MyRouteCBUPaperless;

/**
 * Created by Hasna on 7/28/18.
 */

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class MapMovingOnCurLatLng extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapMovingOnCurLatLng.class.getSimpleName();
    SupportMapFragment mapFragment;
//    MapFragment mapFragment;
    private GoogleMap mMap;
    private List<LatLng> polyLineList;
    private Marker marker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition,PreviosPosition;
    private int index, next;
    private LatLng sydney;
    private Button button;
    private EditText destinationEditText;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//
//        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        polyLineList = new ArrayList<>();
        button = (Button) findViewById(R.id.destination_button);
        destinationEditText = (EditText) findViewById(R.id.edittext_place);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination = destinationEditText.getText().toString();
                destination = destination.replace(" ", "+");
                Log.d(TAG, destination);
                mapFragment.getMapAsync(MapMovingOnCurLatLng.this);
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        //final double latitude = 24.657542878415438;
        //double longitude = 46.78949257583818;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Home and move the camera
        //Added by ismail
        Location nwLocation = GlobalVar.getLastKnownLocation(getApplicationContext());
        double latitude = 0, longitude = 0;
        if (nwLocation != null) {
            latitude = nwLocation.getLatitude();
            longitude = nwLocation.getLongitude();
        }
        else{
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Problem with getting Location,kindly contact administrator", GlobalVar.AlertType.Error);
            return;}
        //
        sydney = new LatLng(latitude, longitude);
        LatLng dest = new LatLng(24.69038275,46.81903672);
        mMap.addMarker(new MarkerOptions().position(sydney).title("testing is going on"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(17)
                .bearing(30)
                .tilt(45)
                .build()));
        String requestUrl = null;
        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + latitude + "," + longitude + "&"
                    + "destination=" + dest.latitude +","+ dest.longitude + "&"
                    + "key=" + getResources().getString(R.string.google_maps_key);
            Log.d(TAG, requestUrl);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    requestUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response + "");
                            try {
                                JSONArray jsonArray = response.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);
                                    Log.d(TAG, polyLineList + "");
                                }
                                //Adjusting bounds
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList) {
                                    builder.include(latLng);
                                }
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyLine = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1)));

                                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
                                polylineAnimator.setDuration(2000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = greyPolyLine.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);
                                    }
                                });
                                polylineAnimator.start();
                                marker = mMap.addMarker(new MarkerOptions().position(sydney)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
                                handler = new Handler();
                                index = -1;
                                next = 1;
                                Location nwLocation = GlobalVar.getLastKnownLocation(getApplicationContext());
                                double latitude = 0, longitude = 0;
                                if (nwLocation != null) {
                                    latitude = nwLocation.getLatitude();
                                    longitude = nwLocation.getLongitude();
                                }
                                LatLng newPos = new LatLng(latitude, longitude);
                                PreviosPosition = newPos;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                        if (index < polyLineList.size() - 1) {
//                                            index++;
//                                            next = index + 1;
//                                        }
//
//                                        if (index < polyLineList.size() - 1) {
//                                            startPosition = polyLineList.get(index);
//                                            endPosition = polyLineList.get(next);
//                                        }

                                        Location nwLocation = GlobalVar.getLocation(getApplicationContext());
                                        double latitude = 0, longitude = 0;
                                        if (nwLocation != null) {
                                            latitude = nwLocation.getLatitude();
                                            longitude = nwLocation.getLongitude();
                                        }else
                                            return;

                                        final LatLng newPos = new LatLng(latitude, longitude);

                                        startPosition = PreviosPosition;
                                        endPosition = newPos;
                                        //double l = startPosition.latitude + 0.000120;
                                        //double ll = startPosition.longitude + 0.000120;
                                        //endPosition = new LatLng(l,ll);
                                        //endPosition = GlobalVar.getRandomLocation(startPosition, 50);


                                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                        valueAnimator.setDuration(3000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                v = valueAnimator.getAnimatedFraction();
                                                lng = v * endPosition.longitude + (1 - v)
                                                        * startPosition.longitude;
                                                lat = v * endPosition.latitude + (1 - v)
                                                        * startPosition.latitude;

                                                LatLng newPos = new LatLng(lat, lng);
                                                marker.setPosition(newPos);

                                               // marker.setPosition(newPos);
                                                marker.setAnchor(0.5f, 0.5f);
                                                marker.setRotation(getBearing(startPosition, newPos));
                                                //PreviosPosition = newPos;
                                                mMap.moveCamera(CameraUpdateFactory
                                                        .newCameraPosition
                                                                (new CameraPosition.Builder()
                                                                        .target(newPos)
                                                                        .zoom(15.5f)
                                                                        .build()));
                                            }
                                        });
                                        if (distance(startPosition.latitude, startPosition.longitude, PreviosPosition.latitude, PreviosPosition.longitude) > 0.0)
                                        valueAnimator.start();
                                        PreviosPosition = newPos;
                                        handler.postDelayed(this, 3000);
                                    }
                                }, 3000);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error + "");
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
}
