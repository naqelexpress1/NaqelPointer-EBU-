package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Contacts {


    public Contacts() {
    }


    public Contacts(String finalJson, View view, Context context) {

        try {
            DBConnections dbConnections = new DBConnections(context, view);
            JSONArray jsonArray = new JSONArray(finalJson);
            boolean insert = false;
            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from Contacts", context);
                if (result.getCount() > 0) {
                    if (result.getCount() < jsonArray.length() || result.getCount() > jsonArray.length()) {

                        insert = true;
                        dbConnections.deleteAllContacts(context);
                    }
                } else
                    insert = true;

                if (!insert)
                    return;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    try {
                        String  name = jsonObject.getString("Name");
                        String phoneno = jsonObject.getString("PhoneNo");
                        int StationID = jsonObject.getInt("StationID");
                        int isprimary = jsonObject.getInt("Secondary");
                        dbConnections.InsertContacts(name,  StationID, phoneno, isprimary, context);

                    } catch (JSONException ignored) {
                    }
                }
            }
            dbConnections.close();
            //GlobalVar.GV().GetDeliveryStatusList(false, context, view);
        } catch (JSONException ignored) {
        }
    }
}