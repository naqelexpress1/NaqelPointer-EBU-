package com.naqelexpress.naqelpointer.Retrofit.Interface;

public interface IAPICallListener <T> {

    void onCallComplete(boolean hasError , String errorMessage);
}
