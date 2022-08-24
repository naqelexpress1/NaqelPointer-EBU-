package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.widget.Toast;

import com.naqelexpress.naqelpointer.Activity.InterCity.Model.InterCityModel;
import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.Retrofit.Interface.Interface;
import com.naqelexpress.naqelpointer.Retrofit.Interface.OkRetrofitClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

public class SubmitDetails {

    FleetSafety activity;
    public SubmitDetails(FleetSafety activity){
        this.activity = activity;
    }

    public void verifyNumber(final InterCityModel interCityModel) {

        // Creating the OkRetrofitClient object that will generate an implementation of the APIService interface.
        OkRetrofitClient<Interface> retrofit = new OkRetrofitClient<Interface>(activity, Interface.class, 2, TimeUnit.MINUTES, 60, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, "http://localhost:49982/Api/Pointer/");

        // Generate the implementation of the APIService interface
        Interface client = retrofit.getClient();
        // Create the ASYNCHRONOUS call
        Call<CommonResult> call = client.submitInterCityDataToServer(interCityModel);
        call.enqueue(new Callback<CommonResult>() {
            @Override
            public void onResponse(Call<CommonResult> call, retrofit2.Response<CommonResult> response) { //retrofit2.Response<WebResponse>
                // TODO: use the repository list and display it
                if (response.isSuccessful()) {

                    // todo display the data instead of just a toast
                    // todo display the data instead of just a toast

                    if (response == null) System.out.println("=== RESPONCE IS NULL =");
                    else {
                        //String body = response.body().;
//                        String responseMessage = response.body().getResponseMessage();
//                        int responseCode = response.body().getResponseCode();
//
//                        if (responseCode == 1) {
//                            if (responseMessage.equals("new")) {
//                                Intent mainIntent = new Intent(activity, ActivityEnterOTP.class); //ActivityEmail
//                                activity.startActivity(mainIntent);
//                                activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//                            } else if (responseMessage.equals("exist")) {
//                                Intent mainIntent = new Intent(activity, LoginActivity.class); //ActivityEmail
//                                activity.startActivity(mainIntent);
//                                activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//                            }
//                        }

                    }
                } else {
                    Toast.makeText(activity, "Server returned an error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommonResult> call, Throwable t) {
//                activity.waitingDialog.dismiss();
                if (t instanceof IOException) {
                    Toast.makeText(activity, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                } else {
                    Toast.makeText(activity, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                }
            }
        });
    }

}
