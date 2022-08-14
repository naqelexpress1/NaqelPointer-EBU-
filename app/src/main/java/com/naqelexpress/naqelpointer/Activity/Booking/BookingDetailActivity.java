package com.naqelexpress.naqelpointer.Activity.Booking;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BookingDetailActivity extends AppCompatActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingdetailnew);
        Bundle bundle = getIntent().getExtras();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        position = bundle.getInt("position");
        bookinglist = getIntent().getParcelableArrayListExtra("value");

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
            ActivityCompat.requestPermissions(BookingDetailActivity.this,
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
                BookingDetailActivity.this);
    }

    public void Delivered(View view) {
        //Status is Pickup
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.UpdateBookingStatus(BookingId, 3, view, getApplicationContext());
        dbConnections.close();

        Intent intent = new Intent(BookingDetailActivity.this, PickUpActivity.class);
        Bundle bundle = new Bundle();
        intent.putParcelableArrayListExtra("value", bookinglist);
        bundle.putString("class", "BookingDetailAcyivity");
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
        //To do need to call API
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
        GlobalVar.GV().ChangeMapSettings(mMap, BookingDetailActivity.this, getWindow().getDecorView().getRootView());

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

}