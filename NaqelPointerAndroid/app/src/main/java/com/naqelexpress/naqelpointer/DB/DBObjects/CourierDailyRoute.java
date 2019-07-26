package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

public class CourierDailyRoute
{
    public int ID = 0 ;
    public int EmployID = 0 ;
    public DateTime StartingTime;
    public String StartLatitude = "";
    public String StartLongitude = "";
//    public DateTime EndTime;
//    public String EndLatitude = "";
//    public String EndLongitude = "";
//    public int DeliverySheetID = 0;

    public CourierDailyRoute()
    {
        StartingTime = DateTime.now();
        EmployID = GlobalVar.GV().EmployID;
    }

}
