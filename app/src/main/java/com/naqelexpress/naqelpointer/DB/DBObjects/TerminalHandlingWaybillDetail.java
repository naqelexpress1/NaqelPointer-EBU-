package com.naqelexpress.naqelpointer.DB.DBObjects;

//Created by sofan on 24/10/2017.

public class TerminalHandlingWaybillDetail
{
    public String WaybillNo = "";
    public int CheckPointID;
    public boolean IsSync = false;

    public TerminalHandlingWaybillDetail(String waybillNo, int checkPointID)
    {
        WaybillNo = waybillNo;
        CheckPointID = checkPointID;
    }
}