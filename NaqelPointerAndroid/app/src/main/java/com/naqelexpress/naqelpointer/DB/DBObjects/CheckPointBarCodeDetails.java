package com.naqelexpress.naqelpointer.DB.DBObjects;

/**
 * Created by sofan on 21/03/2018.
 */

public class CheckPointBarCodeDetails
{
    public String BarCode = "";
    public int CheckPointID;
    public boolean IsSync = false;

    public CheckPointBarCodeDetails(String barCode,int checkPointID)
    {
        BarCode = barCode;
        CheckPointID = checkPointID;
    }
}