package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeliveryStatus {
    public int ID;
    public String Code;
    public String Name;
    public String FName;
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

            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from DeliveryStatus", context);
                if (result.getCount() > 0) {
                    result.moveToFirst();
                    do {
                        dbConnections.deleteDeliveryStatus(Integer.parseInt(result.getString(result.getColumnIndex("ID"))), view, context);
                    }
                    while (result.moveToNext());
                }
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DeliveryStatus instance = new DeliveryStatus();
                try {
                    instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                    instance.Code = jsonObject.getString("Code");
                    instance.Name = jsonObject.getString("Name");
                    instance.FName = jsonObject.getString("FName");

                    dbConnections.InsertDeliveryStatus(instance, context);
                } catch (JSONException ignored) {
                }
            }
            dbConnections.close();
            GlobalVar.GV().GetDeliveryStatusList(false, context, view);
        } catch (JSONException ignored) {
        }
    }
}