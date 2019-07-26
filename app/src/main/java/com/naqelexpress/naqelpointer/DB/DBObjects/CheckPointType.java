package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 24/10/2017.
 */

public class CheckPointType {
    public int ID;
    public String Name;
    public String FName;

    public CheckPointType(int id, String name, String fname) {
        ID = id;
        Name = name;
        FName = fname;
    }

    public CheckPointType() {
    }


    public CheckPointType(String finalJson, View view, Context context) {

        try {
            DBConnections dbConnections = new DBConnections(context, view);
            JSONArray jsonArray = new JSONArray(finalJson);
            boolean insert = false;
            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from CheckPointType", context);
                if (result.getCount() > 0) {
                    if (result.getCount() < jsonArray.length() || result.getCount() > jsonArray.length()) {

//                        result.moveToFirst();
//                        do {
//                            dbConnections.deleteCheckPointType(Integer.parseInt(result.getString(result.getColumnIndex("ID"))), view, context);
//                        }
//                        while (result.moveToNext());
                        insert = true;
                        dbConnections.deleteAllCheckpoint(context);
                    }
                } else
                    insert = true;

                if (!insert)
                    return;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CheckPointType instance = new CheckPointType();
                    try {
                        instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                        instance.Name = jsonObject.getString("Name");
                        instance.FName = jsonObject.getString("FName");

                        dbConnections.InsertCheckPointType(instance, context);
                    } catch (JSONException ignored) {
                    }
                }
            }
            // GlobalVar.GV().GetCheckPointTypeList(false, context, view);
            dbConnections.close();
        } catch (JSONException ignored) {
        }
    }
}