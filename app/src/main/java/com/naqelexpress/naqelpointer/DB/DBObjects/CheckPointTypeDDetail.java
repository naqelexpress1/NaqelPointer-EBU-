package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 13/04/2018.
 */

public class CheckPointTypeDDetail {
    public int ID;
    public String Name;
    public String FName;
    public int CheckPointTypeDetailID;

    public CheckPointTypeDDetail(int id, String name, String fname, int checkPointTypeDetailID) {
        ID = id;
        Name = name;
        FName = fname;
        CheckPointTypeDetailID = checkPointTypeDetailID;
    }

    public CheckPointTypeDDetail() {
    }

    //  public View rootView;

    public CheckPointTypeDDetail(String finalJson, View view, Context context) {
        //  this.rootView = view;
        try {
            DBConnections dbConnections = new DBConnections(context, view);
            JSONArray jsonArray = new JSONArray(finalJson);
            if (jsonArray.length() > 0) {
                boolean insert = false;
                //Delete the existing reasons
                Cursor result = dbConnections.Fill("select * from CheckPointTypeDDetail", context);
                if (result.getCount() > 0) {
                    if (result.getCount() < jsonArray.length() || result.getCount() > jsonArray.length()) {

//                        result.moveToFirst();
//                        do {
//                            dbConnections.deleteCheckPointTypeDDetail(Integer.parseInt(result.getString(result.getColumnIndex("ID"))), view, context);
//                        }
//                        while (result.moveToNext());
                        insert = true;
                        dbConnections.deleteAllCheckPointTypeDDetail(context);
                    }
                } else
                    insert = true;
                if (!insert)
                    return;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CheckPointTypeDDetail instance = new CheckPointTypeDDetail();
                    try {
                        instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                        instance.Name = jsonObject.getString("Name");
                        instance.FName = jsonObject.getString("FName");
                        instance.CheckPointTypeDetailID = Integer.parseInt(jsonObject.getString("CheckPointTypeDetailID"));

                        dbConnections.InsertCheckPointTypeDDetail(instance, context);
                    } catch (JSONException ignored) {
                    }
                }
            }
//            GlobalVar.GV().GetCheckPointTypeDDetailList(false, 0, context, view);
            dbConnections.close();
        } catch (JSONException ignored) {
        }
    }
}