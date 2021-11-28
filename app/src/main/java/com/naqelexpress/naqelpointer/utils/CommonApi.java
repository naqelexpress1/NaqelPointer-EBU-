package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.Models.Request.CBMRequest;
import com.naqelexpress.naqelpointer.callback.Callback;

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
}
