package com.naqelexpress.naqelpointer.Retrofit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Retrofit.Interface.IAPICallListener;
import com.naqelexpress.naqelpointer.Retrofit.Interface.IPointerAPI;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidationGWT;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnlineValidationOffset;
import com.naqelexpress.naqelpointer.Retrofit.Request.OnlineValidationRequest;
import com.naqelexpress.naqelpointer.Retrofit.Response.RetrofitCallResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APICall {

    private IAPICallListener<String> callback;
    private Context context;
    private Activity activity;
    private ProgressDialog progressDialog = null;
    private String errorMessage;
    private boolean hasError;
    private int processType;
    private int count = 0;
    private List<OnlineValidationOffset> onLineValidationList = new ArrayList<>();
    private static final String TAG = "APICall";


    public APICall(Context context, Activity activity, IAPICallListener<String> callback) {
        this.context = context;
        this.activity = activity;
        this.callback = callback;
    }


    // Riyam - Used By : GWT
    public void getOnlineValidationDataGWT (final int processType) {
        try {
            progressDialog = ProgressDialog.show(activity, "Loading", "Upload online validation file , Please wait...", true);
            try {
                new CountDownTimer(300000, 30000) { // counter time is 5 min , update text every min

                    public void onTick(long millisUntilFinished) {
                        count++;
                        if (count == 1){
                            progressDialog.setMessage("Downloading validation file , Please wait...");
                        }else if (count == 2){
                            progressDialog.setMessage("Processing your request .. Kindly wait.");
                        } else if (count == 3) {
                            progressDialog.setMessage("This might take some time .. Kindly wait");
                        } else if (count == 4) {
                            progressDialog.setMessage("Downloading validation file , Please wait...");
                        }

                    }
                    public void onFinish() {
                    }
                }.start();
            } catch (Exception e) {
                Log.d("test" , e.toString());
            }

            //TODO Update to live link
            String url = GlobalVar.GV().NaqelPointerAPILink;

            IPointerAPI iPointerAPI = GlobalVar.getIPointerAPI(url,420,420); //420
            Call<RetrofitCallResponse.OnlineValidationGWTResponse> call = iPointerAPI.GetOnlineValidationDataGWT();

            call.enqueue(new Callback<RetrofitCallResponse.OnlineValidationGWTResponse>() {
                @Override
                public void onResponse(Call<RetrofitCallResponse.OnlineValidationGWTResponse> call, Response<RetrofitCallResponse.OnlineValidationGWTResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        if (!response.body().HasError) {

                            List<OnLineValidationGWT> onLineValidationList = response.body().getOnLineValidationData();
                            DBConnections dbConnections = new DBConnections(context, null);

                            boolean isInserted = false;
                            if (onLineValidationList.size() > 0) {
                                isInserted = dbConnections.insertOnLineValidationGWT(onLineValidationList,processType,context);
                            }

                            if (!isInserted) {
                                hasError = true;
                                errorMessage = "File couldn't be saved locally";
                            }

                            dbConnections.close();

                        } else {
                            progressDialog.dismiss();
                            hasError = true;
                            errorMessage =  response.body().ErrorMessage;                 }
                    } else {
                        Log.d("test" , "in api call dismiss");
                        progressDialog.dismiss();
                        hasError = true;
                        errorMessage =  "Response Code : " + response.code() + " " + response.toString();
                    }

                    progressDialog.dismiss();
                    callback.onCallComplete(hasError,errorMessage);

                }

                @Override
                public void onFailure(Call<RetrofitCallResponse.OnlineValidationGWTResponse> call, Throwable t) {
                    t.printStackTrace();
                    hasError = true;
                    errorMessage = "Kindly check your internet connection";
                    progressDialog.dismiss();
                    callback.onCallComplete(hasError,errorMessage);
                }
            });


        } catch (Exception e ) {
            hasError = true;
            errorMessage = e.toString();
            callback.onCallComplete(hasError,errorMessage);
        }
    }



    // Riyam - Used By : Courier - TH
    int mCounter = 0;
    public void getOnlineValidationDataOffset (final int processType, final int offset , final int counter) {
        try {

            mCounter = counter;
            Map<Integer, String> messages = new HashMap<>();
            messages.put(1 ,"Downloading validation file from server, Please wait. ");
            messages.put(2 ,"Processing your request .. Kindly wait.");
            messages.put(3 ,"This might take some time .. Kindly wait.");
            messages.put(4 ,"Downloading validation file from server, Please wait. ");


            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(activity, "Loading",messages.get(mCounter) , true);
            } else {
                if (mCounter <= 4)
                    progressDialog.setMessage(messages.get(mCounter));
                else
                    progressDialog.setMessage(messages.get(1));
            }

//                if (progressDialog == null) {
//                    Log.d("test" , "dialog is null");
//                }
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    Log.d("test" , "dialog is showing");
//                }
//               if(progressDialog == null || !progressDialog.isShowing()) {
//                   Log.d("test" , "else1");
//                   progressDialog = ProgressDialog.show(activity, "Loading",messages.get(mCounter) , true);
//               } else {
//                   Log.d("test" , "else");
//                   progressDialog = ProgressDialog.show(activity, "Loading",messages.get(mCounter) , true);
//               }

            //TODO : Update to live link
            String url = GlobalVar.GV().NaqelPointerAPILink ;
            OnlineValidationRequest request = new OnlineValidationRequest();
            request.setEmployeeID(GlobalVar.GV().EmployID);
            request.setOffset(offset);

            IPointerAPI iPointerAPI = GlobalVar.getIPointerAPI(url,420,420);
            Call<RetrofitCallResponse.OnlineValidationOffsetResponse> call = iPointerAPI.GetOnlineValidationDataOffset(request);

            call.enqueue(new Callback<RetrofitCallResponse.OnlineValidationOffsetResponse>() {
                @Override
                public void onResponse(Call<RetrofitCallResponse.OnlineValidationOffsetResponse> call, Response<RetrofitCallResponse.OnlineValidationOffsetResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        if (!response.body().HasError) {

                            onLineValidationList.addAll(response.body().getOnLineValidationData());

                            if (response.body().IsEndOfTable) {
                                DBConnections dbConnections = new DBConnections(context, null);
                                boolean isInserted;
                                isInserted = dbConnections.insertOnLineValidationOffset(onLineValidationList,processType,context);
                                dbConnections.close();

                                if (!isInserted) {
                                    setResponse(true , "File couldn't be saved locally");
                                } else {
                                    setResponse(false , "File uploaded successfully");
                                }
                                progressDialog.dismiss();
                                callback.onCallComplete(hasError,errorMessage);

                            } else {
                                getOnlineValidationDataOffset(processType , onLineValidationList.size()  , ++mCounter );
                            }
                        } else {
                            setResponse(true , response.body().ErrorMessage);
                            progressDialog.dismiss();
                            callback.onCallComplete(hasError,errorMessage);
                        }


                    } else {
                        progressDialog.dismiss();
                        setResponse(true ,  "Response Code : " + response.code() + " " + response.toString());
                    }

                }

                @Override
                public void onFailure(Call<RetrofitCallResponse.OnlineValidationOffsetResponse> call, Throwable t) {
                    t.printStackTrace();
                    setResponse(true, t.toString());
                    progressDialog.dismiss();
                    callback.onCallComplete(hasError,errorMessage);
                }
            });


        } catch (Exception e ) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            setResponse(true , e.toString());
            callback.onCallComplete(hasError,errorMessage);
        }
    }


    private void setResponse (boolean hasError , String errorMessage) {
        this.hasError = hasError;
        this.errorMessage = errorMessage;
    }




    // For each NCL get new data registered
    /*
    public void getUpdatedOnlineValidationData (final int processType) {
            try {
                progressDialog = ProgressDialog.show(activity, "Loading", "Upload online validation file , Please wait...", true);
                try {
                    new CountDownTimer(300000, 30000) { // counter time is 5 min , update text every min

                        public void onTick(long millisUntilFinished) {
                            count++;
                            if (count == 1){
                                progressDialog.setMessage("Updating validation file , Please wait...");
                            }else if (count == 2){
                                progressDialog.setMessage("Processing your request .. Kindly wait.");
                            } else if (count == 3) {
                                progressDialog.setMessage("This might take some time .. Kindly wait");
                            } else if (count == 4) {
                                progressDialog.setMessage("Updating validation file , Please wait...");
                            }

                        }
                        public void onFinish() {
                        }
                    }.start();
                } catch (Exception e) {
                    Log.d("test" , e.toString());
                }

                //TODO Update to live link
                String url = GlobalVar.NaqelAPIUAT ;

                IPointerAPI iPointerAPI = GlobalVar.getIPointerAPI(url,420,420);
                Call<RetrofitCallResponse.OnlineValidationResponse> call = iPointerAPI.GetUpdatedOnlineValidationData();

                call.enqueue(new Callback<RetrofitCallResponse.OnlineValidationResponse>() {
                    @Override
                    public void onResponse(Call<RetrofitCallResponse.OnlineValidationResponse> call, Response<RetrofitCallResponse.OnlineValidationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            if (!response.body().HasError) {

                                List<OnLineValidation> onLineValidationList = response.body().getOnLineValidationData();
                                DBConnections dbConnections = new DBConnections(context, null);

                                boolean isInserted = false;
                                if (onLineValidationList.size() > 0) {
                                    isInserted = dbConnections.insertUpdatedOnLineValidation(onLineValidationList,processType,context);
                                }

                                if (!isInserted) {
                                    hasError = true;
                                    errorMessage = "File couldn't be saved locally";
                                }

                                dbConnections.close();

                            } else {
                                hasError = true;
                                errorMessage =  response.body().ErrorMessage;                 }
                        } else {
                            hasError = true;
                            errorMessage =  "Response Code : " + response.code() + " " + response.toString();
                        }

                        progressDialog.dismiss();
                        callback.onCallComplete(hasError,errorMessage);

                    }

                    @Override
                    public void onFailure(Call<RetrofitCallResponse.OnlineValidationResponse> call, Throwable t) {
                        t.printStackTrace();
                        hasError = true;
                        errorMessage = t.toString();
                        progressDialog.dismiss();
                        callback.onCallComplete(hasError,errorMessage);
                    }
                });


            } catch (Exception e ) {
                hasError = true;
                errorMessage = e.toString();
                callback.onCallComplete(hasError,errorMessage);
            }
        }*/




}
