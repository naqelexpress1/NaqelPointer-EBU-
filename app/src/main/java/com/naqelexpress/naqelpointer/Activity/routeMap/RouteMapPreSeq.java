package com.naqelexpress.naqelpointer.Activity.routeMap;

import android.Manifest;
import android.app.Activity;
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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
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
import com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.SelectData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static com.itextpdf.awt.geom.Point2D.distance;

/**
 * Created by Hasna on 7/21/18.
 */

public class RouteMapPreSeq extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    SupportMapFragment mapFragment;
    //MapFragment mapFragment;
    private GoogleMap mMap;

    private ArrayList<MyRouteShipments> myRouteShipmentList;
    //static ArrayList<Places> places; // = new ArrayList<>();
    ArrayList<Integer> colors_marker_route = new ArrayList<>();
    public static ArrayList<Location> places = new ArrayList<>();
    public ArrayList<Location> allplaces = new ArrayList<>();
    public static ArrayList<Location> tplaces = new ArrayList<>();
    private Marker myMarker[];
    boolean Issuggest = false;
    public static ArrayList<HashMap<String, String>> distance_time = new ArrayList<>();
    SweetAlertDialog pDialog;
    String AreaData = "";
    ArrayList<HashMap<String, String>> AreaListAll = new ArrayList<>();
    ArrayList<HashMap<String, String>> AreaListSortbyRadiusandArea = new ArrayList<>();
    Location lastlocation;
    public static ArrayList<HashMap<String, String>> jsonobject_route = new ArrayList<>();
    static int precount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.routemap);


        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.DeleteAllSuggestPlannedLocation(getApplicationContext());
        //dbConnections.clearMyRouteComplaince(getApplicationContext());
        dbConnections.close();

        precount = -1;
        Bundle extras = getIntent().getExtras();
        myRouteShipmentList = extras.getParcelableArrayList("myroute");
        tplaces = extras.getParcelableArrayList("places");
        AreaData = extras.getString("AreaData");


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        places.clear();
        allplaces.clear();


        lastlocation = findLastLocation(0);

        //Get Suggested Waybills
        GetSeqWaybillNo();

        RouteMapPreSeq.distance_time.clear();

        ImageButton distance = (ImageButton) findViewById(R.id.viewmore);
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intetn = new Intent(RouteMapPreSeq.this, RouteMap_Distance.class);
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


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Optimizing routes kindly please wait");
        pDialog.setCancelable(false);
//        pDialog.show();

        Issuggest = false;

//        GetPlannedLocation();

        stopService(
                new Intent(this,
                        com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class));

    }

    boolean isMapReady = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GlobalVar.GV().ChangeMapSettings(mMap, RouteMapPreSeq.this, getWindow().getDecorView().getRootView());


        LatLng ll = new LatLng(places.get(0).getLatitude(), places.get(0).getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 8));
        mMap.setOnInfoWindowClickListener(RouteMapPreSeq.this);
        isMapReady = true;


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = (int) (marker.getTag());

                if (ActivityCompat.checkSelfPermission(RouteMapPreSeq.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(RouteMapPreSeq.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                    if (position != 0) {
                        //commented by ismail because couriers are miss use
                        // custom_alert((Integer) marker.getTag(), Integer.parseInt(marker.getSnippet()));
                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "This is Starting Point.", GlobalVar.AlertType.Error);

                }
                return false;
            }
        });


        //commented below 3 line
        ShowShipmentMarker();

    }

    static int i = 0;

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

    private void ShowShipmentMarker() {

        SelectData selectData = new SelectData();

        try {
            //Location location = new Location();
            double prvLat = 0.0;
            double preLng = 0.0;
            String data = "";
            places.clear();
            places.addAll(selectData.isPredefienedSeqSortbySeq(getApplicationContext(), selectData.isPlannedLocation(getApplicationContext())));
            places.add(0, findLastLocation(0));

            isServicePlannedRoute_MyRouteComp();

            myMarker = new Marker[places.size() + 1];
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

                myMarker[i].setSnippet(String.valueOf(i));


                //double distanceToPlace1 = distance(prvLat, preLng, latLng.latitude , latLng.longitude);
                if (i == 0)
                    data = "Stratingplace_" + String.valueOf(places.get(i).getLatitude()) + "_" + String.valueOf(places.get(i).getLongitude() + "_0_0_0");
                else {
                    String WNo = (String) places.get(i).getExtras().get("WNo");
                    int pos = findWaybillNoPosition(Integer.parseInt(WNo));
                    myMarker[i].setTag(pos);
                    data = data + "@" + WNo + "_" + String.valueOf(places.get(i).getLatitude()) + "_" + String.valueOf(places.get(i).getLongitude() + "_"
                            + String.valueOf(distance(prvLat, preLng, latLng.latitude, latLng.longitude)) + "_" + String.valueOf(i) + "_" + String.valueOf(pos));
                }

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
                    GlobalVar.GV().makeCall(mobileno.getText().toString(), getWindow().getDecorView().getRootView(), RouteMapPreSeq.this);
                }
            });
            mobileno1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(mobileno1.getText().toString(), getWindow().getDecorView().getRootView(), RouteMapPreSeq.this);
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
                    Delivered(myRouteShipmentList.get(position - 1));
                }
            });

            notdeliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NotDelivered(myRouteShipmentList.get(position - 1));
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
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            dialog.getWindow().setLayout(width - 50, height - 50);
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
            distance_time = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("distance_time");
            places = (ArrayList<Location>) savedInstanceState.getSerializable("places");
            colors_marker_route = savedInstanceState.getIntegerArrayList("colors_marker_route");

            GlobalVar.GV().haslocation = (ArrayList<Integer>) savedInstanceState.getSerializable("haslocation");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("places", places);
        outState.putIntegerArrayList("colors_marker_route", colors_marker_route);
        outState.putSerializable("distance_time", distance_time);
        //outState.putStringArrayList("jsonroute", jsonroute);
        outState.putSerializable("haslocation", GlobalVar.GV().haslocation);
    }

    public void GetSeqWaybillNo() {

        places.clear();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from SuggestLocations where Date = '" + GlobalVar.getDate() + "'" +
                " and EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());
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
        } else {
            places = tplaces;
            //         allplaces = places;
        }

        result.close();
        dbConnections.close();

        if (places.size() == 0) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    boolean IsPlanned = false;

  /*  public void GetPlannedLocation() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from plannedLocation where Date = '" + GlobalVar.getDate() + "'" +
                " and EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());

        if (result.getCount() == allplaces.size()) {

            if (isMapReady)
                ShowShipmentMarker();
        }

        result.close();
        dbConnections.close();

    }*/

    private void insertintoPlannedlocationtable() {

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

    private int findWaybillNoindex(String Waybillno) {
        int pos = 0;
        pos = myRouteShipmentList.indexOf(Waybillno);

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

    private Location findLastLocation(int postion) {
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        location.setSpeed(postion);
        return location;
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        //returnIntent.putExtra("result", "refreshdata");
        returnIntent.putExtra("result", "finish");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();

    }

    private void isServicePlannedRoute_MyRouteComp() {
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class)) {
            startService(
                    new Intent(this,
                            com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class));
        }
    }
}
