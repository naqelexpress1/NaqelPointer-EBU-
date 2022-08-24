package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.naqelexpress.naqelpointer.Activity.Constants.ConnectionHelper;
import com.naqelexpress.naqelpointer.Activity.Constants.Constant;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.Interface.Interface;
import com.naqelexpress.naqelpointer.Retrofit.Interface.OkRetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FleetSafety extends AppCompatActivity implements View.OnClickListener {

    TextView back;
    CheckBox fireExtinguisherAvailabilityCBY, fireExtinguisherAvailabilityCBN,
            fireExtinguisherValidityCBY, fireExtinguisherValidityCBN;
    Button submit;
    static ProgressDialog progressDialog = null;
    ArrayList<String> fileNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet_safety);

        findViewById();


    }

    public void findViewById() {
        back = findViewById(R.id.back);

        fireExtinguisherAvailabilityCBY = findViewById(R.id.fireExtinguisherAvailabilityCBY);
        fireExtinguisherAvailabilityCBN = findViewById(R.id.fireExtinguisherAvailabilityCBN);
        fireExtinguisherValidityCBY = findViewById(R.id.fireExtinguisherValidityCBY);
        fireExtinguisherValidityCBN = findViewById(R.id.fireExtinguisherValidityCBN);
        submit = findViewById(R.id.submit);

        back.setOnClickListener(this);
        fireExtinguisherAvailabilityCBY.setOnClickListener(this);
        fireExtinguisherAvailabilityCBN.setOnClickListener(this);
        fireExtinguisherValidityCBY.setOnClickListener(this);
        fireExtinguisherValidityCBN.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fireExtinguisherAvailabilityCBY:
                Constant.interCityModel.setFireExtinguisherAvailability(1);
                if (fireExtinguisherAvailabilityCBN.isChecked()) {
                    fireExtinguisherAvailabilityCBN.setChecked(false);
                }
                break;

            case R.id.fireExtinguisherAvailabilityCBN:
                Constant.interCityModel.setFireExtinguisherAvailability(0);
                if (fireExtinguisherAvailabilityCBY.isChecked()) {
                    fireExtinguisherAvailabilityCBY.setChecked(false);
                }
                break;

            case R.id.fireExtinguisherValidityCBY:
                Constant.interCityModel.setFireExtinguisherValidity(1);
                if (fireExtinguisherValidityCBN.isChecked()) {
                    fireExtinguisherValidityCBN.setChecked(false);
                }
                break;

            case R.id.fireExtinguisherValidityCBN:
                Constant.interCityModel.setFireExtinguisherAvailability(0);
                if (fireExtinguisherValidityCBY.isChecked()) {
                    fireExtinguisherValidityCBY.setChecked(false);
                }
                break;

            case R.id.submit:
                if (ConnectionHelper.isConnectingToInternet(FleetSafety.this)) {
                    fileNames.clear();
                    for (int i = 0; i < Constant.tireConditionPicture.size(); i++) {
                        String[] checkFileName = Constant.tireConditionPicture.get(i).getAbsolutePath().split("/");
                        fileNames.add(checkFileName[checkFileName.length - 1]);
                    }
                    for (int i = 1; i < 5; i++) {
                        if (fileNames.size() < i) {
                            fileNames.add("");
                        }
                    }

                    for (int i = 0; i < Constant.safetyCurtainsCargoPicture.size(); i++) {
                        String[] checkFileName = Constant.safetyCurtainsCargoPicture.get(i).getAbsolutePath().split("/");
                        fileNames.add(checkFileName[checkFileName.length - 1]);
                    }
                    for (int i = 5; i < 9; i++) {
                        if (fileNames.size() < i) {
                            fileNames.add("");
                        }
                    }

                    for (int i = 0; i < Constant.attachments.size(); i++) {
                        String[] checkFileName = Constant.attachments.get(i).getAbsolutePath().split("/");
                        fileNames.add(checkFileName[checkFileName.length - 1]);
                    }
                    for (int i = 8; i < 17; i++) {
                        if (fileNames.size() < i) {
                            fileNames.add("");
                        }
                    }
                    Constant.AllImages.clear();
                    Constant.AllImages.addAll(Constant.tireConditionPicture);
                    Constant.AllImages.addAll(Constant.safetyCurtainsCargoPicture);
                    Constant.AllImages.addAll(Constant.attachments);

                    if (Constant.AllImages.size() > 0) {
                        prepareToUploadImages(Constant.AllImages.get(0).getPath());
                    } else {
                        prepareToSubmitData();
                    }


//                    new SubmitDetails(FleetSafety.this).verifyNumber(Constant.interCityModel);

//                    prepareToSubmitData();

//                    CommonApi.submitCBM2(new com.naqelexpress.naqelpointer.callback.Callback<CommonResult>() {
//                        @Override
//                        public void returnResult(CommonResult result) {
//                            System.out.println();
//
//                            String msg = result.getErrorMessage();
//                            if (result.getHasError())
//                                GlobalVar.GV().alertMsgAll("Info", msg, FleetSafety.this,
//                                        Enum.NORMAL_TYPE, "CBM");
//                            else
//                                GlobalVar.GV().alertMsgAll("Info", msg, FleetSafety.this,
//                                        Enum.SUCCESS_TYPE, "CBM");
//
//                        }
//
//                        @Override
//                        public void returnError(String message) {
//                            //mView.showError(message);
//                            GlobalVar.GV().alertMsgAll("Info", "Something went wrong , please try again", FleetSafety.this,
//                                    Enum.NORMAL_TYPE, "CBM");
//                            System.out.println(message);
//
//                        }
//                    }, Constant.interCityModel);

//                    progressDialog = ProgressDialog.show(FleetSafety.this,
//                            "Please wait.", "Update Facility Login.", true);


//                    CommonApi.submitInterCityData(new Callback<CommonResult>() {
//                        @Override
//                        public void onResponse(Call<CommonResult> call, Response<CommonResult> response) {
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<CommonResult> call, Throwable t) {
//                            if (progressDialog != null && progressDialog.isShowing()) {
//                                progressDialog.dismiss();
//                                progressDialog = null;
//                            }
//                            System.out.println(message);
//                        }
//
//                        @Override
//                        public void returnResult(CommonResult result) {
//                            if (progressDialog != null && progressDialog.isShowing()) {
//                                progressDialog.dismiss();
//                                progressDialog = null;
//                            }
//
//                            String msg = result.getErrorMessage();
//                            if (result.getHasError())
//                                Constant.alert("Info", msg, FleetSafety.this);
////                            GlobalVar.GV().alertMsgAll("Info", msg, FleetSafety.this,
////                                    Enum.NORMAL_TYPE, "CBM");
//                            else
//                                Constant.alert("Info", msg, FleetSafety.this);
////                            GlobalVar.GV().alertMsgAll("Info", msg, FleetSafety.this,
////                                    Enum.SUCCESS_TYPE, "CBM");
//
//
//                        }
//
//                    }, Constant.interCityModel);
                } else {
                    Constant.alert("Internet Connection", "Make sure you are connected with internet", FleetSafety.this);
                }
                break;


            case R.id.back:
                onBackPressed();
                break;


        }
    }

//    public void prepareToUploadImages(){
//        final ProgressDialog progresRing = ProgressDialog.show(FleetSafety.this,
//                "Uploading Images", "please Wait uploading images...", true);
//        progresRing.setCancelable(false);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    for (int i = 0; i < imagepositions.size(); i++) {
//                        try {
//                            boolean upload = false;
//                            while (!upload) {
//                                String result = new uploadImages().execute(String.valueOf(imagepositions.get(i))).get();
//                                if (result != null && result.contains("Created Successfully")) {
//                                    deleteimages(imagepositions.get(i));
//                                    upload = true;
//                                }
//                            }
//                        } catch (Exception e) {
//                            System.out.println(e.toString());
//                        }
//
//                        if (imagepositions.size() - 1 == i) {
//                            String result = alertMail();
//
//                            try {
//                                JSONObject jsonObject = new JSONObject(result);
//                                if (!jsonObject.getBoolean("HasError")) {
//
//                                    GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
//                                    Constant.alert("Info", "Successfully Inserted ", FleetSafety.this);
//                                } else {
//
//                                    Constant.alert("Info", "Something Error , kindly please update again ", FleetSafety.this);
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            // finish();
//                        }
//                    }
//
//                } catch (Exception e) {
//
//                }
//                progresRing.dismiss();
//
//            }
//        }).start();
//    }
//    private class uploadImages extends AsyncTask<String, Integer, String> {
//        StringBuffer buffer;
//        int position = 0;
//
//        @Override
//        protected void onPreExecute() {
//
//            // if (progressDialog == null)
//            //   progressDialog = ProgressDialog.show(ArrivedatDestination.this,
//            //         "Please wait.", "Uploading Images to Server.", true);
//            super.onPreExecute();
//
//        }
//
//        protected String doInBackground(String... params) {
//            position = Integer.parseInt(params[0]);
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject();
//                jsonObject.put("ImageName", takePicture.sendimages.get(position));
//                String image = convertimage(position);
//                if (!image.equals("error"))
//                    jsonObject.put("FileName", image);
//                else
//                    return "201 - Created Successfully";
//            } catch (JSONException e) {
//                e.printStackTrace();
//
//            }
//
//            String jsonData = jsonObject.toString();
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "upload");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception ignored) {
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//            }
//            return null;
////
//
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute("");
//            if (result != null) {
//
//
//                System.out.println(result);
//            }
//
//
//        }
//
//    }

    // Uploading Image/Video
    private void prepareToUploadImages(final String filePath) {

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(filePath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());


        // Creating the OkRetrofitClient object that will generate an implementation of the APIService interface.
        OkRetrofitClient<Interface> retrofit = null;
        try {
            retrofit = new OkRetrofitClient<Interface>(FleetSafety.this, Interface.class, 2, TimeUnit.MINUTES, 30, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, GlobalVar.GV().NaqelPointerAPILink);
            // Generate the implementation of the APIService interface
            Interface client = retrofit.getClient();
            // Create the ASYNCHRONOUS call
            Call<CommonResult> call = client.uploadImages(fileToUpload);//(ApplicantCode, mobileNumber);

            call.enqueue(new Callback<CommonResult>() {
                @Override
                public void onResponse(Call<CommonResult> call, Response<CommonResult> response) {

                    if (response.code() == 1) {
                        Constant.AllImages.remove(0);
                        if (Constant.AllImages.size() > 0) {
                            prepareToUploadImages(Constant.AllImages.get(0).getPath());
                        } else {
                            prepareToSubmitData();
                        }
                    } else {
                        Constant.alert("Error", "Error Occurred Please Try Again", FleetSafety.this);
                    }
                }

                @Override
                public void onFailure(Call<CommonResult> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void prepareToSubmitData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tripId", String.valueOf(Constant.interCityModel.getTripId()));
            jsonObject.put("vehicleTractorHeadAndTrailer", Constant.interCityModel.getVehicleTractorHeadAndTrailer());
            jsonObject.put("driverEmpId", String.valueOf(Constant.interCityModel.getDriverEmpId()));
            jsonObject.put("driverEmpName", Constant.interCityModel.getDriverEmpName());
            jsonObject.put("driverContactNo", Constant.interCityModel.getDriverContactNo());
            jsonObject.put("damageOrPuncture", String.valueOf(Constant.interCityModel.getDamageOrPuncture()));
            jsonObject.put("greaseHubCup", String.valueOf(Constant.interCityModel.getGreaseHubCup()));
            jsonObject.put("spareTire", String.valueOf(Constant.interCityModel.getSpareTire()));
            jsonObject.put("singleDeckerSideBarAvailable", String.valueOf(Constant.interCityModel.getSingleDeckerSideBarAvailable()));
            jsonObject.put("doubleDeckerSideBarAvailable", String.valueOf(Constant.interCityModel.getDoubleDeckerSideBarAvailable()));
            jsonObject.put("checkCurtainLockingRatchet", String.valueOf(Constant.interCityModel.getCheckCurtainLockingRatchet()));
            jsonObject.put("checkCurtainsBeltAndTears", String.valueOf(Constant.interCityModel.getCheckCurtainsBeltAndTears()));
            jsonObject.put("cargoBeltApplied", String.valueOf(Constant.interCityModel.getCargoBeltApplied()));
            jsonObject.put("checkCurtainsRollers", String.valueOf(Constant.interCityModel.getCheckCurtainsRollers()));
            jsonObject.put("checkCurtainsCleanliness", String.valueOf(Constant.interCityModel.getCheckCurtainsCleanliness()));
            jsonObject.put("curtainBeltLock", String.valueOf(Constant.interCityModel.getCurtainBeltLock()));
            jsonObject.put("checkRearDoorBolts", String.valueOf(Constant.interCityModel.getCheckRearDoorBolts()));
            jsonObject.put("checkRearDoorLocks", String.valueOf(Constant.interCityModel.getCheckRearDoorLocks()));
            jsonObject.put("checkSlidingSupportPostAndItsLocking", String.valueOf(Constant.interCityModel.getCheckSlidingSupportPostAndItsLocking()));
            jsonObject.put("checkNumberPlateAndHolder", String.valueOf(Constant.interCityModel.getCheckNumberPlateAndHolder()));
            jsonObject.put("checkAirLeak", String.valueOf(Constant.interCityModel.getCheckAirLeak()));
            jsonObject.put("checkAirSuspensionCondition", String.valueOf(Constant.interCityModel.getCheckAirSuspensionCondition()));
            jsonObject.put("trailerBodyRemarks", Constant.interCityModel.getTrailerBodyRemarks());
            jsonObject.put("landingLegFunctional", String.valueOf(Constant.interCityModel.getLandingLegFunctional()));
            jsonObject.put("landingLegShoes", String.valueOf(Constant.interCityModel.getLandingLegShoes()));
            jsonObject.put("checkLightConditions", String.valueOf(Constant.interCityModel.getCheckLightConditions()));
            jsonObject.put("fireExtinguisherAvailability", String.valueOf(Constant.interCityModel.getFireExtinguisherAvailability()));
            jsonObject.put("fireExtinguisherValidity", String.valueOf(Constant.interCityModel.getFireExtinguisherValidity()));

            jsonObject.put("TireConditionOne",fileNames.get(0));
            jsonObject.put("TireConditionTwo",fileNames.get(1));
            jsonObject.put("TireConditionThree",fileNames.get(2));
            jsonObject.put("TireConditionFour",fileNames.get(3));

            jsonObject.put("SafetyCurtainsAndCargoOne",fileNames.get(4));
            jsonObject.put("SafetyCurtainsAndCargoTwo",fileNames.get(5));
            jsonObject.put("SafetyCurtainsAndCargoThree",fileNames.get(6));
            jsonObject.put("SafetyCurtainsAndCargoFour",fileNames.get(7));

            jsonObject.put("OtherAttachmentOne",fileNames.get(8));
            jsonObject.put("OtherAttachmentTwo",fileNames.get(9));
            jsonObject.put("OtherAttachmentThree",fileNames.get(10));
            jsonObject.put("OtherAttachmentFour",fileNames.get(11));
            jsonObject.put("OtherAttachmentFive",fileNames.get(12));
            jsonObject.put("OtherAttachmentSix",fileNames.get(13));
            jsonObject.put("OtherAttachmentSeven",fileNames.get(14));
            jsonObject.put("OtherAttachmentEight",fileNames.get(15));


            String jsonData = jsonObject.toString();

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
            pd = new ProgressDialog(FleetSafety.this);
            pd.setTitle("Loading");
            pd.setMessage("Submitting Inter City Trip Details ");
            pd.show();
            super.onPreExecute();
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;
            try {
                DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
//                http://localhost:49982/Api/Pointer/InsertInterCityTripDetail
                URL url = new URL("http://localhost:49982/Api/Pointer/" + "InsertInterCityTripDetail");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
