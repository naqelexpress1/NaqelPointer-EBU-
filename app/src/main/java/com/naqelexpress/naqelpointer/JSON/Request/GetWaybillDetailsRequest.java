package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

public class GetWaybillDetailsRequest
{
    public int WaybillNo = 0;
    public int AppTypeID = GlobalVar.GV().AppTypeID;
    public String AppVersion = GlobalVar.GV().AppVersion;
    public int LanguageID = GlobalVar.GV().GetLanguageID();
}
