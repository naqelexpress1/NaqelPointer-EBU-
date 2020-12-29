package com.naqelexpress.naqelpointer.Activity.Delivery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.GetWaybillDetailsRequest;
import com.naqelexpress.naqelpointer.JSON.Results.WaybillDetailsResult;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.TELEPHONY_SERVICE;

public class DeliveryFirstFragment
        extends Fragment {

    public static ArrayList<String> ShipmentBarCodeList = new ArrayList<>();
    public static EditText txtWaybillNo;
    private TextView txtConsigneeName;
    public TextView txtBillingType;
    private TextView txtCODAmount;
    private TextView txtWeight;
    private TextView txtPiecesCount;
    private TextView txtAddress;
    private TextView txtSecondAddress;
    private TextView txtNear;
    private TextView txtMobileNo;
    private TextView txtPhoneNo;
    private View rootView;
    Button btnOpenCamera;
    String shippername = "";

    AppCompatImageButton btnCallMobile, btnCallMobile1;
    AppCompatImageButton btnWhatsApp, btnWhatsApp1;
    TelephonyManager mTelephonyManager;
    boolean watsapp_sms = false;
    boolean signrequired = false;
    static int al = 0;
    public static String Lat = "0", Longi = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {


            rootView = inflater.inflate(R.layout.deliveryfirstfragment, container, false);
            mTelephonyManager = (TelephonyManager) rootView.getContext().getSystemService(TELEPHONY_SERVICE);

            Lat = "0";
            Longi = "0";

            CheckBox actualLocation = (CheckBox) rootView.findViewById(R.id.alocation);
            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtConsigneeName = (TextView) rootView.findViewById(R.id.txtConsigneeName);
            txtBillingType = (TextView) rootView.findViewById(R.id.txtBillingType);
            txtCODAmount = (TextView) rootView.findViewById(R.id.txtCODAmount);
            txtWeight = (TextView) rootView.findViewById(R.id.txtWeight);
            txtPiecesCount = (TextView) rootView.findViewById(R.id.txtPiecesCount);
            txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
            txtSecondAddress = (TextView) rootView.findViewById(R.id.txtSecondAddress);
            txtNear = (TextView) rootView.findViewById(R.id.txtNear);
            txtMobileNo = (TextView) rootView.findViewById(R.id.txtMobileNo);
            txtPhoneNo = (TextView) rootView.findViewById(R.id.txtPhoneNo);
            btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            btnCallMobile = (AppCompatImageButton) rootView.findViewById(R.id.btnCall);
            btnWhatsApp = (AppCompatImageButton) rootView.findViewById(R.id.btnWhatsapp);
            btnCallMobile1 = (AppCompatImageButton) rootView.findViewById(R.id.btnCall1);
            btnWhatsApp1 = (AppCompatImageButton) rootView.findViewById(R.id.btnWhatsapp1);

            txtMobileNo.setTag(0);
            txtPhoneNo.setTag(0);

            actualLocation.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //is chkIos checked?
                    if (((CheckBox) v).isChecked()) {
                        al = 1;
                    } else
                        al = 0;

                }
            });
            String division = GlobalVar.getDivision(getContext());
            if (division.equals("Express"))
                actualLocation.setVisibility(View.GONE);

            btnCallMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(txtMobileNo.getTag().toString(), rootView, getActivity());
                }
            });
            btnCallMobile1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVar.GV().makeCall(txtPhoneNo.getTag().toString(), rootView, getActivity());
                }
            });


            btnWhatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobileno = txtMobileNo.getTag().toString();
                    if (!mobileno.equals("null") && mobileno != null && mobileno.length() > 0) {
                        if (mobileno.length() == 10) {
                            String validate = mobileno.substring(0, 1);
                            if (validate.equals("0"))
                                mobileno = mobileno.replaceFirst("0", "+966");
                        } else {
                            if (mobileno.length() > 10) {
                                //String validate = mobileno.substring(0, 2);
                                if (mobileno.contains("00966"))
                                    mobileno = mobileno.replaceFirst("00966", "+966");
                            } else if (mobileno.length() == 9) {
                                mobileno = "+966" + mobileno;
                            }
                        }

                        if (watsapp_sms)
//                            GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg) + " " + txtWaybillNo.getText().toString() + "." + getString(R.string.watsappPredefinedMsg1) + txtWaybillNo.getText().toString()
//                                    , getActivity().getApplicationContext());
                            showPopup(mobileno);
                        else {
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                                if (GlobalVar.simISavailable(getContext()))
                                    GlobalVar.GV().sendSMS(mobileno, getString(R.string.watsappPredefinedMsg) + " " + txtWaybillNo.getText().toString() + "." + getString(R.string.watsappPredefinedMsg1) + txtWaybillNo.getText().toString()
                                            , getActivity().getApplicationContext());
                                else
                                    GlobalVar.GV().ShowSnackbar(rootView, "Kindly insert valid sim card", GlobalVar.AlertType.Error);
                            else
                                ActivityCompat.requestPermissions(
                                        getActivity(),
                                        new String[]{Manifest.permission.SEND_SMS},
                                        1
                                );
                        }
                    } else
                        GlobalVar.GV().ShowSnackbar(rootView, "Is not valid mobile number.", GlobalVar.AlertType.Warning);
                }
            });

            btnWhatsApp1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobileno = txtPhoneNo.getTag().toString();
                    if (!mobileno.equals("null") && mobileno != null && mobileno.length() > 0) {
                        if (mobileno.length() == 10) {
                            String validate = mobileno.substring(0, 1);
                            if (validate.equals("0"))
                                mobileno = mobileno.replaceFirst("0", "+966");
                        } else {
                            if (mobileno.length() > 10) {
                                //String validate = mobileno.substring(0, 2);
                                if (mobileno.contains("00966"))
                                    mobileno = mobileno.replaceFirst("00966", "+966");
                            } else if (mobileno.length() == 9) {
                                mobileno = "+966" + mobileno;
                            }
                        }
                        if (watsapp_sms)
//                            GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg) + " " + txtWaybillNo.getText().toString() + "." + getString(R.string.watsappPredefinedMsg1) + txtWaybillNo.getText().toString()
//                                    , getActivity().getApplicationContext());
                            showPopup(mobileno);
                        else
                            GlobalVar.GV().sendSMS(mobileno, getString(R.string.watsappPredefinedMsg) + " " + txtWaybillNo.getText().toString() + "." + getString(R.string.watsappPredefinedMsg1) + txtWaybillNo.getText().toString()
                                    , getActivity().getApplicationContext());
                    } else
                        GlobalVar.GV().ShowSnackbar(rootView, "Is not valid mobile number.", GlobalVar.AlertType.Warning);
                }
            });


            Bundle bundle = getArguments();

            if (bundle != null) {
                txtWaybillNo.setText(bundle.getString("WaybillNo"));
                txtWaybillNo.setEnabled(false);
                btnOpenCamera.setVisibility(View.GONE);
            }

            Button btnGetWaybillDetails = (Button) rootView.findViewById(R.id.btnGetWaybillDetails);
            btnGetWaybillDetails.setVisibility(View.VISIBLE);

            btnGetWaybillDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!txtWaybillNo.getText().toString().equals("") &&
                            txtWaybillNo.getText().length() > 7) {
                        ShipmentBarCodeList.clear();
                        GetWayBillInfo();

                    } else
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.validwaybill), GlobalVar.AlertType.Error);
                }
            });


            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else {
                        Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                    }
                }
            });


            initViews();

            // GlobalVar.GV().activity.setRequestedOrientation(getResources().getConfiguration().orientation);
        }


        return rootView;
    }

    String mobilevalidate = "", phonenovalidate = "";
    boolean watsapp = false;

    private boolean hasWatsapp(String mobileno, String phoneno) {


        mobilevalidate = mobileno;
        phonenovalidate = phoneno;

        Thread t = new Thread() {
            public void run() {

                if (!mobilevalidate.equals("null") && mobilevalidate != null && mobilevalidate.length() > 0) {
                    if (mobilevalidate.length() == 10) {
                        String validate = mobilevalidate.substring(0, 1);
                        if (validate.equals("0"))
                            mobilevalidate = mobilevalidate.replaceFirst("0", "+966");
                    } else {
                        if (mobilevalidate.length() > 10) {
                            //String validate = mobileno.substring(0, 2);
                            if (mobilevalidate.contains("00966"))
                                mobilevalidate = mobilevalidate.replaceFirst("00966", "+966");
                        } else if (mobilevalidate.length() == 9) {
                            mobilevalidate = "+966" + mobilevalidate;
                        }
                    }

                }
                //GlobalVar.addMobileNumber(mobileno, getActivity().getApplicationContext());
                watsapp = GlobalVar.contactIdByPhoneNumber(getActivity().getApplicationContext(), mobilevalidate);
                //GlobalVar.deleteContact(getContext(), mobileno);
                if (!watsapp) {
                    if (!phonenovalidate.equals("null") && phonenovalidate != null && phonenovalidate.length() > 0) {
                        if (phonenovalidate.length() == 10) {
                            String validate = phonenovalidate.substring(0, 1);
                            if (validate.equals("0"))
                                phonenovalidate = phonenovalidate.replaceFirst("0", "+966");
                        } else {
                            if (phonenovalidate.length() > 10) {
                                //String validate = mobileno.substring(0, 2);
                                if (phonenovalidate.contains("00966"))
                                    phonenovalidate = phonenovalidate.replaceFirst("00966", "+966");
                            } else if (phonenovalidate.length() == 9) {
                                phonenovalidate = "+966" + phonenovalidate;
                            }
                        }

                    }
                    //GlobalVar.addMobileNumber(phoneno, getActivity().getApplicationContext());

                    watsapp = GlobalVar.contactIdByPhoneNumber(getActivity().getApplicationContext(), phonenovalidate);
                    GlobalVar.deleteContact(getContext(), phonenovalidate);
                }

            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return watsapp;
    }


    @Override
    public void onResume() {
        super.onResume();
        // GlobalVar.GV().activity.setRequestedOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    public void onPause() {
        super.onPause();
        //  GlobalVar.GV().activity.setRequestedOrientation(getResources().getConfiguration().orientation);
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.shipmentBarCodes);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        DataAdapter adapter = new DataAdapter(ShipmentBarCodeList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        if (barcode.length() > 8)
                            barcode = barcode.substring(0, 8);
                        txtWaybillNo.setText(barcode);
                        GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                    }
                }

            }
        }
    }

    private void GetWayBillInfo() {
        String waybillno = txtWaybillNo.getText().toString().substring(0, 8);
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" + waybillno + "'",
                getContext());

        if (result.getCount() > 0) {

            ReadFromLocal(result, dbConnections);
        } else {
            GlobalVar.hideKeyboardFrom(getContext(), rootView);
            GetWaybillDetailsRequest getWaybillDetailsRequest = new GetWaybillDetailsRequest();
            NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
            try {
                getWaybillDetailsRequest.WaybillNo = Integer.parseInt(String.valueOf(nf.parse(txtWaybillNo.getText().toString())));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("WaybillNo", getWaybillDetailsRequest.WaybillNo);
                jsonObject.put("AppTypeID", getWaybillDetailsRequest.AppTypeID);
                jsonObject.put("AppVersion", getWaybillDetailsRequest.AppVersion);
                jsonObject.put("LanguageID", getWaybillDetailsRequest.LanguageID);
                String jsonData = jsonObject.toString();

                new GetWaybillDetailsInfo().execute(jsonData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void ReadFromLocal(Cursor result, DBConnections dbConnections) {

        result.moveToFirst();
        boolean isdelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
        if (!isdelivered) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ID", result.getString(result.getColumnIndex("ID")));
                jsonObject.put("WaybillNo", result.getString(result.getColumnIndex("ItemNo")));
                jsonObject.put("PiecesCount", result.getString(result.getColumnIndex("PiecesCount")));
                jsonObject.put("Weight", result.getString(result.getColumnIndex("Weight")));
                jsonObject.put("BillingType", result.getString(result.getColumnIndex("BillingType")));
                jsonObject.put("CODAmount", result.getString(result.getColumnIndex("CODAmount")));
                jsonObject.put("ConsigneeName", result.getString(result.getColumnIndex("ConsigneeName")));
                jsonObject.put("ConsigneeFName", result.getString(result.getColumnIndex("ConsigneeFName")));
                jsonObject.put("PhoneNo", result.getString(result.getColumnIndex("ConsigneePhoneNumber")));
                jsonObject.put("MobileNo", result.getString(result.getColumnIndex("ConsigneeMobile")));
                jsonObject.put("Address", result.getString(result.getColumnIndex("ConsigneeFirstAddress")));
                jsonObject.put("SecondLine", result.getString(result.getColumnIndex("ConsigneeSecondAddress")));
                jsonObject.put("Near", result.getString(result.getColumnIndex("ConsigneeNear")));
                jsonObject.put("CityName", "");
                jsonObject.put("CityFName", "");
                jsonObject.put("ClientName", result.getString(result.getColumnIndex("ClientName")));
                shippername = result.getString(result.getColumnIndex("ClientName"));
                jsonObject.put("Sign", result.getInt(result.getColumnIndex("Sign")));
                jsonObject.put("ClientID", result.getInt(result.getColumnIndex("ClientID")));
                JSONObject coordinates = new JSONObject();
                coordinates.put("Latitude", result.getString(result.getColumnIndex("Latitude")));
                coordinates.put("Longitude", result.getString(result.getColumnIndex("Longitude")));
                jsonObject.put("locationCoordinate", coordinates);

                Lat = result.getString(result.getColumnIndex("Latitude"));
                Longi = result.getString(result.getColumnIndex("Longitude"));

                Cursor barcodecursor = dbConnections.Fill("select * from BarCode Where WayBillNo = '" + result.getString(result.getColumnIndex("ItemNo"))
                        + "'", getContext());
                JSONArray jsonArray = new JSONArray();
                if (barcodecursor.getCount() > 0) {
                    barcodecursor.moveToFirst();
                    do {

                        jsonArray.put(barcodecursor.getString(barcodecursor.getColumnIndex("BarCode")));
                    } while (barcodecursor.moveToNext());
                }

                jsonObject.put("BarCodeList", jsonArray);

                result.close();
                barcodecursor.close();
                dbConnections.close();

                WaybillDetailsResult waybillDetailsResult = new WaybillDetailsResult(jsonObject.toString());

                txtBillingType.setText(getResources().getString(R.string.txtBillingType) + waybillDetailsResult.BillingType);
                txtCODAmount.setText(getResources().getString(R.string.txtCODAmount) + waybillDetailsResult.CODAmount);
                txtWeight.setText(getResources().getString(R.string.txtWeight) + waybillDetailsResult.Weight);
                txtPiecesCount.setText(getResources().getString(R.string.txtPiecesCount) + waybillDetailsResult.PiecesCount);

                signrequired = waybillDetailsResult.signrequired;
                if (GlobalVar.GV().IsEnglish())
                    txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + waybillDetailsResult.ConsigneeName);
                else
                    txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + waybillDetailsResult.ConsigneeName);

                txtAddress.setText(getResources().getString(R.string.txtAddress) + waybillDetailsResult.Address);

                txtSecondAddress.setText(getResources().getString(R.string.txtSecondAddress) + waybillDetailsResult.SecondLine);
                txtNear.setText(getResources().getString(R.string.txtNear) + waybillDetailsResult.Near);
                txtMobileNo.setText(getResources().getString(R.string.txtMobileNo) + waybillDetailsResult.MobileNo);
                txtMobileNo.setTag(waybillDetailsResult.MobileNo);
                txtPhoneNo.setText(getResources().getString(R.string.txtPhoneNo) + waybillDetailsResult.PhoneNo);
                txtPhoneNo.setTag(waybillDetailsResult.PhoneNo);


                ShipmentBarCodeList = new ArrayList<>();
                for (int i = 0; i < waybillDetailsResult.BarCodeList.size(); i++) {
                    ShipmentBarCodeList.add(waybillDetailsResult.BarCodeList.get(i));
                }

                initViews();
                //boolean validate = hasWatsapp(txtMobileNo.getTag().toString(), txtPhoneNo.getTag().toString());
                boolean validate = true;

                if (!validate) {
                    watsapp_sms = false;
                    btnWhatsApp.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.sym_action_email));
                    btnWhatsApp1.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.sym_action_email));
                } else {
                    watsapp_sms = true;
                    btnWhatsApp.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.whatsapp));
                    btnWhatsApp1.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.whatsapp));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            GlobalVar.ShowDialog(getActivity(), "Info", "Already Delivered this Waybill", true);
        }

    }

    private class GetWaybillDetailsInfo extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        //  ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Please wait.", "Downloading Waybill Info."
                    , true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetWaybillDetails");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setReadTimeout(GlobalVar.GV().Connandtimeout30000);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().Connandtimeout30000);
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
                WaybillDetailsResult waybillDetailsResult = new WaybillDetailsResult(finalJson);

//                if(waybillDetailsResult.BlockClient ==1)
//                {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("Info")
//                            .setMessage("Mentioned Client is Block,kindly contact Operational team.")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int which) {
//                                    getActivity().finish();
//                                }
//                            }).setNegativeButton("Cancel", null).setCancelable(false);
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                    return;
//                }
                txtBillingType.setText(getResources().getString(R.string.txtBillingType) + waybillDetailsResult.BillingType);
                txtCODAmount.setText(getResources().getString(R.string.txtCODAmount) + waybillDetailsResult.CODAmount);
                txtWeight.setText(getResources().getString(R.string.txtWeight) + waybillDetailsResult.Weight);
                txtPiecesCount.setText(getResources().getString(R.string.txtPiecesCount) + waybillDetailsResult.PiecesCount);

                signrequired = waybillDetailsResult.signrequired;
                if (GlobalVar.GV().IsEnglish())
                    txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + waybillDetailsResult.ConsigneeName);
                else
                    txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + waybillDetailsResult.ConsigneeName);

                txtAddress.setText(getResources().getString(R.string.txtAddress) + waybillDetailsResult.Address);

                txtSecondAddress.setText(getResources().getString(R.string.txtSecondAddress) + waybillDetailsResult.SecondLine);
                txtNear.setText(getResources().getString(R.string.txtNear) + waybillDetailsResult.Near);
                txtMobileNo.setText(getResources().getString(R.string.txtMobileNo) + waybillDetailsResult.MobileNo);
                txtMobileNo.setTag(waybillDetailsResult.MobileNo);
                txtPhoneNo.setText(getResources().getString(R.string.txtPhoneNo) + waybillDetailsResult.PhoneNo);
                txtPhoneNo.setTag(waybillDetailsResult.PhoneNo);
                shippername = waybillDetailsResult.ClientName;


                ShipmentBarCodeList = new ArrayList<>();
                for (int i = 0; i < waybillDetailsResult.BarCodeList.size(); i++) {
                    ShipmentBarCodeList.add(waybillDetailsResult.BarCodeList.get(i));
                }

                initViews();
                //boolean validate = hasWatsapp(txtMobileNo.getTag().toString(), txtPhoneNo.getTag().toString());
                boolean validate = true;

                if (!validate) {
                    watsapp_sms = false;
                    btnWhatsApp.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.sym_action_email));
                    btnWhatsApp1.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.sym_action_email));
                } else {
                    watsapp_sms = true;
                    btnWhatsApp.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.whatsapp));
                    btnWhatsApp1.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.whatsapp));
                }
            } else
                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.wentwrong), GlobalVar.AlertType.Error);
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtWaybillNo", txtWaybillNo.getText().toString());
        outState.putString("txtBillingType", txtBillingType.getText().toString());
        outState.putString("txtCODAmount", txtCODAmount.getText().toString());
        outState.putString("txtWeight", txtWeight.getText().toString());
        outState.putString("txtPiecesCount", txtPiecesCount.getText().toString());
        outState.putString("txtConsigneeName", txtConsigneeName.getText().toString());
        outState.putString("txtAddress", txtAddress.getText().toString());
        outState.putString("txtSecondAddress", txtSecondAddress.getText().toString());
        outState.putString("txtNear", txtNear.getText().toString());
        outState.putString("txtMobileNo", txtMobileNo.getText().toString());
        outState.putString("txtPhoneNo", txtPhoneNo.getText().toString());
        outState.putStringArrayList("ShipmentBarCodeList", ShipmentBarCodeList);
        outState.putBoolean("signrequired", signrequired);
        outState.putString("shippername", shippername);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            txtWaybillNo.setText(savedInstanceState.getString("txtWaybillNo"));
            txtBillingType.setText(savedInstanceState.getString("txtBillingType"));
            txtCODAmount.setText(savedInstanceState.getString("txtCODAmount"));
            txtWeight.setText(savedInstanceState.getString("txtWeight"));
            txtPiecesCount.setText(savedInstanceState.getString("txtPiecesCount"));
            txtConsigneeName.setText(savedInstanceState.getString("txtConsigneeName"));
            txtAddress.setText(savedInstanceState.getString("txtAddress"));

            signrequired = savedInstanceState.getBoolean("signrequired");
            txtSecondAddress.setText(savedInstanceState.getString("txtSecondAddress"));
            txtNear.setText(savedInstanceState.getString("txtNear"));
            shippername = savedInstanceState.getString("shippername");
            String splitmno[] = savedInstanceState.getString("txtMobileNo").split(":");


            txtMobileNo.setText(savedInstanceState.getString("txtMobileNo"));
            if (splitmno.length > 1)
                txtMobileNo.setTag(splitmno[1]);
            String splitpno[] = savedInstanceState.getString("txtPhoneNo").split(":");

            txtPhoneNo.setText(savedInstanceState.getString("txtPhoneNo"));
            if (splitpno.length > 1)
                txtPhoneNo.setTag(splitpno[1]);


            ShipmentBarCodeList = savedInstanceState.getStringArrayList("ShipmentBarCodeList");

            initViews();
        }
    }


    PopupWindow popup;

    private void showPopup(final String mobileno) {


        LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.messagedrafts, viewGroup);

        final TextView customerlocation = (TextView) layout.findViewById(R.id.customerlocation);
        final TextView frontofthedoor = (TextView) layout.findViewById(R.id.frontofthedoor);
        final TextView cssupport = (TextView) layout.findViewById(R.id.cssupport);

        customerlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg)
                        + " " + txtWaybillNo.getText().toString() + getString(R.string.watsappPredefinedMsg1)
                        + shippername + getString(R.string.watsappPredefinedMsg2)
                        + txtWaybillNo.getText().toString(), getContext());
                popup.dismiss();
            }
        });
        frontofthedoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.doorPredefinedMsg)
                        + " " + txtWaybillNo.getText().toString() + getString(R.string.doorPredefinedMsg1)
                        + shippername + getString(R.string.doorPredefinedMsg2)
                        + txtWaybillNo.getText().toString(), getContext());
                popup.dismiss();
            }
        });
        cssupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.csPredefinedMsg), getContext());
                popup.dismiss();
            }
        });


        popup = new PopupWindow(getActivity());
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

}
