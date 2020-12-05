package com.naqelexpress.naqelpointer.Activity.Login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class VerifyMobileNo extends AppCompatActivity {

    EditText countrycode, mobileno, otpno;
    int validatefunction = 0;
    Button validatebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mobilenoverification);

        try {

//            Bundle bundle = getIntent().getExtras();
//            int usertype = bundle.getInt("usertype"); //getIntent().getIntExtra("usertype", 0);

            GlobalVar.GV().ResetTriedCount();

            countrycode = (EditText) findViewById(R.id.countrycode);
            mobileno = (EditText) findViewById(R.id.txtPhoneNo);
            otpno = (EditText) findViewById(R.id.otpno);
            validatebtn = (Button) findViewById(R.id.btnLogin);
            validatebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        validateMobileNo();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            if (savedInstanceState != null)
                setSavedInstance(savedInstanceState);

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            Cursor result = dbConnections.Fill("select * from UserME where EmployID = " + GlobalVar.GV().EmployID, getApplicationContext());
            if (result.getCount() > 0) {
                result.moveToFirst();

                HashMap<String, String> temp = new HashMap<>();
                int ismobilnoverified = result.getInt(result.getColumnIndex("IsMobileNoVerified"));
                if (ismobilnoverified == 1) {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }


            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    String MNo = "";

    public void validateMobileNo() throws JSONException {
        String phno = "";
        if (validatefunction == 0) {
            String ccode = countrycode.getText().toString().replace(" ", "");
            String mno = mobileno.getText().toString().replace(" ", "");

            if (ccode.length() == 0) {
                GlobalVar.ShowDialog(VerifyMobileNo.this, "Error", "Kindly enter Valid Country Code (966/971/973)", true);
                return;
            }

            if (mno.length() == 0) {
                GlobalVar.ShowDialog(VerifyMobileNo.this, "Error", "Kindly enter Valid Mobile No", true);
                return;
            }

            if (ccode.equals("966") || ccode.equals("971")) {
                if (mno.length() <= 8) {
                    GlobalVar.ShowDialog(VerifyMobileNo.this, "Error", "Kindly enter Valid Mobile No", true);
                    return;
                } else
                    phno = mno.substring(mno.length() - 9, mno.length());
            } else if (ccode.equals("973"))
                if (mno.length() <= 7) {
                    GlobalVar.ShowDialog(VerifyMobileNo.this, "Error", "Kindly enter Valid Mobile No", true);
                    return;
                } else
                    phno = mno.substring(mno.length() - 8, mno.length());


            JSONObject jsonObject = new JSONObject();
            phno = ccode + phno;
            jsonObject.put("EmployID", GlobalVar.GV().EmployID);
            jsonObject.put("MobileNo", phno);
            MNo = phno;
            new ValidateMobileNo().execute(jsonObject.toString());
        } else if (validatefunction == 1) {
            String otno = otpno.getText().toString().replace(" ", "");
            if (otno.length() == 0)
                GlobalVar.ShowDialog(VerifyMobileNo.this, "Error", "Kindly enter Valid OtpNo", true);


            JSONObject jsonObject = new JSONObject();

            jsonObject.put("EmployID", GlobalVar.GV().EmployID);
            jsonObject.put("MobileNo", MNo);
            jsonObject.put("OtpNo", Integer.parseInt(otpno.getText().toString()));


            new ValidateOtpNo().execute(jsonObject.toString());
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", MNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);


        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void setSavedInstance(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");


        }
    }

    ProgressDialog progressDialog;
    String devision = "";

    private class ValidateMobileNo extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(VerifyMobileNo.this,
                        "Please wait.", "Validate Mobile No.", true);

            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            //GetFacilityStatusRequest getDeliveryStatusRequest = new GetFacilityStatusRequest();
            String jsonData = params[0];

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(DomainURL + "GenerateOtpNoCBU");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
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
            } catch (Exception ignored) {
                isInternetAvailable = ignored.toString();
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
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {
                        otpno.setVisibility(View.VISIBLE);
                        validatefunction = 1;

//                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
//                        startActivity(intent);
//                        finish();
                    } else
                        GlobalVar.ShowDialog(VerifyMobileNo.this, "Error", jsonObject.getString("ErrorMessage"), true);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        //dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.servererror), GlobalVar.AlertType.Error);
                }
                LoadDivisionError();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private class ValidateOtpNo extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(VerifyMobileNo.this,
                        "Please wait.", "Validate Otp No.", true);

            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            //GetFacilityStatusRequest getDeliveryStatusRequest = new GetFacilityStatusRequest();
            String jsonData = params[0];

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(DomainURL + "ValidateOtpNo");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
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
            } catch (Exception ignored) {
                isInternetAvailable = ignored.toString();
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
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        if (dbConnections.IsMobileNoVerified(MNo)) {
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else
                        GlobalVar.ShowDialog(VerifyMobileNo.this, "Error", jsonObject.getString("ErrorMessage"), true);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        //dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.servererror), GlobalVar.AlertType.Error);
                }
                LoadDivisionError();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(VerifyMobileNo.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                });
        alertDialog.show();
    }


}