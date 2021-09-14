package com.naqelexpress.naqelpointer.Models;


public class IsFollowSequncerModel {

    private int WaybillNo;
    private int IsFollow;
    private String ConsLatitude;
    private String ConsLongitude;
    private String CourierLatitude;
    private String CourierLongitude;
    private String FollowTime;
    private String DeliverysheetID;
    private int EmployeeID;
    private int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getWaybillNo() {
        return WaybillNo;
    }

    public void setWaybillNo(int waybillNo) {
        WaybillNo = waybillNo;
    }

    public int getIsFollow() {
        return IsFollow;
    }

    public void setIsFollow(int isFollow) {
        IsFollow = isFollow;
    }

    public String getConsLatitude() {
        return ConsLatitude;
    }

    public void setConsLatitude(String consLatitude) {
        ConsLatitude = consLatitude;
    }

    public String getConsLongitude() {
        return ConsLongitude;
    }

    public void setConsLongitude(String consLongitude) {
        ConsLongitude = consLongitude;
    }

    public String getCourierLatitude() {
        return CourierLatitude;
    }

    public void setCourierLatitude(String courierLatitude) {
        CourierLatitude = courierLatitude;
    }

    public String getCourierLongitude() {
        return CourierLongitude;
    }

    public void setCourierLongitude(String courierLongitude) {
        CourierLongitude = courierLongitude;
    }

    public String getFollowTime() {
        return FollowTime;
    }

    public void setFollowTime(String followTime) {
        FollowTime = followTime;
    }

    public String getDeliverysheetID() {
        return DeliverysheetID;
    }

    public void setDeliverysheetID(String deliverysheetID) {
        DeliverysheetID = deliverysheetID;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }
}