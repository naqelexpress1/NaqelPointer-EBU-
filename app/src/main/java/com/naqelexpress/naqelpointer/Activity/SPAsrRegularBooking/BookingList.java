package com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Activity.BookingCBU.PickupSheetReasonModel;
import com.naqelexpress.naqelpointer.Activity.PickupAsrReg.PickUpActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.Global;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.Models.Request.PickupSheetSPASRRegResult;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.PickupSheetDetailsApi;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookingList extends AppCompatActivity implements AlertCallback {


    private SwipeMenuListView mapListview;
    private BookingListAdapter adapter;
    //public ArrayList<Booking> myBookingList;
    public static ArrayList<BookingModel> myBookingList;
    public static ArrayList<PickupSheetReasonModel> pickupSheetReasonModelArrayList;
    private TextView nodata;
    public static boolean isException = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.content_booking_list);

            mapListview = (SwipeMenuListView) findViewById(R.id.myBookingListView);
            mapListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            myBookingList = new ArrayList<>();
            pickupSheetReasonModelArrayList = new ArrayList<>();
            ID.clear();
            name.clear();
            pickupSheetReasonModelArrayList.clear();

            nodata = (TextView) findViewById(R.id.nodata);

            if (savedInstanceState == null)
                ReadfromLocal();


            mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    Cursor result = dbConnections.Fill("select * from PickUpAuto where IsSync = 0" +
                            " and WaybillNo=" +
                            myBookingList.get(position).WaybillNo, getApplicationContext());

                    if (result.getCount() == 0) {
                        //int pos = Integer.parseInt(((TextView) view.findViewById(R.id.sno)).getText().toString()) - 1;
                        RedirectPickupActivity(position);
                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "you picked up this item, please sync data", GlobalVar.AlertType.Error);
                    dbConnections.close();

                }
            });

        } catch
        (Exception ex) {
            System.out.println(ex.getMessage());
        }


    }

    private void RedirectPickupActivity(int pos) {
        try {

            Intent intent = new Intent(BookingList.this, PickUpActivity.class);
            // Bundle bundle = new Bundle();
            // bundle.putE("value", (Serializable) myBookingList.get(position));
            intent.putExtra("value", myBookingList);
            intent.putExtra("PRMA", pickupSheetReasonModelArrayList);

            intent.putExtra("position", pos);
            intent.putExtra("name", name);
            intent.putExtra("IDs", ID);
            //  bundle.putString("ID", String.valueOf(myBookingList.get(pos).PickupsheetDetailID));
            //intent.putExtras(bundle);

            startActivity(intent);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pickupsheetmenu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:

//                GetBookingList();
                FetchPickupsheetDetailsData();
                return true;
            case R.id.deleteall:
                deleteConfirmRoute();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAdapter() {
        adapter = new BookingListAdapter(BookingList.this, myBookingList, "BookingList");

        mapListview.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
                if (result == 0) {
                    //myBookingList
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

   /* private void GetBookingList() {

        // GlobalVar.GV().LoadMyBooking();
        //GlobalVar.GV().LoadMyBookingList("BringBookingList",true);
        JSONObject jsonObject = new JSONObject();
        try {


            jsonObject.put("EmployID", GlobalVar.GV().EmployID);


            jsonObject.put("AppTypeID", "");
            jsonObject.put("AppVersion", "");
            jsonObject.put("LanguageID", "");
            String jsonData = jsonObject.toString();

            new BringBookingData().execute(jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //adapter.notifyDataSetChanged();


    }*/

    @Override
    public void returnOk(int ok, Activity activity) {

    }

    @Override
    public void returnCancel(int cancel, SweetAlertDialog alertDialog) {
        BookingList.alertDialog = alertDialog;
    }

    @SuppressLint("StaticFieldLeak")
    static SweetAlertDialog alertDialog;

    private void exitdialog() {
        if (alertDialog != null) {
            alertDialog.dismissWithAnimation();
            alertDialog = null;
        }
    }

    private void FetchPickupsheetDetailsData() {
        GlobalVar.GV().alertMsgAll("Info", "Please wait, your request has been processing .", BookingList.this,
                Enum.PROGRESS_TYPE, "com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingList");

        CommonRequest commonRequest = new CommonRequest();

        commonRequest.setEmployID(GlobalVar.GV().EmployID);
        commonRequest.setPassword(DBConnections.getUserPassword(getApplicationContext(), GlobalVar.GV().EmployID));

        PickupSheetDetailsApi.fetchPickupsheetdetails
                (new Callback<PickupSheetSPASRRegResult>() {
                    @Override
                    public void returnResult(PickupSheetSPASRRegResult result) {
                        System.out.println();
                        deleteBookingData();
                        myBookingList.clear();
                        myBookingList = result.PickupSheet;
                        pickupSheetReasonModelArrayList = result.MissingReason;
                        PickupSheetDetails();
                    }

                    @Override
                    public void returnError(String message) {
                        //mView.showError(message);
                        System.out.println(message);
                        exitdialog();
                    }
                }, commonRequest);
    }


  /*  private class BringBookingData extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BookingList.this);
            pd.setTitle("Loading");
            pd.setMessage("Downloading your Booking Request ");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "PickupsheetDetailsWithLatLng"); //PickupsheetDetails
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
                deleteBookingData();
                myBookingList.clear();
                PickupSheetDetails(finalJson);
                pd.dismiss();

            } else {
                //
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
            }
            if (pd != null)
                pd.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }*/

    public static ArrayList<String> name = new ArrayList<>();
    public static ArrayList<Integer> ID = new ArrayList<>();

//    public void Booking(String finalJson) {
//        try {
//
//            JSONObject dataObject = new JSONObject(finalJson);
//            JSONArray jsonArray = dataObject.getJSONArray("PickupSheet");
//            JSONArray jsonArrayMs = dataObject.getJSONArray("MissingReason");
//            if (jsonArray.length() > 0) {
//                nodata.setVisibility(View.GONE);
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    Booking instance = new Booking();
//                    try {
//                        //instance.ID = Integer.parseInt(jsonObject.getString("id"));
//                        //hot code
//                        //instance.ID = 923122 + i;
//                        // Delete Old ID If found
//                        //        dbConnections.DeleteBooking( instance.ID);
//
//                        instance.PSID = jsonObject.getInt("PickupSheetID");
//                        instance.PSDID = jsonObject.getInt("PickupsheetDetailID");
//                        instance.RefNo = jsonObject.getString("WaybillNo");
//                        // instance.ClientID = Integer.parseInt(jsonObject.getString("ClientID"));
//                        //instance.ClientName = jsonObject.getString("ClientName");
//                        instance.PickUpReqDT = jsonObject.getString("Date");
//                        // instance.PicesCount = jsonObject.getDouble("PicesCount");
//                        // instance.Weight = Double.parseDouble(jsonObject.getString("Weight"));
//                        //instance.SpecialInstruction = jsonObject.getString("SpecialInstruction");
//                        //instance.OfficeUpTo = DateTime.parse(jsonObject.getString("OfficeUpTo"));
//                        //instance.PickUpReqDT = DateTime.parse(jsonObject.getString("PickUpReqDT"));
//                        instance.ContactPerson = jsonObject.getString("ConsigneeName");
//                        instance.ContactNumber = jsonObject.getString("PhoneNo");
//                        //instance.Address = jsonObject.getString("FirstAddress");
//                        //instance.Latitude = jsonObject.getString("Latitude");
//                        //instance.Longitude = jsonObject.getString("Longitude");
//                        //instance.GPSLocation = jsonObject.getString("GPSLocation");
//                        // instance.Status = jsonObject.getInt("CurrentStatusID");
//                        instance.Orgin = jsonObject.getString("OrgCode");
//                        instance.Destination = jsonObject.getString("DestCode");
//                        //instance.LoadType = jsonObject.getString("LoadType");
//                        //instance.BillType = jsonObject.getString("BillType");
//                        instance.BillType = jsonObject.getString("Code");
//                        //instance.EmployeeId = Integer.parseInt(jsonObject.getString("AssignedCourierEmployeeID"));
//                        //instance.OriginId = jsonObject.getInt("OriginStationID");
//                        //instance.DestinationId = jsonObject.getInt("DestinationStationID");
//
//
//                        //boolean v = dbConnections.InsertBooking(instance);
////                    System.out.println(v);
//
//
//                        myBookingList.add(new Booking(instance.PSID, instance.PSDID, instance.RefNo,
//                                instance.PickUpReqDT, instance.ContactPerson, instance.Orgin, instance.Destination, instance.BillType,
//                                instance.ContactNumber, (i + 1)
//                        ));
//
//                        // myBookingList.add(instance);
//
//                    } catch (JSONException ignored) {
//                        System.out.println(ignored);
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            } else
//                nodata.setVisibility(View.VISIBLE);
//
//            if (jsonArrayMs.length() > 0) {
//
//                for (int i = 0; i < jsonArrayMs.length(); i++) {
//
//                    JSONObject jsonObject = jsonArrayMs.getJSONObject(i);
//
//                    try {
//                        name.add(jsonObject.getString("Name"));
//
//                        ID.add(jsonObject.getInt("ID"));
//
//                    } catch (JSONException ignored) {
//                        System.out.println(ignored);
//                    }
//                }
//
//            }
//        } catch (JSONException ignored) {
//            System.out.println(ignored);
//        }
//    }


    /*public void PickupSheetDetails(String finalJson) {
        try {
            ArrayList<BookingModel> myBookingList = new ArrayList<>();
            ArrayList<PickupSheetReasonModel> pickupSheetReasonModels = new ArrayList<>();
            JSONObject dataObject = new JSONObject(finalJson);
            JSONArray jsonArray = dataObject.getJSONArray("PickupSheet");
            JSONArray jsonArrayMs = dataObject.getJSONArray("MissingReason");
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BookingModel instance = new BookingModel();
                    try {

                        instance.setsNo(i + 1);
                        instance.setPickupSheetID(jsonObject.getInt("PickupSheetID"));
                        instance.setPickupsheetDetailID(jsonObject.getInt("PickupsheetDetailID"));
                        instance.setWaybillNo(jsonObject.getInt("WaybillNo"));
                        instance.setFromStationID(jsonObject.getInt("FromStationID"));
                        instance.setToStationID(jsonObject.getInt("ToStationID"));
                        instance.setDate(jsonObject.getString("Date"));
                        instance.setConsigneeName(jsonObject.getString("ConsigneeName"));
                        instance.setPhoneNo(jsonObject.getString("PhoneNo"));
                        instance.setRemark(jsonObject.getString("Remark"));
                        instance.setOrgCode(jsonObject.getString("OrgCode"));
                        instance.setDestCode(jsonObject.getString("DestCode"));
                        instance.setClientID(jsonObject.getInt("ClientID"));
                        instance.setClientName(jsonObject.getString("ClientName"));
                        instance.setRefNo(jsonObject.getString("RefNo"));
                        instance.setGoodDesc(jsonObject.getString("GoodDesc"));
                        instance.setMobileNo(jsonObject.getString("MobileNo"));
                        String latlng = jsonObject.getString("LatLng");

                        savemobilenointocontacts(jsonObject.getString("PhoneNo"),
                                jsonObject.getString("MobileNo"), jsonObject.getString("ConsigneeName"),
                                String.valueOf(i + 1), String.valueOf(jsonObject.getInt("WaybillNo")));


                        String split[] = new String[2];
                        split[0] = "0";
                        split[1] = "0";
                        if (latlng != null && latlng.contains(",")) {
                            split = latlng.split(",");
                        }
                        instance.setLat(split[0]);
                        instance.setLng(split[1]);
                        instance.setCode(jsonObject.getString("Code"));
                        instance.setEmployID(jsonObject.getInt("EmployID"));


                        //instance.setIsPickedup(jsonObject.getInt("IsPickedup"));
                        instance.setIsPickedup(0);
                        myBookingList.add(instance);


                    } catch (JSONException ignored) {
                        System.out.println(ignored);
                    }
                }


                dbConnections.insertPickupsheetDetailsData(myBookingList, getApplicationContext());
            }

            if (jsonArrayMs.length() > 0) {

                for (int i = 0; i < jsonArrayMs.length(); i++) {

                    JSONObject jsonObject = jsonArrayMs.getJSONObject(i);

                    try {
                        PickupSheetReasonModel reasonModel = new PickupSheetReasonModel();
                        // name.add(jsonObject.getString("Name"));
                        //ID.add(jsonObject.getInt("ID"));
                        reasonModel.setID(jsonObject.getInt("ID"));
                        reasonModel.setName(jsonObject.getString("Name"));
                        pickupSheetReasonModels.add(reasonModel);

                    } catch (JSONException ignored) {
                        System.out.println(ignored);
                    }
                }
                dbConnections.insertPickupsheetReasonData(pickupSheetReasonModels, getApplicationContext());

            }
            dbConnections.close();
            ReadfromLocal();
        } catch (JSONException ignored) {
            System.out.println(ignored);
        }
    }*/

    public void PickupSheetDetails() {
        try {

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            if (myBookingList.size() > 0)
                dbConnections.insertPickupsheetDetails_SPASRREGData(myBookingList, getApplicationContext());

            if (pickupSheetReasonModelArrayList.size() > 0)
                dbConnections.insertPickupsheetReasonData(pickupSheetReasonModelArrayList, getApplicationContext());


            dbConnections.close();

            ReadfromLocal();
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }

    private void savemobilenointocontacts(String phoneno, String mno, String name, String SNo, String WaybillNo) {
        ArrayList<String> MNos = new ArrayList<>();
        if (!phoneno.equals("null") && phoneno != null && !phoneno.equals("0")
                && phoneno.length() > 0)

            MNos.add(phoneno);


        if (!mno.equals("null") && mno != null &&
                !mno.equals("0") && mno.length() > 0) {

            MNos.add(mno);
        }

        if (MNos.size() > 0) {
            Global global = new Global(BookingList.this);
            global.addMobileNumberintoContacts(SNo + " - ASR - " + WaybillNo, MNos, WaybillNo);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("myBookingList", myBookingList);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            // myBookingList = savedInstanceState.getParcelableArrayList("myBookingList");
            setAdapter();
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isException)
            setAdapter();
        isException = false;
    }

    public void ReadfromLocal() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        GlobalVar.GV().EmployID = 19127;
        myBookingList =
                dbConnections.getPickupSheetSpAsrRegDetailsData(getApplicationContext(), GlobalVar.GV().EmployID);
        exitdialog();
        if (myBookingList.size() > 0) {
            setAdapter();
            nodata.setVisibility(View.GONE);

            mapListview.setVisibility(View.VISIBLE);
            pickupSheetReasonModelArrayList = dbConnections.getPickupSheetDetailsReasonData(getApplicationContext());
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void deleteConfirmRoute() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(BookingList.this);
        builder1.setTitle("Info");
        builder1.setMessage("Do you want to delete all? ");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        //Global global = new Global(BookingList.this);
                        GlobalVar.GV().alertMsgAll("", "Please wait", BookingList.this, Enum.PROGRESS_TYPE,
                                "BookingList");
//                        global.DeleteContact();
                        new DeleteContact().execute("");
                        deleteBookingData();

//                        global.DeleteContact = new Global.DeleteContact();
//
//                        new Global.DeleteContact().execute("http://images.com/image.jpg");

                        //finish();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void deleteBookingData() {
        myBookingList.clear();
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.clearAllPickupsheetData(getApplicationContext());

        dbConnections.close();
    }

    public class DeleteContact extends AsyncTask<String, String, String> {


        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            try {
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                boolean loop = false;
                loop = GlobalVar.deleteContactRawID(dbConnections.PickupSheetContactDetails(getApplicationContext()), getApplicationContext(), 1);
                int time = 1000;
                while (!loop)
                    Thread.sleep(time);


            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
//            if (progressDialog != null && progressDialog.isShowing())
//                progressDialog.dismiss();
            finish();

        }


        @Override
        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(activity,
//                    "Info",
//                    "Your Request is being process,kindly please wait");
        }


        @Override
        protected void onProgressUpdate(String... text) {


        }

    }
}


