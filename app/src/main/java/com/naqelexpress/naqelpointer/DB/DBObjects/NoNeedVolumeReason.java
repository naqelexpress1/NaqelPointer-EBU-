package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 17/03/2018.
 */

public class NoNeedVolumeReason {
    public int ID;
    public String Name;
    public String FName;

    public NoNeedVolumeReason() {
    }

    public NoNeedVolumeReason(int id, String name, String fname) {
        ID = id;
        Name = name;
        FName = fname;
    }


    public NoNeedVolumeReason(String finalJson, View view, Context context) {
        try {
            DBConnections dbConnections = new DBConnections(context, view);
            JSONArray jsonArray = new JSONArray(finalJson);

            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from NoNeedVolumeReason", context);
                boolean insert = false;
                if (result.getCount() > 0) {
                    if (result.getCount() < jsonArray.length() || result.getCount() > jsonArray.length()) {

//                    result.moveToFirst();
//                    do {
//                        dbConnections.deleteVolumeReason(Integer.parseInt(result.getString(result.getColumnIndex("ID"))), context, view);
//                    }
//                    while (result.moveToNext());
                        insert = true;
                        dbConnections.deleteAllVolumeReason(context);
                    }
                } else
                    insert = true;
                if (!insert)
                    return;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    NoNeedVolumeReason instance = new NoNeedVolumeReason();
                    try {
                        instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                        instance.Name = jsonObject.getString("Name");
                        instance.FName = jsonObject.getString("FName");
                        dbConnections.InsertVolumeReason(instance, context);
                    } catch (JSONException ignored) {
                    }
                }
            }

//            GlobalVar.GV().GetNoNeedVolumeReasonList(false, context, view);
//            GlobalVar.GV().currentSettings.LastBringMasterData = DateTime.now();
//            dbConnections.UpdateSettingsLastBringMasterData(GlobalVar.GV().currentSettings, view, context);
            dbConnections.close();
        } catch (JSONException ignored) {
        }
    }
}