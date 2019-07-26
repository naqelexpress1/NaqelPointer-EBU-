package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONObject;

/**
 * Created by sofan on 28/09/2017.
 */

public class CheckBeforeSubmitCODResult
        extends DefaultResult
{
    public String Notes;
    public CheckBeforeSubmitCODResult(String finalJson)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(finalJson);

            Notes = jsonObject.getString("Notes");
        }
        catch (Exception ignored){}
    }
}