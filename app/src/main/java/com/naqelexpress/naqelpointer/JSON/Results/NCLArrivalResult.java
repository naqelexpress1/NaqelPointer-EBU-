package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class NCLArrivalResult extends DefaultResult {

    public int WaybillNo;
    public int CWDestID ;
    public String CWDest ;
    public String WaybillDest ;
    public int WaybillDestID;
    public int IsManifested ;
    public int IsMultiPiece ;
    public int IsStopShipment;
    public int IsDestNotBelongToNcl;

    public NCLArrivalResult(String finalJson) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(finalJson);

            HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
            ErrorMessage = jsonObject.getString("ErrorMessage");

            WaybillNo = jsonObject.getJSONObject("NCLArrival").getInt("WaybillNo");
            CWDestID = jsonObject.getJSONObject("NCLArrival").getInt("CWDestID");
            CWDest = jsonObject.getJSONObject("NCLArrival").getString("CWDest");
            IsManifested = jsonObject.getJSONObject("NCLArrival").getInt("IsManifested");
            IsMultiPiece = jsonObject.getJSONObject("NCLArrival").getInt("IsMultiPiece");
            IsStopShipment = jsonObject.getJSONObject("NCLArrival").getInt("IsStopShipment");
            IsDestNotBelongToNcl = jsonObject.getInt("IsDestNotBelongToNcl");
            WaybillDestID =  jsonObject.getJSONObject("NCLArrival").optInt("WaybillDestID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
