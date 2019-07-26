package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;
//import com.naqelexpress.naqelpointer.JSON.Request.MultiDeliveryDetailRequest;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class MultiDelivery
{
    public int ID = 0 ;
    public String ReceiverName = "";
    public int PiecesCount = 0;
    public DateTime TimeIn = DateTime.now();
    public DateTime TimeOut = DateTime.now();
    public int UserID = GlobalVar.GV().EmployID;
    public boolean IsSync = false;
    public int StationID = GlobalVar.GV().StationID;
    public int WaybillsCount = 0;
    public String Latitude = "";
    public String Longitude = "";
    public double ReceivedAmt = 0;
    public String ReceiptNo = "";
    public int StopPointsID = 0;

    public List<MultiDeliveryDetail> multiDeliveryDetails;
    public List<MultiDeliveryWaybillDetail> multiDeliveryWaybillDetails;

    public MultiDelivery()
    {
        multiDeliveryDetails = new ArrayList<>();
        multiDeliveryWaybillDetails = new ArrayList<>();
    }

    public MultiDelivery(String receiverName, int piecesCount, DateTime timeIn, DateTime timeOut,
                         int waybillsCount, String latitude,String longitude, double receivedAmt,
                         String receiptNo, int stopPointsID)
    {
        ReceiverName = receiverName;
        PiecesCount = piecesCount;
        TimeIn = timeIn;
        TimeOut = timeOut;
        WaybillsCount = waybillsCount;
        Latitude = latitude;
        Longitude = longitude;
        ReceivedAmt = receivedAmt;
        ReceiptNo = receiptNo;
        StopPointsID = stopPointsID;
    }
}