package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WaybillDetailsResult
        extends DefaultResult {
    public int ID = 0;
    public int WaybillNo = 0;
    public int PiecesCount = 0;
    public double Weight = 0;
    public String BillingType = "";
    public double CODAmount = 0;
    public String ConsigneeName = "";
    private String ConsigneeFName = "";
    public String PhoneNo = "";
    public String MobileNo = "";
    public String Address = "";
    public String SecondLine;
    public String Near;
    private String CityName = "";
    private String CityFName = "";
    private LocationCoordinate locationCoordinate;
    public boolean signrequired = false;
    public  String ClientName = "";

    public ArrayList<String> BarCodeList = new ArrayList<String>();

    public WaybillDetailsResult() {
        BarCodeList = new ArrayList<>();
        locationCoordinate = new LocationCoordinate();
    }

    public WaybillDetailsResult(String finalJson) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(finalJson);

            ID = Integer.parseInt(jsonObject.getString("ID"));
            WaybillNo = Integer.parseInt(jsonObject.getString("WaybillNo"));
            PiecesCount = (int) Double.parseDouble(jsonObject.getString("PiecesCount"));
            Weight = Double.parseDouble(jsonObject.getString("Weight"));
            BillingType = jsonObject.getString("BillingType");
            CODAmount = Double.parseDouble(jsonObject.getString("CODAmount"));
            ConsigneeName = jsonObject.getString("ConsigneeName");
            ConsigneeFName = jsonObject.getString("ConsigneeFName");
            PhoneNo = jsonObject.getString("PhoneNo");
            MobileNo = jsonObject.getString("MobileNo");
            Address = jsonObject.getString("Address");
            SecondLine = jsonObject.getString("SecondLine");
            Near = jsonObject.getString("Near");
            CityName = jsonObject.getString("CityName");
            CityFName = jsonObject.getString("CityFName");
            ClientName =   jsonObject.getString("ClientName");
            //if (jsonObject.getInt("ClientID") == 9018270)
            if (jsonObject.getInt("Sign") == 1)
                signrequired = true;

            locationCoordinate = new LocationCoordinate();
            BarCodeList = new ArrayList<>();

            JSONObject Coordinate = jsonObject.getJSONObject("locationCoordinate");
            locationCoordinate.Longitude = Coordinate.getString("Longitude");
            locationCoordinate.Latitude = Coordinate.getString("Latitude");

            JSONArray jsonArray = jsonObject.getJSONArray("BarCodeList");
            for (int i = 0; i < jsonArray.length(); i++) {
                String barcode = jsonArray.getString(i);
                BarCodeList.add(barcode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}