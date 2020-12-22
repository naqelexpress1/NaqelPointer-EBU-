package com.naqelexpress.naqelpointer.OnlineValidation;

public interface AsyncTaskCompleteListener <T> {
    public void onTaskComplete(boolean hasError , String errorMessage);
}

