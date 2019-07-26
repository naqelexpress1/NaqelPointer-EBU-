package com.naqelexpress.naqelpointer.DB.DBObjects;

/**
 * Created by sofan on 05/03/2018.
 */

import com.naqelexpress.naqelpointer.GlobalVar;
import org.joda.time.DateTime;

public class MultiDeliveryDetail
{
    public int ID = 0;
    public String BarCode = "";
    public boolean IsSync = false;
    public int MultiDeliveryID = 0;

    public MultiDeliveryDetail(String barCode, int multiDeliveryID)
    {
        IsSync = false;
        BarCode = barCode;
        MultiDeliveryID = multiDeliveryID;
    }
}