package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.Models.Request.PickupSheetSPASRRegResult;
import com.naqelexpress.naqelpointer.callback.Callback;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PickupSheetDetailsApi {

    public static void fetchPickupsheetdetails(final Callback<PickupSheetSPASRRegResult> callback,
                                               CommonRequest commonRequest) {

        NetworkingUtils.getUserApiInstance()

                .PickupsheetDetailsSpAsrReg(commonRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PickupSheetSPASRRegResult>() {
                    @Override
                    public void onNext(PickupSheetSPASRRegResult bookingModelList) {

                        callback.returnResult(bookingModelList);
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
