package com.naqelexpress.naqelpointer.JSON.Request;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sofan on 24/10/2017.
 */

public class SendCheckPointRequest
{
    public int ID;
    public DateTime Date;
    public int CheckPointTypeID;
    public int EmployID;
    public String Latitude;
    public String Longitude;

    public List<CheckPointWaybillDetailRequest> CheckPointWaybillDetailsRequestList;

    public SendCheckPointRequest()
    {
//        CheckPointWaybillDetailsRequestList = new CheckPointWaybillDetailRequest();
    }
}