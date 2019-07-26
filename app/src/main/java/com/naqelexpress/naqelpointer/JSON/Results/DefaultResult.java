package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class DefaultResult
{
    public boolean HasError;
    public String ErrorMessage;

    public DefaultResult()
    {

    }

    public DefaultResult(String finalJson)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(finalJson);
            HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
            ErrorMessage = jsonObject.getString("ErrorMessage");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}