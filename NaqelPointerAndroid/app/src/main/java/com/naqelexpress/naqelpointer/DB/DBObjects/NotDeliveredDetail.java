package com.naqelexpress.naqelpointer.DB.DBObjects;

public class NotDeliveredDetail
{
    public int ID = 0;
    public String BarCode = "";
    public boolean IsSync = false;
    public int NotDeliveredID = 0;

    public NotDeliveredDetail(String barCode, int notDeliveredID)
    {
        IsSync = false;
        BarCode = barCode;
        NotDeliveredID = notDeliveredID;
    }
}
