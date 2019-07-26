package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

public class PickUp {
    public int ID = 0;
    public int WaybillNo = 0;
    public int ClientID = 0;
    public int FromStationID = 0;
    public int ToStationID = 0;
    public int PieceCount = 0;
    public double Weight = 0;
    public DateTime TimeIn = DateTime.now();
    public DateTime TimeOut = DateTime.now();
    public boolean IsSync = false;
    public int UserID = GlobalVar.GV().UserID;
    public int StationID = GlobalVar.GV().StationID;
    public String RefNo = "";
    public String Latitude = "";
    public String Longitude = "";
    public String CurrentVersion = GlobalVar.GV().AppVersion;

    public PickUp(int ID) {
        this.ID = ID;
    }

    public PickUp(int waybillNo, int clientID, int fromStationID, int toStationID, int pieceCount,
                  double weight, DateTime timeIn, DateTime timeOut, String refNo,
                  String latitude, String longitude) {
        WaybillNo = waybillNo;
        ClientID = clientID;
        FromStationID = fromStationID;
        ToStationID = toStationID;
        PieceCount = pieceCount;
        Weight = weight;
        TimeIn = timeIn;
        TimeOut = timeOut;
        RefNo = refNo;
        Latitude = latitude;
        Longitude = longitude;
    }
}