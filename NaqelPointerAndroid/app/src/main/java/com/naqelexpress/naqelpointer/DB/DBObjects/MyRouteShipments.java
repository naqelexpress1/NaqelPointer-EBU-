package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.Activity.MyRoute.MyRouteActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyRouteShipments implements Parcelable {
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


    public MyRouteShipments() {

    }

    public MyRouteShipments(int id) {
        ID = id;
    }

    public MyRouteShipments(String finalJson, String Latitude, String Longitude, Context context, View view) {

        JSONArray jsonObjectDeliverySheet = null;
        JSONArray Complaint = null;

        try {
            JSONObject jsonObjectHeader = new JSONObject(finalJson);
            jsonObjectDeliverySheet = jsonObjectHeader.getJSONArray("DeliverySheet");
            Complaint = jsonObjectHeader.getJSONArray("Complaint");

        } catch (JSONException e) {
            GlobalVar.GV().ShowSnackbar(view, "No Data with this ID", GlobalVar.AlertType.Error);

            e.printStackTrace();
            return;
        }
        DBConnections dbConnections = new DBConnections(context, null);
        MyRouteActivity.places.clear();
        LatLng ll = null;
        //Places place = new Places();
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location location = GlobalVar.getLastKnownLocation(context);
            // ll = new LatLng(location.getLatitude(), location.getLongitude());
            if (location != null)

                MyRouteActivity.places.add(location);


        }

        JSONObject jsonObject;
        try {
            JSONArray jsonArray = jsonObjectDeliverySheet;

            for (int i = 0; i < jsonArray.length(); i++) {
                MyRouteShipments instance = new MyRouteShipments();
                jsonObject = jsonArray.getJSONObject(i);

                //instance.ID = i + 1;
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
//                instance.Date = new DateTime(Long.parseLong(result.substring(0, result.indexOf('+'))));

                instance.DeliverySheetID = Integer.parseInt(jsonObject.getString("DeliverySheetID"));
                instance.Destination = jsonObject.getString("Destination");

                dt = jsonObject.getString("ExpectedTime");
                result = dt.replaceAll("^/Date\\(", "");
//                instance.ExpectedTime = new DateTime(Long.parseLong(result.substring(0, result.indexOf('+'))));

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
                instance.Header = "";


                if (GlobalVar.GV().CourierDailyRouteID == 0) {
                    CourierDailyRoute courierDailyRoute = new CourierDailyRoute();
                    courierDailyRoute.StartLatitude = String.valueOf(Latitude);
                    courierDailyRoute.StartLongitude = String.valueOf(Longitude);

                    if (dbConnections.InsertCourierDailyRoute(courierDailyRoute, context))
                        GlobalVar.GV().CourierDailyRouteID = dbConnections.getMaxID("CourierDailyRoute Where EmployID = " + GlobalVar.GV().EmployID + " and EndTime is NULL ", context);
                    else
                        GlobalVar.GV().ShowSnackbar(view, context.getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                }

                for (int j = 0; j < Complaint.length(); j++) {
                    JSONObject jo = Complaint.getJSONObject(j);
                    if (jo.getInt("WayBillNo") == Integer.parseInt(jsonObject.getString("ItemNo"))) {
                        if (jo.getInt("ComplaintTypeID") == 6)
                            instance.HasComplaint = true;
                        break;
                    }
                }


                if (GlobalVar.GV().CourierDailyRouteID > 0) {
                    instance.CourierDailyRouteID = GlobalVar.GV().CourierDailyRouteID;
                    {
                        //JSONArray jsonArraycompl = new JSONArray(Complaint);


                        dbConnections.InsertMyRouteShipments(instance, context);
                    }

//                    if (!instance.ConsigneeMobile.equals("null") && instance.ConsigneeMobile != null && !instance.ConsigneeMobile.equals("0")
//                            && instance.ConsigneeMobile.length() > 0) {
//                        Cursor resultDetail = dbConnections.Fill("select * from MobileNo where MobileNo = '" + instance.ConsigneeMobile + "'");
//                        if (resultDetail.getCount() > 0)
//                            dbConnections.UpdateConsigneeName(instance.ConsigneeName, instance.ConsigneeMobile);
//
//                        addMobileNumber(instance.ConsigneeName, instance.ConsigneeMobile, context);
//
//
//                    } else {
//                        if (!instance.ConsigneePhoneNumber.equals("null") && instance.ConsigneePhoneNumber != null &&
//                                !instance.ConsigneePhoneNumber.equals("0") && instance.ConsigneePhoneNumber.length() > 0) {
//
//                            Cursor resultDetail = dbConnections.Fill("select * from MobileNo where MobileNo = '" + instance.ConsigneePhoneNumber + "'");
//                            if (resultDetail.getCount() > 0)
//                                dbConnections.UpdateConsigneeName(instance.ConsigneeName, instance.ConsigneePhoneNumber);
//                            addMobileNumber(instance.ConsigneeName, instance.ConsigneePhoneNumber, context);
//                        }
//                    }


                }
            }


            dbConnections.InsertOFD(jsonArray.length(), GlobalVar.getDate(), context);
            dbConnections.InsertComplaint(Complaint.length(), GlobalVar.getDate(), context);
            dbConnections.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GlobalVar.GV().LoadMyRouteShipments("OrderNo", true, context, view);
    }


    protected MyRouteShipments(Parcel in) {
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

    public static final Creator<MyRouteShipments> CREATOR = new Creator<MyRouteShipments>() {
        @Override
        public MyRouteShipments createFromParcel(Parcel in) {
            return new MyRouteShipments(in);
        }

        @Override
        public MyRouteShipments[] newArray(int size) {
            return new MyRouteShipments[size];
        }
    };

    private void addMobileNumber(String ConsigneeName, String ConsigneeMobile, Context context) {

        deleteContact(context, ConsigneeMobile);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Consignee " + ConsigneeName)
                .build());

        // Adding insert operation to operations list
        // to insert Mobile Number in the table ContactsContract.Data
        String MNo = ValidateMobileNo(ConsigneeMobile);
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MNo)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        try {
            // Executing all the insert operations as a single database transaction
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            DBConnections dbConnections = new DBConnections(context.getApplicationContext(), null);
            dbConnections.InsertMobileNumbers(ConsigneeName, MNo, context);
            dbConnections.close();
            //  Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteContact(Context ctx, String phone) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    // if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    ctx.getContentResolver().delete(uri, null, null);
                    return true;
                    // }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }

    private String ValidateMobileNo(String mobileno) {

        if (!mobileno.equals("null") && mobileno != null && mobileno.length() > 0) {
            if (mobileno.length() == 10) {
                String validate = mobileno.substring(0, 1);
                if (validate.equals("0"))
                    mobileno = mobileno.replaceFirst("0", "+966");
            } else {
                if (mobileno.length() > 10) {
                    //String validate = mobileno.substring(0, 2);
                    if (mobileno.contains("00966"))
                        mobileno = mobileno.replaceFirst("00966", "+966");
                } else if (mobileno.length() == 9) {
                    mobileno = "+966" + mobileno;
                }
            }

        }

        return mobileno;
    }

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

    public MyRouteShipments(String finalJson, UpdateType updateType, Context context, View view) {
        JSONObject jsonObject;
        DBConnections dbConnections = new DBConnections(context, null);
        if (updateType == UpdateType.Optimization) {
            try {
                JSONArray jsonArray = new JSONArray(finalJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);

                    int SNO = Integer.parseInt(jsonObject.getString("SerialNo"));
                    DateTime dTime = DateTime.parse(jsonObject.getString("ExpectedTime"));
                    String itemNo = jsonObject.getString("WayBillNo");

                    Cursor resultDetail = dbConnections.Fill("select * from MyRouteShipments where ItemNo = " + itemNo + " and CourierDailyRouteID = " + GlobalVar.GV().CourierDailyRouteID, context);

                    if (resultDetail.getCount() > 0) {
                        resultDetail.moveToLast();
                        dbConnections.UpdateMyRouteShipmentsWithOptimizationSerial(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("ID"))), SNO, dTime, context);
                    }
                }

                if (jsonArray.length() > 0)
                    GlobalVar.GV().LoadMyRouteShipments("OptimzeSerialNo", true, context, view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (updateType == UpdateType.DeliveryRequestAndComplaint) {
            try {
                JSONArray jsonArray = new JSONArray(finalJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);

                    boolean hasComplaint = Boolean.parseBoolean(jsonObject.getString("hasComplaint"));
                    boolean hasDeliveryRequest = Boolean.parseBoolean(jsonObject.getString("hasDeliveryRequest"));
                    String itemNo = jsonObject.getString("itemNo");

                    Cursor resultDetail = dbConnections.Fill("select * from MyRouteShipments where ItemNo = " + itemNo + " and CourierDailyRouteID = " + GlobalVar.GV().CourierDailyRouteID, context);

                    if (resultDetail.getCount() > 0) {
                        resultDetail.moveToLast();
                        dbConnections.UpdateMyRouteShipmentsWithComplaintAndDeliveryRequest(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("ID"))), hasComplaint, hasDeliveryRequest, context, view);
                    }
                }

                if (jsonArray.length() > 0)
                    GlobalVar.GV().LoadMyRouteShipments("OptimzeSerialNo", false, context, view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dbConnections.close();
    }
}