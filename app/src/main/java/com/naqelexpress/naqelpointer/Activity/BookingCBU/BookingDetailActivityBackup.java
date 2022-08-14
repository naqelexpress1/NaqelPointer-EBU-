package com.naqelexpress.naqelpointer.Activity.BookingCBU;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naqelexpress.naqelpointer.Activity.PickUp.PickUpActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BookingDetailActivityBackup extends AppCompatActivity
        implements OnMapReadyCallback {

    TextView txtReferenceNo, txtClientId, txtClient, txtContactPerson, txtContactNo, txtOrgin,
            txtDestination, txtPiecesCount, txtWeight, txtBillType, txtLoadType, txtReqTime, txtCloseTime,
            txtRReqTime, txtRCloseTime, txtSpecialInst;
    private GoogleMap mMap;
    Booking myBooking;
    String ConsigneeLatitude, ConsigneeLongitude;
    Marker now;
    double Latitude = 0;
    double Longitude = 0;
    LatLng latLng;
    private int BookingId;
    int position;
    ArrayList<Booking> bookinglist;
    ArrayList<String> name;
    ArrayList<Integer> IDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingdetailpickupsheet);
        Bundle bundle = getIntent().getExtras();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        position = bundle.getInt("position");
        bookinglist = getIntent().getParcelableArrayListExtra("value");
        name = getIntent().getStringArrayListExtra("name");
        IDs = getIntent().getIntegerArrayListExtra("IDs");

        txtReferenceNo = (TextView) findViewById(R.id.txtReferenceNo);
        txtClientId = (TextView) findViewById(R.id.txtClientId);
        txtClient = (TextView) findViewById(R.id.txtClientName);
        txtContactPerson = (TextView) findViewById(R.id.txtContactPerson);
        txtContactNo = (TextView) findViewById(R.id.txtContactNo);
        txtOrgin = (TextView) findViewById(R.id.txtOrgin);
        txtDestination = (TextView) findViewById(R.id.txtDestination);
        txtPiecesCount = (TextView) findViewById(R.id.txtPiecesCount);
        txtWeight = (TextView) findViewById(R.id.txtWeight);
        txtBillType = (TextView) findViewById(R.id.txtBillType);
        txtLoadType = (TextView) findViewById(R.id.txtLoadType);
        txtReqTime = (TextView) findViewById(R.id.txtReqTime);
        txtCloseTime = (TextView) findViewById(R.id.txtCloseTime);
//        txtRReqTime = (TextView) findViewById(R.id.txtRReqTime);
//        txtRCloseTime = (TextView) findViewById(R.id.txtRCloseTime);
        txtSpecialInst = (TextView) findViewById(R.id.txtSpecialInstruction);

        Date currentTime = Calendar.getInstance().getTime();

        TextView txtRemReqTime = (TextView) findViewById(R.id.txtRemReqTime);
        txtRemReqTime.setVisibility(View.INVISIBLE);
        TextView txtRemCloseTime = (TextView) findViewById(R.id.txtRemCloseTime);
        txtRemCloseTime.setVisibility(View.INVISIBLE);

        //BookingId=GlobalVar.GV().myBookingList.get(position).ID;

        myBooking = bookinglist.get(position);
        txtReferenceNo.setText(bookinglist.get(position).RefNo);
        txtClientId.setText(String.valueOf(myBooking.ClientID));
        txtClient.setText(myBooking.ClientName);
        txtContactPerson.setText(bookinglist.get(position).ContactPerson);
        txtContactNo.setText(bookinglist.get(position).ContactNumber);
        txtOrgin.setText(bookinglist.get(position).Orgin);
        txtDestination.setText(bookinglist.get(position).Destination);


        txtPiecesCount.setText(String.valueOf(bookinglist.get(position).PicesCount));
        txtWeight.setText(String.valueOf(bookinglist.get(position).Weight));
        txtBillType.setText(bookinglist.get(position).BillType);
        txtLoadType.setText(bookinglist.get(position).LoadType);
        txtLoadType.setVisibility(View.INVISIBLE);
        DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
        String dateStringRT = fmtRT.print(DateTime.parse(bookinglist.get(position).PickUpReqDT));

        txtReqTime.setText(dateStringRT);
        txtCloseTime.setText(bookinglist.get(position).OfficeUpTo.toString("HH:mm"));

        //txtRReqTime.setText(String.format("%R",(DateTime.now().minuteOfHour()-myBooking.PickUpReqDT)));
        // txtRCloseTime.setText(DateTime.now()-myBooking.OfficeUpTo);


        txtSpecialInst.setText(bookinglist.get(position).SpecialInstruction);


        ConsigneeLatitude = myBooking.Latitude;
        ConsigneeLongitude = myBooking.Longitude;

        if (bookinglist.get(position).BillType.equals("A"))
            txtBillType.setText("On Account");
        else if (bookinglist.get(position).BillType.equals("C"))
            txtBillType.setText("Cash");
        else if (bookinglist.get(position).BillType.equals("E"))
            txtBillType.setText("External Billing");
        else if (bookinglist.get(position).BillType.equals("F"))
            txtBillType.setText("Free of Cost");
        else if (bookinglist.get(position).BillType.equals("COD"))
            txtBillType.setText("Cash on Delivery");
        else if (bookinglist.get(position).BillType.equals("FOD"))
            txtBillType.setText("Freight on Delivery");
        else
            txtBillType.setText("Contact Admin");


//        if (bundle != null)
//        {
//            for (int i = 0; i < GlobalVar.GV().myBookingList.size(); i++)
//            {
//                int bid = Integer.parseInt(bundle.getString("ID"));
//
//                int bod = GlobalVar.GV().myBookingList.get(i).ID;
//
//
//                if (GlobalVar.GV().myBookingList.get(i).ID == Integer.parseInt(bundle.getString("ID")) )
//                {
//                    BookingId=GlobalVar.GV().myBookingList.get(i).ID;
//                    myBooking = GlobalVar.GV().myBookingList.get(i);
//                    txtReferenceNo.setText(myBooking.RefNo);
//                    txtClientId.setText(String.valueOf(myBooking.ClientID));
//                    txtClient.setText(myBooking.ClientName);
//                    txtContactPerson.setText(myBooking.ContactPerson);
//                    txtContactNo.setText(myBooking.ContactNumber);
//                    txtOrgin.setText(myBooking.Orgin);
//                    txtDestination.setText(myBooking.Destination);
//
//
//                    txtPiecesCount.setText(String.valueOf(myBooking.PicesCount));
//                    txtWeight.setText(String.valueOf(myBooking.Weight));
//                    txtBillType.setText(myBooking.BillType);
//                    txtLoadType.setText(myBooking.LoadType);
//                    txtReqTime.setText(myBooking.PickUpReqDT.toString("HH:mm"));
//                    txtCloseTime.setText(myBooking.OfficeUpTo.toString("HH:mm"));
//                   //txtRReqTime.setText(String.format("%R",(DateTime.now().minuteOfHour()-myBooking.PickUpReqDT)));
//                   // txtRCloseTime.setText(DateTime.now()-myBooking.OfficeUpTo);
//
//
//                    txtSpecialInst.setText(myBooking.SpecialInstruction);
//
//
//
//
//                    ConsigneeLatitude = myBooking.Latitude;
//                    ConsigneeLongitude = myBooking.Longitude;
//
//
//                    break;
//                }
//            }
//
//
//        }


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            requestLocation();


        } else {
            ActivityCompat.requestPermissions(BookingDetailActivityBackup.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            finish();
        }

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


    public void CallConsignee(View view) {
        GlobalVar.GV().makeCall(txtContactNo.getText().toString(), getWindow().getDecorView().getRootView(),
                BookingDetailActivityBackup.this);
    }

    public void Delivered(View view) {
        //Status is Pickup
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.UpdateBookingStatus(BookingId, 3, view, getApplicationContext());
        dbConnections.close();

        Intent intent = new Intent(BookingDetailActivityBackup.this, PickUpActivity.class);
        Bundle bundle = new Bundle();
        intent.putParcelableArrayListExtra("value", bookinglist);
        bundle.putString("class", "BookingDetailAcyivity");
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
        //To do need to call API
    }

    public void Exception(View view) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivity.this);
//        builder.setTitle("Pickup Exception");
//
//        final EditText notes = new EditText(BookingDetailActivity.this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        notes.setLayoutParams(lp);
//
//
//        CharSequence[] cs = name.toArray(new CharSequence[name.size()]);
//        //String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
//        builder.setItems(cs, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), notes.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.setView(notes);
//        dialog.show();

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(BookingDetailActivityBackup.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Pickup Exception");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (BookingDetailActivityBackup.this, android.R.layout.select_dialog_item);

        arrayAdapter.addAll(name);


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                final int exceptionID = IDs.get(which);
                final AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivityBackup.this);
                builder.setTitle(strName);

                final EditText notes = new EditText(BookingDetailActivityBackup.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                notes.setLayoutParams(lp);


                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String Notes = "";
                        if (notes.getText().toString().length() > 0)
                            Notes = notes.getText().toString();
                        JSONObject jsonObject = new JSONObject();
                        try {


                            jsonObject.put("EmployID", GlobalVar.GV().EmployID);


                            jsonObject.put("Latitude", String.valueOf(Latitude));
                            jsonObject.put("Longitude", String.valueOf(Longitude));
                            jsonObject.put("Notes", Notes);
                            jsonObject.put("PickupExceptionID", exceptionID);
                            jsonObject.put("PSDID", bookinglist.get(position).PSDID);
                            jsonObject.put("PSID", bookinglist.get(position).PSID);
                            jsonObject.put("StationID", GlobalVar.GV().StationID);
                            jsonObject.put("TimeIn", DateTime.now());
                            jsonObject.put("UserID", GlobalVar.GV().UserID);
                            jsonObject.put("WaybillNo", bookinglist.get(position).RefNo);


                            String jsonData = jsonObject.toString();

                            new SavePickupException().execute(jsonData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog1 = builder.create();
                dialog1.setView(notes);
                dialog1.show();
            }
        });
        builderSingle.show();
// create and show the alert dialog

    }

    public void AcceptClick(View view) {
        //Status is Accepted
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.UpdateBookingStatus(BookingId, 1, view, getApplicationContext());
        dbConnections.close();
        // To Do need to call API to Update status in server
    }

    public void RejectClick(View view) {
        //Status is Rejected
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.UpdateBookingStatus(BookingId, 2, view, getApplicationContext());
        dbConnections.close();
        // To Do need to call API to Update status in server
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GlobalVar.GV().ChangeMapSettings(mMap, BookingDetailActivityBackup.this, getWindow().getDecorView().getRootView());

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
        now = mMap.addMarker(new MarkerOptions().position(GlobalVar.GV().currentLocation)
                .icon(icon)
                .title(getString(R.string.MyLocation)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
                if (result == 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", 0);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private class SavePickupException extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(BookingDetailActivityBackup.this);
            pd.setTitle("Loading");
            pd.setMessage("Your request is being process,kindly please wait ");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];


            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertPickupException"); //LoadtoDestination
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
            if (finalJson != null) {
                try {
                    JSONObject jsonObject = new JSONObject(finalJson);
                    crreateAlert(jsonObject.getString("ErrorMessage"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(String.valueOf(finalJson));

            }

            pd.dismiss();
        }
    }

    private void crreateAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivityBackup.this);
        builder.setTitle(msg);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
}