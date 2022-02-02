package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.Models.Request.CBMRequest;
import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.Models.TLAllocationAreaModels;
import com.naqelexpress.naqelpointer.callback.Callback;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommonApi {

    public static void submitCBM(final Callback<CommonResult> callback, CBMRequest cbmRequest) {

        NetworkingUtils.getUserApiInstance()

                .SumbitCBM(cbmRequest)
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

                        callback.returnError(e.getMessage()
                        );
                    }


                });
    }

    public static void FetchSortingTlAllocation
            (final Callback<List<TLAllocationAreaModels>> callback, CommonRequest commonRequest) {

        NetworkingUtils.getUserApiInstance()

                .GetTLAllocationArea(commonRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<TLAllocationAreaModels>>() {
                    @Override
                    public void onNext(List<TLAllocationAreaModels> users) {

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
