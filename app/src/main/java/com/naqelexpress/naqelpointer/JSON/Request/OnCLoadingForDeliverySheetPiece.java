package com.naqelexpress.naqelpointer.JSON.Request;

/**
 * Created by sofan on 19/09/2017.
 */

public class OnCLoadingForDeliverySheetPiece {
    public String BarCode;
    public String WaybillNo;

    public OnCLoadingForDeliverySheetPiece(String barcode, String WaybillNo) {
        this.BarCode = barcode;
        this.WaybillNo = WaybillNo;
    }
}