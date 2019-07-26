package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;


public class UserLogs
{
    public Integer ID = 0 ;
    public int UserID = 0;
    public int LogTypeID = 0;
    public DateTime CTime = new DateTime();
    public String MachineID = "";
//    public int SuperVisorID = 0;

    public UserLogs(int logTypeID, int userID)
    {
        UserID = userID;
        LogTypeID = logTypeID;
        CTime = DateTime.now();
        MachineID = GlobalVar.GV().MachineID;
    }
}