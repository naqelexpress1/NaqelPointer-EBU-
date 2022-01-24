package com.naqelexpress.naqelpointer.JSON.Request;

public class UpdateWaybillRequest {

    public int WaybillNo;
    public int EmployeeID;
    public String Barcode;
    public int NewWaybillDestID;
    public String AppVersion;
    public String Latitude;
    public String Longitude;
    public String StationName;
    public boolean isMultiPiecePopup;

    public boolean isMultiPiecePopup() {
        return isMultiPiecePopup;
    }

    public void setMultiPiecePopup(boolean multiPiecePopup) {
        isMultiPiecePopup = multiPiecePopup;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public int getWaybillNo() {
        return WaybillNo;
    }

    public void setWaybillNo(int waybillNo) {
        WaybillNo = waybillNo;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public int getNewWaybillDestID() {
        return NewWaybillDestID;
    }

    public void setNewWaybillDestID(int newWaybillDestID) {
        NewWaybillDestID = newWaybillDestID;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
