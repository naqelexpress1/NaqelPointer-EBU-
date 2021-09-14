package com.naqelexpress.naqelpointer.Retrofit;

import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.Models.DistrictDataModel;
import com.naqelexpress.naqelpointer.Models.FuelTypeModels;
import com.naqelexpress.naqelpointer.Models.NotificationModels;
import com.naqelexpress.naqelpointer.Models.RatingModel;
import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.Models.Request.FuelRequest;
import com.naqelexpress.naqelpointer.Models.Request.NotificationRequest;
import com.naqelexpress.naqelpointer.Models.Request.PickupSheetSPASRRegResult;
import com.naqelexpress.naqelpointer.Models.SkipRouteLineSeqWaybillnoReasonModels;
import com.naqelexpress.naqelpointer.Retrofit.Request.OnlineValidationRequest;
import com.naqelexpress.naqelpointer.Retrofit.Response.RetrofitCallResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface IPointerAPI {

    @GET("GetOnlineValidationData")
    Call<RetrofitCallResponse.OnlineValidationResponse> GetOnlineValidationData();

    @POST("GetOnlineValidationData_v2")
    Call<RetrofitCallResponse.OnlineValidationOffsetResponse> GetOnlineValidationDataOffset(@Body OnlineValidationRequest request);

    @GET("GetOnlineValidationDataGWT")
    Call<RetrofitCallResponse.OnlineValidationGWTResponse> GetOnlineValidationDataGWT();

    /*@GET("GetUpdatedOnlineValidationData")
    Call<RetrofitCallResponse.OnlineValidationResponse> GetUpdatedOnlineValidationData();*/
    //for CBU
    @POST("SkipRouteLineSeqWaybillno")
    Observable<NotificationModels> SkipRouteLineSeqWaybillno(@Body NotificationRequest notificationRequest);

    @GET("FetchSkipRouteLineSeqWaybillnoReason")
    Observable<List<SkipRouteLineSeqWaybillnoReasonModels>> SkipRouteLineSeqWaybillnoReason();

    //for fetch district
    @POST("GetDistrictDatas")
    Observable<List<DistrictDataModel>> GetDistrictDatas(@Body CommonRequest commonRequest);

    // Fetch Courier Rating
    @POST("CourierRating")
    Observable<RatingModel> CourierRating(@Body CommonRequest commonRequest);

    //BayanNo Details
    @POST("CourierRating")
    Observable<RatingModel> FetchBayanNoDetails(@Body CommonRequest commonRequest);

    //Fuel
    @POST("InsertExpFuel")
    Observable<CommonResult> InsertExpFuel(@Body FuelRequest fuelRequest);

    //Fetch Fuel Type
    @GET("FetchFuelType")
    Observable<List<FuelTypeModels>> FetchFuelType();

    //Pickupsheet Deatails SPASRReg
    @POST("PickupsheetDetailsSpAsrReg")
    Observable<PickupSheetSPASRRegResult> PickupsheetDetailsSpAsrReg(@Body CommonRequest commonRequest);
}
