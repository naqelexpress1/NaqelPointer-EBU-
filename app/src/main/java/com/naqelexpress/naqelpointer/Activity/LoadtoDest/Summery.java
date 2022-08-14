package com.naqelexpress.naqelpointer.Activity.LoadtoDest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

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
import java.util.Map;

public class Summery extends AppCompatActivity implements View.OnClickListener {

    ArrayList<String> validatewaybilldetails = new ArrayList<>();
    ArrayList<String> validateBarcodeetails = new ArrayList<>();
    ArrayList<String> validateSingledetails = new ArrayList<>();
    private SwipeMenuListView swipeMenuListView;
    private SummeryAdapter adapter;
    //private TextView waybillcount, buildpalletcont, singleloadcount;

    ArrayList<HashMap<String, String>> waybills = new ArrayList<>();
    ArrayList<HashMap<String, String>> barcodes = new ArrayList<>();
    Point p;
    String selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.summery);

        TextView waybillcount = (TextView) findViewById(R.id.wbtotal);
        TextView buildpalletcont = (TextView) findViewById(R.id.bptotal);
        TextView singleloadcount = (TextView) findViewById(R.id.sltotal);


        Bundle bundle = getIntent().getExtras();
        validatewaybilldetails = bundle.getStringArrayList("WayBills");
        validateBarcodeetails = bundle.getStringArrayList("BarCode");
        //validateSingledetails  = bundle.getStringArrayList("SingleLoad");

        waybillcount.setText(String.valueOf(validatewaybilldetails.size()));
        buildpalletcont.setText(String.valueOf(bundle.getInt("Pallet")));
        singleloadcount.setText(String.valueOf(bundle.getInt("SingleLoad")));


        swipeMenuListView = (SwipeMenuListView) findViewById(R.id.pieceslist);

        adapter = new SummeryAdapter(LoadtoDestination.summeryList, getApplicationContext());
        swipeMenuListView.setAdapter(adapter);

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();

                if (p != null)
                    showPopup(Summery.this, p);

            }
        });


        new GetSummery().execute("");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.loadtodest, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.summery:
                new GetSummery().execute("");
                return true;
            case R.id.save:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        TextView textView = (TextView) findViewById(R.id.wb);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        textView.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    PopupWindow popup;

    private void showPopup(final Activity context, Point p) {


        //int popupWidth = 200;
        //int popupHeight = 150;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.singleloadpopup, viewGroup);

        TextView wo = (TextView) layout.findViewById(R.id.waybillonly);
        wo.setOnClickListener(this);
        TextView bs = (TextView) layout.findViewById(R.id.balanceshipment);
        bs.setOnClickListener(this);
        TextView ct = (TextView) layout.findViewById(R.id.connectonnext);
        ct.setOnClickListener(this);
        TextView pn = (TextView) layout.findViewById(R.id.pcenotrecev);
        pn.setOnClickListener(this);
        TextView ph = (TextView) layout.findViewById(R.id.pcshold);
        ph.setOnClickListener(this);


        popup = new PopupWindow(context);
        popup.setContentView(layout);
        //popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);

        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;
        String remarks = textView.getText().toString();
        switch (view.getId()) {
            case R.id.waybillonly:
                updateRemarks(remarks);
                break;
            case R.id.balanceshipment:
                updateRemarks(remarks);
                break;
            case R.id.connectonnext:
                updateRemarks(remarks);
                break;
            case R.id.pcenotrecev:
                updateRemarks(remarks);
                break;
            case R.id.pcshold:
                updateRemarks(remarks);
                break;
        }
    }

    private void updateRemarks(String remarks) {
        String value = selectedItem;
        value = value.substring(1, value.length() - 1);
        String[] keyValuePairs = value.split(",");
        Map<String, String> temp = new HashMap<>();

        for (String pair : keyValuePairs) {
            String[] entry = pair.split("=");
            temp.put(entry[0].trim(), entry[1].trim());
        }


        for (int i = 0; i < LoadtoDestination.summeryList.size(); i++) {
            String exiswn = LoadtoDestination.summeryList.get(i).get("WaybillNo");
            if (exiswn.equals(temp.get("WaybillNo"))) {
                LoadtoDestination.summeryList.get(i).put("Remarks", remarks);
                break;
            }
        }
        popup.dismiss();
    }

    private class GetSummery extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Summery.this);
            //progressDialog.setMax(100);
            progressDialog.setMessage("Kindly please wait.");
            progressDialog.setTitle("Please wait");
            progressDialog.show();
            progressDialog.setCancelable(false);

            jsonObject = new JSONObject();
            try {
                StringBuilder builder = new StringBuilder();
                StringBuilder builderbarcode = new StringBuilder();
                if (validatewaybilldetails.size() > 0) {
                    for (int i = 0; i < validatewaybilldetails.size(); i++) {
                        String waybill = validatewaybilldetails.get(i);
                        if (i == validatewaybilldetails.size() - 1)
                            builder.append(waybill);
                        else
                            builder.append(waybill + ",");
                    }
                } else {
                    builder.append("0");
                }
                if (validateBarcodeetails.size() > 0) {
                    for (int i = 0; i < validateBarcodeetails.size(); i++) {
                        String waybill = validateBarcodeetails.get(i);
                        if (i == validateBarcodeetails.size() - 1)
                            builderbarcode.append("'" + waybill + "'");
                        else
                            builderbarcode.append("'" + waybill + "',");
                    }
                } else
                    builderbarcode.append("'0'");

                jsonObject.put("WayBills", builder);
                jsonObject.put("BarCodes", builderbarcode);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = jsonObject.toString();
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;
            String error = "";

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetLoadtoDestSummery");
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
                error = e.toString();
                e.printStackTrace();
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    error = e.toString();
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    error = e.toString();
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return error;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null) {

                ArrayList<HashMap<String, String>> forRemarks = new ArrayList<>();
                forRemarks.addAll(LoadtoDestination.summeryList);
                LoadtoDestination.summeryList.clear();
                barcodes.clear();
                waybills.clear();
                ArrayList<String> duplwaybill = new ArrayList<>();
                ArrayList<String> duplbwaybill = new ArrayList<>();
                try {
                    JSONObject job = new JSONObject(finalJson);
                    JSONArray jsonArray = job.getJSONArray("Pickup");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject = jsonArray.getJSONObject(i);
                        String WayBill = jsonObject.getString("WaybillNo");
                        for (int j = 0; j < WayBillDetails.validatewaybilldetails.size(); j++) {
                            if (WayBillDetails.validatewaybilldetails.get(j).equals(WayBill)) {
                                HashMap<String, String> tempwaybill = new HashMap<>();

                                tempwaybill.put("WaybillNo", jsonObject.getString("WaybillNo"));
                                tempwaybill.put("PieceCount", String.valueOf(jsonObject.getInt("PieceCount")));
                                tempwaybill.put("EmployID", String.valueOf(GlobalVar.GV().EmployID));
                                tempwaybill.put("UserID", String.valueOf(GlobalVar.GV().UserID));
                                tempwaybill.put("IsSync", "false");
                                tempwaybill.put("StationID", jsonObject.getString("ToStationID"));
                                tempwaybill.put("ScannedPC", "0");
                                tempwaybill.put("pending", String.valueOf(jsonObject.getInt("PieceCount")));
                                tempwaybill.put("bgcolor", "0");
                                tempwaybill.put("SW", "1");
                                tempwaybill.put("ByBarcode", "0");
                                tempwaybill.put("Remarks", " ");
                                duplwaybill.add(jsonObject.getString("WaybillNo"));
                                waybills.add(tempwaybill);
                            }
                        }

                    }

                    JSONArray jsonArray1 = job.getJSONArray("PickupBarCode");

                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObject = jsonArray1.getJSONObject(i);
                        HashMap<String, String> tempwaybill = new HashMap<>();
                        tempwaybill.put("WaybillNo", jsonObject.getString("WaybillNo"));
                        tempwaybill.put("BarCode", jsonObject.getString("BarCode"));
                        tempwaybill.put("IsSync", "false");
                        tempwaybill.put("bgcolor", "0");
                        tempwaybill.put("SW", "0");
                        tempwaybill.put("PieceCount", "0");
                        tempwaybill.put("EmployID", String.valueOf(GlobalVar.GV().EmployID));
                        tempwaybill.put("UserID", String.valueOf(GlobalVar.GV().UserID));
                        tempwaybill.put("StationID", "0");
                        tempwaybill.put("ScannedPC", "0");
                        tempwaybill.put("pending", "0");
                        duplbwaybill.add(jsonObject.getString("WaybillNo"));
                        tempwaybill.put("ByBarcode", "1");
                        tempwaybill.put("Remarks", " ");

                        barcodes.add(tempwaybill);
                    }

                    ArrayList<String> addwaybill = new ArrayList<>();
                    if (barcodes.size() > 0) {

                        for (int k = 0; k < barcodes.size(); k++) {
                            boolean add = true;
                            String BarcodeWayBill = barcodes.get(k).get("WaybillNo");
                            for (int l = 0; l < waybills.size(); l++) {
                                String waybill = waybills.get(l).get("WaybillNo");
                                if (duplbwaybill.contains(waybill)) {
                                    if (waybill.equals(BarcodeWayBill)) {
                                        add = false;
                                        String sp = waybills.get(l).get("ScannedPC");
                                        int scnpiece = Integer.parseInt(sp) + 1;
                                        waybills.get(l).put("ScannedPC", String.valueOf(scnpiece));
                                        String rr = waybills.get(l).get("ByBarcode");
                                        if (rr.equals("0")) {
                                            String pieceCount = waybills.get(l).get("PieceCount");
                                            int pending = Integer.parseInt(pieceCount) - scnpiece;
                                            waybills.get(l).put("pending", String.valueOf(pending));
                                            if (pending == 0)
                                                waybills.get(l).put("bgcolor", "1");
                                            else
                                                waybills.get(l).put("bgcolor", "0");
                                            waybills.get(l).put("SW", "1");
                                        }

                                        boolean has = true;
                                        for (int m = 0; m < LoadtoDestination.summeryList.size(); m++) {
                                            String sw = LoadtoDestination.summeryList.get(m).get("WaybillNo");
                                            if (sw.equals(waybill)) {
                                                has = false;
                                                HashMap<String, String> temp = new HashMap<>();
                                                temp.putAll(waybills.get(l));
                                                LoadtoDestination.summeryList.get(m).putAll(temp);
                                                break;
                                            }

                                        }
                                        if (has)
                                            LoadtoDestination.summeryList.add(waybills.get(l));
                                        break;
                                    }
                                } else {
                                    if (!addwaybill.contains(waybill)) {
                                        LoadtoDestination.summeryList.add(waybills.get(l));
                                        addwaybill.add(waybill);
                                    }
                                }
                            }
                            if (add) {
                                String sp = barcodes.get(k).get("ScannedPC");
                                int scnpiece = Integer.parseInt(sp) + 1;
                                barcodes.get(k).put("ScannedPC", String.valueOf(scnpiece));
                                LoadtoDestination.summeryList.add(barcodes.get(k));
                                waybills.add(barcodes.get(k));
                            }
                        }
                    } else
                        LoadtoDestination.summeryList.addAll(waybills);

                    if (LoadtoDestination.summeryList.size() != waybills.size()) {
                        int j = LoadtoDestination.summeryList.size();
                        for (int i = 0; i < waybills.size() - LoadtoDestination.summeryList.size(); i++) {
                            LoadtoDestination.summeryList.add(waybills.get(j));
                            j++;

                        }
                    }

                    for (int i = 0; i < forRemarks.size(); i++) {
                        for (int j = 0; j < LoadtoDestination.summeryList.size(); j++) {
                            String lslw = LoadtoDestination.summeryList.get(j).get("WaybillNo");
                            String rw = forRemarks.get(i).get("WaybillNo");
                            if (lslw.equals(rw)) {
                                LoadtoDestination.summeryList.get(j).put("Remarks", forRemarks.get(i).get("Remarks"));
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    if (finalJson.contains("java.net.ConnectException"))
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly please check your internet connection", GlobalVar.AlertType.Error);
                    e.printStackTrace();
                }

            } else {

                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "No data with these Data's", GlobalVar.AlertType.Error);
            }


            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Delivery")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Summery.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


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
}