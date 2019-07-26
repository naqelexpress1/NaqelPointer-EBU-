

package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class NightStock {
    public int ID = 0;
    public int PieceCount = 0;
    public DateTime CTime = DateTime.now();
    public int UserID = GlobalVar.GV().UserID;
    public boolean IsSync = false;
    public int StationID = GlobalVar.GV().StationID;
    public int WaybillsCount = 0;
    public int IDs = 0;
    public String BIN = "";

    public List<NightStockDetail> nightstockdetails;
    public List<NightStockWaybillDetail> nightstockwaybilldetails;

    public NightStock() {
        nightstockdetails = new ArrayList<NightStockDetail>();
        nightstockwaybilldetails = new ArrayList<NightStockWaybillDetail>();
    }

    public NightStock(int waybillsCount, int piecesCount, DateTime timeIn , String  bin) {

        PieceCount = piecesCount;
        CTime = timeIn;
        WaybillsCount = waybillsCount;
        BIN = bin;

    }
}
