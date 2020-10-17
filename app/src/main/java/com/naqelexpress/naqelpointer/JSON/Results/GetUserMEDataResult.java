package com.naqelexpress.naqelpointer.JSON.Results;

import org.json.JSONException;
import org.json.JSONObject;

public class GetUserMEDataResult
        extends DefaultResult {
    public int ID;
    public int EmployID;
    public String Password;
    public int StationID;
    public int RoleMEID;
    public int StatusID;

    public String EmployName;
    public String EmployFName;
    public String MobileNo;
    public String StationCode;
    public String StationName;
    public String StationFName;
    public String Division;
    public int UsertypeId = 0;
    public int Menu = 0;
    public int UpdateMenu = 0;
    public String AppName;
    public int Appversion;
    public int DisableEnabletxtBox = 1;
    public int CountryID = 0;
    public String CountryCode;
    public boolean PwdNeedUpdate;


    public GetUserMEDataResult(String finalJson) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(finalJson);
            ID = Integer.parseInt(jsonObject.getString("ID"));
            EmployID = Integer.parseInt(jsonObject.getString("EmployID"));
            Password = "" ; // jsonObject.getString("Password");
            StationID = Integer.parseInt(jsonObject.getString("StationID"));
            RoleMEID = Integer.parseInt(jsonObject.getString("RoleMEID"));
            StatusID = Integer.parseInt(jsonObject.getString("StatusID"));

            EmployName = jsonObject.getString("EmployName");
            EmployFName = jsonObject.getString("EmployFName");
            MobileNo = jsonObject.getString("MobileNo");
            StationCode = jsonObject.getString("StationCode");
            StationName = jsonObject.getString("StationName");
            StationFName = jsonObject.getString("StationFName");
            Division = jsonObject.getString("Division");
            UsertypeId = jsonObject.getInt("UserTypeID");
            Menu = jsonObject.getInt("Menu");
            UpdateMenu = jsonObject.getInt("ChangesMainMenu");
            AppName = jsonObject.getString("AppName");
            Appversion = jsonObject.getInt("Appversion");
            DisableEnabletxtBox = jsonObject.getInt("DisableEnabletxtBox");
            CountryID = jsonObject.getInt("CountryID");
            CountryCode = jsonObject.getString("CountryCode");
            PwdNeedUpdate = jsonObject.getBoolean("PwdNeedUpdate");

            HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
            ErrorMessage = jsonObject.getString("ErrorMessage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
