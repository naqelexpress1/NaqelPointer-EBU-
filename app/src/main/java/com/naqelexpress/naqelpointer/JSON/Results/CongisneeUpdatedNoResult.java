package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class CongisneeUpdatedNoResult extends DefaultResult {

    public String MobileNo;
    public String PhoneNo;

    public CongisneeUpdatedNoResult(String finalJson) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(finalJson);

            HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
            ErrorMessage = jsonObject.getString("ErrorMessage");

            MobileNo = jsonObject.getString("MobileNo");
            PhoneNo = jsonObject.getString("PhoneNo");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
