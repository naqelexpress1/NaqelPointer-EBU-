package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.naqelexpress.naqelpointer.Activity.Constants.Constant;
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

public class TripAndVehicleDetail  extends AppCompatActivity implements View.OnClickListener {

    TextView back, next;
    EditText tripId, vehicleTractorHead, driverEmpId, driverEmpName, driverContactNo;
    CheckBox damageOrPunctureCBY, damageOrPunctureCBN, greaseHubCupCBY, greaseHubCupCBN, spareTireCBY, spareTireCBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_and_vehicle_details);

        findViewById();
    }

    public void findViewById(){
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        tripId = findViewById(R.id.tripId);
        vehicleTractorHead = findViewById(R.id.vehicleTractorHead);
        driverEmpId = findViewById(R.id.driverEmpId);
        driverEmpName = findViewById(R.id.driverEmpName);
        driverContactNo = findViewById(R.id.driverContactNo);
        damageOrPunctureCBY = findViewById(R.id.damageOrPunctureCBY);
        damageOrPunctureCBN = findViewById(R.id.damageOrPunctureCBN);
        greaseHubCupCBY = findViewById(R.id.greaseHubCupCBY);
        greaseHubCupCBN = findViewById(R.id.greaseHubCupCBN);
        spareTireCBY = findViewById(R.id.spareTireCBY);
        spareTireCBN = findViewById(R.id.spareTireCBN);

        back.setOnClickListener(this);
        next.setOnClickListener(this);

        damageOrPunctureCBY.setOnClickListener(this);
        damageOrPunctureCBN.setOnClickListener(this);
        greaseHubCupCBY.setOnClickListener(this);
        greaseHubCupCBN.setOnClickListener(this);
        spareTireCBY.setOnClickListener(this);
        spareTireCBN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.damageOrPunctureCBY:
                Constant.interCityModel.setDamageOrPuncture(1);
                if(damageOrPunctureCBN.isChecked()){
                    damageOrPunctureCBN.setChecked(false);
                }
                break;

            case R.id.damageOrPunctureCBN:
                Constant.interCityModel.setDamageOrPuncture(0);
                if(damageOrPunctureCBY.isChecked()){
                    damageOrPunctureCBY.setChecked(false);
                }
                break;


            case R.id.greaseHubCupCBY:
                Constant.interCityModel.setGreaseHubCup(1);
                if(greaseHubCupCBN.isChecked()){
                    greaseHubCupCBN.setChecked(false);
                }
                break;

            case R.id.greaseHubCupCBN:
                Constant.interCityModel.setGreaseHubCup(0);
                if(greaseHubCupCBY.isChecked()){
                    greaseHubCupCBY.setChecked(false);
                }
                break;


            case R.id.spareTireCBY:
                Constant.interCityModel.setSpareTire(1);
                if(spareTireCBN.isChecked()){
                    spareTireCBN.setChecked(false);
                }
                break;

            case R.id.spareTireCBN:
                Constant.interCityModel.setSpareTire(0);
                if(spareTireCBY.isChecked()){
                    spareTireCBY.setChecked(false);
                }
                break;

            case R.id.back:
                onBackPressed();
//                Constant.exitConfirmation(TripAndVehicleDetail.this, "Exit Inter City Trip","Are you sure you want to exit without saving?");
                break;

            case R.id.next:
//                CommonApi.submitCBM2(new Callback<CommonResult>() {
//                    @Override
//                    public void returnResult(CommonResult result) {
//                        System.out.println();
//
//                        String msg = result.getErrorMessage();
//                        if (result.getHasError())
//                            GlobalVar.GV().alertMsgAll("Info", msg, TripAndVehicleDetail.this,
//                                    Enum.NORMAL_TYPE, "CBM");
//                        else
//                            GlobalVar.GV().alertMsgAll("Info", msg, TripAndVehicleDetail.this,
//                                    Enum.SUCCESS_TYPE, "CBM");
//
//                    }
//
//                    @Override
//                    public void returnError(String message) {
//                        //mView.showError(message);
//                        GlobalVar.GV().alertMsgAll("Info", "Something went wrong , please try again", TripAndVehicleDetail.this,
//                                Enum.NORMAL_TYPE, "CBM");
//                        System.out.println(message);
//
//                    }
//                }, Constant.interCityModel);

//                prepareToSubmitData();
//                if(tripId.getText().toString().equals(null) || vehicleTractorHead.getText().toString().equals(null) ||
//                        driverEmpId.getText().toString().equals(null) || driverEmpName.getText().toString().equals(null) ||
//                        driverContactNo.getText().toString().equals(null) || tripId.getText().toString().equals("") ||
//                        vehicleTractorHead.getText().toString().equals("") || driverEmpId.getText().toString().equals("") ||
//                        driverEmpName.getText().toString().equals("") ||
//                        driverContactNo.getText().toString().equals("")){
//                    Constant.alert("Alert", "All Fields Required", TripAndVehicleDetail.this);
//                }else{
//                    Constant.interCityModel.setTripId(Integer.parseInt(tripId.getText().toString()));
//                    Constant.interCityModel.setVehicleTractorHeadAndTrailer(vehicleTractorHead.getText().toString());
//                    Constant.interCityModel.setDriverEmpId(Integer.parseInt(driverEmpId.getText().toString()));
//                    Constant.interCityModel.setDriverEmpName(driverEmpName.getText().toString());
//                    Constant.interCityModel.setDriverContactNo(driverContactNo.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), TireConditionPicture.class);
                    startActivity(intent);
//                }
                break;

        }
    }


    private void prepareToSubmitData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tripId", "12345");
            jsonObject.put("vehicleTractorHeadAndTrailer", "Noshad");
            jsonObject.put("driverEmpId", "24316");
            jsonObject.put("driverEmpName", "Noshad");
            jsonObject.put("driverContactNo", "Noshad");
            jsonObject.put("damageOrPuncture", "1");
            jsonObject.put("greaseHubCup", "1");
            jsonObject.put("spareTire", "1");
            jsonObject.put("singleDeckerSideBarAvailable", "1");
            jsonObject.put("doubleDeckerSideBarAvailable", "1");
            jsonObject.put("checkCurtainLockingRatchet", "1");
            jsonObject.put("checkCurtainsBeltAndTears", "1");
            jsonObject.put("cargoBeltApplied", "1");
            jsonObject.put("checkCurtainsRollers", "1");
            jsonObject.put("checkCurtainsCleanliness", "1");
            jsonObject.put("curtainBeltLock", "1");
            jsonObject.put("checkRearDoorBolts", "1");
            jsonObject.put("checkRearDoorLocks", "1");
            jsonObject.put("checkSlidingSupportPostAndItsLocking", "1");
            jsonObject.put("checkNumberPlateAndHolder", "1");
            jsonObject.put("checkAirLeak","1");
            jsonObject.put("checkAirSuspensionCondition", "1");
            jsonObject.put("trailerBodyRemarks", "Noshad");
            jsonObject.put("landingLegFunctional", "1");
            jsonObject.put("landingLegShoes", "1");
            jsonObject.put("checkLightConditions", "1");
            jsonObject.put("fireExtinguisherAvailability", "1");
            jsonObject.put("fireExtinguisherValidity", "1");
            String jsonData = jsonObject.toString();
            Log.d("MY JSON DATA",jsonData);
            new insertTripAndVehicleDetail().execute(jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //    https://naqelpointersc.naqelksa.com/Api/Pointer/
    private class insertTripAndVehicleDetail extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog pd;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(TripAndVehicleDetail.this);
            pd.setTitle("Loading");
            pd.setMessage("Submitting Inter City Trip Details ");
            pd.show();
            super.onPreExecute();
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            Log.d("MY JSON DATA",jsonData);
            String ch = jsonData;
//            Toast.makeText(TripAndVehicleDetail.this, ch, Toast.LENGTH_SHORT).show();
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;
            try {
                DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
//                http://localhost:49982/Api/Pointer/InsertInterCityTripDetail
                URL url = new URL("http://localhost:49982/Api/Pointer/InsertInterCityTripDetail");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json");//; charset=UTF-8
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
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
                Constant.interCityModel = null;
                Constant.safetyCurtainsCargoPicture.clear();
                Constant.tireConditionPicture.clear();
                Constant.attachments.clear();
                pd.dismiss();
            } else {
                //
                // GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.servererror), GlobalVar.AlertType.Error);
                }
            }
            if (pd != null)
                pd.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }
}
