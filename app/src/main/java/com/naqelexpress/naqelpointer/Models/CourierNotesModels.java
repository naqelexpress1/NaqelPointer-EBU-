package com.naqelexpress.naqelpointer.Models;

public class CourierNotesModels {
    private int WaybillNo;
    private String TimeIn;
    private int UserID;
    private int DeliverySheetID;
    private  int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    private  String Notes;

    public int getWaybillNo() {
        return WaybillNo;
    }

    public void setWaybillNo(int waybillNo) {
        WaybillNo = waybillNo;
    }

    public String getTimeIn() {
        return TimeIn;
    }

    public void setTimeIn(String timeIn) {
        TimeIn = timeIn;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getDeliverySheetID() {
        return DeliverySheetID;
    }

    public void setDeliverySheetID(int deliverySheetID) {
        DeliverySheetID = deliverySheetID;
    }
}