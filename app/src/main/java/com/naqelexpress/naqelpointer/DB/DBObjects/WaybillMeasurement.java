package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sofan on 17/03/2018.
 */

public class WaybillMeasurement {
    public int ID = 0;
    public int WaybillNo = 0;
    public int TotalPieces = 0;
    public int EmployID = GlobalVar.GV().EmployID;
    public int StationID = GlobalVar.GV().StationID;
    public int UserID = GlobalVar.GV().UserID;
    public DateTime CTime = DateTime.now();
    public boolean IsSync = false;
    public String HHD = "";
    public double Weight = 0;
    public boolean NoNeedVolume = false;
    public int NoNeedVolumeReasonID = 0;

    public List<WaybillMeasurementDetail> WaybillMeasurementDetails;

    public WaybillMeasurement() {
        WaybillMeasurementDetails = new ArrayList<>();
    }

    public WaybillMeasurement(int waybillNo, int totalPieces, String hHD,
                              double weight, boolean noNeedVolume, int noNeedVolumeReasonID) {
        WaybillNo = waybillNo;
        TotalPieces = totalPieces;
        HHD = hHD;
        Weight = weight;
        NoNeedVolume = noNeedVolume;
        NoNeedVolumeReasonID = noNeedVolumeReasonID;
    }
}