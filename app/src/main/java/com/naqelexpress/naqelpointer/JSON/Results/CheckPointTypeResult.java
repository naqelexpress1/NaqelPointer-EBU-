package com.naqelexpress.naqelpointer.JSON.Results;

import android.content.Context;
import android.view.View;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Created by sofan on 24/10/2017.

public class CheckPointTypeResult {
    public int ID;
    public String Code;
    public String Name;
    public String FName;
    public View rootView;

    public CheckPointTypeResult() {

    }

    public CheckPointTypeResult(String finalJson, View view, Context context) {
        this.rootView = view;
        try {
            JSONArray jsonArray = new JSONArray(finalJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CheckPointTypeResult instance = new CheckPointTypeResult();
                try {
                    instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                    instance.Code = jsonObject.getString("Code");
                    instance.Name = jsonObject.getString("Name");
                    instance.FName = jsonObject.getString("FName");
                } catch (JSONException ignored) {
                }
                GlobalVar.GV().checkPointTypeResultsList.add(instance);
            }
            GlobalVar.GV().GetCheckPointTypeList(false, context, view);
        } catch (JSONException ignored) {
        }
    }

//    private void CheckCheckPointType(CheckPointType checkPointType) {
//        CheckPointType checkPointType1 = new CheckPointType(checkPointType.ID, checkPointType.Name, checkPointType.FName);
//        DBConnections dbConnections = new DBConnections(GlobalVar.GV().context, rootView);
//        dbConnections.deleteCheckPointType(checkPointType);
//        dbConnections.InsertCheckPointType(checkPointType);
//    }
}