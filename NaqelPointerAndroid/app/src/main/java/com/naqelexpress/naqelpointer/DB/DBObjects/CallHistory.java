package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

//Created by sofan on 24/10/2017.

public class CallHistory {
    public int ID = 0;
    public int EmployID = GlobalVar.GV().EmployID;
    public DateTime Date = DateTime.now();
    public String Type;
    public String MNO = "";
    public String Duration = "";
    public int WaybillNo;
    public String CNumber;
    public DateTime StartDate = DateTime.now();
    public DateTime EndDate = DateTime.now();


    public CallHistory() {

    }

    public CallHistory(String Type, String MNO, String Duration, int WaybillNo, DateTime Date) {
        this.Type = Type;
        this.MNO = MNO;
        this.Duration = Duration;
        this.WaybillNo = WaybillNo;
        this.Date = Date;

    }
}