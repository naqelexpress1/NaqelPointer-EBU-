package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.WaybillNoBarcodeModels;
import com.naqelexpress.naqelpointer.callback.Callback;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FetchHVAlarmApi {

    public static void FetchUAEHVShipments(final Callback<List<WaybillNoBarcodeModels>> callback) {
        NetworkingUtils.getUserApiInstance()

                .GetUAEHVAlarmWaybills()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<WaybillNoBarcodeModels>>() {
                    @Override
                    public void onNext(List<WaybillNoBarcodeModels> users) {

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

    public static void FetchUAEHVShipmentsStringArrayList(final Callback<List<WaybillNoBarcodeModels>> callback) {
        NetworkingUtils.getUserApiInstance()

                .GetUAEHVAlarmWaybills()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<WaybillNoBarcodeModels>>() {
                    @Override
                    public void onNext(List<WaybillNoBarcodeModels> users) {

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
