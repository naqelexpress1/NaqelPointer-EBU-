package com.naqelexpress.naqelpointer.Models.Request;


import com.naqelexpress.naqelpointer.Activity.BookingCBU.PickupSheetReasonModel;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingModel;

import java.util.ArrayList;

public class PickupSheetSPASRRegResult {
    public ArrayList<BookingModel> PickupSheet = new ArrayList<BookingModel>();
    public ArrayList<PickupSheetReasonModel> MissingReason = new ArrayList<>();
    private boolean HasError;
    private String ErrorMessage;


    // Getter Methods

    public boolean getHasError() {
        return HasError;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    // Setter Methods

    public void setHasError(boolean HasError) {
        this.HasError = HasError;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

}

