package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NclNoResult extends DefaultResult {
    public String  NclNo ;
    public ArrayList<Integer> DestinationList= new ArrayList<>();
    public NclNoResult(String finalJson)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(finalJson);
            NclNo = jsonObject.getString("NclNo");
            JSONArray jArray= jsonObject.getJSONArray("DestinationList");
            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    DestinationList.add(jArray.getInt(i));
                }
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
