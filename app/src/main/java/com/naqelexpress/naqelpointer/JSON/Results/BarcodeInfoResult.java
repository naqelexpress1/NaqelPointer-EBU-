package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class BarcodeInfoResult extends DefaultResult {
    public long BarCode;
    public int WayBillNo;
    public int DestId;
    public double weight;
    public double DecalaredValue;

    public BarcodeInfoResult(String finalJson) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(finalJson);
            BarCode = Long.parseLong(jsonObject.getString("BarCode"));
            WayBillNo = Integer.parseInt(jsonObject.getString("WayBillNo"));
            DestId = Integer.parseInt(jsonObject.getString("DestId"));
            DecalaredValue = jsonObject.getDouble("DecalaredValue");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
