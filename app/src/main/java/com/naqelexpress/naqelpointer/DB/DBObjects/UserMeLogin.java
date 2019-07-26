package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

public class UserMeLogin
{
    public int ID;
    public int EmployID;
    public int StateID;
    public DateTime Date;
    public String HHDName;
    public String Version;
    public int TruckID;
    public boolean IsSync;
    public DateTime LogoutDate;
    public boolean LogedOut = false;

    public UserMeLogin (int id)
    {
        ID = id;
    }

    public UserMeLogin(int employID, int stateID)
    {
        Date = DateTime.now();
        EmployID = employID;
        StateID = stateID;
        TruckID = 0;
        IsSync = false;
        HHDName = GlobalVar.GV().MachineID;
        Version = GlobalVar.GV().AppVersion;
    }
}
