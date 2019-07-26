package com.naqelexpress.naqelpointer.JSON.Results;

import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBObjects.NoNeedVolumeReason;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StationResult
    extends DefaultResult
{
    public int ID;
    private String Code;
    public String Name;
    private String FName;
    private int CountryID;

    private StationResult()
    {

    }

//    public StationResult(String finalJson)
//    {
//        try
//        {
//            JSONArray jsonArray = new JSONArray(finalJson);
//            for(int i = 0; i < jsonArray.length(); i++)
//            {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                StationResult instance = new StationResult();
//                try
//                {
//                    instance.ID = Integer.parseInt(jsonObject.getString("ID"));
//                    instance.Code = jsonObject.getString("Code");
//                    instance.Name = jsonObject.getString("Name");
//                    instance.FName = jsonObject.getString("FName");
//                    instance.CountryID = Integer.parseInt(jsonObject.getString("CountryID"));
//                    HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
//                    ErrorMessage = jsonObject.getString("ErrorMessage");
//
//                    CheckStation(instance);
//                }
//                catch (JSONException ignored){}
//            }
//            //GlobalVar.GV().GetStationList(false);
//        }
//        catch (JSONException ignored){}
//    }

//    private void CheckStation(StationResult stationResult)
//    {
//        Station station = new Station(stationResult.ID,stationResult.Code,stationResult.Name,stationResult.FName,stationResult.CountryID);
//        DBConnections dbConnections = new DBConnections(GlobalVar.GV().context,GlobalVar.GV().rootView);
//        dbConnections.deleteStation(station);
//        dbConnections.InsertStation(station);
//    }
}