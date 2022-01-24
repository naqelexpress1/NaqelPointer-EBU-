package com.naqelexpress.naqelpointer.DB.DBObjects;

public class PieceDetail {

    public String Barcode;
    public String Waybill;
    public double Weight;

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getWaybill() {
        return Waybill;
    }

    public void setWaybill(String waybill) {
        Waybill = waybill;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }
}