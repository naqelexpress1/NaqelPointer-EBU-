package com.naqelexpress.naqelpointer.DB.DBObjects;

public class UserME {
    public int ID = 0;
    public int EmployID = 0;
    public String Password = "";
    public int StationID = 0;
    public int RoleMEID = 0;
    //    public String MachineID="";
    public int StatusID;

    public String EmployName;
    public String EmployFName;
    public String MobileNo;
    public String StationCode;
    public String StationName;
    public String StationFName;
    public String Division;
    public int UsertypeID;
    public int Menu = 0;
    public int TruckID = 0;
    public int DisableEnabletxtBox = 1;
    public int CountryID = 0;
    public String CountryCode;
    public String TimeZone;

    public UserME() {
        StatusID = 1;
    }
}