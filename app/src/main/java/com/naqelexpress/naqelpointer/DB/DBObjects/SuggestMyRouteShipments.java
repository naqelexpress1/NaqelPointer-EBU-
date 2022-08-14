package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.core.app.ActivityCompat;
import android.view.View;

import com.naqelexpress.naqelpointer.Activity.SuggestDeliverysheet.MyRouteActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SuggestMyRouteShipments implements Parcelable {
    public int ID = 0;
    public int OrderNo = 0;
    public String ItemNo = "";
    public int TypeID = 0;
    public String BillingType = "";
    public double CODAmount = 0;
    public int DeliverySheetID = 0;
    public DateTime Date = DateTime.now();
    public DateTime ExpectedTime = DateTime.now();
    public String Latitude = "0";
    public String Longitude = "0";
    public int ClientID = 0;
    public String ClientName = "";
    public String ClientFName = "";
    public String ClientAddressPhoneNumber = "";
    public String ClientAddressFirstAddress = "";
    public String ClientAddressSecondAddress = "";
    public String ClientContactName = "";
    public String ClientContactFName = "";
    public String ClientContactPhoneNumber = "";
    public String ClientContactMobileNo = "";
    public String ConsigneeName = "";
    public String ConsigneeFName = "";
    public String ConsigneePhoneNumber = "";
    public String ConsigneeFirstAddress = "";
    public String ConsigneeSecondAddress = "";
    public String ConsigneeNear = "";
    public String ConsigneeMobile = "";
    public String Origin = "";
    public String Destination = "";
    public boolean PODNeeded = false;
    public String PODDetail = "";
    public String PODTypeCode = "";
    public String PODTypeName = "";
    public boolean IsDelivered = false;
    public boolean NotDelivered = false;
    public int CourierDailyRouteID;
    public int OptimzeSerialNo;
    public boolean HasComplaint = false;
    public boolean HasDeliveryRequest = false;
    public String Header = "";
    public int Sign;
    public int SeqNo;
    public String ExistUser = "";
    public String PiecesCount = "";
    public String Weight = "";

    public SuggestMyRouteShipments() {

    }

    public SuggestMyRouteShipments(String finalJson, String Latitude, String Longitude, Context context, View view) {

        MyRouteActivity.myRouteShipmentList.clear();
        JSONArray jsonObjectDeliverySheet = null;

        try {
            JSONObject jsonObjectHeader = new JSONObject(finalJson);
            jsonObjectDeliverySheet = jsonObjectHeader.getJSONArray("DeliverySheet");
            if (jsonObjectDeliverySheet.length() == 0) {
                GlobalVar.GV().ShowSnackbar(view, "No Data with this ID", GlobalVar.AlertType.Error);
                return;
            }
        } catch (JSONException e) {
            GlobalVar.GV().ShowSnackbar(view, "No Data with this ID", GlobalVar.AlertType.Error);

            e.printStackTrace();
            return;
        }

        DBConnections dbConnections = new DBConnections(context, null);
        MyRouteActivity.places.clear();

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location location = GlobalVar.getLastKnownLocation(context);
            if (location != null)

                MyRouteActivity.places.add(location);
        }

        JSONObject jsonObject;
        try {
            JSONArray jsonArray = jsonObjectDeliverySheet;

            for (int i = 0; i < jsonArray.length(); i++) {
                SuggestMyRouteShipments instance = new SuggestMyRouteShipments();
                jsonObject = jsonArray.getJSONObject(i);

                //instance.ID = i + 1;
                instance.SeqNo = i + 1;
                instance.BillingType = jsonObject.getString("BillingType");

                instance.ClientAddressFirstAddress = jsonObject.getString("ClientAddressFirstAddress");
                instance.ClientAddressFirstAddress = jsonObject.getString("ClientAddressLocation");
                instance.ClientAddressPhoneNumber = jsonObject.getString("ClientAddressPhoneNumber");
                instance.ClientAddressSecondAddress = jsonObject.getString("ClientAddressSecondAddress");

                instance.ClientContactFName = jsonObject.getString("ClientContactFName");
                instance.ClientContactMobileNo = jsonObject.getString("ClientContactMobileNo");
                instance.ClientContactName = jsonObject.getString("ClientContactName");
                instance.ClientContactPhoneNumber = jsonObject.getString("ClientContactPhoneNumber");

                instance.ClientID = Integer.parseInt(jsonObject.getString("ClientID"));
                instance.ClientName = jsonObject.getString("ClientName");
                instance.ClientFName = jsonObject.getString("ClientFName");

                instance.CODAmount = Double.parseDouble(jsonObject.getString("CODAmount"));

                instance.ConsigneeName = jsonObject.getString("ConsigneeName");
                instance.ConsigneeFName = jsonObject.getString("ConsigneeFName");
                instance.ConsigneePhoneNumber = jsonObject.getString("ConsigneePhoneNumber");
                instance.ConsigneeFirstAddress = jsonObject.getString("ConsigneeFirstAddress");
                instance.ConsigneeSecondAddress = jsonObject.getString("ConsigneeSecondAddress");
                instance.ConsigneeNear = jsonObject.getString("ConsigneeNear");
                instance.ConsigneeMobile = jsonObject.getString("ConsigneeMobile");

                String dt = jsonObject.getString("Date");
                String result = dt.replaceAll("^/Date\\(", "");
                instance.DeliverySheetID = Integer.parseInt(jsonObject.getString("DeliverySheetID"));
                instance.Destination = jsonObject.getString("Destination");

                dt = jsonObject.getString("ExpectedTime");
                result = dt.replaceAll("^/Date\\(", "");
                instance.ItemNo = jsonObject.getString("ItemNo");
                instance.Latitude = jsonObject.getString("Latitude");
                instance.Longitude = jsonObject.getString("Longitude");


                if (instance.Latitude.length() > 0 && instance.Latitude.equals("null") && instance.Latitude != null) {

                    Location sp = new Location("");
                    sp.setLatitude(Double.parseDouble(instance.Latitude));
                    sp.setLongitude(Double.parseDouble(instance.Longitude));

                    //Places places = new Places(position, latlong);
                    MyRouteActivity.places.add(sp);
                }
                instance.OrderNo = Integer.parseInt(jsonObject.getString("OrderNo"));
                instance.Origin = jsonObject.getString("Origin");
                instance.TypeID = Integer.parseInt(jsonObject.getString("TypeID"));
                instance.PODNeeded = Boolean.parseBoolean(jsonObject.getString("PODNeeded"));
                instance.PODDetail = jsonObject.getString("PODDetail");
                instance.PODTypeCode = jsonObject.getString("PODTypeCode");
                instance.PODTypeName = jsonObject.getString("PODTypeName");
                instance.PiecesCount = String.valueOf(jsonObject.getString("PiecesCount"));
                instance.Weight = String.valueOf(jsonObject.getString("Weight"));
                if (jsonObject.getBoolean("Sign"))
                    instance.Sign = 1;
                else
                    instance.Sign = 0;

                instance.Header = "";

                MyRouteActivity.myRouteShipmentList.add(instance);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected SuggestMyRouteShipments(Parcel in) {
        ID = in.readInt();
        OrderNo = in.readInt();
        ItemNo = in.readString();
        TypeID = in.readInt();
        BillingType = in.readString();
        CODAmount = in.readDouble();
        DeliverySheetID = in.readInt();
        Date = (DateTime) in.readSerializable();
        ExpectedTime = (DateTime) in.readSerializable();
        Latitude = in.readString();
        Longitude = in.readString();
        ClientID = in.readInt();
        ClientName = in.readString();
        ClientFName = in.readString();
        ClientAddressPhoneNumber = in.readString();
        ClientAddressFirstAddress = in.readString();
        ClientAddressSecondAddress = in.readString();
        ClientContactName = in.readString();
        ClientContactFName = in.readString();
        ClientContactPhoneNumber = in.readString();
        ClientContactMobileNo = in.readString();
        ConsigneeName = in.readString();
        ConsigneeFName = in.readString();
        ConsigneePhoneNumber = in.readString();
        ConsigneeFirstAddress = in.readString();
        ConsigneeSecondAddress = in.readString();
        ConsigneeNear = in.readString();
        ConsigneeMobile = in.readString();
        Origin = in.readString();
        Destination = in.readString();
        PODNeeded = in.readByte() != 0;
        PODDetail = in.readString();
        PODTypeCode = in.readString();
        PODTypeName = in.readString();
        IsDelivered = in.readByte() != 0;
        NotDelivered = in.readByte() != 0;
        CourierDailyRouteID = in.readInt();
        OptimzeSerialNo = in.readInt();
        HasComplaint = in.readByte() != 0;
        HasDeliveryRequest = in.readByte() != 0;
    }

    public static final Creator<SuggestMyRouteShipments> CREATOR = new Creator<SuggestMyRouteShipments>() {
        @Override
        public SuggestMyRouteShipments createFromParcel(Parcel in) {
            return new SuggestMyRouteShipments(in);
        }

        @Override
        public SuggestMyRouteShipments[] newArray(int size) {
            return new SuggestMyRouteShipments[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeInt(OrderNo);
        parcel.writeString(ItemNo);
        parcel.writeInt(TypeID);
        parcel.writeString(BillingType);
        parcel.writeDouble(CODAmount);
        parcel.writeInt(DeliverySheetID);
        parcel.writeSerializable(Date);
        parcel.writeSerializable(ExpectedTime);
        parcel.writeString(Latitude);
        parcel.writeString(Longitude);
        parcel.writeInt(ClientID);
        parcel.writeString(ClientName);
        parcel.writeString(ClientFName);
        parcel.writeString(ClientAddressPhoneNumber);
        parcel.writeString(ClientAddressFirstAddress);
        parcel.writeString(ClientAddressSecondAddress);
        parcel.writeString(ClientContactName);
        parcel.writeString(ClientContactFName);
        parcel.writeString(ClientContactPhoneNumber);
        parcel.writeString(ClientContactMobileNo);
        parcel.writeString(ConsigneeName);
        parcel.writeString(ConsigneeFName);
        parcel.writeString(ConsigneePhoneNumber);
        parcel.writeString(ConsigneeFirstAddress);
        parcel.writeString(ConsigneeSecondAddress);
        parcel.writeString(ConsigneeNear);
        parcel.writeString(ConsigneeMobile);
        parcel.writeString(Origin);
        parcel.writeString(Destination);
        parcel.writeByte((byte) (PODNeeded ? 1 : 0));
        parcel.writeString(PODDetail);
        parcel.writeString(PODTypeCode);
        parcel.writeString(PODTypeName);
        parcel.writeByte((byte) (IsDelivered ? 1 : 0));
        parcel.writeByte((byte) (NotDelivered ? 1 : 0));
        parcel.writeInt(CourierDailyRouteID);
        parcel.writeInt(OptimzeSerialNo);
        parcel.writeByte((byte) (HasComplaint ? 1 : 0));
        parcel.writeByte((byte) (HasDeliveryRequest ? 1 : 0));
    }

    public enum UpdateType {
        Optimization,
        DeliveryRequestAndComplaint
    }
}