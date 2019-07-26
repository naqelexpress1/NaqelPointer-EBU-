package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

/**
 * Created by sofan on 23/03/2018.
 */

public class DataTableParameters
{
    public String Source;
    public String FilterString;
    public int Start;
    public int Length;
    public int AppID = GlobalVar.GV().AppID;
}