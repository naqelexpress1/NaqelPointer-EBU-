package com.naqelexpress.naqelpointer.Models;

import org.joda.time.DateTime;

public class AddtoScopeModels {
    private String PIDNCL;
    private DateTime ScanTime;
    private int EmployID;
    private String Latitude;
    private String Longitude;
    private int ID;
    private boolean IsSync;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPIDNCL() {
        return PIDNCL;
    }

    public void setPIDNCL(String PIDNCL) {
        this.PIDNCL = PIDNCL;
    }

    public DateTime getTimeIn() {
        return ScanTime;
    }

    public void setTimeIn(DateTime timeIn) {
        ScanTime = timeIn;
    }

    public int getUserID() {
        return EmployID;
    }

    public void setUserID(int userID) {
        EmployID = userID;
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