package com.naqelexpress.naqelpointer.DB.DBObjects;

public class Inventory {

    public String WaybillNo;
    public boolean IsPartialScan;
    public int PeiceCount;
    public int ScanCount;
    public String Dest;
    public String Reason;
    public int Since;

    public Inventory() {

    }

    public Inventory(String waybillNo, Boolean isPartialscan, int Peice, int Scan, String dest, String reason, int since) {
        WaybillNo = waybillNo;
        IsPartialScan = isPartialscan;
        PeiceCount = Peice;
        ScanCount = Scan;
        Dest = dest;
        Reason = reason;
        Since = since;

    }

}
