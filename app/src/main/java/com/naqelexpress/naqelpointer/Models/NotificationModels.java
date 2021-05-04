package com.naqelexpress.naqelpointer.Models;

public class NotificationModels {
    private float ID;
    private boolean IsSync;
    private boolean HasError;
    private String ErrorMessage;


    // Getter Methods

    public float getID() {
        return ID;
    }

    public boolean getIsSync() {
        return IsSync;
    }

    public boolean getHasError() {
        return HasError;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    // Setter Methods

    public void setID(float ID) {
        this.ID = ID;
    }

    public void setIsSync(boolean IsSync) {
        this.IsSync = IsSync;
    }

    public void setHasError(boolean HasError) {
        this.HasError = HasError;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }
}