package com.naqelexpress.naqelpointer.Models;

import org.joda.time.DateTime;

public class AddtoScopeModels {
    private String PIDNCL;
    private DateTime TimeIn;
    private int UserID;
    private String Latitude;
    private String Longitude;
    private int ID;

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
        return TimeIn;
    }

    public void setTimeIn(DateTime timeIn) {
        TimeIn = timeIn;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
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