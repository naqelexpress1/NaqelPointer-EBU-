package com.naqelexpress.naqelpointer.Activity.SPbookingGroup;

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
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.Activity.BookingCBU.PickupSheetReasonModel;
import com.naqelexpress.naqelpointer.Activity.PickupAsrReg.PickUpActivity;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingList;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingModel;
import com.naqelexpress.naqelpointer.Activity.SPbookingException.SpWaybillException;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
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

public class SpWaybillGroup extends AppCompatActivity {

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
    private WaybillGroupAdapter adapter;
    private TextView nodata;
    double Latitude = 0, Longitude = 0;
    public static ArrayList<String> waybilllist;
    public static boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.spsregular_groupbookinglist);


        Bundle bundle = getIntent().getExtras();

        RefNo = "";

        isFinish = false;
        waybilllist = new ArrayList<>();

        position = bundle.getInt("position");

        bookinglist = (ArrayList<BookingModel>)
                getIntent().getSerializableExtra("value");

        pickupSheetReasonModelArrayList = (ArrayList<PickupSheetReasonModel>)
                getIntent().getSerializableExtra("PRMA");

        if (pickupSheetReasonModelArrayList.size() > 0)
            fetchPickupsheetReasons();


        mapListview = (SwipeMenuListView) findViewById(R.id.myBookingListView);
        mapListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        nodata = (TextView) findViewById(R.id.nodata);

        //  setAdapter();

        ReadfromLocal();

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

        Button exception = (Button) findViewById(R.id.exception);
        exception.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    showPopup();

                Exception();

            }
        });

        Button riderct = (Button) findViewById(R.id.pickup);
        riderct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    showPopup();

                RedirectPickupActivity(0);

            }
        });
    }

    private void setAdapter() {
        adapter = new WaybillGroupAdapter(SpWaybillGroup.this, bookinglist, "BookingList");

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


            Intent intent = new Intent(SpWaybillGroup.this, PickUpActivity.class);
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

        String wnos = waybilllist.toString();
        wnos
                = wnos.replace("[", "")
                .replace("]", "")
                .replace(" ", "");

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        ArrayList<String> WaybillList = dbConnections.getNotPickedupList(wnos, getApplicationContext());
        if (WaybillList.size() == bookinglist.size())
            crreateAlert("There is no pending shipments for Exception");
        else if (WaybillList.size() == 0)
            redirectException(waybilllist, 0);
        else if (WaybillList.size() > 0) {
            ArrayList<String> wlist = new ArrayList<>();
            wlist.addAll(waybilllist);
            wlist.removeAll(WaybillList);
            redirectException(wlist, 1);
        }

    }

    private void redirectException(ArrayList<String> WaybillList, int noCondition) {
        ArrayList<BookingModel> blist = new ArrayList<>();
        if (noCondition == 1) {
            for (String Wno : WaybillList) {
                for (BookingModel bookingModel : bookinglist) {
                    if (bookingModel.getWaybillNo() == Integer.parseInt(Wno)) {
                        blist.add(bookingModel);
                        break;
                    }
                }
            }
        } else
            blist.addAll(bookinglist);

        Intent intent = new Intent(SpWaybillGroup.this, SpWaybillException.class);
        intent.putExtra("PRMA", pickupSheetReasonModelArrayList);
        intent.putExtra("waybilllist", WaybillList);
        intent.putExtra("blist", blist);
        intent.putExtra("value", bookinglist);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("isFinish")) {
                        boolean finish = extras.getBoolean("isFinish");
                        if (finish) {
                            BookingList.isFinish = true;
                            finish();
                        }
                    }
                }

            }
        }
    }


  /*  private void Exception() {

        requestLocation();

        android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(SpWaybillGroup.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Pickup Exception");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (SpWaybillGroup.this, android.R.layout.select_dialog_item);

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
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SpWaybillGroup.this);
                builder.setTitle(strName);

                final EditText notes = new EditText(SpWaybillGroup.this);
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
                        JSONArray jsonArray = new JSONArray();

                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        for (BookingModel bookingModel : bookinglist) {
                            JSONObject jsonObject = new JSONObject();
                            try {


                                jsonObject.put("EmployID", GlobalVar.GV().EmployID);


                                jsonObject.put("Latitude", String.valueOf(Latitude));
                                jsonObject.put("Longitude", String.valueOf(Longitude));
                                jsonObject.put("Notes", Notes);
                                jsonObject.put("PickupExceptionID", exceptionID);
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
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                android.app.AlertDialog dialog1 = builder.create();
                dialog1.setView(notes);
                dialog1.show();
            }
        });
        builderSingle.show();
    }*/

    private class SavePickupException extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SpWaybillGroup.this);
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
                    crreateAlert(jsonObject.getString("ErrorMessage"));
                    if (!jsonObject.getBoolean("HasError")) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

                        int pos = 0;
                        for (BookingModel bookingModel : bookinglist) {
                            dbConnections.UpdatepickupsheetdetailsID(bookingModel.getWaybillNo(), 1);
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


    private void crreateAlert(String msg) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SpWaybillGroup.this);
        builder.setTitle(msg);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BookingList.isFinish = true;
                finish();
            }
        });

        android.app.AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
}