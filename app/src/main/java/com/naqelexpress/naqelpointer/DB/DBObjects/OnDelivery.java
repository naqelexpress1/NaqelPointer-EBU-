package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

public class OnDelivery {
    public int ID = 0;
    public int WaybillNo = 0;
    public String ReceiverName = "";
    public int PiecesCount = 0;
    public DateTime TimeIn = new DateTime();
    public DateTime TimeOut = new DateTime();
    public int EmployID = GlobalVar.GV().EmployID;
    public int StationID = GlobalVar.GV().StationID;
    public Boolean IsPartial = false;
    public String Latitude = "";
    public String Longitude = "";
    public double TotalReceivedAmount = 0;
    public double CashAmount = 0;
    public double POSAmount = 0;
    public boolean IsSync = false;
    public String Barcode = "";

    public OnDelivery(int ID) {
        this.ID = ID;
    }

    public OnDelivery(int waybillNo, String receiverName, int piecesCount, DateTime timeIn,
                      DateTime timeOut, String latitude, String longitude, double totalReceivedAmount,
                      double cashAmount, double posAmount, String barcode) {
        WaybillNo = waybillNo;
        ReceiverName = receiverName;
        PiecesCount = piecesCount;
        TimeIn = timeIn;
        TimeOut = timeOut;
        Latitude = latitude;
        Longitude = longitude;
        TotalReceivedAmount = totalReceivedAmount;
        CashAmount = cashAmount;
        POSAmount = posAmount;
        Barcode = barcode;
    }
}
