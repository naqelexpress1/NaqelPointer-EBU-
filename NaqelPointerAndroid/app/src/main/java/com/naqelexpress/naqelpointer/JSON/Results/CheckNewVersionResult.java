package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckNewVersionResult
    extends DefaultResult
{
    public boolean HasNewVersion;
    private String NewVersion;
    private String WhatIsNew;
    public boolean IsMandatory;

    public CheckNewVersionResult(String finalJson)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(finalJson);
            HasNewVersion = Boolean.parseBoolean(jsonObject.getString("HasNewVersion"));
            NewVersion = jsonObject.getString("NewVersion");
            WhatIsNew = jsonObject.getString("WhatIsNew");
            IsMandatory = Boolean.parseBoolean(jsonObject.getString("IsMandatory"));
            HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
            ErrorMessage = jsonObject.getString("ErrorMessage");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}