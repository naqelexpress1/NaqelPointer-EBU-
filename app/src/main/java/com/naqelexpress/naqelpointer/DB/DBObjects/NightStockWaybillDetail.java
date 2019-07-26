package com.naqelexpress.naqelpointer.DB.DBObjects;

public class NightStockWaybillDetail {
    public int ID = 0;
    public String WaybillNo = "";
    public boolean IsSync = false;
    public int NightStockId = 0;

    public NightStockWaybillDetail(String waybillNo, int nightstockdId)
    {
        WaybillNo = waybillNo;
        NightStockId = nightstockdId;
    }
}
