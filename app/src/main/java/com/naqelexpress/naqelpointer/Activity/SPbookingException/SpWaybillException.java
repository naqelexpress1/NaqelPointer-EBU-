package com.naqelexpress.naqelpointer.Activity.SPbookingException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.Activity.BookingCBU.PickupSheetReasonModel;
import com.naqelexpress.naqelpointer.Activity.PickupAsrReg.PickUpActivity;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingList;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingModel;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SpWaybillException extends AppCompatActivity implements View.OnClickListener {

    DateTime TimeIn;
    private Bundle bundle;
    ArrayList<BookingModel> bookinglist;

    int position;
    static String RefNo = "";
    ArrayList<String> name;
    ArrayList<Integer> IDs;
    ArrayList<PickupSheetReasonModel> pickupSheetReasonModelArrayList;
    //public static ArrayList<BookingModel> myBookingList;
    private SwipeMenuListView mapListview;
    private WaybillExceptionAdapter adapter;
    private TextView nodata;
    double Latitude = 0, Longitude = 0;
    public static ArrayList<String> waybilllist;
    public static boolean isFinish = false;
    public static HashSet<String> exceptionIDs;
    public static HashMap<String, String> exceptionHashmap;
    boolean isChecked = false;
    ArrayList<BookingModel> rmlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.spsregular_groupbookinglist);


        Bundle bundle = getIntent().getExtras();

        exceptionIDs = new HashSet<>();
        exceptionHashmap = new HashMap<>();

        CheckBox bulkexception = (CheckBox) findViewById(R.id.bulkexception);
        bulkexception.setVisibility(View.VISIBLE);
        bulkexception.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    isChecked = true;
                } else isChecked = false;
            }
        });

        RefNo = "";

        isFinish = false;
//        waybilllist = new ArrayList<>();

        TextView t1 = (TextView) findViewById(R.id.t1);

        t1.setText("PID");
        TextView t2 = (TextView) findViewById(R.id.t2);
        t2.setText("Exception");
        Button save = (Button) findViewById(R.id.pickup);
        save.setText("Save");
        save.setOnClickListener(this);
        Button cancel = (Button) findViewById(R.id.exception);
        cancel.setText("Cancel");
        cancel.setOnClickListener(this);
        setFinishOnTouchOutside(false);

//        position = bundle.getInt("position");
//
        bookinglist = (ArrayList<BookingModel>)
                getIntent().getSerializableExtra("value");
//
        rmlist = (ArrayList<BookingModel>)
                getIntent().getSerializableExtra("blist");

        pickupSheetReasonModelArrayList = (ArrayList<PickupSheetReasonModel>)
                getIntent().getSerializableExtra("PRMA");

        waybilllist = (ArrayList<String>)
                getIntent().getStringArrayListExtra("waybilllist");
        //
//        if (pickupSheetReasonModelArrayList.size() > 0)
//            fetchPickupsheetReasons();


        mapListview = (SwipeMenuListView) findViewById(R.id.myBookingListView);
        mapListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        nodata = (TextView) findViewById(R.id.nodata);

        //  setAdapter();

//        ReadfromLocal();

        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
               /* Cursor result = dbConnections.Fill("select * from PickUpAuto where IsSync = 0" +
                        " and WaybillNo=" +
                        myBookingList.get(position).WaybillNo, getApplicationContext());

                if (result.getCount() == 0) {
                    //int pos = Integer.parseInt(((TextView) view.findViewById(R.id.sno)).getText().toString()) - 1;
                    RedirectPickupActivity(position);
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "you picked up this item, please sync data", GlobalVar.AlertType.Error);
                dbConnections.close();
*/
                RedirectPickupActivity(position);

            }
        });


        TimeIn = DateTime.now();
        setAdapter();

//        exception.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                    showPopup();
//
//                Exception();
//
//            }
//        });


//        riderct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                    showPopup();
//
//                RedirectPickupActivity(0);
//
//            }
//        });
    }

    private void setAdapter() {
        exceptionIDs.clear();
        exceptionHashmap.clear();
        adapter = new WaybillExceptionAdapter(SpWaybillException.this, waybilllist, pickupSheetReasonModelArrayList, "BookingList");

        mapListview.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinish == true) {
            BookingList.isFinish = true;
            finish();
        }
    }

    private void RedirectPickupActivity(int pos) {
        try {


            Intent intent = new Intent(SpWaybillException.this, PickUpActivity.class);
            // Bundle bundle = new Bundle();
            // bundle.putE("value", (Serializable) myBookingList.get(position));
            intent.putExtra("value", bookinglist);
            intent.putExtra("PRMA", pickupSheetReasonModelArrayList);

            intent.putExtra("position", pos);
            intent.putExtra("name", name);
            intent.putExtra("IDs", IDs);
            intent.putExtra("class", 0);
            intent.putExtra("waybilllist", waybilllist);

            //  bundle.putString("ID", String.valueOf(myBookingList.get(pos).PickupsheetDetailID));
            //intent.putExtras(bundle);

            startActivity(intent);


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pickupmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    boolean FullyInserted = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuBringData:

                return true;
            case R.id.mnuSave:

                return true;

            case R.id.getdistrict:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void fetchPickupsheetReasons() {
        name = new ArrayList<>();
        IDs = new ArrayList<>();

        for (PickupSheetReasonModel pickupSheetReasonModel : pickupSheetReasonModelArrayList) {
            name.add(pickupSheetReasonModel.getName());
            IDs.add(pickupSheetReasonModel.getID());
        }

    }

    public void ReadfromLocal() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        GlobalVar.GV().EmployID = 19127;
        int officeID = bookinglist.get(position).getSPLOfficesID();
        bookinglist.clear();
        bookinglist =
                dbConnections.getPickupSheetSpAsrRegDetailsDatabySpOfficeID(getApplicationContext(), GlobalVar.GV().EmployID,
                        officeID);

        if (bookinglist.size() > 0) {
            setAdapter();
            nodata.setVisibility(View.GONE);

            mapListview.setVisibility(View.VISIBLE);
            //pickupSheetReasonModelArrayList = dbConnections.getPickupSheetDetailsReasonData(getApplicationContext());
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();

            LatLng latLng = new LatLng(Latitude, Longitude);
            GlobalVar.GV().currentLocation = latLng;


        }
    }


    private void Exception() {

        requestLocation();


        String Notes = "";

        JSONArray jsonArray = new JSONArray();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Iterator<String> exceptionID = exceptionIDs.iterator();
        boolean isloop = false;
        int eID = 0;
        if (exceptionIDs.size() != waybilllist.size()) {
            crreateAlert("Please select all Reasons or use Bulk option to save", false);
            return;
        }
        while (exceptionID.hasNext()) {
            String[] hasStrings = exceptionID.next().toString().split("_");

            eID = Integer.parseInt(hasStrings[1]);

            if (eID == 0) {
                crreateAlert("Please select all Reasons or use Bulk option to save", false);
                return;
            }
        }

        while (exceptionID.hasNext()) {


            String[] hasStrings = exceptionID.next().toString().split("_");
            int wNo = Integer.parseInt(hasStrings[0]);
            eID = Integer.parseInt(hasStrings[1]);


            BookingModel bookingModel = new BookingModel();

            for (BookingModel bm : bookinglist) {
                if (wNo == bm.getWaybillNo()) {
                    bookingModel = bm;
                    break;
                }
            }
            JSONObject jsonObject = new JSONObject();
            try {


                jsonObject.put("EmployID", GlobalVar.GV().EmployID);
                jsonObject.put("Latitude", String.valueOf(Latitude));
                jsonObject.put("Longitude", String.valueOf(Longitude));
                jsonObject.put("Notes", Notes);
                jsonObject.put("PickupExceptionID", eID);
                jsonObject.put("PSDID", bookingModel.getPickupsheetDetailID());
                jsonObject.put("PSID", bookingModel.getPickupSheetID());
                jsonObject.put("StationID", GlobalVar.GV().StationID);
                jsonObject.put("TimeIn", DateTime.now());
                jsonObject.put("UserID", GlobalVar.GV().UserID);
                jsonObject.put("WaybillNo", wNo);

                jsonArray.put(jsonObject);

                dbConnections.InsertPickUpException(getApplicationContext(), String.valueOf(bookingModel.getWaybillNo()),
                        bookingModel.getSPLOfficesID());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dbConnections
                .close();
        String jsonData = jsonArray.toString();

        new SavePickupException().execute(jsonData);

    }


    private void BulkException() {

        if (exceptionIDs.size() == 0) {
            crreateAlert("Please select any one reason", false);
            return;
        }
        requestLocation();


        String Notes = "";

        JSONArray jsonArray = new JSONArray();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        // Iterator<String> exceptionID = exceptionIDs.iterator();
        boolean isloop = false;
        int eID = 0;
        for (BookingModel bookingModel : rmlist) {


            if (!isloop) {
                String first = exceptionIDs.iterator().next();
                String[] firstStrings = first.split("_");
                eID = Integer.parseInt(firstStrings[1]);
                isloop = true;
            }

            if (eID == 0) {
                crreateAlert("Please select any one Reasons ", false);
                exceptionIDs.clear();
                return;
            }

            //  BookingModel bookingModel = new BookingModel();

//            for (BookingModel bm : bookinglist) {
//                if (wNo == bm.getWaybillNo()) {
//                    bookingModel = bm;
//                    break;
//                }
//            }
            JSONObject jsonObject = new JSONObject();
            try {


                jsonObject.put("EmployID", GlobalVar.GV().EmployID);
                jsonObject.put("Latitude", String.valueOf(Latitude));
                jsonObject.put("Longitude", String.valueOf(Longitude));
                jsonObject.put("Notes", Notes);
                jsonObject.put("PickupExceptionID", eID);
                jsonObject.put("PSDID", bookingModel.getPickupsheetDetailID());
                jsonObject.put("PSID", bookingModel.getPickupSheetID());
                jsonObject.put("StationID", GlobalVar.GV().StationID);
                jsonObject.put("TimeIn", DateTime.now());
                jsonObject.put("UserID", GlobalVar.GV().UserID);
                jsonObject.put("WaybillNo", bookingModel.getWaybillNo());

                jsonArray.put(jsonObject);

                dbConnections.InsertPickUpException(getApplicationContext(), String.valueOf(bookingModel.getWaybillNo()),
                        bookingModel.getSPLOfficesID());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dbConnections
                .close();
        String jsonData = jsonArray.toString();

        new SavePickupException().execute(jsonData);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pickup:
                if (!isChecked)

                    Exception();
                else
                    BulkException();

                break;
            case R.id.exception:
                Intent intent = new Intent();
                // String result = rawResult.getContents();
                //Log.d("test", "result " + result);
                intent.putExtra("isFinish", true);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private class SavePickupException extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SpWaybillException.this);
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
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertPickupExceptionbyList"); //LoadtoDestination
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
                    crreateAlert(jsonObject.getString("ErrorMessage"), true);
                    if (!jsonObject.getBoolean("HasError")) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

                        int pos = 0;
                        Iterator<String> hashSet = exceptionIDs.iterator();
                        while (hashSet.hasNext()) {
                            String[] splStrings = hashSet.next().split("_");
                            int wNo = Integer.parseInt(splStrings[0]);
                            dbConnections.UpdatepickupsheetdetailsID(wNo, 1);
                            bookinglist.get(pos).setIsPickedup(1);
                            pos = pos + 1;
                        }

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


    private void crreateAlert(String msg, final boolean isFinish) {
        final android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(SpWaybillException.this);
        builder.setTitle(msg);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish) {
                    //BookingList.isFinish = true;
                    Intent intent = new Intent();
                    // String result = rawResult.getContents();
                    //Log.d("test", "result " + result);
                    intent.putExtra("isFinish", true);
                    setResult(RESULT_OK, intent);

                    finish();
                } else {
                    dialog.dismiss();
                }
            }
        });

        android.app.AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
}