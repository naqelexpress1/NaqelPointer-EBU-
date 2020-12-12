package com.naqelexpress.naqelpointer.Retrofit;

import com.naqelexpress.naqelpointer.JSON.RetrofitCallResponse;
import com.naqelexpress.naqelpointer.OnlineValidation.OnLineValidation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IPointerAPI {

    @GET("GetOnlineValidationData_v2")
    Call<RetrofitCallResponse.OnlineValidationResponse> GetOnlineValidationData_v2();

}
