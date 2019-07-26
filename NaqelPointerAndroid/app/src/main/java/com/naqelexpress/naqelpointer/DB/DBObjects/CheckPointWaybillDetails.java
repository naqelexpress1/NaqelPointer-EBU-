package com.naqelexpress.naqelpointer.DB.DBObjects;

//Created by sofan on 24/10/2017.

public class CheckPointWaybillDetails
{
    public String WaybillNo = "";
    public int CheckPointID;
    public boolean IsSync = false;

    public CheckPointWaybillDetails(String waybillNo,int checkPointID)
    {
        WaybillNo = waybillNo;
        CheckPointID = checkPointID;
    }
}