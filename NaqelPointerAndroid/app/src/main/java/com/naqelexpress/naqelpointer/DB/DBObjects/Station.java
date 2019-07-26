package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Station {
    public int ID;
    public String Code;
    public String Name;
    public String FName;
    public int CountryID;

    public Station() {
    }

    public Station(int id, String code, String name, String fname, int countryID) {
        ID = id;
        Code = code;
        Name = name;
        FName = fname;
        CountryID = countryID;
    }

    public View rootView;

    public Station(String finalJson, View view, Context context) {

        this.rootView = view;
        try {
            DBConnections dbConnections = new DBConnections(context, view);
            // JSONObject dataObject = new JSONObject(finalJson);
            JSONArray jsonArray = new JSONArray(finalJson);
            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from Station",context);
                if (result.getCount() > 0) {
                    result.moveToFirst();
                    do {
                        dbConnections.deleteStation(Integer.parseInt(result.getString(result.getColumnIndex("ID"))), context, view);
                    }
                    while (result.moveToNext());
                }
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Station instance = new Station();
                try {
                    instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                    instance.Name = jsonObject.getString("Name");
                    instance.FName = jsonObject.getString("FName");
                    instance.CountryID = jsonObject.getInt("CountryID");
                    instance.Code = jsonObject.getString("Code");

                    dbConnections.InsertStation(instance, context);
                } catch (JSONException ignored) {
                }
            }
            GlobalVar.GV().GetStationList(false, context, view);
        } catch (JSONException ignored) {
            System.out.println(ignored.getMessage());
        }
    }
}
