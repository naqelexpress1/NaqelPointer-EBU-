package com.naqelexpress.naqelpointer.Activity.AtOriginNew;

import android.app.ProgressDialog;
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
    TextView waybillcount, piececount;
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
                //  adapter.setCallback(this);
                waybilgrid.setAdapter(adapter);


                waybillcount = (TextView) rootView.findViewById(R.id.waybillcount);
                piececount = (TextView) rootView.findViewById(R.id.piececount);

                txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);
                employeid = (EditText) rootView.findViewById(R.id.empid);


                Button pickdata = (Button) rootView.findViewById(R.id.pickup);
                pickdata.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (employeid.getText().toString().replaceAll(" ", "").length() > 0) {
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

                waybilldetails.clear();


            }

            return rootView;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // initViews();
    }

//    @Override
//    public void myStartActivityForResult(Intent intent, int requestCode) {
//        startActivityForResult(intent, requestCode);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        adapter.notifyDataSetChanged();
//    }

    private class GetPickUpData extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            //progressDialog.setMax(100);
            progressDialog.setMessage("Please wait.");
            progressDialog.setTitle("Downloading PickUp Data");
            progressDialog.show();
            progressDialog.setCancelable(false);
            //progressDialog = ProgressDialog.show(getActivity().getApplicationContext(), "Please wait.", "Downloading PickUp Data.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetArrivedAtOriginData");
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
                waybilldetails.clear();
                waybillBardetails.clear();

                FirstFragment.Selectedwaybilldetails.clear();
                SecondFragment.SelectedwaybillBardetails.clear();

                try {
                    JSONObject job = new JSONObject(finalJson);
                    JSONArray jsonArray = job.getJSONArray("Pickup");

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
                        waybillBardetails.add(tempwaybill);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else
                GlobalVar.GV().ShowSnackbar(rootView, "No data with Current Employee ID", GlobalVar.AlertType.Error);

            //ArrayList<HashMap<String, String>> wd = (ArrayList) waybilldetails;
            waybillcount.setText(String.valueOf(waybilldetails.size()));
            piececount.setText(String.valueOf(waybillBardetails.size()));
            adapter.notifyDataSetChanged();
            //adapter.updateData(waybilldetails);
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("waybilldetails", waybilldetails);
        outState.putSerializable("waybillBardetails", waybillBardetails);
        outState.putString("employeid", employeid.getText().toString());
        outState.putString("waybillcount", waybillcount.getText().toString());
        outState.putString("piececount", piececount.getText().toString());

        //outState.putStringArrayList("ShipmentBarCodeList", ShipmentBarCodeList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            waybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybilldetails");
            waybillBardetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybillBardetails");
        }
    }

}