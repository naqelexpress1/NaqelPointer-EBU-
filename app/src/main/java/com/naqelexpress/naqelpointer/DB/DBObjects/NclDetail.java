package com.naqelexpress.naqelpointer.DB.DBObjects;

public class NclDetail {
    public int ID = 0;
    public String BarCode = "";
    public boolean IsSync = false;
    public int NclID = 0;
    public boolean IsDestinationChanged;
    public int UpdatedDestStationID;

    public NclDetail(String barCode, int nclid)
    {
        IsSync = false;
        BarCode = barCode;
        NclID = nclid;
    }

    public NclDetail(String barCode, int nclid , boolean isDestStationChanged, int updatedDestStationID)
    {
        BarCode = barCode;
        NclID = nclid;
        IsSync = false;
        this.IsDestinationChanged = isDestStationChanged;
        this.UpdatedDestStationID = updatedDestStationID;
    }

}
