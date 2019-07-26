package com.naqelexpress.naqelpointer.JSON.Request;

import java.util.ArrayList;

/**
 * Created by sofan on 12/10/2017.
 */

public class CheckWaybillAlreadyPickedUpRequest
        extends DefaultRequest {
    public int WaybillNo;

    public ArrayList<String> BarCode;
}