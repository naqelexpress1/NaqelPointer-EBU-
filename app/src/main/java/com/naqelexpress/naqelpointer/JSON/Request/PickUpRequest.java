package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class PickUpRequest
        extends DefaultRequest {
    public int ID;
    public String WaybillNo;
    public int ClientID;
    public int FromStationID;
    public int ToStationID;
    public int PiecesCount;
    public double Weight;
    public DateTime TimeIn;
    public DateTime TimeOut;
    public int UserMEID;
    public int StationID;
    public String RefNo;
    public String Latitude;
    public String Longitude;
    public int LoadTypeID;
    public int al;
    public int TruckID = 0;
    public String DeviceToken = "";
    public int DistrictID;
    //    public double ReceivedAmount;
    public String CurrentVersion = GlobalVar.GV().AppVersion;
    public List<PickUpDetailRequest> PickUpDetailRequestList;

    public PickUpRequest() {
        PickUpDetailRequestList = new ArrayList<>();
    }
}