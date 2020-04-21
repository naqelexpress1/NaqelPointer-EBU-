package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.content.Context;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

//Created by sofan on 24/03/2018.

public class Booking implements Serializable
{
    public int ID;
    public String RefNo="";
    public int ClientID;
    public String ClientName="";
    public String ClientFName="test";
    public DateTime BookingDate= DateTime.now();
    public int PicesCount;
    public Double Weight;
    public String SpecialInstruction="";
    public DateTime OfficeUpTo= DateTime.now();
    public DateTime PickUpReqDT= DateTime.now();
    public String ContactPerson="";
    public String ContactNumber="";
    public String Address="";
    public String Latitude = "0.0";
    public String Longitude = "0.0";
    public int Status=0;
    public String Orgin="";
    public String Destination="";
    public String LoadType="";
    public String BillType="";
    public int EmployeeId=0;


    public Booking(){}

    public Booking(int id, String refno,int clientID, String clientName, String clientFName,
                   DateTime bookingDate,int picesCount,Double weight,String specialInstruction,
                   DateTime officeUpTo,DateTime pickUpReqDT,String contactPerson,String  contactNumber,
                   String address, String latitude, String  longitude,int status,
                   String orgin,String destination,String loadType,String billType,int employeeId
                   )
    {
        ID = id;
        RefNo = refno;
        ClientID = clientID;
        ClientName = clientName;
        ClientFName = clientFName;
        BookingDate = bookingDate;
        PicesCount = picesCount;
        Weight = weight;
        SpecialInstruction = specialInstruction;
        OfficeUpTo = officeUpTo;
        PickUpReqDT = pickUpReqDT;
        ContactPerson = contactPerson;
        ContactNumber = contactNumber;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
        Status = status;
        Orgin = orgin;
        Destination = destination;
        LoadType = loadType;
        BillType=billType;
        EmployeeId=employeeId;
    }



    public Booking (String finalJson, View view,Context context)
    {

        try
        {
            DBConnections dbConnections = new DBConnections(context,view);
          //  JSONObject dataObject = new JSONObject(finalJson);
            JSONArray jsonArray = new JSONArray(finalJson);
            GlobalVar.GV().myBookingList = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++)
            {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Booking instance = new Booking();
                try
                {
                    //instance.ID = Integer.parseInt(jsonObject.getString("id"));
                    //hot code
                    //instance.ID = 923122 + i;
                       // Delete Old ID If found
                  //        dbConnections.DeleteBooking( instance.ID);

                    instance.RefNo = jsonObject.getString("RefNo");
                    instance.ClientID = Integer.parseInt(jsonObject.getString("ClientID"));
                    instance.ClientName = jsonObject.getString("ClientName");
                    //instance.BookingDate = DateTime.parse(jsonObject.getString("BookingDate"));
                    //instance.PicesCount = Integer.parseInt(jsonObject.getString("PicesCount"));
                    //instance.Weight = Double.parseDouble(jsonObject.getString("Weight"));
                    //instance.SpecialInstruction = jsonObject.getString("SpecialInstruction");
                    //instance.OfficeUpTo = DateTime.parse(jsonObject.getString("OfficeUpTo"));
                    //instance.PickUpReqDT = DateTime.parse(jsonObject.getString("PickUpReqDT"));
                        //instance.ContactPerson = jsonObject.getString("ContactPerson");
                    //instance.ContactNumber = jsonObject.getString("ContactNumber");
                    instance.Address = jsonObject.getString("FirstAddress");
                    //instance.Latitude = jsonObject.getString("Latitude");
                    //instance.Longitude = jsonObject.getString("Longitude");
                    //instance.Status = Integer.parseInt(jsonObject.getString("Status"));
                    instance.Orgin = jsonObject.getString("Origin");
                    instance.Destination = jsonObject.getString("Destination");
                    //instance.LoadType = jsonObject.getString("LoadType");
                    //instance.BillType = jsonObject.getString("BillType");
                    instance.BillType = jsonObject.getString("BillCode");
                    instance.EmployeeId = Integer.parseInt(jsonObject.getString("AssignedCourierEmployeeID"));

                    //hot code
                    instance.BookingDate = DateTime.parse("2018-08-03T06:39:11.773");
                    instance.PicesCount = Integer.parseInt("2");
                    instance.Weight = Double.parseDouble("2.0");
                    instance.SpecialInstruction = "emergency";
                    //instance.OfficeUpTo = DateTime.parse(jsonObject.getString("OfficeUpTo"));
                    //instance.PickUpReqDT = DateTime.parse(jsonObject.getString("PickUpReqDT"));
                    instance.ContactPerson = "mohamed ismail";
                    instance.ContactNumber = "0593793637";

                    //boolean v = dbConnections.InsertBooking(instance);
//                    System.out.println(v);

                    GlobalVar.GV().myBookingList.add(instance);

                }
                catch (JSONException ignored){
                    System.out.println(ignored);
                }
            }
          // GlobalVar.GV().LoadMyBookingList("BookingDate",true);

        }
        catch (JSONException ignored){}
    }
}