package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.Models.FuelTypeModels;
import com.naqelexpress.naqelpointer.Models.Request.FuelRequest;
import com.naqelexpress.naqelpointer.callback.Callback;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ExpFuelApi {

    public static void FuelApi(final Callback<CommonResult> callback,
                               FuelRequest fuelRequest) {
        NetworkingUtils.getUserApiInstance()

                .InsertExpFuel(fuelRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<CommonResult>() {
                    @Override
                    public void onNext(CommonResult users) {

                        callback.returnResult(users);
                    }

                    @Override
                    public void onCompleted() {

                        System.out.println("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.returnError(e.getMessage());
                    }


                });
    }


    public static void FetchFuelType(final Callback<List<FuelTypeModels>> callback) {

        NetworkingUtils.getUserApiInstance()

                .FetchFuelType()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<FuelTypeModels>>() {
                    @Override
                    public void onNext(List<FuelTypeModels> users) {

                        callback.returnResult(users);
                    }

                    @Override
                    public void onCompleted() {

                        System.out.println("");
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.returnError(e.getMessage()
                        );
                    }


                });
    }

}
