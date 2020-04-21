package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

/**
 * Created by sofan on 03/08/2017.
 */

public class UserMELoginRequest
{
    public int ID;
    public int EmployID;
    public int StateID;
    public DateTime Date = new DateTime();
    public String HHDName;
    public String Version = GlobalVar.GV().AppVersion;
    public Boolean IsSync;
    public int TruckID;
    public int AppTypeID = GlobalVar.GV().AppTypeID;
    public String AppVersion = GlobalVar.GV().AppVersion;
    public int LanguageID = GlobalVar.GV().GetLanguageID();

    public UserMELoginRequest(int ID, int employID, int stateID, int truckID, DateTime date)
    {
        this.ID = ID;
        EmployID = employID;
        StateID = stateID;
        Date = date;
        TruckID = truckID;
        Date = DateTime.now();
        HHDName = GlobalVar.GV().MachineID;
        IsSync = false;
    }
}