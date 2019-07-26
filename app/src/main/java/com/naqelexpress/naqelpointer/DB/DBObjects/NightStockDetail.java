package com.naqelexpress.naqelpointer.DB.DBObjects;

public class NightStockDetail {
    public int ID = 0;
    public String BarCode = "";
    public boolean IsSync = false;
    public int NightStockId = 0;

    public NightStockDetail(String barCode, int nightstockdId)
    {
        IsSync = false;
        BarCode = barCode;
        NightStockId = nightstockdId;
    }
}
