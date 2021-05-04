package com.naqelexpress.naqelpointer.Models;


public class DistrictDataModel {
    private String Code;
    private String Name;
    private float StationID;
    private float ID;
    private String Zone;


    // Getter Methods

    public String getCode() {
        return Code;
    }

    public String getName() {
        return Name;
    }

    public float getStationID() {
        return StationID;
    }

    public float getID() {
        return ID;
    }

    public String getZone() {
        return Zone;
    }

    // Setter Methods

    public void setCode(String Code) {
        this.Code = Code;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setStationID(float StationID) {
        this.StationID = StationID;
    }

    public void setID(float ID) {
        this.ID = ID;
    }

    public void setZone(String Zone) {
        this.Zone = Zone;
    }
}