package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

public class GetUserMEDataRequest
{
    public int EmployID;
    public String Passowrd;
    public int AppTypeID = GlobalVar.GV().AppTypeID;
    public String AppVersion = GlobalVar.GV().AppVersion;
    public int LanguageID = GlobalVar.GV().GetLanguageID();
}
