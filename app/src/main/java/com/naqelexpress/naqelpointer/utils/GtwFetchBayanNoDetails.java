package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.RatingModel;
import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.callback.Callback;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GtwFetchBayanNoDetails {

    public static void fetchRating(final Callback<RatingModel> callback,
                                   CommonRequest commonRequest) {

        NetworkingUtils.getUserApiInstance()

                .CourierRating(commonRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RatingModel>() {
                    @Override
                    public void onNext(RatingModel users) {

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
