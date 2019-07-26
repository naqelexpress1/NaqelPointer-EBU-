package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class SendingDataResult
    extends DefaultResult
{
    public int ID;
    public boolean IsSync;

    public SendingDataResult(String finalJson)
    {
        JSONObject jsonObject ;
        try
        {
            jsonObject = new JSONObject(finalJson);
            ID = Integer.parseInt(jsonObject.getString("ID"));
            IsSync = Boolean.parseBoolean(jsonObject.getString("IsSync"));
            HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
            ErrorMessage = jsonObject.getString("ErrorMessage");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}