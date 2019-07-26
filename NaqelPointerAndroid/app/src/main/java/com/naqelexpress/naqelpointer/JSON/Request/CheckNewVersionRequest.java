package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

public class CheckNewVersionRequest
{
    public int AppSystemSettingID = 37;
    public String CurrentVersion = GlobalVar.GV().AppVersion;
    public int AppTypeID = GlobalVar.GV().AppTypeID;
    public String AppVersion = GlobalVar.GV().AppVersion;
    public int LanguageID = GlobalVar.GV().GetLanguageID();
}
