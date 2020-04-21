package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.DB.DBObjects.NotDeliveredDetail;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class NotDeliveredRequest
{
    public int ID;
    public String WaybillNo;
    public DateTime TimeIn;
    public DateTime TimeOut;
    public int UserID = GlobalVar.GV().UserID;
    public int StationID = 0;
    public int PiecesCount =0;
    public String Latitude="";
    public String Longitude="";
    public int DeliveryStatusID = 0;
    public String Notes= "";

    public List<NotDeliveredDetail> notDeliveredDetailRequestList;

    public NotDeliveredRequest()
    {
        notDeliveredDetailRequestList = new ArrayList<>();
    }
}

