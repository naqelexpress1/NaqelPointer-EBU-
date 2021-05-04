package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.NotificationModels;
import com.naqelexpress.naqelpointer.Models.Request.NotificationRequest;
import com.naqelexpress.naqelpointer.Models.SkipRouteLineSeqWaybillnoReasonModels;
import com.naqelexpress.naqelpointer.callback.Callback;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationApi {

    public static void skipRouteLineSeq(final Callback<NotificationModels> callback, NotificationRequest notificationRequest) {
        NetworkingUtils.getUserApiInstance()

                .SkipRouteLineSeqWaybillno(notificationRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NotificationModels>() {
                    @Override
                    public void onNext(NotificationModels users) {

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

    public static void skipRouteLineReason(final Callback<List<SkipRouteLineSeqWaybillnoReasonModels>> callback) {
        NetworkingUtils.getUserApiInstance()

                .SkipRouteLineSeqWaybillnoReason()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<SkipRouteLineSeqWaybillnoReasonModels>>() {
                    @Override
                    public void onNext(List<SkipRouteLineSeqWaybillnoReasonModels> users) {

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
