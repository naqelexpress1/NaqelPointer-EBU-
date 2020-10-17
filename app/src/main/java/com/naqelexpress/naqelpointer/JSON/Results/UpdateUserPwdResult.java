package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUserPwdResult extends DefaultResult {

    public String LastPwdUpdate;

    public UpdateUserPwdResult(String finalJson) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(finalJson);
            LastPwdUpdate = jsonObject.getString("LastPwdUpdate");
            HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
            ErrorMessage = jsonObject.getString("ErrorMessage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
