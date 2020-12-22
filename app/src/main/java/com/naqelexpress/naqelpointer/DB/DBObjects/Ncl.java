package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Ncl {

    public int ID = 0;
    public String NclNo = "";
    public int UserID = GlobalVar.GV().UserID;
    public DateTime Date = DateTime.now();
    public int PieceCount = 0;
    public int WaybillCount = 0;
    public boolean IsSync = false;
    public int EmployID = GlobalVar.GV().EmployID;
    public String OrgDest = "";
    public int StationID = 0;
    public String AppVersion;
    public String Latitude;
    public String Longitude;

    public List<NclDetail> ncldetails;
    public List<NclWaybillDetail> nclwaybilldetails;

    public Ncl() {
        ncldetails = new ArrayList<NclDetail>();
        nclwaybilldetails = new ArrayList<NclWaybillDetail>();
    }

    public Ncl(int waybillsCount, int piecesCount, DateTime date, String nclNo) {
        PieceCount = piecesCount;
        Date = date;
        WaybillCount = waybillsCount;
        NclNo = nclNo;

    }
}
