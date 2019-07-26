package com.naqelexpress.naqelpointer.DB.DBObjects;

/**
 * Created by sofan on 19/09/2017.
 */

public class OnCLoadingForDDetail
{
    public int ID = 0;
    public String BarCode = "";
    public boolean IsSync = false;
    public int OnCLoadingForDID = 0;
    public int WaybillNo = 0;

    public OnCLoadingForDDetail(String barCode, int onCLoadingForDID)
    {
        IsSync = false;
        BarCode = barCode;
        OnCLoadingForDID = onCLoadingForDID;
    }
}
