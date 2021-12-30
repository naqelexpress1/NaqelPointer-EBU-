package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class NotDelivered {
    public int ID = 0;
    public String WaybillNo = "0";
    public DateTime TimeIn = new DateTime();
    public DateTime TimeOut = new DateTime();
    public int UserID = GlobalVar.GV().UserID;
    public boolean IsSync = false;
    public int StationID = GlobalVar.GV().StationID;
    public int PiecesCount = 0;
    public int DeliveryStatusID = 0;
    public String Notes = "";
    public String Latitude = "";
    public String Longitude = "";
    public String DeviceToken = "";
    public int DeliveryStatusReasonID = 0;
    public String Barcode = "";
    public int DeliverySheetID = 0;

    public List<NotDeliveredDetail> NotDeliveredDetails;

    public NotDelivered() {
        NotDeliveredDetails = new ArrayList<>();
    }

    public NotDelivered(int ID) {
        this.ID = ID;
    }

    public NotDelivered(String waybillNo, int piecesCount, DateTime timeIn,
                        DateTime timeOut, String latitude, String longitude, int deliveryStatusID, String notes,
                        int DeliveryStatusReasonID, String barcode) {
        WaybillNo = waybillNo;
        PiecesCount = piecesCount;
        TimeIn = timeIn;
        TimeOut = timeOut;
        Latitude = latitude;
        Longitude = longitude;
        DeliveryStatusID = deliveryStatusID;
        Notes = notes;
        this.DeliveryStatusReasonID = DeliveryStatusReasonID;
        this.Barcode = barcode;
//        DeliverySheetID = dsid;
    }
}