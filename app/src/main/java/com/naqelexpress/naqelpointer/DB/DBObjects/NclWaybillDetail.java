package com.naqelexpress.naqelpointer.DB.DBObjects;

public class NclWaybillDetail {
    public int ID = 0;
    public String WaybillNo = "";
    public boolean IsSync = false;
    public int NclID = 0;

    public NclWaybillDetail(String waybillNo, int nclid)
    {
        WaybillNo = waybillNo;
        NclID = nclid;
    }
}
