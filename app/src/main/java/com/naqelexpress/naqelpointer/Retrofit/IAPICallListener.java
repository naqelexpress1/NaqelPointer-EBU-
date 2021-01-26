package com.naqelexpress.naqelpointer.Retrofit;

public interface IAPICallListener <T> {

    void onCallComplete(boolean hasError , String errorMessage);
}
