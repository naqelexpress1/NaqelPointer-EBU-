package com.naqelexpress.naqelpointer.Retrofit;

public interface IAPICallListener <T> {

    public void onCallComplete(boolean hasError , String errorMessage);
}
