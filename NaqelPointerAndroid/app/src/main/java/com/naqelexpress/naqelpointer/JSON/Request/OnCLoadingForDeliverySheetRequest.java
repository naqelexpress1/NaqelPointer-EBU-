package com.naqelexpress.naqelpointer.JSON.Request;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class OnCLoadingForDeliverySheetRequest
{
    public int ID;
    public int CourierID;
    public int UserID;
    public DateTime CTime;
    public int PieceCount;
    public String TruckID;
    public int WaybillCount;
    public int StationID;

    public List<OnCLoadingForDeliverySheetWaybill> OnCLoadingForDeliverySheetWaybillList;
    public List<OnCLoadingForDeliverySheetPiece> OnCLoadingForDeliverySheetPieceList;

    public OnCLoadingForDeliverySheetRequest()
    {
        OnCLoadingForDeliverySheetWaybillList = new ArrayList<>();
        OnCLoadingForDeliverySheetPieceList = new ArrayList<>();
    }
}
