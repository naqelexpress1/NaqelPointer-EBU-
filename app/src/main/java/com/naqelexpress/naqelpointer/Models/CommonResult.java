package com.naqelexpress.naqelpointer.Models;


public class CommonResult {

    private boolean HasError;

    private String ErrorMessage;


    public boolean getHasError() {
        return HasError;
    }

    public void setHasError(boolean hasError) {
        HasError = hasError;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}