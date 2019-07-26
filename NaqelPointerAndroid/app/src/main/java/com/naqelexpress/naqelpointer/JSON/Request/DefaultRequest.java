package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

class DefaultRequest
{
    public int AppTypeID = GlobalVar.GV().AppTypeID;
    public String AppVersion = GlobalVar.GV().AppVersion;
    public int LanguageID = GlobalVar.GV().GetLanguageID();
}