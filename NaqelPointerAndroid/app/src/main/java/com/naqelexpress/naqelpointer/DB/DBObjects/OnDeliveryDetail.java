package com.naqelexpress.naqelpointer.DB.DBObjects;

public class OnDeliveryDetail
{
    public int ID = 0;
    public String BarCode = "";
    public boolean IsSync = false;
    public int DeliveryID = 0;

    public OnDeliveryDetail(String barCode, int deliveryID)
    {
        IsSync = false;
        BarCode = barCode;
        DeliveryID = deliveryID;
    }
}