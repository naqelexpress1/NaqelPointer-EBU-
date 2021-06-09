package com.naqelexpress.naqelpointer.Activity.routeMap;

import android.Manifest;
import android.app.ActivityManager;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static com.itextpdf.awt.geom.Point2D.distance;

/**
 * Created by Hasna on 7/21/18.
 */

public class OptimiseMapbyDistrict extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    SupportMapFragment mapFragment;
    //MapFragment mapFragment;
    private GoogleMap mMap;

    private ArrayList<MyRouteShipments> myRouteShipmentList;
    //static ArrayList<Places> places; // = new ArrayList<>();
    ArrayList<Integer> colors_marker_route = new ArrayList<>();
    public static ArrayList<Location> places = new ArrayList<>();

    private Marker myMarker[];
    boolean Issuggest = false;
    public static ArrayList<HashMap<String, String>> distance_time = new ArrayList<>();
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.routemap);

        Bundle extras = getIntent().getExtras();
        myRouteShipmentList = extras.getParcelableArrayList("myroute");
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        GetSeqWaybillNo();


        RouteMap.distance_time.clear();

        ImageButton distance = (ImageButton) findViewById(R.id.viewmore);
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intetn = new Intent(OptimiseMapbyDistrict.this, RouteMap_Distance.class);
                startActivity(intetn);
            }
        });

        ImageButton riderct_GoogleMap = (ImageButton) findViewById(R.id.googlemap);
        riderct_GoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                ArrayList<Location> location = dbConnections.GetLocation(getApplicationContext());

                if (places.size() > 0 && location.size() > 0) {

                    String api = "http://maps.google.com/maps?saddr=";
                    StringBuilder sb = new StringBuilder();
                    sb.append(api);
                    int i = 0;
                    sb.append(places.get(0).getLatitude() + "," + places.get(0).getLongitude());
                    for (Location ll : location) {

                        if (i == 0) {
                            sb.append("&daddr=");
                        } else
                            sb.append("+to:");
                        sb.append(ll.getLatitude() + "," + ll.getLongitude());
                        i++;

                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }

            }
        });
        // places = (ArrayList<Places>) getIntent().getSerializableExtra("places");
        //  System.out.println(places);

        //  kpi = bundle.getParcelable("routemap");
        //  System.out.println("test");

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Optimizing routes kindly please wait");
        pDialog.setCancelable(false);
        pDialog.show();

        myMarker = new Marker[places.size()];

//        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        dbConnections.DeleteAllSuggestPlannedLocation(getApplicationContext());

        GetPlannedLocation();

    }

    boolean isMapReady = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GlobalVar.GV().ChangeMapSettings(mMap, OptimiseMapbyDistrict.this, getWindow().getDecorView().getRootView());


        LatLng ll = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 8));
        mMap.setOnInfoWindowClickListener(OptimiseMapbyDistrict.this);
        isMapReady = true;

//        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//            // Use default InfoWindow frame
//            @Override
//            public View getInfoWindow(Marker arg0) {
//                return null;
//            }
//
//            // Defines the contents of the InfoWindow
//            @Override
//            public View getInfoContents(Marker arg0) {
//                View v = null;
//                try {
//
//                    // Getting view from the layout file info_window_layout
//                    v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
//
//                    // Getting reference to the TextView to set latitude
//                    TextView addressTxt = (TextView) v.findViewById(R.id.address);
//                    addressTxt.setText(arg0.getTitle());
//
//                } catch (Exception ev) {
//                    System.out.print(ev.getMessage());
//                }
//
//                return v;
//            }
//        });

        //#startOrigin
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = (int) (marker.getTag());
                // if (distance_time.size() > 0) {

                // LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(OptimiseMapbyDistrict.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(OptimiseMapbyDistrict.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                    //  Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    //   double longitude = location.getLongitude();
                    //    double latitude = location.getLatitude();


                    if (position != 0)
                        custom_alert((Integer) marker.getTag(), Integer.parseInt(marker.getSnippet()));
                    else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "This is Starting Point.", GlobalVar.AlertType.Error);
//                    Location location = GlobalVar.getLastKnownLocation(getApplicationContext());

                    //LatLng ll = places.get(position).ge;
//                    String uri = "http://maps.google.com/maps?saddr=" + location.getLatitude() + "," + location.getLongitude()
//                            + "&daddr=" + places.get(position).getLatitude() + "," + places.get(position).getLongitude();
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                    intent.setPackage("com.google.android.apps.maps");
//                    startActivity(intent);
                }


                //myMarker[position].setTitle(distance_time.get(position).get("address"));
                //myMarker[position].setSnippet(distance_time.get(position).get("address"));
                //  myMarker[position].showInfoWindow();
                //   } else
                //        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "please wait the route is loading", GlobalVar.AlertType.Error);
                return false;
            }
        });
        //#endOrigin
//        double lat = places.get(0).latlng.latitude;
//        double lng = places.get(0).latlng.longitude;
//        LatLng latLng = new LatLng(lat, lng);


        //ArrayList<Places> bow =  sortLocations(places, lat, lng);
        // places.remove(0);


        //if (jsonroute.size() == 0) {

       /* //#startRegion
        if (!Issuggest) {

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
        }
        //EndRegion*/
//        if (isoptimise)
        ShowShipmentMarker();

//        GetPlannedLocation();

//        if (!IsPlanned) {
//            if (places.size() > 1) {
//                for (int i = 0; i < places.size(); i++) {
//
//                    LatLng origin = new LatLng(places.get(i).getLatitude(), places.get(i).getLongitude());
//                    LatLng dest = new LatLng(places.get(i + 1).getLatitude(), places.get(i + 1).getLongitude());
//                    // Getting URL to the Google Directions API
//                    String url = getDirectionsUrl(origin, dest);
//
//
//                    DownloadTask downloadTask = new DownloadTask();
//                    // Start downloading json data from Google Directions API
////                    downloadTask.execute(url, String.valueOf(i));
//
//                    if ((i == places.size() - 2) && (places.size() != 2)) {
//                        origin = new LatLng(places.get(i - 1).getLatitude(), places.get(i - 1).getLongitude());
//                        dest = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());
//                        url = getDirectionsUrl(origin, dest);
//                        downloadTask = new DownloadTask();
////                        downloadTask.execute(url, String.valueOf(places.size() - 1));
//
//                        break;
//                    }
//                    if (i == places.size() - 2) {
//                        if (pDialog != null && pDialog.isShowing())
//                            pDialog.dismissWithAnimation();
//                        break;
//                    }
//
//
//                }
//            }
//        }
        // }

//        else {
//            ShowShipmentMarker();
//            int i = 0;
//            for (String json : jsonroute) {
//                try {
//                    List<List<HashMap<String, String>>> routes = null;
//                    JSONObject jObject = new JSONObject(json);
//
//                    DirectionsJSONParser parser = new DirectionsJSONParser();
////                    String position = "";
//                    routes = parser.parse(jObject);
//                    drawpolyline(routes, String.valueOf(i));
//                    i++;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        }

//        if (pDialog != null && pDialog.isShowing())
//            pDialog.dismissWithAnimation();

    }

    public static List<Location> sortLocations(List<Location> locations, final double myLatitude, final double myLongitude) {
        Comparator comp = new Comparator<Location>() {
            @Override
            public int compare(Location o, Location o2) {
                float[] result1 = new float[3];
                Location.distanceBetween(myLatitude, myLongitude, o.getLatitude(), o.getLongitude(), result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                Location.distanceBetween(myLatitude, myLongitude, o2.getLatitude(), o2.getLongitude(), result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        };


        Collections.sort(locations, comp);
        return locations;
    }

    static int i = 0;

    public static ArrayList<Places> sortLocations(ArrayList<Places> locations, final double myLatitude, final double myLongitude) {

        Comparator comp = new Comparator<ArrayList<Places>>() {
            @Override
            public int compare(ArrayList<Places> o, ArrayList<Places> o2) {
                float[] result1 = new float[3];
                Location.distanceBetween(myLatitude, myLongitude, o.get(i).latlng.latitude, o.get(i).latlng.latitude, result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                Location.distanceBetween(myLatitude, myLongitude, o2.get(i).latlng.latitude, o2.get(i).latlng.latitude, result2);
                Float distance2 = result2[0];
                i++;
                return distance1.compareTo(distance2);
            }


        };


        Collections.sort(locations, comp);
        return locations;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void CalltoConsign(View view) {
        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "please wait the route is loading", GlobalVar.AlertType.Error);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        if ((Integer) marker.getTag() != 0)
//            custom_alert((Integer) marker.getTag());
//        else
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "This is Starting Point.", GlobalVar.AlertType.Error);

    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        String position = "", limitLocation = "";

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
                position = url[1];
                limitLocation = url[2];

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equals("")) {
                String wno;
//                if (places.size() - 1 == Integer.parseInt(position)) {
//
//                    wno = "0";
//
//                } else
//                    wno = myRouteShipmentList.get((int) places.get(i).getSpeed() + 1).ItemNo;
//
//                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//                dbConnections.InsertPlannedLocation(getApplicationContext(), result, Integer.parseInt(position), Integer.parseInt(wno));
//                dbConnections.close();

                ParserTask parserTask = new ParserTask();
                parserTask.execute(result, position, limitLocation);

            }

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    ArrayList<String> jsonroute = new ArrayList<>();


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String position = "", limitLocation = "";

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                position = jsonData[1];
                limitLocation = jsonData[2];
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject, position, getBaseContext(), 0);
                jsonroute.add(jObject.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (result.size() > 0 && result != null) {
                drawpolyline(result, position);


                isoptimise = true;
                if (isMapReady)
                    ShowShipmentMarker();
            }
            pDialog.dismissWithAnimation();
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
            lineOptions.width(2);

            //lineOptions.color(Color.RED);
            //lineOptions.color(color);
            //#startRegion
//            lineOptions.color(colors_marker_route.get(Integer.parseInt(pos)));
            //EndRegion

            lineOptions.color(Color.BLUE);
            lineOptions.geodesic(true);

        }

// Drawing polyline in the Google Map for the i-th route
        try {
//            System.out.println(distance_time);
            mMap.addPolyline(lineOptions);
            //  ShowShipmentMarker_byone(color,position);
        } catch (Exception e) {
            System.out.println(e);
        }

//        if (Integer.parseInt(pos) == places.size() - 2 || Integer.parseInt(pos) == places.size() - 1 || Integer.parseInt(pos) == places.size()) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismissWithAnimation();

        stopService(
                new Intent(this,
                        com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class));
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class)) {
            startService(
                    new Intent(this,
                            com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class));
        }

//        if (!IsPlanned) {
//            Intent returnIntent = new Intent();
//            //returnIntent.putExtra("result", "refreshdata");
//            returnIntent.putExtra("result", "finish");
//            setResult(Activity.RESULT_OK, returnIntent);
//            finish();
//        }
//        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getString(R.string.google_maps_key_forrute);
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

        try {
            //Location location = new Location();
            double prvLat = 0.0;
            double preLng = 0.0;
            String data = "";
            for (int i = 0; i < places.size(); i++) {

                Random random = new Random();

                int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                Bitmap bmp = makeBitmap(getApplicationContext(), String.valueOf(i), color);
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
                myMarker[i].setTag((int) places.get(i).getSpeed());
                myMarker[i].setSnippet(String.valueOf(i));


                //double distanceToPlace1 = distance(prvLat, preLng, latLng.latitude , latLng.longitude);
                if (i == 0)
                    data = "Stratingplace_" + String.valueOf(places.get(i).getLatitude()) + "_" + String.valueOf(places.get(i).getLongitude() + "_0_0_0");
                else
                    data = data + "@" + myRouteShipmentList.get((int) places.get(i).getSpeed() - 1).ItemNo + "_" + String.valueOf(places.get(i).getLatitude()) + "_" + String.valueOf(places.get(i).getLongitude() + "_"
                            + String.valueOf(distance(prvLat, preLng, latLng.latitude, latLng.longitude)) + "_" + String.valueOf(i) + "_" + String.valueOf((int) places.get(i).getSpeed()));
//                data = data + "@" + String.valueOf((int) places.get(i).getAltitude() - 1) + "_" + String.valueOf(places.get(i).getLatitude()) + "_" + String.valueOf(places.get(i).getLongitude() + "_"
//                        + String.valueOf(distance(prvLat, preLng, latLng.latitude, latLng.longitude)) + "_" + String.valueOf(i));

                prvLat = latLng.latitude;
                preLng = latLng.longitude;

                if (i == places.size() - 1 && !Issuggest) {
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    dbConnections.InsertSuggestLocation(getApplicationContext(), data);
                    dbConnections.close();
                }

            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void ShowShipmentMarker_byone(int color, String position) {
        Bitmap bmp = makeBitmap(getApplicationContext(), position, color);
        //colors_marker_route.add(color);

        LatLng latLng = new LatLng(places.get(Integer.parseInt(position)).getLatitude(), places.get(Integer.parseInt(position)).getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .title(RouteMap.distance_time.get(Integer.parseInt(position)).get("address")));


    }

    public Bitmap makeBitmap(Context context, String text, int color) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("naqelSettings", 0);
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

    public class SortPlaces implements Comparator<Location> {
        LatLng currentLoc;

        public SortPlaces(LatLng current) {
            currentLoc = current;
        }

        @Override
        public int compare(final Location place1, final Location place2) {
            double lat1 = place1.getLatitude();
            double lon1 = place1.getLongitude();
            double lat2 = place2.getLatitude();
            double lon2 = place2.getLongitude();

            double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
            double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
            return (int) (distanceToPlace1 - distanceToPlace2);
        }

        public double distance(double fromLat, double fromLon, double toLat, double toLon) {
            double radius = 6378137;   // approximate Earth radius, *in meters*
            double deltaLat = toLat - fromLat;
            double deltaLon = toLon - fromLon;
            double angle = 2 * Math.asin(Math.sqrt(
                    Math.pow(Math.sin(deltaLat / 2), 2) +
                            Math.cos(fromLat) * Math.cos(toLat) *
                                    Math.pow(Math.sin(deltaLon / 2), 2)));
            return radius * angle;
        }
    }

    /*public class SortPlaces implements Comparator<Places> {
        LatLng currentLoc;

        public SortPlaces(LatLng current) {
            currentLoc = current;
        }

        @Override
        public int compare(final Places place1, final Places place2) {
            double lat1 = place1.latlng.latitude;
            double lon1 = place1.latlng.longitude;
            double lat2 = place2.latlng.latitude;
            double lon2 = place2.latlng.longitude;

            double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
            double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
            return (int) (distanceToPlace1 - distanceToPlace2);
        }

        public double distance(double fromLat, double fromLon, double toLat, double toLon) {
            double radius = 6378137;   // approximate Earth radius, *in meters*
            double deltaLat = toLat - fromLat;
            double deltaLon = toLon - fromLon;
            double angle = 2 * Math.asin(Math.sqrt(
                    Math.pow(Math.sin(deltaLat / 2), 2) +
                            Math.cos(fromLat) * Math.cos(toLat) *
                                    Math.pow(Math.sin(deltaLon / 2), 2)));
            return radius * angle;
        }
    }*/


    private void custom_alert(final int position, final int markerposition) {

        try {

            // final int position = findWaybillNoPosition(wno);
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.map_customalert, null);
            ImageView bm = (ImageView) alertLayout.findViewById(R.id.imageview_account_profile);
            final TextView waybillno = alertLayout.findViewById(R.id.waybillno);
            final TextView consignname = alertLayout.findViewById(R.id.consigneename);
            final EditText mobileno = alertLayout.findViewById(R.id.mobileno);
            final EditText mobileno1 = alertLayout.findViewById(R.id.mobileno1);
            Button deliver = (Button) alertLayout.findViewById(R.id.deliver);
            Button notdeliver = (Button) alertLayout.findViewById(R.id.notdeliver);
            Button redirect = (Button) alertLayout.findViewById(R.id.googlemap);


            // final int position = GlobalVar.GV().haslocation.get(pos - 1);
            consignname.setText(myRouteShipmentList.get(position - 1).ConsigneeName);

            waybillno.setText(String.valueOf(position) + "." + myRouteShipmentList.get(position - 1).ItemNo);
            if (myRouteShipmentList.get(position - 1).ConsigneeMobile.length() > 0 && !myRouteShipmentList.get(position - 1).ConsigneeMobile.equals("null"))
                mobileno.setText(myRouteShipmentList.get(position - 1).ConsigneeMobile);
            else
                mobileno.setVisibility(View.GONE);
            if (myRouteShipmentList.get(position - 1).ConsigneePhoneNumber.length() > 0 && !myRouteShipmentList.get(position - 1).ConsigneePhoneNumber.equals("null"))
                mobileno1.setText(myRouteShipmentList.get(position - 1).ConsigneePhoneNumber);
            else
                mobileno1.setVisibility(View.GONE);


            BitmapDrawable
                    ob = new BitmapDrawable(getResources(), makeBitmap(getApplicationContext(), String.valueOf(position), colors_marker_route.get(markerposition)));
            bm.setBackgroundDrawable(ob);

            mobileno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(mobileno.getText().toString(), getWindow().getDecorView().getRootView(), OptimiseMapbyDistrict.this);
                }
            });
            mobileno1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(mobileno1.getText().toString(), getWindow().getDecorView().getRootView(), OptimiseMapbyDistrict.this);
                }
            });

            redirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Location location = GlobalVar.getLastKnownLocation(getApplicationContext());

                    //LatLng ll = places.get(position).ge;
                    // String value = getIntent().getExtras().getString(key)
                    String uri = "http://maps.google.com/maps?saddr=" + location.getLatitude() + "," + location.getLongitude()
                            + "&daddr=" + places.get(markerposition).getLatitude() + "," + places.get(markerposition).getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            });


            deliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ConsigneeLatitude = myRouteShipmentList.get(position - 1).Latitude, ConsigneeLongitude = myRouteShipmentList.get(position - 1).Longitude;
                    if (GetDivision()) {

                        if (GlobalVar.GV().isSeqComplete(getApplicationContext())) {
                            ConsigneeLatitude = "0.0";
                            ConsigneeLongitude = "0.0";
                        }
                        if (GlobalVar.isCourierReachedConsigneeLocation(getApplicationContext(), Double.parseDouble(ConsigneeLatitude), Double.parseDouble(ConsigneeLongitude)))
                            Delivered(myRouteShipmentList.get(position - 1));
                        else
                            ErrorAlert("Info", "Kindly Reach Consignee Location before start Delivery Scan");
                    }
                }
            });

            notdeliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ConsigneeLatitude = myRouteShipmentList.get(position - 1).Latitude, ConsigneeLongitude = myRouteShipmentList.get(position - 1).Longitude;
                    if (GetDivision()) {

                        if (GlobalVar.GV().isSeqComplete(getApplicationContext())) {
                            ConsigneeLatitude = "0.0";
                            ConsigneeLongitude = "0.0";
                        }
                        if (GlobalVar.isCourierReachedConsigneeLocation(getApplicationContext(), Double.parseDouble(ConsigneeLatitude), Double.parseDouble(ConsigneeLongitude)))
                            NotDelivered(myRouteShipmentList.get(position - 1));
                        else
                            ErrorAlert("Info", "Kindly Reach Consignee Location before start Delivery Scan");
                    }
                }
            });


            //final CheckBox cbToggle = alertLayout.findViewById(R.id.mobi);

//        cbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // to encode password in dots
//                    etEmail.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                } else {
//                    // to display the password in normal text
//                    etEmail.setTransformationMethod(null);
//                }
//            }
//        });

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
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

            //            alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    String user = consignname.getText().toString();
//                    String pass = mobileno.getText().toString();
//                    Toast.makeText(getBaseContext(), "Username: " + user + " Email: " + pass, Toast.LENGTH_SHORT).show();
//                }
//            });
            AlertDialog dialog = alert.create();
            dialog.show();
            DisplayMetrics displayMetrics = new DisplayMetrics();

            getWindowManager().

                    getDefaultDisplay().

                    getMetrics(displayMetrics);

            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            dialog.getWindow().

                    setLayout(width - 50, height - 50);
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//
//            lp.copyFrom(dialog.getWindow().getAttributes());
//            lp.width = 150;
//            lp.height = 500;
//            lp.x=-170;
//            lp.y=100;
            //           dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void ErrorAlert(String title, String msg) {
        SweetAlertDialog eDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        eDialog.setCancelable(true);
        eDialog.setTitleText(title);
        eDialog.setContentText(msg);
        eDialog.show();

    }

    public void NotDelivered(MyRouteShipments myRouteShipments) {

        if (GetDivision()) {
            String waybillno = myRouteShipments.ItemNo.substring(0, 8);

            Bundle bundle = new Bundle();
            bundle.putString("ID", String.valueOf(myRouteShipments.ID));
            bundle.putString("WaybillNo", myRouteShipments.ItemNo);
            bundle.putInt("position", myRouteShipments.Position);


            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            Cursor result = dbConnections.Fill("select PiecesCount from MyRouteShipments Where ItemNo = '" + waybillno + "'",
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

    public void Delivered(MyRouteShipments myRouteShipments) {

        Bundle bundle = new Bundle();
        bundle.putString("ID", String.valueOf(myRouteShipments.ID));
        bundle.putString("WaybillNo", myRouteShipments.ItemNo);
        bundle.putInt("position", myRouteShipments.Position);


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


    private boolean GetDivision() {
        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);

        if (division.equals("Express") || division.equals("IRS"))
            return false;
        else
            return true;

    }

    private boolean arePointsNear(LatLng point1, LatLng point2) {
        LatLng sw = new LatLng(point2.latitude - 0.005, point2.longitude - 0.005);
        LatLng ne = new LatLng(point2.latitude + 0.005, point2.longitude + 0.005);
        LatLngBounds bounds = new LatLngBounds(sw, ne);
        if (bounds.contains(point1))
            return true;


        return false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            RouteMap.distance_time = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("distance_time");
            places = (ArrayList<Location>) savedInstanceState.getSerializable("places");
            colors_marker_route = savedInstanceState.getIntegerArrayList("colors_marker_route");
            jsonroute = savedInstanceState.getStringArrayList("jsonroute");
            GlobalVar.GV().haslocation = (ArrayList<Integer>) savedInstanceState.getSerializable("haslocation");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("places", places);
        outState.putIntegerArrayList("colors_marker_route", colors_marker_route);
        outState.putSerializable("distance_time", RouteMap.distance_time);
        //outState.putStringArrayList("jsonroute", jsonroute);
        outState.putSerializable("haslocation", GlobalVar.GV().haslocation);
    }

    public void GetSeqWaybillNo() {

        places.clear();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from SuggestLocations where Date = '" + GlobalVar.getDate() + "'" +
                " and EmpID = " + GlobalVar.GV().EmployID , getApplicationContext()); // + " Limit 1"
        if (result != null && result.getCount() > 0) {
            Issuggest = true;
            result.moveToFirst();
            do {

                String data = result.getString(result.getColumnIndex("StringData"));
                String split[] = data.split("@");
                for (int i = 0; i < split.length; i++) {
                    Bundle extras = new Bundle();

                    String temp[] = split[i].split("_");
                    Location sp = new Location("");
                    extras.putString("WaybillNo", temp[0]);
                    extras.putString("dsID", temp[temp.length - 1]);
                    extras.putInt("PsID", Integer.parseInt(temp[temp.length - 2]));
                    sp.setLatitude(Double.parseDouble(temp[1]));
                    sp.setLongitude(Double.parseDouble(temp[2]));
                    sp.setAltitude(Double.parseDouble(temp[4]));
                    sp.setSpeed(Float.parseFloat(temp[temp.length - 1]));

                    sp.setExtras(extras);
                    if (i == 0)
                        sp.setAltitude(0);
                    else
                        sp.setAltitude(Double.parseDouble(temp[0]));
                    places.add(sp);
                }


            }
            while (result.moveToNext());
        }


        result.close();
        dbConnections.close();

    }

    boolean IsPlanned = false;

    public void GetPlannedLocation() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from plannedLocation where Date = '" + GlobalVar.getDate() + "'" +
                " and EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());

        if (result.getCount() == places.size() -1 ) {


            if (result != null && result.getCount() > 0) {
                IsPlanned = true;
                result.moveToFirst();
                do {

                    String data = result.getString(result.getColumnIndex("StringData"));
                    int position = result.getInt(result.getColumnIndex("position"));

                    if (data.contains("ZERO_RESULTS")) {
                        System.out.println("true");
                        continue;
                    }
                    ParserTask parserTask = new ParserTask();
                    parserTask.execute(data, String.valueOf(position), "0");

                }
                while (result.moveToNext());
            }
        }
        result.close();
        dbConnections.close();

    }

    private int findWaybillNoPosition(int Waybillno) {
        int pos = 0;
        for (int i = 0; i < myRouteShipmentList.size(); i++) {
            if (Waybillno == Integer.parseInt(myRouteShipmentList.get(i).ItemNo)) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    boolean isoptimise = false;

    private void sortGeoCordinates() {
        Location loc = new Location("");
        loc.set(places.get(0));
        LatLng latLng = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());
        places.remove(0);

        if (places.size() > 1) {
            //while (places.size() != 0) {

            Collections.sort(places, new SortPlaces(latLng));

            // }
        }
        places.add(0, loc);
    }

    private void OptimizeLocation() {
        sortGeoCordinates();
        LatLng origin = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());
        LatLng dest = new LatLng(places.get(places.size() - 1).getLatitude(), places.get(places.size() - 1).getLongitude());
        // Getting URL to the Google Directions API
        String url = getDirectionsUrlbyWayPoints(origin, dest);


        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url, String.valueOf(0), String.valueOf(limitlocation));

//        if ((i == places.size() - 2) && (places.size() != 2)) {
//            origin = new LatLng(places.get(i - 1).getLatitude(), places.get(i - 1).getLongitude());
//            dest = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());
//            url = getDirectionsUrl(origin, dest);
//            downloadTask = new DownloadTask();
//            downloadTask.execute(url, String.valueOf(places.size() - 1));
//
//
//        }
    }

    int limitlocation = 0;

    private String getDirectionsUrlbyWayPoints(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        str_origin = "origin=24.852181,46.776634";

        String waypoints = "waypoints=optimize:true|";
        String departure = "departure_time=now";

        // Destination of route
        String str_dest = "destination=" + places.get(places.size() - 1).getLatitude() + "," + places.get(places.size() - 1).getLongitude();

        limitlocation = 0;
        for (int i = 0; i < places.size() - 2; i++) {
            Location location = places.get(i + 1);
            waypoints = waypoints + location.getLatitude() + "," + location.getLongitude() + "|";
        }
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getString(R.string.google_maps_key_forrute);
        // Building the parameters to the web service
//        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        String parameters = str_origin + "&" + str_dest + "&" + waypoints + "&" + departure + "&" + key;
        // Output format
        String output = "json";


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

}
