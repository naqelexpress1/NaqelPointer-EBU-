package com.naqelexpress.naqelpointer.DB.DBObjects;

public class NclDetail {
    public int ID = 0;
    public String BarCode = "";
    public boolean IsSync = false;
    public int NclID = 0;

    public NclDetail(String barCode, int nclid)
    {
        IsSync = false;
        BarCode = barCode;
        NclID = nclid;
    }
}
