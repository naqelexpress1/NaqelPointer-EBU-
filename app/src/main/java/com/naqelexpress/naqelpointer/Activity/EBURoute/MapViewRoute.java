package com.naqelexpress.naqelpointer.Activity.EBURoute;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.naqelexpress.naqelpointer.Activity.routeMap.DirectionsJSONParser;
import com.naqelexpress.naqelpointer.Activity.routeMap.Places;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class MapViewRoute extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    View rootView;
    MapView mMapView;
    private GoogleMap mMap;
    ArrayList<Location> places = new ArrayList<>();
    ArrayList<MyRouteShipments> myRouteShipmentList;
    ArrayList<String> jsonroute = new ArrayList<>();
    private Marker myMarker[];
    ArrayList<Integer> colors_marker_route = new ArrayList<>();
    ArrayList<HashMap<String, String>> distance_time = new ArrayList<>();

    protected void displayReceivedData() {

        LoadMyRouteShipments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.mapview_ebu, container, false);

            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            myRouteShipmentList = new ArrayList<>();


            mMapView.onResume();


            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            mMapView.getMapAsync(new OnMapReadyCallback() {
//                @Override
//                public void onMapReady(GoogleMap mMap) {
//                    googleMap = mMap;
//
//                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                            == PackageManager.PERMISSION_GRANTED)
//                        googleMap.setMyLocationEnabled(true);
//
//                    // For dropping a marker at a point on the Map
//                    LatLng sydney = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());
//                    googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
//
//                    // For zooming automatically to the location of the marker
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                }
//            });

            if (DeliverySheet.call == 1)
                displayReceivedData();

        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LoadMap(googleMap);

    }

    public static List<Location> sortLocations(List<Location> locations, final double myLatitude, final double myLongitude) {
        Comparator comp = new Comparator<Location>() {
            @Override
            public int compare(Location o, Location o2) {
                float[] result1 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, o.getLatitude(), o.getLongitude(), result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, o2.getLatitude(), o2.getLongitude(), result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        };


        Collections.sort(locations, comp);
        return locations;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if ((Integer) marker.getTag() != 0)
            custom_alert((Integer) marker.getTag());
        else
            GlobalVar.GV().ShowSnackbar(rootView, "This is Starting Point.", GlobalVar.AlertType.Error);

    }

    private void custom_alert(int position) {

        try {
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.map_customalert, null);
            ImageView bm = (ImageView) alertLayout.findViewById(R.id.imageview_account_profile);
            final TextView consignname = alertLayout.findViewById(R.id.consigneename);
            final EditText mobileno = alertLayout.findViewById(R.id.mobileno);
            final EditText mobileno1 = alertLayout.findViewById(R.id.mobileno1);
            consignname.setText(GlobalVar.GV().myRouteShipmentList.get(position - 1).ConsigneeName);
            if (GlobalVar.GV().myRouteShipmentList.get(position - 1).ConsigneeMobile.length() > 0 && !GlobalVar.GV().myRouteShipmentList.get(position - 1).ConsigneeMobile.equals("null"))
                mobileno.setText(GlobalVar.GV().myRouteShipmentList.get(position - 1).ConsigneeMobile);
            else
                mobileno.setVisibility(View.GONE);
            if (GlobalVar.GV().myRouteShipmentList.get(position - 1).ConsigneePhoneNumber.length() > 0 && !GlobalVar.GV().myRouteShipmentList.get(position - 1).ConsigneePhoneNumber.equals("null"))
                mobileno1.setText(GlobalVar.GV().myRouteShipmentList.get(position - 1).ConsigneePhoneNumber);
            else
                mobileno1.setVisibility(View.GONE);

            BitmapDrawable ob = new BitmapDrawable(getResources(), makeBitmap(getContext(), String.valueOf(position), colors_marker_route.get(position)));
            bm.setBackgroundDrawable(ob);

            mobileno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(mobileno.getText().toString(), rootView, getActivity());
                }
            });
            mobileno1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(mobileno1.getText().toString(), rootView, getActivity());
                }
            });

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Info");
            // this is set the view from XML inside AlertDialog
            alert.setView(alertLayout);
            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            dialog.getWindow().setLayout(width - 100, height - 300);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String position = "";

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                position = jsonData[1];
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
                jsonroute.add(jObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (result.size() > 0 && result != null)
                drawpolyline(result, position);
        }
    }

    private void drawpolyline(List<List<HashMap<String, String>>> result, String pos) {
        ArrayList points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(10);

            lineOptions.color(colors_marker_route.get(Integer.parseInt(pos)));

            lineOptions.geodesic(true);

        }

        try {
            System.out.println(distance_time);
            mMap.addPolyline(lineOptions);
            //  ShowShipmentMarker_byone(color,position);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void ShowShipmentMarker() {

        for (int i = 0; i < places.size(); i++) {

            Random random = new Random();

            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            Bitmap bmp = makeBitmap(getContext(), String.valueOf(i), color);
            // Canvas canvas = new Canvas(bmp);

            // canvas.drawText(String.valueOf(i+1), 0, 50, paint);
            colors_marker_route.add(color);
            LatLng latLng = new LatLng(places.get(i).getLatitude(), places.get(i).getLongitude());
            ;
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dest_marker);
            myMarker[i] = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
            //.title("Mohamed Ismail"));
            myMarker[i].setTag(i);
        }

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        String position = "";

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
                position = url[1];
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equals("")) {
                ParserTask parserTask = new ParserTask();
                parserTask.execute(result, position);
            }

        }
    }

    public Bitmap makeBitmap(Context context, String text, int color) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        SharedPreferences pref = getContext().getSharedPreferences("naqelSettings", 0);
        boolean multiple_not = pref.getBoolean("mc", true);
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_60);
        if (text.equals("0")) {
            text = "";
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_start);
        }


        bitmap = bitmap.copy(ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        if (multiple_not && text.length() != 0) {
            Paint paint_bm = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint_bm.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, 0, 0, paint_bm);
        }


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED); // Text color
        paint.setTextSize(14 * scale); // Text size
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = bitmap.getWidth() - bounds.width() - 10; // 10 for padding from right
        int y = bounds.height();
        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    public void LoadMyRouteShipments() {

        places.clear();

        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location location = GlobalVar.getLastKnownLocation(getContext());
            if (location != null)

                places.add(location);

        }

        DBConnections dbConnections = new DBConnections(getContext(), null);

        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where DDate = '" +
                GlobalVar.getDate() + "' and EmpID = " + GlobalVar.GV().EmployID, getContext());
        if (result.getCount() > 0) {

            dbConnections.InsertOFD(result.getCount(), GlobalVar.getDate(), getContext());

            myRouteShipmentList = new ArrayList<>();

            result.moveToFirst();
            do {
                MyRouteShipments myRouteShipments = new MyRouteShipments();
//                myRouteShipments.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                myRouteShipments.BillingType = result.getString(result.getColumnIndex("BillingType"));
//                myRouteShipments.OrderNo = Integer.parseInt(result.getString(result.getColumnIndex("OrderNo")));
//                myRouteShipments.ItemNo = result.getString(result.getColumnIndex("ItemNo"));
//
//                myRouteShipments.TypeID = Integer.parseInt(result.getString(result.getColumnIndex("TypeID")));
//                myRouteShipments.CODAmount = Double.parseDouble(result.getString(result.getColumnIndex("CODAmount")));
//                myRouteShipments.DeliverySheetID = Integer.parseInt(result.getString(result.getColumnIndex("DeliverySheetID")));
//                myRouteShipments.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
//                myRouteShipments.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("ExpectedTime")));

                myRouteShipments.Latitude = result.getString(result.getColumnIndex("Latitude"));
                myRouteShipments.Longitude = result.getString(result.getColumnIndex("Longitude"));
                if ((myRouteShipments.Latitude.length() > 0 && myRouteShipments.Longitude.length() > 0) &&
                        !myRouteShipments.Latitude.equals("null") && !myRouteShipments.Longitude.equals("null")) {
                    Location sp = new Location("");
                    try {

                        sp.setLatitude(Double.parseDouble(myRouteShipments.Latitude));
                        sp.setLongitude(Double.parseDouble(myRouteShipments.Longitude));
                    } catch (Exception e) {
                        sp.setLatitude(0);
                        sp.setLongitude(0);
                    }

                    places.add(sp);
                }

//                myRouteShipments.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
//                myRouteShipments.ClientName = result.getString(result.getColumnIndex("ClientName"));
//                myRouteShipments.ClientFName = result.getString(result.getColumnIndex("ClientFName"));
//                myRouteShipments.ClientAddressPhoneNumber = result.getString(result.getColumnIndex("ClientAddressPhoneNumber"));
//                myRouteShipments.ClientAddressFirstAddress = result.getString(result.getColumnIndex("ClientAddressFirstAddress"));
//                myRouteShipments.ClientAddressSecondAddress = result.getString(result.getColumnIndex("ClientAddressSecondAddress"));
//                myRouteShipments.ClientContactName = result.getString(result.getColumnIndex("ClientContactName"));
//                myRouteShipments.ClientContactFName = result.getString(result.getColumnIndex("ClientContactFName"));
//                myRouteShipments.ClientContactPhoneNumber = result.getString(result.getColumnIndex("ClientContactPhoneNumber"));
//                myRouteShipments.ClientContactMobileNo = result.getString(result.getColumnIndex("ClientContactMobileNo"));
//                myRouteShipments.ConsigneeName = result.getString(result.getColumnIndex("ConsigneeName"));
//                myRouteShipments.ConsigneeFName = result.getString(result.getColumnIndex("ConsigneeFName"));
//                myRouteShipments.ConsigneePhoneNumber = result.getString(result.getColumnIndex("ConsigneePhoneNumber"));
//                myRouteShipments.ConsigneeFirstAddress = result.getString(result.getColumnIndex("ConsigneeFirstAddress"));
//                myRouteShipments.ConsigneeSecondAddress = result.getString(result.getColumnIndex("ConsigneeSecondAddress"));
//                myRouteShipments.ConsigneeNear = result.getString(result.getColumnIndex("ConsigneeNear"));
//                myRouteShipments.ConsigneeMobile = result.getString(result.getColumnIndex("ConsigneeMobile"));
//                myRouteShipments.Origin = result.getString(result.getColumnIndex("Origin"));
//                myRouteShipments.Destination = result.getString(result.getColumnIndex("Destination"));
//                myRouteShipments.PODNeeded = Boolean.parseBoolean(result.getString(result.getColumnIndex("PODNeeded")));
//                myRouteShipments.PODDetail = result.getString(result.getColumnIndex("PODDetail"));
//                myRouteShipments.PODTypeCode = result.getString(result.getColumnIndex("PODTypeCode"));
//                myRouteShipments.PODTypeName = result.getString(result.getColumnIndex("PODTypeName"));
//                myRouteShipments.IsDelivered = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsDelivered")));
//                myRouteShipments.NotDelivered = Boolean.parseBoolean(result.getString(result.getColumnIndex("NotDelivered")));
//                myRouteShipments.CourierDailyRouteID = Integer.parseInt(result.getString(result.getColumnIndex("CourierDailyRouteID")));
//                myRouteShipments.OptimzeSerialNo = Integer.parseInt(result.getString(result.getColumnIndex("OptimzeSerialNo")));
//                myRouteShipments.HasComplaint = result.getInt(result.getColumnIndex("HasComplaint")) > 0;
//                myRouteShipments.HasDeliveryRequest = Boolean.parseBoolean(result.getString(result.getColumnIndex("CourierDailyRouteID")));

                //kpi.add(myRouteShipments);
            }
            while (result.moveToNext());

        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {

                LoadMap(map);

            }
        });

        dbConnections.close();

    }

    private void LoadMap(GoogleMap googleMap) {
        mMap = googleMap;
        GlobalVar.GV().ChangeMapSettings(mMap, getActivity(), rootView);


        LatLng ll = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 8));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if ((Integer) marker.getTag() != 0)
                    custom_alert((Integer) marker.getTag());
                else
                    GlobalVar.GV().ShowSnackbar(rootView, "This is Starting Point.", GlobalVar.AlertType.Error);
            }
        });

        myMarker = new Marker[places.size()];

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                View v = null;
                try {

                    // Getting view from the layout file info_window_layout
                    v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                    // Getting reference to the TextView to set latitude
                    TextView addressTxt = (TextView) v.findViewById(R.id.address);
                    addressTxt.setText(arg0.getTitle());

                } catch (Exception ev) {
                    System.out.print(ev.getMessage());
                }

                return v;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = (int) (marker.getTag());
                // if (distance_time.size() > 0) {

                // LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                    Location location = GlobalVar.getLastKnownLocation(getContext());

                    //LatLng ll = places.get(position).ge;
                    String uri = "http://maps.google.com/maps?saddr=" + location.getLatitude() + "," + location.getLongitude()
                            + "&daddr=" + places.get(position).getLatitude() + "," + places.get(position).getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }

                return false;
            }
        });

        if (jsonroute.size() == 0) {
            ArrayList<Places> temp_places = new ArrayList<>();
            ArrayList<Location> sp = new ArrayList<>();
            int j = 0;
            //temp_places.add(places.get(0));
            //places.remove(0);
            if (places.size() > 1) {
                while (places.size() != 0) {

                    ArrayList<Location> temp = places;
                    //ll = temp.get(0).latlng;
                    List<Location> sortpath = sortLocations(temp, temp.get(0).getLatitude(), temp.get(0).getLongitude());
                    //ArrayList<Places> sortpath = sortLocations(temp, ll.latitude, ll.longitude);

                    if (places.size() == 2) {
//                        temp_places.add(sortpath.get(0));
//                        temp_places.add(sortpath.get(1));
                        sp.add(sortpath.get(0));
                        sp.add(sortpath.get(1));
                        places.clear();
                    } else {
//                        temp_places.add(sortpath.get(0));
                        sp.add(sortpath.get(0));
                        places.remove(0);

                    }
                    j += 1;

                }
            }
            places.addAll(sp);

            ShowShipmentMarker();

            if (places.size() > 1) {
                for (int i = 0; i < places.size(); i++) {

                    LatLng origin = new LatLng(places.get(i).getLatitude(), places.get(i).getLongitude());
                    LatLng dest = new LatLng(places.get(i + 1).getLatitude(), places.get(i + 1).getLongitude());
                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url, String.valueOf(i));

                    if ((i == places.size() - 2) && (places.size() != 2)) {
                        origin = new LatLng(places.get(i - 1).getLatitude(), places.get(i - 1).getLongitude());
                        dest = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());
                        url = getDirectionsUrl(origin, dest);
                        downloadTask = new DownloadTask();
                        downloadTask.execute(url, String.valueOf(places.size() - 1));
                        break;
                    }
                    if (i == places.size() - 2)
                        break;


                }
            }
        } else {
            ShowShipmentMarker();
            int i = 0;
            for (String json : jsonroute) {
                try {
                    List<List<HashMap<String, String>>> routes = null;
                    JSONObject jObject = new JSONObject(json);

                    DirectionsJSONParser parser = new DirectionsJSONParser();
//                    String position = "";
                    routes = parser.parse(jObject);
                    drawpolyline(routes, String.valueOf(i));
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        }
    }
}