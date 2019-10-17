package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeliveryStatus {
    public int ID;
    public String Code;
    public String Name;
    public String FName;
    public int SeqOrder ;
    //Need exceptions for Express and Exceptions for Couriers

//    public DeliveryStatus(int ID)
//    {
//        this.ID = ID;
//    }

    public DeliveryStatus() {
    }

    public DeliveryStatus(int id, String code, String name, String fname) {
        ID = id;
        Code = code;
        Name = name;
        FName = fname;
    }


    public DeliveryStatus(String finalJson, View view, Context context) {

        try {
            DBConnections dbConnections = new DBConnections(context, view);
            JSONArray jsonArray = new JSONArray(finalJson);
            boolean insert = false;
            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from DeliveryStatus", context);
                if (result.getCount() > 0) {
                    if (result.getCount() < jsonArray.length() || result.getCount() > jsonArray.length()) {

//                    result.moveToFirst();
//                    do {
//                        dbConnections.deleteDeliveryStatus(Integer.parseInt(result.getString(result.getColumnIndex("ID"))), view, context);
//                    }
//                    while (result.moveToNext());
                        insert = true;
                        dbConnections.deleteAllDeliveryStatus(context);
                    }
                } else
                    insert = true;

                if (!insert)
                    return;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    DeliveryStatus instance = new DeliveryStatus();
                    try {
                        instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                        instance.Code = jsonObject.getString("Code");
                        instance.Name = jsonObject.getString("Name");
                        instance.FName = jsonObject.getString("FName");
                        instance.SeqOrder = jsonObject.getInt("Priority");

                        dbConnections.InsertDeliveryStatus(instance, context);
                    } catch (JSONException ignored) {
                    }
                }
            }
            dbConnections.close();
            //GlobalVar.GV().GetDeliveryStatusList(false, context, view);
        } catch (JSONException ignored) {
        }
    }


    public DeliveryStatus(String finalJson, View view, Context context, int different) {

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