package com.naqelexpress.naqelpointer.DB.DBObjects;

/**
 * Created by sofan on 05/03/2018.
 */

public class MultiDeliveryWaybillDetail
{
    public int ID = 0;
    public String WaybillNo = "";
    public boolean IsSync = false;
    public int MultiDeliveryID = 0;

    public MultiDeliveryWaybillDetail(String waybillNo, int multiDeliveryID)
    {
        WaybillNo = waybillNo;
        MultiDeliveryID = multiDeliveryID;
    }
}