package com.naqelexpress.naqelpointer.Models.Request;


public class CBMRequest {

    public int WaybillNo;
    public double Length;
    public double Width;
    public double Height;
    public int EmployID;
    public boolean isValid;

    public boolean getisValid() {
        return isValid;
    }

    public void setisValid(boolean valid) {
        isValid = valid;
    }

    public int getWaybillNo() {
        return WaybillNo;
    }

    public int getEmployID() {
        return EmployID;
    }

    public void setEmployID(int employID) {
        EmployID = employID;
    }

    public void setWaybillNo(int waybillNo) {
        WaybillNo = waybillNo;
    }

    public double getLength() {
        return Length;
    }

    public void setLength(double length) {
        Length = length;
    }

    public double getWidth() {
        return Width;
    }

    public void setWidth(double width) {
        Width = width;
    }

    public double getHeight() {
        return Height;
    }

    public void setHeight(double height) {
        Height = height;
    }
}