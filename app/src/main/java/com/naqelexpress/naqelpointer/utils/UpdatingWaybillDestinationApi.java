package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.JSON.Request.UpdateWaybillRequest;
import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.callback.Callback;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UpdatingWaybillDestinationApi {

    public static void UpdatingWaybillDestinationApi(final Callback<CommonResult> callback,
                                                     UpdateWaybillRequest updateWaybillRequest) {

        NetworkingUtils.getUserApiInstance()

                .UpdateNonManifestedWaybillDestination(updateWaybillRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<CommonResult>() {
                    @Override
                    public void onNext(CommonResult result) {

                        callback.returnResult(result);
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
