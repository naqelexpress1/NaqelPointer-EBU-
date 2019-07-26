package com.naqelexpress.naqelpointer.JSON.Results;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetShipmentForPickingResult
    extends DefaultResult
{
    public int ID = 0;
    public int WaybillNo = 0;
    public int PiecesCount = 0;
    public double Weight = 0;
    private String BillingType = "";
    private double CODAmount = 0;
    private String ConsigneeName = "";
    private String ConsigneeFName = "";
    private String PhoneNo = "";
    private String MobileNo = "";
    private String Address = "";
    private String SecondLine;
    private String Near;
    private LocationCoordinate locationCoordinate;

    private GetShipmentForPickingResult()
    {
        locationCoordinate = new LocationCoordinate();
    }

    public GetShipmentForPickingResult(String finalJson)
    {
        JSONObject jsonObject;
        try
        {
            JSONArray jsonArray = new JSONArray(finalJson);
            for(int i = 0; i < jsonArray.length(); i++)
            {
                GetShipmentForPickingResult instance = new GetShipmentForPickingResult();
                jsonObject = jsonArray.getJSONObject(i);

                instance.ID = Integer.parseInt(jsonObject.getString("ID"));
                instance.WaybillNo = Integer.parseInt(jsonObject.getString("WaybillNo"));
                instance.PiecesCount = Integer.parseInt(jsonObject.getString("PiecesCount"));
                instance.Weight = Double.parseDouble(jsonObject.getString("Weight"));
                instance.BillingType = jsonObject.getString("BillingType");
                instance.CODAmount = Double.parseDouble(jsonObject.getString("CODAmount"));
                instance.ConsigneeName = jsonObject.getString("ConsigneeName");
                instance.ConsigneeFName = jsonObject.getString("ConsigneeFName");
                instance.PhoneNo = jsonObject.getString("PhoneNo");
                instance.MobileNo = jsonObject.getString("MobileNo");
                instance.Address = jsonObject.getString("Address");
                instance.SecondLine = jsonObject.getString("SecondLine");
                instance.Near = jsonObject.getString("Near");
                instance.locationCoordinate = new LocationCoordinate();
                JSONObject Coordinate = jsonObject.getJSONObject("LocationCoordinate");
                instance.locationCoordinate.Longitude= Coordinate.getString("Longitude");
                instance.locationCoordinate.Latitude = Coordinate.getString("Latitude");

                GlobalVar.GV().GetShipmentForPickingResultList.add(instance);
                //Toast.makeText(GlobalVar.GV().context,this.WaybillNo,Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}