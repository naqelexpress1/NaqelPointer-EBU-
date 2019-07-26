package com.naqelexpress.naqelpointer.DB.DBObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sofan on 17/03/2018.
 */

public class WayBills {

    public List<WaybillMeasurement> WayBills;
    public List<WaybillMeasurementDetail> WaybillMeasurementDetails;

    public WayBills() {
        WayBills = new ArrayList<>();
        WaybillMeasurementDetails = new ArrayList<>();
    }


}