package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class BringPickUpDataResult
    extends DefaultResult
{
    public int ClientID;
    public int OriginStationID;
    public int DestinationStationID;
    public double PiecesCount;
    public double Weight;

    public BringPickUpDataResult(String finalJson)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(finalJson);
            ClientID = Integer.parseInt(jsonObject.getString("ClientID"));
            OriginStationID = Integer.parseInt(jsonObject.getString("OriginStationID"));
            DestinationStationID = Integer.parseInt(jsonObject.getString("DestinationStationID"));
            PiecesCount = Double.parseDouble(jsonObject.getString("PiecesCount"));
            Weight = Double.parseDouble(jsonObject.getString("Weight"));
            HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
            ErrorMessage = jsonObject.getString("ErrorMessage");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
