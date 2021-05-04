package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.DistrictDataModel;
import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.callback.Callback;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PickupApi {

    public static void fetchdistrictdata(final Callback<List<DistrictDataModel>> callback,
                                         CommonRequest commonRequest) {
        NetworkingUtils.getUserApiInstance()
                .GetDistrictDatas(commonRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<DistrictDataModel>>() {
                    @Override
                    public void onNext(List<DistrictDataModel> districtDataModel) {
                        callback.returnResult(districtDataModel);
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
}
