package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacilityStatus {
    public int ID;
    public String Code;
    public String Name;
    public int StationID;
    public int FacilityTypeId;


    public FacilityStatus() {
    }

    public FacilityStatus(int id, String code, String name, int StationID, int FacilityTypeId) {
        ID = id;
        Code = code;
        Name = name;
        this.StationID = StationID;
        this.FacilityTypeId = FacilityTypeId;

    }


    public FacilityStatus(String finalJson, View view, Context context) {

        try {
            DBConnections dbConnections = new DBConnections(context, view);
            JSONArray jsonArray = new JSONArray(finalJson);
            boolean insert = false;
            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from Facility", context);
                if (result.getCount() > 0) {
                    if (result.getCount() < jsonArray.length() || result.getCount() > jsonArray.length()) {

                        insert = true;
                        dbConnections.deleteAllFacility(context);
                    }
                } else
                    insert = true;

                if (!insert)
                    return;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    try {
                        int FacilityID = jsonObject.getInt("ID");
                        String Code = jsonObject.getString("Code");
                        String Fname = jsonObject.getString("Name");
                        int StationID = jsonObject.getInt("StationID");
                        int FtypeID = jsonObject.getInt("FacilityTypeId");
                        String FTName = "";

                        dbConnections.InsertFacility(FacilityID, Code, Fname, StationID, FtypeID, FTName, context);

                    } catch (JSONException ignored) {
                    }
                }
            }
            dbConnections.close();
            //GlobalVar.GV().GetDeliveryStatusList(false, context, view);
        } catch (JSONException ignored) {
        }
    }


    public FacilityStatus(String finalJson, View view, Context context, int different) {

        try {
            DBConnections dbConnections = new DBConnections(context, view);
            JSONArray jsonArray = new JSONArray(finalJson);
            boolean insert = false;
            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from DeliveryStatusReason", context);
                if (result.getCount() > 0) {
                    if (result.getCount() < jsonArray.length() || result.getCount() > jsonArray.length()) {

                        insert = true;
                        dbConnections.deleteAllDeliveryStatusReason(context);
                    }
                } else
                    insert = true;

                if (!insert)
                    return;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    try {
                        int ReasonID = jsonObject.getInt("ID");
                        String Code = jsonObject.getString("Code");
                        String Name = jsonObject.getString("Name");
                        String FName = jsonObject.getString("FName");
                        int DSID = jsonObject.getInt("DeliveryStatusID");

                        dbConnections.InsertDeliveryStatusReason(ReasonID, Code, Name, DSID, context);
                    } catch (JSONException ignored) {
                    }
                }
            }
            dbConnections.close();
        } catch (JSONException ignored) {
        }
    }


}