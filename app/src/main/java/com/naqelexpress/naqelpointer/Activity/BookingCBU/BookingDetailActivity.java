package com.naqelexpress.naqelpointer.Activity.BookingCBU;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookingDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    TextView txtReferenceNo, txtClientId, txtClient, txtContactPerson, txtContactNo, txtOrgin,
            txtDestination, txtPiecesCount, txtWeight, txtBillType, txtLoadType, txtReqTime, txtCloseTime,
            txtRReqTime, txtRCloseTime, txtSpecialInst, txtBookingRefNo, txtGoodDesc, txtMobileNo;
    private GoogleMap mMap;
    BookingModel myBooking;
    String ConsigneeLatitude, ConsigneeLongitude;
    Marker now;
    double Latitude = 0;
    double Longitude = 0;
    LatLng latLng;
    private int BookingId;
    int position;
    ArrayList<BookingModel> bookinglist;
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


        try {
            position = bundle.getInt("position");
            if (getIntent().getParcelableArrayListExtra("value") != null)
                bookinglist = getIntent().getParcelableArrayListExtra("value");
        } catch (Exception e) {
            bookinglist = BookingList.myBookingList;
        }
        if (bookinglist.size() == 0) {
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            bookinglist =
                    dbConnections.getPickupSheetDetailsData(getApplicationContext(), GlobalVar.GV().EmployID);
        }
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
        txtBookingRefNo = (TextView) findViewById(R.id.txtRefno);
        txtGoodDesc = (TextView) findViewById(R.id.txtgoodsDesc);
        txtCloseTime = (TextView) findViewById(R.id.txtCloseTime);
        txtMobileNo = (TextView) findViewById(R.id.txtMobileNo);

//        txtRReqTime = (TextView) findViewById(R.id.txtRReqTime);
//        txtRCloseTime = (TextView) findViewById(R.id.txtRCloseTime);

        txtSpecialInst = (TextView) findViewById(R.id.txtSpecialInstruction);

        Date currentTime = Calendar.getInstance().getTime();

        TextView txtRemReqTime = (TextView) findViewById(R.id.txtRemReqTime);

        TextView txtRemCloseTime = (TextView) findViewById(R.id.txtRemCloseTime);


        txtBookingRefNo.setVisibility(View.VISIBLE);
        txtGoodDesc.setVisibility(View.VISIBLE);

        LinearLayout ll1 = (LinearLayout) findViewById(R.id.ll1);

        LinearLayout ll2 = (LinearLayout) findViewById(R.id.ll2);


        ll1.setVisibility(View.VISIBLE);
        ll2.setVisibility(View.VISIBLE);

        //BookingId=GlobalVar.GV().myBookingList.get(position).ID;

        myBooking = bookinglist.get(position);
        txtReferenceNo.setText(String.valueOf(myBooking.getWaybillNo()));
        txtClientId.setText(String.valueOf(myBooking.ClientID));
        txtClient.setText(myBooking.ClientName);
        txtContactPerson.setText(myBooking.ConsigneeName);
        txtContactNo.setText(myBooking.getPhoneNo());
        txtOrgin.setText(myBooking.getOrgCode());
        txtDestination.setText(myBooking.getDestCode());
        txtBookingRefNo.setText(myBooking.getRefNo());
        txtGoodDesc.setText(myBooking.getGoodDesc());
        txtMobileNo.setText(myBooking.getMobileNo());

//        txtPiecesCount.setText(String.valueOf(bookinglist.get(position).PicesCount));
        txtPiecesCount.setVisibility(View.INVISIBLE);
        //txtWeight.setText(String.valueOf(bookinglist.get(position).Weight));
        txtWeight.setVisibility(View.INVISIBLE);
        txtBillType.setText(myBooking.getCode());
        //txtLoadType.setText(bookinglist.get(position).LoadType);
        txtLoadType.setVisibility(View.INVISIBLE);
        DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
        String dateStringRT = fmtRT.print(DateTime.parse(myBooking.Date));

        txtReqTime.setText(dateStringRT);
        //txtCloseTime.setText(bookinglist.get(position).OfficeUpTo.toString("HH:mm"));


        //txtRReqTime.setText(String.format("%R",(DateTime.now().minuteOfHour()-myBooking.PickUpReqDT)));
        // txtRCloseTime.setText(DateTime.now()-myBooking.OfficeUpTo);


//        txtSpecialInst.setText(bookinglist.get(position).SpecialInstruction);


        ConsigneeLatitude = myBooking.getLat();
        ConsigneeLongitude = myBooking.getLng();

        if (myBooking.getCode().equals("A"))
            txtBillType.setText("On Account");
        else if (myBooking.getCode().equals("C"))
            txtBillType.setText("Cash");
        else if (myBooking.getCode().equals("E"))
            txtBillType.setText("External Billing");
        else if (myBooking.getCode().equals("F"))
            txtBillType.setText("Free of Cost");
        else if (myBooking.getCode().equals("COD"))
            txtBillType.setText("Cash on Delivery");
        else if (myBooking.getCode().equals("FOD"))
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

        txtRemReqTime.setVisibility(View.GONE);
        txtRemCloseTime.setVisibility(View.GONE);
        txtPiecesCount.setVisibility(View.GONE);
        txtWeight.setVisibility(View.GONE);
        txtLoadType.setVisibility(View.GONE);
        txtCloseTime.setVisibility(View.GONE);
        txtSpecialInst.setVisibility(View.GONE);
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

    public void CallConsignee1(View view) {
        GlobalVar.GV().makeCall(txtMobileNo.getText().toString(), getWindow().getDecorView().getRootView(),
                BookingDetailActivity.this);
    }

    public void sendWatsapp(View view) {

        //String mobileno =
        bywatsapp(myBooking.getPhoneNo());

    }

    public void sendWatsapp1(View view) {

        //String mobileno =
        bywatsapp(myBooking.getMobileNo());

    }

    private void bywatsapp(String mobileno) {
        //

        if (!mobileno.equals("null") && mobileno != null && mobileno.length() > 0) {
            if (mobileno.length() == 10) {
                String validate = mobileno.substring(0, 1);
                if (validate.equals("0"))
                    mobileno = mobileno.replaceFirst("0", "+966");
            } else {
                if (mobileno.length() > 10) {
                    if (mobileno.contains("00966"))
                        mobileno = mobileno.replaceFirst("00966", "+966");
                } else if (mobileno.length() == 9) {
                    mobileno = "+966" + mobileno;
                }
            }

//                    GlobalVar.GV().MessageWhatsApp(rootView.getContext(),txtMobileNo.getTag().toString(),"hellow");

            showPopup(mobileno);
        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Is not valid mobile number.", GlobalVar.AlertType.Warning);
    }

    public void Delivered(View view) {
        //Status is Pickup
        try {

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            dbConnections.UpdateBookingStatus(BookingId, 3, view, getApplicationContext());
            dbConnections.close();

            Intent intent = new Intent(BookingDetailActivity.this, PickUpActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("value", bookinglist);
            bundle.putString("class", "BookingDetailAcyivityforCBU");
            bundle.putString("RNo", txtBookingRefNo.getText().toString());
            bundle.putInt("position", position);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
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

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(BookingDetailActivity.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Pickup Exception");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (BookingDetailActivity.this, android.R.layout.select_dialog_item);

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
                final AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivity.this);
                builder.setTitle(strName);

                final EditText notes = new EditText(BookingDetailActivity.this);
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
                            jsonObject.put("PSDID", myBooking.getPickupsheetDetailID());
                            jsonObject.put("PSID", myBooking.getPickupSheetID());
                            jsonObject.put("StationID", GlobalVar.GV().StationID);
                            jsonObject.put("TimeIn", DateTime.now());
                            jsonObject.put("UserID", GlobalVar.GV().UserID);
                            jsonObject.put("WaybillNo", myBooking.getWaybillNo());


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
        GlobalVar.GV().ChangeMapSettings(mMap, BookingDetailActivity.this, getWindow().getDecorView().getRootView());

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
        now = mMap.addMarker(new MarkerOptions().position(GlobalVar.GV().currentLocation)
                .icon(icon)
                .title(getString(R.string.MyLocation)));
        ShowShipmentMarker();
    }

    private void ShowShipmentMarker() {
        if (ConsigneeLongitude.length() > 3 && ConsigneeLongitude.length() > 3) {
            LatLng latLng = new LatLng(GlobalVar.GV().getDoubleFromString(ConsigneeLatitude), GlobalVar.GV().getDoubleFromString(ConsigneeLongitude));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.deliverymarker);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(icon)
                    .title(String.valueOf(myBooking.getWaybillNo())));
        }
//        else
//            mapFragment.getView().setVisibility(View.GONE);
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
            pd = new ProgressDialog(BookingDetailActivity.this);
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
                    if (!jsonObject.getBoolean("HasError")) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        BookingList.isException = true;
                        dbConnections.UpdatepickupsheetdetailsID(myBooking.getWaybillNo(), 1);
                        BookingList.myBookingList.get(myBooking.sNo - 1).setIsPickedup(1);
                        dbConnections.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(String.valueOf(finalJson));

            }

            pd.dismiss();
        }
    }

    private void crreateAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailActivity.this);
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

    PopupWindow popup;

    private void showPopup(final String mobileno) {


        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.messagedrafts, viewGroup);

        final TextView customerlocation = (TextView) layout.findViewById(R.id.customerlocation);
        final TextView frontofthedoor = (TextView) layout.findViewById(R.id.frontofthedoor);
        final TextView cssupport = (TextView) layout.findViewById(R.id.cssupport);
        final TextView resndotp = (TextView) layout.findViewById(R.id.resendotp);
        resndotp.setVisibility(View.GONE);
        final String arabic = "عزيزي العميل, n\n  رجاء قم بمشاركة موقعك على الرابط المرفق أدناه لنقوم بتوصيل شحنتك.(" + " " +
                myBooking.getWaybillNo()
                + ") من)" + myBooking.getClientName();

        customerlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConsigneeLatitude != null && ConsigneeLatitude.length() < 2)
//                    GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg)
//                            + " " + txtWaybillNo.getText().toString() + getString(R.string.watsappPredefinedMsg1)
//                            + txtShipperName.getText().toString() + "\n\n\n" + arabic + getString(R.string.watsappPredefinedMsg2)
//                            + txtWaybillNo.getText().toString(), getApplicationContext());
                    GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getLocationMsg(), getApplicationContext());
                else
                    alertforcommon();

                popup.dismiss();
            }
        });
        frontofthedoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.doorPredefinedMsg)
//                        + " " + txtWaybillNo.getText().toString() + getString(R.string.doorPredefinedMsg1)
//                        + txtShipperName.getText().toString() + getString(R.string.doorPredefinedMsg2)
//                        + txtWaybillNo.getText().toString() + "\n\n\n" + arabic, getApplicationContext());
                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getFrontDoorMsg(), getApplicationContext());
                popup.dismiss();
            }
        });
        cssupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.csPredefinedMsg) + "\n\n\n" + arabic, getApplicationContext());
                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getCsSupportMsg(), getApplicationContext());
                popup.dismiss();
            }
        });


        popup = new PopupWindow(BookingDetailActivity.this);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    private String getLocationMsg() {
        String armsg1 = "مرحبا , أنا مندوب شركة ناقل الرجاء مشاركة الموقع عبر الرابط التالي:  ";
        String armsg2 = "من اجل استلام الشحنة ";
        String armsg3 = "من";
        String armsg4 = "للاسترجاع";
        final String locationMsgAr = armsg1 + "\n" +
                getString(R.string.infotrackLocationLink) + " " + armsg2 + " " +
                getWaybillNo() + " " + armsg3 + " " +

                getClientName() + " " + armsg4;

        final String locationMsgEn = "Hello! This is NAQEL courier. Please share your location " +
                "for picking up Return Shipment " + "\n" +

                getWaybillNo() + " for " +
                getClientName() + " using Link: " + getString(R.string.infotrackLocationLink);


        return locationMsgAr + "\n\n" + locationMsgEn;
    }

    private String getWaybillNo() {
        return String.valueOf(myBooking.getWaybillNo());
    }

    private String getClientName() {
        return myBooking.getClientName();
    }

    private String getFrontDoorMsg() {
        String armsg1 = "مرحبا , مندوب ناقل وصل الى موقعك لاستلام شحنتكم ";
        String armsg2 = "من";
        final String frontDoorMsgAr = armsg1 +
                getWaybillNo() + " " + armsg2 + " " +
                getClientName();
        final String frontDoorMsgEn = "Hello! NAQEL courier has arrived at your front door to pick up Return shipment" +
                getWaybillNo() + " for " +
                getClientName();
        return frontDoorMsgAr + "\n\n" + frontDoorMsgEn;
    }

    private String getCsSupportMsg() {
        final String csSupportMsgAr = getString(R.string.csSupportMsg1Ar) + "\n\n" +
                GlobalVar.getCSPhoneNumber() + "\n" +
                GlobalVar.getCSEmail();
        final String csSupportMsgMsgEn = getString(R.string.csSupportMsg1En) + "\n\n" +
                GlobalVar.getCSPhoneNumber() + "\n" +
                GlobalVar.getCSEmail();
        return csSupportMsgAr + "\n\n" + csSupportMsgMsgEn;
    }

    private void alertforcommon() {
        SweetAlertDialog eDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        eDialog.setCancelable(true);
        eDialog.setTitleText("Has Location");
        eDialog.setContentText("This Shipment already has Location , kindly please start");
        eDialog.show();

    }
}