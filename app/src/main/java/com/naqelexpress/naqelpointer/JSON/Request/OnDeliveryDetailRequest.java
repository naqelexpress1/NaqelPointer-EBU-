package com.naqelexpress.naqelpointer.JSON.Request;

public class OnDeliveryDetailRequest {
    public String BarCode;
    public int WayBillID;

    public OnDeliveryDetailRequest(String barcode, int WayBillID) {
        this.BarCode = barcode;
        this.WayBillID = WayBillID;
    }
}