package com.naqelexpress.naqelpointer.Retrofit.Interface;

import com.naqelexpress.naqelpointer.Activity.InterCity.Model.InterCityModel;
import com.naqelexpress.naqelpointer.Models.CommonResult;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Interface {
    @Multipart
    @POST("uploadInterCityTripImages")
    Call<String> uploadImages(@Part MultipartBody.Part file);


    @POST("InsertInterCityTripDetail")
//    @FormUrlEncoded
    Call<CommonResult> submitInterCityDataToServer(@Body InterCityModel interCityModel);

}