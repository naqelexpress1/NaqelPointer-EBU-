package com.naqelexpress.naqelpointer.DB.DBObjects;

/**
 * Created by sofan on 19/09/2017.
 */

public class OnCLoadingForDWaybill
{
    public int ID = 0;
    public String WaybillNo = "";
    public boolean IsSync = false;
    public int OnCLoadingID = 0;

    public OnCLoadingForDWaybill(String waybillNo, int onCLoadingID)
    {
        IsSync = false;
        WaybillNo = waybillNo;
        OnCLoadingID = onCLoadingID;
    }
}