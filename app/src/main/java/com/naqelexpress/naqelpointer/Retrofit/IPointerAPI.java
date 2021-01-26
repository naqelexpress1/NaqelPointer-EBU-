package com.naqelexpress.naqelpointer.Retrofit;

import com.naqelexpress.naqelpointer.Retrofit.Request.OnlineValidationRequest;
import com.naqelexpress.naqelpointer.Retrofit.Response.RetrofitCallResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IPointerAPI {

    @GET("GetOnlineValidationData")
    Call<RetrofitCallResponse.OnlineValidationResponse> GetOnlineValidationData();

    @POST("GetOnlineValidationData_v2")
    Call<RetrofitCallResponse.OnlineValidationOffsetResponse> GetOnlineValidationDataOffset(@Body OnlineValidationRequest request);

    @GET("GetOnlineValidationDataGWT")
    Call<RetrofitCallResponse.OnlineValidationGWTResponse> GetOnlineValidationDataGWT();

    /*@GET("GetUpdatedOnlineValidationData")
    Call<RetrofitCallResponse.OnlineValidationResponse> GetUpdatedOnlineValidationData();*/
}
