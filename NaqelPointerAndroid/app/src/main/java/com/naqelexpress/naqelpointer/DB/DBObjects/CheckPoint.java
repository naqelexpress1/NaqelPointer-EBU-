package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;
import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.List;

//Created by sofan on 24/10/2017.

public class CheckPoint
{
    public int ID = 0 ;
    public int EmployID = GlobalVar.GV().EmployID;
    public DateTime Date = DateTime.now();
    public int CheckPointTypeID = 0;
    public String Latitude = "";
    public String Longitude = "";
    public boolean IsSync = false;
    public int CheckPointTypeDetailID = 0;
    public int CheckPointTypeDDetailID = 0;
    public String AppVersion = GlobalVar.GV().AppVersion;

    public List<CheckPointWaybillDetails> CheckPointWaybillDetails;
    public List<CheckPointBarCodeDetails> CheckPointBarCodeDetails;

    public CheckPoint()
    {
        CheckPointWaybillDetails = new ArrayList<>();
        CheckPointBarCodeDetails = new ArrayList<>();
    }

    public CheckPoint(int checkPointTypeID, String latitude, String longitude, int checkPointTypeDetailID, int checkPointTypeDDetailID)
    {
        CheckPointTypeID = checkPointTypeID;
        CheckPointTypeDetailID = checkPointTypeDetailID;
        CheckPointTypeDDetailID = checkPointTypeDDetailID;
        Latitude = latitude;
        Longitude = longitude;
    }
}