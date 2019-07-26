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

public class CheckPointTypeDetail {
    public int ID;
    public String Name;
    public String FName;
    public int CheckPointTypeID;

    public CheckPointTypeDetail(int id, String name, String fname, int checkPointTypeID) {
        ID = id;
        Name = name;
        FName = fname;
        CheckPointTypeID = checkPointTypeID;
    }

    public CheckPointTypeDetail() {
    }

    //  public View rootView;

    public CheckPointTypeDetail(String finalJson, View view, Context context) {
        //     this.rootView = view;
        try {
            DBConnections dbConnections = new DBConnections(context, view);
            JSONArray jsonArray = new JSONArray(finalJson);
            if (jsonArray.length() > 0) {
                //Delete the existing reasons
                boolean insert = false;

                Cursor result = dbConnections.Fill("select * from CheckPointTypeDetail", context);
                if (result.getCount() > 0) {
                    if (result.getCount() < jsonArray.length() || result.getCount() > jsonArray.length()) {

//                    result.moveToFirst();
//                    do {
//                        dbConnections.deleteCheckPointTypeDetail(Integer.parseInt(result.getString(result.getColumnIndex("ID"))), view, context);
//                    }
//                    while (result.moveToNext());
                        insert = true;
                        dbConnections.deleteAllCheckPointTypeDetail(context);
                    }
                } else
                    insert = true;


                if (!insert)
                    return;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CheckPointTypeDetail instance = new CheckPointTypeDetail();
                    try {
                        instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                        instance.Name = jsonObject.getString("Name");
                        instance.FName = jsonObject.getString("FName");
                        instance.CheckPointTypeID = Integer.parseInt(jsonObject.getString("CheckPointTypeID"));

                        dbConnections.InsertCheckPointTypeDetail(instance, context);
                    } catch (JSONException ignored) {
                    }
                }
            }
            //   GlobalVar.GV().GetCheckPointTypeDetailList(false, 0, context, view);
        } catch (JSONException ignored) {
        }
    }
}