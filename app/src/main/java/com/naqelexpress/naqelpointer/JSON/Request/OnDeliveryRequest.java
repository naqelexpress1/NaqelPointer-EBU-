package com.naqelexpress.naqelpointer.JSON.Request;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class OnDeliveryRequest {
    public int ID;
    public String WaybillNo;
    public String ReceiverName;
    public int PiecesCount;
    public DateTime TimeIn;
    public DateTime TimeOut;
    public int EmployID;
    public int StationID;
    public boolean IsPartial;
    public String Latitude;
    public String Longitude;
    public double ReceivedAmt;
    public double POSAmount;
    public double CashAmount;
    public int al;
    public String DeviceToken = "";
    public String ST = "";
    public String Barcode = "";
    public String IqamaID = "";
    public String PhoneNo = "";
    public String IqamaName = "";
    public int DeliverySheetID = 0;
    public int OTPNo = 0;
    public String POSResult = "";
    //    public String ReceiptNo ;
//    public int StopPointsID ;
    public List<OnDeliveryDetailRequest> OnDeliveryDetailRequestList;

    public OnDeliveryRequest() {
        OnDeliveryDetailRequestList = new ArrayList<>();
    }
}