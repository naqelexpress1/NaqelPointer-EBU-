package com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBConnections;
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

public class CourierDetails extends Fragment // implements ResultInterface
{
    View rootView;
    private EditText txtBarCode;
    TextView waybillcount, piececount, selectedwaybillcount;
    static CourierAdapterNew adapter;
    private GridView waybilgrid;
    EditText employeid;
    static ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
    static ArrayList<HashMap<String, String>> waybillBardetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.atoriginfirstcourierfrg, container, false);

                waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
                adapter = new CourierAdapterNew(waybilldetails, getContext());
                waybilgrid.setAdapter(adapter);


                waybillcount = (TextView) rootView.findViewById(R.id.waybillcount);
                selectedwaybillcount = (TextView) rootView.findViewById(R.id.selectedwaybillcount);
                piececount = (TextView) rootView.findViewById(R.id.piececount);

                txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);
                employeid = (EditText) rootView.findViewById(R.id.empid);


                Button pickdata = (Button) rootView.findViewById(R.id.pickup);
                pickdata.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (employeid.getText().toString().replaceAll(" ", "").length() > 0) {

                            clearfields();
                            DBConnections dbConnections = new DBConnections(getContext(), null);
                            dbConnections.deleteAtOriginLastScans(getContext());
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("EmployID", employeid.getText().toString());

                                String jsonData = jsonObject.toString();

                                new GetPickUpData().execute(jsonData);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                ReadFromLocal();

            }

            return rootView;
        }
    }

    private void clearfields() {
        waybilldetails.clear();
        waybillBardetails.clear();
        FirstFragment.Selectedwaybilldetails.clear();
        FirstFragment.validatewaybilldetails.clear();
        SecondFragment.SelectedwaybillBardetails.clear();
        SecondFragment.ValidateBarCodeList.clear();
        selectedwaybillcount.setText("0");
        if (FirstFragment.adapter != null)
            FirstFragment.adapter.notifyDataSetChanged();
        if (SecondFragment.adapter != null) {
            SecondFragment.adapter.notifyDataSetChanged();
            SecondFragment.lbTotal.setText("0");
        }
    }

    private void ReadFromLocal() {
        clearfields();
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from AtOriginLastWaybill ", getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            int selectedwayabill = 0;
            do {
                employeid.setText(result.getString(result.getColumnIndex("EmployID")));

                HashMap<String, String> tempwaybill = new HashMap<>();
                tempwaybill.put("WaybillNo", result.getString(result.getColumnIndex("WaybillNo")));
                tempwaybill.put("PieceCount", result.getString(result.getColumnIndex("PieceCount")));
                tempwaybill.put("EmployID", result.getString(result.getColumnIndex("EmployID")));
                tempwaybill.put("UserID", result.getString(result.getColumnIndex("UserID")));
                tempwaybill.put("IsSync", result.getString(result.getColumnIndex("IsSync")));
                tempwaybill.put("StationID", result.getString(result.getColumnIndex("StationID")));
                tempwaybill.put("ScannedPC", result.getString(result.getColumnIndex("ScannedPC")));
                tempwaybill.put("bgcolor", result.getString(result.getColumnIndex("bgcolor")));
                tempwaybill.put("isdelete", result.getString(result.getColumnIndex("isdelete")));

                if (result.getString(result.getColumnIndex("bgcolor")).equals("1")) {
                    selectedwayabill = selectedwayabill + 1;
                    FirstFragment.Selectedwaybilldetails.add(tempwaybill);
                    FirstFragment.validatewaybilldetails.add(result.getString(result.getColumnIndex("WaybillNo")));
                }
                waybilldetails.add(tempwaybill);

            }
            while (result.moveToNext());

            waybillcount.setText(String.valueOf(result.getCount()));
            selectedwaybillcount.setText(String.valueOf(selectedwayabill));
            if (FirstFragment.adapter != null)
                FirstFragment.adapter.notifyDataSetChanged();
        }
        result.close();

        result = dbConnections.Fill("select * from AtOriginLastPieces ", getContext());
        int pc = 0;
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {

                HashMap<String, String> tempbarcode = new HashMap<>();
                tempbarcode.put("WaybillNo", result.getString(result.getColumnIndex("WaybillNo")));
                tempbarcode.put("BarCode", result.getString(result.getColumnIndex("BarCode")));
                tempbarcode.put("IsSync", result.getString(result.getColumnIndex("IsSync")));
                tempbarcode.put("bgcolor", result.getString(result.getColumnIndex("bgcolor")));
                tempbarcode.put("isdelete", result.getString(result.getColumnIndex("isdelete")));

                if (result.getString(result.getColumnIndex("bgcolor")).equals("1")) {
                    pc++;
                    SecondFragment.ValidateBarCodeList.add(result.getString(result.getColumnIndex("BarCode")));
                    SecondFragment.SelectedwaybillBardetails.add(tempbarcode);
                }
                waybillBardetails.add(tempbarcode);
            }
            while (result.moveToNext());

            piececount.setText(String.valueOf(result.getCount()));
            if (SecondFragment.adapter != null) {
                SecondFragment.adapter.notifyDataSetChanged();
                SecondFragment.lbTotal.setText(String.valueOf(pc));
            }
        }
        result.close();

        dbConnections.close();
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    private class GetPickUpData extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait.");
            progressDialog.setTitle("Downloading PickUp Data");
            progressDialog.show();
            progressDialog.setCancelable(false);
            DomainURL = GlobalVar.GV().GetDomainURL(getContext());
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(DomainURL + "GetArrivedAtOriginData");  //GlobalVar.GV().NaqelPointerAPILink

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
                isInternetAvailable = e.toString();
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
                waybilldetails.clear();
                waybillBardetails.clear();

                FirstFragment.Selectedwaybilldetails.clear();
                SecondFragment.SelectedwaybillBardetails.clear();

                try {
                    JSONObject job = new JSONObject(finalJson);
                    JSONArray jsonArray = job.getJSONArray("Pickup");

                    DBConnections dbConnections = new DBConnections(getContext(), null);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> tempwaybill = new HashMap<>();
                        tempwaybill.put("WaybillNo", jsonObject.getString("WaybillNo"));
                        tempwaybill.put("PieceCount", String.valueOf(jsonObject.getInt("PieceCount")));
                        tempwaybill.put("EmployID", String.valueOf(jsonObject.getInt("EmployID")));
                        tempwaybill.put("UserID", String.valueOf(jsonObject.getInt("UserID")));
                        tempwaybill.put("IsSync", "false");
                        tempwaybill.put("StationID", "StationID");
                        tempwaybill.put("ScannedPC", "0");
                        tempwaybill.put("bgcolor", "0");
                        tempwaybill.put("isdelete", "0");
                        dbConnections.InsertAtOriginWaybill(jsonObject.getString("WaybillNo"), String.valueOf(jsonObject.getInt("PieceCount")),
                                String.valueOf(jsonObject.getInt("EmployID")), String.valueOf(jsonObject.getInt("UserID")), getContext());
                        waybilldetails.add(tempwaybill);
                    }
                    JSONArray jsonArray1 = job.getJSONArray("PickupBarCode");

                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObject = jsonArray1.getJSONObject(i);
                        HashMap<String, String> tempwaybill = new HashMap<>();
                        tempwaybill.put("WaybillNo", jsonObject.getString("WaybillNo"));
                        tempwaybill.put("BarCode", jsonObject.getString("BarCode"));
                        tempwaybill.put("IsSync", "false");
                        tempwaybill.put("bgcolor", "0");
                        tempwaybill.put("isdelete", "0");

                        dbConnections.InsertAtOriginPieces(jsonObject.getString("WaybillNo"), jsonObject.getString("BarCode"),
                                getContext());

                        waybillBardetails.add(tempwaybill);
                    }
                    dbConnections.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else{
                //GlobalVar.GV().ShowSnackbar(rootView, "No data with Current Employee ID", GlobalVar.AlertType.Error);
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(rootView, "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        GlobalVar.GV().SwitchoverDomain(getContext(), DomainURL);

                    }

                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.servererror), GlobalVar.AlertType.Error);
                }
            }

            waybillcount.setText(String.valueOf(waybilldetails.size()));
            piececount.setText(String.valueOf(waybillBardetails.size()));
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putSerializable("waybilldetails", waybilldetails);
        // outState.putSerializable("waybillBardetails", waybillBardetails);
        // outState.putString("employeid", employeid.getText().toString());
        // outState.putString("waybillcount", waybillcount.getText().toString());
        // outState.putString("piececount", piececount.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //waybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybilldetails");
            //waybillBardetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybillBardetails");
        }
    }

}