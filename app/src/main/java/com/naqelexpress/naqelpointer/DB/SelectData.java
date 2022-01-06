package com.naqelexpress.naqelpointer.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;

import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectData {
    private static final String DBName = "NaqelPointerDB.db";

    public static MyRouteShipments GetMyRouteShipmentsbyWaybillNo(String WaybillNo, Context context) {

        MyRouteShipments myRouteShipments = new MyRouteShipments();

        DBConnections dbConnections = new DBConnections(context, null);
        int position = 1;
        Cursor result = dbConnections.Fill("select *  from MyRouteShipments where ItemNo = '" + WaybillNo + "' " +
                " order by id desc Limit 1", context);
        GlobalVar.GV().haslocation.clear();

        if (result.getCount() > 0) {
            result.moveToFirst();

            if (result.getCount() > 0) {

                result.moveToFirst();

                myRouteShipments.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                myRouteShipments.BillingType = result.getString(result.getColumnIndex("BillingType"));
                myRouteShipments.OrderNo = Integer.parseInt(result.getString(result.getColumnIndex("OrderNo")));
                myRouteShipments.DsOrderNo = Integer.parseInt(result.getString(result.getColumnIndex("DsOrderNo")));
                myRouteShipments.ItemNo = result.getString(result.getColumnIndex("ItemNo"));
                myRouteShipments.TypeID = Integer.parseInt(result.getString(result.getColumnIndex("TypeID")));
                myRouteShipments.CODAmount = GlobalVar.GV().getDoubleFromString(result.getString(result.getColumnIndex("CODAmount")));
                myRouteShipments.DeliverySheetID = Integer.parseInt(result.getString(result.getColumnIndex("DeliverySheetID")));
                myRouteShipments.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                myRouteShipments.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("ExpectedTime")));
                String Latitude = result.getString(result.getColumnIndex("Latitude"));
                String Longitude = result.getString(result.getColumnIndex("Longitude"));
                if ((Latitude.length() > 0 && Longitude.length() > 0) && !Latitude.equals("null") && !Longitude.equals("null")) {
                    Location sp = new Location("");
                    try {

                        sp.setLatitude(Double.parseDouble(Latitude));
                        sp.setLongitude(Double.parseDouble(Longitude));
                        if (Double.parseDouble(Longitude) != 0.0) {
                            GlobalVar.GV().haslocation.add(position);
                            sp.setSpeed(position);
                        }

                    } catch (Exception e) {
                        sp.setLatitude(0);
                        sp.setLongitude(0);
                    }

                }
                myRouteShipments.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
                myRouteShipments.ClientName = result.getString(result.getColumnIndex("ClientName"));
                myRouteShipments.ClientFName = result.getString(result.getColumnIndex("ClientFName"));
                myRouteShipments.ClientAddressPhoneNumber = result.getString(result.getColumnIndex("ClientAddressPhoneNumber"));
                myRouteShipments.ClientAddressFirstAddress = result.getString(result.getColumnIndex("ClientAddressFirstAddress"));
                myRouteShipments.ClientAddressSecondAddress = result.getString(result.getColumnIndex("ClientAddressSecondAddress"));
                myRouteShipments.ClientContactName = result.getString(result.getColumnIndex("ClientContactName"));
                myRouteShipments.ClientContactFName = result.getString(result.getColumnIndex("ClientContactFName"));
                myRouteShipments.ClientContactPhoneNumber = result.getString(result.getColumnIndex("ClientContactPhoneNumber"));
                myRouteShipments.ClientContactMobileNo = result.getString(result.getColumnIndex("ClientContactMobileNo"));
                myRouteShipments.ConsigneeName = result.getString(result.getColumnIndex("ConsigneeName"));
                myRouteShipments.ConsigneeFName = result.getString(result.getColumnIndex("ConsigneeFName"));
                myRouteShipments.ConsigneePhoneNumber = result.getString(result.getColumnIndex("ConsigneePhoneNumber"));
                myRouteShipments.ConsigneeFirstAddress = result.getString(result.getColumnIndex("ConsigneeFirstAddress"));
                myRouteShipments.ConsigneeSecondAddress = result.getString(result.getColumnIndex("ConsigneeSecondAddress"));
                myRouteShipments.ConsigneeNear = result.getString(result.getColumnIndex("ConsigneeNear"));
                myRouteShipments.ConsigneeMobile = result.getString(result.getColumnIndex("ConsigneeMobile"));
                myRouteShipments.Origin = result.getString(result.getColumnIndex("Origin"));
                myRouteShipments.Destination = result.getString(result.getColumnIndex("Destination"));
                myRouteShipments.PODNeeded = Boolean.parseBoolean(result.getString(result.getColumnIndex("PODNeeded")));
                myRouteShipments.PODDetail = result.getString(result.getColumnIndex("PODDetail"));
                myRouteShipments.PODTypeCode = result.getString(result.getColumnIndex("PODTypeCode"));
                myRouteShipments.PODTypeName = result.getString(result.getColumnIndex("PODTypeName"));
                myRouteShipments.IsDelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
                myRouteShipments.IsPartialDelivered = result.getInt(result.getColumnIndex("PartialDelivered")) > 0;
                myRouteShipments.NotDelivered = result.getInt(result.getColumnIndex("NotDelivered")) > 0;
                myRouteShipments.CourierDailyRouteID = Integer.parseInt(result.getString(result.getColumnIndex("CourierDailyRouteID")));
                myRouteShipments.OptimzeSerialNo = Integer.parseInt(result.getString(result.getColumnIndex("OptimzeSerialNo")));
                myRouteShipments.HasComplaint = result.getInt(result.getColumnIndex("HasComplaint")) > 0;
                myRouteShipments.HasDeliveryRequest = result.getInt(result.getColumnIndex("HasDeliveryRequest")) > 0;
                myRouteShipments.POS = result.getInt(result.getColumnIndex("POS"));
                myRouteShipments.IsPaid = result.getInt(result.getColumnIndex("Ispaid"));
                myRouteShipments.IsMap = result.getInt(result.getColumnIndex("IsMap"));
                myRouteShipments.CustomDuty = result.getDouble(result.getColumnIndex("CustomDuty"));
                myRouteShipments.Position = position + 1;
                myRouteShipments.isOtp = result.getInt(result.getColumnIndex("IsOtp"));


            }

            result.close();
            dbConnections.close();
        }
        return myRouteShipments;
    }

    public static HashMap<String, String> getPaperlessDSHeaderInfo(Context context) {
        HashMap<String, String> hm = new HashMap<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = dbConnections.Fill("SELECT Count(Distinct od.WaybillNo) DeliveredCount ," +
                "( Select Count(Distinct tnd.WaybillNo) from NotDelivered tnd Left Join OnDelivery tod on tod.WaybillNo = tnd.WaybillNo " +
                "and tod.DeliverysheetID = tnd.DeliverysheetID where tod.WaybillNo is null ) AttemptedCount ,  " +
                "(Select Count(Distinct tmr.ItemNo) from MyRouteShipments tmr  " +
                "left Join NotDelivered nd on nd.WaybillNo = tmr.ItemNo and nd.DeliverysheetID = tmr.DeliverysheetID " +
                "left Join OnDelivery od on od.WaybillNo = tmr.ItemNo and od.DeliverysheetID = tmr.DeliverysheetID  " +
                "where nd.ID is  null and od.ID is  null ) NotAttemptCount " +
                "from MyRouteShipments mr " +
                "left join NotDelivered nd on nd.WaybillNo = mr.ItemNo and nd.DeliverysheetID = mr.DeliverysheetID " +
                "left Join OnDelivery od on od.WaybillNo = mr.ItemNo and od.DeliverysheetID = mr.DeliverysheetID  ", context);


        if (result.getCount() > 0) {
            result.moveToFirst();


            hm.put("DeliveredCount", String.valueOf(result.getInt(result.getColumnIndex("DeliveredCount"))));
            hm.put("AttemptedCount", String.valueOf(result.getInt(result.getColumnIndex("AttemptedCount"))));
            hm.put("NotAttemptCount", String.valueOf(result.getInt(result.getColumnIndex("NotAttemptCount"))));


        }
        result.close();
        dbConnections.close();
        return hm;

    }

    public static List<Integer> AllowedFacilityStations(Context context, int DestFacilityID) {
        List<Integer> allowedDestStations = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(context, null);
        Cursor result = null;
        if (DestFacilityID != 0)
            result = dbConnections.Fill("Select * from FacilityAllowedStation where AllowedStationID =  " + DestFacilityID, context);
        else
            result = dbConnections.Fill("Select * from FacilityAllowedStation  ", context);

        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                allowedDestStations.add(result.getInt(result.getColumnIndex("AllowedStationID")));
            } while (result.moveToNext());
        }
        result.close();
        dbConnections.close();
        return allowedDestStations;

    }

    public List<Station> getStationsData(Context context) {

        List<Station> stationList = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            String selectQuery = "SELECT * FROM Station";
            db = SQLiteDatabase.openDatabase(context.getDatabasePath(DBName).getPath(),
                    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Station station = new Station();
                    station.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID"))));
                    station.setCode(cursor.getString(cursor.getColumnIndex("Code")));
                    station.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    stationList.add(station);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();


        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (db != null)
            db.close();
        return stationList;
    }
}
