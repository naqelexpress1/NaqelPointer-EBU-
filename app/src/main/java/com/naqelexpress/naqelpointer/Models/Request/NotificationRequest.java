package com.naqelexpress.naqelpointer.Models.Request;

public class NotificationRequest {

    String WayBillNo = "";
    String Lat = "";
    String Long = "";
    int DeliverySheetID = 0;
    int byEmployID = 0;
    int ReasonID = 0;
    int EmployID = 0;
    boolean isValid = false;

    public boolean getValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getWayBillNo() {
        return WayBillNo;
    }

    public void setWayBillNo(String wayBillNo) {
        WayBillNo = wayBillNo;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public int getDeliverySheetID() {
        return DeliverySheetID;
    }

    public void setDeliverySheetID(int deliverySheetID) {
        DeliverySheetID = deliverySheetID;
    }

    public int getByEmployID() {
        return byEmployID;
    }

    public void setByEmployID(int byEmployID) {
        this.byEmployID = byEmployID;
    }

    public int getReasonID() {
        return ReasonID;
    }

    public void setReasonID(int reasonID) {
        ReasonID = reasonID;
    }

    public int getEmployID() {
        return EmployID;
    }

    public void setEmployID(int employID) {
        EmployID = employID;
    }

//    public NotificationRequest(String wayBillNo, String lat, String aLong,
//                               int DeliverySheetID, int byEmployID, int ReasonID, int EmployID) {
//        WayBillNo = wayBillNo;
//        Lat = lat;
//        Long = aLong;
//        this.DeliverySheetID = DeliverySheetID;
//        this.byEmployID = byEmployID;
//        this.ReasonID = ReasonID;
//        this.EmployID = EmployID;
//    }
//
//    public NotificationRequest() {
//    }
}
