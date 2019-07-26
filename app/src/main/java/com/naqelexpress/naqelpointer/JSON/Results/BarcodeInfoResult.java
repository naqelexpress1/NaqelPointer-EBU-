package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class BarcodeInfoResult extends DefaultResult {
    public long BarCode;
    public int WayBillNo;
    public int DestId;
    public double weight ;
    public BarcodeInfoResult(String finalJson)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(finalJson);
            BarCode = Long.parseLong(jsonObject.getString("BarCode"));
            WayBillNo = Integer.parseInt(jsonObject.getString("WayBillNo"));
            DestId = Integer.parseInt(jsonObject.getString("DestId"));
            weight = jsonObject.getDouble("Weight");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
