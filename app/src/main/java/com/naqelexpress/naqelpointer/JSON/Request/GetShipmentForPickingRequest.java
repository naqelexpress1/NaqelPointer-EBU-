package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

public class GetShipmentForPickingRequest
{
    public int EmployID = GlobalVar.GV().EmployID;
    public int AppTypeID = GlobalVar.GV().AppTypeID;
    public String AppVersion = GlobalVar.GV().AppVersion;
    public int LanguageID = GlobalVar.GV().GetLanguageID();
}