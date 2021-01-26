package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

//Created by Ismail on 24/10/2017.

public class TerminalHandling {
    public int ID = 0;
    public int EmployID = GlobalVar.GV().EmployID;
    public DateTime Date = DateTime.now();
    public int CheckPointTypeID = 0;
    public int TerminalHandlingScanStatusID = 0;
    public int TerminalHandlingScanStatusReasonID = 0;
    public String Latitude = "";
    public String Longitude = "";
    public boolean IsSync = false;
    public int CheckPointTypeDetailID = 0;
    public int CheckPointTypeDDetailID = 0;
    public String AppVersion = GlobalVar.GV().AppVersion;
    public String Reference = "";
    public String Comments = "";
    public int Count = 0;

    //mohammed
    public int TripID = 0;

    public List<CheckPointWaybillDetails> TerminalHandlingWaybillDetails;
    public List<CheckPointBarCodeDetails> TerminalHandlingBarCodeDetails;

    public TerminalHandling() {
        TerminalHandlingWaybillDetails = new ArrayList<>();
        TerminalHandlingBarCodeDetails = new ArrayList<>();
    }

    public TerminalHandling(int checkPointTypeID, String latitude, String longitude, int checkPointTypeDetailID, String reference,
                            String Comments, int Count) {
        CheckPointTypeID = checkPointTypeID;
        CheckPointTypeDetailID = checkPointTypeDetailID;
        Reference = reference;
        Latitude = latitude;
        Longitude = longitude;
        this.Comments = Comments;
        this.Count = Count;
    }

    //mohammed
    public TerminalHandling(int checkPointTypeID, String latitude, String longitude, int checkPointTypeDetailID, String reference,
                            String Comments, int Count ,int tripID) {
        CheckPointTypeID = checkPointTypeID;
        CheckPointTypeDetailID = checkPointTypeDetailID;
        Reference = reference;
        Latitude = latitude;
        Longitude = longitude;
        this.Comments = Comments;
        this.Count = Count;
        TripID = tripID;
    }

}