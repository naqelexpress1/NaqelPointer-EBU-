package com.naqelexpress.naqelpointer.Models.Request;


public class FuelRequest {

    private String Odometer;
    private String FuelPrice;
    private String Litres;
    private int FuelTypeID;
    private int TruckId;
    private int EmployID;


    public String getOdometer() {
        return Odometer;
    }

    public void setOdometer(String odometer) {
        Odometer = odometer;
    }

    public String getFuelPrice() {
        return FuelPrice;
    }

    public void setFuelPrice(String fuelPrice) {
        FuelPrice = fuelPrice;
    }

    public String getLitres() {
        return Litres;
    }

    public void setLitres(String litres) {
        Litres = litres;
    }

    public int getFuelTypeID() {
        return FuelTypeID;
    }

    public void setFuelTypeID(int fuelTypeID) {
        FuelTypeID = fuelTypeID;
    }

    public int getTruckId() {
        return TruckId;
    }

    public void setTruckId(int truckId) {
        TruckId = truckId;
    }

    public int getEmployID() {
        return EmployID;
    }

    public void setEmployID(int employID) {
        EmployID = employID;
    }
}