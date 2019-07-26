package com.naqelexpress.naqelpointer.DB.DBObjects;

/**
 * Created by sofan on 19/09/2017.
 */

public class OFDPieceLevel {
    public int ID = 0;
    public String BarCode = "";
    public boolean IsSync = false;
    public int OnCLoadingForDID = 0;
    public String WaybillNo = "0";

    public OFDPieceLevel(String barCode, int onCLoadingForDID, String waybillNo) {
        IsSync = false;
        BarCode = barCode;
        OnCLoadingForDID = onCLoadingForDID;
        WaybillNo = waybillNo;
    }
}
