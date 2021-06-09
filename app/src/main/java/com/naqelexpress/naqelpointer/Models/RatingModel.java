package com.naqelexpress.naqelpointer.Models;

public class RatingModel {
    private float Rating;
    private float EmployID;
    private String Pwd = null;
    private boolean HasError;
    private String ErrorMessage;


    // Getter Methods

    public float getRating() {
        return Rating;
    }

    public float getEmployID() {
        return EmployID;
    }

    public String getPwd() {
        return Pwd;
    }

    public boolean getHasError() {
        return HasError;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    // Setter Methods

    public void setRating(float Rating) {
        this.Rating = Rating;
    }

    public void setEmployID(float EmployID) {
        this.EmployID = EmployID;
    }

    public void setPwd(String Pwd) {
        this.Pwd = Pwd;
    }

    public void setHasError(boolean HasError) {
        this.HasError = HasError;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }
}