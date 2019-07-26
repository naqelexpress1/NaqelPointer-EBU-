package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

public class OnCloadingForD
{
    public int ID;
    public int CourierID;
    public int UserID = GlobalVar.GV().UserID;
    public boolean IsSync = false;
    public DateTime CTime = DateTime.now();
    public int PieceCount;
    public String TruckID;
    public int WaybillCount;
    public int StationID = GlobalVar.GV().StationID;

    public OnCloadingForD(int ID)
    {
        this.ID = ID;
    }

    public OnCloadingForD(int courierID, int pieceCount, int waybillCount, String truckID)
    {
        CourierID = courierID;
        PieceCount = pieceCount;
        WaybillCount = waybillCount;
        TruckID = truckID;
    }
}