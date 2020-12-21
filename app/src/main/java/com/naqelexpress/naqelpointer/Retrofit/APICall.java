package com.naqelexpress.naqelpointer.Retrofit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.RetrofitCallResponse;
import com.naqelexpress.naqelpointer.OnlineValidation.AsyncTaskCompleteListener;
import com.naqelexpress.naqelpointer.OnlineValidation.OnLineValidation;

import org.json.JSONArray;

import java.net.URL;
import java.util.List;

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
    private String url;


    public APICall(Context context, Activity activity, IAPICallListener<String> callback) {
        this.context = context;
        this.activity = activity;
        this.callback = callback;
    }



    public void getOnlineValidationData (final int processType) {
         try {

             progressDialog = ProgressDialog.show(activity, "Loading", "Upload online validation file , Please wait...", true);
             new CountDownTimer(300000, 30000) { // counter time is 5 min , update text every min

                 public void onTick(long millisUntilFinished) {
                     count++;
                     if (count == 1){
                         progressDialog.setMessage("Uploading online validation file , Please wait...");
                     }else if (count == 2){
                         progressDialog.setMessage("Processing your request .. Kindly wait.");
                     } else if (count == 3) {
                         progressDialog.setMessage("This might take some time .. Kindly wait");
                     } else if (count == 4) {
                         progressDialog.setMessage("Uploading online validation file , Please wait...");
                     }

                 }
                 public void onFinish() {
                 }
             }.start();

             //TODO Riyam
             if (processType == GlobalVar.NclAndArrival)
                 url = GlobalVar.NaqelLocalAPI + "GetOnlineValidationData";

             if (processType == GlobalVar.DsAndInventory)
                 url = GlobalVar.NaqelLocalAPI + "GetOnlineValidationData_v2";

             if (processType == GlobalVar.DsValidation)
                 url = GlobalVar.NaqelLocalAPI + "GetOnlineValidationData";

             IPointerAPI iPointerAPI = GlobalVar.getIPointerAPI(GlobalVar.NaqelLocalAPI,300,300);
             Call<RetrofitCallResponse.OnlineValidationResponse> call = iPointerAPI.GetOnlineValidationData_v2();

             call.enqueue(new Callback<RetrofitCallResponse.OnlineValidationResponse>() {
                 @Override
                 public void onResponse(Call<RetrofitCallResponse.OnlineValidationResponse> call, Response<RetrofitCallResponse.OnlineValidationResponse> response) {
                     Log.d("test" , "On Response");
                     if (response.isSuccessful() && response.body() != null) {

                         if (!response.body().HasError) {

                             List<OnLineValidation> onLineValidationList = response.body().getOnLineValidationData();
                             DBConnections dbConnections = new DBConnections(context, null);

                             boolean isInserted = false;
                             if (onLineValidationList.size() > 0) {
                                 isInserted = dbConnections.insertOnLineValidation(onLineValidationList,processType,context);
                             }

                             if (!isInserted) {
                                 hasError = true;
                                 errorMessage = "File couldn't be saved locally";
                             }

                             dbConnections.close();
                             Log.d("test" , "No error");

                         } else {
                             Log.d("test" , "has error");

                             hasError = true;
                             errorMessage =  response.body().ErrorMessage;                 }
                     } else {
                         Log.d("test" , "no response");
                         hasError = true;
                         errorMessage =  "Response Code : " + response.code() + " " + response.toString();
                     }

                     progressDialog.dismiss();
                     callback.onCallComplete(hasError,errorMessage);

                 }

                 @Override
                 public void onFailure(Call<RetrofitCallResponse.OnlineValidationResponse> call, Throwable t) {
                     Log.d("test" , "failure");
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
    }
}
