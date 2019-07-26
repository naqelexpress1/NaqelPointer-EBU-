package com.naqelexpress.naqelpointer.JSON.Request;

import com.naqelexpress.naqelpointer.GlobalVar;

/**
 * Created by sofan on 28/09/2017.
 */

public class CheckBeforeSubmitCODRequest
{
    public int EmployID = GlobalVar.GV().EmployID;
    public int DeliverySheetID = 0;
    public String DeliverySheetDate = "";
    public double TotalCash = 0;
    public double TotalPOS = 0;
}